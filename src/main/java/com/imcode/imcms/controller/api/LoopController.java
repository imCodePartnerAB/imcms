package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.service.api.LoopService;
import imcode.server.Imcms;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loops")
public class LoopController {

    private final LoopService loopService;

    public LoopController(LoopService loopService) {
        this.loopService = loopService;
    }

    @GetMapping
    public LoopDTO getDocumentLoop(@ModelAttribute LoopDTO loopRequestData) {
        return loopService.getLoop(loopRequestData.getIndex(), loopRequestData.getDocId());
    }

    @PostMapping
    public void saveLoop(@RequestBody LoopDTO loopDTO) throws IllegalAccessException {

        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new IllegalAccessException("User do not have access to change loop structure.");
        }

        loopService.saveLoop(loopDTO);
    }
}
