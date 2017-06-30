package uniandes.unacloud.web.utils.groovy

/**
 * Class to manager all utilities methods for web project in groovy
 * @author CesarF
 *
 */
class Utils {
	/**
	 * Method to validate if an string is not null and it is not empty
	 * @param text
	 * @return
	 */
	def validate(text){
		if (text || text.isEmpty()) 
			return false
		else 
			return true
	}
}
