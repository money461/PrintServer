package com.bondex.common.enums;

/**
 * 返回结果信息 枚举类
 * @author admin
 *
 */
public enum ResEnum implements BaseEnum{

	UNKONW_ERROR ("-1","发生未知异常，请与系统管理员联系！"),
	
	SUCCESS("200","操作成功"),
	
	FAIL("201","操作失败"),
	
	Bindvalidation("203","校验参数异常"),
	
	WARN("301","警告操作失败"),
	
	ERROR("500", "服务器未知错误！"),
	
    UNAUTHORIZED("500", "尚未登录！"),
    
    FORBIDDEN("403", "您没有操作权限！"),
    
    NOT_FOUND("500", "资源不存在！"),
    
    LOGIN_ERROR("500", "账号或密码错误！"),
    
    USER_EXIST("500", "已存在的用户！"),
    
    INVALID_TOKEN("500", "无效的TOKEN，您没有操作权限！"),
    INVALID_ACCESS("500", "无效的请求，该请求已过期！"),
    DELETE_ERROR("500", "删除失败！"),
	
	FILE_TYPE_ERROR_DOC("error.file.type.doc", "文档类型错误"),
	
	LUOJI_ERROR("500","服务器逻辑错误！");
	
	
	
	public String CODE;

    public String MESSAGE;

    ResEnum(String CODE, String MESSAGE){
        this.CODE = CODE;
        this.MESSAGE = MESSAGE;
    }

	@Override
	public Object toValue() {
		
		return CODE;
	}
}
