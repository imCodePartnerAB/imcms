package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MenuRepository;
import imcode.server.Imcms;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
public class MenuDataInitializer extends TestDataCleaner {

    private static final int DOC_ID = 1001;
    private static final int VERSION_INDEX = 0;
    private static final int MENU_INDEX = 1;

    private final MenuRepository menuRepository;
    private final VersionDataInitializer versionDataInitializer;
    private final BiFunction<Menu, Language, MenuDTO> menuToMenuDTO;
    private final LanguageService languageService;
    private Menu savedMenu;
    private Version version;

    public MenuDataInitializer(MenuRepository menuRepository,
                               VersionDataInitializer versionDataInitializer,
                               BiFunction<Menu, Language, MenuDTO> menuToMenuDTO,
                               LanguageService languageService) {
        super(menuRepository);
        this.menuRepository = menuRepository;
        this.versionDataInitializer = versionDataInitializer;
        this.menuToMenuDTO = menuToMenuDTO;
        this.languageService = languageService;
    }

    public MenuDTO createData(boolean withMenuItems) {
        cleanRepositories();
        return createData(withMenuItems, MENU_INDEX);
    }

    public MenuDTO createData(boolean withMenuItems, int menuIndex) {
        return createData(withMenuItems, menuIndex, VERSION_INDEX, DOC_ID);
    }

    public MenuDTO createData(boolean withMenuItems, int menuIndex, int versionIndex, int docId) {
        version = versionDataInitializer.createData(versionIndex, docId);
        return createData(withMenuItems, menuIndex, version);
    }

    public MenuDTO createData(boolean withMenuItems, int menuIndex, Version version) {
        createDataEntity(withMenuItems, menuIndex, version);
        return menuToMenuDTO.apply(savedMenu, languageService.findByCode(Imcms.getUser().getLanguage()));
    }

    public Version getVersion() {
        return version;
    }

    public List<MenuItemDTO> getMenuItemDtoList() {
        return menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(savedMenu.getNo(), savedMenu.getVersion())
                .getMenuItems()
                .stream()
                .map(this::mapMenuItems)
                .collect(Collectors.toList());
    }

    @Override
    public void cleanRepositories() {
        versionDataInitializer.cleanRepositories();
        super.cleanRepositories();
    }

    public Menu createDataEntity(boolean withMenuItems) {
        version = versionDataInitializer.createData(VERSION_INDEX, DOC_ID);
        return createDataEntity(withMenuItems, MENU_INDEX, version);
    }

    private Menu createDataEntity(boolean withMenuItems, int menuIndex, Version version) {
        final Menu menu = new Menu();
        menu.setVersion(version);
        menu.setNo(menuIndex);
        savedMenu = menuRepository.saveAndFlush(menu);

        if (withMenuItems) {
            addMenuItemsTo(savedMenu);

        } else {
            savedMenu.setMenuItems(new ArrayList<>());
        }

        return savedMenu;
    }

    private MenuItemDTO mapMenuItems(MenuItem menuItem) {
        final MenuItemDTO menuItemDTO = new MenuItemDTO();
        menuItemDTO.setDocumentId(menuItem.getDocumentId());
        menuItemDTO.setTitle("Start page");
        menuItemDTO.setChildren(menuItem.getChildren().stream()
                .map(this::mapMenuItems)
                .collect(Collectors.toList()));
        return menuItemDTO;
    }

    private void addMenuItemsTo(Menu menu) {
        final List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(createMenuItem(1));
        menuItems.add(createMenuItem(2));

        final MenuItem menuItem0 = menuItems.get(0);

        menuItem0.setChildren(Arrays.asList(
                createMenuItem(1),
                createMenuItem(2),
                createMenuItem(3)
        ));

        menuItem0.getChildren().get(0).setChildren(Arrays.asList(
                createMenuItem(1),
                createMenuItem(2),
                createMenuItem(3)
        ));


        menu.setMenuItems(menuItems);

        menuRepository.saveAndFlush(menu);
    }

    private MenuItem createMenuItem(int sortOrder) {
        final MenuItem menuItem = new MenuItem();
        menuItem.setSortOrder(sortOrder);
        menuItem.setDocumentId(DOC_ID);
        return menuItem;
    }
}
