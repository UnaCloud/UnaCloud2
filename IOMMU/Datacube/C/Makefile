# Compiler
CC=gcc

# Sources
SOURCES=src

# Includes
INCLUDES=include

# Build
BUILD=build

# VPATH
VPATH=$(SOURCES) $(INCLUDES) $(BUILD)

# Flags
CFLAGS=-c -I $(INCLUDES) -fPIC

default: libdatacube.so

# test: negate.c libimage.so
# 	$(CC) $< -o $(BUILD)/negate -L$(BUILD) -limage -I $(INCLUDES)
test: datacube_test.c libc_datacube.so
	$(CC) $< -o $(BUILD)/datacube_test -L$(BUILD) -ldatacube -I $(INCLUDES) -lm

# Export datacube libraries to Python and CUDA projects
export: libc_datacube.so
	cp $(BUILD)/libc_datacube.so ../PYTHON/lib/
	#cp $(BUILD)/libdatacube.so ../CUDA/lib/

# Lib for Datacube
libc_datacube.so: datacube.o
	$(CC) -shared -o $(BUILD)/$@ $<

datacube.o: datacube.c datacube.h
	$(CC) $(CFLAGS) -o $@ $<

# Test lib
libimage.so: image.o
	$(CC) -shared -o $(BUILD)/$@ $<

image.o: image.c image.h
	$(CC) $(CFLAGS) -o $@ $<

# Clean all binaries
clean:
	rm *.o $(BUILD)/*

backup:
	tar -zcf backup.tar.gz src/ include/ Makefile
