package com.mostka.phprpc.rebind;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.ext.BadPropertyValueException;
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
import com.mostka.phprpc.client.PhpRpc;
import com.mostka.phprpc.client.PhpRpcRelocatePath;
import com.mostka.phprpc.phpGenerator.PhpServiceGenerator;
import com.mostka.phprpc.phpLinker.PhpServiceLinker;

public class PhpRpcServiceGenerator extends Generator{

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {

		JClassType classType;
		try {
			classType = context.getTypeOracle().getType(typeName);

			String serverName;
			try {
				serverName = context.getPropertyOracle().getConfigurationProperty("serverPath").getValues().get(0);
			} catch (BadPropertyValueException e1) {
				e1.printStackTrace();
				throw new UnableToCompleteException();
			}
			PhpServiceLinker servicePhplinker = PhpServiceLinker.create(serverName);
			String phpScriptPath = classType.getPackage().getName().replaceAll("\\.", "/");
			PhpRpcRelocatePath anotation = classType.getAnnotation(PhpRpcRelocatePath.class);
			if (anotation!=null)
				phpScriptPath = anotation.value().replaceAll("\\.", "/");
			int phpServiceCompiledName = servicePhplinker.addServiceLinker(classType.getName(),phpScriptPath + "/" + classType.getName(), classType.getMethods());

			SourceWriter src;
			try {
				src = generateMethod( classType, context, phpServiceCompiledName, logger);
			} catch (Exception e) {
				e.printStackTrace();
				throw new UnableToCompleteException();
			}
			if (src == null)return typeName + "__Async";
			src.commit(logger);
			PhpServiceGenerator phpserviceGenerator = new PhpServiceGenerator(logger, context, typeName);
			try {
				phpserviceGenerator.generate();
			} catch (IOException e) {
				e.printStackTrace();
				throw new UnableToCompleteException();
			}
			
			return typeName + "__Async";
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new UnableToCompleteException();
		}
	}
	private SourceWriter getSourceWriter(JClassType classType, GeneratorContext context, TreeLogger logger, String returnedObjectFullClassPath) {
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
		composer.addImport(returnedObjectFullClassPath);
		composer.addImport(returnedObjectFullClassPath+"PhpObjectGenerated");

		PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
		if (printWriter == null) {System.out.println("bad");
			return null;
		}
		return composer.createSourceWriter(context, printWriter);
	}
	
	private SourceWriter generateMethod( JClassType classType, GeneratorContext context, int phpServiceCompiledName, TreeLogger logger) throws Exception, BadPropertyValueException{
		JMethod[] methods = classType.getMethods();
		String serverName;
		try {
			serverName = context.getPropertyOracle().getConfigurationProperty("serverPath").getValues().get(0);
			serverName+= "/"+context.getPropertyOracle().getConfigurationProperty("phpIndexFile").getValues().get(0);
		} catch (BadPropertyValueException e1) {
			throw e1;
		}
		//PhpRpcRelocatePath anotation = classType.getAnnotation(PhpRpcRelocatePath.class);
		//String serverName = anotation.value(); //TODO replace server name with number fromlinker.ini
		for (int methodPos = 0; methodPos < methods.length; methodPos++) {
			int returlObjectLinkerPos = -1;
			
			JMethod method = methods[methodPos];
			String returnedObjectFullClassPath = "";

			JParameter[] parameters = method.getParameters();
			for (int i = 0; i < parameters.length; i++) {
				JParameter parameter = parameters[i];
				if (i < parameters.length-1){
				}else{
					if (!parameter.getType().getSimpleSourceName().equals("PhpRpcCallback")){
						System.err.println("Method must have last argument PhpRpcCallback<T>");
						throw new Exception("Method must have last argument PhpRpcCallback<T>");
					}else{
						String leaf = parameter.getType().getParameterizedQualifiedSourceName();
						returnedObjectFullClassPath = leaf.substring(40, leaf.length()-1);
					}
				}
			}
			
			
			SourceWriter src = getSourceWriter(classType, context, logger, returnedObjectFullClassPath);
			if (src==null){
				throw new Exception("sourcewriter is null");
			}
			src.println("@Override");	
			src.print("public void "+method.getName()+"(");	
			
			for (int i = 0; i < parameters.length; i++) {
				JParameter parameter = parameters[i];
				if (i < parameters.length-1){
					src.print(parameter.getType().getParameterizedQualifiedSourceName()+" "+parameter.getName());	
					src.print(", ");
				}else{
					if (!parameter.getType().getSimpleSourceName().equals("PhpRpcCallback")){
						System.err.println("Method must have last argument PhpRpcCallback<T>");
						throw new Exception("Method must have last argument PhpRpcCallback<T>");
					}else{
						String leaf = parameter.getType().getParameterizedQualifiedSourceName();
						returnedObjectFullClassPath = leaf.substring(40, leaf.length()-1);
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
				if (parameter.getType().getClass().getName().endsWith(".JArrayType")){
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
				if (parameter.getType().getClass().getName().endsWith(".JPrimitiveType")){
					src.println("		   params.set("+i+", new JSONString(String.valueOf("+parameter.getName()+")));");
				}else
				if (parameter.getType().getParameterizedQualifiedSourceName().equals("java.lang.String")){
					src.println("		   params.set("+i+", new JSONString("+parameter.getName()+"));");
				}else
				if (parameter.getType().getClass().getName().endsWith(".JRealClassType")){
					src.println("		if ("+parameter.getName()+"!=null)");
					src.println("			params.set("+i+", "+parameter.getName()+".toJSONObject());");
					src.println("		else");
					src.println("			params.set("+i+", null);");
				}else{
					System.err.println("Only real type and jsonObject arguments is accepted");
					throw new UnableToCompleteException();
				}
			}
			src.println("		request.put(\"A\", new JSONString(\""+phpServiceCompiledName+"\"));");
			src.println("		request.put(\"B\", new JSONString(\""+methodPos+"\"));");
			src.println("		request.put(\"C\", params);");
			src.println("		request.put(\"D\", new JSONString(\""+returlObjectLinkerPos+"\" ));");
			src.println("		"+returnedObjectFullClassPath+"PhpObjectGenerated instance = new "+returnedObjectFullClassPath+"PhpObjectGenerated();");
			
			src.println("		PhpRpc.callJSONRPCService(request,\n \""+serverName+"\",\n "+parameters[parameters.length-1].getName()+",\n instance);");/*TODO replace server name*/
			src.println("	}");
			return src;
		}
		return null;
	}
}
