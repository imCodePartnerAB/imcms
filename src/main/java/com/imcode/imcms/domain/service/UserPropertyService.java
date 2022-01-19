package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.UserPropertyDTO;
import com.imcode.imcms.model.UserProperty;

import java.util.List;

public interface UserPropertyService {

    List<UserProperty> getAll();

    UserProperty getById(int id);

    List<UserProperty> getByUserId(Integer userId);

    UserProperty getByUserIdAndKeyName(Integer userId, String keyName);

    void create(UserPropertyDTO userProperty);

    UserProperty update(UserProperty userProperty);

    void update(List<UserPropertyDTO> deletedProperties, List<UserPropertyDTO> editedProperties, List<UserPropertyDTO> createdProperties);

    void deleteById(Integer id);

}
