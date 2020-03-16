package com.bondex.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.bondex.dao.LogInfoDao;
import com.bondex.entity.log.Log;
import com.bondex.mapper.AdminDataCurrentMapper;
import com.bondex.util.StringUtils;

@Component
public class LogInfoDaoImpl implements LogInfoDao {
	
	@Autowired
	AdminDataCurrentMapper adminDataCurrentMapper;
	
	@Override
	public List<Log> getlogDetail(Log log) {
		String sql="SELECT * FROM `log` WHERE 1=1 ";
		
		if(StringUtils.isNotNull(log)){
			
			if(StringUtils.isNotBlank(log.getSeqNo())){
				sql+=" and seqNo like '%"+log.getSeqNo()+"'%";
			}
			if(StringUtils.isNotBlank(log.getMawb())){
				sql+=" and mawb like '%"+log.getMawb()+"'%";
			}
			if(StringUtils.isNotBlank(log.getHawb())){
				sql+=" and mawb like '%"+log.getHawb()+"'%";
			}
			if(StringUtils.isNotBlank(log.getSenderName())){
				sql+=" and senderName = '"+log.getSenderName()+"'";
			}
			if(StringUtils.isNotBlank(log.getReciverName())){
				sql+=" and reciverName = '"+log.getReciverName()+"'";
			}
			if(StringUtils.isNotBlank(log.getRocTypeName())){
				sql+=" and rocTypeName = '"+log.getRocTypeName()+"'";
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

}
