package com.bondex.test;

import java.rmi.RemoteException;

import org.springframework.stereotype.Component;
import org.tempuri.IFlowNumberServiceProxy;

@Component
public class FrameworkTest {

	public String aa() throws RemoteException {
		IFlowNumberServiceProxy flowNumberServiceProxy = new IFlowNumberServiceProxy();
		String test = flowNumberServiceProxy.getFlowNumber("", "", "");
		return null;
	}

	public static void main(String[] args) throws RemoteException {
		IFlowNumberServiceProxy flowNumberServiceProxy = new IFlowNumberServiceProxy();
		String test = flowNumberServiceProxy.getFlowNumber("", "", "");
	}

}
