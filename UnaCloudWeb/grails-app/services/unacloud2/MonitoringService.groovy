package unacloud2

import monitoring.MonitorResources;
import grails.transaction.Transactional

@Transactional
class MonitoringService {

	MonitorResources monitor = new MonitorResources();
	
    def getMetricsCPU(String host){
		return monitor.getCPUMetrics(host);
	}
	def generateReport(String host, Date start, Date finish, boolean energy){
		if(energy)return monitor.createReportEnergy(host, start, finish);
		else return monitor.createReportCPU(host, start, finish);
	}
}
