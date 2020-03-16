package com.bondex.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		List<TreeBean> treeBeans = new ArrayList<>();
		List<TreeBean> prent = new ArrayList<>();
		Set<String> set = new HashSet<>();
		TreeBean treeBean = null;
		for (Region regionJDBC : regionJDBCs) {
			set.add(regionJDBC.getParent_name());
			treeBean = new TreeBean();
			treeBean.setId(regionJDBC.getRegion_code());
			treeBean.setRegion_code(regionJDBC.getRegion_code());
			treeBean.setText(regionJDBC.getRegion_name());
			treeBean.setParent_code(regionJDBC.getParent_code());
			treeBean.setPname(regionJDBC.getParent_name());
			treeBeans.add(treeBean);
		}
		
		// 设置父节点
		for (String pname : set) {
			treeBean = new TreeBean();
			treeBean.setText(pname); 
			treeBean.setState("closed");
			List<TreeBean> childrens = new ArrayList<>();
			
			for (TreeBean treeBean1 : treeBeans) {
				if (treeBean1.getPname().equals(pname)) {
					childrens.add(treeBean1);
				}
			}
			
			treeBean.setChildren(childrens); //存入子节点
			prent.add(treeBean); //存入所有的节点
		}
		logger.debug(GsonUtil.GsonString(prent));
		return prent;
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
