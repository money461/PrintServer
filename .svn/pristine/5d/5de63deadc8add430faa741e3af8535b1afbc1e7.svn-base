package org.tempuri1;

public class IPermissionServiceProxy implements org.tempuri1.IPermissionService {
	private String _endpoint = null;
	private org.tempuri1.IPermissionService iPermissionService = null;

	public IPermissionServiceProxy() {
		_initIPermissionServiceProxy();
	}

	public IPermissionServiceProxy(String endpoint) {
		_endpoint = endpoint;
		_initIPermissionServiceProxy();
	}

	private void _initIPermissionServiceProxy() {
		try {
			iPermissionService = (new org.tempuri1.PermissionServiceLocator()).getBasicHttpBinding_IPermissionService();
			if (iPermissionService != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) iPermissionService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) iPermissionService)._getProperty("javax.xml.rpc.service.endpoint.address");
			}

		} catch (javax.xml.rpc.ServiceException serviceException) {
		}
	}

	public String getEndpoint() {
		return _endpoint;
	}

	public void setEndpoint(String endpoint) {
		_endpoint = endpoint;
		if (iPermissionService != null)
			((javax.xml.rpc.Stub) iPermissionService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public org.tempuri1.IPermissionService getIPermissionService() {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService;
	}

	public java.lang.String getHasPermissionModuleList(java.lang.String applicationId, java.lang.String operatorId) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.getHasPermissionModuleList(applicationId, operatorId);
	}

	public java.lang.String getHasPermissionPageButton(java.lang.String applicationId, java.lang.String pageCode, java.lang.String preButtonName, java.lang.String operatorId) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.getHasPermissionPageButton(applicationId, pageCode, preButtonName, operatorId);
	}

	public java.lang.String getOperatorDataPermission(java.lang.String userId, java.lang.String opId, java.lang.String root, java.lang.String applicationId, java.lang.String pageCode, java.lang.String viewName) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.getOperatorDataPermission(userId, opId, root, applicationId, pageCode, viewName);
	}

	public java.lang.String getUserCustomList(java.lang.String applicationId, java.lang.String pageCode, java.lang.String viewName, java.lang.String userId, java.lang.String opId) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.getUserCustomList(applicationId, pageCode, viewName, userId, opId);
	}

	public java.lang.String getCustomListById(java.lang.String id) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.getCustomListById(id);
	}

	public java.lang.String saveUserCustomList(java.lang.String userCustomListJson) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.saveUserCustomList(userCustomListJson);
	}

	public java.lang.String getCustomListCommon(java.lang.String applicationId, java.lang.String pageCode, java.lang.String viewName, java.lang.String viewType) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.getCustomListCommon(applicationId, pageCode, viewName, viewType);
	}

	public java.lang.String getCurrentUserAllOperators(java.lang.String systemId, java.lang.String token, java.lang.String userId) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.getCurrentUserAllOperators(systemId, token, userId);
	}

	public java.lang.String getOperatorSearch(java.lang.String searchItem, java.lang.Integer maxLength) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.getOperatorSearch(searchItem, maxLength);
	}

	public java.lang.String getDeptLogicIdByOperatorId(java.lang.String operatorId) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.getDeptLogicIdByOperatorId(operatorId);
	}

	public java.lang.String getIndependentCompanyByOperatorId(java.lang.String operatorId) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.getIndependentCompanyByOperatorId(operatorId);
	}

	public java.lang.String getOperatorByDeptId(java.lang.String deptId) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.getOperatorByDeptId(deptId);
	}

	public java.lang.String getListByDeptIDAndName(java.lang.String deptId, java.lang.String deptName, java.lang.String topCount) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.getListByDeptIDAndName(deptId, deptName, topCount);
	}

	public java.lang.String getDeptLinkInfoByOpId(java.lang.String operatorId) throws java.rmi.RemoteException {
		if (iPermissionService == null)
			_initIPermissionServiceProxy();
		return iPermissionService.getDeptLinkInfoByOpId(operatorId);
	}

}