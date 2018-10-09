package imcode.server.document.textdocument;

import imcode.server.document.DocumentComparator;
import imcode.server.document.textdocument.MenuItemComparator.MenuItemDocumentComparator;
import imcode.server.user.UserDomainObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MenuDomainObject implements Cloneable, Serializable {

    private static final MenuItemComparator ID = new MenuItemDocumentComparator(DocumentComparator.ID);
    private static final MenuItemComparator HEADLINE = new MenuItemDocumentComparator(DocumentComparator.HEADLINE);
    private static final MenuItemComparator MODIFIED_DATETIME = new MenuItemDocumentComparator(DocumentComparator.MODIFIED_DATETIME);
    private static final MenuItemComparator PUBLISHED_DATETIME = new MenuItemDocumentComparator(DocumentComparator.PUBLICATION_START_DATETIME);
    private static final MenuItemComparator SORT_KEY = new MenuItemComparator.SortKeyComparator();
    private static final MenuItemComparator TREE_SORT_KEY = new MenuItemComparator.TreeSortKeyComparator();

    public final static int MENU_SORT_ORDER__BY_HEADLINE = 1;
    public final static int MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED = 2;
    public final static int MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED = 3;
    public final static int MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER = 4;
    public final static int MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED = 5;
    public final static int MENU_SORT_ORDER__DEFAULT = MENU_SORT_ORDER__BY_HEADLINE;
    public final static int DEFAULT_SORT_KEY = 500;
    private static final int DEFAULT_SORT_KEY_INCREMENT = 10;
    private int id;
    private int sortOrder;
    private HashMap<Integer, MenuItemDomainObject> menuItems;

    public MenuDomainObject() {
        this(0, MENU_SORT_ORDER__DEFAULT);
    }

    public MenuDomainObject(int id, int sortOrder) {
        this.id = id;
        this.sortOrder = sortOrder;
        menuItems = new HashMap<>();
    }

    public MenuDomainObject clone() {
        try {
            MenuDomainObject clone = (MenuDomainObject) super.clone();
            clone.menuItems = new HashMap<>();
            for (Map.Entry<Integer, MenuItemDomainObject> entry : menuItems.entrySet()) {
                Integer documentId = entry.getKey();
                MenuItemDomainObject menuItem = entry.getValue();
                clone.menuItems.put(documentId, menuItem.clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new UnhandledException(e);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        switch (sortOrder) {
            case MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED:
            case MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER:
            case MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED:
            case MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED:
            case MENU_SORT_ORDER__BY_HEADLINE:
                this.sortOrder = sortOrder;
                break;
            default:
                throw new IllegalArgumentException("Bad sort order. Use one of the constants.");
        }
    }

    public MenuItemDomainObject[] getMenuItemsUserCanSee(UserDomainObject user) {
        List menuItemsUserCanSee = getMenuItemsVisibleToUser(user);
        return (MenuItemDomainObject[]) menuItemsUserCanSee.toArray(new MenuItemDomainObject[0]);
    }

    List<MenuItemDomainObject> getMenuItemsVisibleToUser(UserDomainObject user) {
        MenuItemDomainObject[] menuItemsArray = getMenuItems();
        List<MenuItemDomainObject> menuItemsUserCanSee = new ArrayList<>(this.menuItems.size());
        for (MenuItemDomainObject menuItem : menuItemsArray) {
            if (user.canSeeDocumentWhenEditingMenus(menuItem.getDocument())) {
                menuItemsUserCanSee.add(menuItem);
            }
        }
        menuItemsUserCanSee.sort(getMenuItemComparatorForSortOrder(sortOrder));
        return menuItemsUserCanSee;
    }

    public MenuItemDomainObject[] getPublishedMenuItemsUserCanSee(UserDomainObject user) {
        List<MenuItemDomainObject> menuItems = getMenuItemsVisibleToUser(user);
        CollectionUtils.filter(menuItems, object -> object.getDocument().isActive());
        return menuItems.toArray(new MenuItemDomainObject[0]);
    }

    public MenuItemDomainObject[] getMenuItems() {
        Set menuItemsUnsorted = getMenuItemsUnsorted();
        MenuItemDomainObject[] menuItemsArray = (MenuItemDomainObject[]) menuItemsUnsorted.toArray(new MenuItemDomainObject[0]);
        Arrays.sort(menuItemsArray, getMenuItemComparatorForSortOrder(sortOrder));
        return menuItemsArray;
    }

    public Set getMenuItemsUnsorted() {
        HashSet<MenuItemDomainObject> set = new HashSet<>();
        for (MenuItemDomainObject menuItem : menuItems.values()) {
            if (null != menuItem.getDocument()) {
                set.add(menuItem);
            }
        }
        return set;
    }

    public void addMenuItem(MenuItemDomainObject menuItem) {
        if (null == menuItem.getSortKey()) {
            generateSortKey(menuItem);
        }
        if (null != menuItem.getDocument()) {
            addMenuItemUnchecked(menuItem);
        }
    }

    public void addMenuItemUnchecked(MenuItemDomainObject menuItem) {
        menuItems.put(menuItem.getDocumentId(), menuItem);
    }

    private void generateSortKey(MenuItemDomainObject menuItem) {
        Integer maxSortKey = getMaxSortKey();
        int sortKey;
        if (null != maxSortKey) {
            sortKey = maxSortKey + DEFAULT_SORT_KEY_INCREMENT;
        } else {
            sortKey = DEFAULT_SORT_KEY;
        }
        menuItem.setSortKey(sortKey);
    }

    private Integer getMaxSortKey() {
        Collection<Integer> menuItemSortKeys = CollectionUtils.collect(menuItems.values(), MenuItemDomainObject::getSortKey);
        if (menuItemSortKeys.isEmpty()) {
            return null;
        }
        return Collections.max(menuItemSortKeys);
    }

    private Comparator<MenuItemDomainObject> getMenuItemComparatorForSortOrder(int sortOrder) {
        Comparator<MenuItemDomainObject> comparator = HEADLINE.chain(ID);
        if (sortOrder == MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER) {
            comparator = TREE_SORT_KEY.chain(comparator);
        } else if (sortOrder == MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED) {
            comparator = SORT_KEY.reversed().chain(comparator);
        } else if (sortOrder == MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED) {
            comparator = MODIFIED_DATETIME.reversed().chain(comparator);
        } else if (sortOrder == MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED) {
            comparator = PUBLISHED_DATETIME.reversed().chain(comparator);
        }
        return comparator;
    }

    public void removeMenuItemByDocumentId(int childId) {
        menuItems.remove(childId);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MenuDomainObject)) {
            return false;
        }
        MenuDomainObject otherMenu = (MenuDomainObject) obj;
        return new EqualsBuilder().append(sortOrder, otherMenu.sortOrder)
                .append(menuItems, otherMenu.menuItems).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(sortOrder).append(menuItems).toHashCode();
    }

}