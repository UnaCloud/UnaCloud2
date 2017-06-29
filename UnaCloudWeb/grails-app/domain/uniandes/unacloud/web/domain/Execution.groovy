package uniandes.unacloud.web.domain

import java.util.concurrent.TimeUnit
import java.text.DateFormat
import java.text.SimpleDateFormat

import uniandes.unacloud.common.enums.ExecutionStateEnum;

import uniandes.unacloud.share.enums.IPEnum;

/**
 * Entity to represent an execution; an instance which has been deployed by user on UnaCloud infrastructure.
 * @author CesarF
 *
 */
class Execution {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Execution hostname
	 */
    String name
	
	/**
	 * Execution hardware profile assigned
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
	 * Execution last message
	 */
	String message
	
	/**
	 * Physical machine where the node is or was deployed
	 */
	PhysicalMachine executionNode
	
	/**
	 * Last update in graph state
	 */
	Date lastUpdate = new Date();
		
	/**
	 * deployed Image 
	 */
	static belongsTo = [deployImage: DeployedImage]
	
	/**
	 * Last report of execution
	 */
	Date lastReport
	
	
	static constraints = {
		executionNode nullable: true
		stopTime nullable: true 
		startTime nullable:true
		lastReport nullable:true
		lastUpdate nullable:true
	}
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Calculates and returns the remaining execution time (00h:00m:00s format)
	 * @return formated remaining time
	 */
	def remainingTime() {
		if (stopTime == null || status != ExecutionStateEnum.DEPLOYED) 
			return '--'
		long millisTime = (stopTime.getTime() - System.currentTimeMillis()) / 1000
		String s = "" + millisTime % 60;
        String m = "" + ((long)(millisTime / 60)) % 60;
        String h = "" + ((long)(millisTime / 60 / 60));
        if (s.length() == 1) s = "0" + s;
        if (m.length() == 1) m = "0" + m;
        return h + "h:" + m + "m:" + s + "s"
	}
	
	/**
	 * Calculates and returns the remaining execution time in hours
	 * @return hours of execution remaining
	 */
	int runningTimeInHours() {
		long millisTime = (stopTime.getTime() - startTime.getTime()) / 1000
		return (millisTime / 60 / 60) + 1
	}
	
	/**
	 * Saves entity with net interfaces
	 */
	def saveExecution() {		
		this.save(failOnError: true, flush: true)
		for (NetInterface netInterface in interfaces) {
			netInterface.ip.putAt('state',IPEnum.USED)
			netInterface.save(failOnerror:true, flush:true)
		}
	}
	
	/**
	 * Sets status to finished and breaks free IP from net interfaces.
	 */
	def finishExecution() {
		this.putAt("status", ExecutionStateEnum.FINISHED)
		this.putAt("stopTime", new Date())
		for (NetInterface netinterface in interfaces)
			netinterface.ip.putAt("state", IPEnum.AVAILABLE)
	}
	
	/**
	 * Returns main IP configured in Net interfaces
	 * currently is number one
	 * @return
	 */
	def mainIp() {
		return interfaces.getAt(0).ip
	}
		
	/**
	 * Returns database id
	 * @return Long id
	 */
	def Long getDatabaseId() {
		return id;
	}
	
	/**
	 * Validates if execution must show its details based in status used in views
	 * @return true in case execution has DEPLOYED, RECONNECTING or FAILED status
	 * 
	 */
	def boolean showDetails() {
		return status.equals(ExecutionStateEnum.DEPLOYED) || status.equals(ExecutionStateEnum.RECONNECTING) || status.equals(ExecutionStateEnum.FAILED)
	}
	
	/**
	 * Responsible for returning deployedImage
	 * @return deployed image
	 */
	def getDeployedImage() {
		return deployImage;
	}
	
	
	/**
	 * Validates if current state time is above of a certain date given as parameter
	 * @param date to compare
	 * @return true in case current state time is above of a certain date, false otherwise
	 */
	def isAboveStateTime(Date date) {
		return date.getTime() - lastUpdate.getTime() > status.getTime();
	}
}
