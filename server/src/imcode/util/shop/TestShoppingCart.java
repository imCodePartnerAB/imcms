package imcode.util.shop;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestShoppingCart extends TestCase {

    public TestShoppingCart( String name ) {
        super( name );
    }

    private ShoppingItem[] items = new ShoppingItem[7];

    protected void setUp() {
        items[0] = new ShoppingItem();
        items[0].setDescription( 2, "Desc" );
        items[0].setPrice( 0.1 );
        items[1] = new ShoppingItem();
        items[1].setDescription( 2, "XXX" );
        items[1].setPrice( 1.2 );
        items[2] = new ShoppingItem();
        items[2].setDescription( 1, "Desc" );
        items[2].setPrice( 2.3 );
        items[3] = new ShoppingItem();
        items[3].setDescription( 1, "Desc" );
        items[3].setDescription( 2, "Desc" );
        items[3].setPrice( 3.4 );
        items[4] = new ShoppingItem();
        items[4].setDescription( 1, "Desc" );
        items[4].setDescription( 2, "XXX" );
        items[4].setPrice( 4.5 );
        items[5] = new ShoppingItem();
        items[5].setDescription( 1, "XXX" );
        items[5].setDescription( 2, "Desc" );
        items[5].setPrice( 5.6 );
        items[6] = new ShoppingItem();
        items[6].setDescription( 1, "XXX" );
        items[6].setDescription( 2, "XXX" );
        items[6].setPrice( 6.7 );
    }

    public void testAddAndCountAndRemove() {
        ShoppingCart theCart = new ShoppingCart();
        ShoppingItem item1 = new ShoppingItem();
        item1.setPrice( 1 );
        ShoppingItem item2 = new ShoppingItem();
        item2.setPrice( 2 );

        theCart.addItem( item1, 1 );
        assertEquals( 1, theCart.countItem( item1 ) );
        assertEquals( 1, theCart.countItems() );

        theCart.addItem( item2, 2 );
        assertEquals( 2, theCart.countItem( item2 ) );
        assertEquals( 3, theCart.countItems() );

        theCart.removeItem( item1 );
        assertEquals( 0, theCart.countItem( item1 ) );
        assertEquals( 2, theCart.countItem( item2 ) );
        assertEquals( 2, theCart.countItems() );

        theCart.putItem( item2, 4 );
        assertEquals( 0, theCart.countItem( item1 ) );
        assertEquals( 4, theCart.countItem( item2 ) );
        assertEquals( 4, theCart.countItems() );
    }

    public void testSorted() {
        ShoppingCart cart = new ShoppingCart();

        List list = new ArrayList( Arrays.asList( items ) );
        Collections.reverse( list );
        cart.addAll( list );

        ShoppingItem[] cartItems = cart.getItems();

        for ( int i = 0; i < items.length; ++i ) {
            for ( int j = 0; j < items.length; ++j ) {
                if ( i == j ) {
                    assertEquals( "items[" + i + "] == cartItems[" + j + "]\n" + itemsToString( cartItems ), items[i], cartItems[j] );
                } else {
                    assertTrue( "items[" + i + "] != cartItems[" + j + "]\n" + itemsToString( cartItems ), items[i]
                                                                                                           != cartItems[j] );
                }
            }
        }
    }

    private String itemsToString( ShoppingItem[] items ) {
        StringBuffer result = new StringBuffer();

        for ( int i = 0; i < items.length; ++i ) {
            result.append( items[i].toString() );
            result.append( '\n' );
        }
        return result.toString();
    }
}
