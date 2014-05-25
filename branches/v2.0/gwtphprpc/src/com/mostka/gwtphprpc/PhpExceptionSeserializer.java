package com.mostka.gwtphprpc;

import com.mostka.serializer.Serializer;

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
