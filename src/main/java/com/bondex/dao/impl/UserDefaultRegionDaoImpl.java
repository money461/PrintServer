package com.bondex.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.bondex.dao.RegionDao;
import com.bondex.dao.UserDefaultRegionDao;
import com.bondex.entity.Region;
import com.bondex.entity.UserDefaultRegion;
import com.bondex.mapper.AdminDataCurrentMapper;
import com.bondex.util.StringUtils;

@Component
public class UserDefaultRegionDaoImpl implements UserDefaultRegionDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	@Autowired
	private RegionDao regionDao;
	
	@Autowired
	private  AdminDataCurrentMapper adminDataCurrentMapper;
	
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
			List<Region> allParentRegion = regionDao.getALLParentRegion(regionJDBC); //判断region_code该值是否为父节点code
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
	
	

	//更新用户信息和办公室信息 
	/**
	 * region chengdu/jichang
	 */
	@Override
	public Integer updateOrAddUserRegion(UserDefaultRegion userDefaultRegion) {
		if(StringUtils.isNotNull(userDefaultRegion)){
			
			String sql="INSERT INTO default_region (opid,office_id,username,default_region_code,type) VALUES (?,?,?,?,?)"
					+"ON DUPLICATE KEY UPDATE "
					+"office_id=VALUES(office_id),username=VALUES(username),default_region_code=VALUES(default_region_code),type=VALUES(type)";
			
			int i = jdbcTemplate.update(sql,new Object[] { userDefaultRegion.getOpid(),userDefaultRegion.getRegion_id(),userDefaultRegion.getUsername(),userDefaultRegion.getDefault_region_code(),userDefaultRegion.getType()});
			return i;
		}
		
		return 0;
	}
	
	

	//校验表中opid唯一
	@Override
	public Integer checkOpidUnique(String opid) {
		String  sql = "select count(*) from  default_region WHERE opid =?";
		Integer i = jdbcTemplate.queryForObject(sql, new Object[]{opid},Integer.class);
		return i;
	}

	

}
