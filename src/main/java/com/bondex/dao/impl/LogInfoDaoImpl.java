package com.bondex.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.bondex.config.jdbc.JdbcTemplateSupport;
import com.bondex.dao.LogInfoDao;
import com.bondex.dao.base.BaseDao;
import com.bondex.entity.log.Log;
import com.bondex.entity.page.PageBean;
import com.bondex.mapper.AdminDataCurrentMapper;
import com.bondex.shiro.security.entity.SecurityModel;
import com.bondex.util.StringUtils;
import com.bondex.util.shiro.ShiroUtils;

@Repository
public class LogInfoDaoImpl extends BaseDao<Log, Integer> implements LogInfoDao  {
	
	
	private JdbcTemplateSupport jdbcTemplateSupport;
	
	@Autowired
	public LogInfoDaoImpl(JdbcTemplateSupport jdbcTemplateSupport) {
		super(jdbcTemplateSupport);
		this.jdbcTemplateSupport = jdbcTemplateSupport;
	}
	

	@Autowired
	AdminDataCurrentMapper adminDataCurrentMapper;
	
	
	@Override
	public List<Log> getlogDetail(Log log) {
		String sql="SELECT * FROM `log` WHERE 1=1 ";
		
		if(StringUtils.isNotNull(log)){
			
			if(StringUtils.isNotBlank(log.getSeqNo())){
				sql+=" and seq_no like '%"+log.getSeqNo()+"%'";
			}
			if(StringUtils.isNotBlank(log.getCode())){
				sql+=" and code = '"+log.getCode()+"'";
			}
			if(StringUtils.isNotBlank(log.getMawb())){
				sql+=" and mawb like '%"+log.getMawb()+"%'";
			}
			if(StringUtils.isNotBlank(log.getHawb())){
				sql+=" and hawb like '%"+log.getHawb()+"%'";
			}
			if(StringUtils.isNotBlank(log.getSenderName())){
				sql+=" and sender_name = '"+log.getSenderName()+"'";
			}
			if(StringUtils.isNotBlank(log.getReciverName())){
				sql+=" and reciver_name = '"+log.getReciverName()+"'";
			}
			if(StringUtils.isNotBlank(log.getDoctypeName())){
				sql+=" and docType_name = '"+log.getDoctypeName()+"'";
			}
			Map<String, Object> params = log.getParams();
			if(StringUtils.isNotEmpty(params)&& StringUtils.isNotBlank((String)params.get("beginTime"))){
				sql+=" AND date_format(update_time,'%y%m%d') >= date_format('"+params.get("beginTime")+"','%y%m%d')";
			}
			if(StringUtils.isNotEmpty(params) && StringUtils.isNotBlank((String)params.get("endTime"))){
				sql+=" AND date_format(update_time,'%y%m%d') <= date_format('"+params.get("endTime")+"','%y%m%d')";
			}
			
			sql+=" and status = :status";
			sql+=" and code in (:code) ";
		}
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		List<SecurityModel> model = ShiroUtils.getUserSecurityModel();
		List<String> codes = model.stream().map(se -> se.getPageCode()).collect(Collectors.toList());
		map.addValue("status", log.getStatus());
		map.addValue("code", codes);
		
		//JDBC实现 查询 BeanPropertyRowMapper自动驼峰命名转换
		PageBean<Log> pageBean = jdbcTemplateSupport.queryForPage(sql,true,null,map,new BeanPropertyRowMapper<Log>(Log.class));
		List<Log> list = pageBean.getList();
		//mybatis mapper实现
		//List<Log> list = adminDataCurrentMapper.querylogDetail(sql);
		return list;
	}

	/**
	 * 日志入库
	 */
	@Override
	public void insertLableLog(Log log) {
		//插入数据
		super.insert(log, true);
		/*
		jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,updateTime,json) VALUES('','',3,'','','"
				+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + "','"
				+ message.replaceAll("(')", "\\\\'") + "')"); //替换为标准的json数据
        */}

}
