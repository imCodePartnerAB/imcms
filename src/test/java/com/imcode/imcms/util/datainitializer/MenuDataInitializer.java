package com.imcode.imcms.util.datainitializer;

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

@Component
public class MenuDataInitializer implements RepositoryCleaner {

    private final MenuRepository menuRepository;
    private final VersionDataInitializer versionDataInitializer;

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
        final Menu savedMenu = menuRepository.saveAndFlush(menu);
        if (withMenuItems) {
            addMenuItemsTo(savedMenu);
        }
        return savedMenu;
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
