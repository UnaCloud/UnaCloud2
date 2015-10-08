package unacloud

import grails.transaction.Transactional

@Transactional
class RepositoryService {
    			
	def Repository getMainRepository(){
		return Repository.findByName(Constants.MAIN_REPOSITORY)
	}
}
