package com.mostka.phprpc.rebind;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.mostka.phprpc.client.PhpRpc;
import com.mostka.phprpc.client.PhpRpcObject;
import com.mostka.phprpc.phpGenerator.PhpObjectGenerator;


public class PhpRpcObjectGenerator extends Generator {
	private String[] charses = { "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","r","s","t","u","v","w" };
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		JClassType classType;
		try {
			classType = context.getTypeOracle().getType(typeName);
			String newClassName = classType.getPackage().getName()+"."+classType.getSimpleSourceName() + "PhpObjectGenerated";
			
			SourceWriter src = getSourceWriter(classType, context, logger);
			if (src == null)return typeName + "PhpObjectGenerated";
			
			generateMethodParseJSON(src, classType,newClassName,logger);
			generateMethodParseJSONPresenter(src, classType,newClassName, typeName,logger);
			generateMethodToJSONObject(src, classType,newClassName,logger);
			src.commit(logger);
			PhpObjectGenerator phpObjectGenerator = new PhpObjectGenerator(logger, context, typeName);
			try {
				phpObjectGenerator.generate();
			} catch (IOException e) {
				e.printStackTrace();
				throw new UnableToCompleteException();
			}
			phpObjectGenerator=null;
			
			return typeName + "PhpObjectGenerated";
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new UnableToCompleteException();
		}
	}
	private void generateMethodParseJSON(SourceWriter src, JClassType classType, String newClassName, TreeLogger logger) {
		src.println("	@Override");
		src.println("	public void parseJSON(String ___jsonString){");	
		src.println("		JSONValue ___jsonValue = JSONParser.parseStrict(___jsonString);");
		src.println("		JSONObject ___presenter = ((JSONObject) ___jsonValue);");
		src.println("		parseJSONPresenter(___presenter,this);");
		src.println("	}");	
	}

	private void generateMethodParseJSONPresenter(SourceWriter src, JClassType classType, String newClassName, String typeName, TreeLogger logger) throws UnableToCompleteException {		
		src.println("	public static "+newClassName+" parseJSONPresenter(JSONObject jsonObject){");	
		src.println("		if (jsonObject == null) return null;");
		src.println("		"+newClassName+" ___createdObject = new "+typeName+"PhpObjectGenerated();");
		src.println("		parseJSONPresenter(jsonObject, ___createdObject);");
		src.println("		return ___createdObject;");
		src.println("	}");
		src.println("	public static void parseJSONPresenter(JSONObject ___presenter,"+newClassName+" ___destination){");	
		JField[] fields = classType.getFields();
		boolean JSONArrayArrayInitialized = false;
		for (int i = 0; i < fields.length; i++){
			JField field = fields[i];
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JArrayType")){
				if (field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String[]")){
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONString(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("int[]")){
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONint(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("double[]")){
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONdouble(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("char[]")){
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONchar(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("long[]")){
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONlong(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("byte[]")){
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONbyte(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("short[]")){
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONshort(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("boolean[]")){
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONboolean(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else{
		  			String classname = field.getType().getQualifiedSourceName();
		  			classname = classname.substring(0, classname.length()-2);
		  			if (PhpObjectGenerator.getObjectInstance(classname) instanceof PhpRpcObject){
			  			
			  			String objectName = field.getType().getParameterizedQualifiedSourceName();
			  			objectName = objectName.substring(0, objectName.length()-2)+"PhpObjectGenerated";
			  			src.println("		"+(JSONArrayArrayInitialized?"":"JSONArray ")+"array = parseJSONArrayValues(___presenter,\""+charses[i]+"\");");
			  			src.println("		if (array != null){");
			  			src.println("			"+objectName+"[] objectArray = new "+objectName+"[array.size()];");
			  			src.println("			for (int i = 0; i < array.size(); i++) {");
			  			src.println("				if (array.get(i).isObject() != null)");
			  			src.println("					objectArray[i] = "+objectName+".parseJSONPresenter(array.get(i).isObject());");
			  			src.println("				else");
			  			src.println("					objectArray[i] = null;");
			  			src.println("			}");
			  			src.println("			___destination."+field.getName()+" = objectArray;");
			  			src.println("		}");
			  			JSONArrayArrayInitialized = true;
			  		}else{
						logger.log(TreeLogger.ERROR, "only object extends PhpRpcObject object are accepterd. failed for object : "+classname);
						throw new UnableToCompleteException();
			  		}
		  		}
			}else
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JPrimitiveType")){
				src.println("		___destination."+field.getName()+" = ("+field.getType().getParameterizedQualifiedSourceName()+") parseJSONDouble(___presenter,\""+charses[i]+"\");");
			}else
			if (field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String")){
				src.println("		___destination."+field.getName()+" = parseJSONString(___presenter,\""+charses[i]+"\");");
			}else{
				String classname = field.getType().getQualifiedSourceName();
	  			if (PhpObjectGenerator.getObjectInstance(classname) instanceof PhpRpcObject){
					src.println("		if (___presenter.containsKey(\""+field.getName()+"\")){");
					src.println("			GWT.create("+field.getType().getParameterizedQualifiedSourceName()+".class);");
					src.println("			___destination."+field.getName()+" = "+field.getType().getParameterizedQualifiedSourceName()+"PhpObjectGenerated.parseJSONPresenter(___presenter.get(\""+charses[i]+"\").isObject());");
					src.println("		}");
	  			}else{
					logger.log(TreeLogger.ERROR, "only object extends PhpRpcObject object are accepted. failed for object : "+classname);
					throw new UnableToCompleteException();
	  			}
			}
	  			
		}
		//System.err.println(phpOBJECTS_VARS_METADATA+"\n\n"+phpFields);
		src.println("	}");
	}



	private void generateMethodToJSONObject(SourceWriter src, JClassType classType, String newClassName, TreeLogger logger) throws UnableToCompleteException {
		src.println("public JSONObject toJSONObject(){");
		src.println("		JSONObject ___object = new JSONObject();");
		JField[] fields = classType.getFields();
		for (int i = 0; i < fields.length; i++){
			JField field = fields[i];
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JArrayType")){
				if ((field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String[]")) ||
						(field.getType().getParameterizedQualifiedSourceName().equals("int[]")) ||
						(field.getType().getParameterizedQualifiedSourceName().equals("double[]")) ||
						(field.getType().getParameterizedQualifiedSourceName().equals("char[]")) ||
						(field.getType().getParameterizedQualifiedSourceName().equals("long[]")) ||
						(field.getType().getParameterizedQualifiedSourceName().equals("byte[]")) ||
						(field.getType().getParameterizedQualifiedSourceName().equals("short[]")) ||
						(field.getType().getParameterizedQualifiedSourceName().equals("boolean[]"))){
					src.println("		___object.put(\""+charses[i]+"\",PhpRpc.toJSONArray("+field.getName()+"));");
				}else{

					String classname = field.getType().getQualifiedSourceName();
		  			classname = classname.substring(0, classname.length()-2);
		  			if (PhpObjectGenerator.getObjectInstance(classname) instanceof PhpRpcObject){
						src.println("		if ("+field.getName()+" != null){");
						src.println("			JSONArray jsonArray = new JSONArray();");
						src.println("			for (int i = 0; i < "+field.getName()+".length; i++) {");
						src.println("				if ("+field.getName()+"[i]!=null)");
						src.println("					jsonArray.set(i, "+field.getName()+"[i].toJSONObject());");
						src.println("				else");
						src.println("					jsonArray.set(i, null);");
						src.println("			}");
						src.println("			___object.put(\""+charses[i]+"\",jsonArray);");
						src.println("		}");
					}else{
						logger.log(TreeLogger.ERROR, "only object extends PhpRpcObject object are accepterd. failed for object : "+classname);
		  				throw new UnableToCompleteException();
					}
				}
					
			}else
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JPrimitiveType")){
				src.println("		___object.put(\""+charses[i]+"\", new JSONNumber("+field.getName()+"));");
			}else
			if (field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String")){
				src.println("		if ("+field.getName()+"!=null)");
				src.println("		___object.put(\""+charses[i]+"\",new JSONString("+field.getName()+"));");
			}else{
				String classname = field.getType().getQualifiedSourceName();
	  			if (PhpObjectGenerator.getObjectInstance(classname) instanceof PhpRpcObject){
					src.println("		if ("+field.getName()+"!=null)");
					src.println("		___object.put(\""+charses[i]+"\", "+field.getName()+".toJSONObject());");
				}else{
					logger.log(TreeLogger.ERROR, "only object extends PhpRpcObject object are accepterd. failed for object : "+classname);
	  				throw new UnableToCompleteException();
				}
			}
		}
		src.println("		return ___object;");
		src.println("	}");
	}


	private SourceWriter getSourceWriter(JClassType classType, GeneratorContext context, TreeLogger logger) {
		String packageName = classType.getPackage().getName();
		String simpleName = classType.getSimpleSourceName() + "PhpObjectGenerated";
		
		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

		composer.addImport(PhpRpc.class.getCanonicalName());
		composer.addImport(JSONArray.class.getCanonicalName());
		composer.addImport(JSONObject.class.getCanonicalName());
		composer.addImport(JSONNumber.class.getCanonicalName());
		composer.addImport(JSONParser.class.getCanonicalName());
		composer.addImport(JSONString.class.getCanonicalName());
		composer.addImport(JSONValue.class.getCanonicalName());
		composer.addImport(GWT.class.getCanonicalName());
		
		composer.setSuperclass(packageName+"."+classType.getSimpleSourceName());
		
		PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
		if (printWriter == null) {System.out.println("bad");
			return null;
		}
		return composer.createSourceWriter(context, printWriter);
	}
}
