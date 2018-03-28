package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
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
    @CheckAccess(AccessType.TEXT)
    public void saveText(@ModelAttribute TextDTO textDTO) {
        textService.save(textDTO);
    }
}
