package com.bondex.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.shiro.UnavailableSecurityManagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.bondex.config.jdbc.JdbcTemplateSupport;
import com.bondex.dao.CurrentLabelDao;
import com.bondex.dao.base.BaseDao;
import com.bondex.entity.current.BaseLabelDetail;
import com.bondex.entity.page.PageBean;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.CollectionUtils;
import com.bondex.util.StringUtils;
import com.bondex.util.shiro.ShiroUtils;
import com.github.pagehelper.Page;
@Repository
public class CurrentLabelDaoImpl extends BaseDao<BaseLabelDetail, String> implements CurrentLabelDao   {

	private JdbcTemplateSupport jdbcTemplateSupport;
	
	@Autowired
	public CurrentLabelDaoImpl(JdbcTemplateSupport jdbcTemplateSupport) {
		super(jdbcTemplateSupport);
		this.jdbcTemplateSupport = jdbcTemplateSupport;
	}
	
	/**
	 * String rt = list.stream().map(res -> res.getString()).collect(Collectors.joining("','"));
	 * String opids = list.stream().collect(Collectors.joining(",")); //获取用户部门所有的opid
	 */
	@Override
	public PageBean<BaseLabelDetail> selectBaseLabelList(BaseLabelDetail baseLabelDetail,Boolean authorization) {
		UserInfo userInfo = ShiroUtils.getUserInfo();
		Page<BaseLabelDetail> pagination = startPage(true);//设置分页pagehelper
		String sql="SELECT	base_label.id,	base_label.show_num as showNum,	base_label.template_id as templateId,	template.template_name as templateName,	base_label.docType_id as  docTypeId,	base_label.docType_name as docTypeName,	base_label.copies as copies, base_label.print_status AS printStatus, base_label.json_data as jsonData,base_label.opid as opid,base_label.opid_name as opidName,	base_label.alert_name as alertName,	base_label.print_name as print_name,	base_label.update_time as updateTime,	base_label.create_time as createTime	FROM	base_label	LEFT JOIN template ON base_label.template_id = template.template_id WHERE 1 = 1 ";
		String id = baseLabelDetail.getId();
		if(StringUtils.isNotBlank(id)){
			sql+=" and base_label.id in ( "+id +" )";
		}
		String showNum = baseLabelDetail.getShowNum();
		if(StringUtils.isNotBlank(showNum)){
			String[] split = StringUtils.split(showNum, ",");
			showNum = CollectionUtils.array2List(split).stream().collect(Collectors.joining("','"));
			sql+=" and base_label.show_num in ( '"+showNum+"' )";
		}
		String docTypeName = baseLabelDetail.getDocTypeName();
		if(StringUtils.isNotBlank(docTypeName)){
			sql+=" and base_label.docType_name like concat('%','"+docTypeName +"','%' )";
		}
		
		String opidName = baseLabelDetail.getOpidName();
		if(StringUtils.isNotBlank(opidName)){
			sql+=" and base_label.opid_name like concat('%','"+opidName +"','%' )";
			
		}
		String opid = baseLabelDetail.getOpid(); //逗号分隔的查询opid条件
	    if(authorization){ //需要部门权限
		    	List<String> opidData = userInfo.getOpidData(); //获取用户部门所有的opid
				//交集
				if(StringUtils.isNotBlank(opid)){
					List<String> list = CollectionUtils.array2List(opid.split(","));
					opidData.retainAll(list);
				}
				opid  = opidData.stream().collect(Collectors.joining(",")); 
		}
		
	    if(StringUtils.isNotBlank(opid)){
	    	sql+=" and base_label.opid in ( "+opid+" )";
	    }
		
		String printopid = baseLabelDetail.getPrintId(); //打印人
		if(StringUtils.isNotBlank(printopid)){ //逗号分隔
			sql+=" and base_label.print_id in ( "+printopid+" )";
		}
		String printName = baseLabelDetail.getPrintName();
		if(StringUtils.isNotBlank(printName)){
			sql+=" and base_label.print_name like concat('%','"+printName +"','%') ";
			
		}
		Integer status = baseLabelDetail.getPrintStatus();
		if(null!=status){ //逗号分隔
			sql+=" and base_label.print_status = "+status;
		}
		
		Map<String, Object> params = baseLabelDetail.getParams();
		if(StringUtils.isNotEmpty(params)&& StringUtils.isNotBlank((String)params.get("beginTime"))){
			sql+=" AND date_format(base_label.update_time,'%y%m%d') >= date_format('"+params.get("beginTime")+"','%y%m%d')";
		}
		if(StringUtils.isNotEmpty(params) && StringUtils.isNotBlank((String)params.get("endTime"))){
			sql+=" AND date_format(base_label.update_time,'%y%m%d') <= date_format('"+params.get("endTime")+"','%y%m%d')";
		}
		
		PageBean<BaseLabelDetail> result= jdbcTemplateSupport.queryForPage(sql, pagination, new BeanPropertyRowMapper<BaseLabelDetail>(BaseLabelDetail.class));
		
		return result;
	}

	/*
	 * 批量更新
	 */
	@Override
	public void updateBaseLabel(List<BaseLabelDetail> list) {
		
		String opname=null;
		try {
			UserInfo userInfo = ShiroUtils.getUserInfo();
			if(StringUtils.isNotNull(userInfo)){ //消息入库时无用户信息
				String opid = userInfo.getOpid();
				opname = userInfo.getOpname();
			}
		} catch (UnavailableSecurityManagerException e) {
			System.err.println("此请求来自消息监听！");
		}
		for (BaseLabelDetail baseLabelDetail : list) {
			baseLabelDetail.setAlertName(opname); //修改人
			insert(baseLabelDetail, true);
		}
	}

	@Override
	public Integer updateBaseLabelById(BaseLabelDetail baseLabelDetail) {
		return super.updateById(baseLabelDetail, baseLabelDetail.getId(), true);
	}

	
	
}
