package back.services;

import static com.losandes.utils.Constants.ERROR_STATE;
import static com.losandes.utils.Constants.OFF_STATE;
import static com.losandes.utils.Constants.ON_STATE;
import static com.losandes.utils.Constants.VM_TURN_ON;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import unacloud2.DeployedCluster;
import unacloud2.PhysicalMachine;
import unacloud2.VirtualMachineExecution;
import unacloudEnums.VirtualMachineCPUStates
import unacloudEnums.VirtualMachineExecutionStateEnum;
import groovy.sql.Sql;



class BackPersistenceService {
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Updates the virtual machine on persistence service
	 * @param virtualMachineExecutionCode virtual machine id
	 * @param state new VM state
	 * @param message new VM message
	 */
	def updateVirtualMachineState(long virtualMachineExecutionCode, VirtualMachineExecutionStateEnum state, String message) {
		def vm=VirtualMachineExecution.get(virtualMachineExecutionCode)
		if(vm!=null){
			vm.putAt("status", state)
			vm.putAt("message", message)
		}
	}

	public void updateVirtualMachineCPUState(Object virtualMachineExecutionCode, VirtualMachineCPUStates cpuState) {
		/*Virtualmachineexecution vme = (Virtualmachineexecution)em.find(Virtualmachineexecution.class,virtualMachineExecutionCode);
		if(vme!=null){
			em.getTransaction().begin();
			vme.getVirtualmachine().setCpustate(cpuState.ordinal());
			em.merge(vme.getVirtualmachine());
			em.flush();
			em.getTransaction().commit();
			Elasticrule er = vme.getTemplate().getElasticrule();
			if(er!=null&&er.getActive()){
				Collection<Virtualmachine> vms = vme.getTemplate().getVirtualmachineCollection();
				int busy=0,on=0;
				for(Virtualmachine vm:vms){
					if(vm.getVirtualmachinestate()!=OFF_STATE){
						on++;
						if(vm.getCpustate().intValue()==VirtualMachineCPUStates.BUSY.ordinal())busy++;
					}
				}
				int perc=((busy*100)/on);
				System.out.println("busy %= "+busy+"/"+on);
				if(er.getLowerlimit()>perc){
					int t = 0;
					if(er.getAddition())t=er.getFactor().intValue();
					else if(er.getMultiply())t=(int)(on/er.getFactor());
					t=Math.min(on,t);

				}else if(er.getUpperlimit()<perc){
					int t = 0;
					if(er.getAddition())t=er.getFactor().intValue();
					else if(er.getMultiply()){
						t=(int)(on*er.getFactor())-on;
					}
					t=Math.min(vms.size()-on,t);
					System.out.println("Prender "+t);
					//UnaCloudOperations.turnOnVirtualCluster(vme.getSystemuser().getSystemusername(),"",vme.getTemplate().getTemplatecode(),t,vme.getVirtualmachineexecutionrammemory(),vme.getVirtualmachineexecutioncores(),0,20);
				}
			}
			
		}
		System.out.println(virtualMachineExecutionCode+" is "+cpuState);*/
	}

}
