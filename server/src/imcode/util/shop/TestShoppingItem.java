package imcode.util.shop ;

import java.util.* ;

import junit.framework.* ;

public class TestShoppingItem extends TestCase {

    public TestShoppingItem(String name) {
	super(name) ;
    }

    ShoppingItem[] items = new ShoppingItem[8] ;

    protected void setUp() {
	items[0] = new ShoppingItem() ;
	items[0].setDescription(2,"Desc") ;
	items[0].setPrice(0.1) ;
	items[1] = new ShoppingItem() ;
	items[1].setDescription(2,"XXX") ;
	items[1].setPrice(1.2) ;
	items[2] = new ShoppingItem() ;
	items[2].setDescription(1,"Desc") ;
	items[2].setPrice(2.3) ;
	items[3] = new ShoppingItem() ;
	items[3].setDescription(1,"Desc") ;
	items[3].setDescription(2,"Desc") ;
	items[3].setPrice(3.4) ;
	items[4] = new ShoppingItem() ;
	items[4].setDescription(1,"Desc") ;
	items[4].setDescription(2,"XXX") ;
	items[4].setPrice(4.5) ;
	items[5] = new ShoppingItem() ;
	items[5].setDescription(1,"XXX") ;
	items[5].setDescription(2,"Desc") ;
	items[5].setPrice(5.6) ;
	items[6] = new ShoppingItem() ;
	items[6].setDescription(1,"XXX") ;
	items[6].setDescription(2,"XXX") ;
	items[6].setPrice(6.7) ;
	items[7] = new ShoppingItem() ;
	items[7].setDescription(1,"XXX") ;
	items[7].setDescription(2,"XXX") ;
	items[7].setPrice(7.8) ;
    }

    public void testPrice() {
	ShoppingItem item1 = new ShoppingItem() ;
	assertEquals(0, item1.getPrice(), 0) ;
	item1.setPrice(1.33) ;
	assertEquals(1.33, item1.getPrice(), 0) ;
    }

    public void testEquals() {
	ShoppingItem item1 = new ShoppingItem() ;
	ShoppingItem item2 = new ShoppingItem() ;
	item1.setPrice(2.66) ;
	assertEquals(item1, item1) ;
	assertTrue(!item1.equals(item2)) ;
	assertTrue(!item2.equals(item1)) ;
	item2.setPrice(2.66) ;
	assertEquals(item1,item2) ;
	item2.setDescription(1, "Item 2 Desc 1") ;
	assertTrue(!item2.equals(item1)) ;
    }

    public void testDescriptions() {
	ShoppingItem item1 = new ShoppingItem() ;
	item1.setDescription(1,"Item 1 Desc 1") ;
	item1.setDescription(3,"Item 1 Desc 3") ;
	assertEquals("Item 1 Desc 1", item1.getDescription(1)) ;
	assertEquals("Item 1 Desc 3", item1.getDescription(3)) ;
	item1.setDescription(1,null) ;
	assertEquals("", item1.getDescription(1)) ;
    }

    public void testCompareTo() {
	ShoppingItem item1 = new ShoppingItem() ;
	item1.setDescription(1,"Desc 1") ;
	item1.setDescription(2,"Desc 2") ;
	item1.setPrice(1) ;
	ShoppingItem item2 = new ShoppingItem() ;
	item2.setDescription(1,"Desc 1") ;
	item2.setDescription(2,"Desc 2") ;
	item2.setPrice(2) ;

	assertEquals(0, item1.compareTo(item1)) ;
	assertEquals(0, item2.compareTo(item2)) ;

	assertEquals(-1, item1.compareTo(item2)) ;
	assertEquals(1,  item2.compareTo(item1)) ;

	ShoppingItem item3 = new ShoppingItem() ;
	item3.setDescription(1,"Desc 1") ;
	item3.setDescription(3,"Desc 3") ;
	item3.setPrice(1) ;

	assertEquals(1, item2.compareTo(item3)) ;

	item1.setPrice(4) ;
	item1.setDescription(3, "Desc 3") ;

	assertEquals(1, item1.compareTo(item3)) ;

	item1.setDescription(2, "Desc 3-1") ;
	item3.setDescription(2, "Desc 2") ;
	assertEquals(1, item1.compareTo(item3)) ;
	assertEquals(1, item1.compareTo(item2)) ;

	item1.setDescription(1,null) ;
	assertEquals(-1, item1.compareTo(item2)) ;
    }

    public void testComparePriceTo() {
	ShoppingItem item1 = new ShoppingItem() ;
	item1.setPrice(1.33) ;
	ShoppingItem item2 = new ShoppingItem() ;
	item2.setPrice(2.66) ;
	assertEquals( 0, item1.compareTo(item1)) ;
	assertEquals( 0, item2.compareTo(item2)) ;
	assertEquals(-1, item1.compareTo(item2)) ;
	assertEquals( 1, item2.compareTo(item1)) ;
    }

    public void testCompareDescriptionTo() {
	ShoppingItem item1 = new ShoppingItem() ;
	item1.setDescription(2,"Desc") ;
	ShoppingItem item2 = new ShoppingItem() ;
	item2.setDescription(1,"Desc") ;
	ShoppingItem item3 = new ShoppingItem() ;
	item3.setDescription(1,"Desc") ;
	item3.setDescription(2,"Desc") ;
	ShoppingItem item4 = new ShoppingItem() ;
	item4.setDescription(1,"XXX") ;
	item4.setDescription(2,"Desc") ;

	assertTrue(0 > item1.compareDescriptionTo(item2)) ;
	assertTrue(0 > item1.compareDescriptionTo(item3)) ;
	assertTrue(0 > item1.compareDescriptionTo(item4)) ;
	assertTrue(0 < item2.compareDescriptionTo(item1)) ;
	assertTrue(0 > item2.compareDescriptionTo(item3)) ;
	assertTrue(0 > item2.compareDescriptionTo(item4)) ;
	assertTrue(0 < item3.compareDescriptionTo(item2)) ;
	assertTrue(0 < item3.compareDescriptionTo(item1)) ;
	assertTrue(0 > item3.compareDescriptionTo(item4)) ;
	assertTrue(0 < item4.compareDescriptionTo(item1)) ;
	assertTrue(0 < item4.compareDescriptionTo(item2)) ;
	assertTrue(0 < item4.compareDescriptionTo(item3)) ;
    }

    public void testCompareToSort() {

	List itemsList = new ArrayList(Arrays.asList(items)) ;
	Collections.reverse(itemsList) ;

	ShoppingItem[] sortedItems = (ShoppingItem[]) itemsList.toArray(new ShoppingItem[items.length]) ;

	Arrays.sort(sortedItems) ;

	for (int i=0; i < items.length; ++i) {
	    for (int j=0; j < items.length; ++j) {
		if (i == j) {
		    assertEquals("items["+i+"] == cartItems["+j+"]\n"+itemsToString(sortedItems), items[i], sortedItems[j]) ;
		} else {
		    assertTrue("items["+i+"] != cartItems["+j+"]\n"+itemsToString(sortedItems), items[i] != sortedItems[j]) ;
		}
	    }
	}
    }

    public String itemsToString(ShoppingItem[] items) {
	StringBuffer result = new StringBuffer() ;

	for (int i=0; i < items.length; ++i) {
	    result.append(items[i].toString()) ;
	    result.append('\n') ;
	}
	return result.toString() ;
    }
}
