package com.bondex.rabbitmq;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.bondex.dao.LogInfoDao;
import com.bondex.rabbitmq.multiconfig.MessageHelper;
import com.bondex.service.CurrentLabelService;
import com.bondex.service.LabelInfoService;
import com.bondex.service.PaiangService;
import com.bondex.util.GsonUtil;
import com.bondex.util.JsonUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;

/**
 * @version 2018年5月14日 11:30:40
 * @author bondex_public
 *
 */
@Component
public class Consumer {

	/**
	 * org.slf4j.Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Resource(name="labelInfoServiceImpl")
	private LabelInfoService labelInfoService;
	

	@Resource(name="paiangServiceImple")
	private PaiangService paiangService;
	
	@Autowired
	private CurrentLabelService currentLabelService;

	/**
	 * 原始做法
	 * @param msg
	 */
//	@RabbitListener(queues={"${spring.rabbitmq.paiangQueues}"},containerFactory ="vpnNetListenerContainerFactory")
	public void flowPaiang(Message msg, Channel channel) {
		String correlationId;
		try {
			correlationId = MessageHelper.CheckbackQueueMessage(msg, channel);
			if(StringUtils.isBlank(correlationId)){return;}//消息自动应答Ack,结束此次消费
			String string = MessageHelper.msgToObj(msg,String.class);
			List<Map<String, Object>> list = GsonUtil.GsonToListMaps(string);
			for (Map<String, Object> map : list) {
				// 获取需要入库的数据
				String orderNo = map.get("id") != null ? map.get("id").toString() : "";// 委托单号
				String MBLNo = map.get("MBLNo") != null ? map.get("MBLNo").toString() : "";// 运单号
				String TakeCargoBillNo = map.get("TakeCargoBillNo") != null ? map.get("TakeCargoBillNo").toString() : "";// 分单号
				String QTY = map.get("QTY") != null ? map.get("QTY").toString() : "";// 数量
				
				String EDeparture = map.get("EDeparture") != null ? map.get("EDeparture").toString() : "";// 发货日期
				String SendAddress = map.get("SendAddress") != null ? map.get("SendAddress").toString() : "";// 发货人信息
				String RecCustomerName = map.get("RecCustomerName") != null ? map.get("RecCustomerName").toString() : "";// 收货人信息
				String RecAddress = map.get("RecAddress") != null ? map.get("RecAddress").toString() : "";// 收货人信息
				String TakeCargoNo = map.get("TakeCargoNo") != null ? map.get("TakeCargoNo").toString() : "";// 随货同行单号
				
				switch (map.get("type").toString()) {
				case "a": //增加
					StringBuilder add = new StringBuilder(
							"insert into label(MBLNo," + (EDeparture.equals("") ? "" : "EDeparture,")
									+ "SendAddress,RecCustomerName,RecAddress,TakeCargoNo,business_type,reserve3,reserve1,is_print,airport_departure,order_no) values('"
									+ MBLNo + "'," + (EDeparture.equals("") ? "" : "'" + EDeparture + "',") + " '"
									+ SendAddress + "','" + RecCustomerName + "','" + RecAddress + "','" + TakeCargoNo
									+ "',0,'4','0','0','SIA','" + orderNo + "')");
					jdbcTemplate.update(add.toString());
					break;
				case "u": //更新
					StringBuilder update = new StringBuilder("update label set MBLNo = '" + MBLNo + "', "
							+ (EDeparture.equals("") ? "" : "EDeparture = '" + EDeparture + "',") + " SendAddress = '"
							+ SendAddress + "',RecCustomerName = '" + RecCustomerName + "',RecAddress = '" + RecAddress
							+ "',TakeCargoNo = '" + TakeCargoNo + "' where order_no = '" + orderNo + "' ");
					jdbcTemplate.update(update.toString());
					break;
				case "d"://删除
					StringBuilder delete = new StringBuilder(
							"update label set reserve1 = '1'  where order_no = '" + orderNo + "' ");
					jdbcTemplate.update(delete.toString());
					break;
				default:
					break;
				}
	
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
	}
	
	
	@RabbitListener(queues={"${spring.rabbitmq.paiangQueues}"},containerFactory ="vpnNetListenerContainerFactory")
	public void ListenerPaiangData(Message msg, Channel channel){
		String message = null;
		try {
			String correlationId = MessageHelper.CheckbackQueueMessage(msg, channel);
			if(StringUtils.isBlank(correlationId)){return;}//消息自动应答Ack,结束此次消费
			message = MessageHelper.msgToObj(msg, String.class);
			logger.debug("队列名称：[{}]监听到的消息：[{}]", "PaiAngPrintQueue", message);
			//保存标签数据
			paiangService.paiangSaveService(message,correlationId);
			
		} catch (Exception e) {
			System.err.println(message);
			e.printStackTrace();
			
		}
		
		
	}
	
	/**
	 * 监听成都重庆空运标签数据
	 * 每个消费者对应的listener有个Exclusive参数，默认为false, 如果设置为true，concurrency就必须设置为1，即只能单个消费者消费队列里的消息，适用于必须严格执行消息队列的消费顺序（先进先出）
	 * @param msg
	 * @param channel
	 */
	@RabbitListener(queues={"${spring.rabbitmq.airQueues}"},containerFactory ="vpnNetListenerContainerFactory")
    public void ListenerLabel(Message msg, Channel channel){
		String message = null;
		try {
			String correlationId = MessageHelper.CheckbackQueueMessage(msg, channel);
			if(StringUtils.isBlank(correlationId)){return;}//消息自动应答Ack,结束此次消费
			message = MessageHelper.msgToObj(msg, String.class);
			logger.debug("队列名称：[{}]监听到的消息：[{}]", "air_label_queue_o", message);
			
			//保存标签数据
			labelInfoService.labelInfoSave(message,correlationId);
			
		} catch (Exception e) {
			System.err.println(message);
			e.printStackTrace();
			
		}
	}
	
	/**
	 * 监听通用标签数据
	 * RabbitMQ Java Client
	 * 从消息属性中获取reply_to，correlation_id属性，把调用结果发送给reply_to指定的队列，发送的消息属性要带上correlation_id
	 * @param msg
	 * @param channel
	 */
	@RabbitListener(queues={"${spring.rabbitmq.currentLableQueues}"},containerFactory ="vpnNetListenerContainerFactory")
	public void ListenerCurrentLabel(Message msg, Channel channel){
		String message = null;
		try {
			String correlationId = MessageHelper.CheckbackQueueMessage(msg, channel);
			if(StringUtils.isBlank(correlationId)){return;}//消息自动应答Ack,结束此次消费
			MessageProperties messageProperties = msg.getMessageProperties();
			message = MessageHelper.msgToObj(msg, String.class);
			logger.debug("队列名称：[{}]监听到的消息：[{}];消息属性：[{}]", "current_labelPrint_queue", message,messageProperties);
			
			//保存数据
			Object object = currentLabelService.saveBaseLabelMsg(message,correlationId);
			
			//返回保存情况
			byte[] body = JsonUtil.objToStr(object).getBytes("utf-8");
			String replyTo = messageProperties.getReplyTo(); //用于指定回复的队列的名称
			BasicProperties replyProperties = new BasicProperties.Builder().correlationId(correlationId).build();
			channel.basicPublish("", replyTo, replyProperties, body); //发送回执消息
			
		} catch (Exception e) {
			System.err.println(message);
			e.printStackTrace();
			
		}
	}

}
