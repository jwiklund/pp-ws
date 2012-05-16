package com.atex.plugins.wspluginmodel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.json.simple.JSONObject;

import com.polopoly.cm.client.impl.ServiceUtil;
import com.polopoly.service.cm.api.ContentId;
import com.polopoly.service.cm.api.content.ContentDataRead;

@Provider
public class ServiceWSWriter implements MessageBodyWriter<ContentDataRead> {
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return ContentDataRead.class.isAssignableFrom(type);
    }

    public long getSize(ContentDataRead t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return -1;
    }

    public void writeTo(ContentDataRead t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
    {
        OutputStreamWriter stream = new OutputStreamWriter(entityStream);
        JSONObject.writeJSONString(toMap(t), stream);
        stream.flush();
    }

    private Map<String, Map<String, Map<String, String>>> toMap(ContentDataRead t)
    {
        Map<String, Map<String, Map<String, String>>> result = new HashMap<String, Map<String,Map<String,String>>>();
        Map<String, Map<String, String>> components = new HashMap<String, Map<String,String>>();
        for (String group : t.getComponentGroupNames()) {
            Map<String, String> groupMap = components.get(group);
            if (groupMap == null) {
                groupMap = new HashMap<String, String>();
                components.put(group, groupMap);
            }
            for (String name : t.getComponentNames(group)) {
                groupMap.put(name, t.getComponent(group, name));
            }
        }
        result.put("components", components);
        Map<String, Map<String, String>> references = new HashMap<String, Map<String,String>>();
        for (String group : t.getContentReferenceGroupNames()) {
            Map<String, String> groupMap = references.get(group);
            if (groupMap == null) {
                groupMap = new HashMap<String, String>();
                references.put(group, groupMap);
            }
            for (String name : t.getContentReferenceNames(group)) {
                ContentId ref = t.getContentReference(group, name);
                groupMap.put(name, ServiceUtil.convertContentId(ref).getContentIdString());
            }
        }
        result.put("references", references);
        return result;
    }
}
