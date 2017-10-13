package uniandes.unacloud.web.controllers.api

class DeploymentAPIController {

    def create() {
		response.status = 405
		response.setContentType("application/json")
		render(request.JSON)
	}
}
