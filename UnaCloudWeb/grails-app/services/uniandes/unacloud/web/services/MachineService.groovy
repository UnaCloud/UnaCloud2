package uniandes.unacloud.web.services

import uniandes.unacloud.share.enums.ExecutionStateEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;
import uniandes.unacloud.web.domain.Execution
import uniandes.unacloud.web.domain.ExecutionHistory
import uniandes.unacloud.web.domain.Laboratory;
import uniandes.unacloud.web.domain.OperatingSystem;
import uniandes.unacloud.web.domain.PhysicalIP
import uniandes.unacloud.web.domain.PhysicalMachine
import uniandes.unacloud.web.domain.Platform;
import uniandes.unacloud.web.queue.QueueTaskerControl;
import grails.transaction.Transactional

@Transactional
class MachineService {

   	
	/**
	 * Deletes a host (physical machine) from a lab
	 * Validates if there are not deployments in host
	 * @param lab laboratory where is located the host
	 * @param host to be deleted
	 */
	def deleteMachine(Laboratory lab, host) {
		PhysicalMachine hostMachine = PhysicalMachine.where{id == host && laboratory == lab}.find()
		if (hostMachine) {
			if (Execution.where {executionNode == hostMachine && state.state != ExecutionStateEnum.FINISHED}.findAll().size() > 0)
				throw new Exception('The Host can not be deleted because there are some deployments linked to this one')
			def executions = Execution.where{
					executionNode == hostMachine && state.state == ExecutionStateEnum.FINISHED}.findAll()
			for (Execution exe in executions)
					exe.putAt("executionNode", null)
			hostMachine.delete()
		}
	}
	
	/**
	 * Adds a new machine to a given laboratory
	 * @param ip physical machine's IP
	 * @param name physical machine's name
	 * @param cores physical machine's number of processors
	 * @param ram physical machine's RAM memory
	 * @param disk physical machine's available disk space
	 * @param osId physical machine's operating system
	 * @param mac physical machine's MAC address
	 * @param lab Laboratory
	 */
	
	def addMachine(ip, name, cores, pCores, ram, osId, mac, Laboratory lab, plats) {
		def physicalMachine = new PhysicalMachine(name: name, cores: cores, pCores: pCores, ram: ram, highAvailability: (lab.highAvailability),
			mac: mac, state: PhysicalMachineStateEnum.OFF, operatingSystem: OperatingSystem.get(osId), laboratory: lab, ip: new PhysicalIP(ip: ip), platforms: [])
		if (plats.getClass().equals(String))
			physicalMachine.platforms.add(Platform.get(plats))
		else
			for (platId in plats)
				physicalMachine.platforms.add(Platform.get(platId))
		physicalMachine.save(failOnError:true)
	}
	
	/**
	 * Sets values in a host machine
	 * @param ip new ip for host
	 * @param name new name
	 * @param cores new logical cores quantity
	 * @param pCores new physical cores quantity
	 * @param ram new memory in host
	 * @param osId new operating system id
	 * @param mac new MAC address
	 * @param host new name in network
	 * @return
	 */
	def editMachine(ip, name, cores, pCores, ram, osId, mac, PhysicalMachine host, plats) {
		if (!host.ip.ip.equals(ip))
			host.ip.setIp(ip)
		host.setName(name)
		host.setMac(mac)
		host.setCores(Integer.parseInt(cores))
		host.setpCores(Integer.parseInt(pCores))
		host.setRam(Integer.parseInt(ram))
		host.setOperatingSystem(OperatingSystem.get(osId))
		Set platforms = []
		if (plats.getClass().equals(String))
			platforms.add(Platform.get(plats))
		else
			for (platId in plats)
				platforms.add(Platform.get(platId))
		host.platforms = platforms
		host.save(failOnError:true)
	}
	
	/**
	 * Creates a task to stop, update agent or clear cache in a list of host machines
	 * Sends task for queue if it is valid
	 * @param machines
	 */
	def createRequestTasktoMachines(machines, task, user){
		if (task == null || machines.size() == 0)
				throw new Exception("Invalid values");
		List<PhysicalMachine> machineList = new ArrayList<PhysicalMachine>();
		for (PhysicalMachine pm : machines) {
			pm.putAt("state", PhysicalMachineStateEnum.PROCESSING)
			machineList.add(pm);
		}
		QueueTaskerControl.taskMachines(machineList,task, user)
	}

}
