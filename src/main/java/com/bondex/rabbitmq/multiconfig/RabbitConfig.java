package com.bondex.rabbitmq.multiconfig;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	
	
}
