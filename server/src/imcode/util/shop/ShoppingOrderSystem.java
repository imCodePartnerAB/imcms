package imcode.util.shop ;

import java.util.* ;

import imcode.server.User ;

public interface ShoppingOrderSystem {

    /** Retrieve a single shopping order by id. **/
    public ShoppingOrder getShoppingOrderForUserById(User user, int orderId) ;

    /** Retrieve a List of all ShoppingOrders for a User, sorted by datetime. **/
    public List getShoppingOrdersForUser(User user) ;

    /** Add a shopping order. **/
    public void addShoppingOrder(ShoppingOrder order) ;

}
