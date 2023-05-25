package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.dto.BasicImportDocumentInfoDTO;
import com.imcode.imcms.domain.dto.ImportMenuItemDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.BasicImportDocumentInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class MenuItemImportMapper {
	private final BasicImportDocumentInfoService basicImportDocumentInfoService;

	public Optional<MenuItemDTO> map(ImportMenuItemDTO importMenuItem) {
		final Optional<BasicImportDocumentInfoDTO> basicImportDocumentInfo = basicImportDocumentInfoService.getById(importMenuItem.getDocumentId());

		if (basicImportDocumentInfo.isEmpty() || basicImportDocumentInfo.get().getMetaId() == null) {
			return Optional.empty();
		}

		final MenuItemDTO menuItem = new MenuItemDTO();

		menuItem.setDocumentId(basicImportDocumentInfo.get().getMetaId());
		menuItem.setSortOrder(importMenuItem.getSortOrder());

		return Optional.of(menuItem);
	}
}
