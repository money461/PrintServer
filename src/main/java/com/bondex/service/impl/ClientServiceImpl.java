package com.bondex.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bondex.common.Common;
import com.bondex.common.enums.ComEnum;
import com.bondex.config.exception.BusinessException;
import com.bondex.dao.ClientDao;
import com.bondex.dao.LabelInfoDao;
import com.bondex.dao.PrintLogDao;
import com.bondex.entity.Label;
import com.bondex.entity.LabelAndTemplate;
import com.bondex.entity.Region;
import com.bondex.entity.Template;
import com.bondex.entity.area.AreaBean;
import com.bondex.entity.area.Search;
import com.bondex.entity.client.Client;
import com.bondex.entity.client.ClientData;
import com.bondex.entity.client.VwOrderAll;
import com.bondex.entity.log.PrintLog;
import com.bondex.entity.page.Datagrid;
import com.bondex.mapper.TemplateDataMapper;
import com.bondex.rabbitmq.Producer;
import com.bondex.service.ClientService;
import com.bondex.shiro.security.entity.Opid;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.CloneUtils;
import com.bondex.util.GsonUtil;
import com.bondex.util.StringUtils;
import com.bondex.util.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;

@Service
@Transactional(rollbackFor = Exception.class)
public class ClientServiceImpl implements ClientService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;
	@Autowired
	private ClientDao clientDao;
	@Autowired
	private PrintLogDao  printLogDao;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private TemplateDataMapper templateDataMapper;
	@Autowired
	private Producer producer;
	
	@Autowired
	private LabelInfoDao labelInfoDao;

	public final static String SPLITSTRTAG = "\\(/\\)";

	@Override
	public List<InputStream> getPDF(List<Label> list,UserInfo userInfo)	throws IOException {
		List<InputStream> inputStreams = new ArrayList<InputStream>();
		for (Label label2 : list) {
			Client client = getClient(label2, userInfo);
			HttpHeaders headers = new HttpHeaders();
			InputStream inputStream = null;
			List list1 = new LinkedList<>();
			list1.add(MediaType.valueOf("application/pdf"));
			headers.setAccept(list1);
			MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
			params.add("token", userInfo.getToken());
			params.add("reportMsg", GsonUtil.GsonString(client));
			HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(params,headers);
			ResponseEntity<byte[]> response = restTemplate.postForEntity("http://api.bondex.com.cn:12360/excelwebapi/api/GetPDFByReport", httpEntity, byte[].class);
			while (response.getStatusCodeValue() == 204) {
				response = restTemplate.postForEntity("http://api.bondex.com.cn:12360/excelwebapi/api/GetPDFByReport",	httpEntity, byte[].class);
			}
			byte[] result = response.getBody();
			if (response.getStatusCodeValue() == 200) {
				inputStream = new ByteArrayInputStream(result);
			}
			inputStreams.add(inputStream);
		}
		return inputStreams;
	}

	/**
	 * label转client
	 * 
	 * @param report
	 * @param opid
	 * @param thisUsername
	 * @param info
	 * 
	 * @param label
	 * @return
	 */
	private Client getClient(Label label2,UserInfo userInfo) {
		Client client = new Client();// 打印数据
		ClientData clientData = new ClientData();
		VwOrderAll vwOrderAll = new VwOrderAll();
		List<Object> vwOrderAlls = new ArrayList<Object>();

		//查询数据库模板
		List<Template> template = jdbcTemplate.query("select  * from template where id = ?",
				new Object[] { label2.getReserve3()},
				new BeanPropertyRowMapper<Template>(Template.class));
		
		client.setReportID(template.get(0).getTemplateId());// 报表id
		client.setReportTplName(template.get(0).getTemplateName());// 标签名称
		client.setSenderName(userInfo.getOpname() + "/" + userInfo.getPsnname()); //此处改动 不清楚
		client.setSendOPID(userInfo.getOpid());// 操作号
		client.setNoToShow(label2.getMawb() + "_" + label2.getHawb());// 要显示的单号
		client.setOtherToShow("");
		client.setReportWidth(template.get(0).getWidth());// 标签宽度，单位毫米（目前定死）
		client.setReportHeight(template.get(0).getHeight());// 标签高度，单位毫米（目前定死）
		// ClientData
		vwOrderAll.setMblno(label2.getMawb());
		vwOrderAll.setHblno(label2.getHawb());
		vwOrderAll.setTquantity(label2.getTotal());// 件数
		vwOrderAll.setDportcode(label2.getAirportDeparture());// 起始地
		vwOrderAll.setAprotcode(label2.getDestination());// 目的地

		vwOrderAlls.add(vwOrderAll);
		clientData.setVwOrderAll(vwOrderAlls);
		client.setData(GsonUtil.GsonString(clientData));

		return client;
	}

	
	/**
	 *发送打印消息
	 *mqaddress vpnnet 内网  outnet外网
	 *
	 * 发送打印消息 多个标签
	 * @param labels 多个标签
	 * @param regionCode 办公室Code
	 * @param report 打印人 标识 第一次 有值，第二次 没有值
	 * @param mqaddress vpnnet 内网  outnet外网
	 * @param labelAndTemplate 
	 * @return
	 * @throws Exception 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void sendLabel(List<LabelAndTemplate> labelAndTemplates, String regionCode,String mqaddress) throws Exception {
		UserInfo userInfo = ShiroUtils.getUserInfo();
		String gsonString = GsonUtil.GsonString(labelAndTemplates);
		logger.debug("打印的数据：{}",gsonString);
		PrintLog printLog = new PrintLog();
		Date date=  Date.from(LocalDateTime.now().toInstant(ZoneOffset.of("+8"))); //默认时区为东8区
		printLog.setUpdateTime(date);
		printLog.setCreateTime(date);
		printLog.setMessage(gsonString);
		printLog.setStatus(1); //打印失败
		printLog.setMqaddress(mqaddress);
		printLog.setOpid(userInfo.getOpid());
		printLog.setOpidName(userInfo.getOpname());
	 try {
	
	    String id = labelAndTemplates.stream().map(res -> String.valueOf(res.getLabelId())).collect(Collectors.joining(","));
	    printLog.setLabelId(id);
		LabelAndTemplate labelDetail = labelAndTemplates.get(0);
		printLog.setCode(labelDetail.getCode()); //业务code
		printLog.setCodeName(labelDetail.getCodeName());
		
		String showNum = labelAndTemplates.stream().map(label ->{
			if(ComEnum.PaiangLabel.code.equals(label.getCode())){
				return  label.getMBLNo(); //派昂展示运单号
			}else{
				return label.getMawb(); //空运标签业务展示主单号
			}
			
		}).collect(Collectors.joining(","));
		
		printLog.setShowNum(showNum);
		
		List<Client> clients = new ArrayList<>();
		Region defaultRegion=null;
		try {
		// 获取打印区域
		// 获取打印区域 从数据库中获取
		 defaultRegion = clientDao.getRegionByRegioncode(regionCode);
		 printLog.setQueueCode(defaultRegion.getParentCode()+"_"+defaultRegion.getRegionCode()); //打印办公室
		 printLog.setRegionName(defaultRegion.getParentName()+"/"+defaultRegion.getRegionName());
		 //判断是否拥有权限
		 for (LabelAndTemplate label : labelAndTemplates) {
			String reserve3 = label.getReserve3();
			Template template = labelInfoDao.checkUseTemplate(reserve3);
			//为了防止前端传过来的数据有误
			String template_id = template.getTemplateId();
			label.setTemplateId(template_id);
			label.setTemplateName(template.getTemplateName());
			label.setWidth(template.getWidth());
			label.setHeight(template.getHeight());
			
			//根据打印模板 封装打印客户端数据  
			if (ComEnum.PaiangLabel.code.equals(label.getCode())) {  //派昂医药标签
				List<Client> list = getPaiangClients(label, userInfo);
				clients.addAll(list);
			} else {
				String t[] = label.getTotal().split("\\.");
				label.setTotal(t[0]);
				//封装打印客户端 且发送客户端
				Client client = getClients(label, userInfo);
				clients.add(client);
			}
			
			// 发送消息 并且 更新所有打印数据状态为"已打印"
			//当前时间
//			LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss"))
			//修改状态
			clientDao.update("update label set is_print = '1',print_time=NOW(),print_user = '" + userInfo.getOpname()	+ "'  where label_id = '" + label.getLabelId() + "'");
			
		 }
			printLog.setStatus(0); //打印成功
			printLog.setReason("打印数据发送成功！");
		 
		} catch (Exception e) {
			throw new BusinessException("封装打印数据发送异常！原因："+e.getMessage());
		}
	
		printLog.setMessage(GsonUtil.GsonString(clients)); //记录打印数据
		
		try {
		
			for (Client client : clients) {
				// 发送MQ 执行打印
					if(Common.MQAddress_VPNNET.equals(mqaddress)){
						producer.vpnNetPrint(client, defaultRegion); //发送内网
					}else if(Common.MQAddress_OUTNET.equals(mqaddress)){
						producer.outNetPrint(client, defaultRegion); //发送外网
					}else{
						producer.print(client, defaultRegion); //封装打印信息 携带打印区域
					}
			}
		
		} catch (Exception e) {
			throw new BusinessException("发送至"+mqaddress+"打印消息失败！原因："+e.getMessage());
		}
		
	 } catch (BusinessException e) { //自定义异常
			printLog.setReason(e.getMessage());
			
	 } catch (Exception e) {
			printLog.setReason("打印逻辑发生未知异常！"+e.getMessage());
		 
	 }finally{
		 printLogDao.insertPrintLog(printLog); //打印日志入库
	 }
	 
	 if(1==printLog.getStatus()){
			throw new BusinessException("失败！"+printLog.getReason());
	 }
}


	/**
	 * 封装派昂标签
	 * @param label
	 * @param userInfo
	 * @return
	 */
	private List<Client> getPaiangClients(LabelAndTemplate label, UserInfo userInfo) {
			//克隆数据
			
			String serialNo = label.getSerialNo(); //打印序号
			String[] str = serialNo.split(";");
			List<Client> list = new ArrayList<Client>(str.length);
			for (String no : str) {
					Client client = new Client();// 打印客户端数据结构
					
					String template_id =label.getTemplateId();
					client.setReportID(template_id);// 模板id
					client.setReportTplName(label.getTemplateName());// 标签名称
		
					client.setSenderName(userInfo.getOpname() + "/" + userInfo.getPsnname()); //此处改动不是很清楚
					client.setSendOPID(userInfo.getOpid());// 操作号
					client.setNoToShow(label.getMawb() + "_" + label.getHawb());// 要显示的单号
					client.setOtherToShow("");
					
					client.setReportWidth(label.getWidth());// 标签宽度，单位毫米（目前定死）
					client.setReportHeight(label.getHeight());// 标签高度，单位毫米（目前定死）
					
					client.setCopies(1);// 标签打印份数
		
					LabelAndTemplate clone = CloneUtils.clone(label);
					clone.setSerialNo(no);
					
					JSONArray array = new JSONArray();
					array.add(clone);
					
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("vwOrderAll", array);
					String jsonString  = JSON.toJSONStringWithDateFormat(jsonObject ,"yyyy-MM-dd");
					client.setData(jsonString);
			       //派昂封装结束
					list.add(client);
		}
			return list;
		
	}

	//封装打印数据
	private Client getClients(LabelAndTemplate label, UserInfo userInfo) {
			//循环封装打印数据
			Client client = new Client();// 打印客户端数据结构
			ClientData clientData = new ClientData();
			List<Object> vwOrderAlls = new ArrayList<Object>();
			
			String template_id =label.getTemplateId();
			client.setReportID(template_id);// 模板id
			client.setReportTplName(label.getTemplateName());// 标签名称

			client.setSenderName(userInfo.getOpname() + "/" + userInfo.getPsnname()); //此处改动不是很清楚
			client.setSendOPID(userInfo.getOpid());// 操作号
			client.setNoToShow(label.getMawb() + "_" + label.getHawb());// 要显示的单号
			client.setOtherToShow("");
			
			client.setReportWidth(label.getWidth());// 标签宽度，单位毫米（目前定死）
			client.setReportHeight(label.getHeight());// 标签高度，单位毫米（目前定死）
			
				//封装空运标签数据打印格式  重庆标签 打印模板
				if("2785d11a-e261-4c61-8096-8f9f21e2a3f0".equals(template_id)){
					
					if(StringUtils.isBlank(label.getHawb())){
						throw new BusinessException("重庆模板标签数据分单号不能为空！");
					}
					
					String sql = "SELECT * FROM	( SELECT * FROM label WHERE mawb IN ( SELECT mawb FROM label WHERE hawb = '"+label.getHawb()+"' ) ) AS l WHERE	l.hawb = '"+label.getHawb()+"' OR l.hawb = ''";
					List<LabelAndTemplate> labelList= templateDataMapper.queryLabelAndTemplate(sql);
					
					JSONObject  jsonObject2 = new JSONObject();
					
					for (LabelAndTemplate labelAndTemplate : labelList) {
						//分单号不为空的destination是分单的
						if(StringUtils.isNotBlank(labelAndTemplate.getHawb())){
							jsonObject2.put("HawbPieces", labelAndTemplate.getTotal());
							jsonObject2.put("HawbDstn", labelAndTemplate.getDestination());
							
						}else{
							jsonObject2.put("MawbPieces", labelAndTemplate.getTotal());
							jsonObject2.put("MawbDstn", labelAndTemplate.getDestination());
						}
						jsonObject2.put("HawbNo", labelAndTemplate.getHawb());
						jsonObject2.put("MawbNo", labelAndTemplate.getMawb());
						jsonObject2.put("Depature", labelAndTemplate.getAirportDeparture());
					}
					
					vwOrderAlls. add(jsonObject2);
					
				}else{
					VwOrderAll vwOrderAll = new VwOrderAll();
					vwOrderAll.setMblno(label.getMawb()); //主单
					vwOrderAll.setHblno(label.getHawb()); //分单
					vwOrderAll.setTquantity(label.getTotal());// 件数
					vwOrderAll.setDportcode(label.getAirportDeparture());// 起始地
					vwOrderAll.setAprotcode(label.getDestination());// 目的地
					vwOrderAlls.add(vwOrderAll);
				}
				
				client.setCopies(Integer.valueOf(label.getTotal()));// 标签打印份数
				System.out.println("标签核心展示内容==>"+GsonUtil.GsonString(vwOrderAlls));
				clientData.setVwOrderAll(vwOrderAlls);
				client.setData(GsonUtil.GsonString(clientData));
				return client;
	}


	@Override
	public List<Label> getLabel(String label) {
		return clientDao.getLabel(label);
	}


	/**
	 * 分页查询机场地址信息
	 * code 筛选简码
	 * page 当前页码
	 * rows 每页显示条数 
	 * 
	 * 
	 */
	@Override
	public Object postRegion(String code,Integer curPage,Integer pageSize) {
		
		//设置查询条件
		Map<String, String> data = new HashMap<>();
		data.put("sLike", code);// 查询条件
		data.put("maxTotal",String.valueOf(pageSize));// 查询条数
		data.put("fields", "code");// 查询字段

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> param = new LinkedMultiValueMap<String, String>();
		param.add("param", GsonUtil.GsonString(data));
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(param, headers);
		ResponseEntity<String> response = restTemplate.postForEntity("http://baseinfo.bondex.com.cn:8080/apis/airport",	request, String.class);
		String rt = response.getBody();
		Search search = GsonUtil.GsonToBean(rt, Search.class);
		if (search.getSuccess()) {
			
			// 转数据表格json
			Datagrid<AreaBean> datagrid = new Datagrid<AreaBean>();
			List<AreaBean> list = search.getList();
			datagrid.setRows(list);
			return datagrid; //Gson序列化
		}
		return "";
	}

	/**
	 * 查询绑定或者临时的办公室信息
	 */
	@Override
	public Region getDefaultBindRegionByOpid(String default_region, String opid) {
		Region region = null;
		if (StringUtils.isBlank(default_region)) {
			// 查询用户绑定的默认办公室
			region = clientDao.getDefaultBindRegionByOpid(opid);
		} else {
			// 查询临时办公室
			region = clientDao.getRegionByRegioncode(default_region);
		}
		return region;
	}

	/**
	 * 获取操作 opids
	 */
	@Override
	public List<Opid> getOpidName(String param,Integer page,Integer limit) {
		String sql = "select distinct opid_name,opid from label where opid_name like '%"+ param + "%' or opid like '%" + param + "%'"; 
		//获取第1页，10条内容，默认查询总数count
		page = page==null ? 1 :page;
		limit = limit==null ? 10 : limit;
		PageHelper.startPage(page, limit);
		List<Opid> opids= templateDataMapper.queryOpidName(sql);
		//List<Opid> opids = jdbcTemplate.query(, new Object[] {},new BeanPropertyRowMapper<Opid>(Opid.class));
		return opids;
	}

	
	

}
