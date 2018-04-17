package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ProfileDTO extends Profile {

    private static final long serialVersionUID = 6781576769488287699L;

    private String documentName;
    private String name;
    private Integer id;

    public ProfileDTO(Profile from) {
        super(from);
    }
}
