package com.phprpctest.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.rpcphp.client.PhpRpc;
import com.rpcphp.client.PhpRpcCallback;
import com.rpcphp.client.PhpRpcException;

public class PhpRocTest implements EntryPoint {

	public void onModuleLoad() {
		TestObject obj = GWT.create(TestObject.class);
		TestService service = (TestService) GWT.create(TestService.class);
		obj.intArr[0]=5;
		obj.stringArr[1]="jojo";
		obj.testObjectArr=null;
		TestObject obj2 = (TestObject)GWT.create(TestObject.class);

		obj.testObjectArr= new TestObject[2];
		obj.testObjectArr[0]=obj2;
		obj2.intArr[1]=6;
		obj2.stringArr[0]="obj2";
		
		String str = obj.toJSONObject().toString();
		
		obj = GWT.create(TestObject.class);
		//Window.alert(str);
		obj.parseJSON(str);
		
		//Window.alert(obj.object[0].aaa[0]);
		
		PhpRpcCallback<TestObject> callback = new PhpRpcCallback<TestObject>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(TestObject result) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onThrowable(PhpRpcException rpcException) {
				Window.alert(rpcException.getExceptionJs().getMessage());
			}

		};
		service.getTestObject(new String[5], 15, obj,	callback);
	}
}
