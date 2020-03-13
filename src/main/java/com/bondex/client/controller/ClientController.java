package com.bondex.client.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bondex.client.entity.Region;
import com.bondex.client.entity.UserDefaultRegion;
import com.bondex.client.entity.yml.TreeBean;
import com.bondex.client.service.ClientService;
import com.bondex.common.Common;
import com.bondex.common.enums.ResEnum;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.LabelAndTemplate;
import com.bondex.res.AjaxResult;
import com.bondex.res.MsgResult;
import com.bondex.security.entity.JsonResult;
import com.bondex.security.entity.Opid;
import com.bondex.security.entity.UserInfo;
import com.bondex.shiro.security.ShiroUtils;
import com.bondex.util.GsonUtil;
import com.bondex.util.StringUtils;
import com.google.gson.reflect.TypeToken;

@Controller
@RequestMapping("client")
public class ClientController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ClientService clientService;

	@RequestMapping("search")
	@ResponseBody
	public String search(String q) {
		String regions = clientService.searchRegion(q);
		return regions;
	}

	//校验是否是第一次打印(获取打印办公室)
	@RequestMapping(value="getDefaultRegionByOpid",method=RequestMethod.POST)
	@ResponseBody
	public UserDefaultRegion getDefaultRegionByOpid() {
		UserInfo userInfo = ShiroUtils.getUserInfo();
		UserDefaultRegion userDefaultRegion = new UserDefaultRegion();
		userDefaultRegion.setOpid(userInfo.getOpid());
		List<UserDefaultRegion> selectUserDefaultRegion = clientService.selectUserDefaultRegion(userDefaultRegion);
		if(StringUtils.isNotNull(selectUserDefaultRegion)){
			userDefaultRegion = selectUserDefaultRegion.get(0);
		}
		return userDefaultRegion;
	}


	/**
	 * 树形菜单展示区域
	 * @param opid 当前用户id
	 * @return
	 */
	@RequestMapping(value="getTreeRegionByOpid",method=RequestMethod.POST)
	@ResponseBody
	public Object getTreeRegionByOpid() {
		UserInfo userInfo = ShiroUtils.getUserInfo();
		List<TreeBean> treeRegion = clientService.getTreeRegionByOpid(userInfo.getOpid());
		return treeRegion;
	}
	
	

	/**
	 * 更新用户信息和办公室信息
	 * @param region chengdu/jichang
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateOrAddUserRegion",method=RequestMethod.POST)
	@ResponseBody
	public Object updateOrAddUserRegion(UserDefaultRegion userDefaultRegion,HttpServletRequest request) {
		try {
			UserInfo userInfo = ShiroUtils.getUserInfo();
			String opid = userInfo.getOpid();
			userDefaultRegion.setOpid(opid);
			userDefaultRegion.setUsername(userInfo.getOpname());
			Integer i = (Integer)clientService.updateOrAddUserRegion(userDefaultRegion);
			return MsgResult.nodata(ResEnum.SUCCESS.CODE, ResEnum.SUCCESS.MESSAGE);
			
		} catch (Exception e) {
			return  MsgResult.nodata(ResEnum.FAIL.CODE, ResEnum.FAIL.MESSAGE);
		}
	}

	/**
	 * 发送打印消息 多个标签
	 * @param labels 多个标签
	 * @param regionid 办公室区域
	 * @param businessType air
	 * @param mqaddress vpnnet 内网  outnet外网
	 * @return
	 */
	@RequestMapping(value="printLabelSendClient",method=RequestMethod.POST,headers = {"content-type=application/x-www-form-urlencoded; charset=UTF-8"},produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public AjaxResult printLabelSendClient(String labels, String regionid, String businessType,String mqaddress, HttpServletRequest request) {
		HttpSession session = request.getSession();
		UserInfo userInfo = (UserInfo)session.getAttribute(Common.Session_UserInfo);
		List<LabelAndTemplate> labelList = GsonUtil.getGson().fromJson(labels, new TypeToken<List<LabelAndTemplate>>() {}.getType());
		String gsonString = GsonUtil.GsonString(labelList);
		logger.debug("提交准备打印的数据：{}",gsonString);
		clientService.sendLabel(labelList, regionid,userInfo, businessType,mqaddress);
		return AjaxResult.success();
	}

	/**
	 * 导出标签数据
	 * @param labels
	 * @param request
	 * @param response
	 * @param report
	 * @throws IOException
	 */
	@RequestMapping(value="exportLabel",method=RequestMethod.GET)
	public void exportLabel(String labels, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		OutputStream out = null;
		try {
			out = response.getOutputStream();
			String fileName = UUID.randomUUID().toString().replace("-", "").toLowerCase() + ".zip";
			String headStr = "attachment; filename=\"" + fileName + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			ZipOutputStream zipOutputStream = new ZipOutputStream(out);

			UserInfo userInfo = (UserInfo) session.getAttribute(Common.Session_UserInfo);

			List<Label> labels2 = clientService.getLabel(labels);
			List<InputStream> pdf = clientService.getPDF(labels2, userInfo);
			if (pdf == null) {
				response.setStatus(444);
				return;
			}

			byte[] buf = new byte[1024];
			int i = 0;
			for (InputStream inputStream : pdf) {
				// 防止以主单号命名，名称相同
				int k = 0;
				for (int j = 0; j < pdf.size(); j++) {
					if (labels2.get(i).getHawb().equals("")) {
						if (labels2.get(i).getMawb().equals(labels2.get(j).getMawb())) {
							if (k++ >= 1) {
								zipOutputStream.putNextEntry(new ZipEntry(labels2.get(i).getMawb() + "(" + i + ")" + ".pdf"));
								break;
							}
						}
						// 如果最后一次循环还没有重复的，就走没有重复名称代码
						if (j == pdf.size() - 1) {
							zipOutputStream.putNextEntry(new ZipEntry(labels2.get(i).getMawb() + ".pdf"));
						}
					} else {
						zipOutputStream.putNextEntry(new ZipEntry(labels2.get(i).getMawb() + "_" + labels2.get(i).getHawb() + ".pdf"));
						break;
					}

				}
				int temp = 0;
				while ((temp = inputStream.read(buf, 0, 1024)) != -1) { // 读取内容
					zipOutputStream.write(buf, 0, temp); // 压缩输出
				}
				inputStream.close();
				i++;
			}
			zipOutputStream.close();
		} finally {
			response.flushBuffer();// 不可少
			out.close();
		}
	}

	/**
	 * 获取打印标签模板权限
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping("getPrint")
	@ResponseBody
	public String getPrint(HttpServletRequest request) {
		List<JsonResult> list = ShiroUtils.getUserPrintTemplateInfo();
		return GsonUtil.GsonString(list);
	}

	/**
	 * 分页获取起始地 - 目的地
	 * @param code 地址简码 CTU
	 * @param curPage 当前页码
	 * @param pageSize 每页记录数
	 * @return
	 */
	@RequestMapping(value="getRegion",method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Object getRegion(@RequestParam(defaultValue="CTU") String code,@RequestParam(defaultValue="1") Integer curPage,@RequestParam(defaultValue="10") Integer pageSize) {
		return clientService.postRegion(code,curPage,pageSize);
	}
	
	
	/**
	 * 数据库获取录入人操作信息
	 * @param request
	 * @param response
	 * @param q
	 * @return
	 */
	@RequestMapping("getOpidName")
	@ResponseBody
	public Object getOpidName(HttpServletRequest request, HttpServletResponse response, String q,Integer page, Integer limit,String opid_name) {
		if(StringUtils.isNoneBlank(opid_name)){
			q= opid_name;
		}
		q = q == null ? "" : q;
		List<Opid> opids= clientService.getOpidName(q,page,limit);
		
		Map<String, Object> map = new LinkedHashMap<String,Object>();
		map.put("code", 0);
		map.put("count", opids.size());
		map.put("data", opids);
		
		/*Datagrid<Opid> datagrid = new Datagrid<>();
		datagrid.setRows(opids);*/
		
//		return GsonUtil.GsonString(datagrid);
		return map;
	}

	/**
	 * 获取用户opid绑定的默认办公室区域或者临时使用的办公室
	 * @param request
	 * @param session
	 * @param regionid 区域办公室id 存在则表示临时的办公室
	 * @param opid 操作id
	 * @return
	 */
	@RequestMapping(value="getDefaultBindRegionByOpid",method=RequestMethod.POST)
	@ResponseBody
	public Region getDefaultBindRegionByOpid(String default_region_code, String opid) {
		if (StringUtils.isBlank(opid) ){
			opid =ShiroUtils.getUserInfo().getOpid();
		}
		
		return clientService.getDefaultBindRegionByOpid(default_region_code, opid);
	}

}
