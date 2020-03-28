package com.bondex.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bondex.entity.Region;
import com.bondex.rabbitmq.multiconfig.MessageHelper;
import com.bondex.rabbitmq.util.ChannelUtils;
import com.bondex.util.GsonUtil;
import com.bondex.util.JsonUtil;
import com.bondex.util.RandomUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
@Component
public class Producer {
	

	/**
	 * org.slf4j.Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static String SendExchanage = "air_print_label_SendExchangeYXCD";

	//内网链接工厂
	@Autowired
   @Qualifier(value = "vpnNetConnectionFactory")
	private CachingConnectionFactory vpnNetConnectionFactory;
	//内网vpnNetRabbitAdmin
	@Autowired
	@Qualifier(value = "vpnNetRabbitAdmin")
	private RabbitAdmin  vpnNetRabbitAdmin;
	
	@Autowired
    @Qualifier(value = "vpnNetRabbitTemplate")
	private RabbitTemplate vpnNetRabbitTemplate;

	
	//外网链接工厂
	@Autowired
	@Qualifier(value = "outNetConnectionFactory")
	private CachingConnectionFactory outNetConnectionFactory;
	
	//外网vpnNetRabbitAdmin
	@Autowired
	@Qualifier(value = "outNetRabbitAdmin")
	private RabbitAdmin  outNetRabbitAdmin;
	
	@Autowired
	@Qualifier(value = "outNetRabbitTemplate")
	private RabbitTemplate outNetRabbitTemplate;

	/**
	 * 发送消息至内网VPN  通过区域 划分到指定的路由端 至消息队列中
	 * @param message
	 * @param regionInfo chengdu/jichang
	 * @throws Exception
	 * 
	 */
	public void vpnNetPrint(Object message, Region regionInfo) throws Exception {
		 // 声明交换机 可以自动删除
//		vpnNetRabbitAdmin.deleteExchange(SendExchanage);
		vpnNetRabbitAdmin.declareExchange(new TopicExchange(SendExchanage, true, true, new HashMap<>()));
		
		String ROUNTING_KEY = "air_print_label_" + regionInfo.getParent_code() + "_" + regionInfo.getRegion_code() + "_queue_RootKey"; 
		String QUEUE_NAME = "air_print_label_" + regionInfo.getParent_code() + "_" + regionInfo.getRegion_code() + "_queue"; 
		
		//定义队列，均为持久化
		// 1.队列名2.是否持久化，3是否局限与链接，4不再使用是否删除，5其他的属性
//		vpnNetRabbitAdmin.deleteQueue(QUEUE_NAME);
		vpnNetRabbitAdmin.declareQueue(new Queue(QUEUE_NAME, true, false, true, null));
		
		//Binding 绑定
		vpnNetRabbitAdmin.declareBinding(new Binding(QUEUE_NAME,Binding.DestinationType.QUEUE, SendExchanage,ROUNTING_KEY,new HashMap()));
		
		
		logger.info("发送消息至内网交换机：【{}】,路由器：【{}】,队列名称：【{}】，消息体：【{}】",SendExchanage,ROUNTING_KEY,QUEUE_NAME,GsonUtil.GsonString(message));
		
		CorrelationData correlationId = new CorrelationData(RandomUtil.generateStr(32));
		
		vpnNetRabbitTemplate.convertAndSend(SendExchanage, ROUNTING_KEY, MessageHelper.objToMsg(message),correlationId);
		
	}
	/**
	 * 发送消息至外网  通过区域 划分到指定的路由端 至消息队列中
	 * @param message
	 * @param regionInfo
	 * @throws Exception
	 */
	public void outNetPrint(Object message, Region regionInfo) throws Exception {
		// 声明交换机
//		outNetRabbitAdmin.deleteExchange(SendExchanage);
		outNetRabbitAdmin.declareExchange(new TopicExchange(SendExchanage, true, true, new HashMap<>()));
		
		String ROUNTING_KEY = "air_print_label_" + regionInfo.getParent_code() + "_" + regionInfo.getRegion_code() + "_queue_RootKey"; 
		String QUEUE_NAME = "air_print_label_" + regionInfo.getParent_code() + "_" + regionInfo.getRegion_code() + "_queue"; 
		
		//定义队列，均为持久化
		// 1.队列名2.是否持久化，3是否局限与链接，4不再使用是否删除，5其他的属性
//		outNetRabbitAdmin.deleteQueue(QUEUE_NAME);
		outNetRabbitAdmin.declareQueue(new Queue(QUEUE_NAME, true, false, true, null));
		//Binding 绑定
		outNetRabbitAdmin.declareBinding(new Binding(QUEUE_NAME,Binding.DestinationType.QUEUE, SendExchanage,ROUNTING_KEY,new HashMap()));
		
		logger.info("发送消息至外网交换机：【{}】,路由器：【{}】,队列名称：【{}】，消息体：【{}】",SendExchanage,ROUNTING_KEY,QUEUE_NAME,GsonUtil.GsonString(message));
		
		CorrelationData correlationId = new CorrelationData(RandomUtil.generateStr(32));
		
		outNetRabbitTemplate.convertAndSend(SendExchanage, ROUNTING_KEY, MessageHelper.objToMsg(message),correlationId);
		
	}
	
	
	/**
	 * 发送消息  通过区域 划分到指定的路由端 至消息队列中
	 * @param message
	 * @param region
	 * @throws Exception
	 */
	public void print(Object message, Region regionInfo) throws Exception {
		
		String ROUNTING_KEY = "air_print_label_" + regionInfo.getParent_code() + "_" + regionInfo.getRegion_code() + "_queue_RootKey"; 
		String QUEUE_NAME = "air_print_label_" + regionInfo.getParent_code() + "_" + regionInfo.getRegion_code() + "_queue"; 
		
		
		logger.info("发送消息至内网交换机：【{}】,路由器：【{}】,队列名称：【{}】，消息体：【{}】",SendExchanage,ROUNTING_KEY,QUEUE_NAME,message);
		
		// 根据连接工厂创建连接
		ConnectionFactory rabbitConnectionFactory = vpnNetConnectionFactory.getRabbitConnectionFactory();
		// 根据连接创建频道
		Connection connection = rabbitConnectionFactory.newConnection();
		Channel channel = connection.createChannel();
		// 将路由与队列信息绑定到频道
		// 1.队列名2.是否持久化，3是否局限与链接，4不再使用是否删除，5其他的属性
		channel.queueDeclare(QUEUE_NAME, true, false, true, null);
//		channel.exchangeDeclare(EXCHANGES_NAME, BuiltinExchangeType.TOPIC, true, false, null);
		// 第一参数空表示使用默认exchange，第二参数表示发送到的queue，第三参数是发送的消息是（字节数组）
		channel.basicPublish("",QUEUE_NAME, null,JsonUtil.objToStr(message).getBytes("utf-8"));
		channel.close();// 关闭管道
		connection.close();// 关闭连接
	}
	
	
	public void send(Object object) throws IOException{
		
		    Channel channel = ChannelUtils.getChannel("标签打印发送客户端");
		   /* //声明队列 可以不写
	        channel.queueDeclare("队列名称", true, false, false, new HashMap<>());
	        
		    //声明交换机 可以不写
	        channel.exchangeDeclare("交换机名称", BuiltinExchangeType.DIRECT, true, false, new HashMap<>());
		     
	        channel.queueBind("队列名称", "交换机名称", "路由", new HashMap<>());
*/
	        String replyTo = "replyQueueName"; //回执队列名称
	        //声明回执队列
	        channel.queueDeclare(replyTo, true, false, false, new HashMap<>());
	        
	        String correlationId = RandomUtil.UUID32(); //消息id
	        //消息属性需要带上reply_to,correlation_id
	        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().contentType("text/plain").contentEncoding("utf-8").deliveryMode(2).contentType("UTF-8").correlationId(correlationId).replyTo(replyTo).build();
	        
	        byte[] body = JsonUtil.objToStr(object).getBytes("utf-8"); //发送的数据
	       // 第一参数空表示使用默认exchange，第二参数表示发送到的queue，第三 true if the 'mandatory' flag is to be set 如果exchange根据自身类型和消息routeKey无法找到一个符合条件的queue，那么会调用basic.return方法将消息返还给生产者。false：出现上述情形broker会直接将消息扔掉; 第四参数是发送的消息是（字节数组）
	        //channel.basicPublish("","队列名称", true, basicProperties, body);
	        channel.basicPublish("","current_labelPrint_queue", true, basicProperties, body);
	        
	        //创建线程延时队列
	        BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);
	        
