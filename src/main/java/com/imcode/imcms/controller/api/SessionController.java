package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.SessionInfoDTO;
import imcode.util.Utility;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    @GetMapping
    public List<SessionInfoDTO> getActiveSessions() {
        return Utility.getActiveSessions();
    }
}
