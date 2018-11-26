package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ProfileDTO;
import com.imcode.imcms.domain.service.ProfileService;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.Profile;
import com.imcode.imcms.persistence.entity.ProfileJPA;
import com.imcode.imcms.persistence.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultProfileService implements ProfileService {

    private final ProfileRepository profileRepository;
    private DocumentMapper documentMapper;

    public DefaultProfileService(ProfileRepository profileRepository,
                                 DocumentMapper documentMapper) {
        this.profileRepository = profileRepository;
        this.documentMapper = documentMapper;
    }

    @Override
    public List<Profile> getAll() {
        return profileRepository.findAll()
                .stream()
                .map(ProfileDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Profile create(Profile profile) {
        String alias = profile.getDocumentName();

        if (profile.getName().equals("")) {
            throw new IllegalArgumentException();
        }
        if (!(documentMapper.getDocument(alias).isPublished())) {
            throw new NullPointerException();
        }
        return new ProfileDTO(profileRepository.save(new ProfileJPA(profile)));
    }

    @Override
    public Profile update(Profile profile) {
        Integer id = profile.getId();
        String alias = profile.getDocumentName();
        Profile receivedProfile = profileRepository.findOne(id);

        if (profile.getName().equals("")) {
            throw new IllegalArgumentException();
        }
        if (!(documentMapper.getDocument(alias).isPublished())) {
            throw new NullPointerException();
        } else {
            receivedProfile.setName(profile.getName());
            receivedProfile.setDocumentName(profile.getDocumentName());
        }

        return new ProfileDTO(profileRepository.save(new ProfileJPA(receivedProfile)));
    }

    @Override
    public Optional<Profile> getById(Integer id) {
        return Optional.ofNullable(profileRepository.findOne(id)).map(ProfileDTO::new);
    }

    @Override
    public void deleteById(Integer id) {
        profileRepository.delete(id);
    }


}
