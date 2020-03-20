package com.bondex.rabbitmq.multiconfig;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
	/**
	 * #监听通用标签队列名称
		spring.rabbitmq.currentLableQueues=current_labelPrint_queue
		spring.rabbitmq.currentLableExchange=current_labelPrint_Exchange
		spring.rabbitmq.currentLableRoutingKey=current_labelPrint_routingKey
	 */
	@Value("${spring.rabbitmq.currentLableQueues}")
	private String currentLableQueues;
	
	@Value("${spring.rabbitmq.currentLableExchange}")
	private String currentLableExchange;
	
	@Value("${spring.rabbitmq.currentLableRoutingKey}")
	private String currentLableRoutingKey;
	
	/**
	 * 死信队列 交换机标识符 设置该Queue的死信的信箱 死信队列 交换机标识符
	 */
	public static final String DEAD_LETTER_EXCHANGE_KEY = "x-dead-letter-exchange";
	/**
	 * 死信队列交换机绑定键标识符 设置死信routingKey 死信队列交换机绑定键标识符
	 */
	public static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
	
	/**
	 * 为队列设置超时时间    设置队列中的消息存在队列中的时间超过过期时间则成为死信，其值是一个非负整数，单位为微秒   时间 10s 10*1000
	 */
	public static final String DEAD_LETTER_TTL = "x-message-ttl";
	
	@Bean
	public Queue Current_LabelQueue() {
		Map<String,Object> args=new HashMap<>();
    	// 设置该Queue的死信的信箱 死信队列 交换机标识符
    	args.put(DEAD_LETTER_EXCHANGE_KEY,currentLableExchange);
    	// 设置死信routingKey 死信队列交换机绑定键标识符
    	args.put( DEAD_LETTER_ROUTING_KEY,"current_labelPrint_dealLetter_routingKey"); //超时后由该路由到的 current_labelPrint_dealLetter_queue
    	// 设置队列中的消息存在队列中的时间超过过期时间则成为死信，其值是一个非负整数，单位为微秒   时间 10s 10*1000
    	//args.put("x-message-ttl", );
    	//主队列的路由键
		 Queue queue = new Queue(currentLableQueues, true, false, false, args);
	      return queue;
	}
	

	/**
	 * DirectExchange 按照routingkey分发到指定队列    路由模式
	 * @return
	 */
    @Bean
    public Exchange exchange() {
        return new DirectExchange(currentLableExchange, true, false, new HashMap<>());
    }


    @Bean
    public Binding binding() {
        Binding binding = new Binding(currentLableQueues, Binding.DestinationType.QUEUE, currentLableExchange,currentLableRoutingKey, new HashMap<>());
        return binding;
    }
    

	/**
	 * 声明一个死信队列. x-dead-letter-exchange 对应 死信交换机 x-dead-letter-routing-key 对应 死信队列
	 *
	 * @return the queue
	 */
	@Bean
	public Queue deadLetterQueue() {
		return QueueBuilder.durable("current_labelPrint_dealLetter_queue").withArguments(new HashMap<>()).build();
	}

    
    @Bean
    public Binding deadLetterBinding() {
    	Binding binding = new Binding("current_labelPrint_dealLetter_queue", Binding.DestinationType.QUEUE, currentLableExchange,"current_labelPrint_dealLetter_routingKey", new HashMap<>());
    	return binding;
    }
    
	
	/**
	 * 空运标签队列自动配置
	 *
	 * @return the queue
	 */
	@Bean("redirectQueue")
	public Queue redirectQueue() {
		return QueueBuilder.durable("air_label_queue_o").build();
	}
	
	/**
	 * 
	 * 空运标签队列自动配置
	 * @return the binding
	 */
	@Bean
	public Binding redirectBinding() {
		return new Binding("air_label_queue_o", Binding.DestinationType.QUEUE, "DL_EXCHANGE", "KEY_R", null);
	}
	
	
	
	
}

