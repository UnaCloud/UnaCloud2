package unacloud

/**
 * Controller to render error page message
 * @author Cesar
 *
 */
class ErrorController {

    def fivehundred() { 		
		render(view: "error", model: [error:'500',message:'System error', description:'please contact your administrator'])
	}
	
	def fourhundred() {		
		render(view: "error", model: [error:'400',message:'Bad request', description:'We could not find the page you were looking for'])
	}
	
	def error() {
		render(view: "error", model: [error:'505',message:'Unknown system error', description:'please contact your administrator'])		
	}
}
