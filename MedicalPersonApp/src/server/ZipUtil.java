package server;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {

	public static void unzip(String folderPath, byte[] data) throws IOException {
		ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(data));
		ZipEntry entry = in.getNextEntry();
		
		FileOutputStream out = new FileOutputStream(folderPath);
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
