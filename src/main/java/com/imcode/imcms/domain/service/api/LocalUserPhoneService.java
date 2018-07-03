package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.service.PhoneService;
import com.imcode.imcms.persistence.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class LocalUserPhoneService implements PhoneService {

    private final PhoneRepository phoneRepository;

    @Autowired
    LocalUserPhoneService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }


}
