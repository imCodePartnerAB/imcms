package imcode.util.shop ;

import java.util.Date ;

import imcode.server.User ;

/**
   Value-class for a shopping order.
**/
public class ShoppingOrder extends ShoppingCart {

    private Integer id ;
    private User user ;
    private Date datetime ;

    public ShoppingOrder() {
	super() ;
    }

    public ShoppingOrder(ShoppingCart cart) {
	this.addAll(cart) ;
    }

    /**
       get-method for id

       @return the value of id
    **/
    public Integer getId()  {
	return this.id;
    }

    /**
       set-method for id

       @param id Value for id
    **/
    public void setId(Integer id) {
	this.id = id;
    }

    /**
       get-method for user

       @return the value of user
    **/
    public User getUser()  {
	return this.user;
    }

    /**
       set-method for user

       @param user Value for user
    **/
    public void setUser(User user) {
	this.user = user;
    }

    /**
       get-method for datetime

       @return the value of datetime
    **/
    public Date getDatetime()  {
	return this.datetime;
    }

    /**
       set-method for datetime

       @param datetime Value for datetime
    **/
    public void setDatetime(Date datetime) {
	this.datetime = datetime;
    }

}
