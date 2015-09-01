package unacloud2

class ExternalCloudAccount {
	
	String name
	
	static belongsTo = [provider:ExternalCloudProvider]

	String account_id
	
	String account_key
	
	String bucketName
	
    static constraints = {
		name unique: true
		bucketName nullable: true
    }
}
