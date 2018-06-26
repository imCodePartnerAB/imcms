package com.imcode.imcms.persistence.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Data
@Embeddable
public class PasswordReset {

    @NotNull
    @Column(name = "login_password_reset_id")
    private String id;

    @NotNull
    @Column(name = "login_password_reset_ts")
    private long timestamp;
}
