package com.phprpctest.client;

import com.rpcphp.client.PhpRpcCallback;
import com.rpcphp.client.PhpRpcService;
import com.rpcphp.client.PhpRpcServiceRelativePath;

@PhpRpcServiceRelativePath("server/jsonphprpc.config.php")
public class TestService implements PhpRpcService{
	public void getTestObject(String[] arg1, int arg2, TestObject objTest, PhpRpcCallback<TestObject> p_callBack){};
}
