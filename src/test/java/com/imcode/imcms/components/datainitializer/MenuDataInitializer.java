package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MenuRepository;
import com.imcode.imcms.sorted.TypeSort;
import imcode.server.Imcms;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.imcode.imcms.sorted.TypeSort.MANUAL;
import static com.imcode.imcms.sorted.TypeSort.TREE_SORT;

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
    private DocumentDataInitializer documentDataInitializer;
    private DocumentService<DocumentDTO> documentService;

    public MenuDataInitializer(MenuRepository menuRepository,
                               VersionDataInitializer versionDataInitializer,
                               BiFunction<Menu, Language, MenuDTO> menuToMenuDTO,
                               LanguageService languageService,
                               DocumentDataInitializer documentDataInitializer,
                               DocumentService<DocumentDTO> documentService) {
        super(menuRepository);
        this.menuRepository = menuRepository;
        this.versionDataInitializer = versionDataInitializer;
        this.menuToMenuDTO = menuToMenuDTO;
        this.languageService = languageService;
        this.documentDataInitializer = documentDataInitializer;
        this.documentService = documentService;
    }

    public MenuDTO createData(boolean withMenuItems, boolean nested, String typeSort, int count) {
        cleanRepositories();
        return createData(withMenuItems, MENU_INDEX, nested, typeSort, count);
    }

    public MenuDTO createData(boolean withMenuItems, int menuIndex,
                              boolean nested, String typeSort, int count) {
        return createData(withMenuItems, menuIndex, VERSION_INDEX, DOC_ID, nested, typeSort, count);
    }

    public MenuDTO createData(boolean withMenuItems, int menuIndex, int versionIndex,
                              int docId, boolean nested, String typeSort, int count) {
        version = versionDataInitializer.createData(versionIndex, docId);
        return createData(withMenuItems, menuIndex, version, nested, typeSort, count);
    }

    public MenuDTO createData(boolean withMenuItems, int menuIndex, Version version,
                              boolean nested, String typeSort, int count) {
        val menu = createDataEntity(withMenuItems, menuIndex, version, nested, typeSort, count);
        final MenuDTO menuDTO = menuToMenuDTO.apply(menu, languageService.findByCode(Imcms.getUser().getLanguage()));
        menuDTO.setTypeSort(typeSort);
        return menuDTO;
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

    public Menu createDataEntity(boolean withMenuItems, boolean nested, String typeSort, int count) {
        version = versionDataInitializer.createData(VERSION_INDEX, DOC_ID);
        return createDataEntity(withMenuItems, MENU_INDEX, version, nested, typeSort, count);
    }

    private Menu createDataEntity(boolean withMenuItems, int menuIndex, Version version,
                                  boolean nested, String typeSort, int count) {
        final Menu menu = new Menu();
        menu.setVersion(version);
        menu.setNo(menuIndex);
        menu.setNested(nested);
        savedMenu = menuRepository.saveAndFlush(menu);

        if (withMenuItems) {
            addMenuItemsTo(savedMenu, nested, count, getTypeSort(typeSort, nested));
        }

        return savedMenu;
    }

    private String getTypeSort(String typeSort, boolean nested) {
        if (typeSort == null) {
            if (nested) {
                typeSort = String.valueOf(TypeSort.TREE_SORT);
            } else {
                typeSort = String.valueOf(MANUAL);
            }
        }

        return typeSort;
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

    private void addMenuItemsTo(Menu menu, boolean nested, int count, String typeSort) {
        final List<MenuItem> menuItems = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            final int sort = i + 1;
            menuItems.add(createMenuItem(sort));
        }

        if (nested && typeSort.equals(String.valueOf(TREE_SORT))) {
            final MenuItem menuItem0 = menuItems.get(0);

            final List<MenuItem> menuItems1 = Arrays.asList(
                    createMenuItem(1),
                    createMenuItem(2),
                    createMenuItem(3)
            );
            menuItem0.setChildren(new LinkedHashSet<>(menuItems1));

            final List<MenuItem> menuItems2 = Arrays.asList(
                    createMenuItem(1),
                    createMenuItem(2),
                    createMenuItem(3)
            );
            new ArrayList<>(menuItem0.getChildren()).get(0).setChildren(new LinkedHashSet<>(menuItems2));
        }

        menu.setMenuItems(new LinkedHashSet<>(menuItems));

        menuRepository.saveAndFlush(menu);
    }

    private MenuItem createMenuItem(int sortOrder) {
        documentDataInitializer.cleanRepositories();
        final DocumentDTO initDoc = documentDataInitializer.createData();
        documentService.publishDocument(initDoc.getId(), Imcms.getUser().getId());
        final MenuItem menuItem = new MenuItem();
        menuItem.setSortOrder(sortOrder);
        menuItem.setDocumentId(initDoc.getId());
        return menuItem;
    }

    public MenuItemDTO createMenuItemDTO(String sortNumber) {
        documentDataInitializer.cleanRepositories();
        final DocumentDTO initDoc = documentDataInitializer.createData();
        documentService.publishDocument(initDoc.getId(), Imcms.getUser().getId());
        final MenuItemDTO menuItem = new MenuItemDTO();
        menuItem.setDocumentId(initDoc.getId());
        menuItem.setSortNumber(sortNumber);
        return menuItem;
    }
}
