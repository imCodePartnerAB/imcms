package com.imcode.imcms.api;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

/**
 * Parametrized callback for DocumentMapper#getDocument method.
 * A callback is (re)created on each request and (re)assigned to a user.
 *
 * Default doc callback always returns default version of any doc if it is present and the user has at least 'view' rights on it.
 *
 * Working and Custom doc callback return working and custom version of a document with particular id;
 * for other doc ids they behave exactly as default doc callback.
 *
 * @see imcode.server.Imcms
 * @see com.imcode.imcms.servlet.ImcmsSetupFilter
 * @see com.imcode.imcms.mapping.DocumentMapper#getDocument(int)
 */

// scala
public interface DocGetterCallback {

    DocumentLanguages documentLanguages();

    <T extends DocumentDomainObject> T getDoc(int docId, UserDomainObject user, DocumentMapper documentMapper);

    DocGetterCallback copy(DocumentLanguages documentLanguages);
}
