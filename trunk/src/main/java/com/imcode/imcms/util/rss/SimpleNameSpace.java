package com.imcode.imcms.util.rss;

public class SimpleNameSpace extends NameSpace {

    private String nameSpaceUri;
    private String suggestedPrefix;

    public SimpleNameSpace(String suggestedPrefix, String nameSpaceUri) {
        this.suggestedPrefix = suggestedPrefix;
        this.nameSpaceUri = nameSpaceUri;
    }

    public String getNameSpaceUri() {
        return nameSpaceUri;
    }

    public String getSuggestedPrefix() {
        return suggestedPrefix;
    }
}
