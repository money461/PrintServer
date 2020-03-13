package com.bondex.client.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.bondex.client.dao.ClientDao;
import com.bondex.client.entity.Region;
import com.bondex.client.entity.UserDefaultRegion;
import com.bondex.common.enums.ResEnum;
import com.bondex.jdbc.entity.Label;
import com.bondex.mapper.AdminDataCurrentMapper;
import com.bondex.res.AjaxResult;
import com.bondex.res.MsgResult;
import com.bondex.security.ApplicationContextProvider;
import com.bondex.util.StringUtils;

@Component
public class ClientDaoImpl implements ClientDao {
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

	@Override
	public void update(String string) {
		jdbcTemplate.update(string, new Object[] {});
	}

	@Override
	public List<Label> getLabel(String label) {
		return jdbcTemplate.query("select * from label where label_id in(" + label + ")", new Object[] {}, new BeanPropertyRowMapper<Label>(Label.class));
	}

	@Override
	public List<UserDefaultRegion> selectUserDefaultRegion(UserDefaultRegion userDefaultRegion) {
		if(StringUtils.isNull(userDefaultRegion)){
			return null;
		}
		String sql="SELECT	d.id, d.opid, d.username,	d.default_region_code,	d.office_id, d.type,d.createTime,r.region_id,r.region_code,	r.region_name,	r.parent_code,	r.parent_name FROM 	default_region d LEFT JOIN region r   ON	d.default_region_code=r.region_code	 WHERE	1=1 ";
		String opid = userDefaultRegion.getOpid();
		if(StringUtils.isNotBlank(opid)){
			sql+="and d.opid = '"+opid+"' ";
		}
		if(StringUtils.isNotBlank(userDefaultRegion.getUsername())){
			sql+="and d.username like '%"+userDefaultRegion.getUsername()+"%' ";
		}
		String region_code = userDefaultRegion.getRegion_code();
		if(StringUtils.isNotBlank(region_code)){
			//当ztree点击树的父节点时 region_code=chengdu
			Region regionJDBC = new Region();
			regionJDBC.setParent_code(region_code);
			List<Region> allParentRegion = getALLParentRegion(regionJDBC); //判断region_code该值是否为父节点code
			if(StringUtils.isEmpty(allParentRegion)){
				sql+="and r.region_code = '"+region_code+"' ";
			}else{
				sql+="and r.parent_code =  '"+region_code+"' ";
			}
		}
		if(StringUtils.isNotBlank(userDefaultRegion.getRegion_name())){
			sql+="and r.region_name like '%"+userDefaultRegion.getRegion_name()+"%' ";
		}
		if(StringUtils.isNotBlank(userDefaultRegion.getParent_code())){
			sql+="and r.parent_code = '"+userDefaultRegion.getParent_code()+"' ";
		}
		if(StringUtils.isNotBlank(userDefaultRegion.getParent_name())){
			sql+="and r.parent_name like '%"+userDefaultRegion.getParent_name()+"%' ";
		}
		
		 sql+="and d.type = '"+userDefaultRegion.getType()+"' "; //默认值0
		
		 List<UserDefaultRegion> defaultRegions =  adminDataCurrentMapper.selectUserDefaultRegion(sql);
		 
		return defaultRegions;
	}

	/**
	 *根据opid获取当前用户绑定的办公室信息
	 */
	@Override
	public Region getDefaultBindRegionByOpid(String opid) {
		List<Region> defaultRegions = jdbcTemplate.query("select * from region where region_code = (select default_region_code from default_region where opid = ? and type = 0)", new Object[] { opid }, new BeanPropertyRowMapper<Region>(Region.class));
		if (defaultRegions.isEmpty()) {
			return null;
		} else {
			return defaultRegions.get(0);
		}
	}
	

	//根据办公室id获取区域信息
	@Override
	public Region getRegionById(String region_code) {
		List<Region> defaultRegions = jdbcTemplate.query("select * from region where region_code = ?", new Object[] { region_code }, new BeanPropertyRowMapper<Region>(Region.class));
		return defaultRegions.get(0);
	}

	
	//批量添加用户办公地址信息表 用于树形展示 type=0
	/**
	 * 批量更新或者添加办公室
	 */
	@Override
	public Object addOrUpdateRegion(List<Region> regionlist) {
		
		return MsgResult.nodata(ResEnum.SUCCESS.CODE, ResEnum.SUCCESS.MESSAGE);

	}

	//更新用户信息和办公室信息 
	/**
	 * region chengdu/jichang
	 */
	@Override
	public Object updateOrAddUserRegion(UserDefaultRegion userDefaultRegion) {
		if(StringUtils.isNotNull(userDefaultRegion)){
			
			String sql="INSERT INTO default_region (opid,office_id,username,default_region_code,type) VALUES (?,?,?,?,?)"
					+"ON DUPLICATE KEY UPDATE "
					+"office_id=VALUES(office_id),username=VALUES(username),default_region_code=VALUES(default_region_code),type=VALUES(type)";
			
			int i = jdbcTemplate.update(sql,new Object[] { userDefaultRegion.getOpid(),userDefaultRegion.getRegion_id(),userDefaultRegion.getUsername(),userDefaultRegion.getDefault_region_code(),userDefaultRegion.getType()});
			return i;
		}
		
		return 0;
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

	//校验表中opid唯一
	@Override
	public Integer checkOpidUnique(String opid) {
		String  sql = "select count(*) from  default_region WHERE opid =?";
		Integer i = jdbcTemplate.queryForObject(sql, new Object[]{opid},Integer.class);
		return i;
	}

	


}
