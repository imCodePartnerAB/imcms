package imcode.util.shop ;

import java.util.* ;

import imcode.server.user.UserDomainObject ;

public interface ShoppingOrderSystem {

    /** Retrieve a single shopping order by id. **/
    public ShoppingOrder getShoppingOrderForUserById(imcode.server.user.UserDomainObject user, int orderId) ;

    /** Retrieve a List of all ShoppingOrders for a User, sorted by datetime. **/
    public List getShoppingOrdersForUser(imcode.server.user.UserDomainObject user) ;

    /** Add a shopping order. **/
    public void addShoppingOrder(ShoppingOrder order) ;

}
