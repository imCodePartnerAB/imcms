package imcode.util.shop ;

import java.util.* ;

/**
   Class representing one ShoppingItem
**/
public class ShoppingItem {

    /** The price of the item **/
    private double price = 0 ;

    /** The descriptions of the item, maps Integer to String **/
    private Map descriptions = new HashMap() ;

    /**
       get-method for price

       @return the value of price
    **/
    public double getPrice()  {
	return this.price;
    }

    /**
       set-method for price

       @param price Value for price
    **/
    public void setPrice(double price) {
	this.price = price;
    }

    /**
       get-method for descriptions

       @return the value of descriptions
    **/
    public Map getDescriptions()  {
	return this.descriptions;
    }

    /**
       set-method for descriptions

       @param descriptions Value for descriptions
    **/
    public void setDescriptions(Map descriptions) {
	this.descriptions = descriptions;
    }

    /**
       Determine whether this ShoppingItem is equal to another.
       @param o the object to compare to.

       @return true if and only if o is an instance of ShoppingItem, and has the same price and descriptions.
    **/
    public boolean equals(Object o) {
	if (o instanceof ShoppingItem) {
	    ShoppingItem item = (ShoppingItem) o;
	    return item.price == price && item.descriptions.equals(descriptions) ;
	} else {
	    return false ;
	}
    }

    public int hashCode() {
	return (new Double(price)).hashCode() ^ descriptions.hashCode() ;
    }

}
