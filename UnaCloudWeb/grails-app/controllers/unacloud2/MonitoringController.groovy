package unacloud2

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import javassist.bytecode.stackmap.BasicBlock.Catch;

class MonitoringController {
	
	/**
	 * Representation of monitoring services
	 */
	MonitoringService monitoringService
	
	def beforeInterceptor = {
		if(!session.user){
			flash.message="You must log in first"
			redirect(uri:"/login", absolute:true)
			return false
		}
		else if(!(session.user.userType.equals("Administrator"))){
			flash.message="You must be administrator to see this content"
			redirect(uri:"/error", absolute:true)
			return false
		}
	}
	
	def index(){
		def pm = PhysicalMachine.get(params.id);
		def monitor = monitoringService.getMetricsCPU(pm.getName())
		//def machineSet= lab.getOrderedMachines()
		[machine: pm, components:monitor, lab:pm.laboratory.id]
	}	
	
	def getReports(){
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS aa");
			Date init = dateFormat.parse(params.year+'-'+(params.month.length()==1?'0'+params.month:params.month)+'-'+(params.day.length()==1?'0'+params.day:params.day)+' '+params.hour+':00:00.000 '+params.sched);
			String host = params.host;
			Date end = new Date(init.getTime()+(1000*60*60*Integer.parseInt(params.range)));
			boolean type = params.report.equals("cpu")?false:true;
			def file = monitoringService.generateReport(host, init, end,type);
			response.setContentType("application/octet-stream")
			response.setHeader("Content-disposition", "attachment;filename="+file.getName())			
			response.outputStream << file.newInputStream()
			
		}catch(Exception e){
			e.printStackTrace();
			flash.message="There is an error with file, check logs for more information"
			redirect(uri:"/error", absolute:true)
			return false
		}
	}
	def getReportsLab(){
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS aa");
			Date init = dateFormat.parse(params.year+'-'+(params.month.length()==1?'0'+params.month:params.month)+'-'+(params.day.length()==1?'0'+params.day:params.day)+' '+params.hour+':00:00.000 '+params.sched);
			Date end = new Date(init.getTime()+(1000*60*60*Integer.parseInt(params.range)));
			Boolean energy = params.report[1].equals("energy")?true:false;
			Date d = new Date();		
			println params.lab
			String title = (energy?"Energy":"CPU")+"Reports_lab_"+Laboratory.get(Integer.parseInt(params.lab)).name+"_"+(d.getYear()+1900)+"-"+(d.getMonth()+1)+"-"+d.getDate()+'_'+d.getHours()+"-"+d.getMinutes();
			ByteArrayOutputStream baos = new ByteArrayOutputStream()
			ZipOutputStream zipFile = new ZipOutputStream(baos)
			params.each {
				if (it.key.contains("machine")){					
					PhysicalMachine pm= PhysicalMachine.get((it.key - "machine") as Integer)
					def file = monitoringService.generateReport(pm.name, init, end,energy);
					zipFile.putNextEntry(new ZipEntry(file.getName()))		
					file.withInputStream { i ->	zipFile << i }
					zipFile.closeEntry()
				}
			}
			zipFile.finish()
		    response.setHeader("Content-disposition", "filename=\""+title+".zip\"")
		    response.contentType = "application/zip"
		    response.outputStream << baos.toByteArray()
		    response.outputStream.flush()
		}catch(Exception e){
			e.printStackTrace();
			flash.message="There is an error with file, check logs for more information"
			redirect(uri:"/error", absolute:true)
			return false
		}		
	}
}
