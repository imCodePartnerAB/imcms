package imcode.util.shop ;

import java.util.* ;
import java.text.* ;

import imcode.server.* ;
import imcode.server.user.UserDomainObject;

import org.apache.log4j.* ;

public class ShoppingOrderSystemImpl implements ShoppingOrderSystem {

    private IMCServiceInterface imcref ;

    private static Logger log = Logger.getLogger( ShoppingOrderSystemImpl.class.getName() ) ;

    public ShoppingOrderSystemImpl(IMCServiceInterface imcref) {
	this.imcref = imcref ;
    }

    public ShoppingOrder getShoppingOrderForUserById(UserDomainObject user, int orderId) {
	int userId = user.getUserId() ;

	String[] dbData = imcref.sqlProcedure("Shop_GetShoppingOrderForUserById", new String[] { String.valueOf(userId), String.valueOf(orderId) }) ;

	return getShoppingOrderFromDbData(dbData) ;
    }

    /** Retrieve a List of all ShoppingOrders for a User, sorted by datetime. **/
    public List getShoppingOrdersForUser(UserDomainObject user) {
	int userId = user.getUserId() ;
	String[][] dbData = imcref.sqlProcedureMulti("Shop_GetShoppingOrdersForUser", new String[] { String.valueOf(userId) }) ;

	List theList = new ArrayList(dbData.length) ;
	for (int i = 0; i < dbData.length; ++i) {
	    theList.add(getShoppingOrderFromDbData(dbData[i])) ;
	}
	return theList ;
    }

    private ShoppingOrder getShoppingOrderFromDbData(String[] dbData) {
	if (dbData == null || 0 == dbData.length) {
	    return null ;
	}
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;

	int orderId = Integer.parseInt(dbData[0]) ;
	int userId = Integer.parseInt(dbData[2]) ;
	UserDomainObject user = imcref.getUserById(userId) ;
	String datetimeStr = dbData[1] ;

	ShoppingOrder theOrder = new ShoppingOrder() ;
	try {
	    Date datetime = dateFormat.parse(datetimeStr) ;
	    theOrder.setDatetime(datetime) ;
	} catch (ParseException ex) {
	    log.error("Non-parseable date from database, userId "+userId+", orderId "+orderId+", datetime '"+datetimeStr+"'",ex) ;
	}

	theOrder.setId(new Integer(orderId)) ;
	theOrder.setUser(user) ;

	addShoppingItemsToOrder(theOrder) ;

	return theOrder ;
    }

    private void addShoppingItemsToOrder(ShoppingOrder theOrder) {
	String[][] dbData = imcref.sqlProcedureMulti("Shop_GetShoppingItemsForOrder", new String[] { String.valueOf(theOrder.getId()) }) ;

	for (int i = 0; i < dbData.length; ++i) {
	    ShoppingItem item = new ShoppingItem() ;

	    int itemId = Integer.parseInt(dbData[i][0]) ;
	    double price = Double.parseDouble(dbData[i][1]) ;
	    int quantity = Integer.parseInt(dbData[i][2]) ;

	    item.setDescriptions(getDescriptionsForShoppingItem(itemId)) ;
	    item.setPrice(price) ;

	    theOrder.addItem(item, quantity) ;
	}
    }

    private Map getDescriptionsForShoppingItem(int itemId) {
	String[][] dbData = imcref.sqlProcedureMulti("Shop_GetDescriptionsForShoppingItem", new String[] { String.valueOf(itemId) }) ;

	Map theDescriptions = new HashMap() ;
	for (int i = 0; i < dbData.length; ++i) {
	    Integer descriptionNumber = Integer.valueOf(dbData[i][0]) ;
	    String description = dbData[i][1] ;
	    theDescriptions.put(descriptionNumber, description) ;
	}
	return theDescriptions ;
    }

    /** Add a shopping order. **/
    public void addShoppingOrder(ShoppingOrder theOrder) throws NullPointerException {

	UserDomainObject user = theOrder.getUser() ;
	Date datetime = theOrder.getDatetime() ;
	if (null == user) {
	    throw new NullPointerException("null == user") ;
	}
	if (null == datetime) {
	    throw new NullPointerException("null == datetime") ;
	}

	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;

	String orderIdStr = imcref.sqlProcedureStr("Shop_AddShoppingOrder", new String[] {""+theOrder.getUser().getUserId(), dateFormat.format(theOrder.getDatetime()) } ) ;

	addShoppingItemsToOrderInDb(orderIdStr, theOrder, theOrder.getItems()) ;
    }

    private void addShoppingItemsToOrderInDb(String orderIdStr, ShoppingOrder order, ShoppingItem[] items) {
	for (int i = 0; i < items.length; ++i) {
	    ShoppingItem item = items[i] ;
	    String itemIdStr = imcref.sqlProcedureStr("Shop_AddShoppingItemToOrder", new String[] { orderIdStr, ""+item.getPrice(), ""+order.countItem(item) }) ;
	    addDescriptionsToShoppingItemInDb(itemIdStr, item.getDescriptions()) ;
	}
    }

    private void addDescriptionsToShoppingItemInDb(String itemIdStr, Map descriptions) {
	for (Iterator it = descriptions.entrySet().iterator(); it.hasNext(); ) {
	    Map.Entry entry = (Map.Entry)it.next() ;
	    Integer descriptionNumber = (Integer) entry.getKey() ;
	    String  description = (String) entry.getValue() ;
	    imcref.sqlUpdateProcedure("Shop_AddShoppingItemDescription", new String[] { itemIdStr, ""+descriptionNumber, description } ) ;
	}
    }
}
