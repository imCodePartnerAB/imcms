import javax.servlet.* ;
import javax.servlet.http.* ;

import java.io.* ;
import java.text.* ;
import java.util.* ;

import org.apache.oro.text.regex.* ;
import org.apache.oro.text.perl.* ;

import org.apache.log4j.Logger ;

import imcode.util.shop.* ;
import imcode.util.* ;

import imcode.server.parser.* ;
import imcode.server.* ;
import imcode.server.util.DateHelper;
import imcode.server.user.UserDomainObject;

public class PutInShoppingCart extends HttpServlet {

    private final static String SHOP_CONFIG = "shop.properties" ;

    private final static String MAIL_ITEM_FORMAT = "shop/mailitemformat.txt" ;
    private final static String MAIL_FORMAT      = "shop/mailformat.txt" ;

    private static Pattern HASHTAG_PATTERN = null ;

    static {
	Perl5Compiler patComp = new Perl5Compiler() ;
	try {
	    HASHTAG_PATTERN = patComp.compile("#(\\w+?)(\\d*)#",Perl5Compiler.READ_ONLY_MASK) ;
	} catch (MalformedPatternException ex) {
	    // ignored
	}
    }

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
		try {
		    if (null == quantity) {
			/* There was no item in temporary storage,
			   so create a new one */
			quantity = new ShoppingItemQuantity() ;
			int number = -1 ;
			if (null != req.getParameter("number_"+formitemno)) {
			    number = Integer.parseInt(req.getParameter("number_"+formitemno)) ;
			} else if (null != req.getParameter("add_"+formitemno)) {
			    number = Integer.parseInt(req.getParameter("add_"+formitemno)) ;
			    quantity.setAdd(true) ;
			}
			quantity.setQuantity(number) ;
			formItems.put(formitemno, quantity) ;
		    }

		    if ("number".equals(field)) {
			continue ;
		    }
		    if (!"".equals(paramValue)) {
			log.debug("Parsing '"+paramValue+"' for parameter "+aParameter) ;
		    }
		    if ("remove".equals(field)) {
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

	/* For each of the ShoppingItemQuantities in our temporary storage
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
		if (itemQuantity.getAdd()) {
		    /* We're supposed to add to the item in the cart */
		    cart.addItem(itemQuantity.getItem(), itemQuantity.getQuantity()) ;
		} else {
		    /* We're supposed to put (replace) the item in the cart */
		    cart.putItem(itemQuantity.getItem(), itemQuantity.getQuantity()) ;
		}
	    }
	}

	String forwardTo = null ;

	if (null != req.getParameter("send") || null != req.getParameter("send.x")) {
	    forwardTo = req.getParameter("send_next") ;
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
	    UserDomainObject user = null ;
	    // Check if user logged on
	    if ( (user=Check.userLoggedOn(req,res,forwardTo))==null ) {
		return ;
	    }
	    sendMail(req,user) ;

	    ShoppingOrderSystem shoppingOrderSystem = imcref.getShoppingOrderSystem() ;

	    ShoppingOrder theOrder = new ShoppingOrder(cart) ;
	    theOrder.setUser(user) ;
	    theOrder.setDatetime(new Date()) ;

	    /* Store the order in the database */
	    shoppingOrderSystem.addShoppingOrder(theOrder) ;

	    /* Replace the ShoppingCart in the session */
	    session.setAttribute(ShoppingCart.SESSION_NAME, new ShoppingCart()) ;
	    forwardTo = req.getParameter("send_next") ;
	} else {
	    /* Then we go somewhere else, as given by the "next"-parameter */
	    forwardTo = req.getParameter("next") ;
	}

	if (null == forwardTo || "".equals(forwardTo)) {
	    /* or, if there was no "next", back to where we came from */
	    forwardTo = req.getHeader("referer") ;
	}

	log.debug("Redirecting to "+forwardTo) ;
	/* Forward the request to the given location */
	res.sendRedirect(forwardTo) ;
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

    private void sendMail (HttpServletRequest req, UserDomainObject user) throws IOException {


        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

	String mailServer = Utility.getDomainPref( "smtp_server" );
	String stringMailPort = Utility.getDomainPref( "smtp_port" );
	String stringMailtimeout = Utility.getDomainPref( "smtp_timeout" );

	// Handling of default-values is another area where java can't hold a candle to perl.
	int mailport = 25 ;
	try	{
	    mailport = Integer.parseInt( stringMailPort );
	}catch (NumberFormatException ignored){
	    // Do nothing, let mailport stay at default.
	}

	int mailtimeout = 10000 ;
	try {
	    mailtimeout = Integer.parseInt( stringMailtimeout );
	}catch (NumberFormatException ignored){
	    // Do nothing, let mailtimeout stay at default.
	}

	String mailFromAddress = Prefs.get("mail-from-address", SHOP_CONFIG) ;
	String mailToAddress   = Prefs.get("mail-to-address",   SHOP_CONFIG) ;
	String mailSubject     = Prefs.get("mail-subject",      SHOP_CONFIG) ;
	String mailFormat      = imcref.parseDoc(null, MAIL_FORMAT, imcref.getDefaultLanguage()) ;
	String mailItemFormat  = imcref.parseDoc(null, MAIL_ITEM_FORMAT, imcref.getDefaultLanguage()) ;

	Perl5Matcher patternMatcher = new Perl5Matcher() ;

	StringBuffer mailItems = new StringBuffer() ;

	ShoppingCart cart = (ShoppingCart)req.getSession(true).getAttribute(ShoppingCart.SESSION_NAME) ;
	ShoppingItem[] items = cart.getItems() ;

	DecimalFormat priceFormat = createDecimalFormat(null, ".", null) ;

	double totalPrice = 0 ;
	for (int i = 0; i < items.length; ++i) {
	    ShoppingItem item = items[i] ;

	    /* Create a hashmap to use for storing substitutions for a MapSubstitution */
	    HashMap itemStringMap = new HashMap() ;

	    /* Put the item-descriptions in the map */
	    Iterator itemDescriptionIterator = item.getDescriptions().entrySet().iterator() ;
	    while (itemDescriptionIterator.hasNext()) {
		Map.Entry itemDescriptionEntry = (Map.Entry)itemDescriptionIterator.next() ;
		itemStringMap.put("#desc"+itemDescriptionEntry.getKey()+"#",
				  itemDescriptionEntry.getValue()) ;
	    }

	    /* Put the price in the map */
	    itemStringMap.put("#price#",priceFormat.format(item.getPrice())) ;

	    /* Put the quantity in the map */
	    int quantity = cart.countItem(item) ;
	    itemStringMap.put("#quantity#",""+quantity) ;

	    /* Put the total item price for this item in the map */
	    itemStringMap.put("#total_price#",priceFormat.format(quantity*item.getPrice())) ;

	    /* Add to the total price for all items */
	    totalPrice += quantity*item.getPrice() ;

	    /* Replace the tags in the mailitemformat with the appropriate data from the map */
	    String mailItem = Util.substitute(patternMatcher,
					      HASHTAG_PATTERN,
					      new MapSubstitution(itemStringMap,false),
					      mailItemFormat,
					      Util.SUBSTITUTE_ALL) ;
	    mailItems.append(mailItem) ;
	}

	DateFormat dateFormat = DateHelper.DATE_TIME_FORMAT_IN_DATABASE ;

	HashMap mailStringMap = new HashMap() ;
	mailStringMap.put("#items#",               mailItems.toString()) ;
	mailStringMap.put("#datetime#",            dateFormat.format(new Date())) ;
	mailStringMap.put("#user_login_name#",     user.getLoginName()) ;
	mailStringMap.put("#user_title#",          user.getTitle()) ;
	mailStringMap.put("#user_full_name#",      user.getFullName()) ;
	mailStringMap.put("#user_first_name#",     user.getFirstName()) ;
	mailStringMap.put("#user_last_name#",      user.getLastName()) ;
	mailStringMap.put("#user_email#",          user.getEmailAddress()) ;
	mailStringMap.put("#user_workphone#",      user.getWorkPhone()) ;
	mailStringMap.put("#user_mobilephone#",    user.getMobilePhone()) ;
	mailStringMap.put("#user_homephone#",      user.getHomePhone()) ;
	mailStringMap.put("#user_company#",        user.getCompany()) ;
	mailStringMap.put("#user_address#",        user.getAddress()) ;
	mailStringMap.put("#user_zip#",            user.getZip()) ;
	mailStringMap.put("#user_city#",           user.getCity()) ;
	mailStringMap.put("#user_country#",        user.getCountry()) ;
	mailStringMap.put("#user_county_council#", user.getCountyCouncil()) ;
	mailStringMap.put("#total_price#",         priceFormat.format(totalPrice)) ;

	/* Put the mailitems in the mail */
	String mail = Util.substitute(patternMatcher,
				      HASHTAG_PATTERN,
				      new MapSubstitution(mailStringMap, false),
				      mailFormat,
				      Util.SUBSTITUTE_ALL) ;


	log.debug("Sending mail to "+mailToAddress) ;
	/* Send the mail */
	SMTP smtp = new SMTP(mailServer,mailport,mailtimeout) ;
	smtp.sendMailWait(mailFromAddress, mailToAddress, mailSubject,mail) ;

    }

    /**
       Class representing a ShoppingItem and how many of them there are, their quantity.
       Used to temporarily represent a quantity of one ShoppingItem before putting it in the ShoppingCart.
    **/
    private class ShoppingItemQuantity {

	/** The item **/
	private ShoppingItem item = new ShoppingItem() ;

	/** The quantity of item,
	    -1 means no quantity given **/
	private int quantity = -1 ;

	/** Whether to remove the item instead of putting it **/
	private boolean remove = false ;

	/** Whether to add the item instead of putting it **/
	private boolean add = false ;

	/**
	   get-method for item

	   @return the value of item
	**/
	public ShoppingItem getItem() {
	    return this.item;
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

	   @return Whether the item is supposed to be removed from the ShoppingCart
	**/
	public boolean getRemove() {
	    return this.remove ;
	}

	/**
	   set-method for remove

	   @param remove Whether the item is supposed to be removed from the ShoppingCart
	**/
	public void setRemove(boolean remove) {
	    this.remove = remove ;
	}

	/**
	   get-method for add

	   @return Whether the item is supposed to be added to the the cart instead of replacing one in the cart.
	**/
	public boolean getAdd() {
	    return this.add ;
	}

	/**
	   set-method for add

	   @param add Whether the item is supposed to be added to the the cart instead of replacing one in the cart.
	**/
	public void setAdd(boolean add) {
	    this.add = add ;
	}
    }
}
