package com.bondex.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.bondex.entity.Label;
import com.bondex.entity.LabelAndTemplate;
import com.bondex.entity.Region;
import com.bondex.shiro.security.entity.Opid;
import com.bondex.shiro.security.entity.UserInfo;

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
	 * 调用客户端，打印标签
	 * 
	 * @param region
	 * @param labels
	 * @param info
	 * @param opid
	 * @param thisUsername
	 * @param report
	 * 
	 * @return
	 */

	void sendLabel(List<LabelAndTemplate> labelAndTemplates, String regionCode, String mqaddress);

	List<Label> getLabel(String labels);

	Object postRegion(String code,Integer curPage,Integer pageSize);

	Region getDefaultBindRegionByOpid(String regionid, String opid);

	List<Opid> getOpidName(String param,Integer page,Integer limit);




}
