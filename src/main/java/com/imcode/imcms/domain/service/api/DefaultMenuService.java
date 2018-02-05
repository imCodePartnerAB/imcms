package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.Language;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Service("menuService")
class DefaultMenuService extends AbstractVersionedContentService<Menu, MenuRepository> implements MenuService {

    private final VersionService versionService;
    private final DocumentMenuService documentMenuService;
    private final BiFunction<MenuItem, Language, MenuItemDTO> menuItemToDto;
    private final Function<List<MenuItemDTO>, List<MenuItem>> menuItemDtoListToMenuItemList;
    private final BiFunction<Menu, Language, MenuDTO> menuToMenuDTO;
    private final BiFunction<Menu, Language, MenuDTO> menuSaver;
    private final UnaryOperator<MenuItem> toMenuItemsWithoutId;
    private LanguageService languageService;

    DefaultMenuService(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
                       @Qualifier("com.imcode.imcms.persistence.repository.MenuRepository") MenuRepository menuRepository,
                       VersionService versionService,
                       DocumentMenuService documentMenuService,
                       BiFunction<MenuItem, Language, MenuItemDTO> menuItemToDto,
                       Function<List<MenuItemDTO>, List<MenuItem>> menuItemDtoListToMenuItemList,
                       LanguageService languageService,
                       BiFunction<Menu, Language, MenuDTO> menuToMenuDTO,
                       UnaryOperator<MenuItem> toMenuItemsWithoutId) {

        super(menuRepository);
        this.versionService = versionService;
        this.documentMenuService = documentMenuService;
        this.menuItemToDto = menuItemToDto;
        this.menuItemDtoListToMenuItemList = menuItemDtoListToMenuItemList;
        this.languageService = languageService;
        this.menuToMenuDTO = menuToMenuDTO;
        this.toMenuItemsWithoutId = toMenuItemsWithoutId;
        this.menuSaver = (menu, language) -> menuToMenuDTO.apply(menuRepository.save(menu), language);
    }

    @Override
    public List<MenuItemDTO> getMenuItemsOf(int menuIndex, int docId, String language) {
        return getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language);
    }

    @Override
    public List<MenuItemDTO> getPublicMenuItemsOf(int menuIndex, int docId, String language) {
        return getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC, language);
    }

    @Override
    public MenuDTO saveFrom(MenuDTO menuDTO) {

        final Integer docId = menuDTO.getDocId();
        final Menu menu = Optional.ofNullable(getMenu(menuDTO.getMenuIndex(), docId))
                .orElseGet(() -> createMenu(menuDTO));

        menu.setMenuItems(menuItemDtoListToMenuItemList.apply(menuDTO.getMenuItems()));

        final MenuDTO savedMenu = menuSaver.apply(menu, languageService.findByCode(Imcms.getUser().getLanguage()));

        super.updateWorkingVersion(docId);

        return savedMenu;
    }

    @Override
    public Set<MenuDTO> getByVersion(Version version) {
        return repository.findByVersion(version)
                .stream()
                .map(menu -> menuToMenuDTO.apply(menu, languageService.findByCode("en"))) // TODO language?
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void deleteByDocId(Integer docIdToDelete) {
        repository.deleteByDocId(docIdToDelete);
    }

    @Override
    protected Menu removeId(Menu jpa, Version newVersion) {
        final Menu menu = new Menu(jpa);
        menu.setId(null);
        menu.setVersion(newVersion);

        final List<MenuItem> collect = menu.getMenuItems()
                .stream()
                .map(toMenuItemsWithoutId)
                .collect(Collectors.toList());

        menu.setMenuItems(collect);

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

    private List<MenuItemDTO> getMenuItemsOf(int menuIndex, int docId, MenuItemsStatus status, String langCode) {
        final Version version = versionService.getVersion(docId, status.equals(MenuItemsStatus.ALL)
                ? versionService::getDocumentWorkingVersion : versionService::getLatestVersion);

        final Language language = languageService.findByCode(langCode);
        final Menu menu = repository.findByNoAndVersionAndFetchMenuItemsEagerly(menuIndex, version);
        final UserDomainObject user = Imcms.getUser();

        return Optional.ofNullable(menu)
                .map(Menu::getMenuItems)
                .orElseGet(ArrayList::new)
                .stream()
                .map(menuItem -> menuItemToDto.apply(menuItem, language))
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
