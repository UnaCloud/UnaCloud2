package unacloud

import com.losandes.utils.Constants;
import com.losandes.utils.UnaCloudConstants;

import grails.transaction.Transactional

// Creada por Carlos E. Gomez - diciembre 11 de 2015
/**
 * 
 * @author Carlos
 * Refactor by Cesar Forero 
 *
 */
@Transactional
class RepositoryService {
	
	/**
	 * Returns a repository entity queried by name
	 * @param name
	 * @return
	 */
	def Repository getRepositoryByName(String name){
		return Repository.findByName(name)
	}
    	
	/**
	 * Returns main repository entity
	 * @return repository
	 */
	def Repository getMainRepository(){
		return getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY)
	}	
	
	/**
	 * Creates a new repository
	 * @param name repository name
	 * @param path repository path
	 */
	def create(name, path){
		if (Repository.findByName(name)==null) {
			new Repository(name: name, path:path).save()
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
