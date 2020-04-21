package com.bondex.service.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.bondex.cas.SpringCasAutoconfig;
import com.bondex.common.Common;
import com.bondex.config.exception.BusinessException;
import com.bondex.dao.LabelTemplateDao;
import com.bondex.entity.Template;
import com.bondex.entity.page.PageBean;
import com.bondex.service.LabelTemplateService;
import com.bondex.shiro.security.entity.PrintTemplatePremission;
import com.bondex.shiro.security.entity.SecurityModel;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.HttpClientUtil;
import com.bondex.util.StringUtils;
import com.bondex.util.UploadAndStoreFile;
import com.bondex.util.file.FileUtils;
import com.bondex.util.shiro.ShiroUtils;
@Service
@Transactional(rollbackFor = Exception.class)
public class LabelTemplateServiceImpl implements LabelTemplateService {

	@Autowired
	private LabelTemplateDao labelTemplateDao;
	
	@Autowired
	private SpringCasAutoconfig casAutoconfig;
	
	@Resource(name="taskExecutor")
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	@Override
	public PageBean<Template> getALLTemplateByUserAuth(Template template) {
		return labelTemplateDao.getALLTemplateByUserAuth(template);
	}

	@Override
	public void saveorupdateTempalte(Template template) {
		UserInfo userInfo = ShiroUtils.getUserInfo();
		template.setCreateOpid(userInfo.getOpid());
		template.setCreateName(userInfo.getOpname());
		template.setCreateTime(new Date());
		String templateUrl = template.getTemplateUrl();
		if(StringUtils.isBlank(templateUrl)){
			template.setTemplateUrl(null);
		}
		labelTemplateDao.saveorupdateTempalte(template);
	}

	@Override
	public List<Object> getAllCode(String code) {
		//List<String> allCode = labelTemplateDao.getAllCode(code);
		UserInfo userInfo = ShiroUtils.getUserInfo();
		List<SecurityModel> list = userInfo.getSecurityModels();
		ArrayList<Object> arrayList = new ArrayList<Object>();
		for (SecurityModel securityModel : list) {
			if(securityModel.getPageCode().endsWith(Common.PageCode_Suffix)){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", securityModel.getPageCode());
				jsonObject.put("text", securityModel.getModuleName());
				arrayList.add(jsonObject);
			}
		}
		return arrayList;
	}

	/**
	 * 业务code 对应多个模板 同一个code 只能默认绑定一个 code code=0
	 */
	@Override
	public int checkCodeBindTemplateUnique(Template template) {
		Integer isDefault = template.getIsDefault();
		if(isDefault==1){ //code 不默认绑定模板
			return 0;
		}
		
		String code = template.getCode();
		if(StringUtils.isBlank(code)){
			return 0; //校验通过
		}
		Template template2 = new Template();
		template2.setCode(code);
		template2.setIsDefault(isDefault);
		List<Template> list = labelTemplateDao.getALLTemplate(template2);
		if(StringUtils.isNotEmpty(list)){
			if(list.size()!=1){return 1;}
			Template template3 = list.get(0);
			String templateId = template.getTemplateId();
			String templateId2 = template3.getTemplateId();
			if(!templateId2.equals(templateId)){
				return 1; //相同的code默认绑定 不相同的模板id 不通过校验
			}
			
		}
		return 0; //校验通过
	}

	/**
	 * 修改默认模板
	 */
	@Override
	public void changeCodeDefaultTemplate(Template template) {
		//获取需要修改的数据
		Template templat = labelTemplateDao.getTemplateById(template.getId());
		if(StringUtils.isNotNull(templat)){
			
			if(template.getIsDefault()==0){ //需要默认绑定
				//取消该code 与其它模板的绑定
				labelTemplateDao.cancelCodeDefaultTemplate(templat.getCode());
			}
			
			templat.setIsDefault(template.getIsDefault()); //修改绑定状态
			labelTemplateDao.saveorupdateTempalte(templat);
			
		}
		
		
	}

	/**
	 * 获取用户打印权限
	 */
	@Override
	public List<Object> getUserAuthorizationTemplate(String templateId) {
		List<PrintTemplatePremission> list = ShiroUtils.getUserPrintTemplateInfo();
		ArrayList<Object> arrayList = new ArrayList<Object>();
		for (PrintTemplatePremission jsonResult : list) {
			 JSONObject jsonObject = new JSONObject();
			 jsonObject.put("id", jsonResult.getReportid());
			 jsonObject.put("text", jsonResult.getReportname());
			 arrayList.add(jsonObject);
		}
		return arrayList;
	}
	
	/**
	 * 上传文件
	 */

