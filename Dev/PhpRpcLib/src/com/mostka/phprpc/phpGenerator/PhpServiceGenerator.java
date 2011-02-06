package com.mostka.phprpc.phpGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.mostka.phprpc.client.PhpRpcRelocatePath;
import com.mostka.phprpc.phpLinker.PhpServiceLinker;

public class PhpServiceGenerator {
	private TreeLogger logger;
	GeneratorContext context;
	String typeName;
	JClassType classType;
	
	private String serviceName;
	private String serverName;
	private String phpScriptPath;
	private String phpService;
	
	public PhpServiceGenerator(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		this.logger = logger;
		this.context=context;
		this.typeName=typeName;
	}
	public void generate() throws UnableToCompleteException, NotFoundException, IOException{
		//PhpLinker.create().addClassLinker(newClassName, phpFullPath);
		classType = context.getTypeOracle().getType(typeName);
		generateName();
		phpService="class " + serviceName + "{\n";
		phpService+=generateMethods();
		phpService+="}\n";
		/*System.out.println("--------------------------------------------");
		System.out.println(phpService);
		System.out.println("");*/
		writeToFile();
	}
	private void generateName() throws UnableToCompleteException{
		serviceName = classType.getSimpleSourceName() + "Impl";
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
	private String generateMethods(){
		String methods = "";
		JMethod[] jMethods = classType.getMethods();
		for (JMethod method : jMethods) {
			methods+="    public $ARGUMENTS_" + method.getName() + "=\"" + getParametersMetadata(method.getParameters()) +"\";\n";
			methods+="    public function " + method.getName();
			methods+="(" + generateParameters(method.getParameters()) + "){\n";
			methods+="        \n";
			methods+="    }\n";
		}
		return methods;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String getParametersMetadata(JParameter[] jParameters){
		String metadata = "";
		for (int i = 0; i < jParameters.length-1; i++) {
			JParameter jParameter = jParameters[i];
			if (!jParameter.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JPrimitiveType") &&
					!jParameter.getType().getParameterizedQualifiedSourceName().equals("java.lang.String")){
				try {
					Class cl = Class.forName(jParameter.getType().getQualifiedSourceName());
					PhpRpcRelocatePath anotation = (PhpRpcRelocatePath) cl.getAnnotation(PhpRpcRelocatePath.class);
					if (anotation!=null){
						metadata+=anotation.value().replaceAll("\\.", "/")+"/";
						metadata+=jParameter.getType().getSimpleSourceName();
					}else{
						metadata+=jParameter.getType().getQualifiedSourceName().replaceAll("\\.", "/");
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}else{
				metadata+=jParameter.getType().getSimpleSourceName();
			}
			if (i < jParameters.length-2)
				metadata+="|";
		}
		return metadata;
	}
	private void writeToFile() throws IOException, UnableToCompleteException{
		BufferedWriter writer = getPhpWriter(phpScriptPath,serviceName);
			writer.write("<?php\n");
			writer.write(phpService);
			writer.write("\n?>");
			writer.close();
	}
	private BufferedWriter getPhpWriter(String phpScriptPath, String serviceName) throws UnableToCompleteException{
		if (!(new File(phpScriptPath)).exists())
			if (!(new File(phpScriptPath)).mkdirs())
				System.err.print("cant create script directory : "+phpScriptPath);
		
		File f;
	    f=new File(phpScriptPath+"/"+serviceName+".class.php");
	    if(f.exists()){
	    	serviceName+=".TMP";
	    	f = new File(phpScriptPath+"/"+serviceName+".class.php");
	    	if(f.exists()){
	    		f.delete();
	    	}
	    }
	    BufferedWriter file;
		try {
			FileWriter fstream = new FileWriter(phpScriptPath+"/"+serviceName+".class.php");
	        file = new BufferedWriter(fstream);
		} catch (IOException e) {
			e.printStackTrace();
			throw new UnableToCompleteException();
		}
		
		return file;
	}
	private String generateParameters(JParameter[] jParameters){
		String parameters = "";
		for (int i = 0; i < jParameters.length-1; i++) {
			JParameter jParameter = jParameters[i];
			if (jParameter.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JArrayType")){
				parameters += "Array $" + jParameter.getName();
			}else if((jParameter.getType().getClass().getName().equals("com.google.gwt.core.ext.typeinfo.JPrimitiveType")) ||
					 (jParameter.getType().getParameterizedQualifiedSourceName().equals("java.lang.String"))){
				parameters += "$" + jParameter.getName();
			}else{
				String ClassName = jParameter.getType().getSimpleSourceName();
				parameters += ClassName+" $" + jParameter.getName();
			}
			if (i < jParameters.length-2)
				parameters+=", ";
		}
		return parameters;
	}

}
