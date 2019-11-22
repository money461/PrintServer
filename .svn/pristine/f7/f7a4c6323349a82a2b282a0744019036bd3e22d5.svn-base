package com.bondex.rabbitmq.rpc;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RPCServer {
	private static final String RPC_QUEUE_NAME = "rpc_queue";
	static String host = "172.16.75.204";
	static int port = 5672;
	static String username = "ZXOSRMQ012";
	static String password = "astiasti";

	public static void main(String args[]) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
		channel.basicQos(1);

		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			BasicProperties props = delivery.getProperties();
			BasicProperties replyProps = new BasicProperties.Builder().correlationId(props.getCorrelationId()).build();
			String message = new String(delivery.getBody());
			int n = Integer.parseInt(message);
			System.out.println(" 客户端消息：(" + message + ")");
			String repsonse = "这是服务端回执消息：" + message;
			channel.basicPublish("", props.getReplyTo(), replyProps, repsonse.getBytes());
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		}
	}

}
