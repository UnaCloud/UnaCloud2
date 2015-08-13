package unacloud2

import unacloud2.enums.PhysicalMachineStateEnum;
import back.userRestrictions.UserRestrictionProcessorService;

class ClusterService {
	//-----------------------------------------------------------------
	// Services
	//-----------------------------------------------------------------
	
	/**
	 * Representation of UserRestriction services
	 */
	UserRestrictionProcessorService userRestrictionProcessorService
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Creates a new cluster with the given parameters
	 * @param images Virtual machine images belonging to new cluster
	 * @param cluster empty new cluster
	 * @param user cluster owner
	 */
	
    def saveCluster(images, Cluster cluster, User user) {
		cluster.images=[]
		if(images.getClass().equals(String)){
			cluster.images.add(VirtualMachineImage.get(images))
		}
		else{
			for(image in images){
				cluster.images.add(VirtualMachineImage.get(image))
			}
		}
		cluster.save(failOnError: true)
		if(user.userClusters==null)
			user.userClusters
		user.userClusters.add(cluster)
		user.save(failOnError: true)
    }
	
	/**
	 * Deletes the cluster given as parameter
	 * @param cluster cluster to be deleted
	 * @param user cluster owner
	 */
	def deleteCluster(Cluster cluster, User user){
		for (depCluster in DeployedCluster.getAll()){
			if(depCluster.cluster!= null){
				if (depCluster.cluster.equals(cluster)){
					depCluster.cluster=null
					depCluster.save()
				}
			}
		}		
		user.userClusters.remove(cluster)
		user.save()
		cluster.delete()	
	}
	
	/**
	 * TODO documentation
	 */
	def int calculateMaxDeployments(User user, HardwareProfile hwp){
		try {
			if(hwp.cores>0&&hwp.ram>0){
				def labs = userRestrictionProcessorService.getAvoidedLabs(user)
				int cantidad = 0;
				for(Laboratory lab in labs){
					int cantidadLab = 0
					int ips = lab.virtualMachinesIPs.getIpsQuantity()
					def machines = lab.physicalMachines.findAll{it.state == PhysicalMachineStateEnum.ON}
					for(PhysicalMachine mac in machines){
						int macCores = mac.cores
						int macRam = mac.ram
						while(macCores>0&&macRam>0&&cantidadLab<ips){
							if(macCores>=hwp.cores&&macRam>=hwp.ram){
								cantidadLab++;
							}
							macCores-=hwp.cores
							macRam-=hwp.ram
						}
						if(cantidadLab>=ips){
							cantidadLab = ips
							break
						}
					}
					cantidad += cantidadLab
				}
				return cantidad;
			}else return -1;
		} catch (Exception e) {
			e.printStackTrace()
			return -1
		}		
	}
}
