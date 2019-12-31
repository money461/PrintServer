package com.bondex.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


public class ReadTxtFile {

	/**
     * org.slf4j.Logger
     */
	private  static final Logger logger = LoggerFactory.getLogger(ReadTxtFile.class);
	
	   public static String readTxtFile(String filePath){
	        try {
	                String encoding="utf-8";
	                File file=new File(filePath);
	                if(file.isFile() && file.exists()){ //判断文件是否存在
	                    InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
	                    BufferedReader bufferedReader = new BufferedReader(read);
	                    String lineTxt = null;
	                    StringBuffer sb=new StringBuffer();
	                    while((lineTxt = bufferedReader.readLine()) != null){
	                    	sb.append(lineTxt);
	                    }
	                    read.close();
	                    return sb.toString();
	        }else{
	            System.out.println("找不到指定的文件");
	        }
	        } catch (Exception e) {
	            System.out.println("读取文件内容出错");
	            e.printStackTrace();
	        }
	        return null;     
	    }
	    
	   
	    //java在不存在文件夹的目录下创建文件
	   public static void createFile(File file) {
		    	
		        if (file.isFile() && file.exists()) {  // isFile()：判断是否文件，也许可能是文件或者目录   exists()：判断是否存在，可能不存在
		        	file.delete();
		            createFile(file);
		        	
		        } else {
		            if (file.isDirectory()) {  //isDirectory  //判断是否为文件夹
		            	file.mkdirs();
		            } else {
		            	File fileParent = file.getParentFile();  
		            	if(!fileParent.exists()){  
		            	    fileParent.mkdirs();  
		            	    logger.debug("dirDirectory not exists, create dirDirectory ...");
		            	}  
		            	try {
							file.createNewFile();
							logger.debug("dir not exists, create it ...");
						} catch (IOException e) {
							e.printStackTrace();
						}
		            }
		        }

		    }
	   
	   
	   // 判断文件是否存在
	    public static void judeFileExists(File file) {

	        if (file.exists()) {
	            file.delete();
	            System.out.println("file exists,delete....");
	        } else {
	            System.out.println("file not exists, create it ...");
	            try {
	                file.createNewFile();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }

	    }

	    //在创建之前，要通过file.exists()判断是否存在test命名的文件或者文件夹，如果返回true，是不能创建的；然后再通过file.isDirectory()来判断这是不是一个文件夹。
	    // 判断文件夹是否存在
	    public static void judeDirExists(File file) {
	    	
	        if (file.exists()) {
	            if (file.isDirectory()) {  //isDirectory  //判断是否为文件夹
	                System.out.println("dir exists");
	            } else {
	                System.out.println("the same name file exists, can not create dir");
	            }
	        } else {
	            System.out.println("dir not exists, create it ...");
	            file.mkdirs();
	        }

	    }
	    
	    //当前文件中的文件名
	    public static String [] getFileName(String path)
	    {
	        File file = new File(path);
	        String [] fileName = file.list();
	        return fileName;
	    }
	    
	    //递归获取包括当前文件及其子文件的文件名。
	    public static void getAllFileName(String path,ArrayList<String> fileName)
	    {
	        File file = new File(path);
	        File [] files = file.listFiles();
	        String [] names = file.list();
	        if(names != null)
	        fileName.addAll(Arrays.asList(names));
	        for(File a:files)
	        {
	            if(a.isDirectory()) //判断是否是文件夹
	            {
	                getAllFileName(a.getAbsolutePath(),fileName);
	            }
	        }
	    }
	    
	    //获取当前文件夹中所有文件的绝对路径
		public static void getFileAbsolutePathList(String dirPath,List<String> filePathList) {
			File dir = new File(dirPath);
			File[] fileList = dir.listFiles();
			for (File f : fileList) {
//				if ((f.isFile())&& (".csv".equals(f.getName().substring(f.getName().lastIndexOf("."), f.getName().length())))) {
				if (f.isFile()) {
					filePathList.add(f.getAbsolutePath()); //获取文件绝对路径
				}
				
			}
		}
	    
		//递归获取当前文件夹中及其子文件中所有文件的绝对路径
		public static void getALLFileAbsolutePathList(String dirPath,List<String> filePathList) {
			File dir = new File(dirPath);
			File[] fileList = dir.listFiles();
			for (File f : fileList) {
//				if ((f.isFile())&& (".csv".equals(f.getName().substring(f.getName().lastIndexOf("."), f.getName().length())))) {
				if (f.isFile()) {
					filePathList.add(f.getAbsolutePath()); //获取文件绝对路径
				}
				 if(f.isDirectory()) //判断是否是文件夹
	            {
					 getALLFileAbsolutePathList(f.getAbsolutePath(),filePathList);
	            }
				
			}
		}
		
		//获取文件路径下 所有文件最新修改的时间 此抽象路径名表示的文件最后一次被修改的时间。
		//注意 文件路径是否错误，可使用file.exists()查看，若为false，则表示未使用该路径获取到File实例，此时的file.lastModified()值就会是0
		public static String getALLFileAbsolutePathByLastModified(String dirPath){
			List<String> filePathList = new ArrayList<String>();
			getALLFileAbsolutePathList(dirPath, filePathList);
			Map<Object, String> map = new HashMap<Object, String>();
			for (String path : filePathList) {
				File f = new File(path);
				map.put(f.lastModified(),path);
			}
			Object[] obj = map.keySet().toArray();
			Arrays.sort(obj); //根据key 文件时间升序排序
			System.out.println(map.get(obj[obj.length - 1]));
			return dirPath;
		}
		
		public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		/**
		 * //递归获取当前文件夹中及其子文件中所有文件的绝对路径 并按照key=文件最新修改时间排序 value=文件绝对路径
		 * @param dirPath
		 * @param treeMap = new TreeMap<String, String>( new Comparator<String>() {})  
		 */
		public static void getALLFileAbsolutePathList(String dirPath,Map<String, String> treeMap) {
			File dir = new File(dirPath);
			File[] fileList = dir.listFiles();
			 // 对象为空 直接返回
	        if(fileList == null){
	        	//判断是否存在 不存在文件夹 则创建
				ReadTxtFile.judeDirExists(dir);
				fileList = dir.listFiles();
	        }
			
			for (File f : fileList) {
				if ((f.isFile())){
					    String prfexName = f.getName().substring(f.getName().lastIndexOf("."));
						if(".xls".equals(prfexName)||".xlsx".equals(prfexName)){
							
							//map.put(f.getAbsolutePath(),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(f.lastModified())); //获取文件绝对路径
							treeMap.put(format.format(f.lastModified()),f.getAbsolutePath()); //获取文件绝对路径
						}
					
				}
				if(f.isDirectory()) //判断是否是文件夹
				{
					getALLFileAbsolutePathList(f.getAbsolutePath(),treeMap);
				}
				
			}
		}
		
		
		
		/**
		 * 通过时间比较器获取最新修改时间的文件
		 * @param dirPath
		 * @return
		 * 
		 * 注意此处 底层在往出取值的时候用到了我们传入的比较器中的compare（）方法，TreeMap是二叉树结构存储数据。当我们拿着键去get值时，底层拿着我们传入的键去逐个比对此处调用比较器中的compare方法：小于零则往左边去找，大于零往右去找，
		 * 只有当等于0时才返回该值，而我们强制compare（）方法等于0的时候返回1，所以一直往右边去找，永远找不到，直到最后返回null
		 */
		public static String getALLFileAbsolutePathByLastModifiedCompartor(String dirPath){
			TreeMap<String, String> treeMap = new TreeMap<String, String>(new Comparator<String>() {
				
						@Override
	                    public int compare(String key1, String key2) {
							
							 Date d1, d2;
						        try {
						            d1 = format.parse(key1);
						            d2 = format.parse(key2);
						        } catch (ParseException e) {
						            // 解析出错，则不进行排序
						        	logger.error("日期转换异常", e);
						            return 0;
						        }
						        if (d1.before(d2)) {
						            return 1;  //d1<d2 返回1 降序排序 返回-1 升序排序
						        }else if(d1.after(d2)){
						        	return -1;
						        }else{
						        	return 0;  //只有当等于0时才可以 value=get(key)
						        }
							
	                    }
	                });
			
			getALLFileAbsolutePathList(dirPath, treeMap);
			if(treeMap.isEmpty()){
				return null;
			}
			String string = treeMap.get(treeMap.firstKey()); //获取最近更新的文件
			logger.debug("项目路径【{}】下存在文件个数【{}】",dirPath,treeMap.size());
			logger.info("项目路径：【{}】下所有文件最新修改的文件是：【{}】",dirPath,string);
			return string;
		}
		
		/**
	     * MultipartFile 转换成File
	     * 
	     * @param multfile 原文件类型
	     * @return File
	     * @throws IOException
	     */
	    public File multipartConvertFile(MultipartFile multfile) throws IOException {
	    	CommonsMultipartFile cf = (CommonsMultipartFile)multfile; 
			//这个myfile是MultipartFile的
			DiskFileItem fi = (DiskFileItem) cf.getFileItem();
			File file = fi.getStoreLocation();
			//手动创建临时文件
			if(file.length() < 1024*5L){
				File tmpFile = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + 
						file.getName());
				multfile.transferTo(tmpFile);
		        return tmpFile;
			}
	        return file;
	    }
	    
