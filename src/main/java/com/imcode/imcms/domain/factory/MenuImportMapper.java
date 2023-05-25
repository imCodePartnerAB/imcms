package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.dto.ImportMenuDTO;
import com.imcode.imcms.domain.dto.ImportMenuItemDTO;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.enums.TypeSort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class MenuImportMapper {

	private final MenuService menuService;
	private final MenuItemImportMapper menuItemImportMapper;

	public boolean mapAndSave(int docId, ImportMenuDTO importMenu) {
		final List<Integer> skippedMenuItems = new ArrayList<>();
		final List<MenuItemDTO> menuItems = new ArrayList<>();

		final TypeSort typeSort = getTypeSortBySortOrder(importMenu.getTypeSort());

		for (ImportMenuItemDTO importMenuItem : importMenu.getMenuItems()) {
			final Optional<MenuItemDTO> mappedImportMenuItem = menuItemImportMapper.map(importMenuItem);
			if (mappedImportMenuItem.isEmpty()) {
				skippedMenuItems.add(importMenuItem.getDocumentId());
			} else {
				menuItems.add(mappedImportMenuItem.get());
			}
		}

		if (typeSort.equals(TypeSort.TREE_SORT) || typeSort.equals(TypeSort.MANUAL)) {
			for (MenuItemDTO menuItem : menuItems) {
				menuItem.setChildren(findChildren(menuItem, menuItems));
			}
		}

		final MenuDTO menu = new MenuDTO();
		menu.setDocId(docId);
		menu.setTypeSort(typeSort.name());
		menu.setMenuIndex(importMenu.getIndex());
		menu.setMenuItems(menuItems);

		final MenuDTO menuDTO = menuService.saveFrom(menu);

		log.info("Menu with index: {} created in document: {}. Menu items: {}",
				menuDTO.getMenuIndex(), menuDTO.getDocId(), menuDTO.getMenuItems().stream().map(MenuItemDTO::getDocumentId).toList().toString());

		if (!skippedMenuItems.isEmpty()) {
			log.warn("Menu items cannot be added to menu: {} because documents are not imported yet. " +
					"Import it and reimport this document in order to add skipped menu items. " +
					"Skipped menu items: {}", menuDTO.getMenuIndex(), skippedMenuItems.toString());
			return false;
		}

		return true;
	}

	public boolean mapAndSave(int docId, List<ImportMenuDTO> importMenuList) {
		boolean result = true;
		for (ImportMenuDTO importMenu : importMenuList) {
			if (!mapAndSave(docId, importMenu) && result) {
				result = false;
			}
		}

		return result;
	}

	private TypeSort getTypeSortBySortOrder(String sortOrder) {
		return switch (sortOrder) {
			case "MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER" -> TypeSort.TREE_SORT;
			case "MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED" -> TypeSort.MANUAL;
			case "MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED" -> TypeSort.MODIFIED_DATE_DESC;
			case "MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED" -> TypeSort.PUBLISHED_DATE_DESC;
			default -> TypeSort.ALPHABETICAL_ASC;
		};
	}

	private List<MenuItemDTO> findChildren(MenuItemDTO parent, List<MenuItemDTO> menuItems) {
		List<MenuItemDTO> children = new ArrayList<>();

		final String parentSortOrder = parent.getSortOrder();
		for (MenuItemDTO menuItem : menuItems) {

			final String sortOrder = menuItem.getSortOrder();
			if (sortOrder.startsWith(parentSortOrder) &&
					sortOrder.length() > parentSortOrder.length() &&
					sortOrder.charAt(parentSortOrder.length()) == '.') {

				children.add(menuItem);
			}
		}

		return children;
	}

}
