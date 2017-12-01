package uniandes.unacloud.web.controllers.api

import grails.converters.JSON
import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.exception.HttpException;
import uniandes.unacloud.web.exception.NotFoundException;
import uniandes.unacloud.web.exception.PreconditionException;
import uniandes.unacloud.web.exception.UnathorizedException

abstract class AbstractController {
	
	def beforeInterceptor = {
		//Validate token
		def key = request.getHeader("authorization")
		flash.user = User.get(1)
		
		//Validate Json format
		response.setContentType("application/json")
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
	
	def handleHttpException(HttpException e) {
		doResponse(e.getCode(), e.getMessage())
	}
	
	def handleException(Exception e) {
		doResponse(500, e.getMessage())
	}
	
	def doResponse(int code, String message) {
		println "Error: " + code + " m: " + message
		
		def responseData = ["status": code, "text": message]		
		response.setContentType("application/json")		
		render responseData as JSON
	}
    
}
