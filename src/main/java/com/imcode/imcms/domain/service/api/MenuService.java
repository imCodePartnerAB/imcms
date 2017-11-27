package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MenuRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Qualifier("com.imcode.imcms.persistence.repository.MenuRepository")
    private final MenuRepository menuRepository;
    private final VersionService versionService;
    private final DocumentService documentService;
    private final Function<MenuItem, MenuItemDTO> menuItemToDto;
    private final Function<List<MenuItemDTO>, List<MenuItem>> menuItemDtoListToMenuItemList;
    private final Function<Menu, MenuDTO> menuToMenuDTO;
    private final Function<Menu, MenuDTO> menuSaver;

    MenuService(MenuRepository menuRepository,
                VersionService versionService,
                DocumentService documentService,
                Function<MenuItem, MenuItemDTO> menuItemToDto,
                Function<List<MenuItemDTO>, List<MenuItem>> menuItemDtoListToMenuItemList,
                Function<Menu, MenuDTO> menuToMenuDTO) {

        this.menuRepository = menuRepository;
        this.versionService = versionService;
        this.documentService = documentService;
        this.menuItemToDto = menuItemToDto;
        this.menuItemDtoListToMenuItemList = menuItemDtoListToMenuItemList;
        this.menuToMenuDTO = menuToMenuDTO;
        this.menuSaver = menuToMenuDTO.compose(menuRepository::saveAndFlush);
    }

    public List<MenuItemDTO> getMenuItemsOf(int menuIndex, int docId) {
        return getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL);
    }

    public List<MenuItemDTO> getPublicMenuItemsOf(int menuIndex, int docId) {
        return getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC);
    }

    public MenuDTO saveFrom(MenuDTO menuDTO) {

        Menu menu = Optional.ofNullable(getMenu(menuDTO.getMenuIndex(), menuDTO.getDocId()))
                .orElseGet(() -> createMenu(menuDTO));

        if (!menu.getMenuItems().isEmpty()) {
            menu = deleteAllMenuItemsAndFlush(menu);
        }

        menu.setMenuItems(menuItemDtoListToMenuItemList.apply(menuDTO.getMenuItems()));

        return menuSaver.apply(menu);
    }

    public Collection<MenuDTO> findAllByVersion(Version version) {
        return menuRepository.findByVersion(version).stream().map(menuToMenuDTO).collect(Collectors.toList());
    }

    private Menu getMenu(int menuNo, int docId) {
        final Version workingVersion = versionService.getDocumentWorkingVersion(docId);
        return menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menuNo, workingVersion);
    }

    private Menu createMenu(MenuDTO menuDTO) {
        final Version workingVersion = versionService.getDocumentWorkingVersion(menuDTO.getDocId());
        final Menu menu = new Menu();
        menu.setNo(menuDTO.getMenuIndex());
        menu.setVersion(workingVersion);
        return menuRepository.saveAndFlush(menu);
    }

    private List<MenuItemDTO> getMenuItemsOf(int menuIndex, int docId, MenuItemsStatus status) {
        final Version version = versionService.getVersion(docId, status.equals(MenuItemsStatus.ALL)
                ? versionService::getDocumentWorkingVersion : versionService::getLatestVersion);

        final Menu menu = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menuIndex, version);
        final UserDomainObject user = Imcms.getUser();

        return Optional.ofNullable(menu)
                .orElseGet(Menu::new)
                .getMenuItems()
                .stream()
                .map(menuItemToDto)
                .filter(menuItemDTO -> (status == MenuItemsStatus.ALL) || isMenuItemVisibleToUser(menuItemDTO, user))
                .collect(Collectors.toList());
    }

    private boolean isMenuItemVisibleToUser(MenuItemDTO menuItemDTO, UserDomainObject user) {
        final boolean hasAccess = documentService.hasUserAccessToDoc(menuItemDTO.getDocumentId(), user);

        if (hasAccess) {
            final List<MenuItemDTO> children = menuItemDTO.getChildren()
                    .stream()
                    .filter(menuItem -> isMenuItemVisibleToUser(menuItem, user))
                    .collect(Collectors.toList());

            menuItemDTO.setChildren(children);
        }

        return hasAccess;
    }

    private Menu deleteAllMenuItemsAndFlush(Menu menu) {
        for (Iterator<MenuItem> iterator = menu.getMenuItems().iterator(); iterator.hasNext(); ) {
            MenuItem projectEntity = iterator.next();
            projectEntity.setMenu(null);
            iterator.remove();
        }
        return menuRepository.saveAndFlush(menu);
    }

    private enum MenuItemsStatus {
        PUBLIC,
        ALL
    }

}
