package com.bondex.config.exception;

public class BusinessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2971955930568984962L;
	
	/**
	 * 自定义异常处理bean类
	 * * 把前端控制器异常交给异常处理器来处理预期异常
	 * * 继承runtimeException才能事务回滚             runtimeException 继承了exception
	 */
	
	//异常提示代码
    private  String code;
	
	//异常信息
    private  String message;
    
    public BusinessException(){
    	super();
    }
    
    public BusinessException (String message){
    	super(message);
    	this.message = message;
    }
    
    /**
	 * @param code
	 * @param message
	 */

	public BusinessException(String code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}

    
	public BusinessException (String message,Throwable cause){
		super(message, cause);
	}
	
	
	
	
	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
