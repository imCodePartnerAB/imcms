package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ProfileDTO;
import com.imcode.imcms.domain.service.ProfileService;
import com.imcode.imcms.model.Profile;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/profiles")
public class ProfilesController {

    private final ProfileService profileService;

    public ProfilesController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public List<Profile> getAll() {
        return profileService.getAll();
    }

    @GetMapping("/{id}")
    public Profile getById(@PathVariable Integer id) {
        return profileService.getById(id).orElseThrow(() -> new EmptyResultDataAccessException(id));
    }

    @CheckAccess
    @PostMapping
    public Profile create(@RequestBody ProfileDTO profile) {
        return profileService.create(profile);
    }

    @CheckAccess
    @PutMapping
    public Profile update(@RequestBody ProfileDTO profile) {
        return profileService.update(profile);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        profileService.deleteById(id);
    }
}
