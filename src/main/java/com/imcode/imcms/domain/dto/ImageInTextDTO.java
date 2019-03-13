package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * ImageJPA object used in Text Editor. Always has <b>negative</b> {@code index}.
 *
 * @author Serhii from Ubrainians for imCode
 * 13.02.2018.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageInTextDTO extends ImageDTO {
    private static final long serialVersionUID = -2870453434281137390L;

    {
        inText = true;
    }

    public ImageInTextDTO(ImageDTO dataHolder) {
        super(dataHolder);
    }
}
