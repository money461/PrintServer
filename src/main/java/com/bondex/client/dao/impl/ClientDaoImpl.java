package com.bondex.client.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.bondex.client.dao.ClientDao;
import com.bondex.client.entity.DefaultRegion;
import com.bondex.client.entity.Region;
import com.bondex.client.entity.yml.RegionJDBC;
import com.bondex.jdbc.entity.Label;
import com.bondex.util.GsonUtil;

@Component
public class ClientDaoImpl implements ClientDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<RegionJDBC> getAll(String opid) {
		return jdbcTemplate.query("SELECT * FROM region WHERE parent_code = ( SELECT parent_code FROM region WHERE region_id = ( SELECT office_id FROM default_region WHERE opid = ? and type = 0) )", new Object[] { opid }, new BeanPropertyRowMapper<RegionJDBC>(RegionJDBC.class));
	}

	@Override
	public List<RegionJDBC> getRegionName() {
		return jdbcTemplate.query("select * from region", new Object[] {}, new BeanPropertyRowMapper<RegionJDBC>(RegionJDBC.class));
	}

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
	public String isFrist(String opid) {
		List<DefaultRegion> defaultRegions = jdbcTemplate.query("select * from default_region where opid = ? and type = 0", new Object[] { opid }, new BeanPropertyRowMapper<DefaultRegion>(DefaultRegion.class));
		if (defaultRegions.isEmpty()) {
			//当第一次使用的时候，需要配置模板。
			return "false";
		} else {
			//返回模板信息
			return GsonUtil.GsonString(defaultRegions);
		}
	}

	@Override
	public String addDR(String opid, String region) {
		try {
			jdbcTemplate.execute("INSERT INTO default_region(opid,office_id,type) values('" + opid + "','" + region + "',0)");
			return "true";
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "false";
		}

	}

	@Override
	public Region getRegion(String region) {
		List<Region> defaultRegions = jdbcTemplate.query("select * from region where region_id = ?", new Object[] { region }, new BeanPropertyRowMapper<Region>(Region.class));
		return defaultRegions.get(0);
	}

	@Override
	public String getRegionId(String string) {
		List<Region> defaultRegions = jdbcTemplate.query("select * from region where region_code = ?", new Object[] { string }, new BeanPropertyRowMapper<Region>(Region.class));
		return String.valueOf(defaultRegions.get(0).getRegion_id());
	}

	@Override
	public void updateRn(String region, String opid) {
		jdbcTemplate.execute("call updateRegion('" + opid + "','" + region.split("/")[1] + "')");
	}

	@Override
	public Region getThisRegionForOpid(String opid) {
		List<Region> defaultRegions = jdbcTemplate.query("select * from region where region_id = (select office_id from default_region where opid = ? and type = 0)", new Object[] { opid }, new BeanPropertyRowMapper<Region>(Region.class));
		if (defaultRegions.isEmpty()) {
			Region region = new Region();
			region.setParent_name("暂无");
			region.setRegion_name("暂无");
			return region;
		} else {
			return defaultRegions.get(0);
		}
	}

	@Override
	public Region getThisRegion(String code) {
		List<Region> defaultRegions = jdbcTemplate.query("select * from region where region_code = ?", new Object[] { code.split("/")[1] }, new BeanPropertyRowMapper<Region>(Region.class));
		return defaultRegions.get(0);
	}

}
