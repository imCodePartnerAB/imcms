package com.imcode.imcms.mapping.jpa.doc;

/**
 * Created by ajosua on 26/02/14.
 */
interface DocVersionRepositoryCustom {

    DocVersion create(int docId, int userId);

    void setDefault(int docId, int docVersionNo, int userId);
}
