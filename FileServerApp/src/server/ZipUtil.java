package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	public static File zip(File file) throws IOException {
    	String name = getFileWithoutExtension(file.getName());
    	
    	File zipFile = new File(name + ".zip");
    	
    	ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
    	ZipEntry zipEntry = new ZipEntry(file.getName());
    	zipOut.putNextEntry(zipEntry);
    	
    	byte[] buffer = new byte[(int) file.length()];
    	FileInputStream in = new FileInputStream(file);
    	in.read(buffer, 0, buffer.length);
    	in.close();
    	
    	zipOut.write(buffer, 0, buffer.length);
    	zipOut.flush();
    	zipOut.closeEntry();
    	zipOut.finish();
    	zipOut.close();
    	
    	return zipFile;
    }
    
    public static byte[] getBytesOfZip(File file) throws IOException {
    	byte[] buffer = new byte[(int) file.length()];
    	int offset = -1;
    	
    	BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	while ((offset = in.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, offset);
        }
    	
    	in.close();
    	out.flush();
    	buffer = out.toByteArray();
    	out.close();
    	
    	return buffer;
    }
    
    public static String getFileWithoutExtension(String fileName) {
    	String name = null;
    	int index = fileName.lastIndexOf('.');
    	if(index > 0) {
    		name = fileName.substring(0, index);
    	}
    	return name;
    }
    
    public static void saveFile(String folderPath, String fileName, byte[] data, boolean isZip) throws IOException {
    	if(!isZip) {
			File file = new File(folderPath + File.separator + fileName);
			FileOutputStream out = new FileOutputStream(file);
			out.write(data, 0, data.length);
			out.flush();
			out.close();
		} else {
			ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(data));
			ZipEntry entry = in.getNextEntry();
			
			FileOutputStream out = new FileOutputStream(folderPath + File.separator + entry.getName());
			byte[] byteBuff = new byte[4096];
		    int bytesRead = 0;
		    while ((bytesRead = in.read(byteBuff)) != -1)
		    {
		        out.write(byteBuff, 0, bytesRead);
		    }

		    out.close();
			in.closeEntry();
			in.close();
			out.close();
		}
    }
}
