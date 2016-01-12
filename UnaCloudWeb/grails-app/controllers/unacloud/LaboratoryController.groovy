package unacloud

import unacloud.enums.NetworkQualityEnum;
import unacloud.enums.PhysicalMachineStateEnum;

class LaboratoryController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of laboratory services
	 */
	LaboratoryService laboratoryService
	
	/**
	 * Representation of group services
	 */
	
	UserGroupService userGroupService

    //-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------
	
	/**
	 * Makes session verifications before executing user administration actions
	 */
	
	def beforeInterceptor = {
		if(!session.user){
			flash.message="You must log in first"
			redirect(uri:"/login", absolute:true)
			return false
		}
		else{
			def user = User.get(session.user.id)
			session.user.refresh(user)
			if(!userGroupService.isAdmin(user)){
				flash.message="You must be administrator to see this content"
				redirect(uri:"/error", absolute:true)
				return false
			}
		}
	}
	
	/**
	 * Laboratory index action
	 * @return list of all laboratories
	 */
	def index() {
		[labs: Laboratory.list()]
	}
	
	/**
	 * Create lab form action.
	 * @return list of network configurations
	 */
	def create(){
		[netConfigurations: NetworkQualityEnum.configurations]
	}
	
	/**
	 * Save created lab action. Redirects to list when finished 
	 */
	def save(){
		if(params.name&&NetworkQualityEnum.getNetworkQuality(params.net)!=null
			&&params.netGateway&&params.netMask&&params.ipInit&&params.ipEnd){
			try{
				laboratoryService.createLab(params.name, (params.isHigh!=null),NetworkQualityEnum.getNetworkQuality(params.net), (params.isPrivate!=null),params.netGateway, params.netMask,params.ipInit,params.ipEnd);
				redirect(uri:"/admin/lab/list", absolute:true)
			}catch(Exception e){
				flash.message="Error: "+e.message
				redirect(uri:"/admin/lab/new", absolute:true)
			}
			
		}else{
			flash.message="All fields are required"
			redirect(uri:"/admin/lab/new", absolute:true)
		}
	}
	
	/**
	 * Laboratory physical machine index.
	 * @return laboratory selected and list of machines contained in it
	 */
	def lab() {
		def lab = Laboratory.get(params.id)
		if(lab){			
			def machineSet= lab.getOrderedMachines()
			[lab: lab, machineSet:machineSet]
		}else{
			redirect(uri:"/admin/lab/list", absolute:true)
		}
	}	
	
	/**
	 * Delete a laboratory, validates if lab have machines
	 */
	def delete(){
		def lab = Laboratory.get(params.id)
		if(lab){
			try{
				laboratoryService.delete(lab)
				flash.message="Your Laboratory has been modified"
				flash.type="info"
			}catch(Exception e){
				flash.message= e.message;
				redirect(uri:"/admin/lab/"+lab.id, absolute:true)
				return
			}			
		}
		redirect(uri:"/admin/lab/list", absolute:true)
	}
	
	/**
	 * return form data to edit lab
	 * @return
	 */
	def edit(){
		def labt = Laboratory.get(params.id)
		if(labt){
			[lab:labt,netConfigurations: NetworkQualityEnum.configurations]
		}else
			redirect(uri:"/admin/lab/list", absolute:true)		
	}
	
	/**
	 * return form data to edit lab
	 * @return
	 */
	def saveEdit(){
		def lab = Laboratory.get(params.lab)
		if(lab){
			if(params.name&&NetworkQualityEnum.getNetworkQuality(params.net)!=null){
				try{
					laboratoryService.setValues(lab, params.name, NetworkQualityEnum.getNetworkQuality(params.net), (params.isHigh!=null))
					flash.message="Your Laboratory has been modified"
					flash.type="info"
					redirect(uri:"/admin/lab/"+lab.id, absolute:true)
				}catch(Exception e){
					flash.message=e.message
					redirect(uri:"/admin/lab/"+lab.id, absolute:true)
					return
				}
			}else{
				flash.message="All fields are required"
				redirect(uri:"/admin/lab/edit/"+lab.id, absolute:true)
			}			
		}else
		   redirect(uri:"/admin/lab/list", absolute:true)
	}	

	/**
	 * Edit the status of a laboratory
	 * @return
	 */
	def setStatus(){
		def lab = Laboratory.get(params.id)
		if(lab){
			try{
				laboratoryService.setStatus(lab)
				flash.message="Your Laboratory has been modified"
				flash.type="info"
			}catch(Exception e){
				flash.message=e.message
				redirect(uri:"/admin/lab/"+lab.id, absolute:true)
				return
			}
		}
		redirect(uri:"/admin/lab/list", absolute:true)
	}
	
	/**
	 * Show list of ips
	 */
	def ipList(){
		Laboratory lab = Laboratory.get(params.id)
		if(lab&&params.pool){
			long ipId = Long.parseLong(params.pool)
			def ipPool = IPPool.findWhere(id:ipId,laboratory:lab)
			[lab: lab, pool : ipPool]
		}else
			redirect(uri:"/admin/lab/list", absolute:true)
	}
	
	/**
	 * Delete a valid IP in a lab
	 * 
	 * @return
	 */
	def ipDelete(){
		def lab = Laboratory.get(params.id)
		if(lab&&params.ip&&params.pool){
			try{
				laboratoryService.deleteIP(lab,params.ip)
				flash.message="Your IP has been removed"
				flash.type="success"
				redirect(uri:"/admin/lab/"+lab.id+"/pool/"+params.pool, absolute:true)
			}catch(Exception e){
				flash.message=e.message
				redirect(uri:"/admin/lab/"+lab.id, absolute:true)
				return
			}
		}else
			redirect(uri:"/admin/lab/list", absolute:true)
	}
	
	/**
	 * Change the state of a IP from AVAILABLE to DISABLE and vis
	 * @return
	 */
	def ipSet(){
		def lab = Laboratory.get(params.id)
		if(lab&&params.ip){
			try{
				laboratoryService.setStatusIP(lab,params.ip)
				flash.message="Your IP has been modified"
				flash.type="success"
				redirect(uri:"/admin/lab/"+lab.id+'/pool/'+params.pool, absolute:true)
			}catch(Exception e){
				flash.message=e.message
				redirect(uri:"/admin/lab/"+lab.id, absolute:true)
				return
			}
		}else
			redirect(uri:"/admin/lab/list", absolute:true)
	}
	
	/**
	 * Delete a valid Pool in a lab
	 *
	 * @return
	 */
	def poolDelete(){
		def lab = Laboratory.get(params.id)
		if(lab&&params.pool){
			try{
				laboratoryService.deletePool(lab,params.pool)
				flash.message="Your IP Pool has been removed"
				flash.type="success"
				redirect(uri:"/admin/lab/"+lab.id, absolute:true)
			}catch(Exception e){
				flash.message=e.message
				redirect(uri:"/admin/lab/"+lab.id, absolute:true)
				return
			}			
		}else
			redirect(uri:"/admin/lab/list", absolute:true)
	}
	
	/**
	 * Render form to create a new IP Pool
	 * @return
	 */
	def createPool(){
		def lab = Laboratory.get(params.id)
		if(lab)[lab:lab]
		else redirect(uri:"/admin/lab/list", absolute:true)
	}
	
	/**
	 * Save new IP Pool in a Lab
	 * @return
	 */
	def savePool(){
		def lab = Laboratory.get(params.id)
		if(lab){
			if(params.netGateway&&params.netMask&&params.ipInit&&params.ipEnd){
				try{
					laboratoryService.createPool(lab,(params.isPrivate!=null),params.netGateway, params.netMask,params.ipInit,params.ipEnd);
					flash.message="Yor new IP Pool has been added to lab"
					redirect(uri:"/admin/lab/"+lab.id, absolute:true)
				}catch(Exception e){
					flash.message="Error: "+e.message
					redirect(uri:"/admin/lab/"+lab.id+"/pool/new", absolute:true)
				}				
			}else{
				flash.message="All fields are required"
				redirect(uri:"/admin/lab/"+lab.id+"/pool/new", absolute:true)
			}
		}else redirect(uri:"/admin/lab/list", absolute:true)		
	}
	
	/**
	 * Delete a valid Host (Physical Machine) in a lab
	 *
	 * @return
	 */
	def deleteMachine(){
		def lab = Laboratory.get(params.id)
		if(lab&&params.host){
			try{
				laboratoryService.deleteHost(lab,params.host)
				flash.message="Your Host has been removed"
				flash.type="success"
				redirect(uri:"/admin/lab/"+lab.id, absolute:true)
			}catch(Exception e){
				flash.message=e.message
				redirect(uri:"/admin/lab/"+lab.id, absolute:true)
				return
			}
		}else
			redirect(uri:"/admin/lab/list", absolute:true)
	}
	
     /**
	 * Edit physical machine form action
	 * @return physical machine, laboratory which it belongs and list of all 
	 * operating systems
	 */
	def editMachine(){		
		def lab = Laboratory.get(params.id)
		def machine = PhysicalMachine.findWhere(id:Long.parseLong(params.host),laboratory:lab)
		if (!machine) 
			redirect(uri:"/admin/lab/list", absolute:true)
		else
			[machine: machine, lab: lab, oss:OperatingSystem.list()]
		
	}
	
	/**
	 * Create physical machine form action
	 * @return selected laboratory an list of operating systems
	 */
	def createMachine() {
		def lab = Laboratory.get(params.id)
		if(lab){
			[lab: lab,oss: OperatingSystem.list()]
		}else{
			redirect(uri:"/admin/lab/list", absolute:true)
		}
		
	}
	
	/**
	 * Saves created physical machine and redirects to lab page
	 * @return
	 */
	def saveMachine(){
		def lab = Laboratory.get(params.lab)
		if(lab){
			if(params.ip&&params.name&&params.ram&&params.pCores&&params.cores&&params.osId&&params.mac){
				if(params.ram.isInteger()&&params.cores.isInteger()&&params.pCores.isInteger()){
					try{
						laboratoryService.addMachine(params.ip, params.name, params.cores, params.pCores, params.ram, params.osId, params.mac, lab)
						flash.message="Your Host has been added"
						flash.type="success"
					}catch(Exception e){
						flash.message=e.message
					}
					redirect(uri:"/admin/lab/"+lab.id, absolute:true)
				}else{
					flash.message="CPU Cores and RAM Memory must be numbers."
					redirect(uri:"/admin/lab/"+lab.id+"/new", absolute:true)
				}
			}else{
				flash.message="All fields are required."
				redirect(uri:"/admin/lab/"+lab.id+"/new", absolute:true)
			}
		}else{
			redirect(uri:"/admin/lab/list", absolute:true)
		}
	}
	/**
	 * Save new values in an edit host
	 * @return
	 */
	def saveEditMachine(){
		def lab = Laboratory.get(params.lab)
		def machine = PhysicalMachine.findWhere(id:Long.parseLong(params.host),laboratory:lab)
		if(machine){
			if(params.ip&&params.name&&params.ram&&params.pCores&&params.cores&&params.osId&&params.mac){
				if(params.ram.isInteger()&&params.cores.isInteger()&&params.pCores.isInteger()){
					try{
						laboratoryService.editMachine(params.ip, params.name, params.cores, params.pCores, params.ram, params.osId, params.mac, machine)
						flash.message="Your Host has been modified"
						flash.type="success"						
					}catch(Exception e){
						flash.message=e.message
					}
					redirect(uri:"/admin/lab/"+lab.id, absolute:true)
				}else{
					flash.message="CPU Cores and RAM Memory must be numbers."
					redirect(uri:"/admin/lab/"+lab.id+"/edit/"+machine.id, absolute:true)
				}
			}else{
				flash.message="All fields are required."
				redirect(uri:"/admin/lab/"+lab.id+"/edit/"+machine.id, absolute:true)
			}
		}else{
			redirect(uri:"/admin/lab/list", absolute:true)
		}
	}
	
	/**
	 * Stop, Update agent or Clear Cache in selected machines. Returns to lab when finishes
	 */
	
	def updateMachines(){
		def lab = Laboratory.get(params.id)
		if(lab&&params.process in ['stop','update','cache']){
			def hostList = []
			params.each {
				if (it.key.contains("machine")){
					if(it.value.contains("on")){
						PhysicalMachine pm= PhysicalMachine.get((it.key - "machine_") as Integer)
						if(pm.state==PhysicalMachineStateEnum.ON){
							hostList.add(pm)
						}
					}
				}
			}
			if(hostList.size()>0){
				try{
					def user = User.get(session.user.id)
					laboratoryService.createRequestTasktoMachines(hostList,params.process,user)
					flash.message="Your request have been sent."
					flash.type = "info"
				}catch(Exception e){
					flash.message=e.message
				}
			}else
				flash.message="At least one host machine with state ON must be selected."
			redirect(uri:"/admin/lab/"+lab.id, absolute:true)
		}else redirect(uri:"/admin/lab/list", absolute:true)	
	}
}
