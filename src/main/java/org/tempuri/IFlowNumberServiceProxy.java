package org.tempuri;

public class IFlowNumberServiceProxy implements org.tempuri.IFlowNumberService {
  private String _endpoint = null;
  private org.tempuri.IFlowNumberService iFlowNumberService = null;
  
  public IFlowNumberServiceProxy() {
    _initIFlowNumberServiceProxy();
  }
  
  public IFlowNumberServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initIFlowNumberServiceProxy();
  }
  
  private void _initIFlowNumberServiceProxy() {
    try {
      iFlowNumberService = (new org.tempuri.FlowNumberServiceLocator()).getBasicHttpBinding_IFlowNumberService();
      if (iFlowNumberService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iFlowNumberService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iFlowNumberService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iFlowNumberService != null)
      ((javax.xml.rpc.Stub)iFlowNumberService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.tempuri.IFlowNumberService getIFlowNumberService() {
    if (iFlowNumberService == null)
      _initIFlowNumberServiceProxy();
    return iFlowNumberService;
  }
  
  public java.lang.String getFlowNumber(java.lang.String applicationId, java.lang.String profitId, java.lang.String opId) throws java.rmi.RemoteException{
    if (iFlowNumberService == null)
      _initIFlowNumberServiceProxy();
    return iFlowNumberService.getFlowNumber(applicationId, profitId, opId);
  }
  
  
}