package com.bondex.controller.airLabel;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.bondex.common.Common;
import com.bondex.common.enums.ResEnum;
import com.bondex.config.exception.BusinessException;
import com.bondex.controller.BaseController;
import com.bondex.entity.Label;
import com.bondex.entity.LabelAndTemplate;
import com.bondex.entity.Template;
import com.bondex.entity.page.TableDataInfo;
import com.bondex.entity.res.MsgResult;
import com.bondex.service.LabelInfoService;
import com.bondex.util.StringUtils;

@Controller
@RequestMapping(value="/label")
public class AirLabelController extends BaseController {
	
	@Resource(name="labelInfoServiceImpl")
	private LabelInfoService labelInfoService;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	
	/**
	 * 进入空运标签展示页面
	 * @param mawb
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value={"/airlable/{code}","/airlabel"},method = RequestMethod.GET)
	@ResponseBody
	//@RequiresPermissions(value={"quanxianquanxian","look"},logical=Logical.OR)
	public ModelAndView indexpage(@PathVariable(name="code",required=false) String code,ModelAndView modelAndView){
		modelAndView.addObject("code", code);
		modelAndView.setViewName("/airlabel/airlabel"); //页面展示
		return modelAndView;
	}
	

	/**
	 * 获取用户拥有的模板
	 * @param opid
	 * @return
	 */
	@RequestMapping(value="getUserAuthtemplate",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public List<Template> getUsertemplate(Template template) {
		
		return labelInfoService.getUserAuthtemplate(template);
	}
	
	
	
    

	/**
	 * 获取数据库label标签数据
	 * @param page 页码
	 * @param rows 每页记录数
	 * @param label
	 * params key = start_time  end_time
	 * @param start_time
	 * @param end_time
	 * @param sort desc 升序降序
	 * @param order 按照该字段排序
	 * @param opid
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/all",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Object findByPage( Label label,HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		Map<String, Object> map = (Map<String, Object>) session.getAttribute(Common.Session_UserSecurity);
		if(StringUtils.isNull(map)||map.size()==0){
			throw new BusinessException(ResEnum.FORBIDDEN.CODE,"操作号没有配置任何操作权限,无法查看数据");
		}
		Label label2 = JSON.parseObject(URLDecoder.decode(JSON.toJSONString(label), "utf-8"), Label.class);
		startPage(true,"label");//分页 表名称
		List<LabelAndTemplate> list= labelInfoService.selectLabelByPage(label2);
		TableDataInfo dataTable = getDataTable(list); //pagehelper分页
		return MsgResult.result(ResEnum.SUCCESS.CODE, ResEnum.SUCCESS.MESSAGE, dataTable);
	}
	
	//更新打印标签数据
	@RequestMapping(value="/update",method = {RequestMethod.POST },consumes={"application/json; charset=UTF-8"},produces = "application/json; charset=UTF-8")
	@ResponseBody
	public Object update(@RequestBody List<LabelAndTemplate> labels) {
		for (LabelAndTemplate label : labels) {
			//查询修改的模板名称
			List<Template> template = jdbcTemplate.query("select * from template where template_name = ?", new Object[] { label.getTemplateName() }, new BeanPropertyRowMapper<Template>(Template.class));
			label.setReserve3(String.valueOf(template.get(0).getId()));
			labelInfoService.updateLabel(label);
		}
		return MsgResult.nodata(ResEnum.SUCCESS.CODE, ResEnum.SUCCESS.MESSAGE);
	}

	//刪除標簽
	@RequestMapping(value="/delete",method = {RequestMethod.POST },consumes={"application/json; charset=UTF-8"},produces = "application/json; charset=UTF-8")
	@ResponseBody
	public Object delete(@RequestBody List<Label> labels) {
		labelInfoService.deleteLabel(labels);
		return MsgResult.nodata(ResEnum.SUCCESS.CODE, ResEnum.SUCCESS.MESSAGE);
	}

}
