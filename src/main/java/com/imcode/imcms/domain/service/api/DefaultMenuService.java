package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.DocumentMenuService;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MenuRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("menuService")
class DefaultMenuService extends AbstractVersionedContentService<Menu, MenuDTO, MenuRepository> implements MenuService {

    private final VersionService versionService;
    private final DocumentMenuService documentMenuService;
    private final Function<MenuItem, MenuItemDTO> menuItemToDto;
    private final Function<List<MenuItemDTO>, List<MenuItem>> menuItemDtoListToMenuItemList;
    private final Function<Menu, MenuDTO> menuToMenuDTO;
    private final Function<Menu, MenuDTO> menuSaver;

    DefaultMenuService(@Qualifier("com.imcode.imcms.persistence.repository.MenuRepository") MenuRepository menuRepository,
                       VersionService versionService,
                       DocumentMenuService documentMenuService,
                       Function<MenuItem, MenuItemDTO> menuItemToDto,
                       Function<List<MenuItemDTO>, List<MenuItem>> menuItemDtoListToMenuItemList,
                       Function<Menu, MenuDTO> menuToMenuDTO) {

        super(menuRepository);
        this.versionService = versionService;
        this.documentMenuService = documentMenuService;
        this.menuItemToDto = menuItemToDto;
        this.menuItemDtoListToMenuItemList = menuItemDtoListToMenuItemList;
        this.menuToMenuDTO = menuToMenuDTO;
        this.menuSaver = menuToMenuDTO.compose(menuRepository::saveAndFlush);
    }

    @Override
    public List<MenuItemDTO> getMenuItemsOf(int menuIndex, int docId) {
        return getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL);
    }

    @Override
    public List<MenuItemDTO> getPublicMenuItemsOf(int menuIndex, int docId) {
        return getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC);
    }

    @Override
    public MenuDTO saveFrom(MenuDTO menuDTO) {

        final Menu menu = Optional.ofNullable(getMenu(menuDTO.getMenuIndex(), menuDTO.getDocId()))
                .orElseGet(() -> createMenu(menuDTO));

        menu.setMenuItems(menuItemDtoListToMenuItemList.apply(menuDTO.getMenuItems()));

        return menuSaver.apply(menu);
    }

    @Override
    public Collection<MenuDTO> findAllByVersion(Version version) {
        return repository.findByVersion(version).stream().map(menuToMenuDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByDocId(Integer docIdToDelete) {
        repository.deleteByDocId(docIdToDelete);
    }

    @Override
    protected MenuDTO mapping(Menu jpa, Version version) {
        return menuToMenuDTO.apply(jpa);
    }

    @Override
    protected Menu mappingWithoutId(MenuDTO dto, Version version) {
        final Menu menu = createMenu(dto, version);
        menu.setMenuItems(menuItemDtoListToMenuItemList.apply(dto.getMenuItems()));
        return menu;
    }

    private Menu getMenu(int menuNo, int docId) {
        final Version workingVersion = versionService.getDocumentWorkingVersion(docId);
        return repository.findByNoAndVersionAndFetchMenuItemsEagerly(menuNo, workingVersion);
    }

    private Menu createMenu(MenuDTO menuDTO) {
        final Version workingVersion = versionService.getDocumentWorkingVersion(menuDTO.getDocId());
        return createMenu(menuDTO, workingVersion);
    }

    private Menu createMenu(MenuDTO menuDTO, Version version) {
        final Menu menu = new Menu();
        menu.setNo(menuDTO.getMenuIndex());
        menu.setVersion(version);
        return menu;
    }

    private List<MenuItemDTO> getMenuItemsOf(int menuIndex, int docId, MenuItemsStatus status) {
        final Version version = versionService.getVersion(docId, status.equals(MenuItemsStatus.ALL)
                ? versionService::getDocumentWorkingVersion : versionService::getLatestVersion);

        final Menu menu = repository.findByNoAndVersionAndFetchMenuItemsEagerly(menuIndex, version);
        final UserDomainObject user = Imcms.getUser();

        return Optional.ofNullable(menu)
                .map(Menu::getMenuItems)
                .orElseGet(ArrayList::new)
                .stream()
                .map(menuItemToDto)
                .filter(menuItemDTO -> (status == MenuItemsStatus.ALL) || isMenuItemVisibleToUser(menuItemDTO, user))
                .collect(Collectors.toList());
    }

    private boolean isMenuItemVisibleToUser(MenuItemDTO menuItemDTO, UserDomainObject user) {
        final boolean hasAccess = documentMenuService.hasUserAccessToDoc(menuItemDTO.getDocumentId(), user);

        if (hasAccess) {
            final List<MenuItemDTO> children = menuItemDTO.getChildren()
                    .stream()
                    .filter(menuItem -> isMenuItemVisibleToUser(menuItem, user))
                    .collect(Collectors.toList());

            menuItemDTO.setChildren(children);
        }

        return hasAccess;
    }

}
