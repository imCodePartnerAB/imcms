package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {

    private static final long serialVersionUID = -1878691076340113546L;

    private int id;

    private String loginName;

    private String firstName;

    private String lastName;

    private String email;

    public UserDTO(User from) {
        id = from.getId();
        loginName = from.getLogin();
        firstName = from.getFirstName();
        lastName = from.getLastName();
        email = from.getEmail();
    }
}
