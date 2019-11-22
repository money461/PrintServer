package com.bondex.rabbitmq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class SoaRelateMQ {
	static String host = "172.16.75.204";
	// static String host="mq.bondex.com.cn";
	static int port = 5672;
	static String username = "ZXOSRMQ012";
	static String password = "astiasti";

	static String queueName = "Alcedo_ReviceAutoSoaRelateXml_Queue";
	static String exchangeName = "listenExchangeAlcedo";
	static String routingKey = "Alcedo_ReviceAutoSoaRelateXml_RoutintKey";

	static String replyQueueName = "replyQueueName_hdf";
	public static String replyExchangeName = "listenExchange_hdf";

	public static void main(String args[]) {
		String xml = readTxtFile("C:/soarelate.xml");
		System.out.println(getResult("test1234", xml));
	}

	
	/**
	 * 发送mq消息,使用默认配置
	 * 
	 * @param message
	 * 
	 * @return
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public String sendMQ(String message) throws IOException, TimeoutException {
		// 获取到连接以及mq通道
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);
		Connection connection = factory.newConnection();
		// 从连接中创建通道
		Channel channel = connection.createChannel();

		/*
		 * 声明（创建）队列 参数1：队列名称 参数2：为true时server重启队列不会消失
		 * 参数3：队列是否是独占的，如果为true只能被一个connection使用，其他连接建立时会抛出异常
		 * 参数4：队列不再使用时是否自动删除（没有连接，并且没有未处理的消息) 参数5：建立队列时的其他参数
		 */
		channel.queueDeclare(queueName, false, false, false, null);
		/*
		 * 任何发送到Fanout Exchange的消息都会被转发到与该Exchange绑定(Binding)的所有Queue上。 1.可以理解为路由表的模式
		 * 2.这种模式不需要RouteKey
		 * 3.这种模式需要提前将Exchange与Queue进行绑定，一个Exchange可以绑定多个Queue，一个Queue可以同多个Exchange进行绑定。
		 * 4.如果接受到消息的Exchange没有与任何Queue绑定，则消息会被抛弃。
		 */
		channel.exchangeDeclare(exchangeName, "fanout", true, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);

		/*
		 * 向server发布一条消息 参数1：exchange名字，若为空则使用默认的exchange 参数2：routing key 参数3：其他的属性
		 * 参数4：消息体 RabbitMQ默认有一个exchange，叫default exchange，它用一个空字符串表示，它是direct
		 * exchange类型， 任何发往这个exchange的消息都会被路由到routing key的名字对应的队列上，如果没有对应的队列，则消息会被丢弃
		 */
		channel.basicPublish(exchangeName, queueName, null, message.getBytes());
		System.out.println(" [生产者] Sent '" + message + "'");

		// 关闭通道和连接
		channel.close();
		connection.close();
		return username;
	}
	
	public static String getResult(String replyRoutingKey, String xml) {
		String msg = "timeout";
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(host);
			factory.setPort(port);
			factory.setUsername(username);
			factory.setPassword(password);
			Connection connection = factory.newConnection();
			Channel send = connection.createChannel();
			send.queueDeclare(queueName, true, false, false, null);
			send.exchangeDeclare(exchangeName, "topic", true, false, null);
			send.queueBind(queueName, exchangeName, routingKey);
			send.basicPublish("", queueName, null, xml.getBytes("utf-8"));
			send.close();// 关闭管道

			Channel receive = connection.createChannel();
			// 将路由与队列信息绑定到频道
			receive.queueDeclare(replyQueueName, true, false, false, null);
			receive.exchangeDeclare(replyExchangeName, "topic", true, false, null);

			receive.queueBind(replyQueueName, replyExchangeName, replyRoutingKey);
			QueueingConsumer consumer = new QueueingConsumer(receive);
			boolean autoAck = false;
			receive.basicConsume(replyQueueName, autoAck, consumer);
			// 通过死循环反复轮询并获取数据
			while (true) {
				// 获取消息，如果没有消息，这一步将会一直阻塞
				Delivery delivery = consumer.nextDelivery(60000);
				if (delivery != null) {
					msg = new String(delivery.getBody(), "UTF-8");
					receive.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				}
				receive.queueUnbind(replyQueueName, replyExchangeName, replyRoutingKey);
				break;
			}
			receive.close();
			connection.close();// 关闭连接
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	public static String readTxtFile(String filePath) {
		try {
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				StringBuffer sb = new StringBuffer();
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sb.append(lineTxt);
				}
				read.close();
				return sb.toString();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return null;
	}

}
