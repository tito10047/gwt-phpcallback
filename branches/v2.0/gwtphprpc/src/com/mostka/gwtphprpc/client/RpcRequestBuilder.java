package com.mostka.gwtphprpc.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.*;
import com.mostka.gwtphprpc.shared.PhpException;
import com.mostka.gwtphprpc.shared.RpcException;
import com.mostka.serializer.java.Serializer;
import com.mostka.serializer.java.Serializer.BadPrimitiveTypeException;

public class RpcRequestBuilder extends RequestBuilder{
	
	private String url;
	private Serializer serializer = new Serializer();

	public RpcRequestBuilder(String url, int className, int method) {
		super(POST, url);
        this.url = url;

        setHeader("Content-Type", "application/byte-rpc");
		setHeader("Accept", "application/byte-rpc");
	}
	
	public <T>void sendRequest(final com.google.gwt.user.client.rpc.AsyncCallback<T> callback, final HasDeserializer<T> deserializer ){
		try {
			this.sendRequest(serializer.getBuffer(), new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Serializer serializer = new Serializer(response.getText());
						try {
                            callback.onSuccess(deserializer.deserialize(serializer));
						} catch (RpcException e) {
                            e.printStackTrace();
                        } catch (Throwable e) {
                            callback.onFailure(e);
                        }
                    }else{
			        	callback.onFailure(new RpcException("ERROR: Couldn't retrieve response (" + response.getStatusText()+ ")"));
					}
				}
				public void onError(Request request, Throwable exception) {
		        	callback.onFailure(new RpcException("ERROR: Couldn't retrieve response"));
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}
	
	public static void serUrl(String url){
	}
	
	public Serializer getSerializer(){
		return serializer;
	}

}
