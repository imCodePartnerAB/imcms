package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.PhoneType;
import com.imcode.imcms.model.PhoneTypeBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PhoneTypeDTO extends PhoneTypeBase {

    private static final long serialVersionUID = 4093474918341601640L;

    private Integer id;

    private String name;

    public PhoneTypeDTO(PhoneType from) {
        super(from);
    }

}
