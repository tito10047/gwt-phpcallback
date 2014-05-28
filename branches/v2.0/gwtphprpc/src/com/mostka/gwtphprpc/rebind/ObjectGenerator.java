package com.mostka.gwtphprpc.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;

/**
 * Created by Jozef Môstka on 27.5.2014.
 * https://google.com/+JozefMôstka/about
 */
public class ObjectGenerator extends Generator{

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        return null;
    }

    private SourceWriter getSourceWriter(JClassType classType, GeneratorContext context, TreeLogger logger) {
        String packageName = classType.getPackage().getName();
        String simpleName = classType.getSimpleSourceName() + "PhpObjectGenerate_12564";

        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

        //composer.addImport(PhpRpc.class.getCanonicalName());

        composer.setSuperclass(packageName+"."+classType.getSimpleSourceName());

        PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
        if (printWriter == null) {
            return null;
        }
        return composer.createSourceWriter(context, printWriter);
    }
}
