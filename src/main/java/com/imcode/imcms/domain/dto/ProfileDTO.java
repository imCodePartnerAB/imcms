package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO extends Profile {

    private String documentName;
    private String name;
    private Integer id;

    public ProfileDTO(Profile from) {
        super(from);
    }
}
