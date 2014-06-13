package com.mostka.gwtphprpc.client;

import com.mostka.gwtphprpc.shared.PhpException;
import com.mostka.gwtphprpc.shared.RpcException;
import com.mostka.serializer.java.Serializer;

public interface HasDeserializer<T> {
	public T deserialize(Serializer serializer) throws Throwable;
}
