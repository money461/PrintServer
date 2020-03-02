package com.bondex.rabbitmq;

import java.util.HashMap;

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

import com.bondex.client.entity.Region;
import com.bondex.rabbitmq.multiconfig.MessageHelper;
import com.bondex.util.RandomUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
@Component
public class Producer {
	

	/**
	 * org.slf4j.Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
	public void vpnNetPrint(String message, Region regionInfo) throws Exception {
		 // 声明交换机
		String EXCHANGES_NAME = "air_print_label_SendExchangeYXCD"; 
		vpnNetRabbitAdmin.declareExchange(new TopicExchange(EXCHANGES_NAME, true, false, new HashMap<>()));
		
		String ROUNTING_KEY = "air_print_label_" + regionInfo.getParent_code() + "_" + regionInfo.getRegion_code() + "_queue_RootKey"; 
		String QUEUE_NAME = "air_print_label_" + regionInfo.getParent_code() + "_" + regionInfo.getRegion_code() + "_queue"; 
		
		//定义队列，均为持久化
		// 1.队列名2.是否持久化，3是否局限与链接，4不再使用是否删除，5其他的属性
		vpnNetRabbitAdmin.declareQueue(new Queue(QUEUE_NAME, true, false, false, null));
		//Binding 绑定
		vpnNetRabbitAdmin.declareBinding(new Binding(QUEUE_NAME,Binding.DestinationType.QUEUE, EXCHANGES_NAME,ROUNTING_KEY,new HashMap()));
		
		
		logger.info("发送消息至交换机：【{}】,路由器：【{}】,队列名称：【{}】，消息体：【{}】",EXCHANGES_NAME,ROUNTING_KEY,QUEUE_NAME,message);
		
		CorrelationData correlationId = new CorrelationData(RandomUtil.generateStr(32));
		
		vpnNetRabbitTemplate.convertAndSend(EXCHANGES_NAME, ROUNTING_KEY, MessageHelper.objToMsg(message),correlationId);
		
	}
	/**
	 * 发送消息至外网  通过区域 划分到指定的路由端 至消息队列中
	 * @param message
	 * @param regionInfo
	 * @throws Exception
	 */
	public void outNetPrint(String message, Region regionInfo) throws Exception {
		// 声明交换机
		String EXCHANGES_NAME = "air_print_label_SendExchangeYXCD"; 
		outNetRabbitAdmin.declareExchange(new TopicExchange(EXCHANGES_NAME, true, false, new HashMap<>()));
		
		String ROUNTING_KEY = "air_print_label_" + regionInfo.getParent_code() + "_" + regionInfo.getRegion_code() + "_queue_RootKey"; 
		String QUEUE_NAME = "air_print_label_" + regionInfo.getParent_code() + "_" + regionInfo.getRegion_code() + "_queue"; 
		
		//定义队列，均为持久化
		// 1.队列名2.是否持久化，3是否局限与链接，4不再使用是否删除，5其他的属性
		outNetRabbitAdmin.declareQueue(new Queue(QUEUE_NAME, true, false, false, null));
		//Binding 绑定
		outNetRabbitAdmin.declareBinding(new Binding(QUEUE_NAME,Binding.DestinationType.QUEUE, EXCHANGES_NAME,ROUNTING_KEY,new HashMap()));
		
		logger.info("发送消息至交换机：【{}】,路由器：【{}】,队列名称：【{}】，消息体：【{}】",EXCHANGES_NAME,ROUNTING_KEY,QUEUE_NAME,message);
		
		CorrelationData correlationId = new CorrelationData(RandomUtil.generateStr(32));
		
		outNetRabbitTemplate.convertAndSend(EXCHANGES_NAME, ROUNTING_KEY, MessageHelper.objToMsg(message),correlationId);
		
	}
	
	
	/**
	 * 发送消息  通过区域 划分到指定的路由端 至消息队列中
	 * @param message
	 * @param region
	 * @throws Exception
	 */
	public void print(String message, Region regionInfo) throws Exception {
//		String uuid = UUID.randomUUID().toString().toString().replace("-", "");    //产生一个 关联ID correlationID
//		CorrelationData correlationId = new CorrelationData(uuid);
		String EXCHANGES_NAME = "air_print_label_SendExchangeYXCD"; 
		
		String ROUNTING_KEY = "air_print_label_" + regionInfo.getParent_code() + "_" + regionInfo.getRegion_code() + "_queue_RootKey"; 
		String QUEUE_NAME = "air_print_label_" + regionInfo.getParent_code() + "_" + regionInfo.getRegion_code() + "_queue"; 
		
		//air_print_label_xian_xianyang_queue 
//		amqpTemplate.convertAndSend(QUEUE_NAME, message,correlationId); 
		//{"Version":"2.0","ReportID":"4e2b8f59-9858-4297-962f-6ee4862085aa","ReportTplName":"标签-派昂医药","SenderName":"资讯/赵辰辉/赵辰辉","SendOPID":"0001655","OtherToShow":"","ReportWidth":"100","ReportHeight":"100","data":"{\"vwOrderAll\":[{\"eDeparture\":\"2019-07-16\",\"label_id\":0,\"mBLNo\":\"YD2019071501\",\"recAddress\":\"贵阳***(\",\"recCustomerName\":\"贵州省医药（集团）有限责任公司(\",\"reserve3\":\"4\",\"sendAddress\":\"西安杨森\",\"takeCargoNo\":\"8650384640(\"}]}"}
		
		logger.info("发送消息至交换机：【{}】,路由器：【{}】,队列名称：【{}】，消息体：【{}】",EXCHANGES_NAME,ROUNTING_KEY,QUEUE_NAME,message);
		
		// 根据连接工厂创建连接
		ConnectionFactory rabbitConnectionFactory = vpnNetConnectionFactory.getRabbitConnectionFactory();
		// 根据连接创建频道
		Connection connection = rabbitConnectionFactory.newConnection();
		Channel channel = connection.createChannel();
		// 将路由与队列信息绑定到频道
		// 1.队列名2.是否持久化，3是否局限与链接，4不再使用是否删除，5其他的属性
		channel.queueDeclare(QUEUE_NAME, true, false, false, null);
//		channel.exchangeDeclare(EXCHANGES_NAME, "topic", true, false, null);
		// 第一参数空表示使用默认exchange，第二参数表示发送到的queue，第三参数是发送的消息是（字节数组）
		channel.basicPublish("",QUEUE_NAME, null,message.getBytes("UTF-8"));
		channel.close();// 关闭管道
		connection.close();// 关闭连接
		
	}
	
	
	
}
