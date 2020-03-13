package com.bondex.controller.paiang;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.common.Common;
import com.bondex.common.enums.ResEnum;
import com.bondex.config.exception.BusinessException;
import com.bondex.entity.page.Datagrid;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.LabelAndTemplate;
import com.bondex.jdbc.service.LabelInfoService;
import com.bondex.res.MsgResult;
import com.bondex.security.entity.UserInfo;
import com.bondex.shiro.security.ShiroUtils;
import com.bondex.util.StringUtils;

@Controller
@RequestMapping("/paiang")
@Validated
public class PaiangController {


	@Resource(name="paiangServiceImple")
	private LabelInfoService labelInfoService;
	/**
	 * 进入展示页面
	 * @param mawb
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value="/viewdata",method = RequestMethod.GET)
	@ResponseBody
//	@RequiresPermissions(value={"viewdata","look"},logical=Logical.OR)
	public ModelAndView viewdata(@RequestParam(name="mawb",required=false)String mawb,ModelAndView modelAndView){
		try {
			modelAndView.addObject("mawb", mawb);
			modelAndView.setViewName("paiang/paiangList"); //页面展示
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelAndView;
	}
	
	/*
	 * 查询数据 
	 * mawb 主单号
	 * page 页面
	 * rows 行数
	 */
	@RequestMapping(value="/search",method = RequestMethod.POST,headers = {"content-type=application/x-www-form-urlencoded; charset=UTF-8"},produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public Object search(String page, String rows,Label label,String start_time, String end_time, String sort, String order, String opid,HttpServletRequest request) throws Exception{
		HttpSession session = request.getSession();
		Map<String, Object> map = (Map<String, Object>) session.getAttribute(Common.Session_UserSecurity);
		if(StringUtils.isNull(map)||map.size()==0){
			throw new BusinessException(ResEnum.FORBIDDEN.CODE,"操作号没有配置任何操作权限,无法查看数据");
		}
		UserInfo userInfo = ShiroUtils.getUserInfo();
		opid = userInfo.getOpid();
		Datagrid datagrid = labelInfoService.findByPage(page, rows, label, start_time, end_time, sort, order, "medicine");
		 return MsgResult.result(ResEnum.SUCCESS.CODE,ResEnum.SUCCESS.MESSAGE,datagrid);
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST,consumes={"application/json; charset=UTF-8"},produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public Object update(@RequestBody List<LabelAndTemplate> datalist){
		
		 //分单号=key
		 TreeMap<String, List<LabelAndTemplate>> treeMap = new TreeMap<String, List<LabelAndTemplate>>();
		 
		 //封装打印模板并且对数据做出分类
		 for (LabelAndTemplate labelAndTemplate : datalist) {
			 String hawb = labelAndTemplate.getHawb();
			 if(StringUtils.isNotBlank(hawb)){
				 List<LabelAndTemplate> hawblist = treeMap.get(hawb);
				 
				 if(StringUtils.isNull(hawblist)){
					 hawblist = new ArrayList<LabelAndTemplate>();
					 treeMap.put(hawb, hawblist);
				 }
				 hawblist.add(labelAndTemplate);
			 }
		 }
		 
		 Iterator<Entry<String, List<LabelAndTemplate>>> iterator = treeMap.entrySet().iterator();
		 while (iterator.hasNext()) {
		             Entry<String, List<LabelAndTemplate>> entry = iterator.next();
		             String key = entry.getKey();
		             List<LabelAndTemplate> value = entry.getValue();
		             //相同分单下的总件数
		             BigDecimal totalacount = value.stream().map(LabelAndTemplate :: getPackages).reduce(BigDecimal.ZERO,this :: getSum);
		             value.forEach(x->x.setTotalAcount(totalacount.toString()));
		             int acount = totalacount.intValue();
		             
		             int k=1;
	            	 for (LabelAndTemplate labelAndTemplate : value) {
	            		 
	            		 StringBuffer sb = new StringBuffer();
	            		 Integer packages=  labelAndTemplate.getPackages().intValue();
	            		 
	            		 for (int i = k; i <=acount; i++) {
	            			 if(packages==0){
	            				 k=i;
	            				 break; //跳出循环
	            			 }
	            			 sb.append(i).append("-").append(acount).append(";");
	            			 packages--;
	            		 }
	            		 
	            		 labelAndTemplate.setSerialNo(sb.toString());
	            		 
					}
						
		             
		}
		 
		 Datagrid<LabelAndTemplate> datagrid = new Datagrid<LabelAndTemplate>((long) datalist.size(), datalist);
		
		return MsgResult.result(ResEnum.SUCCESS.CODE,ResEnum.SUCCESS.MESSAGE,datagrid);
		
	}
	
	
	 //求和
    private  BigDecimal getSum(BigDecimal ...num){
        BigDecimal result = BigDecimal.ZERO;
        for (int i = 0; i < num.length; i++){
        	BigDecimal nm = num[i]!=null? num[i] :  BigDecimal.ZERO;
        		result = result.add(nm);
        }
        return result;
    }

	
}
