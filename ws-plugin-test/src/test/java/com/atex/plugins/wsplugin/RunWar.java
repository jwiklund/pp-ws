package com.atex.plugins.wsplugin;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class RunWar {
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(9090);
        WebAppContext handler = new WebAppContext("src/main/webapp", "");
        server.addHandler(handler);
        server.start();
    }
}
