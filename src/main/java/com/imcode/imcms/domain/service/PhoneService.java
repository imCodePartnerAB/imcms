package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Phone;

import java.util.List;

public interface PhoneService {
    void updateUserPhones(List<Phone> phones, int userId);
}
