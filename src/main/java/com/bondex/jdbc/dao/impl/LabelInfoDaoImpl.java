package com.bondex.jdbc.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.bondex.common.enums.NewPowerHttpEnum;
import com.bondex.entity.Subscribe;
import com.bondex.jdbc.dao.LabelInfoDao;
import com.bondex.jdbc.entity.JsonRootBean;
import com.bondex.jdbc.entity.Keywords;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.LabelAndTemplate;
import com.bondex.jdbc.entity.Template;
import com.bondex.mapper.AdminDataCurrentMapper;
import com.bondex.mapper.TemplateDataMapper;
import com.bondex.security.SecurityService;
import com.bondex.security.entity.JsonResult;
import com.bondex.security.entity.UserInfo;
import com.bondex.shiro.security.ShiroUtils;
import com.bondex.util.GsonUtil;
import com.bondex.util.StringUtils;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

@Component
public class LabelInfoDaoImpl implements LabelInfoDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private  AdminDataCurrentMapper adminDataCurrentMapper;
	
	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;
	
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private TemplateDataMapper templateDataMapper;
	
	private Map<String, Integer> subscribeTotal = new HashMap<String, Integer>();
	//订阅邮箱地址
	@Value("${email.url.ip}")
	private String urlIp;

	public void subscribe(String mawb) {
		List<Subscribe> subscribes = jdbcTemplate.query("SELECT * FROM subscribe WHERE batch_id = ( SELECT batch_id FROM subscribe WHERE sr_state = '0' AND sr_mawb = ? )", new Object[] { mawb }, new BeanPropertyRowMapper<Subscribe>(Subscribe.class));
		if (!subscribes.isEmpty()) {
			Set<String> batchIds = new HashSet<>();
			for (Subscribe string : subscribes) {
				batchIds.add(string.getBatch_id());
			}

			for (String batchId : batchIds) {
				if (subscribes.size() == 1) {
					if (sendEmail(subscribes)) {
						updateState(subscribes);
						subscribeTotal.remove(batchId);// 清楚map缓存
					}
				} else {
					try {
						int rt = subscribeTotal.get(batchId);
						if (--rt == 0) {// 发送邮件
							if (sendEmail(subscribes)) {
								updateState(subscribes);
								subscribeTotal.remove(batchId);// 清楚map缓存
							}

						} else {
							subscribeTotal.put(batchId, rt);
						}
					} catch (Exception e) {
						subscribeTotal.put(batchId, subscribes.size() - 1);
					}
				}
			}

		}

	}

	private void updateState(List<Subscribe> subscribes) {
		StringBuffer buffer = new StringBuffer();
		for (Subscribe subscribe : subscribes) {
			buffer.append("'" + subscribe.getSrMawb() + "',");
		}
		buffer.deleteCharAt(buffer.length() - 1);// 删除最后一个分号
		jdbcTemplate.update("UPDATE subscribe SET sr_state = '1' where sr_mawb in (" + buffer.toString() + ")", new Object[] {});// 更新数据状态
	}

	private boolean sendEmail(List<Subscribe> subscribes) {
		// 调用接口，发送邮件
		HttpHeaders headers = new HttpHeaders();
		headers.add("AppKey", "labelPrint");
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("Guid", UUID.randomUUID().toString().trim().replaceAll("-", ""));
		params.add("MailTo", subscribes.get(0).getSrEmail());
		params.add("MailSubject", "标签打印服务【订阅标签已入库】");// 邮件主题
		StringBuffer buffer = new StringBuffer();
		for (Subscribe subscribe : subscribes) {
			buffer.append(subscribe.getSrMawb() + "		");
		}
		params.add("MailContent", "订阅标签【" + buffer.toString() + "】已入库，请点击<a href='http://" + urlIp + ":8183/subscribe/redirect/" + GsonUtil.GsonString(subscribes) + "'>进入系统</a>查看。");// 邮件内容
		params.add("CreateDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(params, headers);

		ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://maildbsrv.bondex.com.cn:8087/Mail/ToTask/", httpEntity, String.class);
		String rt = responseEntity.getBody();
		return new JsonParser().parse(rt).getAsJsonObject().get("success").getAsString().equals("true") ? true : false;
	}

	//根据用户部门 保存标签数据
	@Override
	public void saveLabel(JsonRootBean jsonRootBean) {
		String json = null;
		try {
			json = GsonUtil.GsonString(jsonRootBean);
			String main = jsonRootBean.getMain();
			String[] hawb = null;
			//main 中获取标签数据
			Keywords keywords = GsonUtil.GsonToBean(main, Keywords.class);
			if (keywords != null) {
				//主单号                                                                                           //目的地                                                                                 //件数                                                                      //起始地
				if (keywords.getPARENT_BILL_NO() != null && keywords.getUNLOAD_CODE() != null && keywords.getPACK_NO() != 0.0 && keywords.getLOAD_CODE() != null) {
					// 主单号为11位，才进行入库
					if (keywords.getPARENT_BILL_NO().length() == 11) {
     						//查询校验重复 list_id mq报文id，用于判断报文是否重复 res=0 不存在 返回值 存在
						    String res= jdbcTemplate.queryForObject("select ifnull((select list_id  from label where list_id=? limit 1 ), 0)", String.class,keywords.getLIST_ID());
						
						    //设置标签绑定模板
						    
						    //1.判断当前用户部门
						    UserInfo userInfo = new UserInfo();
						    String gen_ER = keywords.getGEN_ER();
						    userInfo.setOpid(gen_ER);
						    //获取token
						    String token = securityService.getPublicToken();
						    userInfo.setToken(token);
						    JSONObject jsonObject = securityService.getFrameworkHttp(null, userInfo, NewPowerHttpEnum.GetCompanyInfoOfDeptByOperatorID);
						    String DeptID = jsonObject.getJSONObject("Data").getString("DeptID");
						    String reserve3;
							    	
							String hawb1 = keywords.getBILL_NO() == null ? "" : keywords.getBILL_NO();
							if (!hawb1.equals("")) {
								hawb = hawb1.split("_"); //切割
							} else {
								hawb = new String[] { "", "" };
							}
							
							if (hawb[1].equals("")) {
								reserve3 = "1"; //富士标签
							} else {
								reserve3 = "2"; //邦达普货标签
							}
							
							if("0151".equals(DeptID)){//海程邦达重庆分公司部门id
						    	reserve3="5"; //重庆标签绑定
							}
							
						if ("0".equals(res)) {// 无重复数据，执行插入操作
							String sql = "INSERT INTO label(mawb,hawb,destination,total,airport_departure,flight_date,reserve3,list_id,opid,opid_name,business_type) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
							

							String s1 = keywords.getPARENT_BILL_NO();
							String s2 = "-";
							int i = 3;// 插入到第三位
							String newString = s1.substring(0, i) + s2 + s1.substring(i, s1.length());

							subscribe(newString);// 检查是否是订阅主单号，如果是则发送邮件提示订阅用户
							
							// 标签数据入库
							Object args[] = { newString, hawb[1], keywords.getUNLOAD_CODE(), Math.round(keywords.getPACK_NO()), keywords.getLOAD_CODE(), keywords.getVOYAGE_DATE(), reserve3, keywords.getLIST_ID(), keywords.getGEN_ER(), keywords.getGEN_NAME(), 1 };
							
							int temp = jdbcTemplate.update(sql, args);

							// 入库失败，记录日志
							if (temp <= 0) {
								//插入失败
								jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,updateTime,json) VALUES(?,?,?,?,?,?,?)", new Object[] { newString, hawb[1], 1, "入库未抛异常，但结果返回小于1", "新增", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
							} else {
								//插入成功
								jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,updateTime,json) VALUES(?,?,?,?,?,?,?)", new Object[] { newString, hawb[1], 0, "", "新增", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
							}
							
							//存在重复数据
						} else {// 执行更新操作
							//list-id
							String sql = "update label set mawb=?,hawb=?,destination=?,total=?,airport_departure=?,flight_date=?,reserve3=?,opid=?,opid_name=? where list_id = ?";
							
							String s1 = keywords.getPARENT_BILL_NO();
							String s2 = "-";
							int i = 3;// 插入到第三位
							String newString = s1.substring(0, i) + s2 + s1.substring(i, s1.length());

							// 标签数据入库
							Object args[] = { newString, hawb[1], keywords.getUNLOAD_CODE(), Math.round(keywords.getPACK_NO()), keywords.getLOAD_CODE(), keywords.getVOYAGE_DATE(), reserve3, keywords.getGEN_ER(), keywords.getGEN_NAME(),keywords.getLIST_ID() };
							int temp = jdbcTemplate.update(sql, args);
							
							if (temp <= 0) {
								//更新失败
								jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,updateTime,json) VALUES(?,?,?,?,?,?,?)", new Object[] { newString, hawb[1], 1, "更新未抛异常，但结果返回小于1", "更新", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
							} else {
								//更新成功
								jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,updateTime,json) VALUES(?,?,?,?,?,?,?)", new Object[] { newString, hawb[1], 0, "", "更新", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
							}
						}
					} else {
						jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,updateTime,json) VALUES(?,?,?,?,?,?,?)", new Object[] { keywords.getPARENT_BILL_NO(), "", 1, "该主单号非法", "主单号非法", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
						System.out.println("主单号为：" + keywords.getPARENT_BILL_NO() + "入库失败");
					}
				} else {
					jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,updateTime,json) VALUES(?,?,?,?,?,?,?)", new Object[] { keywords.getPARENT_BILL_NO(), "", 1, "必填数据存在空数据", "必填数据存在空数据", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
				}
			}
		} catch (JsonSyntaxException e) { //Gson转换异常
			e.printStackTrace();
			jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,updateTime,json) VALUES(?,?,?,?,?,?,?)", new Object[] { "", "", 1, "报文json转换失败。入库数据已经回滚", "", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), "" });
		} catch (DataAccessException e) {
			e.printStackTrace();
			jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,updateTime,json) VALUES(?,?,?,?,?,?,?)", new Object[] { "", "", 1, "代码块异常。入库数据已经回滚", "", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
		}
	}


	@Override
	public List<LabelAndTemplate> findByPage(String sql) {
		List<LabelAndTemplate> keyword = templateDataMapper.queryLabelAndTemplate(sql);
		return keyword;
	}

	@Override
	public String getTotel(String sql) {
		String totel = jdbcTemplate.queryForObject(sql,new Object[] {}, String.class);
		return totel;
	}

	@Override
	public void update(Label label) {
		
		StringBuffer sql = new StringBuffer("UPDATE label SET hawb = ?,reserve3=?,destination=? ");
		if (label.getTotal() != null && !label.getTotal().equals("")) {
			sql.append(", total = '" + label.getTotal() + "' ");
		}
		sql.append("WHERE label_id = ?");
		jdbcTemplate.update(sql.toString(), new Object[] { label.getHawb(), label.getReserve3(), label.getDestination(), label.getLabel_id() });
	}

	@Override
	public void delete(List<Label> label) {
		for (Label label2 : label) {
			jdbcTemplate.update("UPDATE label SET reserve1 = ? WHERE label_id = ?", new Object[] { 1, label2.getLabel_id() });
		}
	}
	
	/**
	 * 获取用户绑定的模板信息
	 */
	@Override
	public List<Template> getUserAuthtemplate(Template template) {
		List<JsonResult> userPrintTemplateInfo = ShiroUtils.getUserPrintTemplateInfo();
		String rt = userPrintTemplateInfo.stream().map(res -> res.getReportid()).collect(Collectors.joining("','"));
		String sql = "select * from template where template_id in('" + rt + "')";
		if(StringUtils.isNotBlank(template.getId())){
			sql+="and id ="+template.getId();
		}
		if(StringUtils.isNotBlank(template.getTemplate_id())){
			sql+="and template_id ="+template.getTemplate_id();
		}
		if(StringUtils.isNotBlank(template.getTemplate_name())){
			sql+="and template_name ="+template.getTemplate_name();
		}
		List<Template> templates = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Template>(Template.class));
		if (templates.isEmpty()) {
			templates = new ArrayList<Template>();
			Template template2 = new Template();
			template2.setTemplate_name("未配置打印模板");
			templates.add(template2);
		}
		return templates;
	}

	
	/**
	 * 获取数据库中的模板信息
	 */
	@Override
	public List<Template> getALLTemplate(Template template) {
		String sql= "select * from template where 1=1 ";
		
		if(StringUtils.isNotBlank(template.getId())){
			sql+="and id = '"+template.getId()+"'";
		}
		if(StringUtils.isNotBlank(template.getTemplate_id())){
			sql+="and template_id = '"+template.getTemplate_id()+"'";
		}
		if(StringUtils.isNotBlank(template.getTemplate_name())){
			sql+="and template_name like '%"+template.getTemplate_name()+"%'";
		}
		if(StringUtils.isNotBlank(template.getStatus())){
			sql+="and status ="+template.getStatus();
		}
		
		List<Template> templates  = adminDataCurrentMapper.queryALLTemplate(sql);
		return templates;
	}

	/**
	 * 保存更新打印模板
	 */
	@Override
	public void saveorupdateTempalte(Template template) {
		
		String sql="INSERT INTO template (template_id,template_name,width,height,status) VALUES (?,?,?,?,?)"	
				    +"ON DUPLICATE KEY UPDATE "
					+"template_id=VALUES(template_id),template_name=VALUES(template_name),width=VALUES(width),height=VALUES(height),status=VALUES(status)";
		jdbcTemplate.update(sql,new Object[] {template.getTemplate_id(),template.getTemplate_name(),template.getWidth(),template.getHeight(),template.getStatus()});
		
	}

}
