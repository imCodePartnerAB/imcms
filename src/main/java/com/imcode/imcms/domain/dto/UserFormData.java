package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.UserData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"password", "password2"}, callSuper = true)
public class UserFormData extends UserData {

    private static final long serialVersionUID = 4260651400624470608L;

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
    private boolean active;
    private Integer[] userPhoneNumberType;
    private String[] userPhoneNumber;
    private int[] roleIds;
    private int[] userAdminRoleIds;

}
