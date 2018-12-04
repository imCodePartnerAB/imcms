package com.imcode.imcms.api;

import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import lombok.Data;

@Data
public class ValidationLink {

    private boolean pageFound;
    private boolean hostFound;
    private boolean hostReachable;
    private DocumentStoredFieldsDTO documentData;
}
