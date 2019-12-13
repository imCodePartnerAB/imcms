package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.exception.SortNotSupportedException;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.DocumentMenuService;
import com.imcode.imcms.domain.service.IdDeleterMenuService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MenuRepository;
import com.imcode.imcms.sorted.TypeSort;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;

@Service
@Transactional
public class DefaultMenuService extends AbstractVersionedContentService<Menu, MenuRepository>
        implements IdDeleterMenuService {

    private static final String DATA_META_ID_ATTRIBUTE = "data-meta-id";
    private static final String DATA_INDEX_ATTRIBUTE = "data-index";
    private static final String DATA_TREEKEY_ATTRIBUTE = "data-treekey";
    private static final String DATA_LEVEL_ATTRIBUTE = "data-level";
    private static final String DATA_SUBLEVELS_ATTRIBUTE = "data-sublvls";
    private static final String CLASS_ATTRIBUTE = "class";

    private final VersionService versionService;
    private final DocumentMenuService documentMenuService;
    private final Function<List<MenuItemDTO>, Set<MenuItem>> menuItemDtoListToMenuItemList;
    private final BiFunction<Menu, Language, MenuDTO> menuSaver;
    private final UnaryOperator<MenuItem> toMenuItemsWithoutId;
    private final LanguageService languageService;
    private final BiFunction<MenuItem, Language, MenuItemDTO> menuItemToDTO;
    private final BiFunction<MenuItem, Language, MenuItemDTO> menuItemToMenuItemDtoWithLang;
    private final CommonContentService commonContentService;
    private final Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem;

    DefaultMenuService(MenuRepository menuRepository,
                       VersionService versionService,
                       DocumentMenuService documentMenuService,
                       BiFunction<MenuItem, Language, MenuItemDTO> menuItemToDTO,
                       Function<List<MenuItemDTO>, Set<MenuItem>> menuItemDtoListToMenuItemList,
                       LanguageService languageService,
                       BiFunction<Menu, Language, MenuDTO> menuToMenuDTO,
                       UnaryOperator<MenuItem> toMenuItemsWithoutId,
                       BiFunction<MenuItem, Language, MenuItemDTO> menuItemToMenuItemDtoWithLang,
                       CommonContentService commonContentService,
                       Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem) {

        super(menuRepository);
        this.versionService = versionService;
        this.documentMenuService = documentMenuService;
        this.menuItemToMenuItemDtoWithLang = menuItemToMenuItemDtoWithLang;
        this.menuItemDtoListToMenuItemList = menuItemDtoListToMenuItemList;
        this.menuItemToDTO = menuItemToDTO;
        this.languageService = languageService;
        this.toMenuItemsWithoutId = toMenuItemsWithoutId;
        this.commonContentService = commonContentService;
        this.menuItemDtoToMenuItem = menuItemDtoToMenuItem;
        this.menuSaver = (menu, language) -> menuToMenuDTO.apply(menuRepository.save(menu), language);
    }

    @Override
    public List<MenuItemDTO> getMenuItems(int docId, int menuIndex, String language, boolean nested, String typeSort) {
        List<MenuItemDTO> menuItemsOf = getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, false);
        if (typeSort == null) {
            if (nested) {
                typeSort = String.valueOf(TypeSort.TREE_SORT);
            } else {
                typeSort = String.valueOf(TypeSort.MANUAL);
            }
        }

        setHasNewerVersionsInItems(menuItemsOf);

        if (!nested || !typeSort.equals(String.valueOf(TypeSort.TREE_SORT))) {
            convertItemsToFlatList(menuItemsOf);
        }

        if (!nested && typeSort.equals(String.valueOf(TypeSort.TREE_SORT))) {
            throw new SortNotSupportedException("Current sorting don't support in menuIndex: " + menuIndex);
        }

        return getSortingMenuItemsByTypeSort(typeSort, menuItemsOf);
    }

    @Override
    public List<MenuItemDTO> getSortedMenuItems(MenuDTO menuDTO) {
        if (!menuDTO.isNested() && menuDTO.getTypeSort().equals(String.valueOf(TypeSort.TREE_SORT))) {
            throw new SortNotSupportedException("Current sorting don't support in flat menu!");
        }

        if (!menuDTO.isNested() || !menuDTO.getTypeSort().equals(String.valueOf(TypeSort.TREE_SORT))) {
            convertItemsToFlatList(menuDTO.getMenuItems());
        }

        final Language userLanguage = languageService.findByCode(Imcms.getUser().getLanguage());
        //double map because from client to fetch itemsDTO which have only doc id and no more info..
        final List<MenuItemDTO> menuItemsDTO = menuDTO.getMenuItems().stream()
                .map(menuItemDtoToMenuItem)
                .map(menuItem -> menuItemToDTO.apply(menuItem, userLanguage))
                .collect(Collectors.toList());

        setHasNewerVersionsInItems(menuItemsDTO);

        return getSortingMenuItemsByTypeSort(menuDTO.getTypeSort(), menuItemsDTO);
    }

    private void convertItemsToFlatList(List<MenuItemDTO> menuItems) {
        final List<MenuItemDTO> childrenMenuItems = new ArrayList<>();

        for (MenuItemDTO menuItemDTO : menuItems) {
            childrenMenuItems.addAll(getAllNestedMenuItems(menuItemDTO));
        }

        menuItems.addAll(childrenMenuItems);
    }

    private List<MenuItemDTO> getSortingMenuItemsByTypeSort(String typeSort, List<MenuItemDTO> menuItems) {
        switch (TypeSort.valueOf(typeSort)) {
            case TREE_SORT:
                return menuItems;
            case MANUAL:
                return getAndSetUpEmptyChildrenMenuItems(menuItems);
            case ALPHABETICAL_ASC:
                return getAndSetUpEmptyChildrenMenuItems(menuItems).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getTitle,
                                Comparator.nullsLast(String::compareToIgnoreCase)))
                        .collect(Collectors.toList());
            case ALPHABETICAL_DESC:
                return getAndSetUpEmptyChildrenMenuItems(menuItems).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getTitle,
                                Comparator.nullsLast(String::compareToIgnoreCase)).reversed())
                        .collect(Collectors.toList());
            case PUBLISHED_DATE_ASC:
                return getAndSetUpEmptyChildrenMenuItems(menuItems).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getPublishedDate,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .collect(Collectors.toList());
            case PUBLISHED_DATE_DESC:
                return getAndSetUpEmptyChildrenMenuItems(menuItems).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getPublishedDate,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList());
            case MODIFIED_DATE_ASC:
                return getAndSetUpEmptyChildrenMenuItems(menuItems).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getModifiedDate,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .collect(Collectors.toList());
            case MODIFIED_DATE_DESC:
                return getAndSetUpEmptyChildrenMenuItems(menuItems).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getModifiedDate,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList());
            default:
                return Collections.EMPTY_LIST;//never come true...
        }
    }

    @Override
    public List<MenuItemDTO> getVisibleMenuItems(int docId, int menuIndex, String language, boolean nested) {
        List<MenuItemDTO> menuItemsOf = getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true);
        if (!nested) {
            convertItemsToFlatList(menuItemsOf);
        }

        setHasNewerVersionsInItems(menuItemsOf);

        return !nested ? getAndSetUpEmptyChildrenMenuItems(menuItemsOf) : menuItemsOf;
    }

    @Override
    public List<MenuItemDTO> getPublicMenuItems(int docId, int menuIndex, String language, boolean nested) {
        List<MenuItemDTO> menuItemsOf = getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC, language, true);
        if (!nested) {
            convertItemsToFlatList(menuItemsOf);
        }

        setHasNewerVersionsInItems(menuItemsOf);

        return !nested ? getAndSetUpEmptyChildrenMenuItems(menuItemsOf) : menuItemsOf;
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex, String language,
                                       boolean nested, String attributes, String treeKey, String wrap) {
        List<MenuItemDTO> menuItemsOf = getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true);
        if (!nested) {
            convertItemsToFlatList(menuItemsOf);
        }

        setHasNewerVersionsInItems(menuItemsOf);

        return convertToMenuHtml(docId, menuIndex, menuItemsOf, nested, attributes, treeKey, wrap);
    }

    private String convertToMenuHtml(int docId, int menuIndex, List<MenuItemDTO> menuItemDTOS, boolean nested,
                                     String attributes, String treeKey, String wrap) {
        List<MenuItemDTO> menuItems;
        String ulTag = "<ul>";
        String ulTagClose = "</ul>";
        String ulData = "<ul data-menu-index=\"%d\" data-doc-id=\"%d\">";
        StringBuilder buildContentMenu = new StringBuilder();
        buildContentMenu.append(ulData);

        if (nested) {
            menuItems = menuItemDTOS;
        } else {
            menuItems = menuItemDTOS.stream()
                    .flatMap(MenuItemDTO::flattened)
                    .collect(Collectors.toList());
        }

        String[] wrapElements = wrap.split(",");

        for (MenuItemDTO menuItemDTO : menuItems) {
            if (attributes.contains("data")) {

                String contentItemElement = String.format(
                        "<li %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\">%s</li>",
                        DATA_META_ID_ATTRIBUTE, menuItemDTO.getDocumentId(),
                        DATA_INDEX_ATTRIBUTE, 1,
                        DATA_TREEKEY_ATTRIBUTE, treeKey,
                        DATA_LEVEL_ATTRIBUTE, 1,
                        DATA_SUBLEVELS_ATTRIBUTE, !menuItemDTO.getChildren().isEmpty(),
                        menuItemDTO.getTitle()).concat("\n");

                buildContentMenu.append(contentItemElement);
                if (!menuItemDTO.getChildren().isEmpty()) {
                    buildChildsContentMenuItem(buildContentMenu, menuItemDTO.getChildren(), treeKey, 0);
                }
            }
        }

        buildContentMenu.append(ulTagClose);

        return String.format(buildContentMenu.toString(), menuIndex, docId);
    }

    private String buildChildsContentMenuItem(StringBuilder content, List<MenuItemDTO> childrenItems, String treeKey, int count) {
        String ulTagClose = "</ul>";
        StringBuilder contentBuilder = new StringBuilder();
        int index = 0;
        int countCall = count;

        for (MenuItemDTO itemDTO : childrenItems) {
            if (!itemDTO.getChildren().isEmpty()) {
                content.append(String.format(
                        "<ul><li %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\">%s",
                        DATA_META_ID_ATTRIBUTE, itemDTO.getDocumentId(),
                        DATA_INDEX_ATTRIBUTE, index,
                        DATA_TREEKEY_ATTRIBUTE, treeKey,
                        DATA_LEVEL_ATTRIBUTE, 1,
                        DATA_SUBLEVELS_ATTRIBUTE, !itemDTO.getChildren().isEmpty(),
                        itemDTO.getDocumentId()).concat("\n<ul>"));

                buildChildsContentMenuItem(content, itemDTO.getChildren(), "20", count++);
            } else {
                contentBuilder.append(singleBuildContentItem(itemDTO));
            }
        }
        contentBuilder.append(ulTagClose);
        content.append(contentBuilder);

        return content.toString();
    }

    private String singleBuildContentItem(MenuItemDTO itemDTO) {
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;

        stringBuilder.append(String.format(
                "<li %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\">%s</li>",
                DATA_META_ID_ATTRIBUTE, itemDTO.getDocumentId(),
                DATA_INDEX_ATTRIBUTE, index,
                DATA_TREEKEY_ATTRIBUTE, "k",
                DATA_LEVEL_ATTRIBUTE, 1,
                DATA_SUBLEVELS_ATTRIBUTE, !itemDTO.getChildren().isEmpty(),
                itemDTO.getDocumentId()).concat("\n"));

        return stringBuilder.toString();
    }

    @Override
    public String getPublicMenuAsHtml(int docId, int menuIndex, String language,
                                      boolean nested, String attributes, String treeKey, String wrap) {
        return null;
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex) {
        return null;
    }

    @Override
    public String getPublicMenuAsHtml(int docId, int menuIndex) {
        return null;
    }

    @Override
    public MenuDTO saveFrom(MenuDTO menuDTO) {

        final Integer docId = menuDTO.getDocId();
        final Menu menu = Optional.ofNullable(getMenu(menuDTO.getMenuIndex(), docId))
                .orElseGet(() -> createMenu(menuDTO));

        menu.setNested(menuDTO.isNested());
        menu.setTypeSort(menuDTO.getTypeSort());
        menu.setMenuItems(menuItemDtoListToMenuItemList.apply(menuDTO.getMenuItems()));

        final MenuDTO savedMenu = menuSaver.apply(menu, languageService.findByCode(Imcms.getUser().getLanguage()));

        super.updateWorkingVersion(docId);

        return savedMenu;
    }

    @Override
    @Transactional
    public void deleteByVersion(Version version) {
        repository.deleteByVersion(version);
    }

    @Override
    @Transactional
    public void deleteByDocId(Integer docIdToDelete) {
        repository.deleteByDocId(docIdToDelete);
    }

    @Override
    public Menu removeId(Menu jpa, Version newVersion) {
        final Menu menu = new Menu();
        menu.setId(null);
        menu.setNo(jpa.getNo());
        menu.setVersion(newVersion);
        menu.setNested(jpa.isNested());
        menu.setTypeSort(jpa.getTypeSort());

        final Set<MenuItem> newMenuItems = jpa.getMenuItems()
                .stream()
                .map(toMenuItemsWithoutId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

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

    @Override
    public List<Menu> getAll() {
        return repository.findAll();
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
        final UserDomainObject user = Imcms.getUser();

        final Function<MenuItem, MenuItemDTO> menuItemFunction = isVisible
                ? menuItem -> menuItemToMenuItemDtoWithLang.apply(menuItem, language)
                : menuItem -> menuItemToDTO.apply(menuItem, language);

        return Optional.ofNullable(menu)
                .map(Menu::getMenuItems)
                .orElseGet(LinkedHashSet::new)
                .stream()
                .map(menuItemFunction)
                .filter(Objects::nonNull)
                .filter(menuItemDTO -> (status == MenuItemsStatus.ALL || isPublicMenuItem(menuItemDTO)))
                .filter(menuItemDTO -> documentMenuService.hasUserAccessToDoc(menuItemDTO.getDocumentId(), user))
                .filter(isMenuItemAccessibleForLang(language, versionReceiver))
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

    private Predicate<MenuItemDTO> isMenuItemAccessibleForLang(Language language, Function<Integer, Version> versionReceiver) {
        return menuItemDTO -> {
            final int versionNo = versionService.getVersion(menuItemDTO.getDocumentId(), versionReceiver).getNo();

            final List<CommonContent> menuItemDocContent = commonContentService.getOrCreateCommonContents(menuItemDTO.getDocumentId(), versionNo);

            final List<Language> enabledLanguages = menuItemDocContent.stream()
                    .filter(item -> item.getLanguage().isEnabled())
                    .map(CommonContent::getLanguage)
                    .collect(Collectors.toList());

            final boolean isLanguageEnabled = enabledLanguages.contains(language);
            final boolean isCurrentLangDefault = language.getCode().equals(Imcms.getServices().getLanguageMapper().getDefaultLanguage());
            final boolean isAllowedToShowWithDefaultLanguage = documentMenuService.getDisabledLanguageShowMode(menuItemDTO.getDocumentId()).equals(SHOW_IN_DEFAULT_LANGUAGE);

            return isLanguageEnabled || (!isCurrentLangDefault && isAllowedToShowWithDefaultLanguage);
        };

    }

    private boolean isPublicMenuItem(MenuItemDTO menuItemDTO) {
        return documentMenuService.isPublicMenuItem(menuItemDTO.getDocumentId());
    }

    private List<MenuItemDTO> getAllNestedMenuItems(MenuItemDTO menuItemDTO) {
        List<MenuItemDTO> nestedMenuItems = new ArrayList<>();
        for (MenuItemDTO menuItem : menuItemDTO.getChildren()) {
            if (!menuItem.getChildren().isEmpty()) {
                nestedMenuItems.add(menuItem);
                nestedMenuItems.addAll(getAllNestedMenuItems(menuItem));
            } else {
                nestedMenuItems.add(menuItem);
            }
        }
        return nestedMenuItems;
    }

    private List<MenuItemDTO> getAndSetUpEmptyChildrenMenuItems(List<MenuItemDTO> menuItemDTOs) {
        return menuItemDTOs.stream()
                .peek(menuItem -> {
                    if (!menuItem.getChildren().isEmpty()) {
                        menuItem.setChildren(Collections.emptyList());
                    }
                })
                .collect(Collectors.toList());
    }

    // TODO: 27.11.19 maybe need add new column hasNewerVersion jpa in future?
    private void setHasNewerVersionsInItems(List<MenuItemDTO> items) {
        items.stream()
                .flatMap(MenuItemDTO::flattened)
                .peek(docItem ->
                        docItem.setHasNewerVersion(versionService.hasNewerVersion(docItem.getDocumentId()))
                );
    }
}
