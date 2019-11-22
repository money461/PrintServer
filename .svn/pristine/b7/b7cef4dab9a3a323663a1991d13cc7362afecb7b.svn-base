/**
 * PermissionServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri1;

public class PermissionServiceLocator extends org.apache.axis.client.Service implements org.tempuri1.PermissionService {

	public PermissionServiceLocator() {
		super();
	}

	public PermissionServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public PermissionServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for BasicHttpBinding_IPermissionService
	private java.lang.String BasicHttpBinding_IPermissionService_address = "http://api.bondex.com.cn:12360/SupportPlatService/PermissionService.svc";

	public java.lang.String getBasicHttpBinding_IPermissionServiceAddress() {
		return BasicHttpBinding_IPermissionService_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String BasicHttpBinding_IPermissionServiceWSDDServiceName = "BasicHttpBinding_IPermissionService";

	public java.lang.String getBasicHttpBinding_IPermissionServiceWSDDServiceName() {
		return BasicHttpBinding_IPermissionServiceWSDDServiceName;
	}

	public void setBasicHttpBinding_IPermissionServiceWSDDServiceName(java.lang.String name) {
		BasicHttpBinding_IPermissionServiceWSDDServiceName = name;
	}

	public org.tempuri1.IPermissionService getBasicHttpBinding_IPermissionService() throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(BasicHttpBinding_IPermissionService_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getBasicHttpBinding_IPermissionService(endpoint);
	}

	public org.tempuri1.IPermissionService getBasicHttpBinding_IPermissionService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			org.tempuri1.BasicHttpBinding_IPermissionServiceStub _stub = new org.tempuri1.BasicHttpBinding_IPermissionServiceStub(portAddress, this);
			_stub.setPortName(getBasicHttpBinding_IPermissionServiceWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setBasicHttpBinding_IPermissionServiceEndpointAddress(java.lang.String address) {
		BasicHttpBinding_IPermissionService_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no
	 * port for the given interface, then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (org.tempuri1.IPermissionService.class.isAssignableFrom(serviceEndpointInterface)) {
				org.tempuri1.BasicHttpBinding_IPermissionServiceStub _stub = new org.tempuri1.BasicHttpBinding_IPermissionServiceStub(new java.net.URL(BasicHttpBinding_IPermissionService_address), this);
				_stub.setPortName(getBasicHttpBinding_IPermissionServiceWSDDServiceName());
				return _stub;
			}
		} catch (java.lang.Throwable t) {
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no
	 * port for the given interface, then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		java.lang.String inputPortName = portName.getLocalPart();
		if ("BasicHttpBinding_IPermissionService".equals(inputPortName)) {
			return getBasicHttpBinding_IPermissionService();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://tempuri.org/", "PermissionService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "BasicHttpBinding_IPermissionService"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

		if ("BasicHttpBinding_IPermissionService".equals(portName)) {
			setBasicHttpBinding_IPermissionServiceEndpointAddress(address);
		} else { // Unknown Port Name
			throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
		}
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
