package com.bondex.jdbc.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bondex.jdbc.dao.LogInfoDao;
import com.bondex.jdbc.entity.Log;
import com.bondex.mapper.AdminDataCurrentMapper;
import com.bondex.util.StringUtils;

@Service
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
			
			sql+=" and status = "+log.getStatus();
		}
		
		List<Log> list = adminDataCurrentMapper.querylogDetail(sql);
		return list;
	}

}
