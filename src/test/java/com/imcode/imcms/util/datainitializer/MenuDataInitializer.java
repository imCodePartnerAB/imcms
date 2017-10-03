package com.imcode.imcms.util.datainitializer;

import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.repository.MenuRepository;
import com.imcode.imcms.util.RepositoryCleaner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MenuDataInitializer implements RepositoryCleaner {

    private final MenuRepository menuRepository;
    private final VersionDataInitializer versionDataInitializer;

    public MenuDataInitializer(@Qualifier("com.imcode.imcms.persistence.repository.MenuRepository") MenuRepository menuRepository,
                               VersionDataInitializer versionDataInitializer) {
        this.menuRepository = menuRepository;
        this.versionDataInitializer = versionDataInitializer;
    }

    public Menu createMenu() {
        cleanRepositories();
        final Menu menu = new Menu();
        final Version version = versionDataInitializer.createData(0, 1001);
        menu.setVersion(version);
        menu.setNo(1);
        return menuRepository.saveAndFlush(menu);
    }

    @Override
    public void cleanRepositories() {
        versionDataInitializer.cleanRepositories();
        cleanRepositories(menuRepository);
    }
}
