/**
 * Copyright (C), 2015-2018, ND Co., Ltd.
 * FileName: ChannelUtils
 * Author:   HuangTaiHong
 * Date:     2018-03-19 下午 7:07
 * Description: RabbitMQ连接工具类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.bondex.rabbitmq.util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈RabbitMQ连接工具类〉
 *
 * @since 1.0.0
 */
public class ChannelUtils {
    public static Channel getChannel(String connectionDescription) {
        try {
            ConnectionFactory connectionFactory = getConnectionFactory();
            Connection connection = connectionFactory.newConnection(connectionDescription);
            return connection.createChannel();
        } catch (Exception e) {
            throw new RuntimeException("获取Channel连接失败");
        }
    }

    private static ConnectionFactory getConnectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setHost("172.16.88.150");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123456");

        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(10000);

        Map<String, Object> connectionFactoryPropertiesMap = new HashMap();
        connectionFactoryPropertiesMap.put("principal", "用户名admin");
        connectionFactoryPropertiesMap.put("description", "标签打印系统V2.0");
        connectionFactoryPropertiesMap.put("emailAddress", "bondex.com.cn");
        connectionFactory.setClientProperties(connectionFactoryPropertiesMap);

        return connectionFactory;
    }
}