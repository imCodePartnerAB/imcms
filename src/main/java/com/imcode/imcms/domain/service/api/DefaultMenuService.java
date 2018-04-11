package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.DocumentMenuService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MenuRepository;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.Imcms;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Service("menuService")
class DefaultMenuService extends AbstractVersionedContentService<Menu, MenuRepository> implements MenuService {

    private final VersionService versionService;
    private final DocumentMenuService documentMenuService;
    private final Function<List<MenuItemDTO>, List<MenuItem>> menuItemDtoListToMenuItemList;
    private final BiFunction<Menu, Language, MenuDTO> menuSaver;
    private final UnaryOperator<MenuItem> toMenuItemsWithoutId;
    private LanguageService languageService;
    private BiFunction<MenuItem, Language, MenuItemDTO> menuItemToDTO;
    private TernaryFunction<MenuItem, Language, Version, MenuItemDTO> menuItemToMenuItemDtoWithLang;

    DefaultMenuService(MenuRepository menuRepository,
                       VersionService versionService,
                       DocumentMenuService documentMenuService,
                       BiFunction<MenuItem, Language, MenuItemDTO> menuItemToDTO,
                       Function<List<MenuItemDTO>, List<MenuItem>> menuItemDtoListToMenuItemList,
                       LanguageService languageService,
                       BiFunction<Menu, Language, MenuDTO> menuToMenuDTO,
                       UnaryOperator<MenuItem> toMenuItemsWithoutId,
                       TernaryFunction<MenuItem, Language, Version, MenuItemDTO> menuItemToMenuItemDtoWithLang) {

        super(menuRepository);
        this.versionService = versionService;
        this.documentMenuService = documentMenuService;
        this.menuItemToMenuItemDtoWithLang = menuItemToMenuItemDtoWithLang;
        this.menuItemDtoListToMenuItemList = menuItemDtoListToMenuItemList;
        this.menuItemToDTO = menuItemToDTO;
        this.languageService = languageService;
        this.toMenuItemsWithoutId = toMenuItemsWithoutId;
        this.menuSaver = (menu, language) -> menuToMenuDTO.apply(menuRepository.save(menu), language);
    }

    @Override
    public List<MenuItemDTO> getMenuItems(int menuIndex, int docId, String language) {
        return getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, false);
    }

    @Override
    public List<MenuItemDTO> getVisibleMenuItems(int menuIndex, int docId, String language) {
        return getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true);
    }

    @Override
    public List<MenuItemDTO> getPublicMenuItems(int menuIndex, int docId, String language) {
        return getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC, language, true);
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
    @Transactional
    public void deleteByDocId(Integer docIdToDelete) {
        repository.deleteByDocId(docIdToDelete);
    }

    @Override
    protected Menu removeId(Menu jpa, Version newVersion) {
        final Menu menu = new Menu();
        menu.setId(null);
        menu.setNo(jpa.getNo());
        menu.setVersion(newVersion);

        final List<MenuItem> newMenuItems = jpa.getMenuItems()
                .stream()
                .map(toMenuItemsWithoutId)
                .collect(Collectors.toList());

        menu.setMenuItems(newMenuItems);

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

    private List<MenuItemDTO> getMenuItemsOf(
            int menuIndex, int docId, MenuItemsStatus status, String langCode, boolean isVisible
    ) {
        final Function<Integer, Version> versionReceiver = MenuItemsStatus.ALL.equals(status)
                ? versionService::getDocumentWorkingVersion
                : versionService::getLatestVersion;

        final Version version = versionService.getVersion(docId, versionReceiver);
        final Language language = languageService.findByCode(langCode);
        final Menu menu = repository.findByNoAndVersionAndFetchMenuItemsEagerly(menuIndex, version);

        final Function<MenuItem, MenuItemDTO> menuItemFunction = isVisible
                ? menuItem -> menuItemToMenuItemDtoWithLang.apply(menuItem, language, version)
                : menuItem -> menuItemToDTO.apply(menuItem, language);

        return Optional.ofNullable(menu)
                .map(Menu::getMenuItems)
                .orElseGet(ArrayList::new)
                .stream()
                .map(menuItemFunction)
                .filter(Objects::nonNull)
                .filter(menuItemDTO -> (status == MenuItemsStatus.ALL || isPublicMenuItem(menuItemDTO)))
                .peek(menuItemDTO -> {
                    if (status == MenuItemsStatus.ALL) return;

                    final List<MenuItemDTO> children = menuItemDTO.getChildren()
                            .stream()
                            .filter(this::isPublicMenuItem)
                            .collect(Collectors.toList());

                    menuItemDTO.setChildren(children);
                })
                .collect(Collectors.toList());
    }

    private boolean isPublicMenuItem(MenuItemDTO menuItemDTO) {
        return documentMenuService.isPublicMenuItem(menuItemDTO.getDocumentId());
    }

}
