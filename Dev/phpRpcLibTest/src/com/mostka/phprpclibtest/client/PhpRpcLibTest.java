package com.mostka.phprpclibtest.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.mostka.phprpc.client.PhpRpcCallback;
import com.mostka.phprpc.client.PhpRpcException;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PhpRpcLibTest implements EntryPoint {
	public void onModuleLoad() {
		//String encoded = Base64.encode("jojo");
		//System.out.println(encoded);
		GWT.create(TestObject2.class);
		TestObject obj = GWT.create(TestObject.class);
		TestService service = GWT.create(TestService.class);
		obj.bbb[0]=5;
		obj.aaa[1]="jojo";
		obj.object=null;
		TestObject obj2 = (TestObject)GWT.create(TestObject.class);

		obj.object= new TestObject[2];
		obj.object[0]=obj2;
		obj2.bbb[1]=6;
		obj2.aaa[0]="obj2";
		
		obj = GWT.create(TestObject.class);
		
		PhpRpcCallback<TestObject> callback = new PhpRpcCallback<TestObject>() {
			public void onFailure(Throwable caught) {
				Window.alert("onFailure1 : "+caught.getMessage());
			}
			public void onSuccess(TestObject result) {
				Window.alert("onSuccess1 : "+result.iii);
			}
			public void onThrowable(PhpRpcException rpcException) {
				Window.alert("onThrowable : "+rpcException.getExceptionJs().getMessage());
			}
		};
		
		PhpRpcCallback<TestObject> callbackSecond = new PhpRpcCallback<TestObject>() {
			public void onFailure(Throwable caught) {
				Window.alert("onFailure1 : "+caught.getMessage());
			}
			public void onSuccess(TestObject result) {
				Window.alert("onSuccess1 : "+result.iii);
			}
			public void onThrowable(PhpRpcException rpcException) {
				Window.alert("onThrowable : "+rpcException.getExceptionJs().getMessage());
			}
		};
		service.getTestObject("string", 15, obj,	callback);
	}
}
