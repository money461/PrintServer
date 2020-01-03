package com.bondex.client.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bondex.client.service.ClientService;
import com.bondex.common.Common;
import com.bondex.entity.Datagrid;
import com.bondex.jdbc.entity.Label;
import com.bondex.res.AjaxResult;
import com.bondex.res.MsgResult;
import com.bondex.security.entity.JsonResult;
import com.bondex.security.entity.Opid;
import com.bondex.security.entity.UserInfo;
import com.bondex.shiro.security.ShiroUtils;
import com.bondex.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

	//校验是否是第一次打印(获取打印办公司)
	@RequestMapping("isFrist")
	@ResponseBody
	public String isFrist(String opid) {
		String regions = clientService.isFrist(opid);
		return regions;
	}

	@RequestMapping("addDR")
	@ResponseBody
	public String addDR(String opid, String region) {
		String regions = clientService.addDR(opid, region);
		return regions;
	}

	/**
	 * 树形菜单展示区域
	 * @param opid
	 * @return
	 */
	@RequestMapping("printLabel")
	@ResponseBody
	public String printLabel(String opid) {
		String regions = clientService.getRegion(opid);
		return regions;
	}

	@RequestMapping("updateRn")
	@ResponseBody
	public String updateRn(String region,HttpServletRequest request) {
		HttpSession session = request.getSession();
		String opid = (String) session.getAttribute(Common.Session_thisOpid);
		try {
			clientService.updateRn(region, opid);
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}
	}

	/**
	 * 发送打印消息 多个标签
	 * @param labels 多个标签
	 * @param regions 区域
	 * @param report 打印人 标识 第一次 有值，第二次 没有值
	 * @param businessType air
	 * @return
	 */
	@RequestMapping("printLabelSendClient")
	@ResponseBody
	public AjaxResult printLabelSendClient(String labels, String regions, String report, String businessType,HttpServletRequest request) {
		HttpSession session = request.getSession();
		UserInfo userInfo = (UserInfo)session.getAttribute(Common.Session_UserInfo);
		List<Label> labelList = GsonUtil.getGson().fromJson(labels, new TypeToken<List<Label>>() {}.getType());
		String gsonString = GsonUtil.GsonString(labelList);
		logger.debug("提交准备打印的数据：{}",gsonString);
		clientService.sendLabel(labelList, regions,userInfo, report, businessType);
		return AjaxResult.success();
	}

	@RequestMapping("exportLabel")
	public void exportLabel(String labels, HttpServletRequest request, HttpServletResponse response, String report) throws IOException {
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
			List<InputStream> pdf = clientService.getPDF(labels2, report, userInfo);
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
		HttpSession session = request.getSession();
		UserInfo userInfo = ShiroUtils.getUserInfo();
		Map<String, Object> map = (Map<String, Object>) session.getAttribute(Common.Session_UserSecurity);
		List<JsonResult> list = (List<JsonResult>) map.get(userInfo.getOpid() + Common.UserSecurity_PrintButton);
		return GsonUtil.GsonString(list);
	}

	@RequestMapping("getRegion")
	@ResponseBody
	public String getRegion(HttpServletRequest request, String q) {
		HttpSession session = request.getSession();
		return clientService.postRegion(q == null ? "a" : q);
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
	public String getOpidName(HttpServletRequest request, HttpServletResponse response, String q) {
		List<Opid> opids= clientService.getOpidName(q == null ? "" : q);
		Datagrid<Opid> datagrid = new Datagrid<>();
		datagrid.setRows(opids);
		return GsonUtil.GsonString(datagrid);
	}

	/**
	 * 获取区域
	 * @param request
	 * @param session
	 * @param code 办公司id
	 * @param opid
	 * @return
	 */
	@RequestMapping(value="getThisRegion",method=RequestMethod.POST)
	@ResponseBody
	public String getThisRegion(HttpServletRequest request, String code, String opid) {
		if (StringUtils.isBlank(opid) ){
			opid =ShiroUtils.getUserInfo().getOpid();
		}
		
		return clientService.getThisRegion(code, opid);
	}

}
