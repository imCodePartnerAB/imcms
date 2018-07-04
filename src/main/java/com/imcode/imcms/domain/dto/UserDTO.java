package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.BasicUserData;
import com.imcode.imcms.persistence.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class UserDTO extends BasicUserData {

    private static final long serialVersionUID = -1878691076340113546L;

    private int id;

    private String login;

    private String firstName;

    private String lastName;

    private String email;

    public UserDTO(User from) {
        super(from);
        id = from.getId();
    }
}
