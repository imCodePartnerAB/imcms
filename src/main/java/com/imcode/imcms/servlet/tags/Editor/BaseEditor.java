package com.imcode.imcms.servlet.tags.Editor;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shadowgun on 24.12.2014.
 */
public abstract class BaseEditor {

    protected static class BuilderBase {
        protected static final String EDITOR_PREFIX = "editor-";
        protected static final String ATTR_PREFIX = "data-";
        protected StringBuilder stringBuilder;
        protected List<String> classList;

        BuilderBase() {
            stringBuilder = new StringBuilder();
            classList = new ArrayList<String>();
            addClass("base");
        }

        final BuilderBase addClass(String className) {
            className = className.contains(EDITOR_PREFIX) ? className : (EDITOR_PREFIX + className);
            classList.add(className);
            return this;
        }

        final BuilderBase addParam(String key, Object value, boolean prefix) {
            if (prefix)
                key = key.contains(ATTR_PREFIX) ? key : (ATTR_PREFIX + key);
            stringBuilder
                    .append(key)
                    .append('=')
                    .append('"')
                    .append(value)
                    .append('"')
                    .append(' ');
            return this;
        }

        final BuilderBase addParam(String key, Object value) {
            return addParam(key, value, true);
        }

        final String build(String content) {

            return String.format
                    (
                            "<div class=\"%s\" %s >%s</div>",
                            Joiner.on(' ').join(classList),
                            stringBuilder.toString().trim(),
                            content
                    );
        }

        final void clear() {
            stringBuilder = new StringBuilder();
            classList.clear();
        }
    }

    protected final BuilderBase builder;

    protected BaseEditor(BuilderBase builder) {
        this.builder = builder;
    }

    public BaseEditor() {
        this(new BuilderBase());
    }

    public String wrap(String content) {
        try {
            return builder.build(content);
        } finally {
            builder.clear();
        }
    }
}
