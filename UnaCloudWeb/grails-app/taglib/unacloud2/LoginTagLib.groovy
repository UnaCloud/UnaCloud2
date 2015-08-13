package unacloud2

import java.lang.ProcessBuilder.Redirect;

class LoginTagLib {
	def validateSession = { attrs, body ->
		if(session.user) 
			out<<body()	
		else{
		flash.message="You must log in first!"
		out<<"${response.sendRedirect(createLink(uri:"/", absolute:true))}"
	
		}
	}
}
