/**
 * IPermissionService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface IPermissionService extends java.rmi.Remote {
    public java.lang.String getHasPermissionModuleList(java.lang.String applicationId, java.lang.String operatorId) throws java.rmi.RemoteException;
    public java.lang.String getHasPermissionPageButton(java.lang.String applicationId, java.lang.String pageCode, java.lang.String preButtonName, java.lang.String operatorId) throws java.rmi.RemoteException;
    public java.lang.String getOperatorDataPermission(java.lang.String userId, java.lang.String opId, java.lang.String root, java.lang.String applicationId, java.lang.String pageCode, java.lang.String viewName) throws java.rmi.RemoteException;
    public java.lang.String getUserCustomList(java.lang.String applicationId, java.lang.String pageCode, java.lang.String viewName, java.lang.String userId, java.lang.String opId) throws java.rmi.RemoteException;
    public java.lang.String getCustomListById(java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String saveUserCustomList(java.lang.String userCustomListJson) throws java.rmi.RemoteException;
    public java.lang.String getCustomListCommon(java.lang.String applicationId, java.lang.String pageCode, java.lang.String viewName, java.lang.String viewType) throws java.rmi.RemoteException;
    public java.lang.String getCurrentUserAllOperators(java.lang.String systemId, java.lang.String token, java.lang.String userId) throws java.rmi.RemoteException;
    public java.lang.String getOperatorSearch(java.lang.String searchItem, java.lang.Integer maxLength) throws java.rmi.RemoteException;
    public java.lang.String getDeptLogicIdByOperatorId(java.lang.String operatorId) throws java.rmi.RemoteException;
    public java.lang.String getIndependentCompanyByOperatorId(java.lang.String operatorId) throws java.rmi.RemoteException;
    public java.lang.String getOperatorByDeptId(java.lang.String deptId) throws java.rmi.RemoteException;
    public java.lang.String getListByDeptIDAndName(java.lang.String deptId, java.lang.String deptName, java.lang.String topCount) throws java.rmi.RemoteException;
    public java.lang.String getDeptLinkInfoByOpId(java.lang.String operatorId) throws java.rmi.RemoteException;
    public java.lang.String getUserReport(java.lang.String opId, java.lang.String applicationId, java.lang.String pageCode, java.lang.String btnNames) throws java.rmi.RemoteException;
}
