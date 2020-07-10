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
        List<MenuItemDTO> menuItemsOf = getSortedMenuItemsByNumbering(
                getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, false)
        );
        if (typeSort == null) {
            if (nested) {
                typeSort = String.valueOf(TypeSort.TREE_SORT);
            } else {
                typeSort = String.valueOf(TypeSort.MANUAL);
            }
        }

        setHasNewerVersionsInItems(menuItemsOf);

        if (!nested && typeSort.equals(String.valueOf(TypeSort.TREE_SORT))) {
            throw new SortNotSupportedException("Current sorting don't support in menuIndex: " + menuIndex);
        }

        return getSortingMenuItemsByTypeSort(typeSort, menuItemsOf);
    }

    @Override
    public List<MenuItemDTO> getSortedMenuItems(MenuDTO menuDTO, String langCode) {
        List<MenuItemDTO> menuItems = getSortedMenuItemsByNumbering(menuDTO.getMenuItems());

        if (!menuDTO.isNested() && menuDTO.getTypeSort().equals(String.valueOf(TypeSort.TREE_SORT))) {
            throw new SortNotSupportedException("Current sorting don't support in flat menu!");
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
        List<MenuItemDTO> menuItemsOf = getSortedMenuItemsByNumbering(
                getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true)
        );
        if (!nested) {
            menuItemsOf = convertItemsToFlatList(menuItemsOf);
        }

        setHasNewerVersionsInItems(menuItemsOf);

        return menuItemsOf;
    }

    @Override
    public List<MenuItemDTO> getPublicMenuItems(int docId, int menuIndex, String language, boolean nested) {
        List<MenuItemDTO> menuItemsOf = getSortedMenuItemsByNumbering(
                getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC, language, true)
        );

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
        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getFlatMenuItemsWithIndex(menuItemsOf));

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
        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getFlatMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(docId, menuIndex, startedMenuItems, nested, attributes, treeKey, wrap);
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex) {
        final String language = Imcms.getUser().getLanguage();
        List<MenuItemDTO> menuItemsOf = convertItemsToFlatList(
                getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true));

        setHasNewerVersionsInItems(menuItemsOf);
        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getFlatMenuItemsWithIndex(menuItemsOf));

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
        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getFlatMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(
                docId, menuIndex, startedMenuItems, false, null, null, null
        );
    }

    @Override
    public MenuDTO saveFrom(MenuDTO menuDTO) {

        final Integer docId = menuDTO.getDocId();
        final Menu menu = Optional.ofNullable(getMenu(menuDTO.getMenuIndex(), docId))
                .orElseGet(() -> createMenu(menuDTO));

        menu.setNested(menuDTO.isNested());
        menu.setTypeSort(menuDTO.getTypeSort());
        menu.setMenuItems(menuItemDtoListToMenuItemList.apply(getSortedMenuItemsByNumbering(menuDTO.getMenuItems())));

        final MenuDTO savedMenu = menuSaver.apply(menu, languageService.findByCode(Imcms.getUser().getLanguage()));

        super.updateWorkingVersion(docId);

        return savedMenu;
    }

    private void setSortNumbersInMenuItems(List<MenuItemDTO> menuItemDTOs, String treeKey) {
        for (int i = 0; i < menuItemDTOs.size(); i++) {
            final MenuItemDTO menuItemDTO = menuItemDTOs.get(i);
            final boolean hasChildren = !menuItemDTO.getChildren().isEmpty();
            final String dataTreeKey = StringUtils.isBlank(treeKey) ? (i + 1) + "" : treeKey + "." + (i + 1);
            if (StringUtils.isBlank(menuItemDTO.getSortNumber())) {
                menuItemDTO.setSortNumber(dataTreeKey);
            }
            if (hasChildren) {
                setSortNumbersInMenuItems(menuItemDTO.getChildren(), dataTreeKey);
            }
        }
    }


    private List<MenuItemDTO> getSortedMenuItemsByNumbering(List<MenuItemDTO> menuItemDTOs) {
        setSortNumbersInMenuItems(menuItemDTOs, null);
        final List<MenuItemDTO> sortedFlatMenuItems = convertItemsToFlatList(menuItemDTOs).stream()
                .sorted(Comparator.comparing(MenuItemDTO::getSortNumber))
                .collect(Collectors.toList());

        final List<MenuItemDTO> newSortedMenuItems = new ArrayList<>();

        sortedFlatMenuItems.forEach(mainItemDTO -> {

            final List<MenuItemDTO> children = sortedFlatMenuItems.stream()
                    .filter(item -> item.getSortNumber().matches(mainItemDTO.getSortNumber().concat(".\\d")))
                    .collect(Collectors.toList());

            if (!isExistChildrenInNewList(newSortedMenuItems, children)) {  //if in list doesn't exist children we can add to list it
                mainItemDTO.setChildren(children);
            }
            newSortedMenuItems.add(mainItemDTO);
        });

        return getFirstMenuItemsOf(newSortedMenuItems);
    }

    private boolean isExistChildrenInNewList(List<MenuItemDTO> newSortedItems, List<MenuItemDTO> children) {
        final List<Integer> menuItemsids = newSortedItems.stream()
                .flatMap(MenuItemDTO::flattened)
                .map(MenuItemDTO::getDocumentId)
                .collect(Collectors.toList());

        final List<Integer> childrenIds = children.stream()
                .map(MenuItemDTO::getDocumentId)
                .collect(Collectors.toList());

        return menuItemsids.containsAll(childrenIds);
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
                return convertItemsToFlatList(menuItems).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getTitle,
                                Comparator.nullsLast(String::compareToIgnoreCase)))
                        .collect(Collectors.toList());
            case ALPHABETICAL_DESC:
                return convertItemsToFlatList(menuItems).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getTitle,
                                Comparator.nullsLast(String::compareToIgnoreCase)).reversed())
                        .collect(Collectors.toList());
            case PUBLISHED_DATE_ASC:
                return convertItemsToFlatList(menuItems).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getPublishedDate,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .collect(Collectors.toList());
            case PUBLISHED_DATE_DESC:
                return convertItemsToFlatList(menuItems).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getPublishedDate,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList());
            case MODIFIED_DATE_ASC:
                return convertItemsToFlatList(menuItems).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getModifiedDate,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .collect(Collectors.toList());
            case MODIFIED_DATE_DESC:
                return convertItemsToFlatList(menuItems).stream()
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

    private List<MenuItemDTO> getFirstMenuItemsOf(List<MenuItemDTO> menuItems) {
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
}
