package com.imcode.imcms.servlet.tags.Editor;

import com.google.common.base.Joiner;

/**
 * Created by Shadowgun on 12.01.2015.
 */
public class SupportEditor extends BaseEditor {
    public SupportEditor() {
        super(new SupportBuilder());
    }

    public String getWrapperPre() {
        try {
            return ((SupportBuilder) super.builder).buildPre();
        } finally {
            builder.clear();
        }
    }

    public String getWrapperPost() {
        return ((SupportBuilder) super.builder).buildPost();
    }

    protected static class SupportBuilder extends BaseEditor.BuilderBase {
        final String buildPre() {

            return String.format
                    (
                            "<div class=\"%s\" %s >",
                            Joiner.on(' ').join(classList),
                            stringBuilder.toString().trim()
                    );
        }

        final String buildPost() {
            return "</div>";
        }
    }
}
