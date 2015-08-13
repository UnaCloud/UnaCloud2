package webutils

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.*;

public class AWSRequestException extends WebApplicationException{
	
		private static final long serialVersionUID = 1L;
		
		
		/**
		 * Create a HTTP 400 exception.
		 * @param message the String that is the entity of the 404 response.
		 */
		public AWSRequestException(String message) {
			super(Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).entity(message).type("application/xml").build());
		}
		
	
}
