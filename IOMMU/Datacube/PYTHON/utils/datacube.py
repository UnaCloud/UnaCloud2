import numpy as np
import ctypes as c
import pickle

class Datacube():

	def __init__(self, lib_c, lib_cuda):
		self.lib_c = lib_c
		self.lib_cuda = lib_cuda

	def np_medians(self, np_data, np_cloud_mask, normalized, min_valid_count, null_value):
		
		medians = {}

		for band in np_data:

			# Datos sin nubes
			data = np.where(np.logical_and(np_data[band] != null_value, np_cloud_mask), np_data[band], np.nan)

			# Todos los datos nulos
			null_data =~ np.isnan(data)

			# Normalizacion
			if normalized:
				# Medias por imagen
				medians_per_image = np.nanmean(data.reshape((data.shape[0], -1)), axis=1)

				# Desviacion estandar por imagen
				std_dev_per_image = np.nanstd(data.reshape((data.shape[0], -1)), axis=1)

				# Datos normalizados
				normalized_data = np.true_divide((data - medians_per_image[:, np.newaxis, np.newaxis]),
													std_dev_per_image[:, np.newaxis, np.newaxis]) \
									 * np.nanmean(std_dev_per_image) + np.nanmean(medians_per_image)
		
				# Calculo de la mediana en el tiempo
				medians[band] = np.nanmedian(normalized_data, axis=0)

				# ??
				#medians[band][np.sum(null_data, 0) < min_valid_count] = np.nan
			else:
				medians[band] = np.nanmedian(data, axis=0)
	
			medians[band] = np.where(np.isnan(medians[band]), null_value, medians[band]).astype('int16')

		return medians

	def c_medians_16(self, np_data, np_cloud_mask, normalized, min_valid_count, null_value):
		
		lib = c.CDLL(self.lib_c)
		dcMediansBandInt16 = lib.dcMediansBandInt16
		dcMediansBandInt16.argtypes = [c.POINTER(c.c_short), c.POINTER(c.c_short), c.POINTER(c.c_bool), c.c_int, c.c_int, c.c_int, c.c_bool, c.c_int, c.c_short]

		medians = {}

		for band in np_data:

			width = np_data[band].shape[2]
			height = np_data[band].shape[1]
			bands = np_data[band].shape[0]

			data = np_data[band].reshape(-1).astype('int16')
			median_band = np.zeros(width * height).astype('int16')
			cloud_mask = np_cloud_mask.reshape(-1).astype('bool')

			p_data = data.ctypes.data_as(c.POINTER(c.c_short))
			p_median_band = median_band.ctypes.data_as(c.POINTER(c.c_short))
			p_cloud_mask = cloud_mask.ctypes.data_as(c.POINTER(c.c_bool))
	
			dcMediansBandInt16(p_data, p_median_band, p_cloud_mask, width, height, bands, normalized, min_valid_count, null_value)

			medians[band] = median_band.reshape(height, width)

		return medians

	def c_medians_32(self, np_data, np_cloud_mask, normalized, min_valid_count, null_value):
		
		lib = c.CDLL(self.lib_c)

		dcMediansBandInt32 = lib.dcMediansBandInt32
		dcMediansBandInt32.argtypes = [c.POINTER(c.c_int), c.POINTER(c.c_int), c.POINTER(c.c_bool), c.c_int, c.c_int, c.c_int, c.c_bool, c.c_int, c.c_int]

		medians = {}

		for band in np_data:

			width = np_data[band].shape[2]
			height = np_data[band].shape[1]
			bands = np_data[band].shape[0]

			data = np_data[band].reshape(-1).astype('int32')
			median_band = np.zeros(width * height).astype('int32')
			cloud_mask = np_cloud_mask.reshape(-1).astype('bool')

			p_data = data.ctypes.data_as(c.POINTER(c.c_int))
			p_median_band = median_band.ctypes.data_as(c.POINTER(c.c_int))
			p_cloud_mask = cloud_mask.ctypes.data_as(c.POINTER(c.c_bool))
	
			dcMediansBandInt32(p_data, p_median_band, p_cloud_mask, width, height, bands, normalized, min_valid_count, null_value)

			medians[band] = median_band.reshape(height, width)

		return medians

	def c_medians_64(self, np_data, np_cloud_mask, normalized, min_valid_count, null_value):
		
		lib = c.CDLL(self.lib_c)
		dcMediansBandFloat64 = lib.dcMediansBandFloat64
		dcMediansBandFloat64.argtypes = [c.POINTER(c.c_double), c.POINTER(c.c_double), c.POINTER(c.c_bool), c.c_int, c.c_int, c.c_int, c.c_bool, c.c_int, c.c_double]

		medians = {}

		for band in np_data:

			width = np_data[band].shape[2]
			height = np_data[band].shape[1]
			bands = np_data[band].shape[0]

			data = np_data[band].reshape(-1).astype('float64')
			median_band = np.zeros(width * height).astype('float64')
			cloud_mask = np_cloud_mask.reshape(-1).astype('bool')

			p_data = data.ctypes.data_as(c.POINTER(c.c_double))
			p_median_band = median_band.ctypes.data_as(c.POINTER(c.c_double))
			p_cloud_mask = cloud_mask.ctypes.data_as(c.POINTER(c.c_bool))
	
			dcMediansBandFloat64(p_data, p_median_band, p_cloud_mask, width, height, bands, normalized, min_valid_count, null_value)

			medians[band] = median_band.reshape(height, width)

		return medians

	def cuda_medians_16(self, np_data, np_cloud_mask, normalized, min_valid_count, null_value):

		lib = c.CDLL(self.lib_cuda)
		dcMediansBandInt16 = lib.dcMediansBandInt16
		dcMediansBandInt16.argtypes = [c.POINTER(c.c_short), c.POINTER(c.c_short), c.POINTER(c.c_bool), c.c_int, c.c_int, c.c_int, c.c_bool, c.c_int, c.c_short]

		medians = {}

		for band in np_data:

			width = np_data[band].shape[2]
			height = np_data[band].shape[1]
			bands = np_data[band].shape[0]

			data = np_data[band].reshape(-1).astype('int16')
			median_band = np.zeros(width * height).astype('int16')
			cloud_mask = np_cloud_mask.reshape(-1).astype('bool')

			p_data = data.ctypes.data_as(c.POINTER(c.c_short))
			p_median_band = median_band.ctypes.data_as(c.POINTER(c.c_short))
			p_cloud_mask = cloud_mask.ctypes.data_as(c.POINTER(c.c_bool))
	
			dcMediansBandInt16(p_data, p_median_band, p_cloud_mask, width, height, bands, normalized, min_valid_count, null_value)

			medians[band] = median_band.reshape(height, width)

		return medians

	def cuda_medians_32(self, np_data, np_cloud_mask, normalized, min_valid_count, null_value):

		lib = c.CDLL(self.lib_cuda)
		dcMediansBandInt32 = lib.dcMediansBandInt32
		dcMediansBandInt32.argtypes = [c.POINTER(c.c_int), c.POINTER(c.c_int), c.POINTER(c.c_bool), c.c_int, c.c_int, c.c_int, c.c_bool, c.c_int, c.c_int]

		medians = {}

		for band in np_data:

			width = np_data[band].shape[2]
			height = np_data[band].shape[1]
			bands = np_data[band].shape[0]

			data = np_data[band].reshape(-1).astype('int32')
			median_band = np.zeros(width * height).astype('int32')
			cloud_mask = np_cloud_mask.reshape(-1).astype('bool')

			p_data = data.ctypes.data_as(c.POINTER(c.c_int))
			p_median_band = median_band.ctypes.data_as(c.POINTER(c.c_int))
			p_cloud_mask = cloud_mask.ctypes.data_as(c.POINTER(c.c_bool))
	
			dcMediansBandInt32(p_data, p_median_band, p_cloud_mask, width, height, bands, normalized, min_valid_count, null_value)

			medians[band] = median_band.reshape(height, width)

		return medians

	def cuda_medians_64(self, np_data, np_cloud_mask, normalized, min_valid_count, null_value):
		
		lib = c.CDLL(self.lib_cuda)
		dcMediansBandFloat64 = lib.dcMediansBandFloat64
		dcMediansBandFloat64.argtypes = [c.POINTER(c.c_double), c.POINTER(c.c_double), c.POINTER(c.c_bool), c.c_int, c.c_int, c.c_int, c.c_bool, c.c_int, c.c_double]

		medians = {}

		for band in np_data:

			width = np_data[band].shape[2]
			height = np_data[band].shape[1]
			bands = np_data[band].shape[0]

			data = np_data[band].reshape(-1).astype('float64')
			median_band = np.zeros(width * height).astype('float64')
			cloud_mask = np_cloud_mask.reshape(-1).astype('bool')

			p_data = data.ctypes.data_as(c.POINTER(c.c_double))
			p_median_band = median_band.ctypes.data_as(c.POINTER(c.c_double))
			p_cloud_mask = cloud_mask.ctypes.data_as(c.POINTER(c.c_bool))
	
			dcMediansBandFloat64(p_data, p_median_band, p_cloud_mask, width, height, bands, normalized, min_valid_count, null_value)

			medians[band] = median_band.reshape(height, width)

		return medians
