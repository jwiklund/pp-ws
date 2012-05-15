package com.atex.plugins.wsplugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

public class WSPluginServlet extends ServletContainer {

    Set<Class<?>> classes;
    Set<Object> singletons;

    protected List<Class<? extends Object>> getResourceClasses()
    {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        return classes;
    }

    private ResourceConfig getDefaultResourceConfig()
    {
        return new ResourceConfig() {

            @Override
            public Object getProperty(String propertyName)
            {
                return null;
            }
            
            @Override
            public Map<String, Object> getProperties()
            {
                return Collections.emptyMap();
            }
            
            @Override
            public Map<String, Boolean> getFeatures()
            {
                return Collections.emptyMap();
            }
            
            @Override
            public boolean getFeature(String featureName)
            {
                return false;
            }

            @Override
            public Set<Class<?>> getClasses()
            {
                return new HashSet<Class<?>>();
            }

            @Override
            public Set<Object> getSingletons()
            {
                return new HashSet<Object>(Arrays.asList(new Ex(), new Hello(), new Other("test2")));
            }
        };
    }

    @Override
    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, ServletConfig servletConfig) throws ServletException
    {
        return getDefaultResourceConfig();
    }

    @Override
    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig wc) throws ServletException
    {
        return getDefaultResourceConfig();
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        reload();
        super.service(request, response);
    }
}
