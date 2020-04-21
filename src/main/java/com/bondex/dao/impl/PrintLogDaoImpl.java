package com.bondex.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.bondex.config.jdbc.JdbcTemplateSupport;
import com.bondex.dao.PrintLogDao;
import com.bondex.dao.base.BaseDao;
import com.bondex.entity.log.PrintLog;
import com.bondex.entity.page.PageBean;
import com.bondex.util.StringUtils;
import com.bondex.util.shiro.ShiroUtils;

@Repository
public class PrintLogDaoImpl extends BaseDao<PrintLog, Integer> implements PrintLogDao {

	private JdbcTemplateSupport jdbcTemplateSupport;
	
	@Autowired
	public PrintLogDaoImpl(JdbcTemplateSupport jdbcTemplateSupport) {
		super(jdbcTemplateSupport);
		this.jdbcTemplateSupport = jdbcTemplateSupport;
	}

	@Override
	public void insertPrintLog(PrintLog log) {
		super.insert(log, true);
	}
	

	//拼装成父子表格形式
	@Override
	public PageBean<PrintLog> getPrintlogDetail(PrintLog printLog) {
		String sql="SELECT * FROM `print_log` WHERE 1=1 ";
		
		if(StringUtils.isNotNull(printLog)){
			
			if( null!=printLog.getId()){
				sql+=" and id = '"+printLog.getId()+"'";
			}
			if(StringUtils.isNotBlank(printLog.getCode())){
				sql+=" and code = '"+printLog.getCode()+"'";
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
			sql+=" and status = :status";
			sql+=" and code in (:code) ";
			
			Map<String, Object> params = printLog.getParams();
			if(StringUtils.isNotEmpty(params)&& StringUtils.isNotBlank((String)params.get("beginTime"))){
				sql+=" AND date_format(update_time,'%y%m%d') >= date_format('"+params.get("beginTime")+"','%y%m%d')";
			}
			if(StringUtils.isNotEmpty(params) && StringUtils.isNotBlank((String)params.get("endTime"))){
				sql+=" AND date_format(update_time,'%y%m%d') <= date_format('"+params.get("endTime")+"','%y%m%d')";
			}
			
		}
		MapSqlParameterSource map = new MapSqlParameterSource();
		List<String> pageCodes = ShiroUtils.getUserInfo().getPageCodes();
		map.addValue("status", printLog.getStatus());
		map.addValue("code", pageCodes);
		
		PageBean<PrintLog> pageBean = jdbcTemplateSupport.queryForPage(sql, true, null, map, new BeanPropertyRowMapper<PrintLog>(PrintLog.class));
		return pageBean;
	}


	
	
}
