package com.bondex.client.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.bondex.client.entity.DefaultRegion;
import com.bondex.client.entity.Region;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.LabelAndTemplate;
import com.bondex.security.entity.Opid;
import com.bondex.security.entity.UserInfo;

public interface ClientService {

	/**
	 * 导出标签 PDF
	 * @param list
	 * @param report
	 * @param userInfo
	 * @return
	 * @throws IOException
	 */
	List<InputStream> getPDF(List<Label> list, UserInfo userInfo) throws IOException;

	/**
	 * 根据用户opid获取办公室区域 并封装为tree
	 * 
	 * @return
	 */
	String getTreeRegionByOpid(String opid);

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
	void sendLabel(List<LabelAndTemplate> labelList, String regionid, UserInfo userInfo, String businessType,String mqaddress);

	String searchRegion(String q);

	List<Label> getLabel(String labels);

	DefaultRegion getDefaultRegionByOpid(String opid);

	Object updateOrAddUserRegion(String region);

	Object postRegion(String code,Integer curPage,Integer pageSize);

	Region getDefaultBindRegionByOpid(String regionid, String opid);

	List<Opid> getOpidName(String param,Integer page,Integer limit);

}
