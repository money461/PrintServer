package com.bondex.rabbitmq.multiconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author innerpeacez
 * @since 2019/3/8
 * 外网MQ
 */

@Configuration
@ConfigurationProperties("spring.rabbitmq.outnet")
public class OutNetRabbitConfiguration extends AbstractRabbitConfiguration {

    @Bean(name = "outNetConnectionFactory")
    public CachingConnectionFactory outNetConnectionFactory() {
        return super.connectionFactory();
    }

    @Bean(name = "outNetRabbitTemplate")
    public RabbitTemplate outNetRabbitTemplate(@Qualifier("outNetConnectionFactory") ConnectionFactory connectionFactory) {
    	 /**
         * 自己定制化amqp模版      可根据需要定制多个  
         * <p>
         * <p>
         * 此处为模版类定义 Jackson消息转换器
         * ConfirmCallback接口用于实现消息发送到RabbitMQ交换器后接收ack回调   即消息发送到exchange  ack
         * ReturnCallback接口用于实现消息发送到RabbitMQ 交换器，但无相应队列与交换器绑定时的回调  即消息发送不到任何一个队列中  ack
         *
         */
        	//每个rabbitTemplate 只能有一个callBack回调机制
        	RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            Logger log = LoggerFactory.getLogger(RabbitTemplate.class);
//              使用jackson 消息转换器
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setEncoding("UTF-8");
//             开启returncallback     yml 需要 配置    publisher-returns: true
            rabbitTemplate.setMandatory(true);
            //消息失败后return回调
            rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
                String correlationId = message.getMessageProperties().getCorrelationIdString();
                log.info("使用的自定义outNetRabbitTemplate消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, replyCode, replyText, exchange, routingKey);
            });
            //        消息确认  yml 需要配置   publisher-returns: true
            rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                if (ack) {
                    log.info("使用的自定义outNetRabbitTemplate消息发送到exchange成功,id: {}", correlationData.getId());
                } else {
                    log.info("使用的自定义outNetRabbitTemplate消息发送到exchange失败,原因: {}", cause);
                }
            });
            return rabbitTemplate;
    }

    /**
     * 监听容器
     * @param configurer
     * @param connectionFactory
     * @return
     */
    @Bean(name = "outNetListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory outNetFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                                             @Qualifier("outNetConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean(value = "outNetRabbitAdmin")
    public RabbitAdmin outNetRabbitAdmin(@Qualifier("outNetConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}

