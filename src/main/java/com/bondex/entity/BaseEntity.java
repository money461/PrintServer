package com.bondex.entity;

import java.util.Date;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import com.bondex.annoation.dao.Column;
import com.bondex.annoation.dao.Ignore;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
public class BaseEntity {


	@Ignore
    private static final long serialVersionUID = 1L;

    /** 搜索值 */
    private String searchValue;


    /**
     * 注解@JsonFormat主要是后台到前台的时间格式的转换
     * 注解@DateTimeFormat主要是前后到后台的时间格式的转换
     */
    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "zh", timezone = "GMT+8")
    @Column(name="create_time")
    private Date createTime;


    /** 更新时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "zh", timezone = "GMT+8")
    @Column(name="update_time")
    @Ignore
    private Date updateTime;

    /** 备注 */
    private String remark;

    /** 请求参数 */ //params[endTime] params[beginTime]
    @Ignore
    private Map<String, Object> params;

}
