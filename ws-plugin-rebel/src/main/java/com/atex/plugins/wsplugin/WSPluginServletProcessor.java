package com.atex.plugins.wsplugin;

import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtField;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.bundled.javassist.Modifier;
import org.zeroturnaround.bundled.javassist.bytecode.Descriptor;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

public class WSPluginServletProcessor extends JavassistClassBytecodeProcessor
{
    @Override
    public void process(ClassPool pool, ClassLoader loader, CtClass wsservlet) throws Exception
    {
        CtField classChange = new CtField(pool.get("boolean"), "generated$classChange", wsservlet);
        classChange.setModifiers(Modifier.PUBLIC);
        wsservlet.addField(classChange);

        CtField lastVersion = new CtField(pool.get("long"), "generated$lastVersion", wsservlet);
        lastVersion.setModifiers(Modifier.PUBLIC);
        wsservlet.addField(lastVersion);

        CtMethod methodGet = wsservlet.getMethod("getDefaultResourceConfig", Descriptor.ofMethod(pool.get("com.sun.jersey.api.core.ResourceConfig"), new CtClass[0]));
        methodGet.setBody("{\n" +
        		  "  generated$lastVersion = loadConfiguration();\n" + 
        		  "  return reloadableResourceConfig();\n" +
        		  "}");

        CtField lastCheck = new CtField(pool.get("long"), "generated$lastCheck", wsservlet);
        wsservlet.addField(lastCheck);
        CtMethod methodService = new CtMethod(pool.get("void"), "service", new CtClass[] {
            pool.get("javax.servlet.http.HttpServletRequest"),
            pool.get("javax.servlet.http.HttpServletResponse")
        }, wsservlet);
        methodService.setBody("{\n" +
        		      "  if (java.lang.System.currentTimeMillis() - generated$lastCheck > 1000) {\n" +
                              "    generated$lastCheck = java.lang.System.currentTimeMillis(); " +
                              "    long version = loadConfiguration();\n" +
                              "    if (generated$lastVersion < version) {\n" +
                              "      generated$lastVersion = version;\n" +
                              "      generated$classChange = false;\n" +
                              "      org.zeroturnaround.javarebel.LoggerFactory.getInstance().echo(\"ws-plugin-rebel.INFO: policy configuration changed, reloading ws plugin configuration\");\n" +
                              "      reload();\n" +
                              "    } else if (generated$classChange) {" +
                              "      generated$classChange = false;\n" +
                              "      reload();\n" +
                              "    }\n" +
                              "    super.service($1, $2);\n" +
                              "  }" +
                              "}");
        wsservlet.addMethod(methodService);

        CtMethod methodInit = new CtMethod(pool.get("void"), "init", new CtClass[0], wsservlet);
        methodInit.setBody("{" +
        		   "  com.atex.plugins.wsplugin.WSPluginServletReloader.register($0);\n" +
                           "  super.init();\n" +
                           "}");
        wsservlet.addMethod(methodInit);

        CtMethod methodDestroy = new CtMethod(pool.get("void"), "destroy", new CtClass[0], wsservlet);
        methodDestroy.setBody("{" +
        		      "  com.atex.plugins.wsplugin.WSPluginServletReloader.deregister($0);\n" +
                              "  super.destroy();\n" +
                              "}");
        wsservlet.addMethod(methodDestroy);
    }
}
