package uniandes.unacloud.web.rest.controllers

import grails.converters.JSON
import grails.rest.RestfulController
import org.springframework.stereotype.Controller

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
class DeploymentRestController extends RestfulController {


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
     * Method for showing error testing in REST a way is found to instantiate images in IntelliJIDEA of Sergio´s Mac
     */
    def errorTest()
    {
        Field[] fs = Deployment.class.getDeclaredFields();
        for(Field f : fs) {
            println f.getName()
        }
        flash.data=request.JSON
        Example e =(Example)flash.data.valor
        println e.nombre+" "+e.valor

        println flash.data.valor

        println "It ought to be deploying"
        //Testing for e
        switch (request.getHeader('exception'))
        {
            case '500': throw new HttpException(500,"MSJ")
            case '404': throw new HttpException(404,"MSJ")
            case '412':throw new HttpException(412,"MSJ")
            case '401': throw new HttpException(401,"MSJ")
        }
        println "Nada"
        throw new Exception("MSJ")
    }


	/**
	 * Deploy action. Checks image number and depending on it, forms the parameters in
	 * order to pass them to the service layer. It also catches exceptions and pass
	 * them to error view. If everything works it redirects to list deployment view.
	 */

	def deploy() {
        /*def data=flash.data
        Cluster cluster = Cluster.get(data.id)
        if (cluster) {
            //Have to define if the session arrives as a token or through what sort of media
            def user = User.get(data.user.id)
            //validates if user is owner to deploy cluster
            if (user.userClusters.find { it.id == cluster.id } != null) {
                if (cluster.state.equals(ClusterEnum.AVAILABLE)) {

                    //Validates if images are available in the platform
                    def availables = cluster.images.findAll { it.state == ImageEnum.AVAILABLE }
                    if (availables.size() != cluster.images.size()) {
                        throw new HttpException(404,"Some images of this cluster are not available at this moment. Please, change cluster to deploy or images in cluster.");
                    }
                    try {
                        //validates if cluster is good configured
                        def requests = new ImageRequestOptions[cluster.images.size()];
                        cluster.images.eachWithIndex { it, idx ->

                            ImageRequestOptions requested =null;
                            for(node in data.cluster.nodes)
                                if(node.id == it.id){
                                    HardwareProfile hp = HardwareProfile.findByName(node.hwp);
                                    requested = new ImageRequestOptions(it, hp, node.quantity, node.gHostName, node.type==="true");
                                    break;
                                }
                            if (requested == null)
                                throw new HttpException(412,"The request does not contain all images.");
                            requests[idx] = requested;
                        }
                        deploymentService.deploy(cluster, user, data.time * 60 * 60 * 1000, requests)
                        return

                        } catch (Exception e) {
                            e.printStackTrace()
                            throw new HttpException (412,"The request does not contain all images ",e.getCause())
                        }
                    }
                    else {
                        throw new HttpException(404,'The cluster is not available')
                    }
            }
            else
            {
                throw new HttpException(401,'You don\'t have permissions to deploy this cluster ')
            }
	    }*/
    }
	
	
	/**
	 * Deployment list action. Controls view all function 
	 * @return deployments that must be shown according to view all checkbox
	 */
	def list() {

        //Need to define authenticity of user through token or another sort of media
		/*def user = User.get(flash.data.user.id)
		if (!user.isAdmin())
			[myDeployments: user.getActiveDeployments()]		
		else {	
			def deployments = deploymentService.getActiveDeployments(user)	
			[myDeployments: user.getActiveDeployments(), deployments: deployments]
		}*/
	}



	/** Log exception */
    private void logException(final Exception exception) {
        println "ERROE-----"
        println "Exception occurred. ${exception?.class}"
    }

}
