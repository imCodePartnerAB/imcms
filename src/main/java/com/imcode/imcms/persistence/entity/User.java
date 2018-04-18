package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * {@see ImcmsAuthenticatorAndUserAndRoleMapper}
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
@ToString(exclude = "password")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User implements Serializable {

    private static final long serialVersionUID = 5707282362269284484L;

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
    @Column(columnDefinition = "int")
    private boolean active = true;
    @NotNull
    @Column(name = "create_date")
    private Date createDate = new Date();
    @NotNull
    @Column(name = "language")
    private String languageIso639_2 = "";
    @NotNull
    @Column(columnDefinition = "int")
    private boolean external;
    @Column(name = "remember_cd")
    private String rememberCd;
    /**
     * Http session id.
     */
    @Column(name = "session_id")
    private String sessionId;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "login_password_is_encrypted", columnDefinition = "bit")
    private PasswordType passwordType = PasswordType.UNENCRYPTED;
    @Embedded
    private PasswordReset passwordReset;

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

    public void setLogin(String login) {
        this.login = (login == null) ? null : login.trim();
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
