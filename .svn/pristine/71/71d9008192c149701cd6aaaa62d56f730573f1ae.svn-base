package com.bondex.client.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.bondex.jdbc.entity.Label;

public interface ClientService {

	List<InputStream> getPDF(List<Label> list, Map info, String report, String thisUsername, String opid) throws IOException;

	/**
	 * 获取所有办公区域
	 * 
	 * @return
	 */
	String getRegion(String opid);

	/**
	 * 调用客户端，打印标签
	 * 
	 * @param region
	 * @param labels
	 * @param info
	 * @param opid
	 * @param thisUsername
	 * @param report
	 * @param businessType
	 * 
	 * @return
	 */
	String sendLabel(String labels, String region, String thisUsername, String opid, Map info, String report, String businessType);

	String searchRegion(String q);

	List<Label> getLabel(String labels);

	String isFrist(String opid);

	String addDR(String opid, String region);

	void updateRn(String region, String opid);

	String postRegion(String szm);

	String getThisRegion(String code, String opid);

	String getOpidName(String string);

}
