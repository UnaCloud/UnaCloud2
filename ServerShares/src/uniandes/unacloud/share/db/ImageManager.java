package uniandes.unacloud.share.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import uniandes.unacloud.share.db.entities.ImageEntity;
import uniandes.unacloud.share.enums.ImageEnum;

/**
 * Class used to execute query, update and delete processes in database for Image Entity. 
 * @author CesarF
 *
 */
public class ImageManager {
	
	/**
	 * Returns an Image entity requested by id and state
	 * @param id physical machine 
	 * @param state of physical machine
	 * @param con Database Connection
	 * @return image, could return null
	 */
	public static ImageEntity getImage(Long id, ImageEnum state, Connection con) {
		try {
			PreparedStatement ps = con.prepareStatement("SELECT vm.id, vm.user, vm.password, vm.token FROM image vm WHERE vm.state = ? AND vm.id = ?;");
			ps.setString(1, state.name());
			ps.setLong(2, id);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();		
			ImageEntity image = null;
			if (rs.next())
				image = new ImageEntity(rs.getLong(1), rs.getString(2), rs.getString(3), state, rs.getString(4));
			try {
				rs.close();
				ps.close();
			} catch (Exception e) {
				
			}
			return image;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * Updates an image entity on database.
	 * @param image to be modified
	 * @param con Database Connection
	 * @return true if image was updated, false in case not
	 */
	public static boolean setImage(ImageEntity image, Connection con) {
		if (image.getId() == null || image.getId() < 1)
			return false;
		try {
			String query = "UPDATE image vm SET vm.state = ? WHERE vm.id = ? AND vm.id > 0;";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, image.getState().name());
			ps.setLong(2, image.getId());
			System.out.println(ps.toString());
			System.out.println("Change " + ps.executeUpdate() + " lines");
			try {
				ps.close();
			} catch (Exception e) {
				
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return false;
	}
	
	/**
	 * Deletes an image based in state and id
	 * @param image to be deleted
	 * @param con Database Connection
	 * @return true in case entity was deleted, false in case not
	 * TODO
	 */
	public static boolean deleteImage(ImageEntity image, Connection con) {
		if (image.getId() == null || image.getId() < 1)
			return false;
		try {
			String query = "DELETE FROM image WHERE state = ? AND id = ? AND id > 0;";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, image.getState().name());
			ps.setLong(2, image.getId());
			System.out.println(ps.toString());
			System.out.println("Delete " + ps.executeUpdate() + " lines");
			try {
				ps.close();
			} catch (Exception e) {
				
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
