package com.bondex.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.expression.ParseException;
import org.springframework.web.multipart.MultipartFile;

public class HttpClientUtil {

	//@SuppressWarnings("resource")
	public static String doPost(String url, String jsonstr, String charset) {

		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try { 
			httpClient = new SSLClient(); 
			httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
			StringEntity se = new StringEntity(jsonstr, Charset.forName("UTF-8"));
			se.setContentType("application/json;charset=UTF-8");
			se.setContentEncoding(new BasicHeader("Content-Type", "application/json;charset=UTF-8"));
			httpPost.setEntity(se);
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	
	/**
	 * 上传附件
	 * @param uploadAndStoreFileUrl
	 * @param file
	 * @param readparams
	 * @return
	 */
	public static String uploadAndStoreFile(String uploadAndStoreFileUrl,MultipartFile file,byte[] params) {
		String originalFilename = file.getOriginalFilename();
		// 创建默认的httpClient实例
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// 创建httppost 
		HttpPost uploadFileHttPost = new HttpPost(uploadAndStoreFileUrl);
		//设置超时时间
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(180 * 1000).setConnectionRequestTimeout(180 * 1000).setSocketTimeout(180 * 1000).setRedirectsEnabled(true).build();
		uploadFileHttPost.setConfig(requestConfig);
		
		//　setMode(HttpMultipartMode mode)，其中mode主要有BROWSER_COMPATIBLE，RFC6532，STRICT三种，默认值是STRICT。
		MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532); //支持中文了。需要改为使用RFC6532。
		try {
			//以二进制的形式添加数据，可以添加File、InputStream、byte[]类型的数据。
			builder.addBinaryBody(originalFilename, file.getBytes(), ContentType.APPLICATION_OCTET_STREAM,	originalFilename);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		builder.addBinaryBody("readparam", params);
		HttpEntity multipart = builder.build();
		
		//设置请求参数  
		uploadFileHttPost.setEntity(multipart);
		CloseableHttpResponse response = null;
		try {
			// 发起请求 并返回请求的响应  
			response = httpClient.execute(uploadFileHttPost);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity responseEntity = response.getEntity();
		String sResponse = null;
		try {
			sResponse = EntityUtils.toString(responseEntity, Consts.UTF_8);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return sResponse;
	}
	
	/**
	 * 上传附件
	 * @param uploadAndStoreFileUrl
	 * @param file
	 * @param readparams
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String uploadAndStoreFile2(String uploadAndStoreFileUrl,MultipartFile multipartFile) {
		// 创建默认的httpClient实例
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// 创建httppost 
		HttpPost uploadFileHttPost = new HttpPost(uploadAndStoreFileUrl+"?bucket=fortest");
		
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		
//		uploadFileHttPost.addHeader("Host", "api.bondex.com.cn:12360");
//		uploadFileHttPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
//		uploadFileHttPost.addHeader("Accept-Encoding", "gzip, deflate, br");
//		uploadFileHttPost.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
//		uploadFileHttPost.addHeader("Connection", "keep-alive");
		uploadFileHttPost.addHeader("Content-Type", "multipart/form-data;boundary=--------------------"+BOUNDARY);
//		uploadFileHttPost.addHeader("Content-Disposition", "form-data;name=\"bucket\"");
//		uploadFileHttPost.addHeader("Content-Disposition", "form-data;name=\"files\";filename=\""+ originalFilename + "\"\r\n");
		//设置超时时间
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(180 * 1000).setConnectionRequestTimeout(180 * 1000).setSocketTimeout(180 * 1000).setRedirectsEnabled(true).build();
		uploadFileHttPost.setConfig(requestConfig);
		
		//　setMode(HttpMultipartMode mode)，其中mode主要有BROWSER_COMPATIBLE，RFC6532，STRICT三种，默认值是STRICT。
		MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532).setCharset(Consts.UTF_8).setContentType(ContentType.MULTIPART_FORM_DATA).setBoundary("--------------------"+BOUNDARY); //支持中文了。需要改为使用RFC6532。
		//文件传入 方法一
//		builder.addPart("files", new FileBody(inputStreamToFile(multipartFile), file.getContentType(),Charset.defaultCharset().toString()));
		//文件传入 方法二
		try {
			String originalFilename = multipartFile.getOriginalFilename();
			builder.addBinaryBody("files", multipartFile.getBytes(), ContentType.APPLICATION_OCTET_STREAM,	originalFilename);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		HttpEntity multipart = builder.build();
		
		//设置请求参数  
		uploadFileHttPost.setEntity(multipart);
		String sResponse = null;
		try {
			// 发起请求 并返回请求的响应  
			HttpResponse response = httpClient.execute(uploadFileHttPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK==statusCode){
				System.out.println("请求响应成功！");
				HttpEntity responseEntity = response.getEntity();
				sResponse = EntityUtils.toString(responseEntity, Consts.UTF_8);
			}else{
				System.out.println("请求响应失败！");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sResponse;
	}
	
	// 以流的形式下载文件。
	public static void downLoadFile (String URL,String fileName,ZipOutputStream zipOutputStream){
		if(StringUtils.isBlank(URL)){
			return ;
		}
		BufferedInputStream ins=null;
		try {
        URL url = new URL(URL);
        URLConnection conn = url.openConnection();
        long length = conn.getContentLength();
        if (length <= 0) {
           throw new FileNotFoundException("下载文件不存在！");
        }
        
        ins = new BufferedInputStream(conn.getInputStream());
        ZipEntry entry = new ZipEntry( fileName + ".repx");
        zipOutputStream.putNextEntry(entry);
        //byte[] buffer =  new byte[ins.available()]; //从输入流中读取不受阻塞
        //ins.read(buffer); //读取数据
        //zipOutputStream.write(buffer); //输出数据文件
        
        //写入文件的方法，同上                  
        int size = 0;  
        byte[] buffer = new byte[4096];  //设置读取数据缓存大小
        while ((size = ins.read(buffer)) > 0) {  
        	zipOutputStream.write(buffer, 0, size);  
        }  
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(null!=ins){
					ins.close(); //关闭读取流
				}
				zipOutputStream.closeEntry();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	
	
	/**
	 * multifile 转 file
	 * @param multifile
	 * @return
	 */
	 public static  File  inputStreamToFile(MultipartFile multifile) {
		File f = null;
		try {
			if (!multifile.equals("") && multifile.getSize() > 0) {
				InputStream ins = multifile.getInputStream();
				f = new File(multifile.getOriginalFilename());
				OutputStream os = new FileOutputStream(f);
				int bytesRead = 0;
				byte[] buffer = new byte[8192];
				while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				os.close();
				ins.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return f;
	 }
	 
	 /**
	  * Object 转 byte[]
	  * @param object
	  * @return
	  * @throws IOException
	  */
	 public static byte[] objectToBytes(final Serializable object) throws IOException {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ObjectOutputStream oos  =  null;
	        try {
	            oos = new ObjectOutputStream(baos);
	            oos.writeObject(object);
	            oos.flush();
	            return baos.toByteArray();
	        } finally {
	            if (oos != null)  {
	                oos.close();
	            }
	            if (baos != null) {
	                baos.close();
	            }
	        }
	    }
	 
	 public static SequenceInputStream getMergeInputStream(MultipartFile[] multipartFiles) throws IOException{
		 Vector<InputStream> vector = new Vector<>();
		 
		 for (MultipartFile multipartFile : multipartFiles) {
			 vector.add(multipartFile.getInputStream());
		}
		 
		 //获取迭代器
         Enumeration<InputStream> elements = vector.elements();
         // 创建合并流
         SequenceInputStream sis = new SequenceInputStream(elements);
		 
         return sis;
	 }
	 
}
