#include "cuda_datacube.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

#define MEDIAN(array, size, type) (size % 2 == 0 ? (type)((array[size/2]+array[(size/2)-1])/2) : array[(int)floor(size/2.0)]);
#define CUDA_MALLOC(array, arraySize, type) cudaMalloc((void **)&array, sizeof(type)*arraySize);
#define CUDA_MALLOC_HOST(array, arraySize, type) cudaMallocHost((void **)&array, sizeof(type)*arraySize);
#define NBANDS 6

/* Utility function to check for and report CUDA errors */
void checkCUDAError(const char *msg)
{
    cudaError_t err = cudaGetLastError();
    if( cudaSuccess != err) 
    {
        fprintf(stderr, "Cuda error: %s: %s.\n", msg, cudaGetErrorString( err) );
        exit(EXIT_FAILURE);
    }                         
}

__global__ void applyMasks16Kernel(short *images, bool *cmasks, int width, int height, int bands, short nullValue){

	int x = threadIdx.x + (blockDim.x * blockIdx.x);
	int y = threadIdx.y + (blockDim.y * blockIdx.y);
	int z = threadIdx.z + (blockDim.z * blockIdx.z);

	int idx = x + (y * width) + (z * height * width);

	if(x < width && y < height && z < bands){
		if(!cmasks[idx])
			images[idx] = nullValue;
	}

}
__global__ void normImages16Kernel(short *images, double *means, double meanMeans, double *stdDevs, double meanStdDevs, int width, int height, int  bands, short nullValue){

	int x = threadIdx.x + (blockDim.x * blockIdx.x);
	int y = threadIdx.y + (blockDim.y * blockIdx.y);
	int z = threadIdx.z + (blockDim.z * blockIdx.z);

	int idx = x + (y * width) + (z * height * width);

	if(x < width && y < height && z < bands){
		if(images[idx] != nullValue)
			images[idx] = (((double)images[idx]-means[z])/stdDevs[z])*meanStdDevs+meanMeans;
	}
}

__global__ void dcMediansBandInt16Kernel(short *images, short *mediansBand, int width, int height, int bands, int minValidCount, short nullValue){

	int x = threadIdx.x + (blockDim.x * blockIdx.x);
	int y = threadIdx.y + (blockDim.y * blockIdx.y);

	int idx = x + (y * width);
	int imgSize = width*height;

	int i, j, bandArrIdx;

	if(x < width && y < height){

		int validCounter = 0;
		int pixel[NBANDS];
		for(i = 0; i < bands; i++){
			bandArrIdx = idx + (imgSize*i);
			if(images[bandArrIdx] != nullValue){
				pixel[validCounter] = images[bandArrIdx];
				validCounter++;
			}
		}

		if(minValidCount <= validCounter){
			for(i = 0; i < validCounter-1; i++){
				for(j = i+1; j < validCounter; j++){
					if(pixel[i] > pixel[j]){
						short tmp = pixel[i];
						pixel[i] = pixel[j];
						pixel[j] = tmp;
					}
				}
			}
			mediansBand[idx] = MEDIAN(pixel, validCounter, short);
		}else{
			mediansBand[idx] = nullValue;
		}
	}
}

