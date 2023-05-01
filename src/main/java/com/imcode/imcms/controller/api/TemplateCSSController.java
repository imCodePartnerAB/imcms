package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.TemplateCSSHistoryEntry;
import com.imcode.imcms.domain.dto.TemplateCSSVersion;
import com.imcode.imcms.domain.service.TemplateCSSService;
import com.imcode.imcms.security.AccessContentType;
import com.imcode.imcms.security.CheckAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/templates/css")
public class TemplateCSSController {

	private final TemplateCSSService templateCSSService;

	@GetMapping("/{templateName}")
	@CheckAccess(docPermission = AccessContentType.DOC_INFO)
	public String get(@PathVariable String templateName,
					  @RequestParam("version") TemplateCSSVersion version,
					  HttpServletResponse res) {
		if (!templateCSSService.existsLocally(templateName)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		res.setHeader("Cache-Control", "no-cache");

		return templateCSSService.get(templateName, version);
	}

	@GetMapping("/{templateName}/history")
	@CheckAccess(docPermission = AccessContentType.DOC_INFO)
	public List<TemplateCSSHistoryEntry> getHistory(@PathVariable String templateName) {
		if (!templateCSSService.existsOnSVN(templateName)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		return templateCSSService.getHistory(templateName);
	}

	@GetMapping("/{templateName}/history/{revision}")
	@CheckAccess(docPermission = AccessContentType.DOC_INFO)
	public String get(@PathVariable String templateName, @PathVariable(required = false) Long revision) {
		if (!templateCSSService.existsOnSVN(templateName, revision)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		return templateCSSService.getRevision(templateName, revision);
	}

	@PutMapping("/{templateName}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@CheckAccess(docPermission = AccessContentType.DOC_INFO)
	public void update(@PathVariable String templateName, @RequestBody String css) {
		if (!templateCSSService.existsOnSVN(templateName)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		if (templateCSSService.equalsWorkingVersion(templateName, css)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		templateCSSService.update(templateName, css);
	}

	@PostMapping("/{templateName}/publish")
	@CheckAccess(docPermission = AccessContentType.DOC_INFO)
	public void publish(@PathVariable String templateName) {
		if (!templateCSSService.existsLocally(templateName)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		if (templateCSSService.equalsVersions(templateName)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		templateCSSService.publish(templateName);
	}
}
