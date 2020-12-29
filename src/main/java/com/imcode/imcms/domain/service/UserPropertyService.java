package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.UserProperty;

import java.util.List;

public interface UserPropertyService {

    List<UserProperty> getAll();

    List<UserProperty> getByUserId(Integer userId);

    UserProperty getByUserIdAndKeyName(Integer userId, String keyName);

    List<UserProperty> getByUserIdAndValue(Integer userId, String value);

    UserProperty create(UserProperty userProperty);

    UserProperty update(UserProperty userProperty);

    void deleteById(Integer id);

}
