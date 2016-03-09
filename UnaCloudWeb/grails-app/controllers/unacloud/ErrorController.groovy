package unacloud

/**
 * Controller to render error page message
 * @author CesarF
 *
 */
class ErrorController {

	/**
	 * In case of 500 error server
	 * @return
	 */
    def fivehundred() { 		
		render(view: "error", model: [error:'500',message:'System error', description:'please contact your administrator'])
	}
	
	/**
	 * In case of 400 error server
	 * @return
	 */
	def fourhundred() {		
		render(view: "error", model: [error:'400',message:'Bad request', description:'We could not find the page you were looking for'])
	}
	
	/**
	 * In case of unspecified error
	 * @return
	 */
	def error() {
		render(view: "error", model: [error:'505',message:'Unknown system error', description:'please contact your administrator'])		
	}
}
