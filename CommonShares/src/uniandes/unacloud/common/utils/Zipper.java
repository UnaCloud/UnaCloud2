package uniandes.unacloud.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zipper {

	/**
     * Zip it
     * @param zipFile output ZIP file location
     */
    public static void zipIt(File zipFile, File folder) throws Exception {

        byte[] buffer = new byte[1024];

	    FileOutputStream fos = new FileOutputStream(zipFile);
	    ZipOutputStream zos = new ZipOutputStream(fos);
	
	    System.out.println("Output to Zip : " + zipFile);
	    FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.endsWith(".zip");
			}
		};
	
	    for(File file : folder.listFiles(filter)){
	
	        System.out.println("File Added : " + file);
	        ZipEntry ze= new ZipEntry(file.getName());
	        zos.putNextEntry(ze);
	
	        FileInputStream in = new FileInputStream(file);
	
	        int len;
	        while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
	
            in.close();
        }
	
        zos.closeEntry();
		//remember close it
	    zos.close();
	
        System.out.println("Done");
   
    }
    
    public static void unzipIt(File zipFile, String outputFolder) throws Exception {
    	byte[] buffer = new byte[1024];

       	//create output directory is not exists
       	File folder = new File(outputFolder);
       	if(!folder.exists()){
       		folder.mkdir();
       	}

       	//get the zip file content
       	ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
       	//get the zipped file list entry
       	ZipEntry ze = zis.getNextEntry();

       	while (ze != null) {

       	   String fileName = ze.getName();
           File newFile = new File(outputFolder + File.separator + fileName);

           System.out.println("file unzip : "+ newFile.getAbsoluteFile());

           //create all non exists folders
           //else you will hit FileNotFoundException for compressed folder
           new File(newFile.getParent()).mkdirs();

           FileOutputStream fos = new FileOutputStream(newFile);

           int len;
           while ((len = zis.read(buffer)) > 0) {
      		   fos.write(buffer, 0, len);
           }

           fos.close();
           ze = zis.getNextEntry();
       	}

        zis.closeEntry();
       	zis.close();

       	System.out.println("Done");
    }
}
