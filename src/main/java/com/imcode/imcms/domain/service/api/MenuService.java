package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.core.CommonContentService;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.domain.service.exception.MenuNotExistException;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.repository.MenuRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
    private final Function<MenuItem, MenuItemDTO> mapper;

    public MenuService(MenuRepository menuRepository,
                       VersionService versionService,
                       CommonContentService commonContentService,
                       Function<MenuItem, MenuItemDTO> mapper) {
        this.menuRepository = menuRepository;
        this.versionService = versionService;
        this.commonContentService = commonContentService;
        this.mapper = mapper;
    }

    public List<MenuItemDTO> getMenuItemsOf(int menuNo, int docId) {
        final Version workingVersion = versionService.getDocumentWorkingVersion(docId);
        final UserDomainObject user = Imcms.getUser();
        final Menu menu = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menuNo, workingVersion);

        return Optional.ofNullable(menu)
                .orElseThrow(() -> new MenuNotExistException(menuNo, docId))
                .getMenuItems()
                .stream()
                .map(mapper)
                .peek(menuItemDTO -> addTitleToMenuItem(menuItemDTO, user))
                .collect(Collectors.toList());
    }

    private void addTitleToMenuItem(MenuItemDTO menuItemDTO, UserDomainObject user) {
        final Version menuItemVersion = versionService.getDocumentWorkingVersion(menuItemDTO.getDocumentId());
        final CommonContent commonContent = commonContentService
                .findByDocIdAndVersionNoAndUserDomainObject(menuItemVersion.getDocId(), menuItemVersion.getNo(), user);

        menuItemDTO.setTitle(commonContent.getHeadline());

        menuItemDTO.getChildren()
                .forEach(childMenuItemDTO -> addTitleToMenuItem(childMenuItemDTO, user));
    }

}
