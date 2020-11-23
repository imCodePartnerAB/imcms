package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.UserProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserPropertyDTO extends UserProperty{

    private static final long serialVersionUID = 1150190095438672681L;

    private Integer id;

    private Integer userId;

    private String keyName;

    private String value;

    public UserPropertyDTO(UserProperty userProperty) {
        super(userProperty);
    }

}
