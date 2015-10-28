package unacloud

import grails.transaction.Transactional

@Transactional
class LaboratoryService {

    /**
	 * Return the list name of labs
	 */
	def getLabsNames(){
		return Laboratory.executeQuery("select name from Laboratory")
	}
}
