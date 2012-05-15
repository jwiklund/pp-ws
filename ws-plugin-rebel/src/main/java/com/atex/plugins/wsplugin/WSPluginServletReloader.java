package com.atex.plugins.wsplugin;

import java.util.HashSet;
import java.util.Set;

import org.zeroturnaround.javarebel.ClassEventListener;
import org.zeroturnaround.javarebel.LoggerFactory;

public class WSPluginServletReloader implements ClassEventListener {

    static Set<WSPluginServletHolder> servlets = new HashSet<WSPluginServletHolder>();

    public static void register(Object wsservlet)
    {
        synchronized (servlets) {
            servlets.add(new WSPluginServletHolder(wsservlet));
        }
    }

    public static void deregister(Object wsservlet)
    {
        synchronized (servlets) {
            servlets.remove(new WSPluginServletHolder(wsservlet));
        }
    }

    public void onClassEvent(int arg0, @SuppressWarnings("rawtypes") Class reloaded)
    {
        synchronized (servlets) {
            for (WSPluginServletHolder holder : servlets) {
                holder.reloadCachedClassNamesIfNeeded();
                if ("com.atex.plugins.wsplugin.WSPluginServlet".equals(reloaded.getName()) ||
                    holder.classes.contains(reloaded.getName()))
                {
                    LoggerFactory.getInstance().echo("ws-plugin-rebel.INFO: " + reloaded.getName() + " changed, reloading ws plugin configuration");
                    holder.markReload();
                }
            }
        }
    }

    public int priority()
    {
        return 0;
    }
}
