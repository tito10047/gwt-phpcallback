package com.mostka.phprpc.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;


public abstract class PhpRpcObject {

	
  	public void parseJSON(String ___jsonString){}
	public JSONObject toJSONObject(){return null;};

  	protected static String parseJSONString(JSONObject ___presenter, String name){
  		if (___presenter.containsKey(name)){
  			if (___presenter.get(name).isString() == null) {
  				System.err.println("JSON parse error. properity "+name+" is no a string");
  		  		return null;
  			}
  	  		return ___presenter.get(name).isString().stringValue();
  		}
  		return null;
  	}
  	protected static String parseJSONArray(JSONObject ___presenter, String name){
  		if (___presenter.containsKey(name)){
  			if (___presenter.get(name).isArray() == null) {
  				System.err.println("JSON parse error. properity "+name+" is no a array");
  		  		return null;
  			}
  			
  	  		return ___presenter.get(name).isString().stringValue();
  		}
  		return null;
  	}
  	protected static double parseJSONDouble(JSONObject ___presenter, String name){
  		if (___presenter.containsKey(name)){
  			if (___presenter.get("bbb").isNumber() == null) {
  				System.err.println("JSON parse error. properity bbb is no a number");
  				return 0;
  			}
  			return ( ___presenter.get("bbb").isNumber().doubleValue());
  		}
		return 0;
	}
  	protected static JSONArray parseJSONArrayValues(JSONObject ___presenter, String name){
  		if (___presenter.containsKey(name)){
  			if (___presenter.get(name).isArray() == null) {
  				System.err.println("JSON parse error. properity "+name+" is no a array");
  		  		return null;
  			}
  	  		return ___presenter.get(name).isArray();
  		}
  		return null;
  	}
}
