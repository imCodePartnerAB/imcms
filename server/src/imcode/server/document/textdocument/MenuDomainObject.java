/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-maj-03
 * Time: 17:36:40
 */
package imcode.server.document.textdocument;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Comparator;

public class MenuDomainObject {

    private int id;
    private int sortOrder;
    private SortedSet menuItems;

    public final static int MENU_SORT_ORDER__BY_HEADLINE = 1;
    public final static int MENU_SORT_ORDER__BY_MANUAL_ORDER = 2;
    public final static int MENU_SORT_ORDER__BY_MODIFIED_DATETIME = 3;
    public final static int MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER = 4;
    public final static int MENU_SORT_ORDER__DEFAULT = MENU_SORT_ORDER__BY_HEADLINE;

    public final static int DEFAULT_SORT_KEY = 500;

    public MenuDomainObject() {
        this(0, MENU_SORT_ORDER__DEFAULT) ;
    }

    public MenuDomainObject( int id, int sortOrder ) {
        this.id = id;
        this.sortOrder = sortOrder;
        menuItems = new TreeSet( getMenuItemComparatorForSortOrder( sortOrder ) );
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
        menuItems.add( menuItem );
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
            case MENU_SORT_ORDER__BY_MANUAL_ORDER:
                comparator = MenuItemComparator.SORT_KEY.chain(comparator);
                break;
            case MENU_SORT_ORDER__BY_MODIFIED_DATETIME:
                comparator = MenuItemComparator.MODIFIED_DATETIME.chain(comparator);
                break;
        }
        return comparator ;
    }

}