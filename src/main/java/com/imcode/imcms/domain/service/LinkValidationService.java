package com.imcode.imcms.domain.service;

import com.imcode.imcms.api.ValidationLink;

import java.util.List;

public interface LinkValidationService {

    List<ValidationLink> validateDocumentsLinks(int startDocumentId, int endDocumentId, boolean onlyBrokenLinks);

}