extern "C" {
	void dcMediansBandInt16(short *src_images, short *medianc, bool *cmask, int width, int height, int bands, bool normalized, int minValidCount, short nullValue){
	
		int i = 0, j = 0;
		int arrSize = width * height * bands;
		int imgSize = width * height;
		int xblocks, yblocks, zblocks;

		// Create images array on the CUDA device
		short *d_images;
		CUDA_MALLOC(d_images, arrSize, short);
		checkCUDAError("Error in CUDA Malloc for images");
		cudaMemcpy(d_images, src_images, sizeof(short)*arrSize, cudaMemcpyHostToDevice);
		checkCUDAError("Error in CUDA Memcpy for images");

		// Creates Cloud mask on the CUDA device
		bool *d_cmask;
		CUDA_MALLOC(d_cmask, arrSize, bool);
		checkCUDAError("Error in CUDA Malloc for cloud masks");	
		cudaMemcpy(d_cmask, cmask, sizeof(bool)*arrSize, cudaMemcpyHostToDevice);
		checkCUDAError("Error in CUDA Malloc for cloud masks");

		// Creates medians compound on the CUDA device
		short *d_medianc;
		CUDA_MALLOC(d_medianc, imgSize, short);
		checkCUDAError("Error in CUDA Malloc for Medians Band");

		// Apply Masks
		xblocks = ceil(width/8.0);
		yblocks = ceil(height/8.0);
		zblocks = ceil(bands/8.0);

		dim3 maskBlocks(xblocks, yblocks, zblocks);
		dim3 maskThreads(8,8,8);

		applyMasks16Kernel<<<maskBlocks, maskThreads>>>(d_images, d_cmask, width, height, bands, nullValue);
		cudaDeviceSynchronize();
		checkCUDAError("Error in Apply Masks");

		cudaFree(d_cmask);

		if(normalized){

			short *images;
			CUDA_MALLOC_HOST(images, arrSize, short);
			cudaMemcpy(images, d_images, sizeof(short)*arrSize, cudaMemcpyDeviceToHost);

			double means[bands];
			double stddev[bands];
			double meanMeans = 0.0;
			double meanStddev = 0.0;
			int validCounter, idx;

			// Mean for each band
			for(i = 0; i < bands; i++){
				validCounter = 0;
				means[i] = 0.0;
				for(j = 0; j < imgSize; j++){
					idx = j+(imgSize*i);
					if(images[idx] != nullValue){
						means[i] += images[idx];
						validCounter++;
					}
				}
				if(validCounter > 0)
					means[i] /= validCounter; 
				//printf("Means %d: %f\n", i, means[i]);
			}
	
			// Standard deviation for each band
			for(i = 0; i < bands; i++){
				validCounter = 0;
				stddev[i] = 0.0;
				for(j = 0; j < imgSize; j++){
					idx = j+(imgSize*i);
					if(images[idx] != nullValue){
						stddev[i] += pow((images[idx] - means[i]), 2);
						validCounter++;
					}
				}
				if(validCounter > 0)
					stddev[i] = sqrt(stddev[i] / validCounter);
				//printf("StdDev %d: %f\n", i, stddev[i]);
			}
	
			// Mean of means and Mean of standard deviations
			for(i = 0; i < bands; i++){
				meanMeans += means[i];
				meanStddev += stddev[i];
			}
			meanMeans /= bands;
			meanStddev /= bands;

			cudaMemcpy(d_images, images, sizeof(short)*arrSize, cudaMemcpyHostToDevice);

			double *d_means;
			CUDA_MALLOC(d_means, bands, double);
			cudaMemcpy(d_means, means, sizeof(double)*bands, cudaMemcpyHostToDevice);

			double *d_stddev;
			CUDA_MALLOC(d_stddev, bands, double);
			cudaMemcpy(d_stddev, stddev, sizeof(double)*bands, cudaMemcpyHostToDevice);

			dim3 normBlocks(ceil(width/8.0),ceil(height/8.0),ceil(bands/8.0));
			dim3 normThreads(8,8,8);

			normImages16Kernel<<<normBlocks, normThreads>>>(d_images, d_means, meanMeans, d_stddev, meanStddev, width, height, bands, nullValue);
			cudaFreeHost(images);
			cudaDeviceSynchronize();
			checkCUDAError("Error on Normalize Images Kernel");

			cudaFree(d_means);
			cudaFree(d_stddev);
		}
		// Compute Medians Band
		dim3 mediansBlocks(ceil(width/16.0), ceil(height/16.0), 1);
		dim3 mediansThreads(16,16,1);
		dcMediansBandInt16Kernel<<<mediansBlocks, mediansThreads>>>(d_images, d_medianc, width, height, bands, minValidCount, nullValue);
		cudaDeviceSynchronize();
		checkCUDAError("Error on Medians Band Kernel");

		cudaMemcpy(medianc, d_medianc, sizeof(short)*imgSize, cudaMemcpyDeviceToHost);

		cudaFree(d_images);
		cudaFree(d_medianc);
	}
}

__global__ void applyMasks32Kernel(int *images, bool *cmasks, int width, int height, int bands, int nullValue){

	int x = threadIdx.x + (blockDim.x * blockIdx.x);
	int y = threadIdx.y + (blockDim.y * blockIdx.y);
	int z = threadIdx.z + (blockDim.z * blockIdx.z);

	int idx = x + (y * width) + (z * height * width);

	if(x < width && y < height && z < bands){
		if(!cmasks[idx])
			images[idx] = nullValue;
	}

}
__global__ void normImages32Kernel(int *images, double *means, double meanMeans, double *stdDevs, double meanStdDevs, int width, int height, int  bands, int nullValue){

	int x = threadIdx.x + (blockDim.x * blockIdx.x);
	int y = threadIdx.y + (blockDim.y * blockIdx.y);
	int z = threadIdx.z + (blockDim.z * blockIdx.z);

	int idx = x + (y * width) + (z * height * width);

	if(x < width && y < height && z < bands){
		if(images[idx] != nullValue)
			images[idx] = (((double)images[idx]-means[z])/stdDevs[z])*meanStdDevs+meanMeans;
	}
}

