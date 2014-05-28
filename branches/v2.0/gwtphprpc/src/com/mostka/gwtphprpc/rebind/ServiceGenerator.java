package com.mostka.gwtphprpc.rebind;

import com.google.gwt.core.ext.*;
import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JTypeParameter;
import com.google.gwt.dev.javac.typemodel.*;
import com.google.gwt.dev.javac.typemodel.JRealClassType;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.mostka.gwtphprpc.client.AsyncCallback;
import com.mostka.gwtphprpc.client.HasDeserializer;
import com.mostka.gwtphprpc.client.RpcRequestBuilder;
import com.mostka.gwtphprpc.shared.RpcException;
import com.mostka.gwtphprpc.shared.ServiceRelocatePath;
import com.mostka.serializer.java.Serializer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * Created by Jozef Môstka on 27.5.2014.
 * https://google.com/+JozefMôstka/about
 */
public class ServiceGenerator extends Generator{

    private ServiceManager serviceManager;

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {

        JClassType classType;
        try {

            if (serviceManager==null){
                String servicePath = context.getPropertyOracle().getConfigurationProperty("servicePath").getValues().get(0);
                String tempPath = context.getPropertyOracle().getConfigurationProperty("tempPath").getValues().get(0);
                String router = context.getPropertyOracle().getConfigurationProperty("router").getValues().get(0);
                serviceManager = new ServiceManager(router, tempPath,servicePath);
            }

            classType = context.getTypeOracle().getType(typeName);


            String namespace = classType.getPackage().getName();

            ServiceRelocatePath annotation = classType.getAnnotation(ServiceRelocatePath.class);
            if (annotation!=null)
                namespace = annotation.value();

            short serviceCompiledName =  serviceManager.pushService(namespace, classType.getName(), classType.getMethods());

            SourceWriter src;
            try {
                src = generateMethods(classType, context, serviceCompiledName, logger);
            } catch (Exception e) {
                e.printStackTrace();
                throw new UnableToCompleteException();
            }
            if (src == null)return typeName + "__Async";
            src.commit(logger);

            serviceManager.generatePhp(serviceCompiledName, logger, context, typeName);


            return typeName + "__Async";
        } catch (NotFoundException e) {
            e.printStackTrace();
            throw new UnableToCompleteException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnableToCompleteException();
        } catch (BadPropertyValueException e1) {
            e1.printStackTrace();
            throw new UnableToCompleteException();
        }
    }

