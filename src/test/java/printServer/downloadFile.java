package printServer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class downloadFile {
	
	private static String Url="http://fortest.bondex.com.cn/CD36D384-413C-DF4D-3EE5-D8C3C03DC56D";
	
	public static void download(){
		InputStream fis=null;
		try {
			
		File file = new File(Url);
		System.out.println(file.getName());
		 // 以流的形式下载文件。
        fis = new BufferedInputStream(new FileInputStream(Url));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        
		} catch (Exception e) {
			
		}finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		download();

	}
	
	

}
