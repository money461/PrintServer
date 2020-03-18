package com.bondex.entity;

import java.util.Date;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import com.bondex.annoation.dao.Ignore;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
public class BaseEntity {


	@Ignore
    private static final long serialVersionUID = 1L;

    /** 搜索值 */
    private String searchValue;


    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    /** 更新时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 备注 */
    private String remark;

    /** 请求参数 */
    private Map<String, Object> params;

}
