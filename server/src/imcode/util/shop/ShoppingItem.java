package imcode.util.shop;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class representing one ShoppingItem
 */
public class ShoppingItem implements Comparable {

    /**
     * The price of the item *
     */
    private double price = 0;

    /**
     * The descriptions of the item, maps Integer to String *
     */
    private Map descriptions = new TreeMap();

    /**
     * get-method for price
     *
     * @return the value of price
     */
    public double getPrice() {
        return this.price;
    }

    /**
     * set-method for price
     *
     * @param price Value for price
     */
    public void setPrice( double price ) {
        this.price = price;
    }

    /**
     * get-method for descriptions
     *
     * @return the value of descriptions
     * @deprecated Use getDescription() instead.
     */
    public Map getDescriptions() {
        return this.descriptions;
    }

    /**
     * set-method for descriptions
     *
     * @param descriptions Value for descriptions
     * @deprecated Use setDescription() instead.
     */
    public void setDescriptions( Map descriptions ) {
        this.descriptions = descriptions;
    }

    /**
     * Set one description of the item.
     */
    public void setDescription( int i, String description ) {
        if ( null == description ) {
            descriptions.remove( new Integer( i ) );
        } else {
            descriptions.put( new Integer( i ), description );
        }
    }

    /**
     * Get one description of the item.
     */
    public String getDescription( int i ) {
        String description = (String)descriptions.get( new Integer( i ) );

        return null == description ? "" : description;
    }

    /**
     * Determine whether this ShoppingItem is equal to another.
     *
     * @param o the object to compare to.
     * @return true if and only if o is an instance of ShoppingItem, and has the same price and descriptions.
     */
    public boolean equals( Object o ) {
        if ( o instanceof ShoppingItem ) {
            ShoppingItem item = (ShoppingItem)o;
            return item.price == price && item.descriptions.equals( descriptions );
        } else {
            return false;
        }
    }

    public int hashCode() {
        return ( new Double( price ) ).hashCode() ^ descriptions.hashCode();
    }

    /**
     * Compare a ShoppingItem to another. *
     */
    public int compareTo( Object o ) {
        ShoppingItem item = (ShoppingItem)o;

        int descriptionComparison = compareDescriptionTo( item );
        return 0 != descriptionComparison ? descriptionComparison : comparePriceTo( item );
    }

    int compareDescriptionTo( ShoppingItem item ) {
        /* Compare descriptions. */
        Iterator it1 = descriptions.entrySet().iterator();
        Iterator it2 = item.descriptions.entrySet().iterator();
        while ( it1.hasNext() || it2.hasNext() ) {
            if ( it1.hasNext() && it2.hasNext() ) {
                Map.Entry desc1 = (Map.Entry)it1.next();
                Map.Entry desc2 = (Map.Entry)it2.next();
                int descCompare = ( (Integer)desc1.getKey() ).compareTo( (Integer)desc2.getKey() );
                if ( 0 != descCompare ) {
                    return -descCompare;
                }
                descCompare = ( (String)desc1.getValue() ).compareTo( (String)desc2.getValue() );
                if ( 0 != descCompare ) {
                    return descCompare;
                }
            } else {
                return it1.hasNext() ? 1 : -1; // Does it1 have more descriptions?
            }
        }
        return 0;
    }

    private int comparePriceTo( ShoppingItem item ) {
        /* Compare price */
        if ( price < item.price ) {
            return -1;
        } else if ( price > item.price ) {
            return +1;
        }
        return 0;
    }

    public String toString() {
        return descriptions.toString() + " " + price;
    }
}
