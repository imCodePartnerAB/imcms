package imcode.util.shop ;

import java.util.* ;

import org.apache.commons.collections.* ;

import org.apache.log4j.Logger ;

public class ShoppingCart extends TreeBag {

    public final static String SESSION_NAME = "imcode.ShoppingCart" ;

    private static Logger log = Logger.getLogger( ShoppingCart.class.getName() ) ;

    public void putItem(ShoppingItem item, int quantity) {
	removeItem(item) ;
	add(item, quantity) ;
    }

    public void addItem(ShoppingItem item, int quantity) {
	add(item, quantity) ;
    }

    public void removeItem(ShoppingItem item) {
	remove(item, getCount(item)) ;
    }

    public int countItem(ShoppingItem item) {
	return getCount(item) ;
    }

    public int countItems() {
	return size() ;
    }

    public ShoppingItem[] getItems() {
	Set uniqueItems = uniqueSet() ;
	return (ShoppingItem[]) uniqueItems.toArray(new ShoppingItem[uniqueItems.size()]) ;
    }
}
