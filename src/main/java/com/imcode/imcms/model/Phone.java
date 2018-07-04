package com.imcode.imcms.model;

import com.imcode.imcms.persistence.entity.User;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class Phone implements Serializable {

    private static final long serialVersionUID = -6783249736220792503L;

    public Phone(String number, User user, PhoneType phoneType) {
        setNumber(number);
        setUser(user);
        setPhoneType(phoneType);
    }

    public Phone(Phone from) {
        setNumber(from.getNumber());
        setPhoneId(from.getPhoneId());
        setPhoneType(from.getPhoneType());
        setUser(from.getUser());
    }

    public abstract Integer getPhoneId();

    public abstract void setPhoneId(Integer phoneId);

    public abstract String getNumber();

    public abstract void setNumber(String number);

    public abstract User getUser();

    public abstract void setUser(User user);

    public abstract PhoneType getPhoneType();

    public abstract void setPhoneType(PhoneType phoneType);

}
