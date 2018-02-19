
package uniandes.unacloud.web.rest.controllers

import grails.converters.JSON
import grails.rest.RestfulController
import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.exceptions.HttpException

/**
 * Abstract class for implementing restful controllers. It extends from RestfulController to allow further use of resources if required.
 */
abstract class AbstractRestController extends RestfulController {

    static responseFormats = ['json', 'xml']

    def beforeInterceptor = {
        //For now the user works with the first id
        flash.userKey = request.getHeader("key")
		//TODO agregar 401
        if (!request.get) {
            try {
                flash.data = request.JSON
            }            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
	
    /**
     * Method for handling http exception.
     * @param e HttpException for handling
     * @return Response in Json with code and error message
     */
    def handleHttpException(final HttpException e) {
        doResponse(e.getCode(), e.getMessage())
    }
	
    /**
     * Method for handling http exception.
     * @param e Exception for handling
     * @return Response in Json with code in 500 and error message
     */
    def handleException(final Exception e) {
        e.printStackTrace()
        doResponse(500, e.getMessage())
    }
	
    /**
     * Generates json for response with the given code and message.
     * @param code Http code
     * @param message Message for exception handling
     * @return Response data in JSON
     */
    def doResponse(int code, String message) {
        println "Error: " + code + " m: " + message

        def responseData = ["status": code, "text": message]
        response.setContentType("application/json")
        response.status = code
        render responseData as JSON
    }
	
    /**
     * Generates an empty render view for successful operations (mostly 200 responses)
     */
    def renderSuccess()
    {
        response.status = 200
        render ""
    }
	
    /**
     * Gets the user with the given key-
     * @param userKey
     * @return User with the given key
     */
    def getUserWithKey(String userKey)
    {
        println ' User access ' + userKey
        return User.findByApiKey(userKey)
    }

}