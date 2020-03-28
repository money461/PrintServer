package com.bondex.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bondex.dao.CurrentLabelDao;
import com.bondex.dao.PrintLogDao;
import com.bondex.entity.current.BaseLabelDetail;
import com.bondex.entity.log.PrintLog;
import com.bondex.entity.page.PageBean;
import com.bondex.service.PrintLogService;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
@Service
@Transactional(rollbackFor = Exception.class)
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
	public PageBean<JSONObject> getChildView(PrintLog printLog) {
		BaseLabelDetail baseLabelDetail = new BaseLabelDetail();
		baseLabelDetail.setId(printLog.getLabelId());
		PageBean<BaseLabelDetail> pageBean = currentLabelDao.selectBaseLabelList(baseLabelDetail, false);
		PageBean<PrintLog> printlogDetail = printLogDao.getPrintlogDetail(printLog);
		List<PrintLog> list2 = printlogDetail.getList();
		PrintLog printLog2 = list2.get(0);
		JSONObject parseObj2 = JSONUtil.parseObj(printLog2);
		long total = pageBean.getTotal();
		List<BaseLabelDetail> list = pageBean.getList();
		ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
		PageBean<JSONObject> pageBean2 = new PageBean<JSONObject>();
		pageBean2.setList(arrayList);
		pageBean2.setTotal(total);
		for (BaseLabelDetail baseLabelDetail2 : list) {
			JSONObject parseObj = JSONUtil.parseObj(baseLabelDetail2);
			parseObj2.putAll(parseObj);
			arrayList.add(parseObj2);
		}
		return pageBean2;
	}

}
