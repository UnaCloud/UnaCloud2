package uniandes.unacloud.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import unacloud.share.enums.VirtualMachineImageEnum;
import uniandes.unacloud.db.entities.UserEntity;
import uniandes.unacloud.db.entities.VirtualImageFileEntity;
import unacloud.share.db.RepositoryManager;

/**
 * Generic class used to create queries and updates for VirtualMachineImage entity
 * Added repository queries
 * Added Hypervisor queries
 * @author Cesar
 *
 */
public class VirtualMachineImageManager {
	
	/**
	 * Method used to return a virtual machine image entity with information about file and repository
	 * @return
	 */
	//TODO improve query to repository, use hash map
	public static VirtualImageFileEntity getVirtualImageWithFile(Long id, VirtualMachineImageEnum state, boolean withUser, boolean withConfigurer, Connection con){
		try {
			String query = null;
			if(withConfigurer) query = "SELECT vm.id, vm.fixed_disk_size, vm.is_public, vm.main_file, vm.repository_id, vm.token, vm.user, vm.password, vm.name, os.configurer"+(withUser?",vm.owner_id":"")+" FROM virtual_machine_image vm INNER JOIN operating_system os ON vm.operating_system_id = os.id WHERE vm.state = ? and vm.id = ?;";
			else query ="SELECT vm.id, vm.fixed_disk_size, vm.is_public, vm.main_file, vm.repository_id, vm.token, vm.user, vm.password, vm.name"+(withUser?",vm.owner_id":"")+" FROM virtual_machine_image vm WHERE vm.state = ? and vm.id = ?;";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, state.name());
			ps.setLong(2, id);
			ResultSet rs = ps.executeQuery();
			VirtualImageFileEntity image = null;
			if(rs.next()){
				image = new VirtualImageFileEntity(rs.getLong(1), state, rs.getString(6), RepositoryManager.getRepository(rs.getLong(5),con), rs.getBoolean(3), rs.getLong(2), rs.getString(4), rs.getString(9), withConfigurer?rs.getString(10):null);
				image.setName(rs.getString(7));
				image.setName(rs.getString(8));
				if(withUser)image.setOwner(new UserEntity(withConfigurer?rs.getLong(11):rs.getLong(10),null,null));
			}
			try{rs.close();ps.close();}catch(Exception e){}
			return image;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	/**
	 * Method used to return a virtual machine image entity based in a token 
	 * @return
	 */
	public static VirtualImageFileEntity getVirtualImageWithFile(String token,Connection con){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT vm.id, vm.fixed_disk_size, vm.is_public, vm.main_file, vm.repository_id, vm.state, vm.name, vm.owner_id FROM virtual_machine_image vm WHERE vm.token = ? ;");
			ps.setString(1,token);
			ResultSet rs = ps.executeQuery();	
			VirtualImageFileEntity image = null;
			if(rs.next()){
				image = new VirtualImageFileEntity(rs.getLong(1), VirtualMachineImageEnum.getEnum(rs.getString(6)), token,RepositoryManager.getRepository(rs.getLong(5),con), rs.getBoolean(3), rs.getLong(2), rs.getString(4), rs.getString(7),null);
				image.setOwner(new UserEntity(rs.getLong(8),null,null));
			}			 
			try{rs.close();ps.close();}catch(Exception e){}
			return image;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	
	/**
	 * Updates a virtual machine image entity on database.
	 * @param machine
	 * @return
	 */
	public static boolean setVirtualMachineFile(VirtualImageFileEntity image, boolean update,Connection con){
		if(image.getId()==null||image.getId()<1)return false;
		try {
			String query = "update virtual_machine_image vm set";
			int isPublic = 0;
			int repository = 0;
			int mainFile = 0;
			int status = 0;
			int token = 0;
			if(image.isPublic()!=null){query+=" vm.is_public = ? ";isPublic = 1;}
			if(image.getRepository()!=null){query+=(isPublic>0?",":"")+" vm.repository_id = ? ";repository = isPublic+1;}
			if(image.getMainFile()!=null){query+=(isPublic>0||repository>0?",":"")+" vm.main_file = ? ";mainFile = repository>0?repository+1:isPublic+1;}	
			if(image.getState()!=null){query+=(isPublic>0||repository>0||mainFile>0?",":"")+" vm.state = ? ";status = mainFile>0?mainFile+1:repository>0?repository+1:isPublic+1;}
			if(image.getToken()!=null){query+=(isPublic>0||repository>0||mainFile>0||token>0?",":"")+" vm.token = ? ";token = status>0?status+1:mainFile>0?mainFile+1:repository>0?repository+1:isPublic+1;}
			if(isPublic>0||repository>0||mainFile>0||status>0||token>0){
				if(update)query+= ", vm.image_version = vm.image_version + 1 ";
				if(image.getFixDisk()!=null&&image.getFixDisk()>0)query+=", vm.fixed_disk_size = "+image.getFixDisk()+" ";
				query += "where vm.id = ? and vm.id > 0;";
				System.out.println(query);
				PreparedStatement ps = con.prepareStatement(query);
				int id = 1;
				if(isPublic>0){ps.setBoolean(isPublic, image.isPublic());id++;}
				if(repository>0){ps.setLong(repository, image.getRepository().getId());id++;}
				if(mainFile>0){ps.setString(mainFile, image.getMainFile());id++;}
				if(status>0){ps.setString(status, image.getState().name());id++;}
				if(token>0){ps.setString(token, image.getToken());id++;}
				ps.setLong(id, image.getId());
				System.out.println("Change "+ps.executeUpdate()+" lines");
				try{ps.close();}catch(Exception e){}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return false;
	}
	
	/**
	 * Return a list of virtual machines owned by user
	 * @param userId
	 * @return
	 */
	//TODO improve query to repository, use hash map
	public static List<VirtualImageFileEntity> getAllVirtualMachinesByUser(Long userId,Connection con){
		try {
			List<VirtualImageFileEntity> list = new ArrayList<VirtualImageFileEntity>();	
			String query = "SELECT vm.id, vm.state, vm.token, vm.repository_id, vm.is_public, vm.fixed_disk_size, vm.main_file, vm.name FROM virtual_machine_image vm WHERE vm.owner_id = ?;";
			PreparedStatement ps = con.prepareStatement(query);			
			ps.setLong(1, userId);
			ResultSet rs = ps.executeQuery();		
			while(rs.next())list.add(new VirtualImageFileEntity(rs.getLong(1), VirtualMachineImageEnum.getEnum(rs.getString(2)), rs.getString(3), RepositoryManager.getRepository(rs.getLong(4),con), rs.getBoolean(5), rs.getLong(6), rs.getString(7), rs.getString(8),null));
			try{rs.close();ps.close();}catch(Exception e){}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
