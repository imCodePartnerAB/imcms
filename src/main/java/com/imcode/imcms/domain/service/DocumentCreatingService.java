package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Document;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.01.18.
 */
public interface DocumentCreatingService<D extends Document> {

    D createFromParent(Integer parentDocId);

}
