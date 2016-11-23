#include "datacube.h"

int main(){

	const WIDTH = 10;
	const HEIGHT = 5;
	const BANDS = 3;
	int i;

	// Cloud Mask
	dcMask cmask;
	cmask.width = WIDTH;
	cmask.height = HEIGHT;
	dcInitMask(&cmask.mask, cmask.width, cmask.height);

	for(i = 0; i < WIDTH*HEIGHT; i++){
		cmask.mask[i] = 1;
	}

	for(i = 0; i < WIDTH; i++){
		cmask.mask[i] = 0;
		cmask.mask[i + (WIDTH * (HEIGHT - 1))] = 0;
	}

	dcPrintMask(cmask.mask, cmask.width, cmask.height);

	// Images
	dcImageInt16 image;
	image.width = WIDTH;
	image.height = HEIGHT;
	image.bands = BANDS;
	dcInitArrayInt16(&image.image, image.width, image.height, image.bands);

	for(i = 0; i < WIDTH*HEIGHT*BANDS; i++){
		
		image.image[i] = i;
	}

	dcPrintArrayInt16(image.image, image.width, image.height, image.bands);

	// Medians
	dcImageInt16 medians;
	medians.width = WIDTH;
	medians.height = HEIGHT;
	medians.bands = 1;
	dcInitArrayInt16(&medians.image, medians.width, medians.height, medians.bands);
	dcMediansBandInt16(image.image, medians.image, cmask.mask, medians.width, medians.height, image.bands, 1, 1, -9999);
	dcPrintArrayInt16(medians.image, medians.width, medians.height, medians.bands);

	// Free memory
	dcDestroyArray(cmask.mask);
	dcDestroyArray(image.image);
	dcDestroyArray(medians.image);

}
