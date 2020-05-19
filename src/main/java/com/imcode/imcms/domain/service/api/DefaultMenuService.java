package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.MenuElementHtmlWrapper;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.stream.IntStream;

import static com.imcode.imcms.components.MenuElementHtmlWrapper.*;
import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;

@Service
@Transactional
public class DefaultMenuService extends AbstractVersionedContentService<Menu, MenuRepository>
        implements IdDeleterMenuService {

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
    private final MenuElementHtmlWrapper menuElementHtmlWrapper;

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
                       Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem,
                       MenuElementHtmlWrapper menuElementHtmlWrapper) {

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
        this.menuElementHtmlWrapper = menuElementHtmlWrapper;
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
            menuItemsOf = convertItemsToFlatList(menuItemsOf);
        }

        if (!nested && typeSort.equals(String.valueOf(TypeSort.TREE_SORT))) {
            throw new SortNotSupportedException("Current sorting don't support in menuIndex: " + menuIndex);
        }

        return getSortingMenuItemsByTypeSort(typeSort, menuItemsOf);
    }

    @Override
    public List<MenuItemDTO> getSortedMenuItems(MenuDTO menuDTO, String langCode) {
        List<MenuItemDTO> menuItems = menuDTO.getMenuItems();
        if (!menuDTO.isNested() && menuDTO.getTypeSort().equals(String.valueOf(TypeSort.TREE_SORT))) {
            throw new SortNotSupportedException("Current sorting don't support in flat menu!");
        }

        if (!menuDTO.isNested() || !menuDTO.getTypeSort().equals(String.valueOf(TypeSort.TREE_SORT))) {
            menuItems = convertItemsToFlatList(menuDTO.getMenuItems());
        }

        final Language language = languageService.findByCode(langCode);
        //double map because from client to fetch itemsDTO which have only doc id and no more info..
        final List<MenuItemDTO> menuItemsDTO = menuItems.stream()
                .map(menuItemDtoToMenuItem)
                .map(menuItem -> menuItemToDTO.apply(menuItem, language))
                .collect(Collectors.toList());

        setHasNewerVersionsInItems(menuItemsDTO);

        return getSortingMenuItemsByTypeSort(menuDTO.getTypeSort(), menuItemsDTO);
    }

    @Override
    public List<MenuItemDTO> getVisibleMenuItems(int docId, int menuIndex, String language, boolean nested) {
        List<MenuItemDTO> menuItemsOf = getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true);
        if (!nested) {
            menuItemsOf = convertItemsToFlatList(menuItemsOf);
        }

        setHasNewerVersionsInItems(menuItemsOf);

        return !nested ? getAndSetUpEmptyChildrenMenuItems(menuItemsOf) : menuItemsOf;
    }

    @Override
    public List<MenuItemDTO> getPublicMenuItems(int docId, int menuIndex, String language, boolean nested) {
        List<MenuItemDTO> menuItemsOf = getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC, language, true);
        if (!nested) {
            menuItemsOf = convertItemsToFlatList(menuItemsOf);
        }

        setHasNewerVersionsInItems(menuItemsOf);

        return !nested ? getAndSetUpEmptyChildrenMenuItems(menuItemsOf) : menuItemsOf;
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex, String language,
                                       boolean nested, String attributes, String treeKey, String wrap) {
        List<MenuItemDTO> menuItemsOf = getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true);
        if (!nested) {
            menuItemsOf = getAndSetUpEmptyChildrenMenuItems(convertItemsToFlatList(menuItemsOf));
        }

        setHasNewerVersionsInItems(menuItemsOf);
        List<MenuItemDTO> startedMenuItems = getStartedMenuItemsOf(getFlatMenuItemsWithIndex(menuItemsOf));

        return convertToMenuHtml(docId, menuIndex, startedMenuItems, nested, attributes, treeKey, wrap);
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
    public List<String> getMenuItemsAsHtmlByData(String dataClass) {
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

    private List<MenuItemDTO> getAndSetUpEmptyChildrenMenuItems(List<MenuItemDTO> menuItemDTOs) {
        return menuItemDTOs.stream()
                .peek(menuItem -> {
                    if (!menuItem.getChildren().isEmpty()) {
                        menuItem.setChildren(Collections.emptyList());
                    }
                })
                .collect(Collectors.toList());
    }

    private void setHasNewerVersionsInItems(List<MenuItemDTO> items) {
        items.stream()
                .flatMap(MenuItemDTO::flattened)
                .peek(docItem ->
                        docItem.setHasNewerVersion(versionService.hasNewerVersion(docItem.getDocumentId()))
                );
    }

    private List<MenuItemDTO> convertItemsToFlatList(List<MenuItemDTO> menuItems) {
        return menuItems.stream()
                .flatMap(MenuItemDTO::flattened)
                .distinct()
                .collect(Collectors.toList());
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

    private List<MenuItemDTO> getFlatMenuItemsWithIndex(List<MenuItemDTO> menuItems) {
        List<MenuItemDTO> flatMenuItems = convertItemsToFlatList(menuItems);

        return IntStream.range(0, flatMenuItems.size())
                .mapToObj(i -> {
                    MenuItemDTO menuItemDTO = flatMenuItems.get(i);
                    menuItemDTO.setDataIndex(i);
                    return menuItemDTO;
                }).collect(Collectors.toList());

    }

    private List<MenuItemDTO> getStartedMenuItemsOf(List<MenuItemDTO> menuItems) {
        List<MenuItemDTO> currentMenuItems = new ArrayList<>();
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getChildren().isEmpty()) {
                currentMenuItems.add(menuItems.get(i));
            } else {
                currentMenuItems.add(menuItems.get(i));
                long amountSkipElement = menuItems.get(i).getChildren().stream().flatMap(MenuItemDTO::flattened).count();
                i += amountSkipElement;
            }
        }
        return currentMenuItems;
    }

    private String convertToMenuHtml(int docId, int menuIndex, List<MenuItemDTO> menuItemDTOS, boolean nested,
                                     String attributes, String treeKey, String wrap) {
        List<MenuItemDTO> menuItems;
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

        List<String> wrappers = StringUtils.isBlank(wrap) ? Collections.EMPTY_LIST : Arrays.asList(wrap.split(","));
        Collections.reverse(wrappers);

        for (int i = 0; i < menuItems.size(); i++) {
            String dataTreeKey = StringUtils.isBlank(treeKey) ? ((i + 1) * 10) + "" : treeKey + "." + ((i + 1) * 10);

            buildContentMenu.append(getBuiltMainParentMenuItem(menuItems.get(i), attributes, dataTreeKey, 1, wrappers));

            if (!menuItems.get(i).getChildren().isEmpty()) {
                buildChildrenContentMenuItem(buildContentMenu.append(UL_TAG_OPEN), menuItems.get(i).getChildren(),
                        dataTreeKey, 2, wrappers);
            }
        }
        buildContentMenu.append(UL_TAG_CLOSE);

        return String.format(buildContentMenu.toString(), menuIndex, docId);
    }

    private String getBuiltMainParentMenuItem(MenuItemDTO parentMenuItem, String attributes,
                                              String treeKey, Integer dataLevel, List<String> wrappers) {
        StringBuilder content = new StringBuilder();
        if (attributes.contains("data")) {
            String htmlContentItemElement = String.format(
                    "<li %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\">%s",
                    DATA_META_ID_ATTRIBUTE, parentMenuItem.getDocumentId(),
                    DATA_INDEX_ATTRIBUTE, parentMenuItem.getDataIndex(),
                    DATA_TREEKEY_ATTRIBUTE, treeKey,
                    DATA_LEVEL_ATTRIBUTE, dataLevel,
                    DATA_SUBLEVELS_ATTRIBUTE, !parentMenuItem.getChildren().isEmpty(),
                    parentMenuItem.getTitle());


            htmlContentItemElement = menuElementHtmlWrapper.getWrappedContent(htmlContentItemElement, wrappers, parentMenuItem);

            content.append(htmlContentItemElement);
        }

        return content.toString();
    }

    private void buildChildrenContentMenuItem(StringBuilder contentMenu, List<MenuItemDTO> childrenItems,
                                              String treeKey, Integer dataLvl, List<String> wrappers) {

        StringBuilder contentMenuItems = new StringBuilder();
        for (int i = 0; i < childrenItems.size(); i++) {
            if (!childrenItems.get(i).getChildren().isEmpty()) {

                String htmlContentMenuItem = String.format(
                        "<li %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\">%s",
                        DATA_META_ID_ATTRIBUTE, childrenItems.get(i).getDocumentId(),
                        DATA_INDEX_ATTRIBUTE, childrenItems.get(i).getDataIndex(),
                        DATA_TREEKEY_ATTRIBUTE, treeKey + "." + ((i + 1) * 10),
                        DATA_LEVEL_ATTRIBUTE, dataLvl,
                        DATA_SUBLEVELS_ATTRIBUTE, !childrenItems.get(i).getChildren().isEmpty(),
                        childrenItems.get(i).getDocumentId());

                htmlContentMenuItem = menuElementHtmlWrapper.getWrappedContent(htmlContentMenuItem, wrappers, childrenItems.get(i));
                contentMenu.append(htmlContentMenuItem);

                buildChildrenContentMenuItem(contentMenu.append(UL_TAG_OPEN), childrenItems.get(i).getChildren(),
                        treeKey + "." + ((i + 1) * 10), dataLvl + 1, wrappers);
            } else {
                contentMenuItems.append(getBuildContentMenuItem(childrenItems.get(i), dataLvl,
                        treeKey + "." + ((i + 1) * 10), wrappers));
            }
        }
        contentMenuItems.append(UL_TAG_CLOSE);
        contentMenu.append(contentMenuItems);
    }

    private String getBuildContentMenuItem(MenuItemDTO itemDTO, Integer dataLvl, String treeKey, List<String> wrappers) {
        String contentMenuItem = String.format(
                "<li %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\">%s</li>",
                DATA_META_ID_ATTRIBUTE, itemDTO.getDocumentId(),
                DATA_INDEX_ATTRIBUTE, itemDTO.getDataIndex(),
                DATA_TREEKEY_ATTRIBUTE, treeKey,
                DATA_LEVEL_ATTRIBUTE, dataLvl,
                DATA_SUBLEVELS_ATTRIBUTE, !itemDTO.getChildren().isEmpty(),
                itemDTO.getDocumentId()).concat("\n");

        contentMenuItem = menuElementHtmlWrapper.getWrappedContent(contentMenuItem, wrappers, itemDTO);

        return contentMenuItem;
    }
}
