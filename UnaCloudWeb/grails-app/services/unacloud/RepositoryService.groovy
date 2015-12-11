package unacloud

import grails.transaction.Transactional

// Creada por Carlos E. Gomez - diciembre 11 de 2015
@Transactional
class RepositoryService {
    	
	/**
	 * Return main repository entity
	 * @return repository
	 */
	def Repository getMainRepository(){
		return Repository.findByName(Constants.MAIN_REPOSITORY)
	}
	
	
	/**
	 * Creates a new repository
	 * @param name repository name
	 * @param path repository path
	 */
	def create(name, path){
		if (Repository.findByName(name)==null) {
			new Repository(name: name, path:path).save()
		} else {
			// Error. No se como hacerlo.
		}
		
	}
	
	/**
	 * Deletes the selected OS
	 * @param os OS to be deleted
	 */
	
	def delete(Repository repo){
		if (repo != getMainRepository() ) {
			repo.delete()
		}		
	}

	
}