	    //给定路径与Json文件，存储到硬盘
	    public static void writeJson(String path,Object json,String fileName){
	        //BufferedWriter writer = null;
	        Writer writer=null;
	        File file = new File(path +"\\"+ fileName + ".json");
	        
	        createFile(file);
	        //写入
	        try {
	        	// 格式化json字符串
//	           String jsonString = formatJson((String)json);

//	            writer = new BufferedWriter(new FileWriter(file));
	        	// 将格式化后的字符串写入文件
	        	writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
	            writer.write((String)json);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }finally {
	            try {
	                if(writer != null){
	                    writer.close();
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        System.out.println(fileName+"----文件写入成功！");
	    }	   

	    /**
		   * 单位缩进字符串。
		   */
		  private static String SPACE = "  ";

		  /**
		   * 返回格式化JSON字符串。
		   * 
		   * @param json 未格式化的JSON字符串。
		   * @return 格式化的JSON字符串。
		   */
		  public static String formatJson(String json) {
		    StringBuffer result = new StringBuffer();

		    int length = json.length();
		    int number = 0;
		    char key = 0;

		    // 遍历输入字符串。
		    for (int i = 0; i < length; i++) {
		      // 1、获取当前字符。
		      key = json.charAt(i);

		      // 2、如果当前字符是前方括号、前花括号做如下处理：
		      if ((key == '[') || (key == '{')) {
		        // （1）如果前面还有字符，并且字符为“：”，打印：换行和缩进字符字符串。
		        if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
		          result.append('\n');
		          result.append(indent(number));
		        }

		        // （2）打印：当前字符。
		        result.append(key);

		        // （3）前方括号、前花括号，的后面必须换行。打印：换行。
		        result.append('\n');

		        // （4）每出现一次前方括号、前花括号；缩进次数增加一次。打印：新行缩进。
		        number++;
		        result.append(indent(number));

		        // （5）进行下一次循环。
		        continue;
		      }

		      // 3、如果当前字符是后方括号、后花括号做如下处理：
		      if ((key == ']') || (key == '}')) {
		        // （1）后方括号、后花括号，的前面必须换行。打印：换行。
		        result.append('\n');

		        // （2）每出现一次后方括号、后花括号；缩进次数减少一次。打印：缩进。
		        number--;
		        result.append(indent(number));

		        // （3）打印：当前字符。
		        result.append(key);

		        // （4）如果当前字符后面还有字符，并且字符不为“，”，打印：换行。
		        if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {
		          result.append('\n');
		        }

		        // （5）继续下一次循环。
		        continue;
		      }

		      // 4、如果当前字符是逗号。逗号后面换行，并缩进，不改变缩进次数。
		      if ((key == ',')) {
		        result.append(key);
		        result.append('\n');
		        result.append(indent(number));
		        continue;
		      }

		      // 5、打印：当前字符。
		      result.append(key);
		    }

		    return result.toString();
		  }

		  /**
		   * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
		   * 
		   * @param number 缩进次数。
		   * @return 指定缩进次数的字符串。
		   */
		  private static String indent(int number) {
		    StringBuffer result = new StringBuffer();
		    for (int i = 0; i < number; i++) {
		      result.append(SPACE);
		    }
		    return result.toString();
		  }
		  
	   
}
