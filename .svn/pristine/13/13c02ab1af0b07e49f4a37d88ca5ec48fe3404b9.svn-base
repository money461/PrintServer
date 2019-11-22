package com.bondex.client.dao;

import java.util.List;

import com.bondex.client.entity.DefaultRegion;
import com.bondex.client.entity.Region;
import com.bondex.client.entity.yml.RegionJDBC;
import com.bondex.jdbc.entity.Label;

public interface ClientDao {

	public List<RegionJDBC> getAll(String opid);

	/**
	 * 获取区域名称
	 * 
	 * @return
	 */
	public List<RegionJDBC> getRegionName();

	public List<RegionJDBC> search(String q);

	public void update(String string);

	public List<Label> getLabel(String label);

	public String isFrist(String opid);

	public String addDR(String opid, String region);

	public Region getRegion(String region);

	public String getRegionId(String string);

	public void updateRn(String region, String opid);

	public Region getThisRegion(String code);

	public Region getThisRegionForOpid(String opid);

}
