#include <stdio.h>
#include <stdlib.h>
#include "image.h"

#define HEIGHT 10
#define WIDTH 10
#define BANDS 3

int main(){

	puts("NEGATING ARRAY");

	int *image;	
	initImage(&image, WIDTH, HEIGHT, BANDS);

	//int i, j, k, counter = 0;

	//for(k = 0; k < BANDS; k++){
	//	for(j = 0; j < HEIGHT; j++){
		
	//		for(i = 0; i < WIDTH; i++){
		
	//			image[i + (WIDTH * j) + (WIDTH * HEIGHT * k)] = counter++;
	//		}
	//	}
	//}

	printImage(image, WIDTH, HEIGHT, BANDS);
	negateImage(image, WIDTH, HEIGHT, BANDS);
	printImage(image, WIDTH, HEIGHT, BANDS);
	destroyImage(&image);
	puts("DONE");
	return 0;
}
