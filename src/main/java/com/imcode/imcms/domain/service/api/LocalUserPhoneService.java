package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.PhoneDTO;
import com.imcode.imcms.domain.service.PhoneService;
import com.imcode.imcms.model.Phone;
import com.imcode.imcms.persistence.entity.PhoneJPA;
import com.imcode.imcms.persistence.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
class LocalUserPhoneService implements PhoneService {

    private final PhoneRepository phoneRepository;

    private final Function<Collection<Phone>, List<PhoneJPA>> toJPA = phones -> phones.stream()
            .map(PhoneJPA::new)
            .collect(Collectors.toList());

    @Autowired
    LocalUserPhoneService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @Override
    public void updateUserPhones(List<Phone> phones, int userId) {
        phoneRepository.deleteByUserId(userId);

        final List<PhoneJPA> saveUs = toJPA.apply(phones);

        phoneRepository.save(saveUs);
    }

    @Override
    public List<Phone> getUserPhones(int userId) {
        return phoneRepository.findByUserId(userId).stream().map(PhoneDTO::new).collect(Collectors.toList());
    }
}
