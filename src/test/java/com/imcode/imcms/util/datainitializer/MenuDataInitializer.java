package com.imcode.imcms.util.datainitializer;

import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.repository.MenuRepository;
import com.imcode.imcms.util.RepositoryCleaner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MenuDataInitializer extends RepositoryCleaner {

    private final MenuRepository menuRepository;
    private final VersionDataInitializer versionDataInitializer;
    private Menu savedMenu;

    public MenuDataInitializer(@Qualifier("com.imcode.imcms.persistence.repository.MenuRepository") MenuRepository menuRepository,
                               VersionDataInitializer versionDataInitializer) {
        this.menuRepository = menuRepository;
        this.versionDataInitializer = versionDataInitializer;
    }

    public Menu createData(boolean withMenuItems) {
        cleanRepositories();
        final Menu menu = new Menu();
        final Version version = versionDataInitializer.createData(0, 1001);
        menu.setVersion(version);
        menu.setNo(1);
        savedMenu = menuRepository.saveAndFlush(menu);
        if (withMenuItems) {
            addMenuItemsTo(savedMenu);
        }
        return savedMenu;
    }

    public List<MenuItemDTO> expectedMenuItems() {
        return savedMenu.getMenuItems().stream()
                .map(this::mapMenuItems)
                .collect(Collectors.toList());
    }

    private MenuItemDTO mapMenuItems(MenuItem menuItem) {
        final MenuItemDTO menuItemDTO = new MenuItemDTO();
        menuItemDTO.setId(menuItem.getId());
        menuItemDTO.setDocumentId(menuItem.getDocumentId());
        menuItemDTO.setTitle("Start page");
        menuItemDTO.setChildren(menuItem.getChildren().stream()
                .map(this::mapMenuItems)
                .collect(Collectors.toList()));
        return menuItemDTO;
    }

    @Override
    public void cleanRepositories() {
        versionDataInitializer.cleanRepositories();
        cleanRepositories(menuRepository);
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

    public MenuItem createMenuItem(int sortOrder) {
        final MenuItem menuItem = new MenuItem();
        menuItem.setSortOrder(sortOrder);
        menuItem.setDocumentId(1001);
        return menuItem;
    }
}
