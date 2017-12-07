package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.service.LoopService;
import com.imcode.imcms.model.Loop;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import org.springframework.web.bind.annotation.*;

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
    public void saveLoop(@RequestBody LoopDTO loopDTO) {

        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to change loop structure.");
        }

        loopService.saveLoop(loopDTO);
    }
}
