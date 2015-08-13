package unacloudservices

import java.util.Date;

import javassist.bytecode.stackmap.BasicBlock.Catch;
import unacloud2.IP;
import unacloud2.OperatingSystem;
import unacloud2.enums.PhysicalMachineStateEnum;

import com.amazonaws.services.ec2.model.MonitoringState;

import back.services.PhysicalMachineStateManagerService;

class MachineStateController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of physical machine state manager
	 */
	
	PhysicalMachineStateManagerService physicalMachineStateManagerService;
	
	//-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------
	
	/**
	 * Receives agent reports and renders a response
	 */
	
	def physicalMachineStart(){
		String hostname=params['hostname']
		String status = params['monitorStatus']
		String statusEnergy = params['monitorStatusEnergy']
		physicalMachineStateManagerService.reportPhysicalMachine(hostname,null,request.getRemoteAddr(),status,statusEnergy)
		render "succeeded"
	}
	
	/**
	 * Receives physical machine stop requests and renders a response
	 */
	
	def physicalMachineStop(){
		String hostname=params['hostname']
		physicalMachineStateManagerService.stopVirtualMachines(hostname)
		physicalMachineStateManagerService.turnOffPhysicalMachine(hostname)
		render "succeeded"
	}
	
	/**
	 * Receives log out info from agents, making physical machine info changes 
	 *  and rendering a response
	 */
	
	def physicalMachineLogoff(){
		String hostname=params['hostname']
		String status = params['monitorStatus']
		String statusEnergy = params['monitorStatusEnergy']
		try{
			physicalMachineStateManagerService.reportPhysicalMachine(hostname,null,request.getRemoteAddr(),status,statusEnergy)
		}catch(Exception ex){
			//println "  Error on reportPhysicalMachineLogin "+ hostname+" "+hostuser+" "+ex.getMessage();
		}
		render "succeeded"
	}
	
	/**
	 * Receives log in info from agents, making physical machine info changes 
	 *  and rendering a response
	 */
	
	def reportPhysicalMachineLogin(){
		String hostname=params['hostname']
		String hostuser=params['hostuser']
		String status = params['monitorStatus']
		String statusEnergy = params['monitorStatusEnergy']
		try{
			physicalMachineStateManagerService.reportPhysicalMachine(hostname,hostuser,request.getRemoteAddr(),status,statusEnergy)
		}catch(Exception ex){
			println "  Error on reportPhysicalMachineLogin "+ hostname+" "+hostuser+" "+ex.getMessage();
		}
		render "succeeded"
	}
	
	/**
	 * Not implemented yet
	 */
	
	def registerPhysicalMachine(){
		String hostname=params['hostname']
		String cores=params['cores']
		String ram=params['ram']
		String mac=params['mac']
		String operatingSystem=params['os']
	}
}
