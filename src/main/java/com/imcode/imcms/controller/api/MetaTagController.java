package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.MetaTagDTO;
import com.imcode.imcms.domain.service.MetaTagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("meta-tag")
public class MetaTagController {
	private final MetaTagService metaTagService;

	public MetaTagController(MetaTagService metaTagService) {
		this.metaTagService = metaTagService;
	}

	@GetMapping("all")
	public List<MetaTagDTO> getMetaTags() {
		return metaTagService.getAll();
	}

	@PostMapping("{metaTagName}")
	public void saveMetaTag(@PathVariable String metaTagName) {
		metaTagService.saveMetaTag(metaTagName);
	}
}
