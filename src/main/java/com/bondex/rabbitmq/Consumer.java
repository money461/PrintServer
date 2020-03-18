package com.bondex.rabbitmq;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.bondex.rabbitmq.multiconfig.MessageHelper;
import com.bondex.service.LabelInfoService;
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

	@RabbitListener(queues={"${spring.rabbitmq.paiangQueues}"},containerFactory ="vpnNetListenerContainerFactory")
	public void flowPaiang(List<Map<String, Object>> msg) throws UnsupportedEncodingException {
		logger.debug("收到消息：{}", msg);
		for (Map<String, Object> map : msg) {
			// 获取需要入库的数据
			String orderNo = map.get("id") != null ? map.get("id").toString() : "";// 委托单号
			String MBLNo = map.get("MBLNo") != null ? map.get("MBLNo").toString() : "";// 运单号
			String EDeparture = map.get("EDeparture") != null ? map.get("EDeparture").toString() : "";// 发货日期
			String SendAddress = map.get("SendAddress") != null ? map.get("SendAddress").toString() : "";// 发货人信息
			String RecCustomerName = map.get("RecCustomerName") != null ? map.get("RecCustomerName").toString() : "";// 收货人信息
			String RecAddress = map.get("RecAddress") != null ? map.get("RecAddress").toString() : "";// 收货人信息
			String TakeCargoNo = map.get("TakeCargoNo") != null ? map.get("TakeCargoNo").toString() : "";// 随货同行单号
			switch (map.get("type").toString()) {
			case "a":
				StringBuilder add = new StringBuilder(
						"insert into label(MBLNo," + (EDeparture.equals("") ? "" : "EDeparture,")
								+ "SendAddress,RecCustomerName,RecAddress,TakeCargoNo,business_type,reserve3,reserve1,is_print,airport_departure,order_no) values('"
								+ MBLNo + "'," + (EDeparture.equals("") ? "" : "'" + EDeparture + "',") + " '"
								+ SendAddress + "','" + RecCustomerName + "','" + RecAddress + "','" + TakeCargoNo
								+ "',0,'4','0','0','SIA','" + orderNo + "')");
				jdbcTemplate.update(add.toString());
				break;
			case "u":
				StringBuilder update = new StringBuilder("update label set MBLNo = '" + MBLNo + "', "
						+ (EDeparture.equals("") ? "" : "EDeparture = '" + EDeparture + "',") + " SendAddress = '"
						+ SendAddress + "',RecCustomerName = '" + RecCustomerName + "',RecAddress = '" + RecAddress
						+ "',TakeCargoNo = '" + TakeCargoNo + "' where order_no = '" + orderNo + "' ");
				jdbcTemplate.update(update.toString());
				break;
			case "d":
				StringBuilder delete = new StringBuilder(
						"update label set reserve1 = '1'  where order_no = '" + orderNo + "' ");
				jdbcTemplate.update(delete.toString());
				break;
			default:
				break;
			}

		}

	}

	@RabbitListener(queues={"${spring.rabbitmq.airQueues}"},containerFactory ="vpnNetListenerContainerFactory")
    public void ListenerLabel(Message msg, Channel channel){
		String message = null;
		try {
			message = MessageHelper.msgToObj(msg, String.class);
			logger.debug("队列名称：[{}]监听到的消息：[{}]", "air_label_queue_o", message);
			//保存标签数据
			labelInfoService.labelInfoSave(message);
			
		} catch (Exception e) {
			System.err.println(message);
			e.printStackTrace();
			
		}
	}

}
