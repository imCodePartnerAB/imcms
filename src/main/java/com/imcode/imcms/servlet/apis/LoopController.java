package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.mapping.dto.LoopDTO;
import com.imcode.imcms.service.LoopService;
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
    public LoopDTO getDocumentLoop(@ModelAttribute LoopDTO loopRequestData) {
        return loopService.getLoop(loopRequestData.getLoopId(), loopRequestData.getDocId());
    }

    @PostMapping
    public void saveLoop(@ModelAttribute LoopDTO loopDTO) throws IllegalAccessException {

        if (!Imcms.getUser().isSuperAdmin()) {
            throw new IllegalAccessException("User do not have access to change loop structure.");
        }

        loopService.saveLoop(loopDTO);
    }
}
