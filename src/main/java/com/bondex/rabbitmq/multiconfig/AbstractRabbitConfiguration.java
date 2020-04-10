package com.bondex.rabbitmq.multiconfig;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

import lombok.Data;

/**
 * @author innerpeacez
 * @since 2019/3/11
 */
@Data
public abstract class AbstractRabbitConfiguration {

    protected String host;
    protected int port;
    protected String username;
    protected String password;
    protected boolean publisherReturns;
    protected boolean publisherConfirms;
    protected String virtualHost;
    
    protected CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setPublisherReturns(publisherReturns);
        connectionFactory.setPublisherConfirms(publisherConfirms);
        connectionFactory.setVirtualHost(virtualHost);
        return connectionFactory;
    }
}

