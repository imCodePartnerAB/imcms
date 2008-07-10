package com.imcode.imcms.util.rss;

public abstract class NameSpace {
    public abstract String getNameSpaceUri();
    public abstract String getSuggestedPrefix();

    public boolean equals(Object obj) {
        if (!( obj instanceof NameSpace)) {
            return false;
        }
        NameSpace nameSpace = (NameSpace) obj;
        return getNameSpaceUri().equals(nameSpace.getNameSpaceUri());
    }

    public int hashCode() {
        return getNameSpaceUri().hashCode();
    }
}
