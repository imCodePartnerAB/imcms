package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/texts")
public class TextController {

    private final TextService textService;

    @Autowired
    TextController(TextService textService) {
        this.textService = textService;
    }

	@GetMapping("/loop")
	public List<Text> getLoopTexts(@ModelAttribute TextDTO textDTO){
		return textService.getLoopTexts(textDTO.getDocId(), textDTO.getLangCode(), textDTO.getLoopEntryRef().getLoopIndex());
	}

    @PostMapping
    @CheckAccess(AccessType.TEXT)
    public TextDTO saveText(@ModelAttribute TextDTO textDTO) {
        return new TextDTO(textService.save(textDTO));
    }

    @PostMapping("/filter")
    public TextDTO filter(@ModelAttribute TextDTO textDTO) {
        return new TextDTO(textService.filter(textDTO));
    }

}
