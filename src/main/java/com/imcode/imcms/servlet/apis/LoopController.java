package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.service.LoopService;
import com.imcode.imcms.domain.service.exception.DocumentNotExistException;
import imcode.server.Imcms;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loop")
public class LoopController {

    private final LoopService loopService;

    public LoopController(LoopService loopService) {
        this.loopService = loopService;
    }

    @GetMapping
    public LoopDTO getDocumentLoop(@ModelAttribute LoopDTO loopRequestData) throws DocumentNotExistException {
        return loopService.getLoop(loopRequestData.getLoopIndex(), loopRequestData.getDocId());
    }

    @PostMapping
    public void saveLoop(@RequestBody LoopDTO loopDTO) throws IllegalAccessException {

        if (!Imcms.getUser().isSuperAdmin()) {
            throw new IllegalAccessException("User do not have access to change loop structure.");
        }

        loopService.saveLoop(loopDTO);
    }
}
