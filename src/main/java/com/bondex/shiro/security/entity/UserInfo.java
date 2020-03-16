package com.bondex.shiro.security.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
/**
 * 当前用户数据基础信息
 * @author Qianli
 * 
 * 2019年1月22日 下午12:11:44
 */
public class UserInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5674666829351992942L;
	private String cstoken; //用于向cs系统跳转，当前使用shipping,WMS
	private String email;  //邮箱
	private String logintype;  //登录方式，表单登录与二维码扫描登录
	private String opid;   //初始登录会返回当前用户默 认操作ID
	private Map<String, String> opids;   //返回当前用户对应的一至多个操作ID  "opids":{"28108":"CD显示操作/张玉凤"}
	private List<Opid> allOpid; //json对象的形式返回 opid=28108, username=CD显示操作/张玉凤
	private String opname; //默认绑定的操作姓名
	private String psncode;  //	员工编号
	private String psnname;  //员工姓名
	private String type;
	private String token; //身份验证串，用于不同系统接口之间跳转验证
	private String tgt; //票据
	private List<String> usercorplist;
	private int TZ;
	private List<String> opidData; //当前操作id 所属下 操作及系统获取操作数据权限
	
	public String getCstoken() {
		return cstoken;
	}
	public void setCstoken(String cstoken) {
		this.cstoken = cstoken;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLogintype() {
		return logintype;
	}
	public void setLogintype(String logintype) {
		this.logintype = logintype;
	}
	public String getOpid() {
		return opid;
	}
	public void setOpid(String opid) {
		this.opid = opid;
	}
	public Map<String, String> getOpids() {
		return opids;
	}
	public void setOpids(Map<String, String> opids) {
		this.opids = opids;
	}
	public List<Opid> getAllOpid() {
		return allOpid;
	}
	public void setAllOpid(List<Opid> allOpid) {
		this.allOpid = allOpid;
	}
	public String getOpname() {
		return opname;
	}
	public void setOpname(String opname) {
		this.opname = opname;
	}
	public String getPsncode() {
		return psncode;
	}
	public void setPsncode(String psncode) {
		this.psncode = psncode;
	}
	public String getPsnname() {
		return psnname;
	}
	public void setPsnname(String psnname) {
		this.psnname = psnname;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getTgt() {
		return tgt;
	}
	public void setTgt(String tgt) {
		this.tgt = tgt;
	}
	public List<String> getUsercorplist() {
		return usercorplist;
	}
	public void setUsercorplist(List<String> usercorplist) {
		this.usercorplist = usercorplist;
	}
	public int getTZ() {
		return TZ;
	}
	public void setTZ(int tZ) {
		TZ = tZ;
	}
	public List<String> getOpidData() {
		return opidData;
	}
	public void setOpidData(List<String> opidData) {
		this.opidData = opidData;
	}
	
	

/*{
"success":true,
"message":[
  {
      "email":"qianli@bondex.com.cn",
      "opid":"280602",
      "opids":{
          "280602":"成都资讯/钱力"
      },
      "psncode":"0006801",
      "psnname":"钱力",
      "tgt":"TGT-215280-x7eycefyjzWhI5c73XyzXPvBc4Q2UsbgcZK4upEiFOckdQ7ngM-cas01.example.org",
      "token":"1615DF2FEE3E75A67B9BD4C7BA14834E6005B70A1BE7B73BC6B28E1F7D6B08569549766EEA50E9F3E99E36B902BB274C896FF56C3828C999D19A7C50F3E2F2ACF710790BDCF0855F1A120793A3DCFB7951E0DE0879AE6B41A0346A48E33758245EE6D7F0E66BD70244F7E71C08230549D493FE24A4D03B026A60ED4345F5981FF078495A10276D82375631BFEA5919CF22D01B755BD5FA1306B708500FA39979592CB02C592F88A1499C6A7BF068EA0065627831C023A8740CE761B8A6014EF1E785D9A07DEC8CBAEC24E67ED9226BB369A57418E06C43B2D643ED03429F4B0CC07A344F24315721"
  }
]
}*/
	
	
}
