package com.mostka.gwtphprpc.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.dev.javac.typemodel.JField;
import com.google.gwt.dev.javac.typemodel.JRealClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.mostka.gwtphprpc.shared.ServiceRelocatePath;

import java.io.PrintWriter;
import java.io.Serializable;

/**
 * Created by Jozef Môstka on 27.5.2014.
 * https://google.com/+JozefMôstka/about
 */
public class ObjectGenerator extends Generator{

    public static final String CLASS_NAME_APPEND = "Generated_545321";
    private ObjectManager objectManager;

    public ObjectGenerator(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        return null;
    }

    public void generate(TreeLogger logger, GeneratorContext context, JRealClassType classType) throws UnableToCompleteException {

        String namespace = classType.getPackage().getName();

        ServiceRelocatePath annotation = classType.getAnnotation(ServiceRelocatePath.class);
        if (annotation!=null)
            namespace = annotation.value();

        SourceWriter sourceWriter = null;
        try {
            sourceWriter = generateClass(logger,context,classType);
        } catch (Exception e) {
            logger.branch(TreeLogger.Type.ERROR, e.getMessage(), e);
            return;
        }
        if (sourceWriter==null){
            return;
        }
        sourceWriter.commit(logger);
        int test=5;
    }

    private SourceWriter generateClass(TreeLogger logger, GeneratorContext context, JRealClassType classType) throws Exception{
        SourceWriter src = getSourceWriter(classType, context, logger);
        if (src==null){
            throw new Exception("sourcewriter is null");
        }

        generateSerializeArr(src,classType);
        generateSerialize(src, classType);
        generateDeserializeArr(src, classType);
        generateDeserialize(src, classType);


        return src;
    }

    private void generateSerializeArr(SourceWriter src, JRealClassType classType) {
        src.print("public static void serialize(Serializer serializer, ");
        src.print(classType.getQualifiedSourceName());
        src.println("[] instances){");
        src.indent();
        src.println("serializer.writeByte(Serializer.OBJECT_ARR);");
        src.println("if (instances==null){");
        src.indent();
        src.println("serializer.writeValue(-1);");
        src.println("return;");
        src.outdent();
        src.println("}");
        src.println("serializer.writeValue(instances.length);");
        src.println("for("+classType.getQualifiedSourceName()+" t:instances){");
        src.indentln("serialize(serializer,t);");
        src.println("}");
        src.outdent();
        src.println("}");
    }

    private void generateSerialize(SourceWriter src, JRealClassType classType) throws Exception {

        String qualifiedSourceName = classType.getQualifiedSourceName();
        src.print("public static void serialize(Serializer serializer, ");
        src.print(qualifiedSourceName);
        src.println(" instance){");
        src.indent();
        src.println("serializer.writeByte(Serializer.OBJECT);");
        src.println("serializer.writeValue(instance == null);");
        src.println("if (instance==null){");
        src.indentln("return;");
        src.println("}");

        JField[] fields = classType.getFields();

        for (JField field :fields) {
            if (isPrimitive(field)){
                src.print("serializer.writeValue((");
                src.print(field.getType().getQualifiedSourceName());
                src.print(") ReflectionHelper.getField("+qualifiedSourceName+".class , instance, \"" );
                src.print(field.getName());
                src.println("\"));");
            }else{
                JClassType aClass = field.getType().isClass();
                if (field.getType().isArray()!=null){
                    aClass = field.getType().isArray().getComponentType().isClass();
                }
                if (objectManager.addObject((JRealClassType) aClass)){
                    src.print(aClass.getQualifiedSourceName()+CLASS_NAME_APPEND);
                    src.print(".serialize(serializer, (");
                    src.print(field.getType().getQualifiedSourceName());
                    src.print(") ReflectionHelper.getField(");
                    src.print(qualifiedSourceName);
                    src.print(".class, instance, \"");
                    src.print(field.getName());
                    src.println("\"));");
                }else{
                    throw new Exception("Return '"+field.getType().getQualifiedSourceName()+"' parameter must be primitive type or implementing '"+Serializable.class.getCanonicalName()+"'");
                }
            }
        }
        src.outdent();
        src.println("}");

    }

    private boolean isPrimitive(JField field) {
        boolean isPrimitive = field.getType().isPrimitive()!=null;
        if (!isPrimitive && field.getType().isArray()!=null){
            isPrimitive = field.getType().isArray().getComponentType().isPrimitive()!=null;
            if (!isPrimitive){
                String substring = field.getType().getQualifiedSourceName();
                substring = substring.substring(0,substring.length()-2);
                isPrimitive = substring.equals(String.class.getCanonicalName());
            }
        }else {
            if (!isPrimitive) {
                String substring = field.getType().getQualifiedSourceName();
                isPrimitive = substring.equals(String.class.getCanonicalName());
            }
        }
        return isPrimitive;
    }

