package uniandes.unacloud.web.rest.controllers

import org.springframework.stereotype.Controller
import uniandes.unacloud.common.enums.TaskEnum
import uniandes.unacloud.share.enums.ExecutionStateEnum
import uniandes.unacloud.share.enums.ImageEnum
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum
import uniandes.unacloud.web.domain.Cluster
import uniandes.unacloud.web.domain.Execution
import uniandes.unacloud.web.domain.HardwareProfile
import uniandes.unacloud.web.domain.Laboratory
import uniandes.unacloud.web.domain.PhysicalMachine
import uniandes.unacloud.web.domain.User
import uniandes.unacloud.web.domain.enums.ClusterEnum
import uniandes.unacloud.web.exceptions.HttpException
import uniandes.unacloud.web.services.DeploymentService
import uniandes.unacloud.web.services.LaboratoryService
import uniandes.unacloud.web.utils.groovy.ImageRequestOptions

/**
 * This Controller contains actions to manage deployment services: list, deploy and stop deployments, copy instances and add instances for deployments.
 * This class render pages for user or process request in services to update entities, there is session verification before all actions.
 * This class has the plus of being a rest controller in order to communicate itself with external apps.
 * @author s.guzmanm
 *
 */
@Controller
class LaboratoryRestController extends AbstractRestController {


	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------

	/**
	 * Representation of cluster service
	 */
	LaboratoryService laboratoryService

	//-----------------------------------------------------------------
	// Actions MVC
	//-----------------------------------------------------------------


    /**
     * Stops, Updates agent or Clears Cache in selected machines. Returns to lab when finishes
     */
    def updateMachines(){

        def data=flash.data
        def lab = Laboratory.get(data.id)
        if (lab && TaskEnum.getEnum(data.process) != null) {
            def hostList = []
            for(machine in data.machines)
            {
                if(machine.value=="on")
                {
                    PhysicalMachine pm = PhysicalMachine.get(machine.id)
                    if (pm.state == PhysicalMachineStateEnum.ON) {
                        hostList.add(pm)
                    }
                }
            }
            if (hostList.size() > 0) {
                    def user = getUserWithKey(flash.userKey)
                    laboratoryService.createRequestTasktoMachines(hostList, TaskEnum.getEnum(data.process), user)
                    renderSuccess()

            } else {
                throw new HttpException(412, "At least one host machine with state ON must be selected.")
            }
            return
        } else
            throw new HttpException(412,"There are no physical machines selected to be updated")
    }
}
