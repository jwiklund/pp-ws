package com.atex.plugins.wsplugin;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletMapping;

public class RunWar {
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(9090);
        ServletHandler handler = new ServletHandler();
        ServletHolder holder = new ServletHolder(WSPluginServlet.class);
        holder.setName("ws");
        handler.addServlet(holder);
        ServletMapping mapping = new ServletMapping();
        mapping.setPathSpec("/ws/*");
        mapping.setServletName("ws");
        handler.addServletMapping(mapping);
        server.addHandler(handler);
        server.start();
    }
}
