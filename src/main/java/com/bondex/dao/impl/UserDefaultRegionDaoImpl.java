package com.bondex.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bondex.config.jdbc.JdbcTemplateSupport;
import com.bondex.dao.RegionDao;
import com.bondex.dao.UserDefaultRegionDao;
import com.bondex.dao.base.BaseDao;
import com.bondex.entity.Region;
import com.bondex.entity.UserDefaultRegion;
import com.bondex.mapper.AdminDataCurrentMapper;
import com.bondex.util.StringUtils;

@Component
public class UserDefaultRegionDaoImpl extends BaseDao<UserDefaultRegion, Integer> implements UserDefaultRegionDao {
	
	private JdbcTemplateSupport jdbcTemplateSupport;
	
	@Autowired
	public UserDefaultRegionDaoImpl(JdbcTemplateSupport jdbcTemplate) {
		super(jdbcTemplate);
		this.jdbcTemplateSupport  = jdbcTemplate;
	}



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
		String sql="SELECT	d.id, d.opid, d.opid_name,	d.default_region, d.type,d.create_time,d.update_time,r.region_id,r.region_code,	r.region_name,	r.parent_code,	r.parent_name FROM 	default_region d LEFT JOIN region r   ON	d.default_region=r.region_code	 WHERE	1=1 ";
		
		Integer id = userDefaultRegion.getId();
		if(StringUtils.isNotNull(id)){
			sql+="and d.id = "+id;
		}
		
		String opid = userDefaultRegion.getOpid();
		if(StringUtils.isNotBlank(opid)){
			sql+="and d.opid = '"+opid+"' ";
		}
		if(StringUtils.isNotBlank(userDefaultRegion.getOpidName())){
			sql+="and d.opid_name like '%"+userDefaultRegion.getOpidName()+"%' ";
		}
		String region_code = userDefaultRegion.getRegionCode();
		if(StringUtils.isNotBlank(region_code)){
			//当ztree点击树的父节点时 region_code=chengdu
			Region regionJDBC = new Region();
			regionJDBC.setParentCode(region_code);
			List<Region> allParentRegion = regionDao.getALLParentRegion(regionJDBC); //判断region_code该值是否为父节点code
			if(StringUtils.isEmpty(allParentRegion)){
				sql+="and r.region_code = '"+region_code+"' ";
			}else{
				sql+="and r.parent_code =  '"+region_code+"' ";
			}
		}
		if(StringUtils.isNotBlank(userDefaultRegion.getRegionName())){
			sql+="and r.region_name like '%"+userDefaultRegion.getRegionName()+"%' ";
		}
		if(StringUtils.isNotBlank(userDefaultRegion.getParentCode())){
			sql+="and r.parent_code = '"+userDefaultRegion.getParentCode()+"' ";
		}
		if(StringUtils.isNotBlank(userDefaultRegion.getParentName())){
			sql+="and r.parent_name like '%"+userDefaultRegion.getParentName()+"%' ";
		}
		
		if(StringUtils.isNotNull(userDefaultRegion.getType())){
			sql+="and d.type = '"+userDefaultRegion.getType()+"' "; //默认值0
		}
		
		 List<UserDefaultRegion> defaultRegions =  adminDataCurrentMapper.selectUserDefaultRegion(sql);
		 
		return defaultRegions;
	}
	
	

	//更新用户信息和办公室信息 
	/**
	 * region chengdu/jichang
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer updateOrAddUserRegion(UserDefaultRegion userDefaultRegion) {
		if(StringUtils.isNotNull(userDefaultRegion)){
			
			String sql="INSERT INTO default_region (opid,opid_name,default_region,type,create_time) VALUES (?,?,?,?,NOW())"
					+"ON DUPLICATE KEY UPDATE "
					+" opid_name=VALUES(opid_name),default_region=VALUES(default_region),type=VALUES(type)";
			
			int i = jdbcTemplate.update(sql,new Object[] { userDefaultRegion.getOpid(),userDefaultRegion.getOpidName(),userDefaultRegion.getDefaultRegion(),userDefaultRegion.getType()});
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



	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer changeStatus(UserDefaultRegion userDefaultRegion) {
		
		UserDefaultRegion user = super.findOneById(userDefaultRegion.getId());
		user.setType(userDefaultRegion.getType());
		Integer integer = super.insertforUpdate(user, true);
		return integer;
	}

	

}
