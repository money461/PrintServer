package com.bondex.shiro.security;

import java.lang.reflect.Type;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bondex.cas.SpringCasAutoconfig;
import com.bondex.common.Common;
import com.bondex.common.enums.NewPowerHttpEnum;
import com.bondex.config.CacheManagerConfig;
import com.bondex.config.restTemplate.HttpRestTemplateUtil;
import com.bondex.entity.page.Datagrid;
import com.bondex.shiro.security.entity.OperatorPagePermission;
import com.bondex.shiro.security.entity.Opid;
import com.bondex.shiro.security.entity.PrintTemplatePremission;
import com.bondex.shiro.security.entity.SecurityModel;
import com.bondex.shiro.security.entity.TokenResult;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.Axis2WebServiceClient;
import com.bondex.util.GsonUtil;
import com.bondex.util.HttpClient;
import com.google.gson.reflect.TypeToken;
/**
 * 权限获取模块
 * @author Qianli
 * 
 * 2019年12月26日 上午10:31:10
 */
@Component
public class SecurityService {

	private  final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("myRestTemplate")
	private RestTemplate myRestTemplate;
	
	@Autowired
	@Qualifier(CacheManagerConfig.CacheManagerName.EHCACHE_CACHE_MAANGER)
	private EhCacheManager cacheManager;
	
	private String applicationId; //应用id
	
	private String frameworkapi;//framework权限获取地址
	
	private SpringCasAutoconfig springCasAutoconfig;
	
	@Resource(name="taskExecutor")
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	public SecurityService(SpringCasAutoconfig springCasAutoconfig) {
		super();
		this.springCasAutoconfig = springCasAutoconfig;
		this.applicationId = springCasAutoconfig.getApplicationId();
		this.frameworkapi = springCasAutoconfig.getFrameworkapi();
	}
	
	
	//获取用户信息 存入session 并且绑定最新与Token opid
	public UserInfo setSerlvetContext(String username){
		UserInfo userInfo=null;
				if (null != username) {
					Session session = SecurityUtils.getSubject().getSession();
					// log.debug(account);
					TokenResult result = GsonUtil.GsonToBean(username, TokenResult.class);
					List<UserInfo> userInfoList = result.getMessage();
					userInfo = userInfoList.get(0);
					
					List<Opid> opids = new ArrayList<Opid>();
					Map<String, String> opidsMap = userInfo.getOpids();
				
					for (String opid : opidsMap.keySet()) {
						Opid opid2 = new Opid(opid, opidsMap.get(opid));
						opids.add(opid2);
					}
					// 将opids 返回当前用户对应的一至多个操作ID
					userInfo.setAllOpid(opids);
					
					// 绑定用户默认的opid
					String getBindingOpid = GetBindingOpid(userInfo.getToken());
					if(userInfo.getAllOpid().size()==1){ //只有一个号时，绑定默认的 
						getBindingOpid = userInfo.getAllOpid().get(0).getOpid();//默认第一个号作为登陆
						BindingOpid(getBindingOpid, userInfo.getToken()); //绑定一个默认的opid
					}
					 userInfo.setOpid(getBindingOpid);
					 userInfo.setOpname(userInfo.getOpids().get(getBindingOpid));
					//获取用户的操作及系统获取操作数据权限
					if(com.bondex.util.StringUtils.isNotBlank(getBindingOpid)){
						SortedSet<SecurityModel> list=null;
					 try {
						//部门下的opid集合
						JSONObject object = getFrameworkHttp(null, userInfo, NewPowerHttpEnum.GetUserRoleOp);
						//JSONObject object  = JSONObject.parseObject(ReadTxtFile.readJsonFromClassPath("data/RoleData.json",String.class));
						JSONArray jsonArray = object.getJSONArray("Data");
						List<String> opidData = JSONObject.parseArray( JSONObject.toJSONString(jsonArray), String.class);
						userInfo.setOpidData(opidData);
						//用户的菜单信息
						JSONObject object2 = getFrameworkHttp(null, userInfo, NewPowerHttpEnum.GetHasPermissionModuleList);
						JSONArray array = object2.getJSONArray("Data");
						Type objectTypemenu = new TypeToken<List<SecurityModel>>() {}.getType();
						List<SecurityModel> securityModels =  GsonUtil.getGson().fromJson(array.toJSONString(), objectTypemenu);
						userInfo.setSecurityModels(securityModels);
						List<String> pageCodes = securityModels.stream().filter(x ->x.getPageCode().endsWith(Common.PageCode_Suffix)).map(res -> res.getPageCode()).collect(Collectors.toList());
						userInfo.setPageCodes(pageCodes); //页面code
						list = securityModelTreeUtil(securityModels);
						
//							String  menu = (String)ReadTxtFile.readJsonFromClassPath("data/menu.json",String.class);
//							Type objectTypemenu = new TypeToken<List<SecurityModel>>() {}.getType();
//							List<SecurityModel>  securityModels =  GsonUtil.getGson().fromJson(menu, objectTypemenu);
						   /**父子菜单整理 */
//							list= securityModelTreeUtil(securityModels);
							
							
						} catch (Exception e) {
							e.printStackTrace();
						}	
						
						System.out.println(GsonUtil.GsonString(list));
						userInfo.setMenus(list);
						
					} 
					//初始页面 的时候弹出 opids 提供用户选择
					Datagrid<Opid> datagrid = new Datagrid<Opid>();
					datagrid.setTotal((long)userInfoList.size());
					datagrid.setRows(userInfo.getAllOpid());
					session.setAttribute(Common.Session_opids, datagrid);
					session.setAttribute(Common.Session_thisOpid, getBindingOpid);
					session.setAttribute(Common.Session_thisUsername, userInfo.getOpname());
					// 将用户信息存入session中
					session.setAttribute(Common.Session_UserInfo, userInfo);
					
					 log.debug("CAS登陆认证成功信息:{}",GsonUtil.GsonString(userInfo));
				}
				return userInfo;
			
			}
	
	

