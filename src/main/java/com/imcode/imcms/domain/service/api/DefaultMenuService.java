package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.DataIsNotValidException;
import com.imcode.imcms.components.MenuHtmlConverter;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.enums.TypeSort;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MenuRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.imcode.imcms.enums.TypeSort.TREE_SORT;
import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;

@Service
@Slf4j
@Transactional
public class DefaultMenuService extends AbstractVersionedContentService<Menu, MenuRepository>
        implements IdDeleterMenuService {

    private final VersionService versionService;
    private final DocumentMenuService documentMenuService;
    private final LanguageService languageService;
    private final MenuHtmlConverter menuHtmlConverter;

    private final Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem;
    private final BiFunction<MenuItem, DocumentDTO, MenuItemDTO> menuItemAndDocumentToMenuItemDTO;
    private final Function<List<MenuItemDTO>, Set<MenuItem>> menuItemDtoListToMenuItemList;
    private final Function<Menu, MenuDTO> menuToMenuDTO;
    private final BiFunction<Menu, List<DocumentDTO>, MenuDTO> menuAndDocumentsToMenuDTO;
    private final UnaryOperator<MenuItem> toMenuItemsWithoutId;

    private final String REGEX_ANY_NUMBER = "\\.(?:\\b|-)([1-9]{1,2}[0]?|100)\\b";

    DefaultMenuService(MenuRepository menuRepository,
                       VersionService versionService,
                       DocumentMenuService documentMenuService,
                       Function<MenuItem, MenuItemDTO> menuItemToDTO,
                       Function<List<MenuItemDTO>, Set<MenuItem>> menuItemDtoListToMenuItemList,
                       LanguageService languageService,
                       MenuHtmlConverter menuHtmlConverter,
                       Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem,
                       BiFunction<MenuItem, DocumentDTO, MenuItemDTO> menuItemAndDocumentToMenuItemDTO,
                       Function<Menu, MenuDTO> menuToMenuDTO,
                       BiFunction<Menu, List<DocumentDTO>, MenuDTO> menuAndDocumentsToMenuDTO,
                       UnaryOperator<MenuItem> toMenuItemsWithoutId) {

        super(menuRepository);
        this.versionService = versionService;
        this.documentMenuService = documentMenuService;
        this.menuItemDtoListToMenuItemList = menuItemDtoListToMenuItemList;
        this.languageService = languageService;
        this.toMenuItemsWithoutId = toMenuItemsWithoutId;
        this.menuHtmlConverter = menuHtmlConverter;
        this.menuItemDtoToMenuItem = menuItemDtoToMenuItem;
        this.menuItemAndDocumentToMenuItemDTO = menuItemAndDocumentToMenuItemDTO;
        this.menuToMenuDTO = menuToMenuDTO;
        this.menuAndDocumentsToMenuDTO = menuAndDocumentsToMenuDTO;
    }

    @Override
    public MenuDTO getMenuDTO(int docId, int menuIndex, String language, String typeSort) {
        final Version version = versionService.getDocumentWorkingVersion(docId);
        final MenuDTO menuDTO = getMenuDTO(menuIndex, version, MenuItemsStatus.ALL, language);

        typeSort = menuDTO.getTypeSort();
        if (StringUtils.isBlank(typeSort)) {
            typeSort = String.valueOf(TREE_SORT); //default value
        }

        menuDTO.setMenuItems(getSortingMenuItemsByTypeSort(typeSort, menuDTO.getMenuItems()));

        return menuDTO;
    }

    @Override
    public List<MenuItemDTO> getSortedMenuItems(MenuDTO menuDTO, String langCode) {
        final String typeSort = menuDTO.getTypeSort();

        final List<MenuItemDTO> menuItems = menuDTO.getMenuItems().stream()
                .flatMap(MenuItemDTO::flattened)
                .collect(Collectors.toList());

        final Map<Integer, DocumentDTO> documentDTOs = Imcms.getServices().getDocumentService().get(
                menuItems.stream().map(MenuItemDTO::getDocumentId).toList()
        ).stream().collect(Collectors.toMap(DocumentDTO::getId, Function.identity()));

        //double map because from client to fetch itemsDTO which have only doc id and no more info..
        final List<MenuItemDTO> menuItemsDTO = menuItems.stream()
                .map(menuItemDtoToMenuItem)
                .map(menuItem -> menuItemAndDocumentToMenuItemDTO.apply(menuItem, documentDTOs.get(menuItem.getDocumentId())))
                .toList();

        return getSortingMenuItemsByTypeSort(typeSort, menuItemsDTO);
    }

    @Override
    public List<MenuItemDTO> getVisibleMenuItems(int docId, int menuIndex, String language) {
        return getVisibleMenuItems(docId, menuIndex, Version.WORKING_VERSION_INDEX, language);
    }

    @Override
    public List<MenuItemDTO> getVisibleMenuItems(int docId, int menuIndex, int versionNo, String language){
        final Version version = versionService.findByDocIdAndNo(docId, versionNo);
        return getMenuDTO(menuIndex, version, MenuItemsStatus.ALL, language).getMenuItems();
    }

    @Override
    public List<MenuItemDTO> getPreviewMenuItems(int docId, int menuIndex, String language) {
        return getPreviewMenuItems(docId, menuIndex, Version.WORKING_VERSION_INDEX, language);
    }

    @Override
    public List<MenuItemDTO> getPreviewMenuItems(int docId, int menuIndex, int versionNo, String language) {
        final Version version = versionService.findByDocIdAndNo(docId, versionNo);
        return getMenuDTO(menuIndex, version, MenuItemsStatus.PUBLIC, language).getMenuItems();
    }

    @Override
    public List<MenuItemDTO> getPublicMenuItems(int docId, int menuIndex, String language) {
        final Version version = versionService.getLatestVersion(docId);
        return getMenuDTO(menuIndex, version, MenuItemsStatus.PUBLIC, language).getMenuItems();
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex, String language,
                                       String attributes, String treeKey, String wrap) {
        return getVisibleMenuAsHtml(docId, menuIndex, Version.WORKING_VERSION_INDEX, language, attributes, treeKey, wrap);
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex, int versionNo, String language,
                                String attributes, String treeKey, String wrap){
        final Version version = versionService.findByDocIdAndNo(docId, versionNo);
        final List<MenuItemDTO> menuItemsOf = getMenuDTO(menuIndex, version, MenuItemsStatus.ALL, language).getMenuItems();

        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(docId, menuIndex, startedMenuItems, attributes, treeKey, wrap);
    }

    @Override
    public String getPreviewMenuAsHtml(int docId, int menuIndex, String language,
                                       String attributes, String treeKey, String wrap) {
        return getPreviewMenuAsHtml(docId, menuIndex, Version.WORKING_VERSION_INDEX, language, attributes, treeKey, wrap);
    }

    @Override
    public String getPreviewMenuAsHtml(int docId, int menuIndex, int versionNo, String language,
                                       String attributes, String treeKey, String wrap) {
        final Version version = versionService.findByDocIdAndNo(docId, versionNo);
        final List<MenuItemDTO> menuItemsOf = getMenuDTO(menuIndex, version, MenuItemsStatus.PUBLIC, language).getMenuItems();

        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(docId, menuIndex, startedMenuItems, attributes, treeKey, wrap);
    }

    @Override
    public String getPublicMenuAsHtml(int docId, int menuIndex, String language,
                                      String attributes, String treeKey, String wrap) {
        final Version version = versionService.getLatestVersion(docId);
        final List<MenuItemDTO> menuItemsOf = getMenuDTO(menuIndex, version, MenuItemsStatus.PUBLIC, language).getMenuItems();

        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(docId, menuIndex, startedMenuItems, attributes, treeKey, wrap);
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex) {
        return getVisibleMenuAsHtml(docId, menuIndex, Version.WORKING_VERSION_INDEX);
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex, int versionNo){
        final String language = Imcms.getUser().getLanguage();
        final Version version = versionService.findByDocIdAndNo(docId, versionNo);
        final List<MenuItemDTO> menuItemsOf = convertItemsToFlatList(getMenuDTO(menuIndex, version, MenuItemsStatus.ALL, language).getMenuItems(), true);

        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(
                docId, menuIndex, startedMenuItems, null, null, null
        );
    }

    @Override
    public String getPublicMenuAsHtml(int docId, int menuIndex) {
        final String language = Imcms.getUser().getLanguage();
        final Version version = versionService.getLatestVersion(docId);
        List<MenuItemDTO> menuItemsOf = convertItemsToFlatList(
                getMenuDTO(menuIndex, version, MenuItemsStatus.PUBLIC, language).getMenuItems(),
                true);

        final List<MenuItemDTO> startedMenuItems = getFirstMenuItemsOf(getMenuItemsWithIndex(menuItemsOf));

        return menuHtmlConverter.convertToMenuHtml(
                docId, menuIndex, startedMenuItems, null, null, null
        );
    }

    @Override
    public MenuDTO saveFrom(MenuDTO menuDTO) {

        final Integer docId = menuDTO.getDocId();
        final Menu menu = Optional.ofNullable(getMenu(menuDTO.getMenuIndex(), docId))
                .orElseGet(() -> createMenu(menuDTO));

        final String typeSort = menuDTO.getTypeSort();

        menu.setTypeSort(typeSort);
        menu.setMenuItems(menuItemDtoListToMenuItemList.apply(
                menuDTO.getMenuItems()
                        .stream()
                        .flatMap(MenuItemDTO::flattened)
                        .peek(menuItemDTO -> checkOnValidDataSortOrder(menuItemDTO.getSortOrder(), typeSort))
                        .collect(Collectors.toList()))
        );

        final MenuDTO savedMenu = menuToMenuDTO.apply(repository.save(menu));

        super.updateWorkingVersion(docId);
        indexAndCacheActualizationAfterCommit(docId);

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
        menu.setTypeSort(menuDTO.getTypeSort());
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

    @Override
    public void setAsWorkingVersion(Version version) {
        final Version workingVersion = versionService.getDocumentWorkingVersion(version.getDocId());
        final List<Menu> menusByVersion = repository.findByVersion(version);

        final List<Menu> saveMenus = new ArrayList<>();
        menusByVersion.forEach(menuByVersion -> {
            Set<MenuItem> menuItemsCopy = menuByVersion.getMenuItems().stream()
                    .map(menuItem -> {
                        MenuItem menuItemCopy = new MenuItem();
                        menuItemCopy.setDocumentId(menuItem.getDocumentId());
                        menuItemCopy.setSortOrder(menuItem.getSortOrder());

                        return menuItemCopy;
                    }).collect(Collectors.toSet());

            Menu menuCopy = new Menu();
            menuCopy.setVersion(workingVersion);
            menuCopy.setNo(menuByVersion.getNo());
            menuCopy.setTypeSort(menuByVersion.getTypeSort());
            menuCopy.setMenuItems(menuItemsCopy);

            saveMenus.add(menuCopy);
        });

        repository.deleteByVersion(workingVersion);
        repository.flush();
        repository.saveAll(saveMenus);
    }

    private MenuDTO getMenuDTO(int menuIndex, Version version, MenuItemsStatus status, String langCode) {
        final Language language = languageService.findByCode(langCode);
        final Optional<Menu> menu = Optional.ofNullable(repository.findByNoAndVersionAndFetchMenuItemsEagerly(menuIndex, version));
        final UserDomainObject user = Imcms.getUser();

        final MenuDTO menuDTO;
        final Map<Integer, DocumentDTO> documentMap;
        if(menu.isPresent()){
            List<DocumentDTO> docs = Imcms.getServices().getDocumentService().get(
                    menu.get().getMenuItems().stream().map(MenuItem::getDocumentId).collect(Collectors.toList())
            );

            menuDTO = menuAndDocumentsToMenuDTO.apply(menu.get(), docs);
            documentMap = docs.stream().collect(Collectors.toMap(DocumentDTO::getId, Function.identity()));
        }else{
            menuDTO = new MenuDTO();
            menuDTO.setMenuItems(Collections.emptyList());

            documentMap = Collections.emptyMap();
        }

        final List<MenuItemDTO> sortedMenuItems = getSortedMenuItemsBySortOrder(menuDTO.getMenuItems());

        final Predicate<MenuItemDTO> menuItemAccessFilter = menuItemDTO -> {
            final DocumentDTO documentDTO = documentMap.get(menuItemDTO.getDocumentId());
            return (status == MenuItemsStatus.ALL || (documentMenuService.isPublicMenuItem(documentDTO) &&
                    documentMenuService.hasUserAccessToDoc(documentDTO, user))) &&
                    isMenuItemAccessibleForLang(language, documentDTO).test(menuItemDTO);
        };

        final List<MenuItemDTO> filteredMenuItems = sortedMenuItems.stream()
                .filter(menuItemAccessFilter)
                .collect(Collectors.toList());

        //filter child menu elements
        filteredMenuItems.forEach(new Consumer<>(){
            @Override
            public void accept(MenuItemDTO menuItemDTO) {
                List<MenuItemDTO> filteredChildrenMenuItems = menuItemDTO.getChildren().stream()
                        .filter(menuItemAccessFilter)
                        .collect(Collectors.toList());

                filteredChildrenMenuItems.forEach(this);

                menuItemDTO.setChildren(filteredChildrenMenuItems);
            }
        });

        menuDTO.setMenuItems(filteredMenuItems);
        return menuDTO;
    }


    private void checkOnValidDataSortOrder(String sortOrder, String typeSort) {
        boolean isNotNumber = !StringUtils.isNumeric(sortOrder);
        boolean isNotTreeSortType = StringUtils.isNoneBlank(typeSort) && !typeSort.equals(String.valueOf(TREE_SORT));

        if (StringUtils.isBlank(sortOrder) || isNotTreeSortType && isNotNumber) {
            throw new DataIsNotValidException("Sort order is not valid it looks like - " + sortOrder);
        }
    }

    private int compare(String sortOrder1, String sortOrder2) {
        checkOnValidDataSortOrder(sortOrder1, "");
        checkOnValidDataSortOrder(sortOrder2, "");

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
        final Set<Integer> ids = newMenuItems.stream()
                .flatMap(MenuItemDTO::flattened)
                .map(MenuItemDTO::getDocumentId)
                .collect(Collectors.toSet());

        final Set<Integer> childrenIds = children.stream()
                .map(MenuItemDTO::getDocumentId)
                .collect(Collectors.toSet());
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

    private Predicate<MenuItemDTO> isMenuItemAccessibleForLang(Language language, DocumentDTO documentDTO) {
        return menuItemDTO -> {
            final List<Language> enabledLanguages = documentDTO.getCommonContents().stream()
                    .filter(CommonContent::isEnabled)
                    .map(CommonContent::getLanguage)
                    .filter(Language::isEnabled)
                    .toList();

            final boolean isLanguageEnabled = enabledLanguages.contains(language);
            final boolean isCurrentLangDefault = language.getCode().equals(Imcms.getServices().getLanguageMapper().getDefaultLanguage());
            final boolean isAllowedToShowWithDefaultLanguage = documentDTO.getDisabledLanguageShowMode().equals(SHOW_IN_DEFAULT_LANGUAGE);

            return isLanguageEnabled || (!isCurrentLangDefault && isAllowedToShowWithDefaultLanguage);
        };

    }

    private List<MenuItemDTO> getSortingMenuItemsByTypeSort(String typeSort, List<MenuItemDTO> menuItems) {
        switch (TypeSort.valueOf(typeSort)) {
            case TREE_SORT:
                return menuItems;
            case MANUAL:
                return convertItemsToFlatList(menuItems, true);
            case ALPHABETICAL_ASC:
                return convertItemsToFlatList(menuItems, true).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getTitle,
                                Comparator.nullsLast(String::compareToIgnoreCase)))
                        .collect(Collectors.toList());
            case ALPHABETICAL_DESC:
                return convertItemsToFlatList(menuItems, true).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getTitle,
                                Comparator.nullsLast(String::compareToIgnoreCase)).reversed())
                        .collect(Collectors.toList());
            case PUBLISHED_DATE_ASC:
                return convertItemsToFlatList(menuItems, true).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getPublishedDate,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .collect(Collectors.toList());
            case PUBLISHED_DATE_DESC:
                return convertItemsToFlatList(menuItems, true).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getPublishedDate,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList());
            case MODIFIED_DATE_ASC:
                return convertItemsToFlatList(menuItems, true).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getModifiedDate,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .collect(Collectors.toList());
            case MODIFIED_DATE_DESC:
                return convertItemsToFlatList(menuItems, true).stream()
                        .sorted(Comparator.comparing(MenuItemDTO::getModifiedDate,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList());
            default:
                return Collections.EMPTY_LIST;//never come true...
        }
    }

    private List<MenuItemDTO> convertItemsToFlatList(List<MenuItemDTO> menuItems, boolean isSetEmptyChildren) {
        List<MenuItemDTO> result = menuItems.stream()
                .flatMap(MenuItemDTO::flattened)
                .distinct()
                .collect(Collectors.toList());
        result.forEach(item -> {
            if (isSetEmptyChildren && !item.getChildren().isEmpty()) item.setChildren(Collections.emptyList());
        });

        return result;
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

    private void indexAndCacheActualizationAfterCommit(int docId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            public void afterCommit() {
                indexAndCacheActualization(docId);
            }
        });
    }
}
