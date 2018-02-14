package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DocumentDTO extends Document implements Serializable {

    protected static final long serialVersionUID = -1197329246115859534L;

    public DocumentDTO(Document from) {
        super(from);
    }

}
