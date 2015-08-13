package back.services

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang.RandomStringUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import grails.transaction.Transactional
import unacloud2.ExternalCloudAccount;
import unacloud2.Repository;
import unacloud2.ServerVariable;
import unacloud2.User;
import unacloud2.VirtualMachineImage

@Transactional
/**
 * Makes public cloud services calls using AWS API.
 * @author asistente
 *
 */

class ExternalCloudCallerService {
	
	AWSCredentials credentials
	
	AmazonEC2Client computingEndpoint
	
	AmazonS3Client storageEndpoint
		
	def initializeBucket( ExternalCloudAccount account){
		String accountCredentials
		try{
			accountCredentials= "accessKey="+account.account_id+System.getProperty("line.separator")+"secretKey="+account.account_key
			InputStream is= new ByteArrayInputStream(accountCredentials.getBytes(StandardCharsets.UTF_8));
			credentials = new PropertiesCredentials(is)
			storageEndpoint = new AmazonS3Client(credentials)
		}
		catch(Exception e){
			throw new Exception("Invalid external computing account variable")
		}
		String bucketName = "unacloud-" + account.name 
		while(storageEndpoint.doesBucketExist(bucketName)){
			String charset = (('a'..'z') + ('0'..'9')).join()
			Integer length =6
			String randomString = RandomStringUtils.random(length, charset.toCharArray())
			bucketName = "unacloud-"+randomString
		}
		storageEndpoint.createBucket(new CreateBucketRequest(bucketName))
		account.putAt("bucketName", bucketName)
	}
	
