package uniandes.unacloud.web.rest.controllers

import grails.converters.JSON
import uniandes.unacloud.web.domain.Execution
import uniandes.unacloud.web.exceptions.HttpException

class ExecutionRestController extends AbstractRestController{

    def findByName(String name) {
        verifyCurrentUser()
        //Need to define authenticity of user through token or another sort of media
        def list=new ArrayList<Integer>();
        def executions=Execution.findAllByNameLike(name+"%")
        for(Execution e:executions)
        {
            if(e.deployedImage.deployment.userId!=flash.user.id)
                throw new HttpException(401,"The user does not possess this deployment")
            list.add(e.id)

        }
        def responseData = ["deploymentId": executions.get(0).deployedImage.deployment.id, "executions": list]
        response.setContentType("application/json")
        response.status = 200
        render responseData as JSON
    }
}
