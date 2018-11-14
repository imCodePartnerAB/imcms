package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileService {

    List<Profile> getAll();

    Profile create(Profile profile);

    Profile update(Profile profile);

    Optional<Profile> getById(Integer id);

    void deleteById(Integer id);

}
