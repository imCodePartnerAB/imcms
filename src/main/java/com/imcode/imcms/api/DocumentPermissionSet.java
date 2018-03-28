package com.imcode.imcms.api;

@Deprecated
public class DocumentPermissionSet {

    private final boolean canEditText;

    @Deprecated
    public DocumentPermissionSet(boolean canEditText) {
        this.canEditText = canEditText;
    }

    @Deprecated
    public boolean getEditTextsPermission() {
        return canEditText;
    }
}
