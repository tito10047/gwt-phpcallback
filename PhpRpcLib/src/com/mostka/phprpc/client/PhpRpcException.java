package com.mostka.phprpc.client;

import com.google.gwt.core.client.JavaScriptObject;

public class PhpRpcException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class RpcExceptionJs extends JavaScriptObject{
		protected RpcExceptionJs() {}
		
		public final native RpcExceptionJs create()			/*-{	return {};	}-*/;
		public final native String getMessage()			/*-{	return this.message	}-*/;
		public final native int getCode()				/*-{	return this.code	}-*/;
		public final native String getFile()			/*-{	return this.file	}-*/;
		public final native int getLine()				/*-{	return this.line	}-*/;
		public final native JavaScriptObject getTrace()	/*-{	return this.trace	}-*/;
		public final native String getTraceAsString()	/*-{	return this.trace	}-*/; 
		public final native String getClassName()		/*-{	return this.ClassName	}-*/; 
	}
	
	private RpcExceptionJs exceptionJs = null;
	
	public PhpRpcException(RpcExceptionJs exceptionJs) {
		super(exceptionJs.getMessage());
		this.exceptionJs=exceptionJs;
	}
	
	public RpcExceptionJs getExceptionJs() {
		return exceptionJs;
	}
	
}
