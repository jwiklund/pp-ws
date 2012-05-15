package com.atex.plugins.wspluginmodel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.polopoly.model.Model;

@Provider
public class ModelWSWriter implements MessageBodyWriter<Model> {

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return Model.class.isAssignableFrom(type);
    }

    public long getSize(Model t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return -1;
    }

    public void writeTo(Model t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
    {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(entityStream));
        write(pw, t, 2);
        pw.flush();
    }

    private void write(PrintWriter pw, Model t, int depth)
    {
        if (depth <= 0) {
            pw.print("{\"expanded\": \"false\"}");
            return;
        }
        if (t == null) {
            pw.print("null");
        } else if (t.getModelType().isBasic()) {
            pw.print('"');
            pw.print(t.getModelType().exportToString(t));
            pw.print('"');
        } else if (t.getModelType().isArray()) {
            if (t instanceof List<?>) {
                List<?> l = (List<?>) t;
                pw.print('[');
                for (int i = 0 ; i < l.size() ; i++) {
                    if (i != 0) {
                        pw.print(", ");
                    }
                    Object o = l.get(i);
                    if (o == null) {
                        pw.print("null");
                    } else if (o instanceof Model) {
                        write(pw, (Model) o, depth - 1);
                    } else if (t.getModelType().getArrayType() != null) {
                        pw.write(t.getModelType().getArrayType().exportToString(o));
                    }
                }
                pw.print(']');
            } else {
                pw.print(String.format("\"unknown list type '%s' with model '%s'\"", t.getModelType(), t.getClass()));
            }
        } else if (t.getModelType().isCompound()) {
            pw.print('{');
            boolean first = true;
            for (String name : t.getAttributeNames()) {
                if (first) {
                    first = false;
                } else {
                    pw.print(", ");
                }
                pw.print(String.format("\"%s\": ", name));
                Object o = t.getAttribute(name);
                if (o == null) {
                    pw.print("null");
                } else if (o instanceof Model) {
                    write(pw, (Model) o, depth - 1);
                } else {
                    pw.write(String.format("\"%s\"", t.getModelType().getAttributeType(name).exportToString(o)));
                }
            }
            pw.print('}');
        } else {
            pw.print(String.format("\"unable to write json for model type '%s'\"", t.getModelType().getName()));
        }
    }
}
