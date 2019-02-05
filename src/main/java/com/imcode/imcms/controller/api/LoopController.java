package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.service.LoopService;
import com.imcode.imcms.model.Loop;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loops")
public class LoopController {

    private final LoopService loopService;

    LoopController(LoopService loopService) {
        this.loopService = loopService;
    }

    @GetMapping
    public Loop getDocumentLoop(@ModelAttribute LoopDTO loopRequestData) {
        return loopService.getLoop(loopRequestData.getIndex(), loopRequestData.getDocId());
    }

    @PostMapping
    @CheckAccess(AccessType.LOOP)
    public void saveLoop(@RequestBody LoopDTO loopDTO) {
        loopService.saveLoop(loopDTO);
    }
}