	def listUserObjects(User u){
		initializeStorage()
		try {
			String accountName= ServerVariable.findByName('EXTERNAL_STORAGE_ACCOUNT').getVariable()
			def bucketName= ExternalCloudAccount.findByName(accountName).bucketName
			if (bucketName==null || bucketName.equals('')){
				initializeBucket( ExternalCloudAccount.findByName(accountName))
				bucketName= ExternalCloudAccount.findByName(accountName).bucketName
			}
			return storageEndpoint.listObjects(bucketName, u.username).getObjectSummaries()
		}catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which " +
					"means your request made it " +
					"to Amazon S3, but was rejected with an error response" +
					" for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
	
	def uploadFile(File f, User u){
		initializeStorage()		
		try {
			System.out.println("Uploading a new object to S3 from a file\n");
			String accountName= ServerVariable.findByName('EXTERNAL_STORAGE_ACCOUNT').getVariable()
			def bucketName= ExternalCloudAccount.findByName(accountName).bucketName
			if (bucketName==null || bucketName.equals('')){
				initializeBucket( ExternalCloudAccount.findByName(accountName))
				bucketName= ExternalCloudAccount.findByName(accountName).bucketName
			}
			storageEndpoint.putObject(new PutObjectRequest(
									 bucketName, u.username+'/'+f.getName(), f).withCannedAcl(CannedAccessControlList.PublicRead));
			
		}catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which " +
					"means your request made it " +
					"to Amazon S3, but was rejected with an error response" +
					" for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
	
	def deleteFile(User u, String objectKey){
		initializeStorage()
		try {
			System.out.println("Deleting object "+objectKey+" from S3\n");
			String accountName= ServerVariable.findByName('EXTERNAL_STORAGE_ACCOUNT').getVariable()
			def bucketName= ExternalCloudAccount.findByName(accountName).bucketName
			storageEndpoint.deleteObject(new DeleteObjectRequest(bucketName, objectKey));
		}catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which " +
					"means your request made it " +
					"to Amazon S3, but was rejected with an error response" +
					" for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
	
	def runInstances(String imageId, int numberOfInstances, instanceType, User u){
		initializeComputing()
		println 'computing service initialized'
		def separator =  java.io.File.separatorChar
		Repository repository= Repository.findByName("Main Repository")
		String keyPairName= "unacloud."+u.username
		println keyPairName
		List keys
		try{
		keys=computingEndpoint.describeKeyPairs().getKeyPairs()
		}
		catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which " +
					"means your request made it " +
					"to Amazon S3, but was rejected with an error response" +
					" for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		println 'describekeypairs'
		File keyFile
		try{
		for(key in keys){
			if (key.getKeyName().equals(keyPairName)){
				println 'key found: ' +key.getKeyName() 
				keyFile= new File(repository.root+"keyPairs"+separator+keyPairName+".pem")
				
				if(!(keyFile.exists())){
					computingEndpoint.deleteKeyPair(new DeleteKeyPairRequest(keyPairName))
				}
				break
			}
		}
		if(keyFile==null){
			println 'key not found'
			CreateKeyPairResult cpr = computingEndpoint.createKeyPair(new CreateKeyPairRequest(keyPairName))
			keyFile=new File(repository.root+"keyPairs"+separator+keyPairName+".pem")
			if(!(keyFile.getParentFile().exists()))
			keyFile.getParentFile().mkdirs()
			keyFile.createNewFile()
			PrintWriter writer = new PrintWriter(keyFile)
			writer.print(cpr.getKeyPair().getKeyMaterial())
			writer.close()
		}
		RunInstancesRequest runInstancesRequest= new RunInstancesRequest(imageId, numberOfInstances, numberOfInstances).withInstanceType(instanceType)
		runInstancesRequest.withKeyName(keyPairName)
		return computingEndpoint.runInstances(runInstancesRequest)
		}
		catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which " +
					"means your request made it " +
					"to Amazon S3, but was rejected with an error response" +
					" for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		
	}
	
	def terminateInstances(List<String> instanceIds){
		initializeComputing()
		TerminateInstancesRequest terminateInstancesRequest= new TerminateInstancesRequest(instanceIds)
		return computingEndpoint.terminateInstances(terminateInstancesRequest).getTerminatingInstances()
	}
	
	def describeImages(String ownerId){
		initializeComputing()
		DescribeImagesRequest describeImagesRequest= new DescribeImagesRequest()
		describeImagesRequest.withOwners(ownerId)
		return computingEndpoint.describeImages(describeImagesRequest).getImages()
	}
	
	def describeInstance(Collection<String> instanceIds){
		initializeComputing()
		if (instanceIds.isEmpty()) return new ArrayList<Instance>()
		DescribeInstancesRequest dir= new DescribeInstancesRequest().withInstanceIds(instanceIds)
		return computingEndpoint.describeInstances(dir).getReservations().get(0).getInstances()
	}
	
	def initializeComputing(){
		ExternalCloudAccount account
		String accountCredentials
		try{
			String accountName= ServerVariable.findByName('EXTERNAL_COMPUTING_ACCOUNT').getVariable()
			account= ExternalCloudAccount.findByName(accountName)
			accountCredentials= "accessKey="+account.account_id+System.getProperty("line.separator")+"secretKey="+account.account_key
			InputStream is= new ByteArrayInputStream(accountCredentials.getBytes(StandardCharsets.UTF_8));
			credentials = new PropertiesCredentials(is)
			computingEndpoint = new AmazonEC2Client(credentials)
			computingEndpoint.setEndpoint(account.provider.endpoint)
		}
		catch(Exception e){
			throw new Exception("Invalid external computing account variable")
		}
	}
	
	def initializeStorage(){
		ExternalCloudAccount account
		String accountCredentials
		try{
			String accountName= ServerVariable.findByName('EXTERNAL_STORAGE_ACCOUNT').getVariable()
			account= ExternalCloudAccount.findByName(accountName)
			accountCredentials= "accessKey="+account.account_id+System.getProperty("line.separator")+"secretKey="+account.account_key
			InputStream is= new ByteArrayInputStream(accountCredentials.getBytes(StandardCharsets.UTF_8));
			credentials = new PropertiesCredentials(is)
			storageEndpoint = new AmazonS3Client(credentials)
			storageEndpoint.setEndpoint(account.provider.endpoint)
		}
		catch(Exception e){
			throw new Exception("Invalid external computing account variable")
		}
	}
	
	
    
}
