package com.mostka.phprpc.client;

public interface PhpRpcCallback<T> extends com.google.gwt.user.client.rpc.AsyncCallback<T>{
	void onThrowable(PhpRpcException rpcException);
}
