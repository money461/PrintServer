package com.bondex.rabbitmq.multiconfig;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;

import com.bondex.util.JsonUtil;
import com.bondex.util.RandomUtil;


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
    public static <T> T msgToObj(Message message, Class<T> clazz) throws UnsupportedEncodingException {
        if (null == message || null == clazz) {
            return null;
        }

        String str = new String(message.getBody(),"utf-8");
        T obj = JsonUtil.strToObj(str, clazz);

        return obj;
    }

}
