package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.UserData;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
@ToString(exclude = "password")
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends UserData implements Serializable {

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
    private String firstName;
    @NotNull
    @Column(name = "last_name")
    private String lastName;
    @NotNull
    private String title;
    @NotNull
    private String company;
    @NotNull
    private String address;
    @NotNull
    private String city;
    @NotNull
    private String zip;
    @NotNull
    private String country;
    @Column(name = "county_council")
    private String province;
    @Email
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "ref")
    private String ref;
    @Column(columnDefinition = "int default 1")
    private boolean active;
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "blocked_date")
    private Date blockedDate; //date when user was blocked

    @Column(name = "amount_attempts", nullable = false)
    private Integer attempts; // count possible attempts log in again

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "login_date")
    private Date lastLoginDate; //date when user was accessed login to system
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

	@Column(name = "one_time_password")
	private String oneTimePassword;

	@Column(name = "2fa_enabled", unique = true)
	private boolean twoFactoryAuthenticationEnabled;

    public User(String login, String password, String email) {
        this(null, login, password, email);
    }

    public User(Integer id, String login, String password, String email) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
        this.active = true;
        this.firstName = "";
        this.lastName = "";
        this.title = "";
        this.company = "";
        this.address = "";
        this.city = "";
        this.zip = "";
        this.country = "";
        this.province = "";
        this.ref = "";
        this.attempts = 0;
    }

    public User(UserData from) {
        super(from);
    }

    public void setLogin(String login) {
        this.login = (login == null) ? "" : login.trim();
    }


    public enum PasswordType {
        UNENCRYPTED, ENCRYPTED
    }

}
