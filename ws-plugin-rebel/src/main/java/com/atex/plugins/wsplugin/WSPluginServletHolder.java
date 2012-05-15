package com.atex.plugins.wsplugin;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WSPluginServletHolder {
    Object wsservlet;
    long cachedChange = -1;
    Set<String> classes = Collections.emptySet();

    private Field classChangeField;
    private Field changeField;
    private Field classesField;
    private Field singletonsField;

    public WSPluginServletHolder(Object wsservlet) {
        this.wsservlet = wsservlet;
    }

    public void markReload() {
        try {
            classChangeField.setBoolean(wsservlet, true);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadCachedClassNamesIfNeeded() {
        if (cachedChange == -1) {
            try {
                classChangeField = wsservlet.getClass().getField("generated$classChange");
                changeField = wsservlet.getClass().getField("generated$lastVersion");
                classesField = wsservlet.getClass().getField("classes");
                singletonsField = wsservlet.getClass().getField("singletons");
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            reloadCachedClassNames();
        }
        else if (cachedChange < getChange()) {
            reloadCachedClassNames();
        }
    }

    private void reloadCachedClassNames() {
        cachedChange = getChange();
        Set<String> classes = new HashSet<String>();
        for (Class<?> clazz : getClasses()) {
            classes.add(clazz.getName());
        }
        for (Object o : getSingletons()) {
            classes.add(o.getClass().getName());
        }
        this.classes = classes;
    }

    @SuppressWarnings("unchecked")
    private Set<Object> getSingletons()
    {
        try {
            return (Set<Object>) singletonsField.get(wsservlet);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getClasses()
    {
        try {
            return (Set<Class<?>>) classesField.get(wsservlet);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private long getChange()
    {
        try {
            return changeField.getLong(wsservlet);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((wsservlet == null) ? 0 : wsservlet.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WSPluginServletHolder other = (WSPluginServletHolder) obj;
        if (wsservlet == null) {
            if (other.wsservlet != null)
                return false;
        } else if (!wsservlet.equals(other.wsservlet))
            return false;
        return true;
    }
}