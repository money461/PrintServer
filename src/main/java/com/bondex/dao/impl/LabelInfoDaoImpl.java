package com.bondex.dao.impl;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.bondex.common.ComEnum;
import com.bondex.common.enums.NewPowerHttpEnum;
import com.bondex.common.enums.ResEnum;
import com.bondex.config.exception.BusinessException;
import com.bondex.config.jdbc.JdbcTemplateSupport;
import com.bondex.dao.LabelInfoDao;
import com.bondex.dao.base.BaseDao;
import com.bondex.entity.Label;
import com.bondex.entity.LabelAndTemplate;
import com.bondex.entity.Subscribe;
import com.bondex.entity.Template;
import com.bondex.entity.log.Log;
import com.bondex.entity.msg.Keywords;
import com.bondex.mapper.AdminDataCurrentMapper;
import com.bondex.mapper.TemplateDataMapper;
import com.bondex.shiro.security.SecurityService;
import com.bondex.shiro.security.entity.JsonResult;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.CommonTool;
import com.bondex.util.GsonUtil;
import com.bondex.util.StringUtils;
import com.bondex.util.shiro.ShiroUtils;
import com.google.gson.JsonParser;

@Component
public class LabelInfoDaoImpl  extends BaseDao<Label, String>implements LabelInfoDao  {
	
	private JdbcTemplateSupport jdbcTemplateSupport;
	
	@Autowired
	public LabelInfoDaoImpl(JdbcTemplateSupport jdbcTemplate) {
		super(jdbcTemplate);
		this.jdbcTemplateSupport = jdbcTemplate;
	}




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
	public Integer saveLabel(String main,Log log) {
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
						    
							//分单号组装
							String hawb  = CommonTool.getSplitResult(keywords.getBILL_NO(), "_");
							if (StringUtils.isBlank(hawb)) {
								reserve3 = "1"; //富士标签
							} else {
								reserve3 = "2"; //邦达普货标签
							}
							
							if("0151".equals(DeptID)){//海程邦达重庆分公司部门id
						    	reserve3="5"; //重庆标签绑定
							}
							
						if ("0".equals(res)) {// 无重复数据，执行插入操作
							String sql = "INSERT INTO label(mawb,hawb,destination,total,airport_departure,flight_date,reserve3,list_id,opid,opid_name,code,code_name,business_type,create_time) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,NOW())";
							
							String mawb  = CommonTool.getMawb(keywords.getPARENT_BILL_NO());

							subscribe(mawb);// 检查是否是订阅主单号，如果是则发送邮件提示订阅用户
							
							// 标签数据入库
							Object args[] = { mawb, hawb, keywords.getUNLOAD_CODE(), Math.round(keywords.getPACK_NO()), keywords.getLOAD_CODE(), keywords.getVOYAGE_DATE(), reserve3, keywords.getLIST_ID(), keywords.getGEN_ER(), keywords.getGEN_NAME(),ComEnum.AirLabel.code,ComEnum.AirLabel.codeName,1 }; //更新的数据默认 business_type=1
							
							int temp = jdbcTemplate.update(sql, args);
							
							log.setMawb(mawb);
							log.setHawb(hawb);
							log.setHandleType("新增");
							return temp;
							
							//存在重复数据
						} else {// 执行更新操作
							//list-id
							String sql = "update label set mawb=?,hawb=?,destination=?,total=?,airport_departure=?,flight_date=?,reserve3=?,opid=?,opid_name=? where list_id = ?";
							String mawb  = CommonTool.getMawb(keywords.getPARENT_BILL_NO());
							// 标签数据入库
							Object args[] = { mawb, hawb, keywords.getUNLOAD_CODE(), Math.round(keywords.getPACK_NO()), keywords.getLOAD_CODE(), keywords.getVOYAGE_DATE(), reserve3, keywords.getGEN_ER(), keywords.getGEN_NAME(),keywords.getLIST_ID() };
							int temp = jdbcTemplate.update(sql, args);
							log.setMawb(mawb);
							log.setHawb(hawb);
							log.setHandleType("更新");
							return temp;
						}
					} else {
						throw new BusinessException("主单号为：" + keywords.getPARENT_BILL_NO() + ",没有11位,不可入库");
					}
				} else {
					throw new BusinessException("主单号/起始地/目的地/件数必填数据存在空值！");
				}
			}else{
				throw new BusinessException("main字段数据格式与代码不完全符合！");
			}
		
	}


	@Override
	public List<LabelAndTemplate> selectLabelByPage(String sql) {
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
		jdbcTemplate.update(sql.toString(), new Object[] { label.getHawb(), label.getReserve3(), label.getDestination(), label.getLabelId() });
	}

	@Override
	public void delete(List<Label> label) {
		for (Label label2 : label) {
			jdbcTemplate.update("UPDATE label SET reserve1 = ? WHERE label_id = ?", new Object[] { 1, label2.getLabelId() });
		}
	}
	
	/**
	 * 获取用户绑定的模板信息
	 */
	@Override
	public List<Template> getUserAuthtemplate(Template template) {
		String sql = "select * from template where template_id in(:templateId)";
		if(StringUtils.isNotBlank(template.getId())){
			sql+=" and id ="+template.getId(); //主键
		}
		if(StringUtils.isNotBlank(template.getTemplateId())){
			sql+=" and template_id = '"+template.getTemplateId()+"'";
		}
		if(StringUtils.isNotBlank(template.getTemplateName())){
			sql+=" and template_name = '"+template.getTemplateName()+"'";
		}
		sql+=" and code = :code ";
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		List<JsonResult> userPrintTemplateInfo = ShiroUtils.getUserPrintTemplateInfo();
		List<String> templateId = userPrintTemplateInfo.stream().map(res -> res.getReportid()).collect(Collectors.toList());
		map.addValue("templateId", templateId);
		map.addValue("code", template.getCode());
		List<Template> templates = jdbcTemplateSupport.query(sql,map, new BeanPropertyRowMapper<Template>(Template.class));
		if (templates.isEmpty()) {
			templates = new ArrayList<Template>();
			Template template2 = new Template();
			template2.setTemplateName("<p style='color:red'>未配置打印模板</p>");
			templates.add(template2);
		}
		return templates;
	}

	

	
	@Override
	public Template checkUseTemplate(String templateId) {
		List<Template> templatelist = jdbcTemplate.query("select * from template where id = ? or template_id=?",	new Object[] { templateId, templateId },new BeanPropertyRowMapper<Template>(Template.class));
		if(null==templatelist || templatelist.size()==0){
			throw new BusinessException(ResEnum.FAIL.CODE, "请指定打印模板！");
		}
		List<JsonResult> userPrintTemplateInfo = ShiroUtils.getUserPrintTemplateInfo();
		Template template = templatelist.get(0);
		if("1".equals(template.getStatus())){
			throw new BusinessException(ResEnum.FAIL.CODE, "打印模板已被禁用,请联系管理员！");
		}
		
		//判断是否有权限
		JsonResult result = userPrintTemplateInfo.stream().filter(re ->re.getReportid().equals(template.getTemplateId())).findFirst().orElse(null);
		
		if(null==result){
			throw new BusinessException(ResEnum.FORBIDDEN.CODE, "未配置打印模板使用权限！");
		}
		return template;
		
		
	}

}
