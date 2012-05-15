package com.atex.plugins.wsplugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.Path;

import com.polopoly.application.Application;
import com.polopoly.application.servlet.ApplicationServletUtil;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.ContentOperationFailedException;
import com.polopoly.cm.client.EjbCmClient;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.common.logging.LogUtil;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

public class WSPluginServlet extends ServletContainer {

    protected final class WSPluginResourceList implements Iterable<String> {
        public Iterator<String> iterator()
        {
            List<String> result = new ArrayList<String>();
            for (Object s : singletons) {
                Path annotation = s.getClass().getAnnotation(Path.class);
                if (annotation != null) {
                    String description = annotation.value() + " - " + s.getClass();
                    if (s instanceof Policy) {
                        description = description + " - " + ((Policy) s).getContentId().getContentId().getContentIdString();
                    }
                    result.add(description);
                }
            }
            Collections.sort(result);
            return result.iterator();
        }
    }

    public Set<Class<?>> classes = new HashSet<Class<?>>();
    public Set<Object> singletons = new HashSet<Object>();

    private PolicyCMServer server;

    @Override
    public void init() throws ServletException
    {
        Application application = ApplicationServletUtil.getApplication(getServletContext());
        CmClient cmclient = (CmClient) application.getApplicationComponent(EjbCmClient.DEFAULT_COMPOUND_NAME);
        server = cmclient.getPolicyCMServer();
        super.init();
    }
    
    public long loadConfiguration() {
        String pluginConfiguration = getServletConfig().getInitParameter("ws-plugin-configuration");
        if (pluginConfiguration == null) {
            pluginConfiguration = "com.atex.plugins.ws-plugin.configuration";
        }
        HashSet<Object> s = new HashSet<Object>();
        s.add(new WSPluginRootResource(new WSPluginResourceList()));
        long changed = 0;
        try {
            Policy configuration = server.getPolicy(new ExternalContentId(pluginConfiguration));
            changed = configuration.getContentId().getVersion();
            if (configuration != null) {
                ContentList resources = configuration.getContent().getContentList("resources");
                ListIterator<ContentReference> resourceIterator = resources.getListIterator();
                while (resourceIterator.hasNext()) {
                    ContentReference reference = resourceIterator.next();
                    Policy policy = server.getPolicy(reference.getReferredContentId());
                    s.add(policy);
                    if (changed < policy.getContentId().getVersion()) {
                        changed = policy.getContentId().getVersion();
                    }
                }
            }
        } catch (ContentOperationFailedException e) {
            LogUtil.getLog().log(Level.FINE, "Configuration content for ws-plugin does not exist");
        } catch (CMException e) {
            LogUtil.getLog().log(Level.WARNING, "Could not load ws-plugin configuration", e);
        }
        classes = new HashSet<Class<?>>();
        singletons = s;
        return changed;
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
