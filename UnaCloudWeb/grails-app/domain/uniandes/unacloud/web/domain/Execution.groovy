package uniandes.unacloud.web.domain

import java.util.concurrent.TimeUnit
import java.text.DateFormat
import java.text.SimpleDateFormat

import uniandes.unacloud.share.enums.ExecutionStateEnum;
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
	 * Actual state , by default is requested
	 */
	ExecutionState state 
	
	/**
	 * Execution last message
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
	 * Last report of execution
	 */
	Date lastReport = new Date()
		
	/**
	 * Duration of execution in milliseconds
	 */
	long duration
	
	/**
	 * Image id where files of this instance will be saved
	 * This id is optional
	 */
	long copyTo	
	
	static constraints = {
		executionNode nullable: true
		stopTime nullable: true 
		startTime nullable: true
		lastReport nullable: false
	}
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Calculates and returns the remaining execution time (00h:00m:00s format)
	 * @return formated remaining time
	 */
	def remainingTime() {
		if (stopTime == null || state.state != ExecutionStateEnum.DEPLOYED) 
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
	 * Saves entity with net interfaces
	 */
	def saveExecution() {		
		this.save(failOnError: true, flush: true)
		for (NetInterface netInterface in interfaces) {
			netInterface.ip.putAt('state', IPEnum.USED)
			netInterface.save(failOnerror: true, flush:true)
		}
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
		return state.state.equals(ExecutionStateEnum.DEPLOYED) || state.state.equals(ExecutionStateEnum.RECONNECTING) || state.state.equals(ExecutionStateEnum.FAILED)
	}
	
	/**
	 * Responsible for returning deployedImage
	 * @return deployed image
	 */
	def getDeployedImage() {
		return deployImage;
	}	
	
	/**
	 * Changes current execution state to next one
	 */	
	//TODO date should be from database
	def goNext(String newMessage) {
		if (state.next != null) {
			state = state.next
			message = newMessage
		}
	}
	
	/**
	 * Changes current execution state to requested next one if exits
	 */
	def goNextRequested	(String newMessage) {
		if (state.nextRequested != null) {
			state = state.nextRequested
			message = newMessage
		}
	}
	
	/**
	 * Changes current execution state to control next one if exits
	 */
	def goNextControl() {
		if (state.nextControl != null) {
			state = state.nextControl
			message = state.controlMessage
		}
	}
	
	def isControlExceeded(Date current) {
		println current.getTime() - lastReport.getTime() + " - " + state.controlTime
		if (state.nextControl != null)
			return current.getTime() - lastReport.getTime() > state.controlTime
		return false
	}
	
	/**
	 * Returns execution history based in one state
	 * @param state to search history for execution
	 * @return history status 
	 */
	def getHistoryStatus(ExecutionStateEnum searchState) {
		return ExecutionHistory.where{state.state == searchState && execution == this}.find()
	}
	
}
