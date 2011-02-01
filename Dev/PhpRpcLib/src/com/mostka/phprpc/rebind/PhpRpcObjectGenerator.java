package com.mostka.phprpc.rebind;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.mostka.phprpc.client.PhpRpc;
import com.mostka.phprpc.client.PhpRpcObject;
import com.mostka.phprpc.client.PhpRpcRelocatePath;
import com.mostka.phprpc.phpLinker.PhpLinker;


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

			} catch (BadPropertyValueException e1) {
				e1.printStackTrace();
				return null;
			}
			SourceWriter src = getSourceWriter(classType, context, logger);
			if (src == null)return typeName + "PhpObjectGenerated";
			
			String phpScriptPath = serverName+"/"+classType.getPackage().getName().replaceAll("\\.", "/");
			PhpRpcRelocatePath anotation = classType.getAnnotation(PhpRpcRelocatePath.class);
			if (anotation!=null)
				phpScriptPath = serverName+"/"+anotation.value().replaceAll("\\.", "/");
			BufferedWriter phpWriter = getPhpWriter(phpScriptPath,classType.getSimpleSourceName());
			String phpFullPath = phpScriptPath+"/"+classType.getSimpleSourceName();
			
			//createFields(src, classType); 
			//createObjectJSONPresenter(src, classType);
			generateMethodParseJSON(src, phpWriter, classType,newClassName,logger);
			String phpOBJECTS_VARS_METADATA = generateMethodParseJSONPresenter(src, phpWriter, classType,newClassName, typeName,logger);
			generateMethodToJSONObject(src, phpWriter, classType,newClassName,logger);
			src.commit(logger);

			try {
				phpWriter.write(phpOBJECTS_VARS_METADATA+"\n");
				phpWriter.write("}\n");
				phpWriter.write("?>\n");
				phpWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//String serverName = anotation.value();
			PhpLinker.create().addClassLinker(newClassName, phpFullPath);
			return typeName + "PhpObjectGenerated";
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	private void generateMethodParseJSON(SourceWriter src, BufferedWriter phpWriter, JClassType classType, String newClassName, TreeLogger logger) {
		src.println("	@Override");
		src.println("	public void parseJSON(String ___jsonString){");	
		src.println("		JSONValue ___jsonValue = JSONParser.parseStrict(___jsonString);");
		src.println("		JSONObject ___presenter = ((JSONObject) ___jsonValue);");
		src.println("		parseJSONPresenter(___presenter,this);");
		src.println("	}");	
	}

	private String generateMethodParseJSONPresenter(SourceWriter src, BufferedWriter phpWriter, JClassType classType, String newClassName, String typeName, TreeLogger logger) throws UnableToCompleteException {		
		src.println("	public static "+newClassName+" parseJSONPresenter(JSONObject jsonObject){");	
		src.println("		if (jsonObject == null) return null;");
		src.println("		"+newClassName+" ___createdObject = new "+typeName+"PhpObjectGenerated();");
		src.println("		parseJSONPresenter(jsonObject, ___createdObject);");
		src.println("		return ___createdObject;");
		src.println("	}");
		src.println("	public static void parseJSONPresenter(JSONObject ___presenter,"+newClassName+" ___destination){");	
		JField[] fields = classType.getFields();
		String phpOBJECTS_VARS_METADATA = "    protected $OBJECTS_VARS_METADATA=array(";
		String phpFields = "";
		Object object = getObjectInstance(typeName);
		boolean JSONArrayArrayInitialized = false;
		for (int i = 0; i < fields.length; i++){
			JField field = fields[i];
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JArrayType")){
				phpFields += createPhpProperityArray(field, object, logger)+"\n";
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
		  		}else{
		  			String classname = field.getType().getQualifiedSourceName();
		  			classname = classname.substring(0, classname.length()-2);
		  			if (getObjectInstance(classname) instanceof PhpRpcObject){
			  			
			  			phpOBJECTS_VARS_METADATA += "\""+field.getType().getSimpleSourceName()+"\"";
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
				phpFields += createPhpProperity(field, object, logger)+"\n";
				phpOBJECTS_VARS_METADATA += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
				src.println("		___destination."+field.getName()+" = ("+field.getType().getParameterizedQualifiedSourceName()+") parseJSONDouble(___presenter,\""+charses[i]+"\");");
			}else
			if (field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String")){
				phpFields += createPhpProperity(field, object, logger)+"\n";
				phpOBJECTS_VARS_METADATA += "\"String\"";
				src.println("		___destination."+field.getName()+" = parseJSONString(___presenter,\""+charses[i]+"\");");
			}else{
				String classname = field.getType().getQualifiedSourceName();
	  			if (getObjectInstance(classname) instanceof PhpRpcObject){
					phpFields += createPhpProperity(field, object, logger)+"\n";
					phpOBJECTS_VARS_METADATA += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
					src.println("		if (___presenter.containsKey(\""+field.getName()+"\")){");
					src.println("			GWT.create("+field.getType().getParameterizedQualifiedSourceName()+".class);");
					src.println("			___destination."+field.getName()+" = "+field.getType().getParameterizedQualifiedSourceName()+"PhpObjectGenerated.parseJSONPresenter(___presenter.get(\""+charses[i]+"\").isObject());");
					src.println("		}");
	  			}else{
					logger.log(TreeLogger.ERROR, "only object extends PhpRpcObject object are accepted. failed for object : "+classname);
					throw new UnableToCompleteException();
	  			}
			}
	  		if (i < fields.length-1)
	  			phpOBJECTS_VARS_METADATA+=",";
	  			
		}
		phpOBJECTS_VARS_METADATA+=");";
		//System.err.println(phpOBJECTS_VARS_METADATA+"\n\n"+phpFields);
		src.println("	}");
		return (phpOBJECTS_VARS_METADATA+"\n\n"+phpFields);
	}
	@SuppressWarnings("rawtypes")
	private Object getObjectInstance(String typeName){
		try {
			Class cl = Class.forName(typeName);
			try {
				@SuppressWarnings("unchecked")
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

	private String createPhpProperityArray(JField field, Object object, TreeLogger logger) throws UnableToCompleteException{
		String str = "    public $";
		str+=field.getName()+" ";
		@SuppressWarnings("rawtypes")
		Class c = object.getClass();
		try {
			Field fielda = c.getDeclaredField(field.getName());
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JArrayType")){
				try {
					if (field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String[]")){
						String[] fieldValue = (String[]) fielda.get(object);
						if (fieldValue==null) 
							str+="= null;";
						else{
							str+="= array(";
							for (int i=0;i<fieldValue.length;i++) {
								String string=fieldValue[i];
								str+=(string==null?"\"\"":"\""+string+"\"");
								if (i<fieldValue.length-1)str+=",";
							}
							str+=");";
						}
					}else if(field.getType().getParameterizedQualifiedSourceName().equals("int[]")){
						int[] fieldValue = (int[]) fielda.get(object);
						if (fieldValue==null) 
							str+="= null;";
						else{
							str+="= array(";
							for (int i=0;i<fieldValue.length;i++) {
								str+=fieldValue[i];
								if (i<fieldValue.length-1)str+=",";
							}
							str+=");";
						}
					}else if(field.getType().getParameterizedQualifiedSourceName().equals("double[]")){
						double[] fieldValue = (double[]) fielda.get(object);
						if (fieldValue==null) 
							str+="= null;";
						else{
							str+="= array(";
							for (int i=0;i<fieldValue.length;i++) {
								str+=fieldValue[i];
								if (i<fieldValue.length-1)str+=",";
							}
							str+=");";
						}
					}else if(field.getType().getParameterizedQualifiedSourceName().equals("char[]")){
						char[] fieldValue = (char[]) fielda.get(object);
						if (fieldValue==null) 
							str+="= null;";
						else{
							str+="= array(";
							for (int i=0;i<fieldValue.length;i++) {
								str+=(Character.isDefined(fieldValue[i])?"\"\"":"\""+fieldValue[i]+"\"");
								if (i<fieldValue.length-1)str+=",";
							}
							str+=");";
						}
					}else if(field.getType().getParameterizedQualifiedSourceName().equals("long[]")){
						long[] fieldValue = (long[]) fielda.get(object);
						if (fieldValue==null) 
							str+="= null;";
						else{
							str+="= array(";
							for (int i=0;i<fieldValue.length;i++) {
								str+=fieldValue[i];
								if (i<fieldValue.length-1)str+=",";
							}
							str+=");";
						}
					}else if(field.getType().getParameterizedQualifiedSourceName().equals("byte[]")){
						byte[] fieldValue = (byte[]) fielda.get(object);
						if (fieldValue==null) 
							str+="= null;";
						else{
							str+="= array(";
							for (int i=0;i<fieldValue.length;i++) {
								str+=fieldValue[i];
								if (i<fieldValue.length-1)str+=",";
							}
							str+=");";
						}
					}else if(field.getType().getParameterizedQualifiedSourceName().equals("short[]")){
						short[] fieldValue = (short[]) fielda.get(object);
						if (fieldValue==null) 
							str+="= null;";
						else{
							str+="= array(";
							for (int i=0;i<fieldValue.length;i++) {
								str+=fieldValue[i];
								if (i<fieldValue.length-1)str+=",";
							}
							str+=");";
						}
					}else if(field.getType().getParameterizedQualifiedSourceName().equals("boolean[]")){
						boolean[] fieldValue = (boolean[]) fielda.get(object);
						if (fieldValue==null) 
							str+="= null;";
						else{
							str+="= array(";
							for (int i=0;i<fieldValue.length;i++) {
								str+=(fieldValue[i]?"true":"false");
								if (i<fieldValue.length-1)str+=",";
							}
							str+=");";
						}
					}else{
			  			String classname = field.getType().getQualifiedSourceName();
			  			classname = classname.substring(0, classname.length()-2);
			  			if (getObjectInstance(classname) instanceof PhpRpcObject){
						PhpRpcObject[] fieldValue = (PhpRpcObject[]) fielda.get(object);
						str+=createPhpProperityDeclaredArray(fieldValue,field);
						}else{
							logger.log(TreeLogger.ERROR, "only object extends PhpRpcObject object are accepterd. failed for object : "+classname);
							 throw new UnableToCompleteException();
						}
			  		}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				int l,i=0;
				do {i++;
					l = (i*60)-str.length();
				} while (l<0);
				for(i=0;i<l;i++)
					str+=" ";
				str+="/*"+field.getType().getParameterizedQualifiedSourceName()+"*/";
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	private String createPhpProperity(JField field, Object object, TreeLogger logger) throws UnableToCompleteException{
		String str = "    public $";
		str+=field.getName()+" ";
		@SuppressWarnings("rawtypes")
		Class c = object.getClass();
		try {
			Field fielda = c.getDeclaredField(field.getName());
			try {
				if (field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String")){
					String fieldValue = (String) fielda.get(object);
					if (fieldValue==null) 
						str+="= null;";
					else{
						str+="= "+(fieldValue==null?"\"\"":"\""+fieldValue+"\"")+";";
					}
				}else if(field.getType().getParameterizedQualifiedSourceName().equals("int")){
					int fieldValue = fielda.getInt(object);
					str+="= "+fieldValue+";";
				}else if(field.getType().getParameterizedQualifiedSourceName().equals("double")){
					double fieldValue = fielda.getDouble(object);
					str+="= "+fieldValue+";";
				}else if(field.getType().getParameterizedQualifiedSourceName().equals("char")){
					char fieldValue = fielda.getChar(object);
					str+="= "+(Character.isDefined(fieldValue)?"\"\"":"\""+fieldValue+"\"")+";";
					
				}else if(field.getType().getParameterizedQualifiedSourceName().equals("long")){
					long fieldValue = fielda.getLong(object);
					str+="= "+fieldValue+";";
				}else if(field.getType().getParameterizedQualifiedSourceName().equals("byte")){
					byte fieldValue = fielda.getByte(object);
					str+="= "+fieldValue+";";
				}else if(field.getType().getParameterizedQualifiedSourceName().equals("short")){
					short fieldValue = fielda.getShort(object);
					str+="= "+fieldValue+";";
				}else if(field.getType().getParameterizedQualifiedSourceName().equals("boolean")){
					boolean fieldValue = fielda.getBoolean(object);
					str+="= "+(fieldValue?"true":"false")+";";
				}else{
		  			String classname = field.getType().getQualifiedSourceName();
		  			Object objects = getObjectInstance(classname);
		  			if (objects!=null)
		  			if (getObjectInstance(classname) instanceof PhpRpcObject){
						PhpRpcObject fieldValue = (PhpRpcObject) fielda.get(object);
						String type = field.getType().getSimpleSourceName();
						type = type.substring(0, type.length()-2);
						str+="= "+(fieldValue==null?"null":"new "+type+"()")+";";
					}else{
						logger.log(TreeLogger.ERROR, "only object extends PhpRpcObject object are accepterd. failed for object : "+classname);
						throw new UnableToCompleteException();
					}
		  		}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			int l,i=0;
			do {i++;
				l = (i*60)-str.length();
			} while (l<0);
			for(i=0;i<l;i++)
				str+=" ";
			str+="/*"+field.getType().getParameterizedQualifiedSourceName()+"*/";
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	private <R extends PhpRpcObject>String createPhpProperityDeclaredArray(R[] value, JField field){
		String str="";
		if (value==null) 
			str+="= null;";
		else{
			str+="= array(";
			String type = field.getType().getSimpleSourceName();
			type = type.substring(0, type.length()-2);
			for (int i=0;i<value.length;i++) {
				str+=(value[i]==null?"null":"new "+type+"()");
				if (i<value.length-1)str+=",";
			}
		}
		return str+");";
	}

	private void generateMethodToJSONObject(SourceWriter src, BufferedWriter phpWriter, JClassType classType, String newClassName, TreeLogger logger) throws UnableToCompleteException {
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
		  			if (getObjectInstance(classname) instanceof PhpRpcObject){
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
	  			if (getObjectInstance(classname) instanceof PhpRpcObject){
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
