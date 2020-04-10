package com.bondex.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bondex.common.ComEnum;
import com.bondex.config.exception.BusinessException;
import com.bondex.dao.LabelInfoDao;
import com.bondex.dao.LogInfoDao;
import com.bondex.dao.PaiangDao;
import com.bondex.entity.Label;
import com.bondex.entity.LabelAndTemplate;
import com.bondex.entity.log.Log;
import com.bondex.service.PaiangService;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.CollectionUtils;
import com.bondex.util.CommonTool;
import com.bondex.util.GsonUtil;
import com.bondex.util.StringUtils;
import com.bondex.util.shiro.ShiroUtils;

@Service(value="paiangServiceImple")
@Transactional(rollbackFor = Exception.class)
public class PaiangServiceImple implements PaiangService {
	
	@Autowired
	private LogInfoDao logInfoDao;

	/**
	 * org.slf4j.Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${system.interface.paiang}")
	private String paiangaddress;
	
	@Autowired
	private LabelInfoDao labelInfoDao;
	
	@Autowired
	private PaiangDao paiangDao;
	
	//派昂调用接口获取数据
	@SuppressWarnings("rawtypes")
	@Override
	public List<LabelAndTemplate> selectPaiangLabelByPage(Label label) {
		
			UserInfo userInfo = ShiroUtils.getUserInfo();
			String opid = userInfo.getOpid();
			List<String> opidData = userInfo.getOpidData();
			String opids = opidData.stream().collect(Collectors.joining("','")); //获取用户部门所有的opid

			//拼接SQL语句
			StringBuffer sql = new StringBuffer();
			sql.append("select label.*, t.template_id,	t.template_name  from label LEFT JOIN template t  on t.id = label.reserve3 where label.opid in ( '" + opids + "') ");
			sql.append(" and label.business_type = 0 "); //是派昂医药的数据
			sql.append(" and label.reserve1 = '0' "); //未删除
		
			Map<String, Object> params = label.getParams();
			if(StringUtils.isNotEmpty(params)&& StringUtils.isNotBlank((String)params.get("startTime"))){
				sql.append(" AND date_format(label.EDeparture,'%y%m%d') >= date_format('"+params.get("startTime")+"','%y%m%d')");
			}
			if(StringUtils.isNotEmpty(params) && StringUtils.isNotBlank((String)params.get("endTime"))){
				sql.append(" AND date_format(label.EDeparture,'%y%m%d') <= date_format('"+params.get("endTime")+"','%y%m%d')");
			}
			
			String mblNo = label.getMBLNo(); //主单号
			if(StringUtils.isNotBlank(mblNo)){
				String[] split = mblNo.split(",");
				List<String> list = CollectionUtils.array2List(split);
				mblNo= list.stream().collect(Collectors.joining("','")); 
				sql.append(" AND label.MBLNo in ('"+mblNo+"')");
			}
			
			String takeCargoNo = label.getTakeCargoNo();
			if(StringUtils.isNotBlank(takeCargoNo)){
						String[] split = takeCargoNo.split(",");
						List<String> list = CollectionUtils.array2List(split);
						takeCargoNo= list.stream().collect(Collectors.joining("','")); 
					sql.append(" AND label.TakeCargoNo in ('"+takeCargoNo+"')");
			}
			if (label.getIsPrint() != null && !label.getIsPrint().equals("undefined") && !label.getIsPrint().equals("")) {
				sql.append("and label.is_print = '" + label.getIsPrint() + "' ");
			} else {
				sql.append("and label.is_print = '0' ");
			}
		    List<LabelAndTemplate> datalist = labelInfoDao.selectLabelByPage(sql.toString());
		    
			 //分单号=key
			 TreeMap<String, List<LabelAndTemplate>> treeMap = new TreeMap<String, List<LabelAndTemplate>>();
			 
			 //封装打印模板并且对数据做出分类
			 for (LabelAndTemplate labelAndTemplate : datalist) {
				 //获取redis数据 修改件数
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
			             //总件数
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
			 
			 
			 String reString =  GsonUtil.GsonString(datalist);
			 logger.debug(reString);
			 return datalist;
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


	/**
	 * 保存入库
	 */
	@Transactional(rollbackFor=Exception.class)
	@Override
	public void paiangSaveService(String message) {
		Log log = new Log();
		Date date=  Date.from(LocalDateTime.now().toInstant(ZoneOffset.of("+8"))); //默认时区为东8区
		log.setUpdateTime(date);
		log.setCreateTime(date);
		log.setJson(message);
		log.setStatus(1);
		log.setCode(ComEnum.PaiangLabel.code); //监听业务固定 所以标签固定写死 
		log.setCodeName(ComEnum.PaiangLabel.codeName);
		try {
			JSONObject parseObject = JSONObject.parseObject(message);
			JSONObject jsonObjectData = parseObject.getJSONObject("Main").getJSONObject("Data");
			JSONObject jsonObject  = jsonObjectData.getJSONObject("body");
			String MBLNo = jsonObject.getString("MBLNo"); //主单号
			String TakeCargoBillNo = jsonObject.getString("TakeCargoBillNo");//分单号
			String[] TakeCargoBillNostr = TakeCargoBillNo.split(" "); //分割分单号
			
			JSONObject jsonObjecthead = jsonObjectData.getJSONObject("head");
			String createOP = jsonObjecthead.getString("createOP");//createOP
			createOP = CommonTool.getSplitResult(createOP, "]");
			
			String createOPid = jsonObjecthead.getString("createOPid");//createOPid
			
			JSONObject jsonObjectmain = parseObject.getJSONObject("Head");
			String seqNo = jsonObjectmain.getString("SeqNo");//SeqNo
			String senderName = jsonObjectmain.getString("SenderName");//BOE
			String reciverName = jsonObjectmain.getString("ReciverName");//派昂管理服务
			String docTypeName = jsonObjectmain.getString("DocTypeName");//派昂医药
			log.setMawb(MBLNo);
			log.setHawb(TakeCargoBillNo);
			log.setSeqNo(seqNo);
			log.setSenderName(senderName);
			log.setReciverName(reciverName);
			log.setDoctypeName(docTypeName);
			String SendAddress = jsonObject.getString("SendAddress");
			JSONArray jsonArray = jsonObject.getJSONArray("CargoList");
			List<Label> list = new ArrayList<Label>();
			int size = jsonArray.size();
			for (int i=0; i<size;i++) {
				JSONObject object = (JSONObject)jsonArray.get(i);
				Label label = new Label();
				label.setMBLNo(MBLNo); //主单号
				String hawb="";
				if(TakeCargoBillNostr.length<=i){
					hawb = TakeCargoBillNostr[0];
				}else{
					hawb = TakeCargoBillNostr[i];
				}
				label.setHawb(hawb); //分单号需要拆分
				label.setOpid(createOPid); //操作人
				label.setOpidName(createOP); //姓名
				label.setSendAddress(SendAddress); //发送地址
				String packages = object.getString("QTY");
				label.setPackages(new BigDecimal(packages)); //件数
				Date EDeparture = object.getDate("EDeparture");
				//Date toDate = LocalDateTimeUtils.convertTextToDate(EDeparture, "yyyy-MM-dd");
				label.setEDeparture(EDeparture); //发货日期
				label.setTakeCargoNo(object.getString("TakeCargoNo")); //交货凭证号
				label.setRecCustomerName(object.getString("RecCustomerName")); //收货人
				label.setAirportDeparture("SIA"); //默认
				label.setReserve1("0");//默认未删除
				label.setReserve3("4");//默认保存派昂标签
				label.setBusinessType(0);//默认标识派昂标签
				label.setCreateTime(date); //创建时间
				label.setCode(ComEnum.PaiangLabel.code); //监听业务固定 所以标签固定写死 
				label.setCodeName(ComEnum.PaiangLabel.codeName);
				list.add(label);
			}
			
			//删除MBLNo 的数据
			paiangDao.delete(list);
			
			paiangDao.savePaiangData(list);
			log.setHandleType("新增");
			log.setStatus(0); //成功
			
		} catch (JSONException e) {
			//JSON转换异常
			log.setDetail("报文json转换失败。入库数据已经回滚");
			
		} catch (BusinessException e) { //自定义异常
			log.setStatus(3);
			log.setDetail(e.getMessage());
			
		} catch (Exception e) {
			log.setDetail("数据入库未知异常！");
			
		}finally {
			logInfoDao.insertLableLog(log); //日志入库
		}
		
	}

