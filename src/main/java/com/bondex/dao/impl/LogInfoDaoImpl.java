package com.bondex.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bondex.config.jdbc.JdbcTemplateSupport;
import com.bondex.dao.LogInfoDao;
import com.bondex.dao.base.BaseDao;
import com.bondex.entity.log.Log;
import com.bondex.entity.page.PageBean;
import com.bondex.mapper.AdminDataCurrentMapper;
import com.bondex.util.LocalDateTimeUtils;
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
	public PageBean<Log> getlogDetail(Log log) {
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
				String beginTime = LocalDateTimeUtils.getBeginTime((String)params.get("beginTime"),LocalDateTimeUtils.YYYY_MM_DD).toString();
				sql+=" AND update_time >= '"+beginTime+"'";
			}
			if(StringUtils.isNotEmpty(params) && StringUtils.isNotBlank((String)params.get("endTime"))){
				String endTime = LocalDateTimeUtils.getEndTime((String)params.get("endTime"),LocalDateTimeUtils.YYYY_MM_DD).toString();
				sql+=" AND update_time <= '"+endTime+"'";
			}
			
			sql+=" and status = :status";
			sql+=" and code in (:code) ";
		}
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		List<String> pageCodes = ShiroUtils.getUserInfo().getPageCodes();
		map.addValue("status", log.getStatus());
		map.addValue("code", pageCodes);
		
		//JDBC实现 查询 BeanPropertyRowMapper自动驼峰命名转换
		PageBean<Log> pageBean = jdbcTemplateSupport.queryForPage(sql,true,null,map,new BeanPropertyRowMapper<Log>(Log.class));
		//mybatis mapper实现
		//List<Log> list = adminDataCurrentMapper.querylogDetail(sql);
		return pageBean;
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
        */
	}

	
	@Override
	public Boolean checkCorrelationIdUnique(String correlationId) {
		String sql="select ifnull((select correlation_id  from log where correlation_id=:correlationId limit 1 ), 0)";
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("correlationId", correlationId);
		String res = jdbcTemplateSupport.queryForObject(sql, map,String.class);
		//结果为 1，则说明记录存在；结果为 0，则说明记录不存在。 
		if("1".equals(res)){ 
			return true;
		}else{
			return false;//不存在
		}
	}

}
