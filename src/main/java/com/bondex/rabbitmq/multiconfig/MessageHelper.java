package com.bondex.rabbitmq.multiconfig;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;

import com.bondex.util.JsonUtil;
import com.bondex.util.RandomUtil;
import com.bondex.util.StringUtils;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;


public class MessageHelper {

	//设置消息
    public static Message objToMsg(Object obj) throws UnsupportedEncodingException {
        if (null == obj) {
            return null;
        }

        Message message = MessageBuilder.withBody(JsonUtil.objToStr(obj).getBytes("utf-8")).setContentEncoding("utf-8").build();
        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);// 消息持久化
        message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
        message.getMessageProperties().setCorrelationIdString(RandomUtil.UUID32());
        message.getMessageProperties().setTimestamp(new Date()); //设置时间

        return message;
    }

    //取出消息转为实体
    public static <T> T msgToObj(Message message, Class<T> clazz) {
        if (null == message || null == clazz) {
            return null;
        }

        String str = byteArrayToStr(message.getBody(),"utf-8");
        T obj = JsonUtil.strToObj(str, clazz);

        return obj;
    }
    
    /**
     * 校验消息 是否封装  correlationId
     * @param message
     * @param channel
     * @return
     * @throws IOException
     */
    public static String CheckbackQueueMessage(Message message, Channel channel ) throws IOException{
    	MessageProperties messageProperties = message.getMessageProperties();
    	@SuppressWarnings("deprecation")
		byte[] correlationbyte = messageProperties.getCorrelationId();//关联id 过时了spring rabbitmq 2.0新版不存在这个
    	String correlationId = byteArrayToStr(correlationbyte,"utf-8");
    	if(StringUtils.isBlank(correlationId)){
			correlationId = RandomUtil.UUID32();
			//重回队列
			//重新发送消息到队尾
	          //创建消息属性 
			BasicProperties basicProperties = com.rabbitmq.client.MessageProperties.PERSISTENT_TEXT_PLAIN //priority==0 保证回到队尾
																					.builder() 
																					.deliveryMode(2)
																	        		.contentType("text/plain")
																	        		.contentEncoding("utf-8")
																	        		.correlationId(correlationId)   //携带唯一的 correlationID
																	        		.build();    //携带callback 回调的队列路由键
            channel.basicQos(1); //;保证一次只分发一个 
            
            channel.basicPublish(message.getMessageProperties().getReceivedExchange(),  message.getMessageProperties().getReceivedRoutingKey(),basicProperties,message.getBody());
            return null;
		}
    	return correlationId;
    }
    
    public static String byteArrayToStr(byte[] byteArray,String charsetName) {
        if (byteArray == null) {
            return null;
        }
        String str = null;
		try {
			str = new String(byteArray,charsetName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return str;
    }

}