	@Override
	public void updatePaiangData(List<Label> datalist) {
		paiangDao.updatePaiangData(datalist);
	}

	
	/**
	 *  //调用接口获取数据
			Map<String, Object> params = new HashMap<>();
			params = label.getParams();
			label.setParams(null);
			JSONObject jsonStu = (JSONObject)JSONObject.toJSON(label);
			jsonStu.putAll(params);
			 PageDomain pageDomain = TableSupport.buildPageRequest();
			 jsonStu.put("pageNum", pageDomain.getPageNum());
			 jsonStu.put("pageSize", pageDomain.getPageSize());
			 jsonStu.put("sort", pageDomain.getOrderBy(false));
			 jsonStu.put("order", pageDomain.getIsAsc());
			 
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
			 String code = parseObject.getString("code");
			 if(!"0".equals(code)){
				 List<LabelAndTemplate> datalist = new ArrayList<>();
				 return datalist;
			 }
			 
			 JSONArray datarows = parseObject.getJSONArray("rows");
			 Long total = parseObject.getLong("total");
			 
			 //反序列化
			 List<LabelAndTemplate> datalist = JSONObject.parseArray(datarows.toJSONString(), LabelAndTemplate.class);
			 
			 System.out.println(JsonUtil.objToStr(datalist));
	 */
	
	
}
