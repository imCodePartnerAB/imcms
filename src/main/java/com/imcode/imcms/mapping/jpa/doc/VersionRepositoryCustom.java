package com.imcode.imcms.mapping.jpa.doc;

interface VersionRepositoryCustom {

    Version create(int docId, int userId);

    void setDefault(int docId, int docVersionNo, int userId);
}
