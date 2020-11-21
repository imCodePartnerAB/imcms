package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.DataIsNotValidException;
import com.imcode.imcms.components.MenuHtmlConverter;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.exception.SortNotSupportedException;
import com.imcode.imcms.domain.service.*;
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

import java.util.*;
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
@Transactional
public class DefaultMenuService extends AbstractVersionedContentService<Menu, MenuRepository>
        implements IdDeleterMenuService {

    private final VersionService versionService;
    private final DocumentMenuService documentMenuService;
    private final Function<List<MenuItemDTO>, Set<MenuItem>> menuItemDtoListToMenuItemList;
    private final Function<Menu, MenuDTO> menuSaver;
    private final UnaryOperator<MenuItem> toMenuItemsWithoutId;
    private final LanguageService languageService;
    private final Function<MenuItem, MenuItemDTO> menuItemToDTO;
    private final BiFunction<MenuItem, Language, MenuItemDTO> menuItemToMenuItemDtoWithLang;
    private final CommonContentService commonContentService;
    private final Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem;
    private final MenuHtmlConverter menuHtmlConverter;

    private final String REGEX_ANY_NUMBER = "\\.(?:\\b|-)([1-9]{1,2}[0]?|100)\\b";

    DefaultMenuService(MenuRepository menuRepository,
                       VersionService versionService,
                       DocumentMenuService documentMenuService,
                       Function<MenuItem, MenuItemDTO> menuItemToDTO,
                       Function<List<MenuItemDTO>, Set<MenuItem>> menuItemDtoListToMenuItemList,
                       LanguageService languageService,
                       Function<Menu, MenuDTO> menuToMenuDTO,
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
        this.menuSaver = menu -> menuToMenuDTO.apply(menuRepository.save(menu));
    }

    @Override
    public List<MenuItemDTO> getMenuItems(int docId, int menuIndex, String language, boolean nested, String typeSort) {
        if (typeSort == null) {
            if (nested) {
                typeSort = String.valueOf(TREE_SORT);
            } else {
                typeSort = String.valueOf(MANUAL);
            }
        }

        final List<MenuItemDTO> menuItemsOf = getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, false);

        setHasNewerVersionsInItems(menuItemsOf);

        if (!nested && typeSort.equals(String.valueOf(TREE_SORT))) {
            throw new SortNotSupportedException("Current sorting don't support in menuIndex: " + menuIndex);
        }

        return getSortingMenuItemsByTypeSort(typeSort, menuItemsOf);
    }

    @Override
    public List<MenuItemDTO> getSortedMenuItems(MenuDTO menuDTO, String langCode) {

        final String typeSort = menuDTO.getTypeSort();

        if (!menuDTO.isNested() && typeSort.equals(String.valueOf(TREE_SORT))) {
            throw new SortNotSupportedException("Current sorting don't support in flat menu!");
        }

        final List<MenuItemDTO> menuItems = menuDTO.getMenuItems().stream()
                .flatMap(MenuItemDTO::flattened)
                .collect(Collectors.toList());

        final Language language = languageService.findByCode(langCode);
        //double map because from client to fetch itemsDTO which have only doc id and no more info..
        final List<MenuItemDTO> menuItemsDTO = getSortedMenuItemsBySortOrder(menuItems.stream()
                .map(menuItemDtoToMenuItem)
                .map(menuItem -> menuItemToDTO.apply(menuItem))
                .collect(Collectors.toList()));

        setHasNewerVersionsInItems(menuItemsDTO);

        return getSortingMenuItemsByTypeSort(typeSort, menuItemsDTO);
    }

    @Override
    public List<MenuItemDTO> getVisibleMenuItems(int docId, int menuIndex, String language, boolean nested) {
        final List<MenuItemDTO> menuItemsOf = nested
                ? getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true)
                : convertItemsToFlatList(getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true), true);

        setHasNewerVersionsInItems(menuItemsOf);

        return menuItemsOf;
    }

    @Override
    public List<MenuItemDTO> getPublicMenuItems(int docId, int menuIndex, String language, boolean nested) {
        List<MenuItemDTO> menuItemsOf = nested
                ? getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC, language, true)
                : convertItemsToFlatList(getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC, language, true), true);

        setHasNewerVersionsInItems(menuItemsOf);

        return menuItemsOf;
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex, String language,
                                       boolean nested, String attributes, String treeKey, String wrap) {
        List<MenuItemDTO> menuItemsOf = nested
                ? getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true)
                : convertItemsToFlatList(getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true), true);

        setHasNewerVersionsInItems(menuItemsOf);
        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(docId, menuIndex, startedMenuItems, nested, attributes, treeKey, wrap);
    }

    @Override
    public String getPublicMenuAsHtml(int docId, int menuIndex, String language,
                                      boolean nested, String attributes, String treeKey, String wrap) {
        List<MenuItemDTO> menuItemsOf = getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC, language, true);

        setHasNewerVersionsInItems(menuItemsOf);
        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(docId, menuIndex, startedMenuItems, nested, attributes, treeKey, wrap);
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex) {
        final String language = Imcms.getUser().getLanguage();
        List<MenuItemDTO> menuItemsOf = convertItemsToFlatList(getMenuItemsOf(menuIndex, docId, MenuItemsStatus.ALL, language, true), true);

        setHasNewerVersionsInItems(menuItemsOf);
        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(
                docId, menuIndex, startedMenuItems, false, null, null, null
        );
    }

    @Override
    public String getPublicMenuAsHtml(int docId, int menuIndex) {
        final String language = Imcms.getUser().getLanguage();
        List<MenuItemDTO> menuItemsOf = convertItemsToFlatList(getMenuItemsOf(menuIndex, docId, MenuItemsStatus.PUBLIC, language, true), true);

        setHasNewerVersionsInItems(menuItemsOf);
        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(
                docId, menuIndex, startedMenuItems, false, null, null, null
        );
    }

    @Override
    public MenuDTO saveFrom(MenuDTO menuDTO) {

        final Integer docId = menuDTO.getDocId();
        final Menu menu = Optional.ofNullable(getMenu(menuDTO.getMenuIndex(), docId))
                .orElseGet(() -> createMenu(menuDTO));

        final String typeSort = menuDTO.getTypeSort();
        menu.setNested(menuDTO.isNested());
        menu.setMenuItems(menuItemDtoListToMenuItemList.apply(
                menuDTO.getMenuItems()
                        .stream()
                        .flatMap(MenuItemDTO::flattened)
                        .peek(menuItemDTO -> log.error("menu item docId - {}, sortOrder - {}", menuItemDTO.getDocumentId(), menuItemDTO.getSortOrder()))
                        .collect(Collectors.toList()))
        );

        final MenuDTO savedMenu = menuSaver.apply(menu);
        savedMenu.setTypeSort(typeSort);
//        savedMenu.setMenuItems(savedMenu.getMenuItems());

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

    @Override
    public List<Menu> getByDocId(Integer docId) {
        boolean isNewVersion = versionService.hasNewerVersion(docId);

        final Version version = isNewVersion
                ? versionService.getDocumentWorkingVersion(docId)
                : versionService.getLatestVersion(docId);

        return repository.findByVersion(version);
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
                : menuItemToDTO;

        return getSortedMenuItemsBySortOrder(Optional.ofNullable(menu)
                .map(Menu::getMenuItems)
                .orElseGet(LinkedHashSet::new)
                .stream()
                .map(menuItemFunction)
                .filter(Objects::nonNull)
                .filter(menuItemDTO -> (status == MenuItemsStatus.ALL || isPublicMenuItem(menuItemDTO)))
                .filter(menuItemDTO -> documentMenuService.hasUserAccessToDoc(menuItemDTO.getDocumentId(), user))
                .filter(isMenuItemAccessibleForLang(language, versionReceiver))
                .collect(Collectors.toList()));
    }

    public int compare(String sortOrder1, String sortOrder2) {
        if (StringUtils.isBlank(sortOrder1) || StringUtils.isBlank(sortOrder2)) {
            throw new DataIsNotValidException("Sort order is empty or null!!");
        }
        String[] split1 = sortOrder1.split("\\."), split2 = sortOrder2.split("\\.");
        int result = 0;
        for (int i = 0; i < Math.min(split1.length, split2.length); i++) {
            // compare current segment
            if ((result = Integer.compare(Integer.parseInt(split1[i]), Integer.parseInt(split2[i]))) != 0) {
                return result;
            }
        }
        // all was equal up to now, like "1.1" vs "1.1.1"
        return Integer.compare(split1.length, split2.length);
    }

    private List<MenuItemDTO> getSortedMenuItemsBySortOrder(List<MenuItemDTO> menuItems) {

        final List<MenuItemDTO> sortedMenuItems = menuItems.stream()
                .sorted(Comparator.comparing(MenuItemDTO::getSortOrder, this::compare))
                .collect(Collectors.toList());

        final List<MenuItemDTO> newMenuItems = new ArrayList<>();

        sortedMenuItems.forEach(mainItemDTO -> {

            final List<MenuItemDTO> children = sortedMenuItems.stream()
                    .filter(item -> item.getSortOrder().matches(mainItemDTO.getSortOrder().concat(REGEX_ANY_NUMBER)))
                    .collect(Collectors.toList());

            if (!checkToContainsMenuItemInNewList(newMenuItems, children)) {
                mainItemDTO.setChildren(children);
            }
            newMenuItems.add(mainItemDTO);
        });

        //need copied array for remove elements from first array and to get unique array menu items
        final List<MenuItemDTO> newMenuItems2 = new ArrayList<>(newMenuItems);

        return removeMenuItems(newMenuItems, newMenuItems2);
    }

    private boolean checkToContainsMenuItemInNewList(List<MenuItemDTO> newMenuItems, List<MenuItemDTO> children) {
        final List<Integer> ids = newMenuItems.stream()
                .flatMap(MenuItemDTO::flattened)
                .map(MenuItemDTO::getDocumentId)
                .collect(Collectors.toList());

        final List<Integer> childrenIds = children.stream()
                .map(MenuItemDTO::getDocumentId)
                .collect(Collectors.toList());
        return ids.containsAll(childrenIds);
    }

    private List<MenuItemDTO> removeMenuItems(List<MenuItemDTO> newMenuItems, List<MenuItemDTO> newMenuItems2) {

        for (final MenuItemDTO currentMenuItemDTO : newMenuItems) {
            final boolean hasChildren = !currentMenuItemDTO.getChildren().isEmpty();

            if (hasChildren) {
                if (newMenuItems2.containsAll(currentMenuItemDTO.getChildren())) {
                    newMenuItems2.removeAll(currentMenuItemDTO.getChildren());
                }

                removeMenuItems(currentMenuItemDTO.getChildren(), newMenuItems2);
            }
        }

        return newMenuItems2;
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

    private List<MenuItemDTO> getSortingMenuItemsByTypeSort(String typeSort, List<MenuItemDTO> menuItems) {
        switch (TypeSort.valueOf(typeSort)) {
            case TREE_SORT:
                return menuItems;
            case MANUAL:
                return convertItemsToFlatList(menuItems, true);
            case ALPHABETICAL_ASC:
                return convertItemsToFlatList(menuItems.stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getTitle,
                                Comparator.nullsLast(String::compareToIgnoreCase)))
                        .collect(Collectors.toList()), true);
            case ALPHABETICAL_DESC:
                return convertItemsToFlatList(menuItems.stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getTitle,
                                Comparator.nullsLast(String::compareToIgnoreCase)).reversed())
                        .collect(Collectors.toList()), true);
            case PUBLISHED_DATE_ASC:
                return convertItemsToFlatList(menuItems.stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getPublishedDate,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .collect(Collectors.toList()), true);
            case PUBLISHED_DATE_DESC:
                return convertItemsToFlatList(menuItems.stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getPublishedDate,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList()), true);
            case MODIFIED_DATE_ASC:
                return convertItemsToFlatList(menuItems.stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getModifiedDate,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .collect(Collectors.toList()), true);
            case MODIFIED_DATE_DESC:
                return convertItemsToFlatList(menuItems.stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getModifiedDate,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList()), true);
            default:
                return Collections.EMPTY_LIST;//never come true...
        }
    }

    private List<MenuItemDTO> convertItemsToFlatList(List<MenuItemDTO> menuItems, boolean isSetEmptyChildren) {
        return menuItems.stream()
                .flatMap(MenuItemDTO::flattened)
                .distinct()
                .peek(item -> {
                    if (isSetEmptyChildren && !item.getChildren().isEmpty()) item.setChildren(Collections.emptyList());
                })
                .collect(Collectors.toList());
    }

    private List<MenuItemDTO> getMenuItemsWithIndex(List<MenuItemDTO> menuItems) {
        final List<MenuItemDTO> resultMenuItems = convertItemsToFlatList(menuItems, false);

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
}
