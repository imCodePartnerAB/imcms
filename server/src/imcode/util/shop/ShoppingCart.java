package imcode.util.shop ;

import java.util.* ;

import org.apache.log4j.Logger ;

public class ShoppingCart {

    public final static String SESSION_NAME = "imcode.ShoppingCart" ;

    private static Logger log = Logger.getLogger( ShoppingCart.class.getName() ) ;

    private Map cart = Collections.synchronizedMap(new HashMap()) ;

    public void putItem(ShoppingItem item, int quantity) {
	log.debug("Putting "+quantity+" of item with price "+item.getPrice()) ;
	cart.put(item, new Integer(quantity)) ;
    }

    public void addItem(ShoppingItem item, int quantity) {
	int oldQuantity = countItem(item) ;
	log.debug("Adding "+quantity+" of item with price "+item.getPrice()+" to old count of "+oldQuantity) ;
	putItem(item, quantity + oldQuantity) ;
    }

    public void removeItem(ShoppingItem item) {
	cart.remove(item) ;
    }

    public int countItem(ShoppingItem item) {
	Integer count = (Integer)cart.get(item) ;
	if (null == count) {
	    return 0 ;
	}
	return count.intValue() ;
    }

    public int countItems() {
	int totalItems = 0 ;
	synchronized (cart) {
	    Iterator cartIterator = cart.entrySet().iterator() ;
	    while (cartIterator.hasNext()) {
		Map.Entry cartEntry = (Map.Entry)cartIterator.next() ;
		Integer entryCount = (Integer)cartEntry.getValue() ;
		totalItems += entryCount.intValue() ;
	    }
	}
	return totalItems ;
    }

    public ShoppingItem[] getItems() {
	Set itemSet = cart.keySet() ;
	synchronized (cart) {
	    return (ShoppingItem[])itemSet.toArray(new ShoppingItem[cart.size()]) ;
	}
    }
}
