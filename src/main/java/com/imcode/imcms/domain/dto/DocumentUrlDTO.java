package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.DocumentURL;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class DocumentUrlDTO extends DocumentURL implements Cloneable {

    private static final long serialVersionUID = -7558203958715812408L;

    private Integer id;

    private String urlFrameName;

    private String urlTarget;

    private String url;

    private String urlText;

    private String urlLanguagePrefix;

    private Integer docId;

    public DocumentUrlDTO(DocumentURL from) {
        super(from);
        this.docId = from.getDocId();
    }

    public static DocumentUrlDTO createDefault() {
        final DocumentUrlDTO documentUrlDTO = new DocumentUrlDTO();

        documentUrlDTO.setUrlFrameName("");
        documentUrlDTO.setUrlTarget("");
        documentUrlDTO.setUrl("");
        documentUrlDTO.setUrlText("");
        documentUrlDTO.setUrlLanguagePrefix("");

        return documentUrlDTO;
    }

	public static DocumentUrlDTO createDefaultWithUrl(String url) {
		final DocumentUrlDTO documentUrlDTO = createDefault();

		documentUrlDTO.setUrl(url);

		return documentUrlDTO;
	}

    @Override
    public DocumentUrlDTO clone() {
        try {
            final DocumentUrlDTO cloneDocumentUrlDTO = (DocumentUrlDTO) super.clone();
            cloneDocumentUrlDTO.setId(null);
            cloneDocumentUrlDTO.setDocId(null);

            return cloneDocumentUrlDTO;
        } catch (CloneNotSupportedException e) {
            return null; // must not happened
        }
    }
}
