package com.mostka.phprpclibtest.client;

import com.mostka.phprpc.client.PhpRpcCallback;
import com.mostka.phprpc.client.PhpRpcService;
import com.mostka.phprpc.client.PhpRpcServiceRelativePath;



@PhpRpcServiceRelativePath("jsonphprpc.config.php")
public class TestService implements PhpRpcService{
	public void getTestObject(String arg1, int arg2, TestObject objTest, PhpRpcCallback<TestObject> p_callBack){};
}
