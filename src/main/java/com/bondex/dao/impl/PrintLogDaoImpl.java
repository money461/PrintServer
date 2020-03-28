package com.bondex.dao.impl;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.bondex.config.jdbc.JdbcTemplateSupport;
import com.bondex.dao.PrintLogDao;
import com.bondex.dao.base.BaseDao;
import com.bondex.entity.log.PrintLog;
import com.bondex.entity.page.PageBean;
import com.bondex.util.CollectionUtils;
import com.bondex.util.StringUtils;
import com.github.pagehelper.Page;

@Repository
public class PrintLogDaoImpl extends BaseDao<PrintLog, Integer> implements PrintLogDao {

	@Autowired
	public PrintLogDaoImpl(JdbcTemplateSupport jdbcTemplate) {
		super(jdbcTemplate);
	}

	@Override
	public void insertPrintLog(PrintLog log) {
		super.insert(log, true);
	}
	

	//拼装成父子表格形式
	@Override
	public PageBean<PrintLog> getPrintlogDetail(PrintLog printLog) {
		Page<PrintLog> pagination = startPage(true);//设置分页pagehelper
		
		String sql="SELECT * FROM `print_log` WHERE 1=1 ";
		
		if(StringUtils.isNotNull(printLog)){
			
			if( null!=printLog.getId()){
				sql+=" and id = '"+printLog.getId()+"'";
			}
			if(StringUtils.isNotBlank(printLog.getLabelId())){
				sql+=" and label_id = '"+printLog.getLabelId()+"'";
			}
			if(StringUtils.isNotBlank(printLog.getShowNum())){
				sql+=" and show_num like '%"+printLog.getShowNum()+"%'";
			}
			if(StringUtils.isNotBlank(printLog.getOpid())){
				sql+=" and opid in ("+printLog.getOpid()+")";
			}
			if(StringUtils.isNotBlank(printLog.getOpidName())){
				sql+=" and opid_name like '%"+printLog.getOpidName()+"%'";
			}
			
			String queueCode = printLog.getQueueCode();
			if(StringUtils.isNotBlank(queueCode)){
				sql+=" and queue_code like '%"+queueCode+"%'";
			}
			 String regionName = printLog.getRegionName();
			if(StringUtils.isNotBlank(regionName)){
				sql+=" and region_name like '%"+regionName+"%'";
			}
			//状态
			sql+=" and status = ?";
			
			Map<String, Object> params = printLog.getParams();
			if(StringUtils.isNotEmpty(params)&& StringUtils.isNotBlank((String)params.get("beginTime"))){
				sql+=" AND date_format(update_time,'%y%m%d') >= date_format('"+params.get("beginTime")+"','%y%m%d')";
			}
			if(StringUtils.isNotEmpty(params) && StringUtils.isNotBlank((String)params.get("endTime"))){
				sql+=" AND date_format(update_time,'%y%m%d') <= date_format('"+params.get("endTime")+"','%y%m%d')";
			}
			
		}
		
		return findPageBySQL(sql, new Object[] {printLog.getStatus()},new BeanPropertyRowMapper<PrintLog>(PrintLog.class), pagination);
	}


	
	
}
