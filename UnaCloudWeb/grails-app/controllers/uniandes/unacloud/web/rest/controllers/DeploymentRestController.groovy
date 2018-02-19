package uniandes.unacloud.web.rest.controllers

import grails.converters.JSON
import grails.rest.RestfulController
import org.springframework.stereotype.Controller
import uniandes.unacloud.share.enums.DeploymentStateEnum
import uniandes.unacloud.share.enums.ExecutionStateEnum
import uniandes.unacloud.share.enums.ImageEnum
import uniandes.unacloud.web.domain.*
import uniandes.unacloud.web.domain.enums.ClusterEnum

import uniandes.unacloud.web.exceptions.HttpException

import uniandes.unacloud.web.services.DeploymentService
import uniandes.unacloud.web.services.LaboratoryService
import uniandes.unacloud.web.services.UserRestrictionService
import uniandes.unacloud.web.utils.groovy.ImageRequestOptions

import java.lang.reflect.Field

/**
 * This Controller contains actions to manage deployment services: list, deploy and stop deployments, copy instances and add instances for deployments.
 * This class render pages for user or process request in services to update entities, there is session verification before all actions.
 * This class has the plus of being a rest controller in order to communicate itself with external apps.
 * @author s.guzmanm
 *
 */
@Controller
class DeploymentRestController extends AbstractRestController {


	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------

	/**
	 * Representation of cluster service
	 */
	DeploymentService deploymentService

	//-----------------------------------------------------------------
	// Actions MVC
	//-----------------------------------------------------------------


	/**
	 * Deploy action. Checks image number and depending on it, forms the parameters in
	 * order to pass them to the service layer. It also catches exceptions and pass
	 * them to error view. If everything works it redirects to list deployment view.
	 * 
	 * Body Example
	 * {
     *   "cluster":
     *    {
     *      "id":2,
     *      "nodes":[{
     *      "id":13,
     *       "hwp":1,
     *      "quantity":1,
     *      "gHostName":"1",
     *      "type":"false"
     *    }
     *    ]
     *    },
     *    "time":1
     *
     * }
	 * 
	 * 
	 */

	def deploy() {
        def data = flash.data
        Cluster cluster = Cluster.get(data.cluster.id)
        if (cluster) {
            //Have to define if the session arrives as a token or through what sort of media
            def user = flash.user
            //validates if user is owner to deploy cluster
            if (user.userClusters.find { it.id == cluster.id } != null) {
                if (cluster.state.equals(ClusterEnum.AVAILABLE)) {

                    //Validates if images are available in the platform
                    def availables = cluster.images.findAll { it.state == ImageEnum.AVAILABLE }
                    if (availables.size() != cluster.images.size())
                        throw new HttpException(404, "Some images of this cluster are not available at this moment. Please, change cluster to deploy or images in cluster.");
                   
                        //validates if cluster is good configured
                        def requests = new ImageRequestOptions[cluster.images.size()];

                        String host;
                        cluster.images.eachWithIndex { it, idx ->
                            ImageRequestOptions requested =null;
                            for(node in data.cluster.nodes)
                            {
                                if(node.id == it.id){
                                    HardwareProfile hp = HardwareProfile.get(node.hwp);
                                    host = node.gHostName
                                    requested = new ImageRequestOptions(it, hp, node.quantity, node.gHostName, node.type == "true");
                                    break;
                                }
                            }
                            if (requested == null)
                                throw new HttpException(412,"The request does not contain all images.");
                            requests[idx] = requested;
                        }
                        try
                        {
                            deploymentService.deploy(cluster, user, data.time.toLong() * 60 * 60 * 1000, requests)
                            renderSuccess()
                        } catch (Exception e) {
                            e.printStackTrace()
                            throw e
                        }
                    }
                    else 
                        throw new HttpException(404,'The cluster is not available')
            }
            else
                throw new HttpException(401,'You don\'t have permissions to deploy this cluster ')
	    }
        else 
           throw new HttpException(404, "The cluster does not exist")
    }
	
	/**
	 * Deployment list action. Controls view all function 
	 * @return deployments that must be shown according to view all checkbox
	 */
	def list() {
        //Need to define authenticity of user through token or another sort of media
        def list = flash.user.getActiveDeployments()
        respond list
	}
	
    /**
     * Stops execution action. All nodes selected on the deployment interface with status FAILED or DEPLOYED will be
     * stopped. Redirects to index when the operation is finished.
     *
     *
     * Body example
     *
     *   "executions":[
     *  {
     *      "id":4
     *  },
     *  {
     *      "id":5
     *  }
     *  ]
     */
    def stop() {
        def user = flash.user
        List<Execution> executions = new ArrayList<>();
        def data = flash.data
        for(exec in data.executions)
        {
                Execution vm = Execution.get((exec.id) as Integer)
                if (vm != null && (vm.state.state == ExecutionStateEnum.DEPLOYED || vm.state.state == ExecutionStateEnum.FAILED)) {
                    if (vm.deployImage.deployment.user == user || user.isAdmin())
                        executions.add(vm)
                }
        }
        if (executions.size() > 0) {
            deploymentService.stopExecutions(executions,user)
            renderSuccess()
        }
        else {
            throw new HttpException(412, 'Only executions with state FAILED or DEPLOYED can be selected to be FINISHED')
        }
    }
}
