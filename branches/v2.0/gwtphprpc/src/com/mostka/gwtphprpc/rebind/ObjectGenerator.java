package com.mostka.gwtphprpc.rebind;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.dev.javac.typemodel.*;
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
    private boolean isProdMode;

    public ObjectGenerator(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        return null;
    }

    public void generate(TreeLogger logger, GeneratorContext context, JRealClassType classType) throws UnableToCompleteException {
        isProdMode = context.isProdMode();
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

        generatePrivateFieldMethods(src,classType);
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

    private void generatePrivateFieldMethods(SourceWriter src, JRealClassType classType) throws Exception {
        String qualifiedSourceName = classType.getQualifiedSourceName();
        JField[] fields = classType.getFields();
        for (JField field :fields) {
            if (!isPrimitive(field)) {
                JClassType aClass = field.getType().isClass();
                if (field.getType().isArray()!=null){
                    aClass = field.getType().isArray().getComponentType().isClass();
                    if (!objectManager.addObject((JRealClassType) aClass)){
                        throw new Exception("Return '"+field.getType().getQualifiedSourceName()+"' parameter must be primitive type or implementing '"+Serializable.class.getCanonicalName()+"'");
                    }
                }
            }
            src.print("private static "+(isProdMode?"native ":"")+"void set"+ucFirst(field.getName())+"("+qualifiedSourceName+" instance, "+field.getType().getQualifiedSourceName()+" value)");
            if (isProdMode){
                src.println("/*-{");
                src.indentln("instance.@"+qualifiedSourceName+"::"+field.getName()+" = value;");
                src.println("}-*/;");
            }else{
                src.println("{");
                src.indentln("ReflectionHelper.setField("+qualifiedSourceName+".class,instance,\""+field.getName()+"\",value);");
                src.println("}");
            }
            src.print("private static "+(isProdMode?"native ":"")+field.getType().getQualifiedSourceName()+" get"+ucFirst(field.getName())+"("+qualifiedSourceName+" instance)");
            if (isProdMode){
                src.println("/*-{");
                src.indentln("return instance.@"+qualifiedSourceName+"::"+field.getName()+";");
                src.println("}-*/;");
            }else{
                src.println("{");
                src.indentln("ReflectionHelper.getField("+qualifiedSourceName+".class,instance,\""+field.getName()+"\");");
                src.println("}");
            }
        }

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

        JRealClassType superclass = (JRealClassType) classType.getSuperclass();
        if (superclass!=null &&
                !superclass.getQualifiedSourceName().equals(java.lang.Object.class.getCanonicalName()) &&
                !superclass.getQualifiedSourceName().equals(Serializable.class.getCanonicalName())
                ){
            if (!objectManager.addObject(superclass)) {
                throw new Exception("superclass '"+superclass.getQualifiedSourceName()+"' must implementing '" + Serializable.class.getCanonicalName() + "'");
            }
            src.print(superclass.getQualifiedSourceName()+CLASS_NAME_APPEND);
            src.println(".serialize(serializer, instance);");
        }

        JField[] fields = classType.getFields();

        for (JField field :fields) {
            if (isPrimitive(field)){
                src.print("serializer.writeValue(");
                //src.print("serializer.writeValue((");
                if (field.isPrivate()){
                    src.print("get"+ucFirst(field.getName())+"(instance)");
                }else{
                    src.print("instance."+field.getName());
                }
                src.println(");");
                //src.print(field.getType().getQualifiedSourceName());
                //src.print(") ReflectionHelper.getField("+qualifiedSourceName+".class , instance, \"" );
                //src.print(field.getName());
                //src.println("\"));");
            }else{
                JClassType aClass = field.getType().isClass();
                if (field.getType().isArray()!=null){
                    aClass = field.getType().isArray().getComponentType().isClass();
                }
                src.print(aClass.getQualifiedSourceName()+CLASS_NAME_APPEND);
                src.print(".serialize(serializer, (");
                if (field.isPrivate()){
                    src.print("get"+ucFirst(field.getName())+"(instance)");
                }else{
                    src.print("instance."+field.getName());
                }
                //src.print(field.getType().getQualifiedSourceName());
                //src.print(") ReflectionHelper.getField(");
                //src.print(qualifiedSourceName);
                //src.print(".class, instance, \"");
                //src.print(field.getName());
                //src.println("\"));");
                src.println("));");
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
        String qualifiedSourceName = classType.getQualifiedSourceName();
        src.println("public static "+qualifiedSourceName+" deserialize(Serializer serializer) throws Exception {");
        src.indentln("return deserialize(serializer,new "+qualifiedSourceName+"());");
        src.println("}");
        src.println("public static "+qualifiedSourceName+" deserialize(Serializer serializer, "+qualifiedSourceName+" instance) throws Exception {");
        src.indent();
        src.println("serializer.checkType(Serializer.OBJECT);");
        src.println("if (serializer.readBoolean()){");
        src.indentln("return null;");
        src.println("}");

        JRealClassType superclass = (JRealClassType) classType.getSuperclass();
        if (superclass!=null &&
                !superclass.getQualifiedSourceName().equals(java.lang.Object.class.getCanonicalName()) &&
                !superclass.getQualifiedSourceName().equals(Serializable.class.getCanonicalName())
                ){
            src.print(superclass.getQualifiedSourceName()+CLASS_NAME_APPEND);
            src.println(".deserialize(serializer, instance);");
        }

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
            if (field.isPrivate()){
                src.print("set"+ucFirst(field.getName())+"(instance,");
            }else{
                src.print("instance."+field.getName()+" = ");
            }
            //src.print("ReflectionHelper.setField(");
            //src.print(qualifiedSourceName);
            //src.print(".class,instance,\"");
            //src.print(field.getName());
            //src.print("\", ");
            if (isPrimitive(field)){
                src.print("serializer.read");
                simpleSourceName = ucFirst(simpleSourceName);
                src.print(simpleSourceName);
                if (field.getType().isArray()!=null){
                    src.print("Arr");
                }
                src.print("()");
            }else{
                src.print(fieldQualifiedSourceName +CLASS_NAME_APPEND);
                src.print(".deserialize");
                if (field.getType().isArray()!=null){
                    src.print("Arr");
                }
                src.print("(serializer)");
            }
            if (field.isPrivate()){
                src.print(")");
            }
            src.println(";");
        }

        src.println("return instance;");
        src.outdent();
        src.println("}");
    }

    private String ucFirst(String str) {
        return Character.toUpperCase(str.charAt(0))+ str.substring(1);
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
