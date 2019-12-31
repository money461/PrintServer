package com.bondex.res;

import java.util.HashMap;

import com.bondex.common.enums.ResEnum;
import com.bondex.util.StringUtils;

/**
 * 操作异常信息消息提醒返回
 * 
 * @author ruoyi
 */
public class AjaxResult extends HashMap<String, Object>
{
    private static final long serialVersionUID = 1L;

    public static final String CODE_TAG = "status";

    public static final String MSG_TAG = "message";

    public static final String DATA_TAG = "data";

	private ResEnum resEnum;

    
    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public AjaxResult()
    {
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     * 
     * @param type 状态类型
     * @param msg 返回内容
     */
    public AjaxResult(ResEnum type)
    {
        super.put(CODE_TAG, type.CODE);
        super.put(MSG_TAG, type.MESSAGE);
    }
    
    /**
     * 初始化一个新创建的 AjaxResult 对象
     * 
     * @param type 状态类型
     * @param msg 返回内容
     */
    public AjaxResult(ResEnum type, String msg)
    {
    	super.put(CODE_TAG, type.CODE);
    	super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     * 
     * @param type 状态类型
     * @param msg 返回内容
     * @param data 数据对象
     */
    public AjaxResult(ResEnum type, String msg, Object data)
    {
        super.put(CODE_TAG, type.CODE);
        super.put(MSG_TAG, msg);
        if (StringUtils.isNotNull(data))
        {
            super.put(DATA_TAG, data);
        }
    }
    
    public AjaxResult(String code, String msg, Object data)
    {
    	super.put(CODE_TAG, code);
    	super.put(MSG_TAG, msg);
    	if (StringUtils.isNotNull(data))
    	{
    		super.put(DATA_TAG, data);
    	}
    }

    /**
     * 返回成功消息
     * 
     * @return 成功消息
     */
    public static AjaxResult success()
    {
        return AjaxResult.success("操作成功");
    }

    /**
     * 返回成功数据
     * 
     * @return 成功消息
     */
    public static AjaxResult success(Object data)
    {
        return AjaxResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     * 
     * @param msg 返回内容
     * @return 成功消息
     */
    public static AjaxResult success(String msg)
    {
        return AjaxResult.success(msg, null);
    }

    /**
     * 返回成功消息
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(String msg, Object data)
    {
        return new AjaxResult(ResEnum.SUCCESS, msg, data);
    }

    /**
     * 返回警告消息
     * 
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult warn(String msg)
    {
        return AjaxResult.warn(msg, null);
    }

    /**
     * 返回警告消息
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static AjaxResult warn(String msg, Object data)
    {
        return new AjaxResult(ResEnum.WARN, msg, data);
    }

   
    /**
     * 返回错误消息
     * 
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult error(String msg)
    {
        return AjaxResult.error(msg, null);
    }

    /**
     * 返回错误消息
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static AjaxResult error(String msg, Object data)
    {
        return new AjaxResult(ResEnum.ERROR, msg, data);
    }
    
    /**
     * 返回自定义消息
     * @param code
     * @param msg
     * @param data
     * @return
     */
    public static AjaxResult result(String code,String msg, Object data)
    {
    	return new AjaxResult(code, msg, data);
    }
    
    /**
     * 返回自定义消息
     * @param code
     * @param msg
     * @param data
     * @return
     */
    public static AjaxResult result(String code,String msg)
    {
    	return new AjaxResult(code, msg, null);
    }
    
    /**
     * 返回自定义消息
     * @param code
     * @param msg
     * @param data
     * @return
     */
    public static AjaxResult result(ResEnum resEnum)
    {
    	return new AjaxResult(resEnum);
    }
    
}
