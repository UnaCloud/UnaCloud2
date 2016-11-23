#include "datacube.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

#define MEDIAN(array, size, type) (size % 2 == 0 ? (type)((array[size/2]+array[(size/2)-1])/2) : array[(int)floor(size/2.0)]);
#define MEAN(array, size, type)	mean##type(array, size);

void dcInitMask(_Bool **array, int width, int height){

	int i;
	int arrSize = width * height;
	_Bool *newArray = (_Bool *)malloc(sizeof(_Bool) * arrSize);

	for(i = 0; i < arrSize; i++){
		
		newArray[i] = 0;
	}

	(*array) = newArray;
}

void dcPrintMask(_Bool *array, int width, int height){

	int i, j;
	int arrSize = width * height;
	
	printf("Mask:\n");
	
	for(j = 0;   j < height; j++){
			
		for(i = 0; i < width; i++){
			printf("%d ", array[i + (width * j)]);
		}
		puts("");
	}
	puts("");
}

void dcInitArrayUInt8(unsigned char **array, int width, int height, int bands){

	int i;
	int arrSize = width * height * bands;
	unsigned char *newArray = (unsigned char *)malloc(sizeof(unsigned char) * arrSize);

	for(i = 0; i < arrSize; i++){
		
		newArray[i] = 0;
	}

	(*array) = newArray;
}

void dcPrintArrayUInt8(unsigned char *array, int width, int height, int bands){

	int i, j, k;
	int arrSize = width * height * bands;
	
	for(k = 0; k < bands; k++){

		printf("Band %d:\n", k+1);
	
		for(j = 0;   j < height; j++){
			
			for(i = 0; i < width; i++){
				printf("%d ", array[i]);
			}
			puts("");
		}
		puts("");
	}
}

short meanInt16(short *array, int size){

	int i, sum = 0;
	for(i = 0; i < size; i++){
		sum += array[i];
	}
	sum = sum / size;
	return (short)sum;

}

int cmpFuncInt16(const void *a, const void *b)
{
	return ( *(short*)a - *(short*)b );
}

void dcInitArrayInt16(short**array, int width, int height, int bands){

	int i;
	int arrSize = width * height * bands;
	short *newArray = (short *)malloc(sizeof(short) * arrSize);

	for(i = 0; i < arrSize; i++){
		
		newArray[i] = 0;
	}

	(*array) = newArray;
}

void dcPrintArrayInt16(short *array, int width, int height, int bands){

	int i, j, k;
	int arrSize = width * height * bands;
	
	for(k = 0; k < bands; k++){

		printf("Band %d:\n", k+1);
	
		for(j = 0;   j < height; j++){
			
			for(i = 0; i < width; i++){
				printf("%d ", array[i + (width * j) + (width * height * k)]);
			}
			puts("");
		}
		puts("");
	}
}

