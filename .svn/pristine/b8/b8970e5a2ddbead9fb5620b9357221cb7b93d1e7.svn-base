package com.bondex.service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 发送MQ消息接口
 * 
 * @author bondex_public
 *
 */
public interface SendMqService {

	/**
	 * 处理传入数据，并调用发送MQ方法发送MQ
	 * 
	 * @return
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public boolean sendMq(String message) throws IOException, TimeoutException;

	/**
	 * 接收mq中的消息
	 * 
	 * @return
	 * @throws TimeoutException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConsumerCancelledException
	 * @throws ShutdownSignalException
	 */
	public boolean receiveMQ() throws IOException, TimeoutException, ShutdownSignalException, ConsumerCancelledException, InterruptedException;

}
