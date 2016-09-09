package imcode.server.document.textdocument;

import com.imcode.imcms.mapping.DocGetterCallback;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.*;

/**
 * Menu is a one-level navigation control between documents.
 * A menu can contain any number of items - links to other documents.
 */
public class MenuDomainObject implements Cloneable, Serializable {

    public final static int MENU_SORT_ORDER__BY_HEADLINE = 1;
    public final static int MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED = 2;
    public final static int MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED = 3;
    public final static int MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER = 4;
    public final static int MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED = 5;
    public final static int MENU_SORT_ORDER__DEFAULT = MENU_SORT_ORDER__BY_HEADLINE;

    public static final int DEFAULT_SORT_KEY = 500;

    private static final int DEFAULT_SORT_KEY_INCREMENT = 10;

    private volatile int sortOrder;

    /**
     * Map of included meta_id to included DocumentDomainObject.
     */
    private volatile Map<Integer, MenuItemDomainObject> menuItems = new HashMap<>();

    public MenuDomainObject() {
        this(MENU_SORT_ORDER__DEFAULT);
    }

    public MenuDomainObject(int sortOrder) {
        this.sortOrder = sortOrder;
        menuItems = new HashMap<>();
    }

    public MenuDomainObject clone() {
        try {
            MenuDomainObject clone = (MenuDomainObject) super.clone();
            clone.menuItems = new HashMap<>();
            for (Map.Entry<Integer, MenuItemDomainObject> entry : menuItems.entrySet()) {
                clone.menuItems.put(entry.getKey(), entry.getValue().clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new UnhandledException(e);
        }
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
        List<MenuItemDomainObject> menuItemsUserCanSee = getMenuItemsVisibleToUser(user);
        return menuItemsUserCanSee.toArray(new MenuItemDomainObject[menuItemsUserCanSee.size()]);
    }

    List<MenuItemDomainObject> getMenuItemsVisibleToUser(UserDomainObject user) {
        MenuItemDomainObject[] menuItemsArray = getMenuItems();
        List<MenuItemDomainObject> menuItemsUserCanSee = new ArrayList<>(this.menuItems.size());
        for (MenuItemDomainObject menuItem : menuItemsArray) {
            if (user.canSeeDocumentWhenEditingMenus(menuItem.getDocument())) {
                menuItemsUserCanSee.add(menuItem);
            }
        }
        Collections.sort(menuItemsUserCanSee, getMenuItemComparatorForSortOrder(sortOrder));
        return menuItemsUserCanSee;
    }

    /**
     * @return Menu items pointing to active documents.
     */
    public MenuItemDomainObject[] getPublishedMenuItemsUserCanSee(UserDomainObject user) {
        List<MenuItemDomainObject> menuItems = getMenuItemsVisibleToUser(user);
        CollectionUtils.filter(menuItems, object -> ((MenuItemDomainObject) object).getDocument().isActive());
        return menuItems.toArray(new MenuItemDomainObject[menuItems.size()]);
    }

    public MenuItemDomainObject getMenuItemByDocId(Integer docId) {
        if (menuItems.containsKey(docId))
            return menuItems.get(docId);
        return null;
    }

    public MenuItemDomainObject[] getMenuItems() {
        Set<MenuItemDomainObject> menuItemsUnsorted = getMenuItemsUnsorted();
        MenuItemDomainObject[] menuItemsArray = menuItemsUnsorted.toArray(new MenuItemDomainObject[menuItemsUnsorted.size()]);
        Arrays.sort(menuItemsArray, getMenuItemComparatorForSortOrder(sortOrder));
        return menuItemsArray;
    }

    public LinkedList<MenuItemDomainObject.TreeMenuItemDomainObject> getMenuItemsAsTree() {
        sortOrder = MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER;
        final LinkedList<MenuItemDomainObject> items = new LinkedList<>(Arrays.asList(getMenuItems()));

        return buildTree(items);
    }

    public LinkedList<MenuItemDomainObject.TreeMenuItemDomainObject> getMenuItemsVisibleToUserAsTree() {
        sortOrder = MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER;
        final LinkedList<MenuItemDomainObject> items = new LinkedList<>(Arrays.asList(getPublishedMenuItemsUserCanSee(Imcms.getUser())));
        return buildTree(items);
    }

    private LinkedList<MenuItemDomainObject.TreeMenuItemDomainObject> buildTree(LinkedList<MenuItemDomainObject> items) {
        final LinkedList<MenuItemDomainObject.TreeMenuItemDomainObject> tree = new LinkedList<>();
        MenuItemDomainObject.TreeMenuItemDomainObject current = null;
        int currentLevel = 1;
        MenuItemDomainObject item;

        while ((item = items.peek()) != null) {
            TreeSortKeyDomainObject treeSortKey = item.getTreeSortKey();
            final int itemLevel = treeSortKey.getLevelCount();

            if (current == null || itemLevel == currentLevel) {
                if (shouldBeAdded(tree, item)) {
                    current = new MenuItemDomainObject.TreeMenuItemDomainObject();
                    currentLevel = treeSortKey.getLevelCount();
                    current.setMenuItem(item);
                    tree.addLast(current);
                }
                items.remove();

            } else if (itemLevel > currentLevel && itemLevel - currentLevel == 1) {
                current.getSubMenuItems().addAll(buildTree(items));
            } else {
                break;
            }
        }
        return tree;
    }

    private boolean shouldBeAdded(LinkedList<MenuItemDomainObject.TreeMenuItemDomainObject> tree, MenuItemDomainObject item) {
        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        int docId = item.getDocument().getId();

        DocGetterCallback docGetterCallback = Imcms.getServices()
                .getImcmsAuthenticatorAndUserAndRoleMapper()
                .getDefaultUser() // check callback as default user because admin should see docs in menu as others
                .getDocGetterCallback();

        docGetterCallback.setLanguage(item.getDocument().getLanguage());
        DocumentDomainObject doc = docGetterCallback.getDoc(docId, documentMapper);

        if (doc == null) {
            return false; // if user hasn't permissions to see document
        }

        if (tree.isEmpty()) {
            return true; // this is the first item, add it without level checking!

        } else {
            TreeSortKeyDomainObject lastKey = tree.getLast().getMenuItem().getTreeSortKey();
            TreeSortKeyDomainObject newKey = item.getTreeSortKey();
            int highestLevelKey = 0;
            int minLevelCount = 1;

            if (lastKey.getLevelKey(highestLevelKey) == newKey.getLevelKey(highestLevelKey)) {
                return true; // if last and new menu are sub-menu of same thing.

            } else if ((lastKey.getLevelCount() == minLevelCount) && (newKey.getLevelCount() == minLevelCount)) {
                return true; // if both are members of highest menu level
            }
        }

        return false;
    }

    public Set<MenuItemDomainObject> getMenuItemsUnsorted() {
        HashSet<MenuItemDomainObject> set = new HashSet<>();
        for (MenuItemDomainObject menuItem : menuItems.values()) {
            if (null != menuItem.getDocument()) {
                set.add(menuItem);
            }
        }
        return set;
    }

    /**
     * Adds menu item to this menu only if it contains a document.
     *
     * @param menuItem
     */
    public void addMenuItem(MenuItemDomainObject menuItem) {
        if (null == menuItem.getSortKey()) {
            generateSortKey(menuItem);
        }
        if (null != menuItem.getDocument()) {
            addMenuItemUnchecked(menuItem);
        }
    }

    /**
     * Adds menu item to this menu without checking if it references a document.
     *
     * @param menuItem
     */
    public void addMenuItemUnchecked(MenuItemDomainObject menuItem) {
        if (null == menuItem.getSortKey()) {
            generateSortKey(menuItem);
        }

        menuItems.put(menuItem.getDocumentId(), menuItem);
    }

    private void generateSortKey(MenuItemDomainObject menuItem) {
        Integer maxSortKey = getMaxSortKey();
        Integer sortKey;
        if (null != maxSortKey) {
            sortKey = maxSortKey.intValue() + DEFAULT_SORT_KEY_INCREMENT;
        } else {
            sortKey = DEFAULT_SORT_KEY;
        }
        menuItem.setSortKey(sortKey);
    }

    private Integer getMaxSortKey() {
        Collection<Integer> menuItemSortKeys = CollectionUtils.collect(menuItems.values(), new Transformer() {
            public Integer transform(Object o) {
                return ((MenuItemDomainObject) o).getSortKey();
            }
        });

        return menuItemSortKeys.isEmpty() ? null : Collections.max(menuItemSortKeys);
    }

    private Comparator getMenuItemComparatorForSortOrder(int sortOrder) {

        Comparator comparator = MenuItemComparator.HEADLINE.chain(MenuItemComparator.ID);
        if (sortOrder == MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER) {
            comparator = MenuItemComparator.TREE_SORT_KEY.chain(comparator);
        } else if (sortOrder == MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED) {
            comparator = MenuItemComparator.SORT_KEY.reversed().chain(comparator);
        } else if (sortOrder == MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED) {
            comparator = MenuItemComparator.MODIFIED_DATETIME.reversed().chain(comparator);
        } else if (sortOrder == MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED) {
            comparator = MenuItemComparator.PUBLISHED_DATETIME.reversed().chain(comparator);
        }
        return comparator;
    }

    public void removeMenuItemByDocumentId(int childId) {
        menuItems.remove(childId);
    }

    public void removeAllMenuItems(){
        menuItems.clear();
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


    public Map<Integer, MenuItemDomainObject> getItemsMap() {
        return menuItems;
    }
}