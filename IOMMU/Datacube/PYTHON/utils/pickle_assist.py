import pickle

class PickleAssist():

	def load(self, pickle_file):
		with open(pickle_file, 'rb') as ifile:
			return pickle.load(ifile)

	def save(self, var, pickle_file):
		with open(pickle_file, 'wb') as ofile:
			pickle.dump(var, ofile)
