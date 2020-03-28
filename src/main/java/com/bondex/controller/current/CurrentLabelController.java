package com.bondex.controller.current;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.controller.BaseController;
import com.bondex.entity.current.BaseLabelDetail;
import com.bondex.entity.current.Baselabel;
import com.bondex.entity.page.PageBean;
import com.bondex.entity.page.TableDataInfo;
import com.bondex.entity.res.AjaxResult;
import com.bondex.service.CurrentLabelService;
import com.bondex.util.GsonUtil;
import com.bondex.util.JsonUtil;
import com.bondex.util.StringUtils;
import com.google.gson.reflect.TypeToken;
@Controller
@RequestMapping(value="/currentlabel")
public class CurrentLabelController extends BaseController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CurrentLabelService currentLabelService;
	
	/**
	 * 查询基本数据
	 * @return
	 */

	@RequestMapping(value="/all",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Object selectBaseLabelList(BaseLabelDetail baseLabelDetail){
		PageBean<BaseLabelDetail> list = currentLabelService.selectBaseLabelList(baseLabelDetail);
		TableDataInfo info = getDataTable(list);
		List<BaseLabelDetail> listdata = list.getList();
		if(StringUtils.isEmpty(listdata)){
			info.setCode(1);
			info.setMsg("<b style='color:red;'>抱歉，没有找到匹配的数据！</b>");
		}
		return info;
	}
	
	
	@RequestMapping(value="/update",method={RequestMethod.POST})
	@ResponseBody
	public Object updateBaseLabel(@RequestBody List<BaseLabelDetail> list){
		currentLabelService.updateBaseLabel(list);
		return AjaxResult.success();
	}
	
	/**
	 * 发送通用打印数据
	 * @param list
	 * @param regionCode
	 * @param mqaddress
	 * @return
	 */
	@RequestMapping(value="/printCurrentLabelSendClient",method=RequestMethod.POST,headers = {"content-type=application/x-www-form-urlencoded; charset=UTF-8"},produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public AjaxResult printCurrentLabelSendClient(String labels,String regionCode,String mqaddress) {
		List<BaseLabelDetail> list = GsonUtil.getGson().fromJson(labels, new TypeToken<List<BaseLabelDetail>>() {}.getType());
		currentLabelService.printCurrentLabelSendClient(list,regionCode,mqaddress);
		return AjaxResult.success();
	}
	
	@RequestMapping(value="/currentPrintLabel",method={RequestMethod.POST,RequestMethod.GET},headers = {"content-type=application/x-www-form-urlencoded; charset=UTF-8"},produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public Object printCurrentLabelSendClient(List<Baselabel>  list,ModelAndView modelAndView){
		
		list = new ArrayList<Baselabel>();
		Baselabel baselabel = new Baselabel();
		baselabel.setShowNum("mawb2020022488888"); //展示单号
		baselabel.setDocTypeId("OrderManageClient");
		baselabel.setDocTypeName("订单管理请求");
		baselabel.setCopies(5);
		baselabel.setOpid("231243");
		baselabel.setOpidName("重庆空运操作/邹凤");
		baselabel.setJsonData("[{\"Mblno\":\"074-28742501\",\"Hblno\":\"77732686\",\"Tquantity\":\"1\",\"Dportcode\":\"CTU\",\"Aprotcode\":\"MEX\"}]");
		list.add(baselabel);
		
		modelAndView.addObject("data", JsonUtil.objToStr(list));
		modelAndView.setViewName("current/currentLabel"); //页面展示
		return modelAndView;
	}
	
	
}
