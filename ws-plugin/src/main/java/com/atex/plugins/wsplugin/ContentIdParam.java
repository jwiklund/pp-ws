package com.atex.plugins.wsplugin;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentIdFactory;
import com.polopoly.cm.ExternalContentId;

public class ContentIdParam {
    public final ContentId param;
    public final String origin;

    public ContentIdParam(ContentId param, String origin) {
        this.param = param;
        this.origin = origin;
    }

    public static ContentIdParam valueOf(String value) {
        if (value.matches("\\d+\\.\\d+(\\.\\d+)")) {
            return new ContentIdParam(ContentIdFactory.createContentId(value), value);
        }
        return new ContentIdParam(new ExternalContentId(value), value);
    }
}
