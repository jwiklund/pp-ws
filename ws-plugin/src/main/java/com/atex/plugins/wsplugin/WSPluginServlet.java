package com.atex.plugins.wsplugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

public class WSPluginServlet extends ServletContainer {

    public Set<Class<?>> classes = new HashSet<Class<?>>();
    public Set<Object> singletons = new HashSet<Object>();

    public long loadConfiguration() {
        classes = new HashSet<Class<?>>();
        singletons = new HashSet<Object>();
        return 0;
    }

    protected List<Class<? extends Object>> getResourceClasses()
    {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        return classes;
    }

    private ResourceConfig getDefaultResourceConfig()
    {
        loadConfiguration();
        return reloadableResourceConfig();
    }

    private ResourceConfig reloadableResourceConfig()
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
                return classes;
            }

            @Override
            public Set<Object> getSingletons()
            {
                return singletons;
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
}
