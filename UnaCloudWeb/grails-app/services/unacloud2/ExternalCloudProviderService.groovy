package unacloud2

import unacloud2.enums.ExternalCloudTypeEnum;
import grails.transaction.Transactional

@Transactional
class ExternalCloudProviderService {
	
	
    def addProvider(String name, String endpoint, String type) {
		ExternalCloudTypeEnum typeEnum = ExternalCloudTypeEnum.valueOf(type)
		return new ExternalCloudProvider(name: name, endpoint: endpoint, type: typeEnum).save(failOnError:true)
    }
	
	/**
	 * Deletes the selected Provider
	 * @param p Provider to be deleted
	 */
	
	def deleteProvider(ExternalCloudProvider p){
		p.delete()
		
	}
	
	/**
	 * Edits the given OS
	 * @param os OS to be edited
	 * @param name OS new name
	 * @param configurer OS new configurer class
	 */
	
	def setValues(ExternalCloudProvider p, name, endpoint, String type) {
		ExternalCloudTypeEnum typeEnum = ExternalCloudTypeEnum.valueOf(type)
		p.putAt("name", name)
		p.putAt("endpoint", endpoint)
		p.putAt("type", typeEnum)
	}
	
}
