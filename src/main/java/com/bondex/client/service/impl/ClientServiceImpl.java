package com.bondex.client.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bondex.client.dao.ClientDao;
import com.bondex.client.entity.Client;
import com.bondex.client.entity.ClientData;
import com.bondex.client.entity.Listarea;
import com.bondex.client.entity.Region;
import com.bondex.client.entity.Search;
import com.bondex.client.entity.VwOrderAll;
import com.bondex.client.entity.yml.RegionJDBC;
import com.bondex.client.entity.yml.TreeBean;
import com.bondex.client.service.ClientService;
import com.bondex.common.enums.ResEnum;
import com.bondex.config.exception.BusinessException;
import com.bondex.entity.Datagrid;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.Template;
import com.bondex.rabbitmq.Producer;
import com.bondex.security.entity.Opid;
import com.bondex.security.entity.UserInfo;
import com.bondex.util.CloneUtils;
import com.bondex.util.GsonUtil;
import com.bondex.util.StringUtils;

@Component
@Transactional
public class ClientServiceImpl implements ClientService {
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ClientDao clientDao;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private Producer producer;

	public final static String SPLITSTRTAG = "\\(/\\)";

	@Override
	public List<InputStream> getPDF(List<Label> list, String report,UserInfo userInfo)	throws IOException {
		List<InputStream> inputStreams = new ArrayList<InputStream>();
		for (Label label2 : list) {
			Client client = getClient(label2, report, userInfo);
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
	private Client getClient(Label label2, String report,UserInfo userInfo) {
		String[] rt = report.split(",");
		Client client = new Client();// 打印数据
		ClientData clientData = new ClientData();
		VwOrderAll vwOrderAll = new VwOrderAll();
		List<VwOrderAll> vwOrderAlls = new ArrayList<VwOrderAll>();

		/*
		 * if (label2.getReserve3().equals("1")) {
		 * client.setReportID("988b4162-00af-4924-a6fe-6b310d161900");// 报表id
		 * client.setReportTplName("标签-富士康");// 标签名称（标签 目前定死） } else if
		 * (label2.getReserve3().equals("2")) {
		 * client.setReportID("a357f19f-6a1f-4171-ab8e-1f1a2b77377a");// 报表id
		 * client.setReportTplName("标签-邦达普货");// 标签名称（标签 目前定死） } else { if
		 * (label2.getHawb().trim().equals("")) {
		 * client.setReportID("988b4162-00af-4924-a6fe-6b310d161900");// 报表id
		 * client.setReportTplName("标签-富士康");// 标签名称（标签 目前定死） } else {
		 * client.setReportID("a357f19f-6a1f-4171-ab8e-1f1a2b77377a");// 报表id
		 * client.setReportTplName("标签-邦达普货");// 标签名称（标签 目前定死） } }
		 */
		List<Template> template = jdbcTemplate.query("select * from template where id = ? or template_name = ?",
				new Object[] { label2.getReserve3(), label2.getReserve3() },
				new BeanPropertyRowMapper<Template>(Template.class));
		client.setReportID(template.get(0).getTemplate_id());// 报表id
		client.setReportTplName(template.get(0).getTemplate_name());// 标签名称

		client.setSenderName(userInfo.getOpname() + "/" + userInfo.getPsnname()); //此处改动 不清楚
		client.setSendOPID(userInfo.getOpid());// 操作号
		client.setNoToShow(label2.getMawb() + "_" + label2.getHawb());// 要显示的单号
		client.setOtherToShow("");
		client.setReportWidth("100");// 标签宽度，单位毫米（目前定死）
		client.setReportHeight("70");// 标签高度，单位毫米（目前定死）
		// ClientData
		vwOrderAll.setMblno(label2.getMawb());
		vwOrderAll.setHblno(label2.getHawb());
		vwOrderAll.setTquantity(label2.getTotal());// 件数
		vwOrderAll.setDportcode(label2.getAirport_departure());// 起始地
		vwOrderAll.setAprotcode(label2.getDestination());// 目的地

		vwOrderAlls.add(vwOrderAll);
		clientData.setVwOrderAll(vwOrderAlls);
		client.setData(GsonUtil.GsonString(clientData));

		return client;
	}

	/**
	 * 获取办公室区域
	 */
	@Override
	public String getRegion(String opid) {
		List<RegionJDBC> regionJDBCs = clientDao.getAll(opid);
		List<TreeBean> treeBeans = new ArrayList<>();
		List<TreeBean> prent = new ArrayList<>();
		Set<String> set = new HashSet<>();
		TreeBean treeBean = null;
		for (RegionJDBC regionJDBC : regionJDBCs) {
			set.add(regionJDBC.getParent_name());
			treeBean = new TreeBean();
			treeBean.setText(regionJDBC.getRegion_name());
			treeBean.setParent_code(regionJDBC.getParent_code());
			treeBean.setRegion_code(regionJDBC.getRegion_code());
			treeBean.setPname(regionJDBC.getParent_name());
			treeBeans.add(treeBean);
		}
		// 设置父节点
		for (String string : set) {
			treeBean = new TreeBean();
			treeBean.setText(string);
			treeBean.setState("closed");
			List<TreeBean> childrens = new ArrayList<>();
			for (TreeBean treeBean1 : treeBeans) {
				if (treeBean1.getPname().equals(string)) {
					childrens.add(treeBean1);
				}
			}
			treeBean.setChildren(childrens);
			prent.add(treeBean);
		}
		return GsonUtil.GsonString(prent);
	}

	/**
	 *发送打印消息
	 */
	@Override
	public void sendLabel(List<Label> labels, String region, UserInfo userInfo, String report,String businessType) {
		List<Label> lables3 = new ArrayList<>();
		// 发送消息 并且 更新数据状态为"已打印“
		for (Label label : labels) {
			
			//当前时间
//			LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss"))
			String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			//修改状态
			clientDao.update("update label set is_print = '1',reserve2='" + nowTime + "',print_user = '" + userInfo.getOpname()	+ "'  where label_id = '" + label.getLabel_id() + "'");
			
			//派昂
			if (businessType.equals("medicine")) {
				
				//调用封装数据方法
				
				
				// 切割字符串，判断多行
				/*if (label.getRecCustomerName() != null && label.getTakeCargoNo() != null) {
					String[] RecCustomerNames = label.getRecCustomerName().split(SPLITSTRTAG);
					String[] TakeCargoNos = label.getTakeCargoNo().split(SPLITSTRTAG);
					String[] RecAddress = label.getRecAddress().split(SPLITSTRTAG);
					for (int i = 0; i < TakeCargoNos.length; i++) {
						Label e = new Label();
						e.setRecCustomerName(RecCustomerNames[i]);
						e.setTakeCargoNo(TakeCargoNos[i]);
						e.setMBLNo(label.getMBLNo());
//						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						try {
							e.setEDeparture(label.getEDeparture());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						e.setSendAddress(label.getSendAddress());
						e.setRecAddress(RecAddress[i]);
						e.setReserve3(label.getReserve3());
						e.setTotal(label.getTotal());
						lables3.add(e);
					}
				}*/
			}
		}
		
		if (!lables3.isEmpty()) {
			labels = lables3;
		}

		//第二次 获取打印区域 从数据库中获取
		if (report.equals("")) {
			if (!region.contains("/")) {
				// 获取打印区域
				Region defaultRegion = clientDao.getRegion(region);
				region = defaultRegion.getParent_code() + "/" + defaultRegion.getRegion_code();
			}
			
		} else {
			// 入库，用户第二次打印则不会在页面弹出选择区域的界面
			String[] rt = region.split("/");
			String rid = clientDao.getRegionId(rt[1]);
			clientDao.addDR(userInfo.getOpid(), rid);
		}
		List<Client> clients = new ArrayList<>();
		for (Label label : labels) {
			if (businessType.equals("medicine")) {
				clients = getClients(labels, userInfo, businessType);
			} else {
				String t[] = label.getTotal().split("\\.");
				label.setTotal(t[0]);
				clients = getClients(labels, userInfo, businessType);
			}
		}
		for (Client client : clients) {
			// 发送MQ 执行打印
			try {
				producer.print(GsonUtil.GsonString(client), region); //封装打印信息 携带打印区域
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	//封装打印数据
	private List<Client> getClients(List<Label> labels2, UserInfo userInfo, String businessType) {
		List<Client> clients = new ArrayList<>();
		Client client;
		ClientData clientData;
		VwOrderAll vwOrderAll;
		List<VwOrderAll> vwOrderAlls;
		JSONArray array;
		JSONObject jsonObject = new JSONObject();
		for (Label label : labels2) {
			client = new Client();// 打印数据
			clientData = new ClientData();
			vwOrderAlls = new ArrayList<VwOrderAll>();
			vwOrderAll = new VwOrderAll();
			array = new JSONArray();
			
			List<Template> template = jdbcTemplate.query("select * from template where id = ? or template_name=?",	new Object[] { label.getReserve3(), label.getReserve3() },	new BeanPropertyRowMapper<Template>(Template.class));
			if(null==template || template.size()==0){
				throw new BusinessException(ResEnum.FAIL.CODE, "请指定打印模板！");
			}
			client.setReportID(template.get(0).getTemplate_id());// 模板id
			client.setReportTplName(template.get(0).getTemplate_name());// 标签名称

			client.setSenderName(userInfo.getOpname() + "/" + userInfo.getPsnname()); //此处改动不是很清楚
			client.setSendOPID(userInfo.getOpid());// 操作号
			client.setOtherToShow("");
			// ClientData
			if (businessType.equals("medicine")) {
				client.setCopies("1");// 标签打印份数
				client.setReportWidth("100");// 标签宽度，单位毫米（目前定死）
				client.setReportHeight("100");// 标签高度，单位毫米（目前定死）
				//克隆数据
				String totalAccount = label.getTotalAccount();
				for (int i = 1; i <=Integer.valueOf(totalAccount); i++) {
					Label clone = CloneUtils.clone(label);
					clone.setSerizalNo(i+"-"+totalAccount);
					array.add(clone);
				}
				
				jsonObject.put("vwOrderAll", array);
				client.setData(jsonObject.toJSONString());
				
				clients.add(client);
				
			} else {
				client.setCopies(label.getTotal());// 标签打印份数
				client.setNoToShow(label.getMawb() + "_" + label.getHawb());// 要显示的单号
				client.setReportWidth("100");// 标签宽度，单位毫米（目前定死）
				client.setReportHeight("70");// 标签高度，单位毫米（目前定死）

				vwOrderAll.setMblno(label.getMawb());
				vwOrderAll.setHblno(label.getHawb());
				vwOrderAll.setTquantity(label.getTotal());// 件数
				vwOrderAll.setDportcode(label.getAirport_departure());// 起始地
				vwOrderAll.setAprotcode(label.getDestination());// 目的地

				vwOrderAlls.add(vwOrderAll);
				clientData.setVwOrderAll(vwOrderAlls);
				client.setData(GsonUtil.GsonString(clientData));
				clients.add(client);
			}
			
			
		}
		return clients;
	}

	@Override
	public String searchRegion(String q) {
		List<RegionJDBC> regionJDBCs = clientDao.search(q);
		Datagrid<RegionJDBC> datagrid = new Datagrid<RegionJDBC>();
		datagrid.setRows(regionJDBCs);
		return GsonUtil.GsonString(datagrid);
	}

	@Override
	public List<Label> getLabel(String label) {
		return clientDao.getLabel(label);
	}

	@Override
	public String isFrist(String opid) {
		return clientDao.isFrist(opid);
	}

	@Override
	public String addDR(String opid, String region) {
		return clientDao.addDR(opid, region);
	}

	@Override
	public void updateRn(String region, String opid) {
		clientDao.updateRn(region, opid);
	}

	@Override
	public String postRegion(String szm) {
		Map<String, String> map1 = new HashMap<>();
		map1.put("sLike", szm);// 查询条件
		map1.put("maxTotal", "10");// 查询条数
		map1.put("fields", "code");// 查询字段

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("param", GsonUtil.GsonString(map1));
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<String> response = restTemplate.postForEntity("http://baseinfo.bondex.com.cn:8080/apis/airport",	request, String.class);

		// 转数据表格json
		Datagrid datagrid = new Datagrid<>();
		String rt = response.getBody();
		Search search = GsonUtil.GsonToBean(rt, Search.class);
		List<Listarea> list = search.getList();
		for (Listarea list2 : list) {
			list2.setName("[" + list2.getCode() + "]" + list2.getName());
		}
		if (search.getSuccess()) {
			datagrid.setRows(list);
			return GsonUtil.GsonString(datagrid);
		}
		return "";
	}

	@Override
	public String getThisRegion(String code, String opid) {
		Region region = null;
		if (code.equals("null")) {// 查询默认办公室
			region = clientDao.getThisRegionForOpid(opid);
		} else {// 查询临时办公室
			region = clientDao.getThisRegion(code);
		}
		return GsonUtil.GsonString(region);
	}

	/**
	 * 获取操作 opids
	 */
	@Override
	public List<Opid> getOpidName(String string) {
		List<Opid> opids = jdbcTemplate.query("select distinct opid_name,opid from label where opid_name like '%"
				+ string + "%' or opid like '%" + string + "%'", new Object[] {},new BeanPropertyRowMapper<Opid>(Opid.class));
		return opids;
	}

}
