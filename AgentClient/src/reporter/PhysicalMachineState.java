package reporter;

import com.losandes.utils.OperatingSystem;

import monitoring.PhysicalMachineMonitor;
import physicalmachine.Network;
import communication.AbstractGrailsCommunicator;

/**
 * Responsible for reporting the physical machine state to Clouder Server
 */
public class PhysicalMachineState {
	/**
	 * reports a start operation
	 */
	public static void reportPhyisicalMachineStart(){
		AbstractGrailsCommunicator.pushInfo("machineState/physicalMachineStart","hostname",Network.getHostname(),"monitorStatus",PhysicalMachineMonitor.getInstance().getStatusCpu().getTitle(),"monitorStatusEnergy",PhysicalMachineMonitor.getInstance().getStatusEnergy().getTitle());
	}
	/**
	 * reports a stop operation
	 */
	public static void reportPhyisicalMachineStop(){
		AbstractGrailsCommunicator.pushInfo("machineState/physicalMachineStop","hostname",Network.getHostname());
	}
	/**
	 * reports a log off event
	 */
	public static void reportPhyisicalMachineUserLogoff(){
		AbstractGrailsCommunicator.pushInfo("machineState/physicalMachineLogoff","hostname",Network.getHostname(),"monitorStatus",PhysicalMachineMonitor.getInstance().getStatusCpu().getTitle(),"monitorStatusEnergy",PhysicalMachineMonitor.getInstance().getStatusEnergy().getTitle());
	}
	/**
	 * reports a log in event
	 */
	public static void reportPhyisicalMachineUserLogin(){
		AbstractGrailsCommunicator.pushInfo("machineState/reportPhysicalMachineLogin","hostname",Network.getHostname(),"hostuser",OperatingSystem.getUserName(),"monitorStatus",PhysicalMachineMonitor.getInstance().getStatusCpu().getTitle(),"monitorStatusEnergy",PhysicalMachineMonitor.getInstance().getStatusEnergy().getTitle());
	}
	/**
	 * sends a registration request with machine info
	 */
	public static void registerPhysicalMachine(){
		AbstractGrailsCommunicator.pushInfo("machineState/registerPhysicalMachine","hostname",Network.getHostname(),"cores","1","ram","4096","mac","AA_BB_CC_DD_EE_AA","os","win7");
	}
}
