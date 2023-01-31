package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.User;
import imcode.server.user.PhoneNumber;

import java.util.Set;

public class UserGDPRDataDTO{

    private Integer id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String title;
    private String company;
    private String address;
    private String city;
    private String zip;
    private String country;
    private String province;
    private String email;
    private String ref;
    private boolean active;
    private String languageIso639_2;
    private boolean twoFactoryAuthenticationEnabled;
    private Set<Integer> roles;
    private Set<PhoneNumber> phoneNumbers;

    public UserGDPRDataDTO(User user){
        this.id = user.getId();
        this.login = user.getLogin();
        this.password = user.getPassword();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.title = user.getTitle();
        this.company = user.getCompany();
        this.address = user.getAddress();
        this.city = user.getCity();
        this.zip = user.getZip();
        this.country = user.getCountry();
        this.province = user.getProvince();
        this.email = user.getEmail();
        this.ref = user.getRef();
        this.active = user.isActive();
        this.languageIso639_2 = user.getLanguageIso639_2();
        this.twoFactoryAuthenticationEnabled = user.isTwoFactoryAuthenticationEnabled();
    }

    public UserGDPRDataDTO(User user, Set<Integer> roles, Set<PhoneNumber> phoneNumbers){
        this(user);
        this.roles = roles;
        this.phoneNumbers = phoneNumbers;
    }

}
