package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.dto.ImportMenuDTO;
import com.imcode.imcms.domain.dto.ImportMenuItemDTO;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.enums.TypeSort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class MenuImportMapper {

	private final MenuService menuService;
	private final MenuItemImportMapper menuItemImportMapper;

	public void mapAndSave(int importDocId, int docId, ImportMenuDTO importMenu) {
		final MenuDTO menu = new MenuDTO();

		menu.setDocId(docId);
		// TODO: 01.02.23 fix type sort

		final String typeSort = switch (importMenu.getSortOrder()) {
			case "MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER" -> TypeSort.TREE_SORT.name();
			case "MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED" -> TypeSort.MANUAL.name();
			case "MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED" -> TypeSort.MODIFIED_DATE_DESC.name();
			case "MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED" -> TypeSort.PUBLISHED_DATE_DESC.name();
			default -> TypeSort.ALPHABETICAL_ASC.name();
		};

		menu.setTypeSort(typeSort);
		menu.setMenuIndex(importMenu.getIndex());

		final List<MenuItemDTO> menuItems = new ArrayList<>();
		for (ImportMenuItemDTO importMenuItem : importMenu.getMenuItems()) {
			menuItemImportMapper.map(importMenuItem)
					.ifPresentOrElse(menuItem -> {
						menuItems.add(menuItem);
						log.info(String.format("Document with id: %d exists and added to menu with index: %d in document with id: %d",
								importMenuItem.getDocumentId(), importMenu.getIndex(), importDocId));
					}, () -> {
						log.warn(String.format("Menu with index: %d in document with id(rb4): %d has menu entry with " +
								"document id(rb4): %d but that document are not imported yet. Import it and reimport " +
								"this document in order to update it!", importMenu.getIndex(), importDocId, importMenuItem.getDocumentId()));
					});
		}

		menu.setMenuItems(menuItems);
		menuService.saveFrom(menu);
	}

	public void mapAndSave(int importDocId, int docId, List<ImportMenuDTO> importMenuList) {
		importMenuList.forEach(importMenu -> mapAndSave(importDocId, docId, importMenu));
	}
}