	        boolean autoAck = false; //取消自动回复false 
	        //监听回执消息
	        channel.basicConsume(replyTo, autoAck, "标签打印发送客户端", new DefaultConsumer(channel) {
	            @Override
	            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
	            	try {
	                System.out.println("----------RPC调用结果----------");
	                System.out.println(consumerTag);
	                System.out.println("消息属性为:" + properties);
	                String correlationId2 = properties.getCorrelationId();
	                if(correlationId.equalsIgnoreCase(correlationId2) || null!=body){
	                	String backmsg = new String(body,"utf-8");
	                	System.out.println("消息内容为" +backmsg );
	                	response.put(backmsg); //将返回消息放入延时队列
	                	//手动应答
	                	channel.basicAck(envelope.getDeliveryTag(), false);
	                }
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	            }
	        });
	        String sResult="TIME_OUT";
			try {
				String result = response.poll(10, TimeUnit.SECONDS); //60S后存在值 占满队列空间1 就返回消息
				sResult = null==result?sResult:result;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			logger.debug("消息ID:{},返回结果：{}",correlationId,sResult); //1f8089e278b143498da496a3c740e061
			
	}
	
	
	public static void main(String[] args) throws Exception {
		Producer producer = new Producer();
		producer.send("测试发送打印标签数据");
		while(true){
			
		}
	}
	
}
