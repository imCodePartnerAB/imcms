package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.core.CommonContentService;
import com.imcode.imcms.domain.service.core.MetaService;
import com.imcode.imcms.domain.service.core.PropertyService;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.mapping.jpa.doc.Meta;
import com.imcode.imcms.mapping.jpa.doc.Property;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.repository.MenuRepository;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
    private final MetaService metaService;
    private final PropertyService propertyService;
    private final Function<MenuItem, MenuItemDTO> menuItemToDto;
    private final Function<List<MenuItemDTO>, List<MenuItem>> menuItemDtoListToMenuItemList;

    public MenuService(MenuRepository menuRepository,
                       VersionService versionService,
                       CommonContentService commonContentService,
                       MetaService metaService,
                       PropertyService propertyService,
                       Function<MenuItem, MenuItemDTO> menuItemToDto,
                       Function<List<MenuItemDTO>, List<MenuItem>> menuItemDtoListToMenuItemList) {
        this.menuRepository = menuRepository;
        this.versionService = versionService;
        this.commonContentService = commonContentService;
        this.metaService = metaService;
        this.propertyService = propertyService;
        this.menuItemToDto = menuItemToDto;
        this.menuItemDtoListToMenuItemList = menuItemDtoListToMenuItemList;
    }

    public List<MenuItemDTO> getMenuItemsOf(int menuNo, int docId) {
        return getMenuItemsOf(menuNo, docId, MenuItemsStatus.ALL);
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

    public List<MenuItemDTO> getPublicMenuItemsOf(int menuNo, int docId) {
        return getMenuItemsOf(menuNo, docId, MenuItemsStatus.PUBLIC);
    }

    private List<MenuItemDTO> getMenuItemsOf(int menuNo, int docId, MenuItemsStatus status) {
        final Version version = getVersion(docId, status);

        final Menu menu = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menuNo, version);
        final UserDomainObject user = Imcms.getUser();

        return Optional.ofNullable(menu)
                .orElseGet(Menu::new)
                .getMenuItems()
                .stream()
                .map(menuItemToDto)
                .peek(menuItemDTO -> addTitleToMenuItem(menuItemDTO, user, status))
                .filter(menuItemDTO -> (status == MenuItemsStatus.ALL) || isMenuItemVisibleToUser(menuItemDTO, user))
                .collect(Collectors.toList());
    }

    private Menu createMenu(MenuDTO menuDTO) {
        final Version workingVersion = versionService.getDocumentWorkingVersion(menuDTO.getDocId());
        final Menu menu = new Menu();
        menu.setNo(menuDTO.getMenuId());
        menu.setVersion(workingVersion);
        return menuRepository.saveAndFlush(menu);
    }

    private Menu deleteAllMenuItemsAndFlush(Menu menu) {
        for (Iterator<MenuItem> iterator = menu.getMenuItems().iterator(); iterator.hasNext(); ) {
            MenuItem projectEntity = iterator.next();
            projectEntity.setMenu(null);
            iterator.remove();
        }
        return menuRepository.saveAndFlush(menu);
    }

    private void addTitleToMenuItem(MenuItemDTO menuItemDTO, UserDomainObject user, MenuItemsStatus status) {
        final Version menuItemVersion = getVersion(menuItemDTO.getDocumentId(), status);
        final CommonContent commonContent = commonContentService
                .findByDocIdAndVersionNoAndUser(menuItemVersion.getDocId(), menuItemVersion.getNo(), user);

        menuItemDTO.setTitle(commonContent.getHeadline());

        menuItemDTO.getChildren()
                .forEach(childMenuItemDTO -> addTitleToMenuItem(childMenuItemDTO, user, status));
    }

    private Version getVersion(int docId, MenuItemsStatus status) {
        return status == MenuItemsStatus.ALL
                ? versionService.getDocumentWorkingVersion(docId)
                : versionService.getLatestVersion(docId);
    }

    private boolean isMenuItemVisibleToUser(MenuItemDTO menuItemDTO, UserDomainObject user) {
        final Meta meta = metaService.getOne(menuItemDTO.getDocumentId());

        final boolean hasAccess = user.hasUserAccessToDoc(meta);

        if (hasAccess) {
            final List<MenuItemDTO> children = menuItemDTO.getChildren().stream()
                    .filter(menuItem -> isMenuItemVisibleToUser(menuItem, user))
                    .collect(Collectors.toList());
            menuItemDTO.setChildren(children);
        }

        final Property propertyAlias = propertyService.findByDocIdAndName(menuItemDTO.getDocumentId(), DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS);

        final String alias = "/" + (propertyAlias == null ? menuItemDTO.getId() : propertyAlias.getValue());

        menuItemDTO.setTarget(meta.getTarget());
        menuItemDTO.setLink(alias);

        return hasAccess;
    }

    private Menu getMenu(int menuNo, int docId) {
        final Version workingVersion = versionService.getDocumentWorkingVersion(docId);
        return menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menuNo, workingVersion);
    }

    private enum MenuItemsStatus {
        PUBLIC,
        ALL
    }

}
