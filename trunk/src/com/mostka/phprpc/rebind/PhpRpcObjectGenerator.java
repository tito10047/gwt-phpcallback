package com.mostka.phprpc.rebind;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.google.gwt.core.ext.BadPropertyValueException;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.mostka.phprpc.client.PhpRpc;


public class PhpRpcObjectGenerator extends Generator {
	private String[] charses = { "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","r","s","t","u","v","w" };
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		JClassType classType;
		try {
			classType = context.getTypeOracle().getType(typeName);
			

			String newClassName = classType.getPackage().getName()+"."+classType.getSimpleSourceName() + "PhpObjectGenerated";
			
			String serverName;
			try {
				serverName = context.getPropertyOracle().getConfigurationProperty("serverPath").getValues().get(0);
				System.out.println(serverName);
			} catch (BadPropertyValueException e1) {
				e1.printStackTrace();
				return null;
			}
			SourceWriter src = getSourceWriter(classType, context, logger);
			if (src == null)return typeName + "PhpObjectGenerated";
			
			String phpScriptPath = serverName+"/"+classType.getPackage().getName().replaceAll("\\.", "/");
			BufferedWriter phpWriter = getPhpWriter(phpScriptPath,classType.getSimpleSourceName());
			
			//createFields(src, classType); 
			//createObjectJSONPresenter(src, classType);
			generateMethodParseJSON(src, phpWriter, classType,newClassName);
			generateMethodParseJSONPresenter(src, phpWriter, classType,newClassName, typeName);
			generateMethodToJSONObject(src, phpWriter, classType,newClassName);
			src.commit(logger);
			
			return typeName + "PhpObjectGenerated";
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*private void createFields(SourceWriter src, JClassType classType) {
		JField[] fields = classType.getFields();
		for (JField field : fields) {
			src.println("	"+field+";");
		}
	}*/

	private void createObjectJSONPresenter(SourceWriter src, JClassType classType) {
		src.println("	public static class JSONPresenter extends JavaScriptObject{ protected JSONPresenter() {};");	
		JField[] fields = classType.getFields();
		for (JField field : fields) {
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JPrimitiveType")){
				src.println("		public final native "+field.getType().getParameterizedQualifiedSourceName()+" ___"+field.getName()+"()/*-{return this."+field.getName()+";}-*/;");
			}else
			if (field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String")){
				src.println("		public final native java.lang.String ___"+field.getName()+"()/*-{return this."+field.getName()+";}-*/;");
			}else
			if(field.getEnclosingType().getSuperclass().getParameterizedQualifiedSourceName().equals("com.mostka.phprpc.client.PhpRpcObject")){
				src.println("		public final native "+field.getType().getParameterizedQualifiedSourceName()+"PhpObjectGenerated.JSONPresenter ___"+field.getName()+"()/*-{return this."+field.getName()+";}-*/;");
			}else{
				System.out.println(field.getEnclosingType().getSuperclass().getParameterizedQualifiedSourceName());
				new Exception("only object extends PhpRpcObject object are accepterd").printStackTrace();
			}
		}
		src.println("	}");
	}
	
	private void generateMethodParseJSON(SourceWriter src, BufferedWriter phpWriter, JClassType classType, String newClassName) {
		src.println("	@Override");
		src.println("	public void parseJSON(String ___jsonString){");	
		src.println("		JSONValue ___jsonValue = JSONParser.parseStrict(___jsonString);");
		src.println("		JSONObject ___presenter = ((JSONObject) ___jsonValue);");
		src.println("		parseJSONPresenter(___presenter,this);");
		src.println("	}");	
	}

