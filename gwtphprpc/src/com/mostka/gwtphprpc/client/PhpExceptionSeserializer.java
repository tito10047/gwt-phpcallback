package com.mostka.gwtphprpc.client;

import com.mostka.gwtphprpc.shared.PhpException;
import com.mostka.gwtphprpc.shared.RpcException;
import com.mostka.serializer.java.Serializer;

public class PhpExceptionSeserializer{

	public static HasDeserializer<PhpException> serializer(){
		return new HasDeserializer<PhpException>() {
			public PhpException deserialize(Serializer serializer) throws RpcException {
				// TODO Auto-generated method stub
				return (PhpException) new RpcException("failed:TODO");
			}
		};
	}

}