__global__ void dcMediansBandInt32Kernel(int *images, int *mediansBand, int width, int height, int bands, int minValidCount, int nullValue){

	int x = threadIdx.x + (blockDim.x * blockIdx.x);
	int y = threadIdx.y + (blockDim.y * blockIdx.y);

	int idx = x + (y * width);
	int imgSize = width*height;

	int i, j, bandArrIdx;

	if(x < width && y < height){

		int validCounter = 0;
		int pixel[NBANDS];
		for(i = 0; i < bands; i++){
			bandArrIdx = idx + (imgSize*i);
			if(images[bandArrIdx] != nullValue){
				pixel[validCounter] = images[bandArrIdx];
				validCounter++;
			}
		}

		if(minValidCount <= validCounter){
			for(i = 0; i < validCounter-1; i++){
				for(j = i+1; j < validCounter; j++){
					if(pixel[i] > pixel[j]){
						int tmp = pixel[i];
						pixel[i] = pixel[j];
						pixel[j] = tmp;
					}
				}
			}
			mediansBand[idx] = MEDIAN(pixel, validCounter, int);
		}else{
			mediansBand[idx] = nullValue;
		}
	}
}

extern "C" {
	void dcMediansBandInt32(int *src_images, int *medianc, bool *cmask, int width, int height, int bands, bool normalized, int minValidCount, int nullValue){
	
		int i = 0, j = 0;
		int arrSize = width * height * bands;
		int imgSize = width * height;
		int xblocks, yblocks, zblocks;

		// Create images array on the CUDA device
		int *d_images;
		CUDA_MALLOC(d_images, arrSize, int);
		checkCUDAError("Error in CUDA Malloc for images");
		cudaMemcpy(d_images, src_images, sizeof(int)*arrSize, cudaMemcpyHostToDevice);
		checkCUDAError("Error in CUDA Memcpy for images");

		// Creates Cloud mask on the CUDA device
		bool *d_cmask;
		CUDA_MALLOC(d_cmask, arrSize, bool);
		checkCUDAError("Error in CUDA Malloc for cloud masks");	
		cudaMemcpy(d_cmask, cmask, sizeof(bool)*arrSize, cudaMemcpyHostToDevice);
		checkCUDAError("Error in CUDA Malloc for cloud masks");

		// Creates medians compound on the CUDA device
		int *d_medianc;
		CUDA_MALLOC(d_medianc, imgSize, int);
		checkCUDAError("Error in CUDA Malloc for Medians Band");

		// Apply Masks
		xblocks = ceil(width/8.0);
		yblocks = ceil(height/8.0);
		zblocks = ceil(bands/8.0);

		dim3 maskBlocks(xblocks, yblocks, zblocks);
		dim3 maskThreads(8,8,8);

		applyMasks32Kernel<<<maskBlocks, maskThreads>>>(d_images, d_cmask, width, height, bands, nullValue);
		cudaDeviceSynchronize();
		checkCUDAError("Error in Apply Masks");

		cudaFree(d_cmask);

		if(normalized){

			int *images;
			CUDA_MALLOC_HOST(images, arrSize, int);
			cudaMemcpy(images, d_images, sizeof(int)*arrSize, cudaMemcpyDeviceToHost);

			double means[bands];
			double stddev[bands];
			double meanMeans = 0.0;
			double meanStddev = 0.0;
			int validCounter, idx;

			// Mean for each band
			for(i = 0; i < bands; i++){
				validCounter = 0;
				means[i] = 0.0;
				for(j = 0; j < imgSize; j++){
					idx = j+(imgSize*i);
					if(images[idx] != nullValue){
						means[i] += images[idx];
						validCounter++;
					}
				}
				if(validCounter > 0)
					means[i] /= validCounter; 
				//printf("Means %d: %f\n", i, means[i]);
			}
	
			// Standard deviation for each band
			for(i = 0; i < bands; i++){
				validCounter = 0;
				stddev[i] = 0.0;
				for(j = 0; j < imgSize; j++){
					idx = j+(imgSize*i);
					if(images[idx] != nullValue){
						stddev[i] += pow((images[idx] - means[i]), 2);
						validCounter++;
					}
				}
				if(validCounter > 0)
					stddev[i] = sqrt(stddev[i] / validCounter);
				//printf("StdDev %d: %f\n", i, stddev[i]);
			}
	
			// Mean of means and Mean of standard deviations
			for(i = 0; i < bands; i++){
				meanMeans += means[i];
				meanStddev += stddev[i];
			}
			meanMeans /= bands;
			meanStddev /= bands;

			cudaMemcpy(d_images, images, sizeof(int)*arrSize, cudaMemcpyHostToDevice);

			double *d_means;
			CUDA_MALLOC(d_means, bands, double);
			cudaMemcpy(d_means, means, sizeof(double)*bands, cudaMemcpyHostToDevice);

			double *d_stddev;
			CUDA_MALLOC(d_stddev, bands, double);
			cudaMemcpy(d_stddev, stddev, sizeof(double)*bands, cudaMemcpyHostToDevice);

			dim3 normBlocks(ceil(width/8.0),ceil(height/8.0),ceil(bands/8.0));
			dim3 normThreads(8,8,8);

			normImages32Kernel<<<normBlocks, normThreads>>>(d_images, d_means, meanMeans, d_stddev, meanStddev, width, height, bands, nullValue);
			cudaFreeHost(images);
			cudaDeviceSynchronize();
			checkCUDAError("Error on Normalize Images Kernel");

			cudaFree(d_means);
			cudaFree(d_stddev);
		}
		// Compute Medians Band
		dim3 mediansBlocks(ceil(width/16.0), ceil(height/16.0), 1);
		dim3 mediansThreads(16,16,1);
		dcMediansBandInt32Kernel<<<mediansBlocks, mediansThreads>>>(d_images, d_medianc, width, height, bands, minValidCount, nullValue);
		cudaDeviceSynchronize();
		checkCUDAError("Error on Medians Band Kernel");

		cudaMemcpy(medianc, d_medianc, sizeof(int)*imgSize, cudaMemcpyDeviceToHost);

		cudaFree(d_images);
		cudaFree(d_medianc);
	}
}