	/**
	 * 获取用户权限，并存放session
	 * 
	 * @param session
	 * @param opids
	 * 
	 * @throws RemoteException
	 */
	public Map<String, Object> getSecurity( UserInfo userInfo,Set<String> premissionSet) {
		
		
		// 获取当前用户与token绑定的opid
		String opid = GetBindingOpid(userInfo.getToken());
		if(StringUtils.isBlank(opid)){
			return null;
		}
		//List<Opid> opids = userInfo.getAllOpid(); //获取所有的opid
		userInfo.setOpid(opid);
		userInfo.setOpname(userInfo.getOpids().get(opid));
		
		CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(() -> {
			 System.out.println("线程-"+Thread.currentThread().getName() +"执行获取功能权限");
			// 获取用户功能权限 包括所有的按钮权限
			JSONObject object = getFrameworkHttp(null, userInfo, NewPowerHttpEnum.GetOperatorPagePermission);
			JSONArray array = object.getJSONArray("Data");
			Type objectType = new TypeToken<List<OperatorPagePermission>>() {}.getType();
			List<OperatorPagePermission> operatorpremission = GsonUtil.getGson().fromJson(array.toJSONString(), objectType);
			
			//获取所有的BtnNames 作为权限Code校验
			List<String> pageCodes = operatorpremission.stream().map(x -> x.getPageCode()).collect(Collectors.toList());
			premissionSet.addAll(pageCodes);
			List<String> BtnNames = operatorpremission.stream().map(x -> x.getBtnNames()).collect(Collectors.toList());
			premissionSet.addAll(BtnNames);
			
		   return operatorpremission;
			
		},threadPoolTaskExecutor);	
	
		
		CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(() -> {
			 System.out.println("线程-"+Thread.currentThread().getName() +"执行获取打印模板权限");
			//打印按钮权限
			JSONObject object2 = getFrameworkHttp(null, userInfo, NewPowerHttpEnum.GetModulePrintButton);
			JSONArray array2 = object2.getJSONArray("Data");
			
			//获取标签打印功能权限
			JSONArray collect = array2.stream().filter(obj -> {
				 JSONObject jsonObj = (JSONObject)obj;
				 if(applicationId.equals(jsonObj.getString("ApplicationID"))){
					return true;
				 }else{
					 return false;
				 }
				
			}).collect(Collectors.toCollection(JSONArray::new));
			
			JSONObject printJsonObject = (JSONObject)collect.get(0);
			LinkedHashMap<String, String> userReportMap = new LinkedHashMap<>();
			userReportMap.put("OpId", opid); // 用户岗位操作号（必填
			userReportMap.put("ApplicationId", applicationId); 
			userReportMap.put("PageCode", printJsonObject.getString("PageCode")); //AirPrintLabel
			userReportMap.put("BtnNames",  printJsonObject.getString("BtnNames")); // 打印按钮名称（必填 AirPrintLabel.label-print
			
			JSONObject userReportjsonObject = Axis2WebServiceClient.getWebServicePararm(userReportMap, "GetUserReport").getJSONObject("GetUserReportResult");
			
			JSONArray jsonArray = userReportjsonObject.getJSONArray("JsonResult");
			
			// 转json对象
			Type printButton = new TypeToken<List<PrintTemplatePremission>>() {}.getType();
			List<PrintTemplatePremission> printButtons =  GsonUtil.getGson().fromJson(jsonArray.toJSONString(), printButton);
			
			return printButtons;
			
		},threadPoolTaskExecutor);	
		
		//权限map
		CompletableFuture<Map<String, Object>> future3 =
                future1.thenCombine(future2, (t1Result, t2Result) -> {
                	    System.out.println("线程-"+Thread.currentThread().getName() +"执行合并结果");
	                	Map<String, Object> authorizationMap = new HashMap<String, Object>();
	                	authorizationMap.put(opid + Common.UserSecurity_Model, t1Result);// 用户功能权限
	                	authorizationMap.put(opid + Common.UserSecurity_PrintButton, t2Result);// 用户打印权限
                        return authorizationMap;
                });
		
		System.out.println("线程等待所有子线程执行完成-"+Thread.currentThread().getName());
		Map<String, Object> map = null;
		try {
			map = future3.get(10L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	

	/**
	 * 获取数据
	 * @param paramap 接口参数
	 * @param userInfo 当前用户信息
	 * @param HttpEnum 请求的接口方法名称 枚举中设置
	 * @return
	 */
	public JSONObject getFrameworkHttp(Map<String, String> paramap, UserInfo userInfo, NewPowerHttpEnum HttpEnum) {
		JSONObject jsonObject =null;
		StringBuffer rootUrlStr = new StringBuffer(frameworkapi);

		JSONObject param = new JSONObject();
		param.put("Token", userInfo.getToken()); //cas token
		switch (HttpEnum) {
		case GetHasPermissionModuleList:
			rootUrlStr.append(NewPowerHttpEnum.GetHasPermissionModuleList.url);
			//封装参数
			param.put("ApplicationID", applicationId);
			param.put("OperatorID", userInfo.getOpid());
			urlGetMethodFrameWork(rootUrlStr, param); //拼接url
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetHasPermissionPageButton:
			rootUrlStr.append(NewPowerHttpEnum.GetHasPermissionPageButton.url);
			param.put("ApplicationID", applicationId);
			param.put("PageCode", paramap.get("PageCode"));
			param.put("OperatorID", userInfo.getOpid());
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetPageButtonByPermissionValue:
			rootUrlStr.append(NewPowerHttpEnum.GetPageButtonByPermissionValue.url);
			param.put("ApplicationID", applicationId);
			param.put("PageCode", paramap.get("PageCode")); //页面代码code
			param.put("PreButtonName", paramap.get("PreButtonName")); //前缀
			param.put("PermissionValue", paramap.get("PermissionValue")); //访问权限值
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetOperatorPagePermission:
			rootUrlStr.append(NewPowerHttpEnum.GetOperatorPagePermission.url);
			param.put("ApplicationID", applicationId);
			//param.put("PageCode", paramap.get("PageCode")); //页面代码code
			param.put("OperatorID", userInfo.getOpid()); //操作id
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetUserRoleOp:
			rootUrlStr.append(NewPowerHttpEnum.GetUserRoleOp.url);
			param.put("ApplicationID", applicationId);
			param.put("OperatorID", userInfo.getOpid());
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetCompanyInfoOfDeptByOperatorID:
			rootUrlStr.append(NewPowerHttpEnum.GetCompanyInfoOfDeptByOperatorID.url);
			param.put("OperatorID", userInfo.getOpid());
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetFlowNo:
			rootUrlStr.append(NewPowerHttpEnum.GetFlowNo.url);
			param.put("ApplicationID", applicationId);
			param.put("EmsID", userInfo.getOpid());
			param.put("ProFile", paramap.get("ProfitId"));
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetOperator:
			rootUrlStr.append(NewPowerHttpEnum.GetOperator.url);
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetModulePrintButton:
			rootUrlStr.append(NewPowerHttpEnum.GetModulePrintButton.url);
			HttpHeaders headers = new HttpHeaders();
		    headers.add("Accept", MediaType.ALL_VALUE);
		    headers.add("Token",userInfo.getToken());
		    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			jsonObject = HttpRestTemplateUtil.doPost(rootUrlStr.toString(),new LinkedMultiValueMap<>(), headers,JSONObject.class);
			return jsonObject;
		default:
			break;
		}
		return jsonObject;
	}

	//GET请求拼接参数 获取完整的ApiUrl
	private void urlGetMethodFrameWork(StringBuffer rootUrlStr, JSONObject objpare) {
		Set<String> set = objpare.keySet();
		String[] keys = new String[set.size()];
		set.toArray(keys);
		for (int i = 0; i < keys.length; i++) {
			if (i == 0) {
				rootUrlStr.append("?");
			}
			String keyStr = keys[i];
			rootUrlStr.append(keyStr + "=" + objpare.getString(keyStr));
			if (keys.length - 1 != i) {
				rootUrlStr.append("&");
			}
		}
	}

	
	
	/**
	 * 获取当前用户最新的操作ID
	 * 
	 * @param token
	 * @return
	 */
	public String GetBindingOpid(String token) {

		if (StringUtils.isNotBlank(token)) {

			StringBuffer sb2 = new StringBuffer();
			String param = sb2.append("token=").append(token).toString();
			try {
				String res =HttpClient.sendPost("http://cas.bondex.com.cn:8080/takeopid.jsp", param);

				return res;
			} catch (Exception e) {
				log.debug("获取当前绑定token:{}的opid失败！原因：{}", token, e.getMessage());

			} finally {

			}

		}
		return null;

	}

	
	
	
	
	/**
	 * 绑定opid 登录CAS系统之后设置或切换操作ID
	 * 
	 * @param opid
	 * @param token
	 *            CAS登录成功后返回的token
	 * @return
	 */
	public Boolean BindingOpid(String opid, String token) {

		if (StringUtils.isNotBlank(opid) && StringUtils.isNotBlank(token)) {

			StringBuffer sb2 = new StringBuffer();
			String param = sb2.append("opid=").append(opid).append("&").append("token=").append(token).toString();
			try {
				String res = HttpClient.sendPost("http://cas.bondex.com.cn:8080/putopid.jsp", param);

				if (null != res && "ok".equalsIgnoreCase(res)) {
					log.debug("绑定opid:{},token:{}绑定成功！", opid, token);
					return true;
				}
			} catch (Exception e) {
				log.debug("绑定opid:{},token:{}失败！原因：{}", opid, token, e.getMessage());

			} finally {

			}

		}

		return false;

	}
	
	/**
	 * CAS 登陆成功后 调用cas接口，获取带opids的用户信息
	 * 
	 * @param map
	 * @param session
	 * @return
	 */
	private UserInfo getAllOpids(String token) {
	
		String rt = HttpClient.sendPost(springCasAutoconfig.getLoadMore(), "token=" + token);
		// log.debug(rt);
	
		TokenResult idResult = GsonUtil.GsonToBean(rt, TokenResult.class);
		List<UserInfo> userInfoList = idResult.getMessage();
		UserInfo userInfo = userInfoList.get(0);
	
		List<Opid> opids = new ArrayList<Opid>();
	
		Map<String, String> opidsMap = userInfo.getOpids();
	
		for (String opid : opidsMap.keySet()) {
			Opid opid2 = new Opid(opid, opidsMap.get(opid));
			opids.add(opid2);
		}
		
		// 将opids 返回当前用户对应的一至多个操作ID
		userInfo.setAllOpid(opids);
		if (null == userInfo.getOpid()) {
			userInfo.setOpid(opids.get(0).getOpid()); //多个号设置默认的绑定的opid
		}
		userInfo.setToken(token);
		return userInfo;
	}
	
	/**
	 * 获取公共账户token
	 * @return
	 */
	public String getPublicToken() {
		
		org.apache.shiro.cache.Cache<Object, Object> cache = cacheManager.getCache("PublicTokenCache");
		String token= (String) cache.get("SystemPublicToken");
		if(StringUtils.isNoneBlank(token)){
			return token;
		}
		ResponseEntity<JSONObject > res = myRestTemplate.getForEntity(springCasAutoconfig.getCasPublicUrl(), JSONObject.class);
		JSONObject body = res.getBody();
		if (null!=body && body.getBoolean("success")) {
		    JSONArray message =body.getJSONArray("message");
		    JSONObject json = (JSONObject) message.get(0);
		    token = json.getString("token");
		    cache.put("SystemPublicToken", token);
		}
		return token;
	}
	
	/**
	 * 检查用户是否为系统管理员
	 * @param userInfo
	 * @return
	 */
	private String CheckSystemAdmin(UserInfo userInfo) {
		
		StringBuffer sb2 = new StringBuffer();
		String url = sb2.append(springCasAutoconfig.getIsadmin()).append("?").append("email=").append(userInfo.getEmail()).toString();
		try {
			String res = myRestTemplate.getForObject(url,String.class);
			return res;
		} catch (Exception e) {
			log.debug("判断是否为系统管理员失败！原因：{}", e.getMessage());

		}
		
		return "1";
	}

	
	
	private SortedSet<SecurityModel> securityModelTreeUtil(List<SecurityModel> securityModels) {
		
		SortedSet<SecurityModel> arrayList = getInstanceTreeSet();
		for (SecurityModel bean : securityModels) {
			if ("0".equals(bean.getParentID())){ //最高级父节点开始循环
				bean.setIsClosed("true");//不展开
				SortedSet<SecurityModel> list = buildTree(securityModels, bean.getModuleID());
				bean.setChildren(list);
				arrayList.add(bean);
			}
		}
		return arrayList;
	}
	
	/**
	 * 递归
	 * @param treeBeans 不包含最高层次节点的集合
	 * @param pId 父类id
	 * @return
	 */
	private static SortedSet<SecurityModel> buildTree(List<SecurityModel> securityModels,String pId){
		
		SortedSet<SecurityModel> result =getInstanceTreeSet() ;
		
		for (SecurityModel treeBean : securityModels) {
			String id = treeBean.getModuleID();
			String pid = treeBean.getParentID();
			if(StringUtils.isNotBlank(pid)){
				if(pid.equals(pId)){
					 //递归查询当前子节点的子节点
					SortedSet<SecurityModel> childrenTree = buildTree(securityModels, id);
					treeBean.setChildren(childrenTree);
					String url = null;
					if(StringUtils.isNotBlank(treeBean.getPageCode())){
						url= treeBean.getModuleURL() +"/"+ treeBean.getPageCode();
					}else{
						url= treeBean.getModuleURL();
					}
					treeBean.setModuleURL(url);
					result.add(treeBean);
				}
			}
		}
		
		return result;
		
	}
	
	
	private static  SortedSet<SecurityModel> getInstanceTreeSet(){
	
		return new TreeSet<SecurityModel>(new Comparator<SecurityModel>() {
	
	    	/**
	    	 * 比较器中返回为0 ，Set中认为是相等的，会执行去重操作。如果比较器逻辑有问题，可能会触发死循环比较。
	    	 */
			@Override
			public int compare(SecurityModel o1, SecurityModel o2) {
				String showOrder1 = o1.getShowOrder();
				String showOrder2 = o2.getShowOrder();
				try {
					if(StringUtils.isNotBlank(showOrder1) && StringUtils.isNotBlank(showOrder2)){
						Integer one = Integer.valueOf(showOrder1);
						Integer two = Integer.valueOf(showOrder2);
						
						if(one>two){
							return 1;
						}
						
					}
					
				} catch (Exception e) {
					System.err.println("菜单排序异常！");
				}
				return -1;
			}
		} );
	}
	
}
