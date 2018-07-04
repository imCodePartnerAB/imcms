package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class PhoneTypeBase implements PhoneType {

    private static final long serialVersionUID = 5808867962893542942L;

    public PhoneTypeBase(PhoneType from) {
        setId(from.getId());
        setName(from.getName());
    }

}