__global__ void applyMasks64Kernel(double *images, bool *cmasks, int width, int height, int bands, double nullValue){

	int x = threadIdx.x + (blockDim.x * blockIdx.x);
	int y = threadIdx.y + (blockDim.y * blockIdx.y);
	int z = threadIdx.z + (blockDim.z * blockIdx.z);

	int idx = x + (y * width) + (z * height * width);

	if(x < width && y < height && z < bands){
		if(!cmasks[idx])
			images[idx] = nullValue;
	}

}
__global__ void normImages64Kernel(double *images, double *means, double meanMeans, double *stdDevs, double meanStdDevs, int width, int height, int  bands, double nullValue){

	int x = threadIdx.x + (blockDim.x * blockIdx.x);
	int y = threadIdx.y + (blockDim.y * blockIdx.y);
	int z = threadIdx.z + (blockDim.z * blockIdx.z);

	int idx = x + (y * width) + (z * height * width);

	if(x < width && y < height && z < bands){
		if(images[idx] != nullValue)
			images[idx] = (((double)images[idx]-means[z])/stdDevs[z])*meanStdDevs+meanMeans;
	}
}

__global__ void dcMediansBandFloat64Kernel(double *images, double *mediansBand, int width, int height, int bands, int minValidCount, double nullValue){

	int x = threadIdx.x + (blockDim.x * blockIdx.x);
	int y = threadIdx.y + (blockDim.y * blockIdx.y);

	int idx = x + (y * width);
	int imgSize = width*height;

	int i, j, bandArrIdx;

	if(x < width && y < height){

		int validCounter = 0;
		double pixel[NBANDS];
		for(i = 0; i < bands; i++){
			bandArrIdx = idx + (imgSize*i);
			if(images[bandArrIdx] != nullValue){
				pixel[validCounter] = images[bandArrIdx];
				validCounter++;
			}
		}

		if(minValidCount <= validCounter){
			for(i = 0; i < validCounter-1; i++){
				for(j = i+1; j < validCounter; j++){
					if(pixel[i] > pixel[j]){
						double tmp = pixel[i];
						pixel[i] = pixel[j];
						pixel[j] = tmp;
					}
				}
			}
			mediansBand[idx] = MEDIAN(pixel, validCounter, double);
		}else{
			mediansBand[idx] = nullValue;
		}
	}
}