    private SourceWriter generateMethods(JClassType classType, GeneratorContext context, short serviceCompiledName, TreeLogger logger) throws Exception {

        SourceWriter src = getSourceWriter(classType, context, logger);
        if (src==null){
            throw new Exception("sourcewriter is null");
        }

        JMethod[] methods = classType.getMethods();
        String router = serviceManager.getRouter();

        src.indent();
        for (int methodPos = 0; methodPos < methods.length; methodPos++) {
            JMethod method = methods[methodPos];

            String returnedObjectFullClassPath;

            JParameter[] parameters = method.getParameters();
            JParameter parameter = parameters[parameters.length-1];
            //TODO: check with full namespace name
            com.google.gwt.dev.javac.typemodel.JParameterizedType JParType = (com.google.gwt.dev.javac.typemodel.JParameterizedType) parameter.getType();

            if(!JParType.getBaseType().getQualifiedSourceName().equals(AsyncCallback.class.getCanonicalName())){
                System.err.println("Method must have last argument AsyncCallback<T>");
                throw new Exception("Method must have last argument AsyncCallback<T>");
            }

            //==== TEST ======
            JParameterizedType parameterizedType = parameter.getType().getLeafType().isParameterized();
            if (parameterizedType==null){
                throw new Exception("argument AsyncCallback must be parametrized");
            }
            JClassType[] typeArgs = parameterizedType.getTypeArgs();// 1 TstObject
            if (typeArgs.length!=1){
                throw new Exception("argument AsyncCallback must have only one parameter");
            }
            JRealClassType returnClassType = (JRealClassType) typeArgs[0];
            returnedObjectFullClassPath = returnClassType.getQualifiedSourceName();
            boolean returnTypeIsNotPrimitive = returnClassType.isPrimitive()==null && !returnClassType.getQualifiedSourceName().equals(String.class.getCanonicalName());
            if (returnTypeIsNotPrimitive){
                com.google.gwt.dev.javac.typemodel.JClassType[] implementedInterfaces = returnClassType.getImplementedInterfaces();
                boolean found=false;
                for (com.google.gwt.dev.javac.typemodel.JClassType interfaceClassType :implementedInterfaces) {
                    if (interfaceClassType.getQualifiedSourceName().equals(Serializable.class.getCanonicalName())){
                        found=true;
                        break;
                    }
                }
                if (!found){
                    throw new Exception("Return parameter must be primitive type or implementing '"+Serializable.class.getCanonicalName()+"'");
                }

            }

            String callbackParamName = parameter.getName();
            src.print("public void "+method.getName()+"(");

            String[] params = new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                JParameter jParameter = parameters[i];
                params[i]=jParameter.getType().getParameterizedQualifiedSourceName()+" "+jParameter.getName();
            }

            src.print(Joiner.on(",").join(params));
            src.println("){");
            src.indent();
            src.println("RpcRequestBuilder request = new RpcRequestBuilder(\""+router+"\","+serviceCompiledName+","+methodPos+");");
            src.println("Serializer serializer = request.getSerializer();");
            for (int i = 0; i < parameters.length-1; i++) {
                JParameter jParameter = parameters[i];
                if (jParameter.getType().isPrimitive()!=null || jParameter.getType().getParameterizedQualifiedSourceName().equals(String.class.getCanonicalName())) {
                    src.println("serializer.writeValue("+jParameter.getName()+");");
                }else{
                    boolean find=false;
                    JClassType paramClass = jParameter.getType().isClass();
                    if (paramClass==null){
                        throw new Exception("paramete '"+jParameter.getType().getQualifiedSourceName()+"' must be primitive type or must be implemented by '"+Serializable.class.getCanonicalName()+"'");
                    }
                    JClassType[] implementedInterfaces = paramClass.getImplementedInterfaces();
                    for (JClassType interface_ :implementedInterfaces) {
                        if(interface_.getQualifiedSourceName().equals(Serializable.class.getCanonicalName())){
                            find=true;
                            break;
                        }
                    }
                    if (!find){
                        throw new Exception(jParameter.getType().getParameterizedQualifiedSourceName()+" is not implementind '"+Serializable.class.getCanonicalName()+"'");
                    }
                    src.println(jParameter.getType().getParameterizedQualifiedSourceName()+"Generated.___serialize(serializer, "+jParameter.getName()+");");
                }
            }
            src.println();
            src.println("request.sendRequest("+callbackParamName+",new HasDeserializer<"+returnedObjectFullClassPath+">() {");
            src.indent();
            src.println("public "+returnedObjectFullClassPath+" deserialize(Serializer serializer) throws RpcException {");
            src.indent();
            src.println("try{");
            if (returnTypeIsNotPrimitive){
                src.indentln("return "+returnedObjectFullClassPath+"Generated.___deserialize(serializer);");
            }else{
                src.indent();
                String name = returnClassType.getName();
                src.print("return serializer.read"+Character.toUpperCase(name.charAt(0))+name.substring(1));
                if (returnClassType.isArray()!=null){
                    src.print("Arr");
                }
                src.println("();");
                src.outdent();
            }
            src.println("} catch (BadPrimitiveTypeException e) {");
            src.indentln("throw new RpcException(\"cant parse response\",e);");
            src.println("}");
            src.outdent();
            src.println("}");
            src.outdent();
            src.println("});");
            src.outdent();
            src.println("}");
        }
        src.outdent();
        return src;
    }

    private SourceWriter getSourceWriter(JClassType classType, GeneratorContext context, TreeLogger logger) {
        String packageName = classType.getPackage().getName();
        String simpleName = classType.getSimpleSourceName() + "__Async";

        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

        composer.addImport(RpcRequestBuilder.class.getCanonicalName());
        composer.addImport(Serializer.class.getCanonicalName());
        composer.addImport(HasDeserializer.class.getCanonicalName());
        composer.addImport(RpcException.class.getCanonicalName());
        composer.addImport(Serializer.BadPrimitiveTypeException.class.getCanonicalName());

        composer.addImplementedInterface(packageName + "." + classType.getSimpleSourceName());

        PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
        if (printWriter == null) {
            return null;
        }
        return composer.createSourceWriter(context, printWriter);
    }
}
