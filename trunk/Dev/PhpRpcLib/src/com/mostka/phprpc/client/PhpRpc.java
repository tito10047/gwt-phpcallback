package com.mostka.phprpc.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

public class PhpRpc{
	private static ArrayList<PhpRpcObject> calledMethods = new ArrayList<PhpRpcObject>();
	public static final <T extends PhpRpcObject>  void callJSONRPCService(final JSONObject jsonRequest, final String serverName, final PhpRpcCallback<T> phpRpcCallBack, T instance){
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, serverName);
		builder.setHeader("Content-Type", "application/json-rpc");
		final int calledPos = calledMethods.size();
		calledMethods.add(instance);
		jsonRequest.put("D", new JSONNumber(calledPos));
		try {
			@SuppressWarnings("unused")
			Request request = builder.sendRequest(jsonRequest.toString(), new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
				        try {
				        	JSONObject l_transferObject=  JSONParser.parseStrict(response.getText()).isObject();
				        	if (l_transferObject.get("error").isNull()==null){
				        		phpRpcCallBack.onThrowable(new PhpRpcException(null)); 
				        	}else{
				        		if (l_transferObject.get("result").isObject() != null){
				        			int pos = (int) l_transferObject.get("D").isNumber().doubleValue();
				        			@SuppressWarnings("unchecked")
									T instance = (T) calledMethods.get(pos);
				        			calledMethods.set(pos, null);
									instance.parseJSON(l_transferObject.get("result").toString());
				        			phpRpcCallBack.onSuccess(instance);
				        		}
				        	}
						} catch (Exception l_e) {
							l_e.printStackTrace();
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
			if (jsonArray.get(i).isNumber() != null){
				val[i]=(char) jsonArray.get(i).isNumber().doubleValue();
			}else if (jsonArray.get(i).isString() != null){
				val[i]=jsonArray.get(i).isString().stringValue().toCharArray()[0];
			}
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
