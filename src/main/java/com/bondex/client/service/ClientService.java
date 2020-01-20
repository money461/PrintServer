package com.bondex.client.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.LabelAndTemplate;
import com.bondex.security.entity.Opid;
import com.bondex.security.entity.UserInfo;
import com.github.pagehelper.Page;

public interface ClientService {

	/**
	 * 导出标签 PDF
	 * @param list
	 * @param report
	 * @param userInfo
	 * @return
	 * @throws IOException
	 */
	List<InputStream> getPDF(List<Label> list, String report, UserInfo userInfo) throws IOException;

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
	void sendLabel(List<LabelAndTemplate> labelList, String region, UserInfo userInfo, String report, String businessType);

	String searchRegion(String q);

	List<Label> getLabel(String labels);

	String isFrist(String opid);

	String addDR(String opid, String region);

	void updateRn(String region, String opid);

	String postRegion(String szm);

	String getThisRegion(String code, String opid);

	List<Opid> getOpidName(String param,Integer page,Integer limit);

}
