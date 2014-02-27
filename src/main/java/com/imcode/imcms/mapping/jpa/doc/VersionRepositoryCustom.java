package com.imcode.imcms.mapping.jpa.doc;

/**
 * Created by ajosua on 26/02/14.
 */
interface VersionRepositoryCustom {

    DocVersion create(int docId, int userId);

    void setDefault(int docId, int docVersionNo, int userId);
}
