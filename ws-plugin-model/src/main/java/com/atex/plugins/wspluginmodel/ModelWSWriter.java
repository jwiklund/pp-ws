package com.atex.plugins.wspluginmodel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.polopoly.cm.app.policy.ContentListLazyModel;
import com.polopoly.cm.app.policy.ContentListProviderListBased;
import com.polopoly.model.Model;
import com.polopoly.model.ModelTypeCompound;
import com.polopoly.model.ModelTypeFieldData;
import com.polopoly.model.ModelTypeFieldDataProvider;

@Provider
public class ModelWSWriter implements MessageBodyWriter<Model> {

    @Context
    private UriInfo info;

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
        MultivaluedMap<String, String> params = info.getQueryParameters();
        boolean pretty = params.containsKey("pretty");
        int depth = 0;
        if (params.containsKey("depth")) {
            String d = params.getFirst("depth");
            if (d.matches("\\d+")) {
                depth = Integer.valueOf(d);
            }
        }
        write(pw, t, pretty, depth + 1, 0);
        pw.flush();
    }

    private void write(PrintWriter pw, Model t, boolean pretty, int depth, int indent)
    {
        if (depth <= 0) {
            pw.print("{\"expanded\": \"false\"}");
            return;
        }
        if (t == null) {
            pw.print("null");
        } else if (t.getModelType() == null) {
            pw.print('{');
            boolean first = true;
            for (String name : t.getAttributeNames()) {
                if (first) {
                    first = false;
                } else {
                    pw.print(",");
                }
                if (pretty) {
                    pw.print("\n");
                    pw.print(indent(indent + 1));
                }
                pw.print(String.format("\"%s\": ", name));
                Object o = t.getAttribute(name);
                if (o == null) {
                    pw.print("null");
                } else if (o instanceof Model) {
                    write(pw, (Model) o, pretty, depth - 1, indent + 1);
                } else {
                    pw.print(String.format("\"unknown type '%s'\"", o.getClass()));
                }
            }
            if (pretty) {
                pw.print("\n");
                pw.print(indent(indent));
            }
            pw.print('}');
        } else if (t.getModelType().isBasic()) {
            pw.print('"');
            pw.print(t.getModelType().exportToString(t));
            pw.print('"');
        } else if (t.getModelType().isReference()) {
            pw.print(String.format("{ \"contentId\": \"%s\"", "reference"));
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
                        write(pw, (Model) o, pretty, depth - 1, indent);
                    } else if (t.getModelType().getArrayType() != null) {
                        pw.print('"');
                        pw.write(t.getModelType().getArrayType().exportToString(o));
                        pw.print('"');
                    } else {
                        pw.print(String.format("\"unknown type '%s'\"", o.getClass()));
                    }
                }
                pw.print(']');
            } else {
                pw.print(String.format("\"unknown list type '%s' with model '%s'\"", t.getModelType(), t.getClass()));
            }
        } else if (t.getModelType().isCompound()) {
            pw.print('{');
            boolean first = true;
            ModelTypeFieldDataProvider p = null;
            if (t.getModelType() instanceof ModelTypeFieldDataProvider) {
                p = (ModelTypeFieldDataProvider) t.getModelType();
            }
            for (String name : t.getAttributeNames()) {
                if (p != null) {
                    ModelTypeFieldData mfd = p.getModelTypeFieldData(name);
                    if (mfd.isStatic() || mfd.isTransient()) {
                        continue;
                    }
                }
                if (first) {
                    first = false;
                } else {
                    pw.print(",");
                }
                if (pretty) {
                    pw.print("\n");
                    pw.print(indent(indent + 1));
                }
                pw.print(String.format("\"%s\": ", name));
                Object o = t.getAttribute(name);
                if (o == null) {
                    pw.print("null");
                } else if (o instanceof Model) {
                    write(pw, (Model) o, pretty, depth - 1, indent + 1);
                } else if (o instanceof ContentListProviderListBased) {
                    // Hacketiy
                    if (depth > 1) {
                        List<?> l = (ContentListProviderListBased) o;
                        pw.print('[');
                        for (int i = 0 ; i < l.size() ; i++) {
                            if (i != 0) {
                                pw.print(", ");
                            }
                            Object r = l.get(i);
                            if (r == null) {
                                pw.print("null");
                            } else if (r instanceof Model) {
                                write(pw, (Model) r, pretty, depth - 1, indent + 1);
                            } else if (t.getModelType().getArrayType() != null) {
                                pw.print('"');
                                pw.write(t.getModelType().getArrayType().exportToString(o));
                                pw.print('"');
                            } else {
                                pw.print(String.format("\"unknown type '%s'\"", o.getClass()));
                            }
                        }
                        pw.print(']');
                    } else {
                        pw.print("[{\"expanded\": \"false\"}]");
                    }
                } else {
                    pw.write(String.format("\"%s\"", t.getModelType().getAttributeType(name).exportToString(o)));
                }
            }
            if (pretty) {
                pw.print("\n");
                pw.print(indent(indent));
            }
            pw.print('}');
        } else {
            pw.print(String.format("\"unable to write json for model type '%s'\"", t.getModelType().getName()));
        }
    }

    private char[] indent(int length)
    {
        char[] res = new char[length * 2];
        for (int i = 0 ; i < res.length ; i++) {
            res[i] = ' ';
        }
        return res;
    }
}
