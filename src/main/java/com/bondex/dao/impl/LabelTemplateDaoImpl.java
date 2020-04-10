package com.bondex.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.bondex.config.jdbc.JdbcTemplateSupport;
import com.bondex.dao.LabelTemplateDao;
import com.bondex.dao.base.BaseDao;
import com.bondex.entity.Template;
import com.bondex.entity.page.PageBean;
import com.bondex.mapper.AdminDataCurrentMapper;
import com.bondex.shiro.security.entity.JsonResult;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.StringUtils;
import com.bondex.util.context.ApplicationContextProvider;
import com.bondex.util.shiro.ShiroUtils;

import cn.hutool.core.util.ObjectUtil;

@Repository
public class LabelTemplateDaoImpl extends BaseDao<Template,String> implements LabelTemplateDao {

	@Autowired
	private AdminDataCurrentMapper adminDataCurrentMapper;
	
	private JdbcTemplateSupport jdbcTemplateSupport;
	
	@Autowired
	public LabelTemplateDaoImpl(JdbcTemplateSupport jdbcTemplateSupport) {
		super(jdbcTemplateSupport);
		this.jdbcTemplateSupport = jdbcTemplateSupport;
	}

	/**
	 * 获取数据库中的模板信息
	 */
	@Override
	public List<Template> getALLTemplate(Template template) {
		
		String sql= "select * from template where 1=1 ";
		
		if(StringUtils.isNotBlank(template.getId())){
			sql+=" and id = '"+template.getId()+"'";
		}
		
		if(StringUtils.isNotBlank(template.getCode())){
			sql+=" and code = '"+template.getCode()+"'";
		}
		
		if(StringUtils.isNotBlank(template.getTemplateId())){
			sql+=" and template_id  = '"+template.getTemplateId()+"'";
		}
		if(StringUtils.isNotBlank(template.getTemplateName())){
			sql+=" and template_name like '%"+template.getTemplateName()+"%'";
		}
		if(StringUtils.isNotBlank(template.getStatus())){
			sql+=" and status ="+template.getStatus();
		}
		
		if(ObjectUtil.isNotNull(template.getIsDefault())){
			sql+=" and is_default ="+template.getIsDefault();
		}
		
		String activeProfile = ApplicationContextProvider.getActiveProfile();
		MapSqlParameterSource map = new MapSqlParameterSource();
		if(!"dev".equals(activeProfile)){
			List<JsonResult> securitylist = ShiroUtils.getUserPrintTemplateInfo();
			List<String> rtList = securitylist.stream().map(res -> res.getReportid()).collect(Collectors.toList());
			map.addValue("templateId", rtList);
			sql+=" and template_id  in (:templateId) ";
		}
		
		PageBean<Template> pageBean = jdbcTemplateSupport.queryForPage(sql,true,null,map, new BeanPropertyRowMapper<Template>(Template.class) );
		List<Template> list = pageBean.getList();
		return list;
	}

	/**
	 * 保存更新打印模板
	 */
	@Override
	public void saveorupdateTempalte(Template template) {
		super.insertforUpdate(template, true);
	}

	@Override
	public List<String> getAllCode(String code) {
		String sql = "select DISTINCT code FROM template where 1=1 ";
		if(StringUtils.isNotBlank(code)){
			sql += " and code like '%"+code+"%'";
		}
		sql+=" GROUP BY code";
		List<String> list = adminDataCurrentMapper.queryALLCode(sql);
		return list;
	}

	@Override
	public Template getTemplateById(String id) {
		
		return super.findOneById(id);
	}

	@Override
	public void cancelCodeDefaultTemplate(String code) {
		String sql = "update template set is_default = 1 , create_opid = :opid,create_name = :createName where  code = :code and is_default = 0";
		UserInfo userInfo = ShiroUtils.getUserInfo();
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("opid", userInfo.getOpid());
		map.addValue("createName", userInfo.getOpname());
		map.addValue("code", code);
		
		jdbcTemplateSupport.update(sql,map);
	}

	
}
