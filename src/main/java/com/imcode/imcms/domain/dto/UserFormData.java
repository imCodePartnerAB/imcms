package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.UserData;
import com.imcode.imcms.persistence.entity.User;
import imcode.server.LanguageMapper;
import imcode.server.user.UserDomainObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"password", "password2"}, callSuper = true)
public class UserFormData extends UserData {

    private static final long serialVersionUID = 4260651400624470608L;

    private Integer id;
    private String login;
    private String password;
    private String password2;
    private String firstName;
    private String lastName;
    private String title;
    private String company;
    private String address;
    private String zip;
    private String city;
    private String province;
    private String country;
    private String langCode;
    private String email;
    private String ref;
    private boolean active;
    private Date createDate;
    private Integer[] userPhoneNumberType;
    private String[] userPhoneNumber;
    private int[] roleIds;
    private int[] userAdminRoleIds;
    private boolean external;

    public UserFormData(UserData from) {
        super(from);
    }

    public UserFormData(User from) {
        super(from);
        this.setCreateDate(from.getCreateDate());
        this.setActive(from.isActive());
        this.setLangCode(LanguageMapper.convert639_2to639_1(from.getLanguageIso639_2()));
    }

    public UserFormData(UserDomainObject from) {
        super(from);

        if (from.getId() == 0) {
            setId(null);
        }

        setExternal(from.isImcmsExternal());
        setLangCode(from.getLanguage());

        final int[] roles = from.getRoleIds().stream().mapToInt(Integer::intValue).toArray();

        setRoleIds(roles);
    }
}
