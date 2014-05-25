package com.mostka.gwtphprpc;

import com.mostka.serializer.Serializer;

public interface HasDeserializer<T> {
	public T deserialize(Serializer serializer) throws RpcException;
}
