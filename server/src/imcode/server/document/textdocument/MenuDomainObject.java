/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-maj-03
 * Time: 17:36:40
 */
package imcode.server.document.textdocument;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.collections.Predicate;

import java.util.*;

public class MenuDomainObject {

    private int id;
    private int sortOrder;
    private SortedSet menuItems;

    public final static int MENU_SORT_ORDER__BY_HEADLINE = 1;
    public final static int MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED = 2;
    public final static int MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED = 3;
    public final static int MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER = 4;
    public final static int MENU_SORT_ORDER__DEFAULT = MENU_SORT_ORDER__BY_HEADLINE;

    public final static int DEFAULT_SORT_KEY = 500;
    private static final int DEFAULT_SORT_KEY_INCREMENT = 10;

    public MenuDomainObject() {
        this(0, MENU_SORT_ORDER__DEFAULT) ;
    }

    public MenuDomainObject( int id, int sortOrder ) {
        this.id = id;
        this.sortOrder = sortOrder;
        menuItems = SetUtils.predicatedSortedSet( new TreeSet( getMenuItemComparatorForSortOrder( sortOrder ) ), new Predicate() {
            public boolean evaluate( Object o ) {
                return o instanceof MenuItemDomainObject && null != ((MenuItemDomainObject)o).getSortKey() ;
            }
        });
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public MenuItemDomainObject[] getMenuItems() {
        return (MenuItemDomainObject[])menuItems.toArray( new MenuItemDomainObject[menuItems.size()] );
    }

    public void addMenuItem( MenuItemDomainObject menuItem ) {
        if (null == menuItem.getSortKey()) {
            Integer maxSortKey = getMaxSortKey();
            Integer sortKey;
            if ( null != maxSortKey ) {
                sortKey = new Integer( maxSortKey.intValue() + DEFAULT_SORT_KEY_INCREMENT );
            } else {
                sortKey = new Integer( DEFAULT_SORT_KEY );
            }
            menuItem.setSortKey( sortKey );
        }

        menuItems.add( menuItem );
    }

    private Integer getMaxSortKey() {
        Collection menuItemSortKeys = CollectionUtils.collect(menuItems,new Transformer() {
            public Object transform( Object o ) {
                return ((MenuItemDomainObject)o).getSortKey() ;
            }
        }) ;
        if (menuItemSortKeys.isEmpty()) {
            return null ;
        }
        return (Integer)Collections.max(menuItemSortKeys) ;
    }

    public void removeMenuItem( MenuItemDomainObject menuItem ) {
        menuItems.remove( menuItem );
    }

    private Comparator getMenuItemComparatorForSortOrder( int sortOrder ) {

        Comparator comparator = MenuItemComparator.HEADLINE.chain( MenuItemComparator.ID );
        switch ( sortOrder ) {
            case MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER:
                comparator = MenuItemComparator.TREE_SORT_KEY.chain(comparator);
                break ;
            case MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED:
                comparator = MenuItemComparator.SORT_KEY.reversed().chain(comparator) ;
                break;
            case MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED:
                comparator = MenuItemComparator.MODIFIED_DATETIME.reversed().chain(comparator);
                break;
        }
        return comparator ;
    }

}