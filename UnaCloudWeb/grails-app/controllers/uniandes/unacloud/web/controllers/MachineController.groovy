package uniandes.unacloud.web.controllers

import uniandes.unacloud.common.enums.TaskEnum
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum
import uniandes.unacloud.web.domain.Laboratory
import uniandes.unacloud.web.domain.OperatingSystem
import uniandes.unacloud.web.domain.PhysicalMachine
import uniandes.unacloud.web.domain.Platform
import uniandes.unacloud.web.domain.User
import uniandes.unacloud.web.services.MachineService
import uniandes.unacloud.web.services.ServerVariableService

class MachineController extends AbsAdminController {

	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------

	/**
	 * Representation of laboratory services
	 */
	MachineService machineService

	/**
	 * Representation of server variable service
	 */
	ServerVariableService serverVariableService

	//-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------

	/**
	 * Deletes a valid Host (Physical Machine) in a lab
	 *
	 * @return
	 */
	def delete() {
		def lab = Laboratory.get(params.id)
		if (lab && params.host) {
			try {
				machineService.deleteMachine(lab, params.host)
				flash.message = "Your Host has been removed"
				flash.type = "success"
				redirect(uri: "/admin/lab/" + lab.id, absolute: true)
			} catch (Exception e) {
				flash.message = e.message
				redirect(uri: "/admin/lab/" + lab.id, absolute: true)
				return
			}
		} else
			redirect(uri: "/admin/lab/list", absolute: true)
	}

	/**
	 * Edits physical machine form action
	 * @return physical machine, laboratory which it belongs and list of all
	 * operating systems
	 */
	def edit() {
		def lab = Laboratory.get(params.id)
		def machine = PhysicalMachine.findWhere(id: Long.parseLong(params.host), laboratory: lab)
		if (!machine)
			redirect(uri: "/admin/lab/list", absolute: true)
		else
			[machine: machine, lab: lab, oss: OperatingSystem.list(), platforms: Platform.list()]

	}

	/**
	 * Edits physical machine form action
	 * @return physical machine, laboratory which it belongs and list of all
	 * operating systems
	 */
	def index() {
		def lab = Laboratory.get(params.id)
		def machine = PhysicalMachine.findWhere(id: Long.parseLong(params.host), laboratory: lab)
		if (!machine)
			redirect(uri: "/admin/lab/list", absolute: true)
		else {
			def fileUrl = null
			if (machine.lastLog != null && !machine.lastLog.isEmpty())
				fileUrl = serverVariableService.getUrlFileManager() + "log/" + machine.name + "/" + machine.lastLog
			[machine: machine, lab: lab, fileUrl: fileUrl]
		}

	}

	/**
	 * Creates physical machine form action
	 * @return selected laboratory an list of operating systems
	 */
	def create() {
		def lab = Laboratory.get(params.id)
		if (lab) {
			[lab: lab, oss: OperatingSystem.list(), platforms: Platform.list()]
		} else {
			redirect(uri: "/admin/lab/list", absolute: true)
		}
	}

	/**
	 * Saves created physical machine and redirects to lab page
	 * @return
	 */
	def save() {
		def lab = Laboratory.get(params.lab)
		if (lab) {
			if (!params.plats)
				params.plats = []
			if (params.ip && params.name && params.ram && params.pCores && params.cores && params.osId && params.mac) {
				if (params.ram.isInteger() && params.cores.isInteger() && params.pCores.isInteger()) {
					try {
						machineService.addMachine(params.ip, params.name, params.cores, params.pCores, params.ram, params.osId, params.mac, lab, params.plats)
						flash.message = "Your Host has been added"
						flash.type = "success"
					} catch (Exception e) {
						e.printStackTrace();
						flash.message = e.message
					}
					redirect(uri: "/admin/lab/" + lab.id, absolute: true)
				} else {
					flash.message = "CPU Cores and RAM Memory must be numbers."
					redirect(uri: "/admin/lab/" + lab.id + "/new", absolute: true)
				}
			} else {
				flash.message = "All fields are required."
				redirect(uri: "/admin/lab/" + lab.id + "/new", absolute: true)
			}
		} else
			redirect(uri: "/admin/lab/list", absolute: true)

	}

	/**
	 * Saves new values in an edit host
	 * @return
	 */
	def saveEdit() {
		def lab = Laboratory.get(params.lab)
		def machine = PhysicalMachine.findWhere(id: Long.parseLong(params.host), laboratory: lab)
		if (machine) {
			if (!params.plats)
				params.plats = []
			if (params.ip && params.name && params.ram && params.pCores && params.cores && params.osId && params.mac) {
				if (params.ram.isInteger() && params.cores.isInteger() && params.pCores.isInteger()) {
					try {
						machineService.editMachine(params.ip, params.name, params.cores, params.pCores, params.ram, params.osId, params.mac, machine, params.plats)
						flash.message = "Your Host has been modified"
						flash.type = "success"
					} catch (Exception e) {
						flash.message = e.message
					}
					redirect(uri: "/admin/lab/" + lab.id, absolute: true)
				} else {
					flash.message = "CPU Cores and RAM Memory must be numbers."
					redirect(uri: "/admin/lab/" + lab.id + "/edit/" + machine.id, absolute: true)
				}
			} else {
				flash.message = "All fields are required."
				redirect(uri: "/admin/lab/" + lab.id + "/edit/" + machine.id, absolute: true)
			}
		} else
			redirect(uri: "/admin/lab/list", absolute: true)
	}

	/**
	 * Send a task to agents. Returns to lab when finishes
	 */
/**
 * Send a task to agents. Returns to lab when finishes
 */
	def requestTask() {
		def lab = Laboratory.get(params.id)
		print params.process+" "+TaskEnum.getEnum(params.process)
		if (lab && TaskEnum.getEnum(params.process) != null) {
			def hostList = []
			print params
			params.each {
				if (it.key.contains("machine")) {
					if (it.value.contains("on")) {
						PhysicalMachine pm = PhysicalMachine.get((it.key - "machine_") as Integer)
						print pm
						if (pm.state == PhysicalMachineStateEnum.ON)
							hostList.add(pm)
					}
				}
			}
			if (hostList.size() > 0) {
				try {
					def user = User.get(session.user.id)
					machineService.createRequestTasktoMachines(hostList, TaskEnum.getEnum(params.process), user)
                    if (hostList.size()==1)
                        flash.message = "Your request has been sent."
                    else
					    flash.message = "Your requests have been sent."
					flash.type = "info"
				} catch (Exception e) {
					flash.message = e.message
				}
			} else
				flash.message = "At least one host machine with state ON must be selected."
			redirect(uri: "/admin/lab/" + lab.id, absolute: true)
		} else {
			flash.message = "Task not allowed"
			redirect(uri: "/admin/lab/list", absolute: true)
		}
	}
}