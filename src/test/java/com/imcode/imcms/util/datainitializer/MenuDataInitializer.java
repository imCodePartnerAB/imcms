package com.imcode.imcms.util.datainitializer;

import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuHistory;
import com.imcode.imcms.persistence.repository.MenuHistoryRepository;
import com.imcode.imcms.persistence.repository.MenuRepository;
import com.imcode.imcms.util.RepositoryCleaner;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

@Component
public class MenuDataInitializer implements RepositoryCleaner {

    @Autowired
    @Qualifier("com.imcode.imcms.persistence.repository.MenuRepository")
    private MenuRepository menuRepository;

    @Autowired
    @Qualifier("com.imcode.imcms.persistence.repository.MenuHistoryRepository")
    private MenuHistoryRepository menuHistoryRepository;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    public Menu createMenu() {
        cleanRepositories();
        final Menu menu = new Menu();
        final Version version = versionDataInitializer.createData(0, 1001);
        menu.setVersion(version);
        menu.setNo(1);
        return menuRepository.saveAndFlush(menu);
    }

    public MenuHistory createMenuHistory() throws InvocationTargetException, IllegalAccessException {
        final Menu menu = createMenu();
        final MenuHistory menuHistory = new MenuHistory();
        BeanUtils.copyProperties(menuHistory, menu);
        menuHistory.setUserId(0);
        menuHistory.setModifiedDate(new Date());
        return menuHistoryRepository.saveAndFlush(menuHistory);
    }

    @Override
    public void cleanRepositories() {
        versionDataInitializer.cleanRepositories();
        cleanRepositories(menuRepository, menuHistoryRepository);
    }

}
