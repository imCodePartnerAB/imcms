package com.imcode.imcms.mapping.jpa.doc;

interface VersionRepositoryCustom {

    Version create(int docId, int userId);
}
