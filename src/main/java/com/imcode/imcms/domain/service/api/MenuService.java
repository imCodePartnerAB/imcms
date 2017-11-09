package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.core.CommonContentService;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
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
    private final CommonContentService commonContentService;
    private final DocumentService documentService;
    private final Function<MenuItem, MenuItemDTO> menuItemToDto;
    private final Function<List<MenuItemDTO>, List<MenuItem>> menuItemDtoListToMenuItemList;
    private final Function<Menu, MenuDTO> menuToMenuDTO;

    public MenuService(MenuRepository menuRepository,
                       VersionService versionService,
                       CommonContentService commonContentService,
                       DocumentService documentService,
                       Function<MenuItem, MenuItemDTO> menuItemToDto,
                       Function<List<MenuItemDTO>, List<MenuItem>> menuItemDtoListToMenuItemList,
                       Function<Menu, MenuDTO> menuToMenuDTO) {

        this.menuRepository = menuRepository;
        this.versionService = versionService;
        this.commonContentService = commonContentService;
        this.documentService = documentService;
        this.menuItemToDto = menuItemToDto;
        this.menuItemDtoListToMenuItemList = menuItemDtoListToMenuItemList;
        this.menuToMenuDTO = menuToMenuDTO;
    }

    public List<MenuItemDTO> getMenuItemsOf(int menuNo, int docId) {
        return getMenuItemsOf(menuNo, docId, MenuItemsStatus.ALL);
    }

    public List<MenuItemDTO> getPublicMenuItemsOf(int menuNo, int docId) {
        return getMenuItemsOf(menuNo, docId, MenuItemsStatus.PUBLIC);
    }

    public void saveFrom(MenuDTO menuDTO) {

        Menu menu = Optional.ofNullable(getMenu(menuDTO.getMenuId(), menuDTO.getDocId()))
                .orElseGet(() -> createMenu(menuDTO));

        if (!menu.getMenuItems().isEmpty()) {
            menu = deleteAllMenuItemsAndFlush(menu);
        }

        menu.setMenuItems(menuItemDtoListToMenuItemList.apply(menuDTO.getMenuItems()));

        menuRepository.saveAndFlush(menu);
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
        menu.setNo(menuDTO.getMenuId());
        menu.setVersion(workingVersion);
        return menuRepository.saveAndFlush(menu);
    }

    private List<MenuItemDTO> getMenuItemsOf(int menuNo, int docId, MenuItemsStatus status) {
        final Version version = versionService.getVersion(docId, status.equals(MenuItemsStatus.ALL)
                ? versionService::getDocumentWorkingVersion : versionService::getLatestVersion);

        final Menu menu = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menuNo, version);
        final UserDomainObject user = Imcms.getUser();

        return Optional.ofNullable(menu)
                .orElseGet(Menu::new)
                .getMenuItems()
                .stream()
                .map(menuItemToDto)
                .peek(menuItemDTO -> addTitleToMenuItem(menuItemDTO, user))
                .filter(menuItemDTO -> (status == MenuItemsStatus.ALL) || isMenuItemVisibleToUser(menuItemDTO, user))
                .collect(Collectors.toList());
    }

    /**
     * Also adds necessary props from meta to current item and it items.
     */
    private boolean isMenuItemVisibleToUser(MenuItemDTO menuItemDTO, UserDomainObject user) {
        final DocumentDTO documentDTO = documentService.get(menuItemDTO.getDocumentId());

        final boolean hasAccess = documentService.hasUserAccessToDoc(documentDTO.getId(), user);

        if (hasAccess) {
            final List<MenuItemDTO> children = menuItemDTO.getChildren().stream()
                    .filter(menuItem -> isMenuItemVisibleToUser(menuItem, user))
                    .collect(Collectors.toList());
            menuItemDTO.setChildren(children);
        }

        final String link = "/" + (documentDTO.getAlias() == null ? documentDTO.getId() : documentDTO.getAlias());

        menuItemDTO.setTarget(documentDTO.getTarget());
        menuItemDTO.setLink(link);

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

    private void addTitleToMenuItem(MenuItemDTO menuItemDTO, UserDomainObject user) {
        final Version latestVersion = versionService.getLatestVersion(menuItemDTO.getDocumentId());
        final CommonContent commonContent = commonContentService
                .getOrCreate(latestVersion.getDocId(), latestVersion.getNo(), user);

        menuItemDTO.setTitle(commonContent.getHeadline());

        menuItemDTO.getChildren()
                .forEach(childMenuItemDTO -> addTitleToMenuItem(childMenuItemDTO, user));
    }

    private enum MenuItemsStatus {
        PUBLIC,
        ALL
    }

}
