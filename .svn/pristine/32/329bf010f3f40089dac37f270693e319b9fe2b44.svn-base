/**
 * FlowNumberServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class FlowNumberServiceLocator extends org.apache.axis.client.Service implements org.tempuri.FlowNumberService {

    public FlowNumberServiceLocator() {
    }


    public FlowNumberServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public FlowNumberServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BasicHttpBinding_IFlowNumberService
    private java.lang.String BasicHttpBinding_IFlowNumberService_address = "http://api.bondex.com.cn:12360/SupportPlatService/FlowNumberService.svc";

    public java.lang.String getBasicHttpBinding_IFlowNumberServiceAddress() {
        return BasicHttpBinding_IFlowNumberService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BasicHttpBinding_IFlowNumberServiceWSDDServiceName = "BasicHttpBinding_IFlowNumberService";

    public java.lang.String getBasicHttpBinding_IFlowNumberServiceWSDDServiceName() {
        return BasicHttpBinding_IFlowNumberServiceWSDDServiceName;
    }

    public void setBasicHttpBinding_IFlowNumberServiceWSDDServiceName(java.lang.String name) {
        BasicHttpBinding_IFlowNumberServiceWSDDServiceName = name;
    }

    public org.tempuri.IFlowNumberService getBasicHttpBinding_IFlowNumberService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BasicHttpBinding_IFlowNumberService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBasicHttpBinding_IFlowNumberService(endpoint);
    }

    public org.tempuri.IFlowNumberService getBasicHttpBinding_IFlowNumberService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.BasicHttpBinding_IFlowNumberServiceStub _stub = new org.tempuri.BasicHttpBinding_IFlowNumberServiceStub(portAddress, this);
            _stub.setPortName(getBasicHttpBinding_IFlowNumberServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBasicHttpBinding_IFlowNumberServiceEndpointAddress(java.lang.String address) {
        BasicHttpBinding_IFlowNumberService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.tempuri.IFlowNumberService.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.BasicHttpBinding_IFlowNumberServiceStub _stub = new org.tempuri.BasicHttpBinding_IFlowNumberServiceStub(new java.net.URL(BasicHttpBinding_IFlowNumberService_address), this);
                _stub.setPortName(getBasicHttpBinding_IFlowNumberServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("BasicHttpBinding_IFlowNumberService".equals(inputPortName)) {
            return getBasicHttpBinding_IFlowNumberService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "FlowNumberService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "BasicHttpBinding_IFlowNumberService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("BasicHttpBinding_IFlowNumberService".equals(portName)) {
            setBasicHttpBinding_IFlowNumberServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
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
