package com.bondex.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.bondex.dao.LabelTemplateDao;
import com.bondex.entity.Template;
import com.bondex.service.LabelTemplateService;
import com.bondex.shiro.security.entity.JsonResult;
import com.bondex.shiro.security.entity.SecurityModel;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.StringUtils;
import com.bondex.util.shiro.ShiroUtils;
@Service
@Transactional(rollbackFor = Exception.class)
public class LabelTemplateServiceImpl implements LabelTemplateService {

	@Autowired
	private LabelTemplateDao labelTemplateDao;
	
	@Override
	public List<Template> getALLTemplate(Template template) {
		return labelTemplateDao.getALLTemplate(template);
	}

	@Override
	public void saveorupdateTempalte(Template template) {
		UserInfo userInfo = ShiroUtils.getUserInfo();
		template.setCreateOpid(userInfo.getOpid());
		template.setCreateName(userInfo.getOpname());
		template.setCreateTime(new Date());
		labelTemplateDao.saveorupdateTempalte(template);
	}

	@Override
	public List<Object> getAllCode(String code) {
		//List<String> allCode = labelTemplateDao.getAllCode(code);
		List<SecurityModel> list = ShiroUtils.getUserSecurityModel();
		ArrayList<Object> arrayList = new ArrayList<Object>();
		for (SecurityModel securityModel : list) {
			 JSONObject jsonObject = new JSONObject();
			 jsonObject.put("id", securityModel.getPageCode());
			 jsonObject.put("text", securityModel.getModuleName());
			 arrayList.add(jsonObject);
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
		List<JsonResult> list = ShiroUtils.getUserPrintTemplateInfo();
		ArrayList<Object> arrayList = new ArrayList<Object>();
		for (JsonResult jsonResult : list) {
			 JSONObject jsonObject = new JSONObject();
			 jsonObject.put("id", jsonResult.getReportid());
			 jsonObject.put("text", jsonResult.getReportname());
			 arrayList.add(jsonObject);
		}
		return arrayList;
	}

	
	
}
