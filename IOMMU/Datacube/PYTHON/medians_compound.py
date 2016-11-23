from ConfigParser import ConfigParser
import numpy as np

from utils.datacube import Datacube
from utils.counter import Counter
from utils.pickle_assist import PickleAssist

# System configuration ####
conf = ConfigParser()
conf.read('settings.conf')

dc = Datacube(conf.get('libraries', 'c_library'), conf.get('libraries', 'cuda_library'))
counter = Counter()
pickle = PickleAssist()

av_functions = {
				'np_medians' : dc.np_medians,
				'c_medians_16' : dc.c_medians_16,
				'c_medians_32' : dc.c_medians_32,
				'c_medians_64' : dc.c_medians_64,
				'cuda_medians_16' : dc.cuda_medians_16,
				'cuda_medians_32' : dc.cuda_medians_32,
				'cuda_medians_64' : dc.cuda_medians_64
				}

###########################

print 'Starting loading of Medians compound'

# Loading bands ###########
counter.start('Loading bands...')

np_data = pickle.load(conf.get('data', 'np_query'))

counter.stop()
###########################

# Loading cloud mask ######
counter.start('Loading cloud mask...')

np_cmask = pickle.load(conf.get('data', 'np_cmask'))

counter.stop()
###########################

# Run Tests ###############
num_tests = int(conf.get('tests', 'num_tests'))
functions = conf.get('tests', 'functions').split(',')
normalized = True if conf.get('tests', 'normalized') == 'True' else False
null_value = int(conf.get('tests', 'null_value'))
min_valid_pixels = int(conf.get('tests', 'min_valid_pixels'))

print 'Total tests: ' + str(num_tests)

with open(conf.get('tests', 'output_file'), 'w') as ofile:

	ofile.write('id_prueba,function,min_valid_pixels,normalized,time\n')

	for each_function in functions:
		print 'Beginning "' + each_function + '" test'

		i = 0

		while i < num_tests:
			i = i+1
			counter.start('Running medians compound - Test ' + str(i))
			av_functions[each_function](np_data, np_cmask, normalized, min_valid_pixels, null_value)
			counter.stop()
			ofile.write(str(i) + ',' + each_function + ',' + str(min_valid_pixels) + ',' + conf.get('tests', 'normalized') + ',' + str(counter.get_total_time()) + '\n')

###########################

print 'DONE'
