package unacloud

import grails.transaction.Transactional

@Transactional
class RepositoryService {
    	
	/**
	 * Return main repository entity
	 * @return repository
	 */
	def Repository getMainRepository(){
		return Repository.findByName(Constants.MAIN_REPOSITORY)
	}
}
