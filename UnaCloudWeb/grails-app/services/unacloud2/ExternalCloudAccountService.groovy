package unacloud2

import unacloud2.enums.ExternalCloudTypeEnum;
import back.services.ExternalCloudCallerService;
import grails.transaction.Transactional

@Transactional
class ExternalCloudAccountService {
	ExternalCloudCallerService externalCloudCallerService
	
    def addAccount(name, provider, account_id, account_key) {
		println "adding account"
		println provider.type
		
		if (provider.type== ExternalCloudTypeEnum.STORAGE){
			println "adding bucket to account"
			def account= new ExternalCloudAccount(name: name, provider: provider, account_id:account_id, account_key:account_key).save(failOnError: true)
			return externalCloudCallerService.initializeBucket(account)
			
		}
		else return new ExternalCloudAccount(name: name, provider: provider, account_id:account_id, account_key:account_key).save(failOnError: true)
    }
	
	def deleteAccount(ExternalCloudAccount a){
		a.delete(FailOnError: true)
	}
	
	def setValues(ExternalCloudAccount a, name, provider, account_id, account_key){
		a.putAt('name', name)
		a.putAt("provider", provider)
		a.putAt("account_id", account_id)
		a.putAt("account_key", account_key)
	}
}
