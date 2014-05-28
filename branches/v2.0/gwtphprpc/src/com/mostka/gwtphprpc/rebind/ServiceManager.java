package com.mostka.gwtphprpc.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JMethod;

import java.io.IOException;

/**
 * Created by Jozef Môstka on 27.5.2014.
 * https://google.com/+JozefMôstka/about
 */
public class ServiceManager {
    private String router;
    private String tempPath;
    private String servicePath;

    public ServiceManager(String router, String tempPath, String servicePath) {
        this.router = router;
        this.tempPath = tempPath;
        this.servicePath = servicePath;
    }

    public short pushService(String namespace, String className, JMethod[] methods) {
        return 0;
    }

    public void generatePhp(short serviceCompiledName, TreeLogger logger, GeneratorContext context, String typeName) throws IOException {

    }

    public String getRouter() {
        return router;
    }
}
