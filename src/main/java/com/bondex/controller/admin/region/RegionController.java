package com.bondex.controller.admin.region;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.controller.BaseController;
import com.bondex.entity.Region;
import com.bondex.entity.tree.TreeBean;
import com.bondex.entity.tree.Ztree;
import com.bondex.service.RegionService;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.GsonUtil;
import com.bondex.util.StringUtils;
import com.bondex.util.shiro.ShiroUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(value="/region")
@Api(tags={"接口分组1"})
public class RegionController extends BaseController{
	
	@Autowired
	private RegionService  regionService;
	
	
	
	
	/**
	 * 进入办公室管理展示页面
	 * @param mawb
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value={"/view/{code}","/view"},method = RequestMethod.GET)
	@ResponseBody
//	@RequiresPermissions(value={"viewdata","look"},logical=Logical.OR)
	public ModelAndView indexpage(@PathVariable(name="code",required=false) String code,ModelAndView modelAndView){
		modelAndView.addObject("code", code);
		modelAndView.setViewName("/admin/region/regionlist"); //页面展示
		return modelAndView;
	}
	
	
	/**
	 * 获取数据库中所有的办公室
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.POST)
	@ResponseBody
	public List<Region> getAllRegion(Region Region) {
		//获取所有的办公室信息
		List<Region> allParentRegion = regionService.getALLParentRegion(Region);
		List<Region> allRegion = regionService.getALLRegion(Region);
		allParentRegion.addAll(allRegion);
		return allParentRegion;
	}
	


    /**
     * 新增办公室
     */
    @GetMapping("/add/{parentId}")
    public ModelAndView add(@PathVariable("parentId") String parent_code, ModelAndView modelAndView)
    {
    	Region re =new  Region(); 
    	re.setParentCode(parent_code);
    		//存在父级节点的查询
    	List<Region> allParentRegion = regionService.getALLParentRegion(re);
    	if(StringUtils.isNotEmpty(allParentRegion)){
    		re = allParentRegion.get(0);
    	}else{
    		re.setRegionName("");
    		re.setRegionCode("");
    	}
    	//不存在父级节点直接跳转页面
    	modelAndView.addObject("region",re);
    	modelAndView.setViewName("admin/region/add");
        return   modelAndView;
    }
    
    /**
     * 编辑办公室
     * @return
     */
    @GetMapping("/edit/{regionCode}")
    public ModelAndView edit(@PathVariable("regionCode") String regionCode, ModelAndView modelAndView){
    	Region region = new Region();
    	region.setRegionCode(regionCode);
    	List<Region> list = regionService.getALLRegion(region);
    	Region region2 = list.get(0);
    	modelAndView.addObject("region",region2);
    	modelAndView.setViewName("admin/region/edit");
        return   modelAndView;
    }
    
    /**
     * 初始化办公室树
     * @param region_code 被选中的节点  父节点 或者子节点
     * @param modelAndView
     * @return
     */
    @GetMapping("/selectregionTree/{region_code}")
    public ModelAndView selectregionTree(@PathVariable("region_code") String region_code, ModelAndView modelAndView){
    
    	Region re =new  Region(); 
    	re.setParentCode(region_code);
	    List<Region> allParentRegion = regionService.getALLParentRegion(re);
	    if(StringUtils.isEmpty(allParentRegion)){
	    	//子节点
	    	re.setRegionCode(region_code);
	    	List<Region> allRegion = regionService.getALLRegion(re);
	    	re = allRegion.get(0);
	    }else{
	    	re = allParentRegion.get(0);
	    }
	    
		modelAndView.addObject("region", re);
		modelAndView.setViewName("/admin/region/tree");
		return modelAndView;
    	
    }
    
    
    /**
     * 封装TreeSelect数据结构
     * @return
     */
    @RequestMapping(value="/treeSelectRegionData",method=RequestMethod.GET)
    @ResponseBody
    public Object getTreeSelectRegionData(){
    	//强制转换
//    	List<Region> treeSelectRegionData = getAllRegion(null); //获取所有的父类办公室
//    	List<Map<String,Object>> treeList  = RegionTreeUtil.treeSelectOptionMap(treeSelectRegionData);
    	
    	//根据 Ztree递归
    	List<Ztree> treeAllRegionData = getTreeAllRegionData();
    	List<Ztree> treeList= RegionTreeUtil.ztreeOptionMap(treeAllRegionData);
    	System.out.println(GsonUtil.GsonString(treeList));
		return treeList;
    }
    
    
    /**
     * 办公室树形展示
     * @return
     */
    @RequestMapping(value="/treeAllRegionData",method=RequestMethod.GET)
    @ResponseBody
    public List<Ztree> getTreeAllRegionData(){
    	List<Region> allParentRegion = getAllRegion(null); //获取所有的父类办公室
    	//封装未tree类型结构
    	List<Ztree> ztree = initZtree(allParentRegion);
    	return ztree;
    	
    }
    
    
    /**
     * 对象转部门树
     *
     * @param deptList 办公室列表
     * @param roleDeptList 角色已存在菜单列表
     * @return 树结构列表
     */
    public List<Ztree> initZtree(List<Region> allParentRegion)
    {
        List<Ztree> ztrees = new ArrayList<Ztree>();
        for (Region Region : allParentRegion)
        {
                Ztree ztree = new Ztree();
                ztree.setId(Region.getRegionCode());
                ztree.setpId(Region.getParentCode());
                ztree.setName(Region.getRegionName());
                ztrees.add(ztree);
        }
        return ztrees;
    }
    
    // 保证 region_code 全表唯一
    @RequestMapping(value="/checkregionCodeUnique")
    @ResponseBody
    public int checkregionCodeUnique(Region Region ){
    	return regionService.checkregionCodeUnique(Region);
    }
    
    //添加办公室
    @PostMapping(value="/saveOrUpdateRegion")
    @ResponseBody
    public Object saveOrUpdateRegion(Region Region ){
    	
    	return regionService.saveOrUpdateRegion(Region);
    }
    
    @RequestMapping(value="/search")
	@ResponseBody
	public String search(String q) {
		String regions = regionService.searchRegion(q);
		return regions;
	}
    

	/**
	 * 树形菜单展示区域
	 * @param opid 当前用户id
	 * @return
	 */
	@RequestMapping(value="/getTreeRegionByOpid",method=RequestMethod.POST)
	@ResponseBody
	public Object getTreeRegionByOpid() {
		UserInfo userInfo = ShiroUtils.getUserInfo();
		List<TreeBean> treeRegion = regionService.getTreeRegionByOpid(userInfo.getOpid());
		return treeRegion;
	}
	
	 //添加打印机
	@ApiOperation(value = "添加打印机", notes = "打印机名称必须填写", httpMethod = "GET/POST", tags = "接口分组1")
	@RequestMapping(value="/addprinter",method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Object addprinter(@Validated Region Region ){
    	return regionService.saveOrUpdateRegion(Region);
    }
	

}
