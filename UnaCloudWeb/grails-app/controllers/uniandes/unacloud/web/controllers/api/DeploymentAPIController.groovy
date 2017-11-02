package uniandes.unacloud.web.controllers.api

import javassist.bytecode.stackmap.BasicBlock.Catch;

import org.codehaus.groovy.grails.web.json.JSONObject;

import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.web.domain.Cluster
import uniandes.unacloud.web.domain.enums.ClusterEnum;
import uniandes.unacloud.web.exception.NotFoundException

class DeploymentAPIController extends AbstractController{

	
	def beforeInterceptor = {
		//Validate token
		println request.getHeader("authorization")
		//Validate Json format
		if (!request.get) {
			try {
				flash.data = request.JSON
			}
			catch(Exception e) {
				e.printStackTrace();
				render status: 400
			}
		}			
	}
	
    def create() {
		def data = flash.data
		data.nodes[0].name
		response.status = 200
		response.setContentType("application/json")
		throw new NotFoundException("Hola");
		render(request.JSON)
	}
	
	def list() {
		response.status = 200
		response.setContentType("application/json")		
		render(request.JSON)
	}
}
