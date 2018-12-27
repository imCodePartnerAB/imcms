package com.imcode.imcms.api;

import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import lombok.Data;

@Data
public class ValidationLink implements Cloneable {

    private boolean pageFound;
    private boolean hostFound;
    private boolean hostReachable;
    private String url;
    private EditLink editLink;
    private DocumentStoredFieldsDTO documentData;
    private LinkType linkType;
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public enum LinkType {
        TEXT(),
        IMAGE(),
        URL()
    }
}