    private void generateDeserializeArr(SourceWriter src, JRealClassType classType) {
        JClassType aClass = classType.isClass();
        if (classType.isArray()!=null){
            aClass = classType.isArray().getComponentType().isClass();
        }
        src.print("public static ");
        src.print(classType.getQualifiedSourceName());
        src.println("[] deserializeArr(Serializer serializer) throws BadPrimitiveTypeException{");
        src.indent();
        src.println("serializer.checkType(Serializer.OBJECT_ARR);");
        src.println("int len = serializer.readInteger();");
        src.println("if (len==-1){");
        src.indentln("return null;");
        src.println("}");
        src.println(classType.getQualifiedSourceName() + "[] ret = new " + classType.getQualifiedSourceName() + "[len];");
        src.println("for(int i=0;i<len;i++){");
        src.indent();
        src.println("try{");
        src.indentln("ret[i]=" + classType.getQualifiedSourceName() + CLASS_NAME_APPEND + ".deserialize(serializer);");
        src.println("} catch (Exception e) {");
        src.indentln("e.printStackTrace();");
        src.println("}");
        src.outdent();
        src.println("}");
        src.println("return ret;");
        src.outdent();
        src.println("}");
    }

    private void generateDeserialize(SourceWriter src, JRealClassType classType) {
        src.print("public static ");
        String qualifiedSourceName = classType.getQualifiedSourceName();
        src.print(qualifiedSourceName);
        src.println(" deserialize(Serializer serializer) throws Exception {");
        src.indent();
        src.println("serializer.checkType(Serializer.OBJECT);");
        src.println("if (serializer.readBoolean()){");
        src.indentln("return null;");
        src.println("}");
        src.print(qualifiedSourceName);
        src.print(" instance = new ");
        src.print(qualifiedSourceName);
        src.println("();");

        JField[] fields = classType.getFields();

        for (JField field :fields) {
            JClassType aClass = field.getType().isClass();
            String simpleSourceName;
            String fieldQualifiedSourceName;
            if (field.getType().isArray()!=null){
                aClass = field.getType().isArray().getComponentType().isClass();
            }
            if (aClass!=null){
                simpleSourceName = aClass.getSimpleSourceName();
                fieldQualifiedSourceName = aClass.getQualifiedSourceName();
            }else{
                if (field.getType().isArray()!=null){
                    simpleSourceName = field.getType().isArray().getComponentType().isPrimitive().getSimpleSourceName();
                    fieldQualifiedSourceName = field.getType().isArray().getComponentType().isPrimitive().getQualifiedSourceName();
                }else {
                    simpleSourceName = field.getType().getSimpleSourceName();
                    fieldQualifiedSourceName = field.getType().getQualifiedSourceName();
                }
            }
            src.print("ReflectionHelper.setField(");
            src.print(qualifiedSourceName);
            src.print(".class,instance,\"");
            src.print(field.getName());
            src.print("\", ");
            if (isPrimitive(field)){
                src.print("serializer.read");
                simpleSourceName = Character.toUpperCase(simpleSourceName.charAt(0))+simpleSourceName.substring(1);
                src.print(simpleSourceName);
                if (field.getType().isArray()!=null){
                    src.print("Arr");
                }
                src.print("());");
            }else{
                src.print(fieldQualifiedSourceName +CLASS_NAME_APPEND);
                src.print(".deserialize");
                if (field.getType().isArray()!=null){
                    src.print("Arr");
                }
                src.print("(serializer));");
            }
            src.println();
        }

        src.println("return instance;");
        src.outdent();
        src.println("}");
    }

    private SourceWriter getSourceWriter(JClassType classType, GeneratorContext context, TreeLogger logger) {
        String packageName = classType.getPackage().getName();
        String simpleName = classType.getSimpleSourceName() + CLASS_NAME_APPEND;

        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

        composer.addImport(com.google.gwt.user.client.rpc.impl.ReflectionHelper.class.getCanonicalName());
        composer.addImport(com.mostka.serializer.java.Serializer.BadPrimitiveTypeException.class.getCanonicalName());
        composer.addImport(com.mostka.serializer.java.Serializer.class.getCanonicalName());

        //composer.setSuperclass(packageName+"."+classType.getSimpleSourceName());

        PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
        if (printWriter == null) {
            return null;
        }
        return composer.createSourceWriter(context, printWriter);
    }
}
