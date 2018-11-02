package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.SystemPropertyService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemPropertyController {

    private final SystemPropertyService systemPropertyService;

    public SystemPropertyController(SystemPropertyService systemPropertyService) {
        this.systemPropertyService = systemPropertyService;
    }
}
