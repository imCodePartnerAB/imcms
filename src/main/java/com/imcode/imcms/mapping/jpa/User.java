package com.imcode.imcms.mapping.jpa;

import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

/**
 * {@see ImcmsAuthenticatorAndUserAndRoleMapper}
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;
    @NotNull
    @Column(name = "login_name")
    private String login;
    @NotNull
    @Column(name = "login_password")
    private String password;
    @NotNull
    @Column(name = "first_name")
    private String firstName = "";
    @NotNull
    @Column(name = "last_name")
    private String lastName = "";
    @NotNull
    private String title = "";
    @NotNull
    private String company = "";
    @NotNull
    private String address = "";
    @NotNull
    private String city = "";
    @NotNull
    private String zip = "";
    @NotNull
    private String country = "";
    @Column(name = "county_council")
    private String province = "";
    @Email
    @NotNull
    @Column(name = "email")
    private String email = "";
    private boolean active = true;
    @NotNull
    @Column(name = "create_date")
    private Date createDate = new Date();
    @NotNull
    @Column(name = "language")
    private String languageIso639_2 = "";
    @NotNull
    private boolean external;
    @Column(name = "remember_cd")
    private String rememberCd;
    /**
     * Http session id.
     */
    @Column(name = "session_id")
    private String sessionId;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "login_password_is_encrypted")
    private PasswordType passwordType = PasswordType.UNENCRYPTED;
    @Embedded
    private PasswordReset passwordReset;

    public User() {
    }

    public User(String login, String password, String email) {
        this(null, login, password, email);
    }

    public User(Integer id, String login, String password, String email) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof User && (equals((User) o)));
    }

    private boolean equals(User that) {
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.login, that.login)
                && Objects.equals(this.password, that.password)
                && Objects.equals(this.email, that.email)
                && Objects.equals(this.firstName, that.firstName)
                && Objects.equals(this.lastName, that.lastName)
                && Objects.equals(this.title, that.title)
                && Objects.equals(this.company, that.company)
                && Objects.equals(this.address, that.address)
                && Objects.equals(this.city, that.city)
                && Objects.equals(this.zip, that.zip)
                && Objects.equals(this.country, that.country)
                && Objects.equals(this.province, that.province)
                && Objects.equals(this.active, that.active)
                && Objects.equals(this.createDate, that.createDate)
                && Objects.equals(this.languageIso639_2, that.languageIso639_2)
                && Objects.equals(this.external, that.external)
                && Objects.equals(this.sessionId, that.sessionId)
                && Objects.equals(this.passwordType, that.passwordType)
                && Objects.equals(this.passwordReset, that.passwordReset)
                && Objects.equals(this.rememberCd, that.rememberCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, email, firstName, lastName, title, company, address, city, zip,
                country, active, createDate, languageIso639_2, external, sessionId, passwordType, passwordReset,
                rememberCd);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = (login == null) ? null : login.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = (password == null) ? null : password.trim();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getLanguageIso639_2() {
        return languageIso639_2;
    }

    public void setLanguageIso639_2(String languageIso639_2) {
        this.languageIso639_2 = languageIso639_2;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public PasswordType getPasswordType() {
        return passwordType;
    }

    public void setPasswordType(PasswordType passwordType) {
        this.passwordType = passwordType;
    }

    public PasswordReset getPasswordReset() {
        return passwordReset;
    }

    public void setPasswordReset(PasswordReset passwordReset) {
        this.passwordReset = passwordReset;
    }

    public enum PasswordType {
        UNENCRYPTED, ENCRYPTED
    }

    @Embeddable
    public static class PasswordReset {

        @NotNull
        @Column(name = "login_password_reset_id")
        private String id;

        @NotNull
        @Column(name = "login_password_reset_ts")
        private long timestamp;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, timestamp);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof PasswordReset && equals((PasswordReset) obj));
        }

        private boolean equals(PasswordReset that) {
            return Objects.equals(this.id, that.id) && Objects.equals(this.timestamp, that.timestamp);
        }
    }
}
