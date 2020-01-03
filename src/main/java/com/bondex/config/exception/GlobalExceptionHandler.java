package com.bondex.config.exception;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.common.enums.ResEnum;
import com.bondex.res.AjaxResult;
import com.bondex.res.ResultUtil;
import com.bondex.util.ServletUtils;

/**
 * 全局异常处理器
 * 
 * @author ruoyi
 */
//@ControllerAdvice //使用加强Controller做全局异常处理：@ControllerAdvice 
//@RestControllerAdvice //@ControllerAdvice 和 @ResponseBody
//@ResponseBody //需要加上 @ResponseBody ********** ajax success获取异常结果   不加上 ajax error获得执行
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 权限校验失败 如果请求为ajax返回json，普通请求跳转页面
     */
    @ExceptionHandler(AuthorizationException.class)
    public Object handleAuthorizationException(HttpServletRequest request, AuthorizationException e)
    {
    	log.error("[捕捉全局异常请求URL:][{}],原因：{}",request.getRequestURL(),e);
        if (ServletUtils.isAjaxRequest(request))
        {
            return AjaxResult.result(ResEnum.FORBIDDEN);
        }
        else
        {
            
            return  ResultUtil.view("error/403");
        }
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    public AjaxResult handleException(HttpServletRequest request,HttpRequestMethodNotSupportedException e)
    {
    	log.error("[捕捉全局异常请求URL:][{}],原因：{}",request.getRequestURL(),e.getMessage());
        return AjaxResult.error("不支持' " + e.getMethod() + "'请求");
    }

    /**
     * 拦截未知的运行时异常
     */
//    @ExceptionHandler(RuntimeException.class)
    public AjaxResult notFount(HttpServletRequest request,RuntimeException e)
    {
    	log.error("[捕捉运行时异常--请求URL:][{}],原因：{}",request.getRequestURL(),e);
        return AjaxResult.error("运行时异常:" + e.getMessage());
    }


    /**
     * 业务自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    public Object businessException(HttpServletRequest request, BusinessException e)
    {
    	log.error("[捕捉自定义异常--请求URL:][{}],原因：{}",request.getRequestURL(),e.getMessage());
        if (ServletUtils.isAjaxRequest(request))
        {
            return AjaxResult.result(e.getCode(),e.getMessage());
        }
        else
        {
            LinkedHashMap<String, Object> modelMap = new LinkedHashMap<String,Object>();
            modelMap.put("message", e.getMessage());
            modelMap.put("code", e.getCode());
            modelMap.put("url",request.getRequestURL());
            return ResultUtil.view("error/error", modelMap);
        }
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(value={Exception.class,RuntimeException.class})
    public Object handleException(HttpServletRequest request,Exception e)
    {
    	log.error("[捕捉全局异常请求URL:][{}],原因：{}",request.getRequestURL(),e.getMessage());
        
    	if (ServletUtils.isAjaxRequest(request))
        {
            return AjaxResult.error("服务器错误，请联系管理员");
        }
        else
        {
            LinkedHashMap<String, Object> modelMap = new LinkedHashMap<String,Object>();
            modelMap.put("message", e.getMessage());
            modelMap.put("code", ResEnum.UNKONW_ERROR.CODE);
            modelMap.put("url",request.getRequestURL());
            return ResultUtil.view("error/error", modelMap);
        }
    }

   
}
