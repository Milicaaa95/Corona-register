package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
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
}
