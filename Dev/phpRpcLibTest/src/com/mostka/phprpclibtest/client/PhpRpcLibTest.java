package com.mostka.phprpclibtest.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.mostka.phprpc.client.PhpRpc;
import com.mostka.phprpc.client.PhpRpcCallback;
import com.mostka.phprpc.client.PhpRpcException;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PhpRpcLibTest implements EntryPoint {
	public void onModuleLoad() {
		GWT.create(TestObject2.class);
		TestService service = GWT.create(TestService.class);
		TestObject obj = GWT.create(TestObject.class);
		obj.bbb[0]=5;
		obj.aaa[1]="jojo";
		obj.object=null;
		TestObject obj2 = (TestObject)GWT.create(TestObject.class);

		obj.object= new TestObject[2];
		obj.object[0]=obj2;
		obj2.bbb[1]=6;
		obj2.aaa[0]="obj2";
		
		//String str = obj.toJSONObject().toString();
		
		obj = GWT.create(TestObject.class);
		//Window.alert(str);
		//obj.parseJSON(str);
		
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
		service.getTestObject("string", 15, obj,	callback);
	}
}