	@Override
	public Object importTemplate(MultipartFile[] files, Boolean batchUploadSupport, boolean updateSupport) {
		
	 String uploadAndStoreFileUrl = casAutoconfig.getUploadAndStoreFileUrl();
	     //批量上传 文件名必须在数据库中存在
	 if(batchUploadSupport){
		  //获取所有的文件名称
	   	 List<String> fileNames = new ArrayList<>();
	   	 StringBuilder sb = new StringBuilder();
   		 for (MultipartFile file : files) {
   			 String orgfilename = file.getOriginalFilename();
   			 if(file.getSize()<=0){
   				 throw new  BusinessException("500",orgfilename+"上传文件不能为空！");
   			 }
   			 //添加文件名称
   			 String prefixName = UploadAndStoreFile.getFilePrefixName(orgfilename);
   			 fileNames.add(prefixName);
   			 sb.append(prefixName).append(",");
   		 }
		 
	   	 //校验数据库中文件是否存在。
	   	Template template = new Template();
	   	template.setTemplateId(sb.toString());
	   	//查询已经上传入库的模板
	   	PageBean<Template> pageBean = labelTemplateDao.getALLTemplateByUserAuth(template);
	   	List<Template> templateByUserAuth = pageBean.getList();
	   	List<String> rtList2 = templateByUserAuth.stream().map(res -> res.getTemplateId()).collect(Collectors.toList());
	   	HashSet<String> set = new HashSet<String>();
	   	set.addAll(rtList2);
	   	for (String fileName : fileNames) {
	   		if(!set.contains(fileName)){
	   			throw new  BusinessException("500","文件名[ "+fileName+" ]非法(无模板权限/无指定模板名称),不能入库！");
	   		}
		}
	   	//是否更新覆盖模板上传文件
	   	if(!updateSupport){
	   		//排除已上传的文件
	   		fileNames.removeAll(rtList2);
	   	}
		
	    for (MultipartFile file : files) {
	    	 String originalFilename = file.getOriginalFilename();
	    	 String prefixName = UploadAndStoreFile.getFilePrefixName(originalFilename);
	    	 if(!fileNames.contains(prefixName)){
	    		 continue;
	    	 }
	    	// HttpPost 文件上传 
	    	String response = HttpClientUtil.uploadAndStoreFile2(uploadAndStoreFileUrl, file);
	    	System.out.println(response);
	    	if(StringUtils.isNotNull(response)){
	    		JSONObject jsonObject = JSONObject.parseObject(response);
	    		String code = jsonObject.getString("code");
	    		if("1".equals(code)){
	    			JSONObject jsonObject2= (JSONObject)jsonObject.getJSONArray("data").get(0);
	    			String templateUrl  = jsonObject2.getString("UniqueName");
	    			//更新数据库
	    			Template template2 = new Template();
	    			template.setTemplateId(prefixName);
	    			template.setTemplateUrl(templateUrl);
	    			labelTemplateDao.saveorupdateTempalte(template2);
	    		}else{
	    			throw new  BusinessException("500","上传文件失败，数据回滚！");
	    		}
	    		
	    	}else{
	    		throw new  BusinessException("500","上传文件失败，数据回滚！");
	    	}
	    	
		}
	}else{
		//单独上传文件
		String response = HttpClientUtil.uploadAndStoreFile2(uploadAndStoreFileUrl, files[0]);
	 	System.out.println(response);
    	if(StringUtils.isNotNull(response)){
    		JSONObject jsonObject = JSONObject.parseObject(response);
    		String code = jsonObject.getString("code");
    		if("1".equals(code)){
    			JSONObject jsonObject2= (JSONObject)jsonObject.getJSONArray("data").get(0);
    			String templateUrl  = jsonObject2.getString("UniqueName");
    			String DownloadUrl  = jsonObject2.getString("DownloadUrl");
    			String FileName  = jsonObject2.getString("FileName");
    			LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
    			linkedHashMap.put("downloadUrl", DownloadUrl);
    			linkedHashMap.put("templateUrl", templateUrl);
    			linkedHashMap.put("fileName", FileName);
    			return linkedHashMap; //返回唯一文件名地址
    		}else{
    			throw new  BusinessException("500","上传文件失败！");
    		}
    		
    	}else{
    		throw new  BusinessException("500","上传文件失败！");
    	}
		
	}
	return null;
}

	/**
	 * 批量文件下载
	 */
	@Override
	public String exportTemplate(String templateId, HttpServletRequest request, HttpServletResponse response) {
		Template template1 = new Template();
		template1.setTemplateId(templateId);
		List<Template> allTemplate = labelTemplateDao.getALLTemplate(template1);
	
		 OutputStream out=null;
       	 BufferedOutputStream bos=null;
       	 ZipOutputStream zipOutputStream=null;
       	try {
       		 out = response.getOutputStream();
       		//使用装饰模式，把out装饰进去bos中。使用缓冲写出速度变快
       		 bos=new BufferedOutputStream(out);
       		
       		 zipOutputStream=new ZipOutputStream(bos);
       	    //设置头信息 
     		FileUtils.setMyHeader(response, "打印模板");
       	
		for (Template template : allTemplate) {
			String url = template.getTemplateUrl();
			String templateName = template.getTemplateName();
			if(StringUtils.isBlank(url)){
				throw new BusinessException(templateName+"未上传打印模板！");
			}
			HttpClientUtil.downLoadFile(url,templateName, zipOutputStream);
		}	
			
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				try {
					zipOutputStream.flush();
					zipOutputStream.close();
					bos.flush();
					bos.close();
					out.flush();
					out.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		  
		return null;
	}
	
	
}
