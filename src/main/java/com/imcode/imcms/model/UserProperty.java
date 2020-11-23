package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class UserProperty implements Serializable {

    private static final long serialVersionUID = 2649214048826330042L;

    public UserProperty(UserProperty userProperty) {
        setId(userProperty.getId());
        setUserId(userProperty.getUserId());
        setKeyName(userProperty.getKeyName());
        setValue(userProperty.getValue());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Integer getUserId();

    public abstract void setUserId(Integer userId);

    public abstract String getKeyName();

    public abstract void setKeyName(String keyName);

    public abstract String getValue();

    public abstract void setValue(String value);
}
