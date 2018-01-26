package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlDocumentDTO extends Document implements Serializable {

    private static final long serialVersionUID = -8053993136553115412L;

    private DocumentUrlDTO documentURL;

    {
        super.type = Meta.DocumentType.URL;
    }

    public UrlDocumentDTO(Document from) {
        super(from);
    }

    UrlDocumentDTO(UberDocumentDTO from) {
        this((Document) from);
        this.documentURL = from.getDocumentURL();
    }

    public static UrlDocumentDTO createEmpty(Document from) {
        final UrlDocumentDTO urlDocumentDTO = new UrlDocumentDTO(from);
        urlDocumentDTO.documentURL = DocumentUrlDTO.createDefault();

        return urlDocumentDTO;
    }
}
