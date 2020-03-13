/**
 * MIT License
 * Copyright (c) 2018 yadong.zhang
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bondex.res;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import com.bondex.common.enums.ResEnum;
import com.bondex.entity.page.Datagrid;
import com.github.pagehelper.PageInfo;

/**
 * 接口返回工具类，支持ModelAndView、ResponseVO、PageResult
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0
 * @website https://www.zhyd.me
 * @date 2018/4/18 11:48
 * @since 1.0
 */
public class ResultUtil {

    public static ModelAndView view(String view) {
        return new ModelAndView(view);
    }

    public static ModelAndView view(String view, Map<String, Object> model) {
        return new ModelAndView(view, model);
    }

    public static ModelAndView redirect(String view) {
        return new ModelAndView("redirect:" + view);
    }

    public static ResponseVO error(String status, String message) {
        return vo(status, message, null);
    }

    public static ResponseVO error(ResEnum status) {
        return vo(status.CODE, status.MESSAGE, null);
    }

    public static ResponseVO error(String message) {
        return vo(ResEnum.ERROR.CODE, message, null);
    }

    public static ResponseVO success(String message, Object data) {
        return vo(ResEnum.SUCCESS.CODE, message, data);
    }

    public static ResponseVO success(String message) {
        return success(message, null);
    }

    public static ResponseVO success(ResEnum status) {
        return vo(status.CODE, status.MESSAGE, null);
    }

    public static ResponseVO vo(String status, String message, Object data) {
        return new ResponseVO<>(status, message, data);
    }

    public static Datagrid tablePage(long total, List<?> list) {
        return new Datagrid(total, list);
    }

    public static Datagrid tablePage(PageInfo info) {
        if (info == null) {
            return new Datagrid(0L, new ArrayList());
        }
        return tablePage(info.getTotal(), info.getList());
    }

}
