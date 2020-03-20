package com.bondex.rabbitmq.multiconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author innerpeacez
 * @since 2019/3/8
 *  内网MQ
 */

@Configuration
@ConfigurationProperties("spring.rabbitmq.vpn")
public class VPNNetRabbitConfiguration extends AbstractRabbitConfiguration {

    @Bean(name = "vpnNetConnectionFactory")
    @Primary
    public CachingConnectionFactory vpnNetConnectionFactory() {
        return super.connectionFactory();
    }

    @Bean(name = "vpnNetRabbitTemplate")
    @Primary
    public RabbitTemplate vpnNetRabbitTemplate(@Qualifier("vpnNetConnectionFactory") CachingConnectionFactory connectionFactory) {
    	
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
            rabbitTemplate.setMessagePropertiesConverter(defaultMessagePropertiesConverter());
            rabbitTemplate.setEncoding("UTF-8");
//             开启returncallback     yml 需要 配置    publisher-returns: true
            rabbitTemplate.setMandatory(true);
            //消息失败后return回调
            rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
                String correlationId = message.getMessageProperties().getCorrelationIdString();
                log.info("使用的自定义vpnNetRabbitTemplate消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, replyCode, replyText, exchange, routingKey);
            });
            //        消息确认  yml 需要配置   publisher-returns: true
            rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                if (ack) {
                    log.info("使用的自定义vpnNetRabbitTemplate消息发送到exchange成功,id: {}", correlationData.getId());
                } else {
                    log.info("使用的自定义vpnNetRabbitTemplate消息发送到exchange失败,原因: {}", cause);
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
    @Bean(name = "vpnNetListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory vpnNetFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                                             @Qualifier("vpnNetConnectionFactory") CachingConnectionFactory connectionFactory) {
    	SimpleRabbitListenerContainerFactory ContainerFactory = new SimpleRabbitListenerContainerFactory();
        
        ContainerFactory.setMessageConverter(messageConverter());
        ContainerFactory.setConnectionFactory(connectionFactory);
        ContainerFactory.setAcknowledgeMode(AcknowledgeMode.AUTO); //设置确认模式手工确认   Spring AMQP消息可靠消费
        ContainerFactory.setReceiveTimeout(10000L);
//      ContainerFactory.setTaskExecutor(threadPoolTaskExecutor); //设置线程池
       // 设置消费者线程数
        ContainerFactory.setConcurrentConsumers(10);
        // 设置最大消费者线程数
        ContainerFactory.setMaxConcurrentConsumers(30);
        //设置公平分发Prefetch Count为配置设置的值3，意味着每个消费者每次会预取3个消息准备消费。
        ContainerFactory.setPrefetchCount(3);
        
        ///新消费者开始之间的最小间隔。
        ContainerFactory.setStartConsumerMinInterval(1000L);
        //消费者停止之间的最小间隔。
        ContainerFactory.setStopConsumerMinInterval(1000L);
        
        //触发新消费者的连续接收次数。
        ContainerFactory.setConsecutiveActiveTrigger(5);
        
        //触发停止使用者的连续超时次数。
        ContainerFactory.setConsecutiveIdleTrigger(6);
        
        ContainerFactory.setAutoStartup(true);
        //重试次数超过上面的设置之后是否丢弃（false不丢弃时需要写相应代码将该消息加入死信队列）
        ContainerFactory.setDefaultRequeueRejected(false);
        
        configurer.configure(ContainerFactory, connectionFactory); //配置生效
        
        return ContainerFactory;
    }

    @Bean(value = "vpnNetRabbitAdmin")
    public RabbitAdmin vpnNetRabbitAdmin(@Qualifier("vpnNetConnectionFactory") CachingConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }


	
	/**
	 * MQ message配置json序列化
	 * @return
	 */
	//@Bean
	public MessageConverter messageConverter(){
		return new Jackson2JsonMessageConverter();
	}
	
	@Bean
	public MessagePropertiesConverter defaultMessagePropertiesConverter(){
	   DefaultMessagePropertiesConverter messagePropertiesConverter=new DefaultMessagePropertiesConverter();
	   messagePropertiesConverter.setCorrelationIdPolicy(DefaultMessagePropertiesConverter.CorrelationIdPolicy.STRING);
	    return messagePropertiesConverter;
	}


}


