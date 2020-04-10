package com.bondex.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.bondex.common.enums.ResEnum;
import com.bondex.dao.ClientDao;
import com.bondex.entity.Label;
import com.bondex.entity.Region;
import com.bondex.entity.res.MsgResult;

@Component
public class ClientDaoImpl implements ClientDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	


	@Override
	public void update(String string) {
		jdbcTemplate.update(string, new Object[] {});
	}

	@Override
	public List<Label> getLabel(String label) {
		return jdbcTemplate.query("select * from label where label_id in(" + label + ")", new Object[] {}, new BeanPropertyRowMapper<Label>(Label.class));
	}



	/**
	 *根据opid获取当前用户绑定的办公室信息
	 */
	@Override
	public Region getDefaultBindRegionByOpid(String opid) {
		List<Region> defaultRegions = jdbcTemplate.query("select * from region where region_code = (select default_region from default_region where opid = ? and type = 0)", new Object[] { opid }, new BeanPropertyRowMapper<Region>(Region.class));
		if (defaultRegions.isEmpty()) {
			return null;
		} else {
			return defaultRegions.get(0);
		}
	}
	

	//根据办公室id获取区域信息
	@Override
	public Region getRegionByRegioncode(String region_code) {
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


	


}
