package uniandes.unacloud.web.domain

import java.util.concurrent.TimeUnit
import java.text.DateFormat
import java.text.SimpleDateFormat

import uniandes.unacloud.common.enums.ExecutionStateEnum;

import uniandes.unacloud.share.enums.IPEnum;

/**
 * Entity to represent a virtual machine in execution; an instance of a virtual machine which has been deployed by user on UnaCloud infrastructure.
 * @author CesarF
 *
 */
class Execution {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Virtual machine hostname
	 */
    String name
	
	/**
	 * Virtual machine hardware profile assigned
	 */
	HardwareProfile hardwareProfile
	
	/**
	 * Configured Net interfaces 
	 */
	static hasMany = [interfaces:NetInterface]
	
	/**
	 * Date when the node was started
	 */
	Date startTime
	
	/**
	 * Date when the node was or should be stopped
	 */
	Date stopTime
	
	/**
	 * Actual node state  (QUEUED,COPYING,CONFIGURING,DEPLOYING,DEPLOYED,FAILED,FINISHING,FINISHED,REQUEST_COPY,RECONNECTING)
	 */
	ExecutionStateEnum status
	
	/**
	 * Virtual Machine interface message
	 */
	String message
	
	/**
	 * Physical machine where the node is or was deployed
	 */
	PhysicalMachine executionNode
	
		
	/**
	 * deployed Image 
	 */
	static belongsTo = [deployImage: DeployedImage]
	
	/**
	 * Last report of virtual machine
	 */
	Date lastReport
	
	
	static constraints = {
		executionNode nullable: true
		stopTime nullable: true 
		startTime nullable:true
		lastReport nullable:true
	}
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Calculates and returns the remaining execution time (00h:00m:00s format)
	 * @return formated remaining time
	 */
	def remainingTime(){
		if(stopTime==null||status!=ExecutionStateEnum.DEPLOYED)return '--'
		long millisTime=(stopTime.getTime()-System.currentTimeMillis())/1000
		String s = ""+millisTime%60;
        String m = ""+((long)(millisTime/60))%60;
        String h = ""+((long)(millisTime/60/60));
        if(s.length()==1)s="0"+s;
        if(m.length()==1)m="0"+m;
        return h+"h:"+m+"m:"+s+"s"
	}
	
	/**
	 * Calculates and returns the remaining execution time in hours
	 * @return hours of execution remaining
	 */
	int runningTimeInHours(){
		long millisTime=(stopTime.getTime()-startTime.getTime())/1000
		return (millisTime/60/60)+1
	}
	
	/**
	 * Saves entity with net interfaces
	 */
	def saveExecution(){		
		this.save(failOnError:true,flush:true)
		for(NetInterface netInterface in interfaces){
			netInterface.ip.putAt('state',IPEnum.USED)
			netInterface.save(failOnerror:true,flush:true)
		}
	}
	
	/**
	 * Sets status to finished and breaks free IP from net interfaces.
	 */
	def finishExecution(){
		this.putAt("status", ExecutionStateEnum.FINISHED)
		this.putAt("stopTime", new Date())
		for(NetInterface netinterface in interfaces)
			netinterface.ip.putAt("state",IPEnum.AVAILABLE)
	}
	
	/**
	 * Returns main IP configured in Net interfaces
	 * currently is number one
	 * @return
	 */
	def mainIp(){
		return interfaces.getAt(0).ip
	}
	
	/**
 	 * Returns time of last state from node
	 * @return last date reported in state machine graph
	 */
	def Date getLastStateTime(){
		def exe = this
		//def requestExec = ExecutionRequest.findAll("from ExecutionRequest as e where e.execution = ? and status = ? ",[this,status],[max:1, sort:'requestTime', order: "asc"])
		def requestExec = ExecutionRequest.list(fetch: [execution: exe,status:exe.status],max:1, sort:'requestTime', order: "desc")		
		if(requestExec&&requestExec.size()>0)return requestExec.get(0).requestTime
		else new Date();
	}
	
	/**
	 * Returns database id
	 * @return Long id
	 */
	def Long getDatabaseId(){
		return id;
	}
	
	/**
	 * Validates if execution must show its details based in status used in views
	 * @return true in case execution has DEPLOYED, RECONNECTING or FAILED status
	 * 
	 */
	def boolean showDetails(){
		return status.equals(ExecutionStateEnum.DEPLOYED)||status.equals(ExecutionStateEnum.RECONNECTING)||status.equals(ExecutionStateEnum.FAILED)
	}
}
