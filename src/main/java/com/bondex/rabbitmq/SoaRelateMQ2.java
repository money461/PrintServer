package com.bondex.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class SoaRelateMQ2 {
	static String host = "172.16.75.204";
	static int port = 5672;
	static String username = "ZXOSRMQ012";
	static String password = "astiasti";

	private static String EXCHANGE_NAME = "air_print_label_exchange";

	public static void main(String args[]) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		// 声明exchange
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		// 消息内容
		String message = "删除商品， id = 1001";
		channel.basicPublish(EXCHANGE_NAME, "qingdao", null, message.getBytes());
		System.out.println(" [x] Sent '" + message + "'");

		channel.close();
		connection.close();
	}

}
