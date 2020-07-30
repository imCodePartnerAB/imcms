package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.MenuHtmlConverter;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.stream.IntStream;

import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
import static com.imcode.imcms.sorted.TypeSort.MANUAL;
import static com.imcode.imcms.sorted.TypeSort.TREE_SORT;

@Service
@Slf4j
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
    private final MenuHtmlConverter menuHtmlConverter;

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
                       MenuHtmlConverter menuHtmlConverter) {

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
        this.menuHtmlConverter = menuHtmlConverter;
        this.menuSaver = (menu, language) -> menuToMenuDTO.apply(menuRepository.save(menu), language);
    }

    @Override
    public List<MenuItemDTO> getMenuItems(int docId, int menuIndex, String language, boolean nested, String typeSort) {
        if (typeSort == null) {
            if (nested) {
                typeSort = String.valueOf(TypeSort.TREE_SORT);
            } else {
                typeSort = String.valueOf(MANUAL);
            }
        }

        List<MenuItemDTO> menuItemsOf;

        if (typeSort.equals(String.valueOf(TREE_SORT))) {
            menuItemsOf = getNumberSortMenuItems(getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, false), typeSort);
        } else {
            menuItemsOf = getNumberSortMenuItems(convertItemsToFlatList(
                    getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, false)), typeSort);
        }

        menuItemsOf.stream()
                .flatMap(MenuItemDTO::flattened)
                .forEach(item -> log.error("Method getMenuItems, docId {} and sort-number {}", item.getDocumentId(), item.getSortNumber()));

        setHasNewerVersionsInItems(menuItemsOf);

        if (!nested && typeSort.equals(String.valueOf(TypeSort.TREE_SORT))) {
            throw new SortNotSupportedException("Current sorting don't support in menuIndex: " + menuIndex);
        }

        return getSortingMenuItemsByTypeSort(typeSort, menuItemsOf);
    }

    @Override
    public List<MenuItemDTO> getSortedMenuItems(MenuDTO menuDTO, String langCode) {

        if (!menuDTO.isNested() && menuDTO.getTypeSort().equals(String.valueOf(TypeSort.TREE_SORT))) {
            throw new SortNotSupportedException("Current sorting don't support in flat menu!");
        }

        final String typeSort = menuDTO.getTypeSort();
        List<MenuItemDTO> menuItems;
        if (typeSort.equals(String.valueOf(TREE_SORT))) {
            menuItems = getNumberSortMenuItems(menuDTO.getMenuItems(), typeSort);
        } else {
            menuItems = getNumberSortMenuItems(convertItemsToFlatList(menuDTO.getMenuItems()), typeSort);
        }

        final Language language = languageService.findByCode(langCode);
        //double map because from client to fetch itemsDTO which have only doc id and no more info..
        final List<MenuItemDTO> menuItemsDTO = menuItems.stream()
                .map(menuItemDtoToMenuItem)
                .map(menuItem -> menuItemToDTO.apply(menuItem, language))
                .collect(Collectors.toList());

        setHasNewerVersionsInItems(menuItemsDTO);

        return getSortingMenuItemsByTypeSort(typeSort, menuItemsDTO);
    }

    @Override
    public List<MenuItemDTO> getVisibleMenuItems(int docId, int menuIndex, String language, boolean nested) {
        List<MenuItemDTO> menuItemsOf = getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true);

        menuItemsOf.stream()
                .flatMap(MenuItemDTO::flattened)
                .forEach(item -> log.error("Method getVisibleMenuItems, docId {} and sort-number {}", item.getDocumentId(), item.getSortNumber()));

        if (!nested) {
            menuItemsOf = convertItemsToFlatList(menuItemsOf);
        }

        setHasNewerVersionsInItems(menuItemsOf);

        return menuItemsOf;
    }

    @Override
    public List<MenuItemDTO> getPublicMenuItems(int docId, int menuIndex, String language, boolean nested) {
        List<MenuItemDTO> menuItemsOf = getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC, language, true);

        menuItemsOf.stream()
                .flatMap(MenuItemDTO::flattened)
                .forEach(item -> log.error("Method getPublicMenuItems, docId {} and sort-number {}", item.getDocumentId(), item.getSortNumber()));

        if (!nested) {
            menuItemsOf = convertItemsToFlatList(menuItemsOf);
        }

        setHasNewerVersionsInItems(menuItemsOf);

        return menuItemsOf;
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex, String language,
                                       boolean nested, String attributes, String treeKey, String wrap) {
        List<MenuItemDTO> menuItemsOf = getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true);
        if (!nested) {
            menuItemsOf = convertItemsToFlatList(menuItemsOf);
        }

        setHasNewerVersionsInItems(menuItemsOf);
        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(docId, menuIndex, startedMenuItems, nested, attributes, treeKey, wrap);
    }

    @Override
    public String getPublicMenuAsHtml(int docId, int menuIndex, String language,
                                      boolean nested, String attributes, String treeKey, String wrap) {
        List<MenuItemDTO> menuItemsOf = getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC, language, true);
        if (!nested) {
            menuItemsOf = convertItemsToFlatList(menuItemsOf);
        }

        setHasNewerVersionsInItems(menuItemsOf);
        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(docId, menuIndex, startedMenuItems, nested, attributes, treeKey, wrap);
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex) {
        final String language = Imcms.getUser().getLanguage();
        List<MenuItemDTO> menuItemsOf = convertItemsToFlatList(
                getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true));

        setHasNewerVersionsInItems(menuItemsOf);
        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(
                docId, menuIndex, startedMenuItems, false, null, null, null
        );
    }

    @Override
    public String getPublicMenuAsHtml(int docId, int menuIndex) {
        final String language = Imcms.getUser().getLanguage();
        List<MenuItemDTO> menuItemsOf = convertItemsToFlatList(
                getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC, language, true));

        setHasNewerVersionsInItems(menuItemsOf);
        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(
                docId, menuIndex, startedMenuItems, false, null, null, null
        );
    }

    @Override
    @Transactional
    public MenuDTO saveFrom(MenuDTO menuDTO) {

        final Integer docId = menuDTO.getDocId();
        final Menu menu = Optional.ofNullable(getMenu(menuDTO.getMenuIndex(), docId))
                .orElseGet(() -> createMenu(menuDTO));


        final String typeSort = menuDTO.getTypeSort();
        menu.setNested(menuDTO.isNested());
        menu.setTypeSort(typeSort);
        menu.setMenuItems(menuItemDtoListToMenuItemList.apply(getNumberSortMenuItems(menuDTO.getMenuItems(), typeSort)));


        menu.getMenuItems().stream()
                .flatMap(MenuItem::flattened)
                .forEach(item -> log.error("Method save (BEFORE SAVE), docId {} and sort-number {}", item.getDocumentId(), item.getSortNumber()));

        final MenuDTO savedMenu = menuSaver.apply(menu, languageService.findByCode(Imcms.getUser().getLanguage()));

        savedMenu.getMenuItems().stream()
                .flatMap(MenuItemDTO::flattened)
                .forEach(item -> log.error("Method save (AFTER SAVE), docId {} and sort-number {}", item.getDocumentId(), item.getSortNumber()));

        super.updateWorkingVersion(docId);

        getMenuItemsOf(savedMenu.getMenuIndex(), savedMenu.getDocId(), MenuItemsStatus.ALL, Imcms.getLanguage().getCode(), true)
                .stream()
                .flatMap(MenuItemDTO::flattened)
                .forEach(item -> log.error("Method save (AFTER updateWorkingVersion), docId {} and sort-number {}", item.getDocumentId(), item.getSortNumber()));

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
                    log.error("Method getMenuItemsOf,in menuItem docId {}, children is empty: {}",
                            menuItemDTO.getDocumentId(), menuItemDTO.getChildren().isEmpty());
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
                .peek(item -> item.setChildren(Collections.emptyList()))
                .collect(Collectors.toList());
    }

    private List<MenuItemDTO> getSortingMenuItemsByTypeSort(String typeSort, List<MenuItemDTO> menuItems) {
        switch (TypeSort.valueOf(typeSort)) {
            case TREE_SORT:
                return menuItems;
            case MANUAL:
                return convertItemsToFlatList(menuItems);
            case ALPHABETICAL_ASC:
                return menuItems.stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getTitle,
                                Comparator.nullsLast(String::compareToIgnoreCase)))
                        .collect(Collectors.toList());
            case ALPHABETICAL_DESC:
                return menuItems.stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getTitle,
                                Comparator.nullsLast(String::compareToIgnoreCase)).reversed())
                        .collect(Collectors.toList());
            case PUBLISHED_DATE_ASC:
                return menuItems.stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getPublishedDate,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .collect(Collectors.toList());
            case PUBLISHED_DATE_DESC:
                return menuItems.stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getPublishedDate,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList());
            case MODIFIED_DATE_ASC:
                return menuItems.stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getModifiedDate,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .collect(Collectors.toList());
            case MODIFIED_DATE_DESC:
                return menuItems.stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getModifiedDate,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList());
            default:
                return Collections.EMPTY_LIST;//never come true...
        }
    }

    private List<MenuItemDTO> getMenuItemsWithIndex(List<MenuItemDTO> menuItems) {
        final List<MenuItemDTO> resultMenuItems = convertItemsToFlatList(menuItems);

        return IntStream.range(0, resultMenuItems.size())
                .mapToObj(i -> {
                    MenuItemDTO menuItemDTO = resultMenuItems.get(i);
                    menuItemDTO.setDataIndex(i);
                    return menuItemDTO;
                }).collect(Collectors.toList());

    }

    private List<MenuItemDTO> getFirstMenuItemsOf(List<MenuItemDTO> menuItems) {
        List<MenuItemDTO> currentMenuItems = new ArrayList<>();
        for (int i = 0; i < menuItems.size(); i++) {
            final MenuItemDTO currentItemDTO = menuItems.get(i);
            if (currentItemDTO.getChildren().isEmpty()) {
                currentMenuItems.add(currentItemDTO);
            } else {
                currentMenuItems.add(currentItemDTO);
                long amountSkipElement = currentItemDTO.getChildren().stream().flatMap(MenuItemDTO::flattened).count();
                i += amountSkipElement;
            }
        }
        return currentMenuItems;
    }

    private void setSortNumbersInMenuItems(List<MenuItemDTO> menuItemDTOs, String treeKey, String typeSort, boolean isEmptyItemSortNumber) {
        boolean flagIncrement = false;
        for (int i = 0; i < menuItemDTOs.size(); i++) {
            final MenuItemDTO menuItemDTO = menuItemDTOs.get(i);
            isEmptyItemSortNumber = StringUtils.isBlank(menuItemDTO.getSortNumber());
            final boolean hasChildren = !menuItemDTO.getChildren().isEmpty();
            String dataTreeKey = StringUtils.isBlank(treeKey) ? (i + 1) + "" : treeKey + "." + (i + 1);

            if (isNotWholeNumberAndNotTreeSortType(typeSort, menuItemDTO, flagIncrement)) {
                menuItemDTO.setSortNumber(dataTreeKey);
                flagIncrement = true;
            } else if (isEmptyItemSortNumber) {
                menuItemDTO.setSortNumber(dataTreeKey);
            }

            if (hasChildren) {
                dataTreeKey = isEmptyItemSortNumber ? dataTreeKey : menuItemDTO.getSortNumber();
                setSortNumbersInMenuItems(menuItemDTO.getChildren(), dataTreeKey, typeSort, isEmptyItemSortNumber);
            }
        }
    }

    private boolean isNotWholeNumberAndNotTreeSortType(String typeSort, MenuItemDTO menuItemDTO, boolean flagIncrement) {
        return (!typeSort.equals(String.valueOf(TREE_SORT)) && !StringUtils.isNumeric(menuItemDTO.getSortNumber()))
                || (flagIncrement);
    }


    private List<MenuItemDTO> getNumberSortMenuItems(List<MenuItemDTO> menuItemDTOs, String typeSort) {
        setSortNumbersInMenuItems(menuItemDTOs, null, typeSort, false);

        final List<MenuItemDTO> sortedFlatMenuItems = convertItemsToFlatList(menuItemDTOs).stream()
                .sorted(Comparator.comparing(MenuItemDTO::getSortNumber))
                .collect(Collectors.toList());

        final List<MenuItemDTO> newSortedMenuItems = new ArrayList<>();

        sortedFlatMenuItems.forEach(mainItemDTO -> {

            final List<MenuItemDTO> children = sortedFlatMenuItems.stream()
                    .filter(item -> item.getSortNumber().matches(mainItemDTO.getSortNumber().concat(".\\d")))
                    .collect(Collectors.toList());

            if (!existMenuItemsInNewList(newSortedMenuItems, children)) {  //if in list doesn't exist children we can add to list it
                mainItemDTO.setChildren(children);
            }
            if (!existMenuItemInNewList(newSortedMenuItems, mainItemDTO)) {
                newSortedMenuItems.add(mainItemDTO);
            }
        });

        return newSortedMenuItems.stream()
                .sorted(Comparator.comparing(firstItem -> {
                    try {
                        return Integer.parseInt(firstItem.getSortNumber());
                    } catch (NumberFormatException n) {
                        return 0;
                    }
                }))
                .collect(Collectors.toList());
    }

    private boolean existMenuItemsInNewList(List<MenuItemDTO> newSortedItems, List<MenuItemDTO> children) {
        final List<Integer> menuItemsIds = getAllIds(newSortedItems);
        final List<Integer> childrenIds = children.stream()
                .map(MenuItemDTO::getDocumentId)
                .collect(Collectors.toList());

        return menuItemsIds.containsAll(childrenIds);
    }

    private boolean existMenuItemInNewList(List<MenuItemDTO> newSortedItems, MenuItemDTO currentItem) {
        final List<Integer> menuItemsIds = getAllIds(newSortedItems);
        return menuItemsIds.contains(currentItem.getDocumentId());
    }

    private List<Integer> getAllIds(List<MenuItemDTO> newSortedItems) {
        return newSortedItems.stream()
                .flatMap(MenuItemDTO::flattened)
                .map(MenuItemDTO::getDocumentId)
                .collect(Collectors.toList());
    }
}
