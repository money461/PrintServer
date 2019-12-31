package com.bondex.jdbc.service.impl;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bondex.entity.Datagrid;
import com.bondex.jdbc.dao.LabelInfoDao;
import com.bondex.jdbc.entity.JsonRootBean;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.LabelAndTemplate;
import com.bondex.jdbc.entity.Template;
import com.bondex.jdbc.service.LabelInfoService;
import com.bondex.security.entity.JsonResult;
import com.bondex.util.GsonUtil;
import com.bondex.util.HttpClient;
import com.google.gson.reflect.TypeToken;
@Service(value="paiangServiceImple")
public class PaiangServiceImple implements LabelInfoService {

	@Value("${system.interface.paiang}")
	private String paiangaddress;
	
	@Autowired
	private LabelInfoDao labelInfoDao;
	
	
	
	@Override
	public boolean labelInfoSave(JsonRootBean jsonRootBean) {
		return false;
	}

	//派昂调用接口获取数据
	@Override
	public Datagrid findByPage(String page, String rows, Label label, String start_time, String end_time, String sort,	String order, String opid, List<JsonResult> list, String businessType) throws Exception {
		if (businessType.equals("medicine")) {
			
			//标签权限
			Type objectType = new TypeToken<List<List<JsonResult>>>() {}.getType();
			List<List<JsonResult>> jsonResults = GsonUtil.getGson().fromJson(GsonUtil.GsonString(list), objectType);
			//查询指定的模板及其数据
			Template template = getTemplate(jsonResults.get(0), "4");
			
			//调用接口获取数据
			 JSONObject jsonStu = (JSONObject)JSONObject.toJSON(label);
			 jsonStu.put("page", page);
			 jsonStu.put("rows", rows);
			 
			//map对象
			Map<String, Object> data =new LinkedHashMap<>();
			//循环转换
			 Iterator it =jsonStu.entrySet().iterator();
			 while (it.hasNext()) {
			       Map.Entry<String, Object> entry = (Entry<String, Object>) it.next();
			       Object value = entry.getValue();
			       data.put(entry.getKey(), value);
			 }
			 String datajson = HttpClient.doPost(paiangaddress, data);
			 JSONObject parseObject = JSONObject.parseObject(datajson);
			 JSONArray datarows = parseObject.getJSONArray("rows");
			 Integer total = parseObject.getInteger("total");
			 
			 //反序列化
			 List<LabelAndTemplate> datalist = JSONObject.parseArray(datarows.toJSONString(), LabelAndTemplate.class);
			 
//			 TreeMap<String, List<LabelAndTemplate>> treeMap = new TreeMap<String, List<LabelAndTemplate>>();
			 
			 //封装打印模板并且对数据做出分类
			 for (LabelAndTemplate labelAndTemplate : datalist) {
				 labelAndTemplate.setDeliveryCustomer("陕西派昂医药有限责任公司");
				 labelAndTemplate.setId(template.getId());
				 labelAndTemplate.setTemplate_id(template.getTemplate_id());
				 labelAndTemplate.setTemplate_name(template.getTemplate_name());
			 }
			 
			 Datagrid<LabelAndTemplate> datagrid = new Datagrid<LabelAndTemplate>(String.valueOf(total), datalist);
			 System.out.println(GsonUtil.GsonString(datagrid));
			 return datagrid;
		}
		
		return null;
	}

	@Override
	public void updateLabel(Label label) {
		
	}

	@Override
	public void delete(List<Label> label) {
		
	}

	@Override
	public Template getTemplate(List<JsonResult> jsonResults, String id) {
		String rt = "";
		for (JsonResult jsonResult : jsonResults) {
			rt += "'" + jsonResult.getReportid() + "',";
		}
		rt = rt.substring(0, rt.length() - 1);
		Template template = labelInfoDao.getTemplate(rt, id);
		return template;
	}

	
	
}
