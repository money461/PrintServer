package com.bondex.client.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.bondex.client.dao.ClientDao;
import com.bondex.client.entity.DefaultRegion;
import com.bondex.client.entity.Region;
import com.bondex.client.entity.yml.RegionJDBC;
import com.bondex.common.enums.ResEnum;
import com.bondex.jdbc.entity.Label;
import com.bondex.res.MsgResult;
import com.bondex.security.ApplicationContextProvider;
import com.bondex.security.entity.UserInfo;
import com.bondex.shiro.security.ShiroUtils;

@Component
public class ClientDaoImpl implements ClientDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 根据用户opid查询办公室下拉树
	 */
	@Override
	public List<RegionJDBC> getRegionByOpid(String opid) {
		//测试环境下查询所有的用户
		if("test".equals(ApplicationContextProvider.getActiveProfile())){
			return getALLRegion();
		}
		return jdbcTemplate.query("SELECT * FROM region WHERE parent_code = ( SELECT parent_code FROM region WHERE region_id = ( SELECT office_id FROM default_region WHERE opid = ? and type = 0) )", new Object[] { opid }, new BeanPropertyRowMapper<RegionJDBC>(RegionJDBC.class));
	}

	/**
	 * 获取所有的区域办公室
	 */
	@Override
	public List<RegionJDBC> getALLRegion() {
		return jdbcTemplate.query("select * from region", new Object[] {}, new BeanPropertyRowMapper<RegionJDBC>(RegionJDBC.class));
	}

	
	//似乎没有使用
	@Override
	public List<RegionJDBC> search(String q) {
		return jdbcTemplate.query("select * from region where region_name like '%" + q + "%' or region_code like '%" + q + "%'or parent_code like '%" + q + "%'or parent_name like '%" + q + "%'", new Object[] {}, new BeanPropertyRowMapper<RegionJDBC>(RegionJDBC.class));
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
	public DefaultRegion getDefaultRegionByOpid(String opid) {
	    List<DefaultRegion> defaultRegions = jdbcTemplate.query("select * from default_region where opid = ? and type = 0", new Object[] { opid }, new BeanPropertyRowMapper<DefaultRegion>(DefaultRegion.class));
	    if(defaultRegions.isEmpty()){
	    	return null;
	    }
		return defaultRegions.get(0);
	}

	

	//根据办公室id获取区域信息
	@Override
	public Region getRegionById(String regionid) {
		List<Region> defaultRegions = jdbcTemplate.query("select * from region where region_id = ?", new Object[] { regionid }, new BeanPropertyRowMapper<Region>(Region.class));
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
	public Object updateOrAddUserRegion(String regionid) {
		UserInfo userInfo = ShiroUtils.getUserInfo();
		String opid = userInfo.getOpid();
		String sql="INSERT INTO default_region (opid,office_id,username,default_region_id,type) VALUES (?,?,?,?,?)"
			+"ON DUPLICATE KEY UPDATE "
			+"office_id=VALUES(office_id),username=VALUES(username),default_region_id=VALUES(default_region_id),type=VALUES(type)";
		jdbcTemplate.update(sql,new Object[] { opid,regionid,userInfo.getOpname(),null,0});
		
		return MsgResult.nodata(ResEnum.SUCCESS.CODE, ResEnum.SUCCESS.MESSAGE);
	}

	/**
	 *根据opid获取当前用户绑定的办公室信息
	 */
	@Override
	public Region getDefaultBindRegionByOpid(String opid) {
		List<Region> defaultRegions = jdbcTemplate.query("select * from region where region_id = (select office_id from default_region where opid = ? and type = 0)", new Object[] { opid }, new BeanPropertyRowMapper<Region>(Region.class));
		if (defaultRegions.isEmpty()) {
			return null;
		} else {
			return defaultRegions.get(0);
		}
	}



}
