#include "image.h"
#include <stdlib.h>
#include <stdio.h>

void initImage(int **image, int width, int height, int bands){

	int i;

	int *data;
	int arrsize = width * height * bands;
	data = (int *)malloc(sizeof(int) * arrsize); 

	for(i = 0; i < arrsize; i++){

		data[i] = 0;
	}

	*image = data;
}

void destroyImage(int **image){

	free(*image);
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

void negateImage(int *image, int width, int height, int bands){

	int i;
	int arrsize = width * height * bands;

	for(i = 0; i < arrsize; i++){

		image[i] = 255 - image[i];
	}

}
