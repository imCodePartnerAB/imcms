package com.imcode.imcms.mapping.orm;

import com.google.common.base.Objects;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "login_name")
    private String login;

    @Column(name = "login_password")
    private String password;

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof User && (equals((User) o)));
    }

    private boolean equals(User that) {
        return Objects.equal(this.id, that.id)
                && Objects.equal(this.login, that.login)
                && Objects.equal(this.password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, login, password);
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
