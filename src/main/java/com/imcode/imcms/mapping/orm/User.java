package com.imcode.imcms.mapping.orm;

import com.google.common.base.Objects;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * JPA mapping (currently) does not replace or extend functionality provided by ImcmsAuthenticatorAndUserAndRoleMapper.
 * Must not be used for updates.
 */
@Entity
@Table(name = "users")
public class User {

    enum PasswordType {
        UNENCRYPTED, ENCRYPTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @NotNull
    @Column(name = "login_name")
    private String login;

    @Column(name = "login_password")
    private String password;

    @Column(name = "first_name")
    private String firstName = "";

    @Column(name = "last_name")
    private String lastName = "";

    private String title = "";
    private String company = "";
    private String address = "";
    private String city = "";
    private String zip = "";
    private String country = "";

    @Email @NotNull
    @Column(name = "email")
    private String email = "";
    private boolean active = true;

    @Column(name = "create_date")
    private Date createDate = new Date();

    @Column(name = "language")
    private String languageIso639_2;

    private boolean external;

    /**
     * Http session id.
     */
    @Column(name = "session_id")
    private String sessionId;

    @Enumerated
    @Column(name = "login_password_is_encrypted")
    private PasswordType passwordType = PasswordType.UNENCRYPTED;

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
        return Objects.equal(this.id, that.id)
                && Objects.equal(this.login, that.login)
                && Objects.equal(this.password, that.password)
                && Objects.equal(this.email, that.email)
                && Objects.equal(this.firstName, that.firstName)
                && Objects.equal(this.lastName, that.lastName)
                && Objects.equal(this.title, that.title)
                && Objects.equal(this.company, that.company)
                && Objects.equal(this.address, that.address)
                && Objects.equal(this.city, that.city)
                && Objects.equal(this.zip, that.zip)
                && Objects.equal(this.country, that.country)
                && Objects.equal(this.active, that.active)
                && Objects.equal(this.createDate, that.createDate)
                && Objects.equal(this.languageIso639_2, that.languageIso639_2)
                && Objects.equal(this.external, that.external)
                && Objects.equal(this.sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, login, password, email, firstName, lastName, title, company, address, city, zip,
                    country, active, createDate, languageIso639_2, external, sessionId);
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
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
