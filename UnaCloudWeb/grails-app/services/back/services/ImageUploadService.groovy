package back.services

import com.losandes.utils.RandomUtils;

import grails.util.Environment;
import communication.messages.InvalidOperationResponse;
import communication.messages.vmo.VirtualMachineSaveImageMessage
import communication.messages.vmo.VirtualMachineSaveImageResponse;
import communication.messages.vmo.VirtualMachineStartResponse.VirtualMachineState;
import unacloud2.DeployedImage;
import unacloud2.Repository;
import unacloud2.User
import unacloud2.VirtualMachineExecution;
import unacloud2.VirtualMachineImage;
import unacloud2.VirtualMachineImageEnum;
import unacloud2.VirtualMachineImageService;
import unacloudEnums.VirtualMachineExecutionStateEnum;

class ImageUploadService {
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	
	/**
	 * Representation of Variable Manager Service
	 */	
	VariableManagerService variableManagerService
	
	/**
	 * Representation of Virtual Machine Image Service
	 */
	VirtualMachineImageService virtualMachineImageService
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
   
	/**
	 * Sends a message to physical machine agent asking it to send an image file belongs to a current execution. 
	 * Validates if should creates or replaces old image, adds a token to image and sends it in message.
	 * @param vm
	 * @param image
	 * @param virtualMachineId
	 * @param imageName
	 * @param user
	 * @return
	 */
	def saveImage(VirtualMachineExecution vm, DeployedImage image, long virtualMachineId,String imageName, User user){
		print "Empieza servicio de copia"		
		VirtualMachineSaveImageMessage vmsim = new VirtualMachineSaveImageMessage();
		if(vm.status== VirtualMachineExecutionStateEnum.DEPLOYED){
			println vm.name+" "+vm.message
			if(!Environment.isDevelopmentMode()){
				try{
					String token = RandomUtils.generateRandomString(32);
					VirtualMachineImage otherVm = VirtualMachineImage.findByName(imageName);
					def owner = null
					if(otherVm){
						def query = User.where{images{otherVm} && id == user.id};
						owner= query.find([max:1]);
						if(owner){
							println "Reemplazar imagen"
							otherVm.token=token;
							otherVm.state=VirtualMachineImageEnum.COPYING;
							otherVm.save();						
					    }
					}					
					if(!owner){
					    println "nueva imagen"
					    def repository= Repository.findByName("Main Repository")
						java.io.File newFile= new java.io.File(repository.root+imageName+"_"+user.username)
						newFile.mkdirs()						
						def newImage = new VirtualMachineImage( fixedDiskSize:  image.image.fixedDiskSize, name: imageName , available: false,
								isPublic: false, imageVersion: 0,accessProtocol: image.image.accessProtocol , operatingSystem: image.image.operatingSystem,
								user: image.image.user, password: image.image.password, mainFile: repository.root+imageName+"_"+user.username+java.io.File.separatorChar+"temp")	            	  
						newImage.setToken(token);
						newImage.setState(VirtualMachineImageEnum.COPYING);
					    newImage.save(failOnError: true)						
						user.images.add(newImage);						
						user.save();
						if(repository.images==null)repository.images
						
						repository.images.add(newImage)
						repository.save()
					}
					vmsim.setTokenCom(token);
					vmsim.setImageId(image.image.id);
					vmsim.setVirtualMachineExecutionId(virtualMachineId);
					String pmIp=vm.executionNode.ip.ip;
					try{
						/*
						 * Sends the message to the physical machine agent
						 * where the virtual machine was allocated
						 */
						println "Abriendo socket a "+pmIp+" "+variableManagerService.getIntValue("CLOUDER_CLIENT_PORT");
						Socket s=new Socket(pmIp,variableManagerService.getIntValue("CLOUDER_CLIENT_PORT"));
						s.setSoTimeout(15000);
						ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
						oos.writeObject(vmsim);
						oos.flush();
						ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
						Object c=ois.readObject();
						if(c instanceof VirtualMachineSaveImageResponse && ((VirtualMachineSaveImageResponse)c).state==VirtualMachineState.COPYNG){
							println "Respuesta afirmativa..."
							vm.setStatus(VirtualMachineExecutionStateEnum.COPYING);
							vm.setMessage("Copying to Server");
							vm.save();						
						}else{
							VirtualMachineImage vi = virtualMachineImageService.getImage(vmsim.getImageId())
							if(vi!=null){
								if(vi.getMainFile().endsWith("temp")){
									String mf = vi.getMainFile();
									virtualMachineImageService.deleteImage(vi.id);
									new java.io.File(mf).getParentFile().delete();
								}else{
									virtualMachineImageService.setPath(vi.getToken(),vi.getMainFile());
									virtualMachineImageService.changeImageState(vi.id, VirtualMachineImageEnum.AVAILABLE)
								}
							}
							vm.setStatus(VirtualMachineExecutionStateEnum.FAILED);
							vm.setMessage(((InvalidOperationResponse )c).getMessage());							
							vm.save();
						}
						oos.close();
						s.close();
					}catch(Exception e){
					    VirtualMachineImage vi = virtualMachineImageService.getImage(vmsim.getImageId())
						if(vi!=null){
							if(vi.getMainFile().endsWith("temp")){
								String mf = vi.getMainFile();
								virtualMachineImageService.deleteImage(vi.id);
								new java.io.File(mf).getParentFile().delete();
							}else{
								virtualMachineImageService.setPath(vi.getToken(),vi.getMainFile());
								virtualMachineImageService.changeImageState(vi.id, VirtualMachineImageEnum.AVAILABLE)
							}
						}
						vm.setStatus(VirtualMachineExecutionStateEnum.FAILED)
						vm.setMessage("Connection error")
						vm.save();
						println e.getMessage()+" "+pmIp;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}
