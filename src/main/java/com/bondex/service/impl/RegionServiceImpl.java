package com.bondex.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bondex.dao.RegionDao;
import com.bondex.entity.Region;
import com.bondex.entity.page.Datagrid;
import com.bondex.entity.tree.TreeBean;
import com.bondex.service.RegionService;
import com.bondex.util.GsonUtil;
@Service
@Transactional(rollbackFor = Exception.class)
public class RegionServiceImpl implements RegionService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RegionDao regionDao;
	/**
	 * 根据用户opid获取办公室区域 并封装为tree
	 */
	@Override
	public List<TreeBean> getTreeRegionByOpid(String opid) {
		//查询办公室
		List<Region> regionJDBCs = regionDao.getRegionByOpid(opid);
		
		return transferTree(regionJDBCs);
	}

	
	/*
	 * 树形转换
	 */
	private List<TreeBean> transferTree(List<Region> regionJDBCs){
		List<TreeBean> result = new ArrayList<>();
		
		List<TreeBean> treeBeans = new ArrayList<>();
		LinkedHashMap<String, String> parentMap = new LinkedHashMap<String,String>();
		for (Region regionJDBC : regionJDBCs) {
			TreeBean treeBean = new TreeBean();
			parentMap.put(regionJDBC.getParentCode(),regionJDBC.getParentName());
			treeBean.setId(regionJDBC.getRegionCode());
			treeBean.setParentCode(regionJDBC.getParentCode());
			treeBean.setRegionCode(regionJDBC.getRegionCode());
			treeBean.setText(regionJDBC.getRegionName());
			treeBean.setPname(regionJDBC.getParentName());
			treeBeans.add(treeBean);
		}
		
		
		// 设置父节点
		Iterator<Entry<String, String>> iterator = parentMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, String> next = iterator.next();
			String parent_code = next.getKey();
			String parent_name = next.getValue();
			TreeBean treeBean = new TreeBean();
			treeBean.setText(parent_name); 
			treeBean.setState("closed"); //父节点不展示子节点
			
			List<TreeBean> childrens = new ArrayList<>();
			
			for (TreeBean treeBean1 : treeBeans) {
				if (treeBean1.getParentCode().equals(parent_code)) {
					childrens.add(treeBean1);
				}
			}
			
			treeBean.setChildren(childrens); //存入子节点
			result.add(treeBean); //存入所有的节点
		}
		logger.debug(GsonUtil.GsonString(result));
		return result;
	}
	

	
	@Override
	public List<Region> getALLParentRegion(Region region) {
		return regionDao.getALLParentRegion(region);
	}

	@Override
	public List<Region> getALLRegion(Region region) {
		return regionDao.getALLRegion(region);
	}

	@Override
	public Object saveOrUpdateRegion(Region region) {
		return regionDao.saveOrUpdateRegion(region);
	}

	@Override
	public int checkregionCodeUnique(Region region) {
		return regionDao.checkregionCodeUnique(region);
	}

	@Override
	public String searchRegion(String q) {
		List<Region> regionJDBCs = regionDao.search(q);
		Datagrid<Region> datagrid = new Datagrid<Region>();
		datagrid.setRows(regionJDBCs);
		return GsonUtil.GsonString(datagrid);
	}
	
}
