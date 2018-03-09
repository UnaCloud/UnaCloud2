package uniandes.unacloud.web.controllers

import uniandes.unacloud.web.services.LaboratoryService;
import uniandes.unacloud.web.services.UserGroupService;
import uniandes.unacloud.web.domain.enums.NetworkQualityEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;
import uniandes.unacloud.common.enums.TaskEnum;
import uniandes.unacloud.web.domain.IPPool;
import uniandes.unacloud.web.domain.Laboratory;
import uniandes.unacloud.web.domain.OperatingSystem;
import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.Platform;
import uniandes.unacloud.web.domain.User;

/**
 * This Controller contains actions to manage laboratory services: crud for laboratory, physical ip and physical machine.
 * This class render pages for user or process request in services to update entities, there is session verification before all actions
 * @author CesarF
 *
 */
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
		if (!session.user) {
			flash.message = "You must log in first"
			redirect(uri:"/login", absolute:true)
			return false
		}
		else {
			def user = User.get(session.user.id)
			session.user.refresh(user)
			if (!userGroupService.isAdmin(user)) {
				flash.message = "You must be administrator to see this content"
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
	 * Creates lab form action.
	 * @return list of network configurations
	 */
	def create() {
		[netConfigurations: NetworkQualityEnum.configurations]
	}
	
	/**
	 * Saves new lab action. Redirects to list when finished 
	 */
	def save() {
		if (params.name && NetworkQualityEnum.getNetworkQuality(params.net) != null
			&& params.netGateway && params.netMask && params.ipInit && params.ipEnd) {
			try {
				laboratoryService.createLab(params.name, (params.isHigh != null), NetworkQualityEnum.getNetworkQuality(params.net), (params.isPrivate != null), params.netGateway, params.netMask, params.ipInit, params.ipEnd);
				redirect(uri:"/admin/lab/list", absolute:true)
			} catch(Exception e) {
				flash.message = "Error: " + e.message
				redirect(uri:"/admin/lab/new", absolute:true)
			}			
		} else {
			flash.message = "All fields are required"
			redirect(uri:"/admin/lab/new", absolute:true)
		}
	}
	
	/**
	 * Laboratory physical machine index.
	 * @return laboratory selected and list of machines contained in it
	 */
	def lab() {
		def lab = Laboratory.get(params.id)
		if (lab) {			
			def machineSet = lab.getOrderedMachines()
			[lab: lab, machineSet:machineSet]
		} 
		else 
			redirect(uri:"/admin/lab/list", absolute:true)
		
	}	
	
	/**
	 * Deletes a laboratory, validates if lab have machines
	 */
	def delete() {
		def lab = Laboratory.get(params.id)
		if (lab) {
			try {
				laboratoryService.delete(lab)
				flash.message = "Your Laboratory has been modified"
				flash.type = "info"
			} catch(Exception e) {
				flash.message = e.message;
				redirect(uri:"/admin/lab/" + lab.id, absolute:true)
				return
			}			
		}
		redirect(uri:"/admin/lab/list", absolute:true)
	}
	
	/**
	 * returns form data to edit lab
	 * @return
	 */
	def edit() {
		def labt = Laboratory.get(params.id)
		if (labt)
			[lab:labt, netConfigurations: NetworkQualityEnum.configurations]
		else
			redirect(uri:"/admin/lab/list", absolute:true)		
	}
	
	/**
	 * returns form data to edit lab
	 * @return
	 */
	def saveEdit() {
		def lab = Laboratory.get(params.lab)
		if (lab)
			if (params.name && NetworkQualityEnum.getNetworkQuality(params.net) != null) {
				try {
					laboratoryService.setValues(lab, params.name, NetworkQualityEnum.getNetworkQuality(params.net), (params.isHigh != null))
					flash.message = "Your Laboratory has been modified"
					flash.type = "info"
					redirect(uri:"/admin/lab/" + lab.id, absolute:true)
				} catch(Exception e) {
					flash.message = e.message
					redirect(uri:"/admin/lab/" + lab.id, absolute:true)
					return
				}
			} else {
				flash.message = "All fields are required"
				redirect(uri:"/admin/lab/edit/" + lab.id, absolute:true)
			}			
		else
		   redirect(uri:"/admin/lab/list", absolute:true)
	}	

	/**
	 * Edits the status of a laboratory
	 * @return
	 */
	def setStatus() {
		def lab = Laboratory.get(params.id)
		if (lab)
			try {
				laboratoryService.setStatus(lab)
				flash.message = "Your Laboratory has been modified"
				flash.type = "info"
			} catch (Exception e) {
				flash.message = e.message
				redirect(uri:"/admin/lab/" + lab.id, absolute:true)
				return
			}		
		redirect(uri:"/admin/lab/list", absolute:true)
	}
	
	
}
