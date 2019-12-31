package com.bondex.jdbc.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.bondex.entity.Subscribe;
import com.bondex.jdbc.dao.LabelInfoDao;
import com.bondex.jdbc.entity.JsonRootBean;
import com.bondex.jdbc.entity.Keywords;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.LabelAndTemplate;
import com.bondex.jdbc.entity.Template;
import com.bondex.mapper.TemplateDataMapper;
import com.bondex.util.GsonUtil;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

@Component
public class LabelInfoDaoImpl implements LabelInfoDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private RestTemplate restTemplate;
	
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

	@Override
	public void saveLabel(JsonRootBean jsonRootBean) {
		String json = null;
		try {
			json = GsonUtil.GsonString(jsonRootBean);
			String main = jsonRootBean.getMain();
			String[] hawb = null;
			Keywords keywords = GsonUtil.GsonToBean(main, Keywords.class);
			if (keywords != null) {
				if (keywords.getPARENT_BILL_NO() != null && keywords.getUNLOAD_CODE() != null && keywords.getPACK_NO() != 0.0 && keywords.getLOAD_CODE() != null) {
					// 主单号为11位，才进行入库
					if (keywords.getPARENT_BILL_NO().length() == 11) {
						List<Template> template = jdbcTemplate.query("select * from label where list_id = ?", new Object[] { keywords.getLIST_ID() }, new BeanPropertyRowMapper<Template>(Template.class));
						if (template.isEmpty()) {// 无重复数据，执行插入操作
							String sql = "INSERT INTO label(mawb,hawb,destination,total,airport_departure,flight_date,reserve3,list_id,opid,opid_name,business_type) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
							String hawb1 = keywords.getBILL_NO() == null ? "" : keywords.getBILL_NO();
							if (!hawb1.equals("")) {
								hawb = hawb1.split("_");
							} else {
								hawb = new String[] { "", "" };
							}
							String reserve3;
							if (hawb[1].equals("")) {
								reserve3 = "1";
							} else {
								reserve3 = "2";
							}

							String s1 = keywords.getPARENT_BILL_NO();
							String s2 = "-";
							int i = 3;// 插入到第三位
							String newString = s1.substring(0, i) + s2 + s1.substring(i, s1.length());

							subscribe(newString);// 检查是否是订阅主单号，如果是则发送邮件提示订阅用户
							Object args[] = { newString, hawb[1], keywords.getUNLOAD_CODE(), Math.round(keywords.getPACK_NO()), keywords.getLOAD_CODE(), keywords.getVOYAGE_DATE(), reserve3, keywords.getLIST_ID(), keywords.getGEN_ER(), keywords.getGEN_NAME(), 1 };
							int temp = jdbcTemplate.update(sql, args);// 标签数据入库

							// 入库失败，记录日志
							if (temp <= 0) {
								jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,update_data,json) VALUES(?,?,?,?,?,?,?)", new Object[] { newString, hawb[1], 1, "入库未抛异常，但结果返回小于1", "新增", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
							} else {
								jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,update_data,json) VALUES(?,?,?,?,?,?,?)", new Object[] { newString, hawb[1], 0, "", "新增", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
							}
						} else {// 执行更新操作
							String sql = "update label set mawb=?,hawb=?,destination=?,total=?,airport_departure=?,flight_date=?,reserve3=?,opid=?,opid_name=? where list_id = ?";
							String hawb1 = keywords.getBILL_NO() == null ? "" : keywords.getBILL_NO();
							if (!hawb1.equals("")) {
								hawb = hawb1.split("_");
							} else {
								hawb = new String[] { "", "" };
							}
							String reserve3;
							if (hawb[1].equals("")) {
								reserve3 = "1";
							} else {
								reserve3 = "2";
							}

							String s1 = keywords.getPARENT_BILL_NO();
							String s2 = "-";
							int i = 3;// 插入到第三位
							String newString = s1.substring(0, i) + s2 + s1.substring(i, s1.length());

							Object args[] = { newString, hawb[1], keywords.getUNLOAD_CODE(), Math.round(keywords.getPACK_NO()), keywords.getLOAD_CODE(), keywords.getVOYAGE_DATE(), reserve3, keywords.getLIST_ID(), keywords.getGEN_ER(), keywords.getGEN_NAME() };
							int temp = jdbcTemplate.update(sql, args);// 标签数据入库
							if (temp <= 0) {
								jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,update_data,json) VALUES(?,?,?,?,?,?,?)", new Object[] { newString, hawb[1], 1, "更新未抛异常，但结果返回小于1", "更新", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
							} else {
								jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,update_data,json) VALUES(?,?,?,?,?,?,?)", new Object[] { newString, hawb[1], 0, "", "更新", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
							}
						}
					} else {
						jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,update_data,json) VALUES(?,?,?,?,?,?,?)", new Object[] { keywords.getPARENT_BILL_NO(), "", 1, "该主单号非法", "主单号非法", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
						System.out.println("主单号为：" + keywords.getPARENT_BILL_NO() + "入库失败");
					}
				} else {
					jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,update_data,json) VALUES(?,?,?,?,?,?,?)", new Object[] { keywords.getPARENT_BILL_NO(), "", 1, "必填数据存在空数据", "必填数据存在空数据", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
				}
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,update_data,json) VALUES(?,?,?,?,?,?,?)", new Object[] { "", "", 1, "报文json转换失败。入库数据已经回滚", "", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), "" });
		} catch (DataAccessException e) {
			e.printStackTrace();
			jdbcTemplate.update("insert into log(mawb,hawb,state,detail,handle_type,update_data,json) VALUES(?,?,?,?,?,?,?)", new Object[] { "", "", 1, "代码块异常。入库数据已经回滚", "", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), json });
		}
	}

	public String getUuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	@Override
	public List<LabelAndTemplate> findByPage(String sql) {
		//select * from label LEFT JOIN template t  on t.id = label.reserve3 where t.template_id in ('a357f19f-6a1f-4171-ab8e-1f1a2b77377a','988b4162-00af-4924-a6fe-6b310d161900','2b7eddea-d953-4186-bc3b-a642940d57ea','4e2b8f59-9858-4297-962f-6ee4862085aa')  and business_type = 0 and reserve1 = '0' and airport_departure = (SELECT load_code from load_code where region_parent_code = (select parent_code from region where region_id = (SELECT office_id from default_region where opid = '0001655' and type = 1))) ORDER BY is_print,CREATE_time desc limit 0,10
		List<LabelAndTemplate> keyword = templateDataMapper.queryLabelAndTemplate(sql);
//		List<LabelAndTemplate> keyword = jdbcTemplate.query(sql,new Object[] {}, new BeanPropertyRowMapper<LabelAndTemplate>(LabelAndTemplate.class));
		return keyword;
	}

	@Override
	public String getTotel(String sql) {
		String totel = jdbcTemplate.queryForObject(sql,new Object[] {}, String.class);
		return totel;
	}

	@Override
	public void update(Label label) {
		/*
		 * if (label.getHawb().equals("")) {
		 * jdbcTemplate.update("UPDATE label SET hawb = ?,reserve3=? WHERE label_id = ?"
		 * , new Object[] { label.getHawb(), label.getReserve3(), label.getLabel_id()
		 * }); } else { jdbcTemplate.
		 * update("UPDATE label SET hawb = ?,reserve3='2' WHERE label_id = ?", new
		 * Object[] { label.getHawb(), label.getLabel_id() }); }
		 */
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

	@Override
	public Template getTemplate(String rt, String id) {
		List<Template> template = jdbcTemplate.query("select * from template where id = ? and template_id in(" + rt + ") or template_name =?", new Object[] { id, id }, new BeanPropertyRowMapper<Template>(Template.class));
		if (template.isEmpty()) {
			Template template2 = new Template();
			template2.setTemplate_name("未配置打印模板");
			return template2;
		} else {
			return template.get(0);
		}
	}

}
