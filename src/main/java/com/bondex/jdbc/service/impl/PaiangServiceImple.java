package com.bondex.jdbc.service.impl;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bondex.config.exception.BusinessException;
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
import com.bondex.util.StringUtils;
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
	@SuppressWarnings("rawtypes")
	@Override
	public Datagrid findByPage(String page, String rows, Label label, String start_time, String end_time, String sort,	String order, String opid, List<JsonResult> list, String businessType) throws Exception {
		if (businessType.equals("medicine")) {
			
			//标签权限
			Type objectType = new TypeToken<List<List<JsonResult>>>() {}.getType();
			List<List<JsonResult>> jsonResultsList = GsonUtil.getGson().fromJson(GsonUtil.GsonString(list), objectType);
			List<JsonResult> jsonResults =jsonResultsList.get(0);
			//查询指定的模板及其数据
			Template template = getTemplate(jsonResults, "4");
			
			//调用接口获取数据
			 JSONObject jsonStu = (JSONObject)JSONObject.toJSON(label);
			 jsonStu.put("page", page);
			 jsonStu.put("rows", rows);
			 
			//map对象
			StringBuilder sb = new StringBuilder();
			//循环转换
			 Iterator it =jsonStu.entrySet().iterator();
			 while (it.hasNext()) {
			       Map.Entry<String, Object> entry = (Entry<String, Object>) it.next();
			       String value = String.valueOf(entry.getValue());
			       if(StringUtils.isNotBlank(value) && "null"!=value){
			    	   if(sb.length()==0){
			    		   sb.append(entry.getKey()).append("=").append(value);
			    	   }else{
			    		   
			    		   sb.append("&").append(entry.getKey()).append("=").append(value);
			    	   }
			       }
			 }
			 String datajson = HttpClient.sendPost(paiangaddress, sb.toString());
//			 String datajson  = ReadTxtFile.readTxtFile("F:\\workspace\\Bondex\\printServer\\src\\main\\resources\\static\\js\\paiangdata.json");
			 JSONObject parseObject = JSONObject.parseObject(datajson); //{"msg":"未查询到订单","total":0,"code":500}
			 String msg = parseObject.getString("msg");
			 if(StringUtils.isNotBlank(msg)){
				 throw new BusinessException(msg);
			 }
			 JSONArray datarows = parseObject.getJSONArray("rows");
			 Integer total = parseObject.getInteger("total");
			 
			 //反序列化
			 List<LabelAndTemplate> datalist = JSONObject.parseArray(datarows.toJSONString(), LabelAndTemplate.class);
			 
			 //分单号=key
			 TreeMap<String, List<LabelAndTemplate>> treeMap = new TreeMap<String, List<LabelAndTemplate>>();
			 
			 //封装打印模板并且对数据做出分类
			 for (LabelAndTemplate labelAndTemplate : datalist) {
				 
				 labelAndTemplate.setSendAddress("陕西派昂医药有限责任公司"); //发货人 固定
				 labelAndTemplate.setReserve3(String.valueOf(template.getId())); //模版外键 
				 labelAndTemplate.setId(template.getId());
				 labelAndTemplate.setTemplate_id(template.getTemplate_id());
				 labelAndTemplate.setTemplate_name(template.getTemplate_name());
				 labelAndTemplate.setMBLNo(labelAndTemplate.getMawb()); //主单号即运单号
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
		            		 
		            		 StringBuffer sbb = new StringBuffer();
		            		 Integer packages=  labelAndTemplate.getPackages().intValue();
		            		 
		            		 for (int i = k; i <=acount; i++) {
		            			 if(packages==0){
		            				 k=i;
		            				 break; //跳出循环
		            			 }
		            			 sbb.append(i).append("-").append(acount).append(";");
		            			 packages--;
		            		 }
		            		 
		            		 labelAndTemplate.setSerialNo(sbb.toString());
		            		 
						}
							
			             
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
		Iterator<JsonResult> iterator = jsonResults.iterator();
		while(iterator.hasNext()){
			JsonResult jsonResult = iterator.next();
			rt += "'" + jsonResult.getReportid() + "',";
		}
		rt = rt.substring(0, rt.length() - 1);
		Template template = labelInfoDao.getTemplate(rt, id);
		return template;
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