extern "C" {
	void dcMediansBandFloat64(double *src_images, double *medianc, bool *cmask, int width, int height, int bands, bool normalized, int minValidCount, double nullValue){
	
		int i = 0, j = 0;
		int arrSize = width * height * bands;
		int imgSize = width * height;
		int xblocks, yblocks, zblocks;

		// Create images array on the CUDA device
		double *d_images;
		CUDA_MALLOC(d_images, arrSize, double);
		checkCUDAError("Error in CUDA Malloc for images");
		cudaMemcpy(d_images, src_images, sizeof(double)*arrSize, cudaMemcpyHostToDevice);
		checkCUDAError("Error in CUDA Memcpy for images");

		// Creates Cloud mask on the CUDA device
		bool *d_cmask;
		CUDA_MALLOC(d_cmask, arrSize, bool);
		checkCUDAError("Error in CUDA Malloc for cloud masks");	
		cudaMemcpy(d_cmask, cmask, sizeof(bool)*arrSize, cudaMemcpyHostToDevice);
		checkCUDAError("Error in CUDA Malloc for cloud masks");

		// Creates medians compound on the CUDA device
		double *d_medianc;
		CUDA_MALLOC(d_medianc, imgSize, double);
		checkCUDAError("Error in CUDA Malloc for Medians Band");

		// Apply Masks
		xblocks = ceil(width/8.0);
		yblocks = ceil(height/8.0);
		zblocks = ceil(bands/8.0);

		dim3 maskBlocks(xblocks, yblocks, zblocks);
		dim3 maskThreads(8,8,8);

		applyMasks64Kernel<<<maskBlocks, maskThreads>>>(d_images, d_cmask, width, height, bands, nullValue);
		cudaDeviceSynchronize();
		checkCUDAError("Error in Apply Masks");

		cudaFree(d_cmask);

		if(normalized){

			double *images;
			CUDA_MALLOC_HOST(images, arrSize, double);
			cudaMemcpy(images, d_images, sizeof(double)*arrSize, cudaMemcpyDeviceToHost);

			double means[bands];
			double stddev[bands];
			double meanMeans = 0.0;
			double meanStddev = 0.0;
			int validCounter, idx;

			// Mean for each band
			for(i = 0; i < bands; i++){
				validCounter = 0;
				means[i] = 0.0;
				for(j = 0; j < imgSize; j++){
					idx = j+(imgSize*i);
					if(images[idx] != nullValue){
						means[i] += images[idx];
						validCounter++;
					}
				}
				if(validCounter > 0)
					means[i] /= validCounter; 
				//printf("Means %d: %f\n", i, means[i]);
			}
	
			// Standard deviation for each band
			for(i = 0; i < bands; i++){
				validCounter = 0;
				stddev[i] = 0.0;
				for(j = 0; j < imgSize; j++){
					idx = j+(imgSize*i);
					if(images[idx] != nullValue){
						stddev[i] += pow((images[idx] - means[i]), 2);
						validCounter++;
					}
				}
				if(validCounter > 0)
					stddev[i] = sqrt(stddev[i] / validCounter);
				//printf("StdDev %d: %f\n", i, stddev[i]);
			}
	
			// Mean of means and Mean of standard deviations
			for(i = 0; i < bands; i++){
				meanMeans += means[i];
				meanStddev += stddev[i];
			}
			meanMeans /= bands;
			meanStddev /= bands;

			cudaMemcpy(d_images, images, sizeof(double)*arrSize, cudaMemcpyHostToDevice);

			double *d_means;
			CUDA_MALLOC(d_means, bands, double);
			cudaMemcpy(d_means, means, sizeof(double)*bands, cudaMemcpyHostToDevice);

			double *d_stddev;
			CUDA_MALLOC(d_stddev, bands, double);
			cudaMemcpy(d_stddev, stddev, sizeof(double)*bands, cudaMemcpyHostToDevice);

			dim3 normBlocks(ceil(width/8.0),ceil(height/8.0),ceil(bands/8.0));
			dim3 normThreads(8,8,8);

			normImages64Kernel<<<normBlocks, normThreads>>>(d_images, d_means, meanMeans, d_stddev, meanStddev, width, height, bands, nullValue);
			cudaFreeHost(images);
			cudaDeviceSynchronize();
			checkCUDAError("Error on Normalize Images Kernel");

			cudaFree(d_means);
			cudaFree(d_stddev);
		}
		// Compute Medians Band
		dim3 mediansBlocks(ceil(width/16.0), ceil(height/16.0), 1);
		dim3 mediansThreads(16,16,1);
		dcMediansBandFloat64Kernel<<<mediansBlocks, mediansThreads>>>(d_images, d_medianc, width, height, bands, minValidCount, nullValue);
		cudaDeviceSynchronize();
		checkCUDAError("Error on Medians Band Kernel");

		cudaMemcpy(medianc, d_medianc, sizeof(double)*imgSize, cudaMemcpyDeviceToHost);

		cudaFree(d_images);
		cudaFree(d_medianc);
	}
}
