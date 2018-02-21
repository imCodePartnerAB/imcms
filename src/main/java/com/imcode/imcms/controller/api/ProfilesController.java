package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.ProfileService;
import com.imcode.imcms.model.Profile;
import org.springframework.web.bind.annotation.GetMapping;
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

}