void dcMediansBandInt16(short *src_images, short *medianc, _Bool *cmask, int width, int height, int bands, _Bool normalized, int minValidCount, short nullValue){

	int i = 0, j = 0, idx = 0, validCounter = 0;
	double means[bands];
	double stddev[bands];
	double meanMeans = 0.0;
	double meanStddev = 0.0;
	int arrSize = width * height * bands;
	int imgSize = width * height;

	short *images = (short *)malloc(sizeof(short)*arrSize);

	memcpy(images, src_images, sizeof(short)*arrSize);

	// Apply cloud mask
	for(i = 0; i < arrSize; i++){
		
		if(cmask[i] == 0)
			images[i] = nullValue;
	}

	// Normalize bands
	if(normalized){

		// Mean for each band
		for(i = 0; i < bands; i++){
			validCounter = 0;
			means[i] = 0;
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
			stddev[i] = 0;
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

		// Normalize values
		for(i = 0; i < bands; i++){
			for(j = 0; j < imgSize; j++){
				idx = j+(imgSize*i);
				if(images[idx] != nullValue)
					images[idx] = (short)((((double)images[idx]-means[i])/stddev[i]) * meanStddev + meanMeans);
			}
		}
	}

	// Compute medians per pixel
	for(i = 0; i < imgSize; i++){
		
		short pixel[bands];
		validCounter = 0;
		for(j = 0; j < bands; j++){
			idx = i + (imgSize*j);
			if(images[idx] != nullValue){
				pixel[validCounter] = images[idx];
				validCounter++;
			}
		}
		if(minValidCount <= validCounter){
			qsort(pixel, validCounter, sizeof(short), cmpFuncInt16);
			medianc[i] = MEDIAN(pixel, validCounter, short);
		}else{
			medianc[i] = nullValue;
		}
	}
	free(images);
}

int cmpFuncInt32(const void *a, const void *b)
{
	return ( *(int*)a - *(int*)b );
}

void dcMediansBandInt32(int *src_images, int *medianc, _Bool *cmask, int width, int height, int bands, _Bool normalized, int minValidCount, int nullValue){

	int i = 0, j = 0, idx = 0, validCounter = 0;
	double means[bands];
	double stddev[bands];
	double meanMeans = 0.0;
	double meanStddev = 0.0;
	int arrSize = width * height * bands;
	int imgSize = width * height;

	int *images = (int *)malloc(sizeof(int)*arrSize);

	memcpy(images, src_images, sizeof(int)*arrSize);

	// Apply cloud mask
	for(i = 0; i < arrSize; i++){
		
		if(cmask[i] == 0)
			images[i] = nullValue;
	}

	// Normalize bands
	if(normalized){

		// Mean for each band
		for(i = 0; i < bands; i++){
			validCounter = 0;
			means[i] = 0;
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
			stddev[i] = 0;
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

		// Normalize values
		for(i = 0; i < bands; i++){
			for(j = 0; j < imgSize; j++){
				idx = j+(imgSize*i);
				if(images[idx] != nullValue)
					images[idx] = (int)((((double)images[idx]-means[i])/stddev[i]) * meanStddev + meanMeans);
			}
		}
	}

	// Compute medians per pixel
	for(i = 0; i < imgSize; i++){
		
		int pixel[bands];
		validCounter = 0;
		for(j = 0; j < bands; j++){
			idx = i + (imgSize*j);
			if(images[idx] != nullValue){
				pixel[validCounter] = images[idx];
				validCounter++;
			}
		}
		if(minValidCount <= validCounter){
			qsort(pixel, validCounter, sizeof(int), cmpFuncInt16);
			medianc[i] = MEDIAN(pixel, validCounter, int);
		}else{
			medianc[i] = nullValue;
		}
	}
	free(images);
}

int cmpFuncFloat64(const void *a, const void *b)
{
	return ( *(double*)a - *(double*)b );
}

void dcMediansBandFloat64(double *src_images, double *medianc, _Bool *cmask, int width, int height, int bands, _Bool normalized, int minValidCount, double nullValue){

	int i = 0, j = 0, idx = 0, validCounter = 0;
	double means[bands];
	double stddev[bands];
	double meanMeans = 0.0;
	double meanStddev = 0.0;
	int arrSize = width * height * bands;
	int imgSize = width * height;

	double *images = (double *)malloc(sizeof(double)*arrSize);

	memcpy(images, src_images, sizeof(double)*arrSize);

	// Apply cloud mask
	for(i = 0; i < arrSize; i++){
		
		if(cmask[i] == 0)
			images[i] = nullValue;
	}

	// Normalize bands
	if(normalized){

		// Mean for each band
		for(i = 0; i < bands; i++){
			validCounter = 0;
			means[i] = 0;
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
			stddev[i] = 0;
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

		// Normalize values
		for(i = 0; i < bands; i++){
			for(j = 0; j < imgSize; j++){
				idx = j+(imgSize*i);
				if(images[idx] != nullValue)
					images[idx] = (double)((((double)images[idx]-means[i])/stddev[i]) * meanStddev + meanMeans);
			}
		}
	}

	// Compute medians per pixel
	for(i = 0; i < imgSize; i++){
		
		double pixel[bands];
		validCounter = 0;
		for(j = 0; j < bands; j++){
			idx = i + (imgSize*j);
			if(images[idx] != nullValue){
				pixel[validCounter] = images[idx];
				validCounter++;
			}
		}
		if(minValidCount <= validCounter){
			qsort(pixel, validCounter, sizeof(double), cmpFuncFloat64);
			medianc[i] = MEDIAN(pixel, validCounter, double);
		}else{
			medianc[i] = nullValue;
		}
	}
	free(images);
}


void dcInitArrayInt32(int **array, int width, int height, int bands){

	int i;
	int arrSize = width * height * bands;
	int *newArray = (int *)malloc(sizeof(int) * arrSize);

	for(i = 0; i < arrSize; i++){
		
		newArray[i] = 0;
	}

	(*array) = newArray;
}

void dcPrintArrayInt32(int *array, int width, int height, int bands){

	int i, j, k;
	int arrSize = width * height * bands;
	
	for(k = 0; k < bands; k++){

		printf("Band %d:\n", k+1);
	
		for(j = 0;   j < height; j++){
			
			for(i = 0; i < width; i++){
				printf("%d ", array[i]);
			}
			puts("");
		}
		puts("");
	}
}

void dcInitArrayFloat(float **array, int width, int height, int bands){

	int i;
	int arrSize = width * height * bands;
	float *newArray = (float *)malloc(sizeof(float) * arrSize);

	for(i = 0; i < arrSize; i++){
		
		newArray[i] = 0;
	}

	(*array) = newArray;
}

void dcPrintArrayFloat(float *array, int width, int height, int bands){

	int i, j, k;
	int arrSize = width * height * bands;
	
	for(k = 0; k < bands; k++){

		printf("Band %d:\n", k+1);
	
		for(j = 0;   j < height; j++){
			
			for(i = 0; i < width; i++){
				printf("%f ", array[i]);
			}
			puts("");
		}
		puts("");
	}
}

void dcDestroyArray(void *array){

	free(array);
}
