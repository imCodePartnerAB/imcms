import javax.servlet.* ;
import javax.servlet.http.* ;

import java.io.* ;
import java.text.* ;
import java.util.* ;

import org.apache.oro.text.perl.Perl5Util ;
import org.apache.log4j.Logger ;

import imcode.util.shop.* ;

public class PutInShoppingCart extends HttpServlet {

    private static Logger log = Logger.getLogger( PutInShoppingCart.class.getName() ) ;

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

	/* Create a DecimalFormat usable for parsing the price-parameter */
	DecimalFormat priceFormat = createDecimalFormat(req.getParameter("priceformatpattern"),
							req.getParameter("priceformatgroupingseparator"),
							req.getParameter("priceformatdecimalseparator")) ;

	/*
	  Temporary storage for our ShoppingItems as ShoppingItemQuantities
	  before putting them in the ShoppingCart
	*/
	HashMap formItems = new HashMap() ;

	log.debug("Using format "+priceFormat.toPattern()+" which means "+priceFormat.toLocalizedPattern()) ;

	/* A perl-regexp-matcher */
	Perl5Util perl = new Perl5Util() ;

	/* Get the parameters */
	Enumeration paramEnum = req.getParameterNames() ;

	/* Loop through the parameters */
	while (paramEnum.hasMoreElements()) {

	    /* Get the next parameter */
	    String aParameter = (String)paramEnum.nextElement() ;

	    /* Check if the parameter looks like one we're interested in */
	    if (perl.match("/^(\\w+?)(\\d*)_(\\d+)$/", aParameter)) {
		String field = perl.group(1) ;
		String fieldno = perl.group(2) ;
		String formitemno = perl.group(3) ;
		String paramValue = req.getParameter(aParameter) ;

		/* We temporarily hold the item in a ShoppingItemQuantity
		   in the map formItems, until we put it in the ShoppingCart */
		ShoppingItemQuantity quantity = (ShoppingItemQuantity)formItems.get(formitemno) ;
		if (null == quantity) {
		    /* There was no item in temporary storage,
		       so create a new one */
		    quantity = new ShoppingItemQuantity() ;
		    formItems.put(formitemno, quantity) ;
		}

		try {
		    if (!"".equals(paramValue)) {
			log.debug("Parsing '"+paramValue+"' for parameter "+aParameter) ;
		    }
		    if ("number".equals(field)) {
			/* The quantity of this item */
			quantity.setQuantity(Integer.parseInt(paramValue)) ;
		    } else if ("remove".equals(field)) {
			quantity.setRemove(true) ;
		    } else if ("price".equals(field)) {
			/* Parse the given price with the DecimalFormat we created from the given parameters */
			quantity.getItem().setPrice(priceFormat.parse(paramValue).doubleValue()) ;
		    } else if ("desc".equals(field) && fieldno.length() > 0) {
			/* Put this description-field in the ShoppingItem */
			quantity.getItem().getDescriptions().put(Integer.valueOf(fieldno),paramValue) ;
		    }
		} catch (NumberFormatException nfe) {
		    /* Something was wrong with this field. Ignore it. */
		    if (!"".equals(paramValue)) {
			log.debug("Failed to parse number: "+nfe) ;
		    }
		} catch (ParseException pe) {
		    /* Something was wrong with this field. Ignore it. */
		    if (!"".equals(paramValue)) {
			log.debug("Failed to parse price: "+pe) ;
		    }
		}
	    }
	}

	/* Get the session, creating a new one if needed */
	HttpSession session = req.getSession(true) ;

	/* Get the ShoppingCart, if there is one, from the session */
	ShoppingCart cart = (ShoppingCart)session.getAttribute(ShoppingCart.SESSION_NAME) ;
	if (null == cart) {
	    /* Otherwise, create a new ShoppingCart */
	    cart = new ShoppingCart() ;
	    /* and put it in the session */
	    session.setAttribute(ShoppingCart.SESSION_NAME, cart) ;
	}

	/* For each of the ShoppingItemQuantities in our tempory storage
	   of items from the webpage...	*/
	Iterator formItemsIterator = formItems.values().iterator() ;
	while (formItemsIterator.hasNext()) {
	    ShoppingItemQuantity itemQuantity = (ShoppingItemQuantity)formItemsIterator.next() ;

	    /* If this item is supposed to be removed from the ShoppingCart
	       or if a quantity of zero (0) was specified. */
	    if (itemQuantity.getRemove() || 0 == itemQuantity.getQuantity()) {
		/* remove it */
		cart.removeItem(itemQuantity.getItem()) ;
	    } else if (itemQuantity.getQuantity() > 0) {
		/* Else, if there are more than 0 of the item,
		   put the given quantity of the item in the ShoppingCart */
		cart.putItem(itemQuantity.getItem(), itemQuantity.getQuantity()) ;
	    }
	}

	/* Then we go somewhere else, as given by the "next"-parameter */
	String forwardTo = req.getParameter("next") ;
	if (null == forwardTo || "".equals(forwardTo)) {
	    /* or, if there was no "next", back to where we came from */
	    forwardTo = req.getHeader("referer") ;
	}

	/* Forward the request to the given location */
	req.getRequestDispatcher(forwardTo).forward(req,res) ;
    }

    /**
       Used to create a DecimalFormat that can be used to parse the price-parameter.
    **/
    private DecimalFormat createDecimalFormat(String pattern, String groupingseparator, String decimalseparator) {
	DecimalFormat theDecimalFormat ;
	if (null != pattern) {
	    theDecimalFormat = new DecimalFormat(pattern) ;
	} else {
	    theDecimalFormat = new DecimalFormat() ;
	}

	DecimalFormatSymbols theSymbols = theDecimalFormat.getDecimalFormatSymbols() ;
	if (null != groupingseparator && !"".equals(groupingseparator)) {
	    theSymbols.setGroupingSeparator(groupingseparator.charAt(0)) ;
	}
	if (null != decimalseparator && !"".equals(decimalseparator)) {
	    theSymbols.setDecimalSeparator(decimalseparator.charAt(0)) ;
	}
	theDecimalFormat.setDecimalFormatSymbols(theSymbols) ;

	return theDecimalFormat ;
    }

    /**
       Class representing a ShoppingItem and how many of them there are, their quantity.
       Used to temporarily represent a quantity of one ShoppingItem before putting it in the ShoppingCart.
    **/
    private class ShoppingItemQuantity {

	/** The item **/
	private ShoppingItem item = new ShoppingItem() ;

	/** The quantity of item **/
	private int quantity = 0 ;

	/** Whether to remove the item instead of adding it **/
	private boolean remove = false ;

	/**
	   get-method for item

	   @return the value of item
	**/
	public ShoppingItem getItem() {
	    return this.item;
	}

	/**
	   set-method for item

	   @param item Value for item
	**/
	public void setItem(ShoppingItem item) {
	    this.item = item;
	}

	/**
	   get-method for quantity

	   @return the value of quantity
	**/
	public int getQuantity()  {
	    return this.quantity;
	}

	/**
	   set-method for quantity

	   @param quantity Value for quantity
	**/
	public void setQuantity(int quantity) {
	    this.quantity = quantity;
	}

	/**
	   get-method for remove

	   @return Whether the item this ShoppingItemQuantity represents is supposed to be removed from the ShoppingCart
	**/
	public boolean getRemove() {
	    return this.remove ;
	}

	/**
	   set-method for remove

	   @param remove Whether the item this ShoppingItemQuantity represents is supposed to be removed from the ShoppingCart
	**/
	public void setRemove(boolean remove) {
	    this.remove = remove ;
	}
    }
}
