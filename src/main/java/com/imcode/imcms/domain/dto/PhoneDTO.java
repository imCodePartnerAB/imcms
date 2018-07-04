package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Phone;
import com.imcode.imcms.model.PhoneType;
import com.imcode.imcms.persistence.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PhoneDTO extends Phone {

    private static final long serialVersionUID = -2580417412533125169L;

    private Integer phoneId;

    private String number;

    private User user;

    private PhoneTypeDTO phoneType;

    public PhoneDTO(Phone from) {
        super(from);
    }

    public PhoneDTO(String number, User user, PhoneType phoneType) {
        super(number, user, phoneType);

    }

    @Override
    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = new PhoneTypeDTO(phoneType);
    }
}
