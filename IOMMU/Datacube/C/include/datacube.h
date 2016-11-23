typedef struct {
	_Bool *mask;
	int width;
	int height;
}dcMask;

typedef struct {
	unsigned char *image;
	int width;
	int height;
	int bands;
}dcImageUInt8;

typedef struct {
	short *image;
	int width;
	int height;
	int bands;
}dcImageInt16;

typedef struct {
	int *image;
	int width;
	int height;
	int bands;
}dcImageInt32;

typedef struct {
	float *image;
	int width;
	int height;
	int bands;
}dcImageFloat;

void dcInitMask(_Bool **, int, int);
void dcPrintMask(_Bool*, int, int);

void dcInitArrayUInt8(unsigned char**, int, int, int);
void dcPrintArrayUInt8(unsigned char*, int, int, int);

void dcInitArrayInt16(short**, int, int, int);
void dcPrintArrayInt16(short*, int, int, int);
void dcMediansBandInt16(short *, short *, _Bool *, int, int, int, _Bool, int, short);

void dcInitArrayInt32(int **, int, int, int);
void dcPrintArrayInt32(int *, int, int, int);
void dcMediansBandInt32(int *, int *, _Bool *, int, int, int, _Bool, int, int);

void dcMediansBandFloat64(double *, double *, _Bool *, int, int, int, _Bool, int, double);

void dcInitArrayFloat(float **, int, int, int);
void dcPrintArrayFloat(float *, int, int, int);

void dcDestroyArray(void *);
