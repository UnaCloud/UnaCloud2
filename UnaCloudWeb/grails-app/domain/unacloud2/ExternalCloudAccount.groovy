package unacloud2

class ExternalCloudAccount {
	
	String name
	
	ExternalCloudProvider provider

	String account_id
	
	String account_key
	
	String bucketName
	
    static constraints = {
		name unique: true
		bucketName nullable: true
    }
}
