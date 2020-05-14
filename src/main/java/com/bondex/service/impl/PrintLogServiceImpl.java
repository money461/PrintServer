package com.bondex.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bondex.dao.CurrentLabelDao;
import com.bondex.dao.PrintLogDao;
import com.bondex.entity.current.BaseLabelDetail;
import com.bondex.entity.log.PrintLog;
import com.bondex.entity.page.PageBean;
import com.bondex.service.PrintLogService;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
@Service
public class PrintLogServiceImpl implements PrintLogService {

	@Autowired
	private PrintLogDao printLogDao;
	
	@Autowired
	private CurrentLabelDao currentLabelDao;
	
	@Override
	public PageBean<PrintLog> getPrintlogDetail(PrintLog printLog) {
		
		return printLogDao.getPrintlogDetail(printLog);
	}

	@Override
	public List<JSONObject> getChildView(PrintLog printLog) {
		List<BaseLabelDetail> list = currentLabelDao.selectBaseLabelListByInId(printLog.getLabelId());
		PageBean<PrintLog> pageBean = printLogDao.getPrintlogDetail(printLog);
		List<PrintLog> list2 = pageBean.getList();
		PrintLog printLog2 = list2.get(0);
		JSONObject parseObj2 = JSONUtil.parseObj(printLog2);
		ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
		for (BaseLabelDetail baseLabelDetail2 : list) {
			JSONObject parseObj = JSONUtil.parseObj(baseLabelDetail2);
			parseObj2.putAll(parseObj);
			arrayList.add(parseObj2);
		}
		return arrayList;
	}

}
