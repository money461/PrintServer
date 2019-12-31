package com.bondex.res;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class MsgResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8638455767812379711L;
	
	//返回代码
	 @JsonInclude(Include.NON_NULL)
	private Object status;
	
	//返回信息
	 @JsonInclude(Include.NON_NULL)
	private Object message;
	
	//返回数据
	 @JsonInclude(Include.NON_NULL)
	private Object data;

	
	public MsgResult() {
		super();
	}

	
	public static MsgResult result=null;
	
	public static MsgResult getInstant(){
		if(null==result){
			result = new MsgResult();
		}
		
		return result;
	}


	//返回有数据方法
	public static MsgResult result(Object status, Object message, Object data){
		MsgResult mResult = MsgResult.getInstant();
		mResult.setStatus(status);
		mResult.setMessage(message);
		mResult.setData(data);
		return mResult;
	}
	
	//返回无数据方法
	public static MsgResult nodata(Object status, Object message){
		MsgResult mResult = MsgResult.getInstant();
		mResult.setStatus(status);;
		mResult.setMessage(message);
		mResult.setData(null);
		return mResult;
	}
	
	
	public MsgResult(Object status, Object msg, Object data) {
		super();
		this.status = status;
		this.message = msg;
		this.data = data;
	}
	
	public MsgResult(Object status, Object msg) {
		super();
		this.status = status;
		this.message = msg;
	}




	public Object getStatus() {
		return status;
	}


	public void setStatus(Object status) {
		this.status = status;
	}



	public Object getMessage() {
		return message;
	}


	public void setMessage(Object message) {
		this.message = message;
	}


	public Object getData() {
		return data;
	}




	public void setData(Object data) {
		this.data = data;
	}




	@Override
	public String toString() {
		return "MsgResult [status=" + status + ", msg=" + message + ", data=" + data + "]";
	}
	
	
	

}
