package com.mostka.gwtphprpc.shared;

@SuppressWarnings("serial")
public class RpcException extends Exception{

	public RpcException(Exception e) {
		super(e);
	}
	public RpcException(String message, Exception e) {
		super(message,e);
	}
	public RpcException() {
		// TODO Auto-generated constructor stub
	}
	public RpcException(String message) {
		super(message);
	}
}
