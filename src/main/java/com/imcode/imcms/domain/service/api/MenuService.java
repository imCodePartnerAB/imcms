package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.exception.MenuNotExistException;
import com.imcode.imcms.domain.service.core.CommonContentService;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.repository.MenuRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.imcode.imcms.util.mapping.MappingUtils.mapMenuItemDtoListToMenuItem;

@Service
public class MenuService {

    @Qualifier("com.imcode.imcms.persistence.repository.MenuRepository")
    private final MenuRepository menuRepository;
    private final VersionService versionService;
    private final CommonContentService commonContentService;
    private final Function<MenuItem, MenuItemDTO> menuItemToDto;
    private final Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem;

    public MenuService(MenuRepository menuRepository,
                       VersionService versionService,
                       CommonContentService commonContentService,
                       Function<MenuItem, MenuItemDTO> menuItemToDto,
                       Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem) {
        this.menuRepository = menuRepository;
        this.versionService = versionService;
        this.commonContentService = commonContentService;
        this.menuItemToDto = menuItemToDto;
        this.menuItemDtoToMenuItem = menuItemDtoToMenuItem;
    }

    public List<MenuItemDTO> getMenuItemsOf(int menuNo, int docId) {
        final Menu menu = getMenu(menuNo, docId);
        final UserDomainObject user = Imcms.getUser();

        return Optional.ofNullable(menu)
                .orElseThrow(() -> new MenuNotExistException(menuNo, docId))
                .getMenuItems()
                .stream()
                .map(menuItemToDto)
                .peek(menuItemDTO -> addTitleToMenuItem(menuItemDTO, user))
                .collect(Collectors.toList());
    }

    public void saveMenuItems(MenuDTO menuDTO) {

        Menu menu = Optional.ofNullable(getMenu(menuDTO.getMenuId(), menuDTO.getDocId()))
                .orElseThrow(() -> new MenuNotExistException(menuDTO.getMenuId(), menuDTO.getDocId()));

        if (!menu.getMenuItems().isEmpty()) {
            menu = deleteAllMenuItemsAndFlush(menu);
        }

        final List<MenuItemDTO> menuItems = menuDTO.getMenuItems();
        menu.setMenuItems(mapMenuItemDtoListToMenuItem(menuItems, menuItemDtoToMenuItem));

        menuRepository.saveAndFlush(menu);
    }

    private Menu deleteAllMenuItemsAndFlush(Menu menu) {
        for (Iterator<MenuItem> iterator = menu.getMenuItems().iterator(); iterator.hasNext(); ) {
            MenuItem projectEntity = iterator.next();
            projectEntity.setMenu(null);
            iterator.remove();
        }
        return menuRepository.saveAndFlush(menu);
    }

    private void addTitleToMenuItem(MenuItemDTO menuItemDTO, UserDomainObject user) {
        final Version menuItemVersion = versionService.getDocumentWorkingVersion(menuItemDTO.getDocumentId());
        final CommonContent commonContent = commonContentService
                .findByDocIdAndVersionNoAndUser(menuItemVersion.getDocId(), menuItemVersion.getNo(), user);

        menuItemDTO.setTitle(commonContent.getHeadline());

        menuItemDTO.getChildren()
                .forEach(childMenuItemDTO -> addTitleToMenuItem(childMenuItemDTO, user));
    }

    private Menu getMenu(int menuNo, int docId) {
        final Version workingVersion = versionService.getDocumentWorkingVersion(docId);
        return menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menuNo, workingVersion);
    }

}
