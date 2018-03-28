package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ProfileDTO;
import com.imcode.imcms.domain.service.ProfileService;
import com.imcode.imcms.model.Profile;
import com.imcode.imcms.persistence.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultProfileService implements ProfileService {

    private final ProfileRepository profileRepository;

    public DefaultProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public List<Profile> getAll() {
        return profileRepository.findAll().stream().map(ProfileDTO::new).collect(Collectors.toList());
    }

}
