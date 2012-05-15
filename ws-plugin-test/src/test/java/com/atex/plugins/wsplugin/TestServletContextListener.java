package com.atex.plugins.wsplugin;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.polopoly.application.Application;
import com.polopoly.application.ConnectionPropertiesException;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.application.PacemakerComponent;
import com.polopoly.application.PacemakerSettings;
import com.polopoly.application.StandardApplication;
import com.polopoly.application.config.ConfigurationRuntimeException;
import com.polopoly.application.servlet.ApplicationServletUtil;
import com.polopoly.cm.client.EjbCmClient;

public class TestServletContextListener implements ServletContextListener
{
    private Application _application;

    public void contextInitialized(ServletContextEvent sce)
    {
        try {
            ServletContext sc = sce.getServletContext();

            _application = new StandardApplication(ApplicationServletUtil.getApplicationName(sc));
            _application.setManagedBeanRegistry(ApplicationServletUtil.getManagedBeanRegistry());

            EjbCmClient cmClient = new EjbCmClient();
            _application.addApplicationComponent(cmClient);

            PacemakerComponent pacemaker = new PacemakerComponent();
            PacemakerSettings settings = pacemaker.getPacemakerSettings();
            settings.setEnabled(true);
            settings.setFixedRate(true);
            settings.setInterval(1);
            pacemaker.setPacemakerSettings(settings);
            _application.addApplicationComponent(pacemaker);

            _application.readConnectionProperties(ApplicationServletUtil.getConnectionProperties(sc));

            _application.init();

            ApplicationServletUtil.setApplication(sc, _application);
        }
        catch (IllegalApplicationStateException e) {
            throw new RuntimeException("This is a programming error, should never happend.", e);
        }
        catch (ConnectionPropertiesException e) {
            throw new RuntimeException("Could not get, read or apply connection properties.", e);
        }
        catch (ConfigurationRuntimeException e) {
            throw new RuntimeException("Could not read or apply configuration.", e);
        }
    }

    public void contextDestroyed(ServletContextEvent sce)
    {
        _application.destroy();
    }
}
