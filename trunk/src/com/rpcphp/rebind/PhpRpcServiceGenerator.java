package com.rpcphp.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.rpcphp.client.PhpRpc;
import com.rpcphp.client.PhpRpcServiceRelativePath;

public class PhpRpcServiceGenerator extends Generator{

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {

		JClassType classType;
		try {
			classType = context.getTypeOracle().getType(typeName);
			SourceWriter src = getSourceWriter(classType, context, logger);
			try {
				generateMethod(src, classType);
			} catch (Exception e) {
				e.printStackTrace();
			}
			src.commit(logger);
			return typeName + "__Async";
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	private SourceWriter getSourceWriter(JClassType classType, GeneratorContext context, TreeLogger logger) {
		String packageName = classType.getPackage().getName();
		String simpleName = classType.getSimpleSourceName() + "__Async";
		
		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
		
		composer.setSuperclass(packageName+"."+classType.getSimpleSourceName());

		composer.addImport(JavaScriptObject.class.getCanonicalName());
		composer.addImport(JSONArray.class.getCanonicalName());
		composer.addImport(JSONObject.class.getCanonicalName());
		composer.addImport(JSONString.class.getCanonicalName());
		composer.addImport(AsyncCallback.class.getCanonicalName());
		composer.addImport(PhpRpc.class.getCanonicalName());
		composer.addImport(Window.class.getCanonicalName());

		PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
		if (printWriter == null) {System.out.println("bad");
			return null;
		}
		return composer.createSourceWriter(context, printWriter);
	}
	
	private void generateMethod(SourceWriter src, JClassType classType) throws Exception{
		JMethod[] methods = classType.getMethods();
		PhpRpcServiceRelativePath anotation = classType.getAnnotation(PhpRpcServiceRelativePath.class);
		String serverName = anotation.value();
		for (JMethod method : methods) {
			src.println("@Override");	
			src.print("public void "+method.getName()+"(");	
			
			JParameter[] parameters = method.getParameters();
			for (int i = 0; i < parameters.length; i++) {
				JParameter parameter = parameters[i];
				if (i < parameters.length-1){
					src.print(parameter.getType().getParameterizedQualifiedSourceName()+" "+parameter.getName());	
					src.print(", ");
				}else{
					if (!parameter.getType().getSimpleSourceName().equals("PhpRpcCallback")){
						System.err.println("Method must have last argument PhpRpcCallback<T>");
						throw new Exception("Method must have last argument PhpRpcCallback<T>");
					}
					src.print(parameter.getType().getParameterizedQualifiedSourceName()+" "+parameter.getName());
				}
			}
			src.println("){");
			src.println("  		JSONObject request = new JSONObject();");
			src.println("  		JSONArray params = new JSONArray();");
			for (int i = 0; i < parameters.length; i++) {
				JParameter parameter = parameters[i];
				if (i == parameters.length-1) break;
				if (parameter.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JArrayType")){
					if ((parameter.getType().getParameterizedQualifiedSourceName().equals("java.lang.String[]")) ||
							(parameter.getType().getParameterizedQualifiedSourceName().equals("int[]")) ||
							(parameter.getType().getParameterizedQualifiedSourceName().equals("double[]")) ||
							(parameter.getType().getParameterizedQualifiedSourceName().equals("char[]")) ||
							(parameter.getType().getParameterizedQualifiedSourceName().equals("long[]")) ||
							(parameter.getType().getParameterizedQualifiedSourceName().equals("byte[]")) ||
							(parameter.getType().getParameterizedQualifiedSourceName().equals("short[]")) ||
							(parameter.getType().getParameterizedQualifiedSourceName().equals("boolean[]")))
						src.println("		params.set("+i+",PhpRpc.toJSONArray("+parameter.getName()+"));");
						else
						System.err.println("array type : "+parameter.getType().getParameterizedQualifiedSourceName()+" is not accepted");
				}else
				if (parameter.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JPrimitiveType")){
					src.println("		   params.set("+i+", new JSONString(String.valueOf("+parameter.getName()+")));");
				}else
				if (parameter.getType().getParameterizedQualifiedSourceName().equals("java.lang.String")){
					src.println("		   params.set("+i+", new JSONString("+parameter.getName()+"));");
				}else
				if (parameter.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JRealClassType")){
					src.println("		if ("+parameter.getName()+"!=null)");
					src.println("			params.set("+i+", "+parameter.getName()+".toJSONObject());");
					src.println("		else");
					src.println("			params.set("+i+", null);");
				}else{
					System.err.println("Only real type and jsonObject arguments is accepted");
					throw new UnableToCompleteException();
				}
			}
			src.println("		request.put(\"class\", new JSONString(\""+classType.getSimpleSourceName()+"\"));");
			src.println("		request.put(\"method\", new JSONString(\""+method.getName()+"\"));");
			src.println("		request.put(\"parms\", params);");
			src.println("		PhpRpc.callJSONRPCService(request.toString(), \""+serverName+"\", "+parameters[parameters.length-1].getName()+");");/*TODO replace server name*/
			src.println("	}");
		}
	}
}
