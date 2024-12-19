package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.UserData;
import com.imcode.imcms.persistence.entity.User;
import imcode.server.LanguageMapper;
import imcode.server.user.PhoneNumber;
import imcode.server.user.UserDomainObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private boolean external;
	private String oneTimePassword;
	private boolean twoFactoryAuthenticationEnabled;
    private Date blockedDate; //date when user was blocked
    private Integer attempts;
    private Date lastLoginDate;
    private boolean flagOfBlocking; // check unblocking user from the GUI or API..
    private PasswordResetDTO passwordReset;

    public UserFormData(UserData from) {
        super(from);
    }

    public UserFormData(User from) {
        super(from);
        this.setCreateDate(from.getCreateDate());
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

        List<PhoneNumber> localPhones = new ArrayList<>(from.getPhoneNumbers());
        Integer[] localUserPhoneNumberType = new Integer[localPhones.size()];
        String[] localUserPhoneNumber = new String[localPhones.size()];
        for(int i = 0; i<localPhones.size(); i++){
            localUserPhoneNumberType[i] = localPhones.get(i).getType().getId();
            localUserPhoneNumber[i] = localPhones.get(i).getNumber();
        }
        setUserPhoneNumber(localUserPhoneNumber);
        setUserPhoneNumberType(localUserPhoneNumberType);

        setPasswordReset(from.getPasswordReset());
    }
}
