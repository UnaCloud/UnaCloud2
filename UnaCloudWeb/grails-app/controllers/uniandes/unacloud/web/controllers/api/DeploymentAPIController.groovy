package uniandes.unacloud.web.controllers.api

class DeploymentAPIController {

	
	def beforeInterceptor = {
		request.headerNames.each{
		   println it
		 }
		println request.getHeader("access-token")
	}
	
    def create() {
		response.status = 405
		response.setContentType("application/json")
		render(request.JSON)
	}
}
