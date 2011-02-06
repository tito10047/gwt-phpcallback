package com.mostka.phprpc.phpGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.mostka.phprpc.client.PhpRpcObject;
import com.mostka.phprpc.client.PhpRpcRelocatePath;

public class PhpObjectGenerator {
	private TreeLogger logger;
	GeneratorContext context;
	String typeName;
	JClassType classType;

	String phpObject = "";
	String phpObjectConstructor = "";
	
	private String className;
	private String serverName;
	private String phpScriptPath;
	
	
	public PhpObjectGenerator(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException{
		this.logger = logger;
		this.context=context;
		this.typeName=typeName;
	}
	
	public void generate() throws UnableToCompleteException, NotFoundException, IOException{
		classType = context.getTypeOracle().getType(typeName);
		//String newClassName = classType.getPackage().getName()+"."+classType.getSimpleSourceName()
		generateName();
		//PhpLinker.create().addClassLinker(newClassName, phpFullPath);
		phpObject+="class "+className+" extends GwtRpcObjectABS{\n\n";
		phpObject+=generateFieldsMetadata();
		phpObject+=generateFieldsDefinitions();
		phpObject+="    function __construct(){\n";
		phpObject+=			phpObjectConstructor;
		phpObject+="    }\n";
		phpObject+="}";
		/*System.out.println("--------------------------------------------");
		System.out.println(phpObject);
		System.out.println();*/
		writeToFile();

	}
	private void writeToFile() throws IOException, UnableToCompleteException{
		BufferedWriter writer = getPhpWriter(phpScriptPath,className);
			writer.write("<?php\n");
			writer.write(phpObject);
			writer.write("\n?>");
			writer.close();
	}
	private void generateName() throws UnableToCompleteException{
		className = classType.getSimpleSourceName();
		try {
			serverName = context.getPropertyOracle().getConfigurationProperty("serverPath").getValues().get(0);
		} catch (BadPropertyValueException e1) {
			e1.printStackTrace();
			throw new UnableToCompleteException();
		}
		phpScriptPath = serverName+"/"+classType.getPackage().getName().replaceAll("\\.", "/");
		PhpRpcRelocatePath anotation = classType.getAnnotation(PhpRpcRelocatePath.class);
		if (anotation!=null)
			phpScriptPath = serverName+"/"+anotation.value().replaceAll("\\.", "/");
	}
	private String generateFieldsMetadata() throws UnableToCompleteException{
		String fieldsMetadata = "    protected $OBJECTS_VARS_METADATA=array(";
		JField[] fields = classType.getFields();
		for (int i = 0; i < fields.length; i++){
			JField field = fields[i];
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JArrayType")){
		  		if (field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String[]")){
		  			fieldsMetadata += "\"String[]\"";
				}else if(field.getType().getParameterizedQualifiedSourceName().equals("int[]")){
					fieldsMetadata += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("double[]")){
		  			fieldsMetadata += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("char[]")){
		  			fieldsMetadata += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("long[]")){
		  			fieldsMetadata += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("byte[]")){
		  			fieldsMetadata += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("short[]")){
		  			fieldsMetadata += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  		}else if(field.getType().getParameterizedQualifiedSourceName().equals("boolean[]")){
		  			fieldsMetadata += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
		  		}else{
		  			String classname = field.getType().getQualifiedSourceName();
		  			classname = classname.substring(0, classname.length()-2);
		  			if (getObjectInstance(classname) instanceof PhpRpcObject){
		  				String path=getPhpClassPath(field.getType());
		  				fieldsMetadata+="\""+path+"[]\"";
			  		}else{
						logger.log(TreeLogger.ERROR, "only object extends PhpRpcObject object are accepterd. failed for object : "+classname);
						throw new UnableToCompleteException();
			  		}
		  		}
			}else
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JPrimitiveType")){
				fieldsMetadata += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
			}else
			if (field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String")){
				fieldsMetadata += "\"String\"";
			}else{
				String classname = field.getType().getQualifiedSourceName();
	  			if (getObjectInstance(classname) instanceof PhpRpcObject){
					fieldsMetadata += "\""+field.getType().getParameterizedQualifiedSourceName()+"\"";
				}else{
					logger.log(TreeLogger.ERROR, "only object extends PhpRpcObject object are accepted. failed for object : "+classname);
					throw new UnableToCompleteException();
	  			}
			}
	  		if (i < fields.length-1)
	  			fieldsMetadata+=",";
	  			
		}
		fieldsMetadata+=");";
		return (fieldsMetadata+"\n\n");
	}
	private String generateFieldsDefinitions() throws UnableToCompleteException{
		String fieldsDefinitions = "";
		JField[] fields = classType.getFields();
		Object object = getObjectInstance(typeName);
		for (int i = 0; i < fields.length; i++){
			JField field = fields[i];
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JArrayType")){
				fieldsDefinitions += createFieldArray(field, object)+"\n";
			}else
			if (field.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JPrimitiveType")){
				fieldsDefinitions += createField(field, object)+"\n";
			}else
			if (field.getType().getParameterizedQualifiedSourceName().equals("java.lang.String")){
				fieldsDefinitions += createField(field, object)+"\n";
			}else{
				String classname = field.getType().getQualifiedSourceName();
	  			if (getObjectInstance(classname) instanceof PhpRpcObject){
	  				fieldsDefinitions += createField(field, object)+"\n";
				}else{
					logger.log(TreeLogger.ERROR, "only object extends PhpRpcObject object are accepted. failed for object : "+classname);
					throw new UnableToCompleteException();
	  			}
			}
		}
		return (fieldsDefinitions+"\n");
		
	}

	private String createFieldArray(JField field, Object object) throws UnableToCompleteException{
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
						str+=createFieldDeclaredArray(fieldValue,field);
						}else{
							logger.log(TreeLogger.ERROR, "only object extends PhpRpcObject object are accepterd. failed for object : "+classname);
							 throw new UnableToCompleteException();
						}
			  		}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
	  				throw new UnableToCompleteException();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
	  				throw new UnableToCompleteException();
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
			throw new UnableToCompleteException();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			throw new UnableToCompleteException();
		}
		return str;
	}
	

	
	private String createField(JField field, Object object) throws UnableToCompleteException{
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
  				throw new UnableToCompleteException();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
  				throw new UnableToCompleteException();
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
			throw new UnableToCompleteException();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			throw new UnableToCompleteException();
		}
		return str;
	}
	private String getPhpClassPath(JType jtype){
		String path="";
		try {
			String classPath = (jtype.getQualifiedSourceName().contains("[]")?jtype.getQualifiedSourceName().substring(0, jtype.getQualifiedSourceName().length()-2):jtype.getQualifiedSourceName());
			Class cl = Class.forName(classPath);
			PhpRpcRelocatePath anotation = (PhpRpcRelocatePath) cl.getAnnotation(PhpRpcRelocatePath.class);
			if (anotation!=null){
				classPath = (jtype.getSimpleSourceName().contains("[]")?jtype.getSimpleSourceName().substring(0, jtype.getSimpleSourceName().length()-2):jtype.getQualifiedSourceName());
				path+=anotation.value().replaceAll("\\.", "/")+"/";
				path+=classPath;
			}else{
				path+=classPath.replaceAll("\\.", "/");
			}
			return path;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return path;
	}
	private <R extends PhpRpcObject>String createFieldDeclaredArray(R[] value, JField field){
		String str="";
		if (value==null) 
			str+="= null;";
		else{
			str+="= array(";
			String type = field.getType().getSimpleSourceName();
			type = type.substring(0, type.length()-2);
			for (int i=0;i<value.length;i++) {
				str+="null";
				if (value[i]!=null){
					String path=getPhpClassPath(field.getType());
					phpObjectConstructor+="        $this->"+field.getName() + "[" + i + "] = autoloadclass( \""+path+"\" );\n";
				}
				if (i<value.length-1)str+=",";
			}
		}
		return str+");";
	}
	
	
	private BufferedWriter getPhpWriter(String phpScriptPath, String className) throws UnableToCompleteException{
		if (!(new File(phpScriptPath)).exists())
			if (!(new File(phpScriptPath)).mkdirs())
				System.err.print("cant create script directory : "+phpScriptPath);
		
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
			throw new UnableToCompleteException();
		}
		
		return file;
	}
	@SuppressWarnings("rawtypes")
	public static Object getObjectInstance(String typeName) throws UnableToCompleteException{
		try {
			Class cl = Class.forName(typeName);
			try {
				@SuppressWarnings("unchecked")
				java.lang.reflect.Constructor co = cl.getConstructor();
				try {
					return co.newInstance();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
	  				throw new UnableToCompleteException();
				} catch (InstantiationException e) {
					e.printStackTrace();
	  				throw new UnableToCompleteException();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
	  				throw new UnableToCompleteException();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
	  				throw new UnableToCompleteException();
				}
			} catch (SecurityException e1) {
				e1.printStackTrace();
  				throw new UnableToCompleteException();
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
  				throw new UnableToCompleteException();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new UnableToCompleteException();
		}
	}
}
