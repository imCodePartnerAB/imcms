package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
public abstract class UserData extends BasicUserData implements Serializable {

    private static final long serialVersionUID = 2120543692994074940L;

    public UserData(UserData from) {
        super(from);
        setPassword(from.getPassword());
        setTitle(from.getTitle());
        setCompany(from.getCompany());
        setAddress(from.getAddress());
        setZip(from.getZip());
        setCity(from.getCity());
        setProvince(from.getProvince());
        setCountry(from.getCountry());
        setRef(from.getRef());
        setBlockedDate(from.getBlockedDate());
        setAttempts(from.getAttempts());
    }

    public abstract String getPassword();

    public abstract void setPassword(String password);

    public abstract String getTitle();

    public abstract void setTitle(String title);

    public abstract String getCompany();

    public abstract void setCompany(String company);

    public abstract String getAddress();

    public abstract void setAddress(String address);

    public abstract String getZip();

    public abstract void setZip(String zip);

    public abstract String getCity();

    public abstract void setCity(String city);

    public abstract String getProvince();

    public abstract void setProvince(String province);

    public abstract String getCountry();

    public abstract void setCountry(String country);

    public abstract String getRef();

    public abstract void setRef(String ref);

    public abstract boolean isActive();

    public abstract void setActive(boolean active);

    public abstract Date getBlockedDate();

    public abstract void setBlockedDate(Date blockedDate);

    public abstract Integer getAttempts();

    public abstract void setAttempts(Integer attempts);
}
