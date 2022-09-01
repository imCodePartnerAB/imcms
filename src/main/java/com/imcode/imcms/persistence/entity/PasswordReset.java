package com.imcode.imcms.persistence.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PasswordReset {

    @NotNull
    @Column(name = "login_password_reset_id")
    private String id;

    @NotNull
    @Column(name = "login_password_reset_ts")
    private long timestamp;
}
