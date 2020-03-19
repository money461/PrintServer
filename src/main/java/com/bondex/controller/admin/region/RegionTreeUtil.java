package com.bondex.controller.admin.region;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bondex.entity.Region;
import com.bondex.entity.tree.Ztree;
import com.bondex.util.StringUtils;

/**
 * 
 * @author Qianli
 *  生成标准的ztree结构
 */
public class RegionTreeUtil {
	
	public static List<Map<String, Object>> treeSelectOptionMap(List<Region> treeSelectRegionData) {
		
		 List<Map<String,Object>> treeList  =new ArrayList<Map<String,Object>>();
		
		for (Region region : treeSelectRegionData) {
			LinkedHashMap<String, Object> linkedHashMap = null;
			if (StringUtils.isBlank(region.getParent_code())){ //最高级父节点开始循环
				linkedHashMap = selectOptionssMap(treeSelectRegionData,linkedHashMap,region);
			} 
			if (StringUtils.isNotEmpty(linkedHashMap)){
				treeList.add(linkedHashMap);
			}
		}
		
		return treeList;
	}

	
	
	  /**
     * 下拉菜单树map集合数据
     * @param map
     * @param permission
     * @return
     */
    public static LinkedHashMap<String, Object> selectOptionssMap(List<Region> treeSelectRegionData,LinkedHashMap<String,Object> map, Region region){
    	    map = new LinkedHashMap<String, Object>();
            map.put("id", region.getRegion_code());
            map.put("name",region.getRegion_name());
            map.put("open", true);//展开
            String region_code = region.getRegion_code();
            if(StringUtils.isNotBlank(region_code)){
            	map.put("children", createOpionChildren(treeSelectRegionData,region_code));
            }
        return map;
    }


    /**
     * 递归设置下拉树
     * @param parent_code
     * @return
     */
    public static List<LinkedHashMap<String, Object>> createOpionChildren(List<Region> treeSelectRegionData,String parent_code) {
    	
    	List<Region> childrenList = treeSelectRegionData.stream().filter(x->parent_code.equals(x.getParent_code())).collect(Collectors.toList());
    	
        List<LinkedHashMap<String, Object>> childList = new ArrayList<LinkedHashMap<String, Object>>();
        for (Region region:childrenList) {
        	LinkedHashMap<String, Object> map = null;
            if (region.getParent_code().equals(parent_code)) {
            	map = selectOptionssMap(treeSelectRegionData,map,region);
            	}
            if (StringUtils.isNotEmpty(map)){
            	childList.add(map);
            	}
        }
        return childList;
    }


	public static List<Ztree> ztreeOptionMap(List<Ztree> treeAllRegionData) {
		ArrayList<Ztree> arrayList = new ArrayList<Ztree>();
		for (Ztree bean : treeAllRegionData) {
			if (StringUtils.isBlank(bean.getpId())){ //最高级父节点开始循环
				bean.setOpen(true);//展开
				List<Ztree> list = buildTree(treeAllRegionData, bean.getId());
				bean.setChildren(list);
				arrayList.add(bean);
			}
		}
		return arrayList;
	}
	
	/**
	 * 递归
	 * @param treeBeans 不包含最高层次节点的集合
	 * @param pId 父类id
	 * @return
	 */
	private static List<Ztree> buildTree(List<Ztree> treeBeans,String pId){
		
		List<Ztree> result = new ArrayList<>();
		
		for (Ztree treeBean : treeBeans) {
			String id = treeBean.getId();
			String pid = treeBean.getpId();
			if(StringUtils.isNotBlank(pid)){
				if(pid.equals(pId)){
					 //递归查询当前子节点的子节点
					List<Ztree> childrenTree = buildTree(treeBeans, id);
					treeBean.setChildren(childrenTree);
					result.add(treeBean);
				}
			}
		}
		
		return result;
		
	}
	
	
	
}
