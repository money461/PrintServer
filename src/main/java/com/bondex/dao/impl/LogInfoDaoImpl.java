package com.bondex.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bondex.dao.LogInfoDao;
import com.bondex.dao.base.BaseDao;
import com.bondex.entity.log.Log;
import com.bondex.mapper.AdminDataCurrentMapper;
import com.bondex.util.StringUtils;

@Repository
public class LogInfoDaoImpl extends BaseDao<Log, Integer> implements LogInfoDao  {
	
	@Autowired
	public LogInfoDaoImpl(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
	}

	@Autowired
	AdminDataCurrentMapper adminDataCurrentMapper;
	
	
	
	@Override
	public List<Log> getlogDetail(Log log) {
		String sql="SELECT * FROM `log` WHERE 1=1 ";
		
		if(StringUtils.isNotNull(log)){
			
			if(StringUtils.isNotBlank(log.getSeqNo())){
				sql+=" and seqNo like '%"+log.getSeqNo()+"%'";
			}
			if(StringUtils.isNotBlank(log.getMawb())){
				sql+=" and mawb like '%"+log.getMawb()+"%'";
			}
			if(StringUtils.isNotBlank(log.getHawb())){
				sql+=" and mawb like '%"+log.getHawb()+"%'";
			}
			if(StringUtils.isNotBlank(log.getSenderName())){
				sql+=" and senderName = '"+log.getSenderName()+"'";
			}
			if(StringUtils.isNotBlank(log.getReciverName())){
				sql+=" and reciverName = '"+log.getReciverName()+"'";
			}
			if(StringUtils.isNotBlank(log.getDocTypeName())){
				sql+=" and docTypeName = '"+log.getDocTypeName()+"'";
			}
			Map<String, Object> params = log.getParams();
			if(StringUtils.isNotEmpty(params)&& StringUtils.isNotBlank((String)params.get("beginTime"))){
				sql+=" AND date_format(updateTime,'%y%m%d') >= date_format('"+params.get("beginTime")+"','%y%m%d')";
			}
			if(StringUtils.isNotEmpty(params) && StringUtils.isNotBlank((String)params.get("endTime"))){
				sql+=" AND date_format(updateTime,'%y%m%d') <= date_format('"+params.get("endTime")+"','%y%m%d')";
			}
			
			sql+=" and status = "+log.getStatus();
		}
		
		List<Log> list = adminDataCurrentMapper.querylogDetail(sql);
		return list;
	}

	/**
	 * 日志入库
	 */
	@Override
	public void insertLable(Log log) {
		//插入数据
		super.insert(log, true);
		/*
		jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,updateTime,json) VALUES('','',3,'','','"
				+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + "','"
				+ message.replaceAll("(')", "\\\\'") + "')"); //替换为标准的json数据
        */}

}
