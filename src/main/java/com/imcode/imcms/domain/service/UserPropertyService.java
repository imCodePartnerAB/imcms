package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.UserPropertyDTO;
import com.imcode.imcms.model.UserProperty;

import java.util.List;

public interface UserPropertyService {

    List<UserProperty> getAll();

    List<UserProperty> getByUserId(Integer userId);

    UserProperty getByUserIdAndKeyName(Integer userId, String keyName);

    void create(List<UserPropertyDTO> userProperties);

    UserProperty update(UserProperty userProperty);

    void deleteById(Integer id);

}
