package com.bondex.util;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bondex.common.Common;
import com.bondex.shiro.security.entity.UserInfo;

import cn.hutool.core.date.DateUtil;
/**
 * 文档归档必填参数 
 * @author Qianli
 * 
 */
public class UploadAndStoreFile {

public static String createReadparamWithKeys(String originalFilename,String contentType, UserInfo userInfo) {
	String foldId ="";
	JSONObject jsonObject = new JSONObject();
	JSONObject head = new JSONObject();
	head.put("SeqNo", UUID.randomUUID().toString());
	head.put("SenderId", userInfo.getEmail());
	head.put("SenderName", "标签打印");
	head.put("FolderId", foldId); //文档类型
	head.put("FolderName", "");
	head.put("CreateOpId", userInfo.getOpid());
	head.put("CreateUserId", "");
	head.put("CreateUser", userInfo.getEmail());
	head.put("CreateTime", DateUtil.now());
	head.put("LoginToken", "");
	head.put("Action", "Add");
	head.put("Version", "1.0");
	JSONObject main = new JSONObject();
	main.put("FileId", 0);
	main.put("UserCode", userInfo.getEmail());
	main.put("FolderId", foldId);
	main.put("FileName", "");
	main.put("FileExtension", getFilenameSuffix(originalFilename));
	main.put("FullName", originalFilename);
	main.put("FileSize", "");
	main.put("FileLastWriteTime", DateUtil.now());
	main.put("FileCreationTime",DateUtil.now());
	main.put("UploadTime", DateUtil.now());
	main.put("CreateOpId", userInfo.getOpid());
	main.put("CreateUser", userInfo.getOpname());
	main.put("MimeType",  contentType);
	main.put("Remarks", "");
	
	JSONArray fileKeys = getfileKeys();
	main.put("FileKey", fileKeys);
	jsonObject.put("Head", head);
	jsonObject.put("Main", main);
	return jsonObject.toJSONString();
}
public static JSONArray getfileKeys() {
	JSONArray fileKeys = new JSONArray();
	JSONObject filekey2 = new JSONObject();
	filekey2.put("KeyCode", "");
	filekey2.put("KeyValue","");
	fileKeys.add(filekey2);
	return fileKeys;

}



/**
 * 获取文件名称的后缀
 *
 * @param filename
 * @return 文件后缀
 */
public static String getFilenameSuffix(String filename) {
    String suffix = null;
    if (StringUtils.isNotBlank(filename) && filename.trim().lastIndexOf(Common.POINT)!= -1) {
        suffix = filename.substring(filename.lastIndexOf(Common.POINT) + 1).toLowerCase();
    }
    return suffix;
}
/**
 * 获取前缀
 * @param filename
 * @return
 */
public static String getFilePrefixName(String filename) {
	String suffix = null;
	if (StringUtils.isNotBlank(filename) && filename.contains(Common.POINT)) {
		suffix = filename.substring(0,filename.lastIndexOf(Common.POINT));
	}
	return suffix;
}

/**
 * 文件上传后的地址拼接
 *  //http://docacc.bondex.com.cn/3A72008E-D03D-3F28-BE45-4DC9506A7CE0?attname=988b4162-00af-4924-a6fe-6b310d161900.repx;
 * @param data
 */
public static String getTemplateUrl(String data,String filename) {
	
	if(StringUtils.isNotBlank(data)){
		StringBuilder sb  = new StringBuilder();
		sb.append("/").append(data).append("?attname=").append(filename);
		return sb.toString();
	}
	return "";
	
}

	
}
