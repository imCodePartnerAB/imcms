package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.VersionData;
import com.imcode.imcms.domain.service.api.DefaultVersionDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
public class VersionDataController {

    private final DefaultVersionDataService versionDataService;

    @Autowired
    public VersionDataController(DefaultVersionDataService versionDataService) {
        this.versionDataService = versionDataService;
    }

    @GetMapping
    public VersionData get() {
        return versionDataService.getVersionData();
    }
}
