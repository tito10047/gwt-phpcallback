package com.mostka.phprpclibtest.client;

import com.mostka.phprpc.client.PhpRpcObject;
import com.mostka.phprpc.client.PhpRpcRelocatePath;


@PhpRpcRelocatePath("objects")
public class TestObject extends PhpRpcObject {

	public String[] aaa = {"ss","dd"};
	public int[] bbb = new int[2];
	public double[] ccc = {2,5,6,4};
	public char[] ddd = new char[2];
	public long[] eee = new long[2];
	public byte[] fff = new byte[2];
	public short[] ggg ;
	public boolean[] hhh = new boolean[2];
	public TestObject[] object = {null, null};
	public TestObject2[] object2 = {null, new TestObject2()};
	public String iii = "nie super";
	/*public double ddouble = 0.5;*/
	
	
}