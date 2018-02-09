package uniandes.unacloud.web.controllers.api

import uniandes.unacloud.web.exception.NotFoundException;
import uniandes.unacloud.web.exception.PreconditionException;

abstract class AbstractController {

	def handlePrecondition(PreconditionException e) {		
		doResponse(412, e.getMessage())
	}

	def handleNotFoundException(NotFoundException e) {
		doResponse(404, e.getMessage())
	}

	def handleException(Exception e) {
		doResponse(500, e.getMessage())
	}
	
	def doResponse(int code, String message) {
		println "Error"
		render (status: code, text: message)
	}
    
}
