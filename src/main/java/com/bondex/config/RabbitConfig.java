package com.bondex.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.ConnectionFactory;

@Configuration
public class RabbitConfig {
	
	
	/**
	 * 死信队列 交换机标识符
	 */
	public static final String DEAD_LETTER_QUEUE_KEY = "x-dead-letter-exchange";
	/**
	 * 死信队列交换机绑定键标识符
	 */
	public static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

	/**
	 * 死信队列跟交换机类型没有关系 不一定为directExchange 不影响该类型交换机的特性.
	 *
	 * @return the exchange
	 */
	@Bean("deadLetterExchange")
	public Exchange deadLetterExchange() {
		return ExchangeBuilder.directExchange("DL_EXCHANGE").durable(true).build();
	}

	/**
	 * 声明一个死信队列. x-dead-letter-exchange 对应 死信交换机 x-dead-letter-routing-key 对应 死信队列
	 *
	 * @return the queue
	 */
	@Bean("deadLetterQueue")
	public Queue deadLetterQueue() {
		Map<String, Object> args = new HashMap<>(2);
		// x-dead-letter-exchange 声明 死信交换机
		args.put(DEAD_LETTER_QUEUE_KEY, "DL_EXCHANGE");
		// x-dead-letter-routing-key 声明 死信路由键
		args.put(DEAD_LETTER_ROUTING_KEY, "KEY_R");
		return QueueBuilder.durable("DL_QUEUE").withArguments(args).build();
	}

	/**
	 * 定义死信队列转发队列.
	 *
	 * @return the queue
	 */
	@Bean("redirectQueue")
	public Queue redirectQueue() {
		return QueueBuilder.durable("air_label_queue_o").build();
	}
	

	/**
	 * 死信路由通过 DL_KEY 绑定键绑定到死信队列上.
	 *
	 * @return the binding
	 */
	@Bean
	public Binding deadLetterBinding() {
		return new Binding("DL_QUEUE", Binding.DestinationType.QUEUE, "DL_EXCHANGE", "DL_KEY", null);

	}

	/**
	 * 死信路由通过 KEY_R 绑定键绑定到死信队列上.
	 *
	 * @return the binding
	 */
	@Bean
	public Binding redirectBinding() {
		return new Binding("air_label_queue_o", Binding.DestinationType.QUEUE, "DL_EXCHANGE", "KEY_R", null);
	}
	
	
	 /**
     * 自己定制化amqp模版      可根据需要定制多个  
     * <p>
     * <p>
     * 此处为模版类定义 Jackson消息转换器
     * ConfirmCallback接口用于实现消息发送到RabbitMQ交换器后接收ack回调   即消息发送到exchange  ack
     * ReturnCallback接口用于实现消息发送到RabbitMQ 交换器，但无相应队列与交换器绑定时的回调  即消息发送不到任何一个队列中  ack
     *
     * @return the amqp template
     */
//    @Primary
    @Bean(name="amqpTemplate")
    public AmqpTemplate amqpTemplate(@Autowired CachingConnectionFactory cachconnectionFactory ) {
    	//每个rabbitTemplate 只能有一个callBack回调机制
    	RabbitTemplate rabbitTemplate = new RabbitTemplate(cachconnectionFactory);
        Logger log = LoggerFactory.getLogger(RabbitTemplate.class);
//          使用jackson 消息转换器
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setEncoding("UTF-8");
//         开启returncallback     yml 需要 配置    publisher-returns: true
        rabbitTemplate.setMandatory(true);
        //消息失败后return回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String correlationId = message.getMessageProperties().getCorrelationIdString();
            log.info("使用的自定义amqpTemplate消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, replyCode, replyText, exchange, routingKey);
        });
        //        消息确认  yml 需要配置   publisher-returns: true
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("使用的自定义amqpTemplate消息发送到exchange成功,id: {}", correlationData.getId());
            } else {
                log.info("使用的自定义amqpTemplate消息发送到exchange失败,原因: {}", cause);
            }
        });
        return rabbitTemplate;
    }
	
}
