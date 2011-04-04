package com.mostka.phprpclibtest.client;

import com.mostka.phprpc.client.PhpRpcObject;

public class PhpRpcReturnsLinkerImpl implements com.mostka.phprpc.client.PhpRpcReturnsLinker {
	
	private static PhpRpcReturnsLinkerImpl instance = new PhpRpcReturnsLinkerImpl();
	
	private PhpRpcReturnsLinkerImpl() {}
	public static PhpRpcReturnsLinkerImpl getInstance(){
		return instance;
	}
	
	@Override
	public  PhpRpcObject getReturnInstance(int instanceLinkerPos){
		switch (instanceLinkerPos) {
		case 9:return new com.mostka.phprpclibtest.client.TestObject();

		default:return null;
		}
	}
}
