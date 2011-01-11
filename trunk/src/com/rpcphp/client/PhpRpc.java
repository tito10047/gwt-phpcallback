package com.rpcphp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;

public class PhpRpc{

	public static final <T>void callJSONRPCService(final String jsonRequest, final String serverName, final PhpRpcCallback<T> phpRpcCallBack){
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, serverName);
		builder.setHeader("Content-Type", "application/json-rpc");
		
		try {
			@SuppressWarnings("unused")
			Request request = builder.sendRequest(jsonRequest, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
				        try {
				        	//JavaScriptObject l_transferObject=parseResponse(response.getText());
				        	//p_asyncCallBack.onSuccess(l_transferObject);
						} catch (Exception l_e) {
							if (l_e instanceof PhpRpcException) {
								PhpRpcException exception = (PhpRpcException) l_e;
								phpRpcCallBack.onThrowable(exception);
							}else
								phpRpcCallBack.onFailure(l_e);
						}
			        } else {
			        	GWT.log("Call "+serverName+" ERROR: Couldn't retrieve JSON (" + response.getStatusText()+ ")");
			        	phpRpcCallBack.onFailure(new Exception("Call "+serverName+" ERROR: Couldn't retrieve JSON (" + response.getStatusText()+ ")"));
			        }
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					GWT.log("Call "+serverName+" ERROR: Couldn't retrieve JSON");
					phpRpcCallBack.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			GWT.log("Call "+serverName+" ERROR: Couldn't retrieve JSON");
			phpRpcCallBack.onFailure(e);
		}
	}
	public static final <T> T create(Class<?> classLiteral){
		return GWT.create(classLiteral);
	}
	
  	public static JSONArray toJSONArray(String[] arr){
  		if (arr == null) return null;
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i]==null) 
				jsonArray.set(i, null);
			else
				jsonArray.set(i, new JSONString(arr[i]));
		}
  		return jsonArray;
  	}
	public static String[] toJSONString(JSONArray jsonArray){
		if (jsonArray == null) return null;
		String[] val = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			if (jsonArray.get(i).isString()==null)
				val[i]=null;
			else
				val[i]=jsonArray.get(i).isString().stringValue();
		}
		return val;
	}
  	public static  JSONArray toJSONArray(int[] arr){
  		if (arr == null) return null;
  		JSONArray jsonArray = new JSONArray();for (int i = 0; i < arr.length; i++) {
				jsonArray.set(i, new JSONNumber(arr[i]));
		}
  		return jsonArray;
  	}
	public static int[] toJSONint(JSONArray jsonArray){
		if (jsonArray == null) return null;
		int[] val = new int[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			val[i]=(int) jsonArray.get(i).isNumber().doubleValue();
		}
		return val;
	}
	
  	public static  JSONArray toJSONArray(double[] arr){
  		if (arr == null) return null;
  		JSONArray jsonArray = new JSONArray();for (int i = 0; i < arr.length; i++) {
				jsonArray.set(i, new JSONNumber(arr[i]));
		}
  		return jsonArray;
  	}
	public static double[] toJSONdouble(JSONArray jsonArray){
		if (jsonArray == null) return null;
		double[] val = new double[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			val[i]=(double) jsonArray.get(i).isNumber().doubleValue();
		}
		return val;
	}
  	public static  JSONArray toJSONArray(char[] arr){
  		if (arr == null) return null;
  		JSONArray jsonArray = new JSONArray();for (int i = 0; i < arr.length; i++) {
				jsonArray.set(i, new JSONNumber(arr[i]));
		}
  		return jsonArray;
  	}
	public static char[] toJSONchar(JSONArray jsonArray){
		if (jsonArray == null) return null;
		char[] val = new char[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			val[i]=(char) jsonArray.get(i).isNumber().doubleValue();
		}
		return val;
	}
  	public static  JSONArray toJSONArray(long[] arr){
  		if (arr == null) return null;
  		JSONArray jsonArray = new JSONArray();for (int i = 0; i < arr.length; i++) {
				jsonArray.set(i, new JSONNumber(arr[i]));
		}
  		return jsonArray;
  	}
	public static long[] toJSONlong(JSONArray jsonArray){
		if (jsonArray == null) return null;
		long[] val = new long[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			val[i]=(long) jsonArray.get(i).isNumber().doubleValue();
		}
		return val;
	}
  	public static  JSONArray toJSONArray(byte[] arr){
  		if (arr == null) return null;
  		JSONArray jsonArray = new JSONArray();for (int i = 0; i < arr.length; i++) {
				jsonArray.set(i, new JSONNumber(arr[i]));
		}
  		return jsonArray;
  	}
	public static byte[] toJSONbyte(JSONArray jsonArray){
		if (jsonArray == null) return null;
		byte[] val = new byte[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			val[i]=(byte) jsonArray.get(i).isNumber().doubleValue();
		}
		return val;
	}
  	public static  JSONArray toJSONArray(short[] arr){
  		if (arr == null) return null;
  		JSONArray jsonArray = new JSONArray();for (int i = 0; i < arr.length; i++) {
				jsonArray.set(i, new JSONNumber(arr[i]));
		}
  		return jsonArray;
  	}
	public static short[] toJSONshort(JSONArray jsonArray){
		if (jsonArray == null) return null;
		short[] val = new short[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			val[i]=(short) jsonArray.get(i).isNumber().doubleValue();
		}
		return val;
	}
  	public static  JSONArray toJSONArray(boolean[] arr){
  		if (arr == null) return null;
  		JSONArray jsonArray = new JSONArray();for (int i = 0; i < arr.length; i++) {
				jsonArray.set(i, new JSONString(arr[i]+""));
		}
  		return jsonArray;
  	}
	public static boolean[] toJSONboolean(JSONArray jsonArray){
		if (jsonArray == null) return null;
		boolean[] val = new boolean[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			if (jsonArray.get(i).isString()!=null)
				if (jsonArray.get(i).isString().stringValue()!="false")
					val[i]=false;
				else
					val[i]=true;
			
		}
		return val;
	}
}
