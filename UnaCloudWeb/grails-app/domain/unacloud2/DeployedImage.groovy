package unacloud2

import java.util.ArrayList;

import unacloudEnums.VirtualMachineExecutionStateEnum;

class DeployedImage {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * representation of the virtual machine image
	 */
	static belongsTo =  [image: VirtualMachineImage]
	
	/**
	 * it tells if the image is set to be deployed in high availability machines
	 */
	boolean highAvaliavility
	
	/**
	 * list of deployed nodes from the image
	 */
	static hasMany = [virtualMachines: VirtualMachineExecution]
    
	
	static constraints = {
    }
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Gets virtual machines ordered by name
	 * @return list of ordered virtual machines
	 */
	ArrayList <VirtualMachineExecution> getOrderedVMs(){
		VirtualMachineComparator c= new VirtualMachineComparator()
		ArrayList <VirtualMachineExecution> array = new ArrayList(virtualMachines).sort(true,c)
		return array
	}
	
	/**
	 * Returns the number of nodes in an active state
	 * @return the number of active nodes
	 */
	def numberOfActiveMachines(){
		def counter=0
		virtualMachines.each {
			if (!(it.status==VirtualMachineExecutionStateEnum.FINISHED))
			counter++
		}
		return counter
	}
	
	/**
	 * Gets the RAM property set when this image was deployed
	 * @return RAM memory of any virtual machine
	 */
	def getDeployedRAM(){
		for(virtualMachine in virtualMachines){
			return virtualMachine.hardwareProfile.ram
		}
	}
	
	/**
	 * Gets the cores property set when this image was deployed
	 * @return number of cores of any virtual machine
	 */
	def getDeployedCores(){
		for(virtualMachine in virtualMachines){
			return virtualMachine.hardwareProfile.cores
		}
	}
	
	/**
	 * Gets the hostname property set when this image was deployed
	 * @return hostname pattern obtained from any virtual machine
	 */
	def getDeployedHostname(){
		for(virtualMachine in virtualMachines){
			def ip =virtualMachine.ip.ip.split('\\.')
			println ip
			return virtualMachine.name.substring(0, virtualMachine.name.length()-(ip[2].length()+ip[3].length()))
		}
	}
}
