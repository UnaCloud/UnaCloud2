import time

class Counter():

	STAT_RUNNING = 'Idle'
	STAT_IDLE = 'Running'

	def __init__(self):
		print 'New counter created'
		self.status = self.STAT_IDLE

	def start(self, message):
		if self.status == self.STAT_IDLE:
			self.start_time = time.time()
			self.status = self.STAT_RUNNING
			print message
		else:
			print 'The counter is already running'

	def stop(self):
		if self.status == self.STAT_RUNNING:
			self.end_time = time.time()
			self.status = self.STAT_IDLE
			self.print_time()
		else:
			print 'The counter is not running'

	def print_status(self):
		print 'Status: ' + self.status

	def get_total_time(self):
		if self.status == self.STAT_IDLE:
			return (self.end_time - self.start_time)
		else:
			print 'The counter is running'
			return None

	def print_time(self):
		if self.status == self.STAT_IDLE:
			print 'Elapsed time: ' + (str)(self.end_time - self.start_time)
		else:
			print 'The counter is running'
