package com.atex.plugins.wsplugin;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.polopoly.application.Application;
import com.polopoly.application.servlet.ApplicationServletUtil;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.EjbCmClient;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.UserId;

public class TestFilter implements Filter
{
    private CmClient client;

    public void init(FilterConfig filterConfig) throws ServletException
    {
        Application application = ApplicationServletUtil.getApplication(filterConfig.getServletContext());
        client = (CmClient) application.getApplicationComponent(EjbCmClient.DEFAULT_COMPOUND_NAME);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        try {
            client.getPolicyCMServer().setCurrentCaller(new Caller(new UserId("sysadmin")));
            chain.doFilter(request, response);
        } finally {
            client.getPolicyCMServer().setCurrentCaller(null);
        }
    }

    public void destroy()
    {
    }
}
