package imcode.server.document.textdocument;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.UnhandledException;

import java.util.*;

public class MenuDomainObject implements Cloneable {

    private int id;
    private int sortOrder;
    private Map menuItems;

    public final static int MENU_SORT_ORDER__BY_HEADLINE = 1;
    public final static int MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED = 2;
    public final static int MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED = 3;
    public final static int MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER = 4;
    public final static int MENU_SORT_ORDER__DEFAULT = MENU_SORT_ORDER__BY_HEADLINE;

    public final static int DEFAULT_SORT_KEY = 500;
    private static final int DEFAULT_SORT_KEY_INCREMENT = 10;

    public MenuDomainObject() {
        this( 0, MENU_SORT_ORDER__DEFAULT );
    }

    public Object clone() throws CloneNotSupportedException {
        try {
            MenuDomainObject clone = (MenuDomainObject)super.clone() ;
            clone.menuItems = new HashMap() ;
            for ( Iterator iterator = menuItems.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry)iterator.next();
                Integer documentId = (Integer)entry.getKey();
                MenuItemDomainObject menuItem = (MenuItemDomainObject)entry.getValue();
                clone.menuItems.put(documentId, menuItem.clone()) ;
            }
            return clone ;
        } catch ( CloneNotSupportedException e ) {
            throw new UnhandledException( e );
        }
    }

    public MenuDomainObject( int id, int sortOrder ) {
        this.id = id;
        this.sortOrder = sortOrder;
        menuItems = new HashMap();
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
        MenuItemDomainObject[] menuItemsArray = (MenuItemDomainObject[])menuItems.values().toArray( new MenuItemDomainObject[menuItems.size()] );
        Arrays.sort( menuItemsArray, getMenuItemComparatorForSortOrder( sortOrder ) );
        return menuItemsArray;
    }

    public void addMenuItem( MenuItemDomainObject menuItem ) {
        if ( null == menuItem.getSortKey() ) {
            Integer maxSortKey = getMaxSortKey();
            Integer sortKey;
            if ( null != maxSortKey ) {
                sortKey = new Integer( maxSortKey.intValue() + DEFAULT_SORT_KEY_INCREMENT );
            } else {
                sortKey = new Integer( DEFAULT_SORT_KEY );
            }
            menuItem.setSortKey( sortKey );
        }

        menuItems.put( new Integer(menuItem.getDocumentReference().getDocumentId()), menuItem );
    }

    private Integer getMaxSortKey() {
        Collection menuItemSortKeys = CollectionUtils.collect( menuItems.values(), new Transformer() {
            public Object transform( Object o ) {
                return ( (MenuItemDomainObject)o ).getSortKey();
            }
        } );
        if ( menuItemSortKeys.isEmpty() ) {
            return null;
        }
        return (Integer)Collections.max( menuItemSortKeys );
    }

    private Comparator getMenuItemComparatorForSortOrder( int sortOrder ) {

        Comparator comparator = MenuItemComparator.HEADLINE.chain( MenuItemComparator.ID );
        if ( sortOrder == MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER ) {
            comparator = MenuItemComparator.TREE_SORT_KEY.chain( comparator );
        } else if ( sortOrder == MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED ) {
            comparator = MenuItemComparator.SORT_KEY.reversed().chain( comparator );
        } else if ( sortOrder == MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED ) {
            comparator = MenuItemComparator.MODIFIED_DATETIME.reversed().chain( comparator );
        }
        return comparator;
    }

    public void setSortOrder( int sortOrder ) {
        switch ( sortOrder ) {
            case MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED:
            case MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER:
            case MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED:
            case MENU_SORT_ORDER__BY_HEADLINE:
                this.sortOrder = sortOrder;
                break;
            default:
                throw new IllegalArgumentException( "Bad sort order. Use one of the constants." );
        }
    }

    public boolean isEmpty() {
        return menuItems.isEmpty();
    }

    public void removeMenuItemByDocumentId( int childId ) {
        menuItems.remove( new Integer( childId )) ;
    }

    public boolean equals( Object obj ) {
        if (!(obj instanceof MenuDomainObject)) {
            return false ;
        }
        MenuDomainObject otherMenu = (MenuDomainObject)obj;
        return otherMenu.sortOrder == sortOrder && otherMenu.menuItems.equals( menuItems ) ;
    }

    public int hashCode() {
        return sortOrder + menuItems.hashCode() ;
    }
}