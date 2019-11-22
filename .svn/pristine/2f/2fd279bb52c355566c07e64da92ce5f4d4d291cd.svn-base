package com.bondex.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MyFilter implements Filter {
	@Override
	public void destroy() {
		System.out.println("销毁容器");
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {/*
		// 开启线程，入库MQ消息
		new Thread(new Runnable() {
			private String queueName = "air_label_queue";
			private String exchangeName = "air_label_exchange";
			private String routingKey = "air_label_routingKey";
			private LabelInfoService labelInfoService = new LabelInfoServiceImpl();

			@Override	
			public void run() {
				try {
					ConnectionFactory factory = new ConnectionFactory();
					factory.setHost("mq.bondex.com.cn");
					factory.setPort(5672);
					factory.setUsername("ZXOSRMQ051");
					factory.setPassword("astiasti");
					Connection connection = factory.newConnection();
					// 从连接中创建通道
					Channel channel = connection.createChannel();

					channel.queueDeclare(queueName, true, false, false, null);
					channel.exchangeDeclare(exchangeName, "topic", true, false, null);
					channel.queueBind(queueName, exchangeName, routingKey);

					 创建消费者对象，用于读取消息 
					QueueingConsumer consumer = new QueueingConsumer(channel);
					channel.basicConsume(queueName, true, consumer);

					 读取队列，并且阻塞，即在读到消息之前在这里阻塞，直到等到消息，完成消息的阅读后，继续阻塞循环 
					while (true) {
						QueueingConsumer.Delivery delivery = consumer.nextDelivery();
						String message = new String(delivery.getBody());
						labelInfoService.labelInfoSave(new Gson().fromJson(message, JsonRootBean.class));
					}

				} catch (ShutdownSignalException | ConsumerCancelledException | IOException | TimeoutException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	*/}

}
