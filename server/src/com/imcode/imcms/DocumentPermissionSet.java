package com.imcode.imcms;

import imcode.server.IMCConstants;

interface DocumentPermissionSet {
    public final static Integer FULL = new Integer( IMCConstants.DOC_PERM_SET_FULL );
    public final static Integer READ = new Integer( IMCConstants.DOC_PERM_SET_READ );
    public final static Integer RESTRICTED_1 = new Integer( IMCConstants.DOC_PERM_SET_RESTRICTED_1 );
    public final static Integer RESTRICTED_2 = new Integer( IMCConstants.DOC_PERM_SET_RESTRICTED_2 );
}