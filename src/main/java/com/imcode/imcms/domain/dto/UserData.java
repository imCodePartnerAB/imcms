package com.imcode.imcms.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString(exclude = {"password1", "password2"})
public class UserData {

    private String loginName;
    private String password1;
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
    private Integer[] userPhoneNumberType;
    private String[] userPhoneNumber;
    private String email;
    private boolean active;
    private int[] roleIds;
    private int[] userAdminRoleIds;

}
