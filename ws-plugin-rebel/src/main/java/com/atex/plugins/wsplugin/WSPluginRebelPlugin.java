package com.atex.plugins.wsplugin;

import org.zeroturnaround.javarebel.ClassResourceSource;
import org.zeroturnaround.javarebel.Integration;
import org.zeroturnaround.javarebel.IntegrationFactory;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.Plugin;
import org.zeroturnaround.javarebel.ReloaderFactory;

public class WSPluginRebelPlugin implements Plugin {
    public void preinit()
    {
        Integration i = IntegrationFactory.getInstance();
        ClassLoader cl = WSPluginRebelPlugin.class.getClassLoader();
        i.addIntegrationProcessor(cl, "com.atex.plugins.wsplugin.WSPluginServlet", new WSPluginServletProcessor());
        ReloaderFactory.getInstance().addClassReloadListener(new WSPluginServletReloader());
        LoggerFactory.getInstance().echo("ws-plugin-rebel.INFO: enabled");
    }

    public String getId()
    {
        return "ws-plugin";
    }

    public String getName()
    {
        return "WS Plugin Rebel Plugin";
    }

    public String getDescription()
    {
        return "Reloads ws configuration on changes in policy classes";
    }

    public String getAuthor()
    {
        return null;
    }

    public String getWebsite()
    {
        return null;
    }

    public boolean checkDependencies(ClassLoader classLoader, ClassResourceSource classResourceSource)
    {
        return classResourceSource.getClassResource("com.atex.plugins.wsplugin.WSPluginServlet") != null;
    }

    public String getSupportedVersions()
    {
        return null;
    }

    public String getTestedVersions()
    {
        return null;
    }
}
