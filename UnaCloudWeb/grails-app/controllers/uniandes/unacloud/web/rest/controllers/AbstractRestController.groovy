
package uniandes.unacloud.web.rest.controllers

import grails.converters.JSON
import grails.rest.RestfulController
import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.exceptions.HttpException


abstract class AbstractRestController{

    static responseFormats = ['json', 'xml']


    def beforeInterceptor = {
        //Validate token
        def key = request.getHeader("authorization")
        flash.user = User.get(1)

        if (!request.get) {
            try {
                flash.data = request.JSON
            }
            catch(Exception e) {
                e.printStackTrace();
                response.sendError(400);
            }
        }
    }

    def handleHttpException(final HttpException e) {
        doResponse(e.getCode(), e.getMessage())
    }

    def handleException(final Exception e) {
        doResponse(500, e.getMessage())
    }

    def doResponse(int code, String message) {
        println "Error: " + code + " m: " + message

        def responseData = ["status": code, "text": message]
        response.setContentType("application/json")
        render responseData as JSON
    }

}