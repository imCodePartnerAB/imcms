package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.api.TextService;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/texts")
public class TextController {

    private final TextService textService;

    @Autowired
    TextController(TextService textService) {
        this.textService = textService;
    }

    @PostMapping
    public void saveText(@ModelAttribute TextDTO textDTO) {

        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to change image structure.");
        }

        textService.save(textDTO);
    }
}
