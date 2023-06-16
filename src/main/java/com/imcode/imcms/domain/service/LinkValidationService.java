package com.imcode.imcms.domain.service;

import com.imcode.imcms.api.ValidationLink;

import java.util.List;
import java.util.regex.Pattern;

public interface LinkValidationService {

    List<ValidationLink> validateDocumentsLinks(int startDocumentId, int endDocumentId, boolean onlyBrokenLinks);

    /**
     * @param url check if provided url external with default pattern url
     * @return true if external, false if internal, null if url not matches pattern
     */
    Boolean isExternal(String url);

    /**
     * @param url url to test
     * @param pattern pattern to test url
     * @return true if external, false if internal, null if url not matches pattern
     */
    Boolean isExternal(String url, Pattern pattern);
}
