package com.bondex.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.bondex.dao.RegionDao;
import com.bondex.entity.Region;
import com.bondex.entity.res.AjaxResult;
import com.bondex.mapper.AdminDataCurrentMapper;
import com.bondex.util.StringUtils;
import com.bondex.util.context.ApplicationContextProvider;

@Component
public class RegionDaoImpl implements RegionDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private  AdminDataCurrentMapper adminDataCurrentMapper;


	/**
	 * 根据用户opid查询办公室下拉树
	 */
	@Override
	public List<Region> getRegionByOpid(String opid) {
		//测试环境下查询所有的用户
		String activeProfile = ApplicationContextProvider.getActiveProfile();
		if("test".equals(activeProfile) || "dev".equals(activeProfile)){
			return getALLRegion(null);
		}
		
		return jdbcTemplate.query("SELECT * FROM region WHERE parent_code = ( SELECT parent_code FROM region WHERE region_code = ( SELECT default_region_code FROM default_region WHERE opid = ? and type = 0) )", new Object[] { opid }, new BeanPropertyRowMapper<Region>(Region.class));
	}

	/**
	 * 获取所有的区域办公室
	 */
	@Override
	public List<Region> getALLRegion(Region regionJDBC) {
			String sql="select * from region where 1=1 ";
		  if(StringUtils.isNotNull(regionJDBC)){
			  String region_code = regionJDBC.getRegion_code();
			  String region_name = regionJDBC.getRegion_name();
			  String parent_name = regionJDBC.getParent_name();
			  if(StringUtils.isNotBlank(region_code)){
				  sql+="and region_code = '"+region_code+"'";
				  
			  }
			  if(StringUtils.isNotBlank(region_name)){
				  sql+="and region_name like '%"+region_name+"%'";
			  }
			  if(StringUtils.isNotBlank(parent_name)){
				  sql+="and parent_name like '%"+parent_name+"%'";
			  }
		  }
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Region>(Region.class));
	}
	
	
	/**
	 * 获取所有的父级区域
	 */
	@Override
	public List<Region> getALLParentRegion(Region regionJDBC) {
		String sql = "SELECT   parent_code AS region_code,parent_name AS region_name FROM region where 1=1 ";
		if(StringUtils.isNotNull(regionJDBC)){
			String parent_code = regionJDBC.getParent_code();
			String region_name = regionJDBC.getRegion_name();
			String parent_name = regionJDBC.getParent_name();
			
			if(StringUtils.isNotBlank(parent_code)){
				sql+="and parent_code = '"+parent_code+"'";
			}
			if(StringUtils.isNotBlank(region_name)){
				sql+="and region_name = '"+region_name+"'";
			}
			if(StringUtils.isNotBlank(parent_name)){
				sql+="and parent_name = '"+parent_name+"'";
			}
			
		}
		
		sql+=" GROUP BY parent_code";
		
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Region>(Region.class));
	}
	
	
	//似乎没有使用
	@Override
	public List<Region> search(String q) {
		return jdbcTemplate.query("select * from region where region_name like '%" + q + "%' or region_code like '%" + q + "%'or parent_code like '%" + q + "%'or parent_name like '%" + q + "%'", new Object[] {}, new BeanPropertyRowMapper<Region>(Region.class));
	}
	

	//校验唯一性region_code后台返回0 不存在校验通过    返回1 存在校验不通过
	@Override
	public int checkregionCodeUnique(Region regionJDBC) {
		if(StringUtils.isNull(regionJDBC)){
			return 1;
		}
		
		if( StringUtils.isNotBlank(regionJDBC.getRegion_code()) ){
			//校验
			String sql="select count(*) from region where region_code=?";
			Long re = jdbcTemplate.queryForObject(sql,new Object[]{regionJDBC.getRegion_code()},long.class);
			if(1==re){
				return 1;
			}
		}
		if(StringUtils.isNotBlank(regionJDBC.getParent_code()) && StringUtils.isNotBlank(regionJDBC.getRegion_name())){ //办公室名称不能相同
			//校验
			String sql="select count(*) from region where region_name = ? and parent_code = ?";
			Long re = jdbcTemplate.queryForObject(sql,new Object[]{regionJDBC.getRegion_name(),regionJDBC.getParent_code()},long.class);
			if(1==re){
				return 1;
			}
		}
		//parent_code与parent_name 要么同时存在 要么同时不存在
		if(StringUtils.isNotBlank(regionJDBC.getParent_code()) && StringUtils.isNotBlank(regionJDBC.getParent_name()) ){ //区域parent_Code不存在的时候 区域名称也不可以相同
			
			//校验
			String sql="select count(*) from region where parent_code = ? and parent_name = ?";
			Long re = jdbcTemplate.queryForObject(sql,new Object[]{regionJDBC.getParent_code(),regionJDBC.getParent_name()},long.class);
			//同时都存在 校验通过 
			
			//同时都不存在
			sql="select count(*) from region where parent_code = ? or parent_name = ?";
			Long re2 = jdbcTemplate.queryForObject(sql,new Object[]{regionJDBC.getParent_code(),regionJDBC.getParent_name()},long.class);
			if(re>0 || re2==0){
				//同时存在 要么同时不存在 校验通过 
			}else{
				return 1;
			}
			
		}
		
		return 0;
	}

	//更新保存办公室
	@Override
	public Object saveOrUpdateRegion(Region regionJDBC) {
		String sql="INSERT INTO region (region_name,region_code,parent_code,parent_name) VALUES (?,?,?,?)"
				+"ON DUPLICATE KEY UPDATE "
				+"region_name=VALUES(region_name),region_code=VALUES(region_code),parent_code=VALUES(parent_code), parent_name = VALUES(parent_name)";
		int i = jdbcTemplate.update(sql,new Object[]{regionJDBC.getRegion_name(),regionJDBC.getRegion_code(),regionJDBC.getParent_code(),regionJDBC.getParent_name()});
		if(1==i){
			return AjaxResult.success();
		}
		
		return AjaxResult.error("操作失败！");
	}

}
