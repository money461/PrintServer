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
package com.bondex.config.freemaker;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.jagregory.shiro.freemarker.ShiroTags;

/**
 * freemarker配置类
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0
 * @website https://www.zhyd.me
 * @date 2018/4/16 16:26
 * @since 1.0
 */
@Configuration
public class FreemarkerShiroConfig implements InitializingBean  {

  
	/**
	 * freemarker标签解析器
	 */

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        freemarker.template.Configuration configuration = freeMarkerConfigurer.getConfiguration();
        /**
         * 添加自定义标签
         */
//        configuration.setSharedVariable("zhydTag", customTagDirective);
        /**
         * 设置shiro标签会话
         */
        ShiroTags shiroTags = new ShiroTags();
        shiroTags.put("hasAnyPermissions", new HasAnyPermissionsTag());
        configuration.setSharedVariable("shiro",shiroTags);
        configuration.setNumberFormat("#");//防止页面输出数字,变成2,000
        //可以添加很多自己的要传输到页面的[方法、对象、值]
    }

    
    
}
