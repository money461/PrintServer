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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bondex.client.service.ClientService;
import com.bondex.jdbc.entity.Label;
import com.bondex.security.entity.JsonResult;
import com.google.gson.Gson;

@Controller
@RequestMapping("client")
public class ClientController {
	@Autowired
	private ClientService clientService;

	@RequestMapping("search")
	@ResponseBody
	public String search(String q) {
		String regions = clientService.searchRegion(q);
		return regions;
	}

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

	@RequestMapping("printLabel")
	@ResponseBody
	public String printLabel(String opid) {
		String regions = clientService.getRegion(opid);
		return regions;
	}

	@RequestMapping("updateRn")
	@ResponseBody
	public String updateRn(String region, HttpSession session) {
		String opid = (String) session.getAttribute("thisOpid");
		try {
			clientService.updateRn(region, opid);
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}
	}

	/**
	 * 发送打印消息
	 * @param labels
	 * @param regions
	 * @param session
	 * @param report
	 * @param businessType
	 * @return
	 */
	@RequestMapping("printLabelSendClient")
	@ResponseBody
	public String printLabelSendClient(String labels, String regions, HttpSession session, String report, String businessType) {
		Map info = (Map) session.getAttribute("userInfo");
		String opid = (String) session.getAttribute("thisOpid");
		String thisUsername = (String) session.getAttribute("thisUsername");
		String rt = clientService.sendLabel(labels, regions, thisUsername, opid, info, report, businessType);
		return rt;
	}

	@RequestMapping("exportLabel")
	public void exportLabel(String labels, HttpServletRequest request, HttpSession session, HttpServletResponse response, String report) throws IOException {
		OutputStream out = null;
		try {
			out = response.getOutputStream();
			String fileName = UUID.randomUUID().toString().replace("-", "").toLowerCase() + ".zip";
			String headStr = "attachment; filename=\"" + fileName + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			ZipOutputStream zipOutputStream = new ZipOutputStream(out);

			Map info = (Map) session.getAttribute("userInfo");
			String opid = (String) session.getAttribute("thisOpid");
			String thisUsername = (String) session.getAttribute("thisUsername");

			List<Label> labels2 = clientService.getLabel(labels);
			List<InputStream> pdf = clientService.getPDF(labels2, info, report, thisUsername, opid);
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

	@RequestMapping("getPrint")
	@ResponseBody
	public String getPrint(HttpServletRequest request, HttpSession session) {
		String opid = (String) session.getAttribute("thisOpid");
		Map<String, Object> map = (Map<String, Object>) session.getAttribute("userSecurity");
		List<JsonResult> list = (List<JsonResult>) map.get(opid + "printButton");
		return new Gson().toJson(list);
	}

	@RequestMapping("getRegion")
	@ResponseBody
	public String getRegion(HttpServletRequest request, HttpSession session, String q) {
		return clientService.postRegion(q == null ? "a" : q);
	}

	@RequestMapping("getOpidName")
	@ResponseBody
	public String getOpidName(HttpServletRequest request, HttpSession session, String q) {
		return clientService.getOpidName(q == null ? "" : q);
	}

	@RequestMapping("getThisRegion")
	@ResponseBody
	public String getThisRegion(HttpServletRequest request, HttpSession session, String code, String opid) {
		if (opid.equals("null")) {
			opid = (String) session.getAttribute("thisOpid");
		}
		return clientService.getThisRegion(code, opid);
	}

}
