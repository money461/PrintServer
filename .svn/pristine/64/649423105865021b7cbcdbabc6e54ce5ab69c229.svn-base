package com.bondex.rabbitmq.rpc;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class RPCClient2 {
	static String host = "172.16.75.204";
	static int port = 5672;
	static String username = "ZXOSRMQ012";
	static String password = "astiasti";
	private Connection connection;
	private Channel channel;
	private String requestQueueName = "rpc_queue";
	private String replyQueueName;
	private QueueingConsumer consumer;

	public RPCClient2() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);
		connection = factory.newConnection();
		channel = connection.createChannel();

		replyQueueName = channel.queueDeclare().getQueue();
		consumer = new QueueingConsumer(channel);
		channel.basicConsume(replyQueueName, true, consumer);
	}

	public String call(String message) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		String response = null;
		String corrId = UUID.randomUUID().toString();

		BasicProperties props = new BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName).build();
		channel.basicPublish("", requestQueueName, props, message.getBytes());

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			if (delivery.getProperties().getCorrelationId().equals(corrId)) {
				response = new String(delivery.getBody());
				return response;
			}
		}

	}

	public void close() throws Exception {
		connection.close();
	}

	public static void main(String args[]) throws Exception {
		RPCClient2 fibRpc = new RPCClient2();
		String response = fibRpc.call("40");
		System.out.println(" 返回消息 '" + response + "'");
		fibRpc.close();

	}
}