	private void generateMethodParseJSONPresenter(SourceWriter src, BufferedWriter phpWriter, JClassType classType, String newClassName, String typeName) {	
		//src.println("	@Override");	
		src.println("	public static "+newClassName+" parseJSONPresenter(JSONObject jsonObject){");	
		src.println("		if (jsonObject == null) return null;");
		src.println("		"+newClassName+" ___createdObject = new "+newClassName+"();");
		src.println("		parseJSONPresenter(jsonObject, ___createdObject);");
		src.println("		return ___createdObject;");
		src.println("	}");
		//System.out.println(typeName);
		//src.println("	@Override");	
		src.println("	public static void parseJSONPresenter(JSONObject ___presenter,"+newClassName+" ___destination){");	
		JField[] fields = classType.getFields();
		String phpOBJECTS_VARS_METADATA = "    protected $OBJECTS_VARS_METADATA=array(";
		String phpFields = "";
		Object object = getObjectInstance(typeName);
		for (int i = 0; i < fields.length; i++){
			JField field = fields[i];
			phpFields += createPhpProperity(field, object)+"\n";
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JArrayType")){
		  		if (field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String[]")){
		  			phpOBJECTS_VARS_METADATA += "\"String[]\"";
					src.println("		___destination."+field.getName()+" = PhpRpc.toJSONString(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("int[]")){
		  			phpOBJECTS_VARS_METADATA += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONint(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("double[]")){
		  			phpOBJECTS_VARS_METADATA += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONdouble(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("char[]")){
		  			phpOBJECTS_VARS_METADATA += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONchar(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("long[]")){
		  			phpOBJECTS_VARS_METADATA += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONlong(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("byte[]")){
		  			phpOBJECTS_VARS_METADATA += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONbyte(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("short[]")){
		  			phpOBJECTS_VARS_METADATA += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONshort(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("boolean[]")){
		  			phpOBJECTS_VARS_METADATA += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  			src.println("		___destination."+field.getName()+" = PhpRpc.toJSONboolean(parseJSONArrayValues(___presenter,\""+charses[i]+"\"));");
		  		}else if(field.getEnclosingType().getSuperclass().getParameterizedQualifiedSourceName().equals("com.mostka.phprpc.client.PhpRpcObject")){
		  			phpOBJECTS_VARS_METADATA += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  			String objectName = field.getType().getParameterizedQualifiedSourceName();
		  			objectName = objectName.substring(0, objectName.length()-2)+"PhpObjectGenerated";
		  			src.println("		JSONArray array = parseJSONArrayValues(___presenter,\""+charses[i]+"\");");
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
		  		}else{
		  			System.err.println("array type : "+field.getType().getParameterizedQualifiedSourceName()+" is not accepted");
		  		}
			}else
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JPrimitiveType")){
				phpOBJECTS_VARS_METADATA += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
				src.println("		___destination."+field.getName()+" = ("+field.getType().getParameterizedQualifiedSourceName()+") parseJSONDouble(___presenter,\""+charses[i]+"\");");
			}else
			if (field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String")){
				phpOBJECTS_VARS_METADATA += "\"String\"";
				src.println("		___destination."+field.getName()+" = parseJSONString(___presenter,\""+charses[i]+"\");");
			}else{
				phpOBJECTS_VARS_METADATA += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
				src.println("		if (___presenter.containsKey(\""+field.getName()+"\"))");
				src.println("		___destination."+field.getName()+" = "+field.getType().getParameterizedQualifiedSourceName()+"PhpObjectGenerated.parseJSONPresenter(___presenter.get(\""+charses[i]+"\").isObject());");
			}
	  		if (i < fields.length-1)
	  			phpOBJECTS_VARS_METADATA+=",";
	  		else
	  			phpOBJECTS_VARS_METADATA+=");";
		}
		src.println("	}");
	}
	private Object getObjectInstance(String typeName){
		try {
			Class cl = Class.forName(typeName);
			try {
				java.lang.reflect.Constructor co = cl.getConstructor();
				try {
					return co.newInstance();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String createPhpProperity(JField field, Object object){
		String str = "    public $";
		str+=field.getName()+" ";
		System.out.println("----");
		/*Class fieldTypeClass;
		try {
			fieldTypeClass = Class.forName(field.getType().getQualifiedSourceName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}*/
		
		Class c = object.getClass();
		String[] fieldValue;
		try {
			Field fielda = c.getField("aaa");
			try {
				fieldValue = (String[]) fielda.get(object);
				System.out.println(fieldValue[0]);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		
		
		if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JArrayType")){
			
		}else{
			
		}
		
		
		return str;
	}

	private void generateMethodToJSONObject(SourceWriter src, BufferedWriter phpWriter, JClassType classType, String newClassName) {
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
				}else
				if(field.getEnclosingType().getSuperclass().getParameterizedQualifiedSourceName().equals("com.mostka.phprpc.client.PhpRpcObject")){
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
				}else
					System.err.println("array type : "+field.getType().getParameterizedQualifiedSourceName()+" is not accepted 2");
				
					
			}else
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JPrimitiveType")){
				src.println("		___object.put(\""+charses[i]+"\", new JSONNumber("+field.getName()+"));");
			}else
			if (field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String")){
				src.println("		if ("+field.getName()+"!=null)");
				src.println("		___object.put(\""+charses[i]+"\",new JSONString("+field.getName()+"));");
			}else
			if(field.getEnclosingType().getSuperclass().getParameterizedQualifiedSourceName().equals("com.mostka.phprpc.client.PhpRpcObject")){
				src.println("		if ("+field.getName()+"!=null)");
				src.println("		___object.put(\""+charses[i]+"\", "+field.getName()+".toJSONObject());");
			}else{
				System.out.println(field.getEnclosingType().getSuperclass().getParameterizedQualifiedSourceName());
				new Exception("only object extends PhpRpcObject object are accepterd").printStackTrace();
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
		
		composer.setSuperclass(packageName+"."+classType.getSimpleSourceName());
		
		PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
		if (printWriter == null) {System.out.println("bad");
			return null;
		}
		return composer.createSourceWriter(context, printWriter);
	}
	private BufferedWriter getPhpWriter(String phpScriptPath, String className){
		if (!(new File(phpScriptPath)).exists())
			if (!(new File(phpScriptPath)).mkdirs())
				System.err.print("cant create script directory : "+phpScriptPath);
			else
				System.err.print("created directory : "+phpScriptPath);
		
		File f;
	    f=new File(phpScriptPath+"/"+className+".class.php");
	    if(f.exists()){
	    	f.delete();
	    }
	    BufferedWriter file;
		try {
			FileWriter fstream = new FileWriter(phpScriptPath+"/"+className+".class.php");
	        file = new BufferedWriter(fstream);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			file.write("<?php\n");
			file.write("class "+className+" extends GwtRpcObject{\n\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return file;
	}
}
