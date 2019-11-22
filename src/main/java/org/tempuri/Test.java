package org.tempuri;

import java.rmi.RemoteException;

public class Test {
	public static void main(String[] args) throws RemoteException {
		IPermissionService permissionService = new IPermissionServiceProxy();
		String a = permissionService.getUserReport("IT030", "821", "QuotationInfo", "QuotationInfo.btnPrint");
		System.err.println(a);

	}

}
