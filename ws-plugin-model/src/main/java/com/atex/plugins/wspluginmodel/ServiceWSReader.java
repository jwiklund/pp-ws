package com.atex.plugins.wspluginmodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.json.simple.JSONValue;

import com.polopoly.cm.ContentIdFactory;
import com.polopoly.cm.client.impl.ServiceUtil;
import com.polopoly.service.cm.api.content.ContentDataRead;
import com.polopoly.service.cm.standard.content.StdContentData;

@Provider
public class ServiceWSReader implements MessageBodyReader<ContentDataRead>
{

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return type.isAssignableFrom(ContentDataRead.class);
    }

    public ContentDataRead readFrom(Class<ContentDataRead> type, Type genericType, Annotation[] annotations, MediaType mediaType,
        MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
    {
        Object result = JSONValue.parse(new InputStreamReader(entityStream));
        if (result instanceof Map<?, ?>) {
            return fromMap((Map<?, ?>) result);
        }
        throw new WebApplicationException(Status.BAD_REQUEST);
    }

    private ContentDataRead fromMap(Map<?, ?> result)
    {
        StdContentData data = new StdContentData();
        Object unknownAllComponents = result.get("components");
        if (unknownAllComponents instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, ?> groups = (Map<String, ?>) unknownAllComponents;
            for (String group : groups.keySet()) {
                Object unknownComponents = groups.get(group);
                if (unknownComponents instanceof Map<?, ?>) {
                    @SuppressWarnings("unchecked")
                    Map<String, ?> components = (Map<String, ?>) unknownComponents;
                    for (String name : components.keySet()) {
                        Object unknownValue = components.get(name);
                        if (unknownValue instanceof String) {
                            data.setComponent(group, name, (String) unknownValue);
                        }
                    }
                }
            }
        }
        Object unknownAllReferences = result.get("references");
        if (unknownAllReferences instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, ?> groups = (Map<String, ?>) unknownAllReferences;
            for (String group : groups.keySet()) {
                Object unknownReferences = groups.get(group);
                if (unknownReferences instanceof Map<?, ?>) {
                    @SuppressWarnings("unchecked")
                    Map<String, ?> references = (Map<String, ?>) unknownReferences;
                    for (String name : references.keySet()) {
                        Object unknownValue = references.get(name);
                        if (unknownValue instanceof String) {
                            data.setContentReference(group, name, ServiceUtil.convertContentId(ContentIdFactory.createContentId((String) unknownValue)));
                        }
                    }
                }
            }
        }
        return data;
    }
}
