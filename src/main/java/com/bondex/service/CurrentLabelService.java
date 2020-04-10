package com.bondex.service;

import java.util.List;

import com.bondex.entity.current.BaseLabelDetail;

public interface CurrentLabelService {

	Object saveBaseLabelMsg(String message);
	
	List<BaseLabelDetail> selectBaseLabelList(BaseLabelDetail baseLabelDetail);

	void updateBaseLabel(List<BaseLabelDetail> list);

	void printCurrentLabelSendClient(List<BaseLabelDetail> list, String regionCode, String mqaddress);


}
