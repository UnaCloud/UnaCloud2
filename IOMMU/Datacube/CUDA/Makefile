# Compiler
CC=g++

# CUDA Compiler
NVCC=nvcc

# Sources
SOURCES=src

# Includes
INCLUDES=include

# Libraries
LIB=lib

# Build
BUILD=build

# VPATH
VPATH=$(SOURCES) $(INCLUDES) $(BUILD)

all: libcuda_datacube.so

export: libcuda_datacube.so
	cp $(BUILD)/libcuda_datacube.so ../PYTHON/lib/

libcuda_datacube.so: cuda_datacube.cu cuda_datacube.h
	$(NVCC) -Wno-deprecated-gpu-targets -Xcompiler -fPIC -shared -I $(INCLUDES) -o $(BUILD)/$@ $<

clean:
	rm *.o $(BUILD)/*

backup:
	tar -zcf image.tar.gz src/ include/ Makefile
