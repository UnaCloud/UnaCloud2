#include "cuda_image.h"
#include <stdlib.h>
#include <stdio.h>
#include <math.h>

#define THREADS_PER_BLOCK 8

__global__ void negateImageKernel(int *image, int width, int height, int bands){

	int x = threadIdx.x + blockIdx.x * blockDim.x;
	int y = threadIdx.y + blockIdx.y * blockDim.y;
	int z = threadIdx.z + blockIdx.z * blockDim.z;

	int idx = x + y * gridDim.x * blockDim.x + z * gridDim.x * blockDim.x * gridDim.y * blockDim.y;

	if( idx < (width * height * bands))
		image[idx] = 255 - image[idx];
}

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

void printImage(int *image, int width, int height, int bands){

	int i, j, k;

	for(k = 0; k < bands; k++){
	
		printf("Band %d: \n", k);

		for(j = 0; j < height; j++){
		
			for(i = 0; i < width; i++){
	
				printf("%d\t", image[i + (width * j) + (width * height * k)]);
			}
			puts("");
		}
	
		printf("\n\n");
	}

}

extern "C" {
	void cudaNegateImage(int *image, int width, int height, int bands){

		int *d_image;
		int arrsize = width * height * bands;
		int xblocks = ceil((float)width/THREADS_PER_BLOCK);
		int yblocks = ceil((float)height/THREADS_PER_BLOCK);
		int zblocks = ceil((float)bands/THREADS_PER_BLOCK);

		cudaMalloc((void **)&d_image, sizeof(int) * arrsize);

		checkCUDAError("Error in CUDA Malloc");
	
		cudaMemcpy(d_image, image, sizeof(int) * arrsize, cudaMemcpyHostToDevice);
	
		checkCUDAError("Error writing on Device");

		dim3 nblocks(xblocks, yblocks, zblocks);
		dim3 nthreads(THREADS_PER_BLOCK,THREADS_PER_BLOCK,THREADS_PER_BLOCK);

		printf("Number of blocks in x: %d\n", xblocks);
		printf("Number of blocks in y: %d\n", yblocks);
		printf("Number of blocks in z: %d\n", zblocks);
		printf("Number of threads per block: %d\n", THREADS_PER_BLOCK);
		printf("Total threads: %d\n", xblocks*yblocks*zblocks*THREADS_PER_BLOCK*THREADS_PER_BLOCK*THREADS_PER_BLOCK);
		printf("Total pixels: %d\n", arrsize);

		negateImageKernel<<<nblocks, nthreads>>>(d_image, width, height, bands);
		cudaThreadSynchronize();

		checkCUDAError("Error on kernel");

	//	printImage(image, width, height, bands);
		cudaMemcpy(image, d_image, sizeof(int) * arrsize, cudaMemcpyDeviceToHost);

		checkCUDAError("Error writing on Host");

	//	printImage(image, width, height, bands);
		cudaFree(d_image);
	}
}
