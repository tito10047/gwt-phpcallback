package com.mostka.gwtphprpc.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.dev.javac.typemodel.JRealClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jozef Môstka on 28.5.2014.
 * https://google.com/+JozefMôstka/about
 */
public class ObjectManager {
    private final String tempPath;
    private final String objectPath;
    private final ArrayList<JRealClassType> classes;
    private final ObjectGenerator objectGenerator;

    public ObjectManager(String tempPath, String objectPath) {
        this.tempPath = tempPath;
        this.objectPath = objectPath;
        objectGenerator = new ObjectGenerator(this);
        classes = new ArrayList<JRealClassType>();
    }

    public static boolean isNonPrimitive(JRealClassType classType) {
        return classType.isPrimitive()==null && !classType.getQualifiedSourceName().equals(String.class.getCanonicalName());
    }

    public boolean addObject(JRealClassType classType){
        if (classType == null) {
            return false;
        }
        com.google.gwt.dev.javac.typemodel.JClassType[] implementedInterfaces = classType.getImplementedInterfaces();
        boolean found=false;
        // check if object implements Serializable
        for (com.google.gwt.dev.javac.typemodel.JClassType interfaceClassType :implementedInterfaces) {
            if (interfaceClassType.getQualifiedSourceName().equals(Serializable.class.getCanonicalName())){
                found=true;
                break;
            }
        }
        if (!found){
            return false;
        }
        if (classes.contains(classType)){
            return true;
        }
        classes.add(classType);
        return true;
    }

    public void generate(TreeLogger logger, GeneratorContext context) throws UnableToCompleteException {
        for (int i=0;i<classes.size();i++) {
            JRealClassType classType = classes.get(i);
            objectGenerator.generate(logger,context,classType);
        }

    }

/*
    private SourceWriter getSourceWriter(JClassType classType, GeneratorContext context, TreeLogger logger) {
        String packageName = classType.getPackage().getName();
        String simpleName = classType.getSimpleSourceName() + "__Async";

        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

        //composer.addImport(RpcRequestBuilder.class.getCanonicalName());

        //composer.addImplementedInterface(packageName + "." + classType.getSimpleSourceName());

        PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
        if (printWriter == null) {
            return null;
        }
        return composer.createSourceWriter(context, printWriter);
    }*/
}
