package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class BasicUserData implements Serializable {

    private static final long serialVersionUID = -1000623692760863774L;

    public BasicUserData(BasicUserData from) {
        setId(from.getId());
        setLogin(from.getLogin());
        setFirstName(from.getFirstName());
        setLastName(from.getLastName());
        setEmail(from.getEmail());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getLogin();

    public abstract void setLogin(String login);

    public abstract String getFirstName();

    public abstract void setFirstName(String firstName);

    public abstract String getLastName();

    public abstract void setLastName(String lastName);

    public abstract String getEmail();

    public abstract void setEmail(String email);
}
