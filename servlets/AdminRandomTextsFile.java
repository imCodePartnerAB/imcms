import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.server.* ;
import imcode.util.* ;
import java.text.*;
import imcode.util.fortune.*;


public class AdminRandomTextsFile extends Administrator implements imcode.server.IMCConstants{
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private final static String HTML_TEMPLATE	= "admin_random_texts_file.html" ;
    private final static String OPTION_LINE	= "option_line.frag" ;
    private final static String DATE_ERROR		= "date_err_msg.frag" ;
    private final static String TEXT_ERROR		= "text_err_msg.frag" ;
    private final static SimpleDateFormat dateForm = new SimpleDateFormat("yyMMdd");

    /**
       The GET method creates the html page when this side has been
       redirected from somewhere else.
    **/
    public void doGet(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException{
	this.doPost(req,res);

    } // End doGet

    /**
       doPost
    */
    public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException{
	// Lets get the server this request was aimed for
	String host = req.getHeader("Host") ;
	String imcServer = Utility.getDomainPref("adminserver",host) ;

	HttpSession session = req.getSession();
	imcode.server.User user ;

	// Check if the user logged on
	if ( (user = Check.userLoggedOn(req,res,"StartDoc" )) == null )	{
	    return ;
	}

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	String whichFile = (String)session.getAttribute("file") ;

	if (req.getParameter("back")!=null || whichFile == null || "".equals(whichFile)) {
	    res.sendRedirect("AdminRandomTexts") ;
	    return;
	}

	List lines = (List)session.getAttribute("lines");
	String date1 = "";
	String date2 = "";
	String text  = "";

	String errMsgDate	= IMCServiceRMI.parseExternalDoc(imcServer, null, DATE_ERROR , user.getLangPrefix(), DOCTYPE_FORTUNES+"");
	String errMsgTxt	= IMCServiceRMI.parseExternalDoc(imcServer, null, TEXT_ERROR , user.getLangPrefix(), DOCTYPE_FORTUNES+"");

	if (req.getParameter("save")!=null) {
	    addLineToList(req,lines);

	    IMCServiceRMI.setQuoteList(imcServer, whichFile+".txt", lines);

	    //tillbaks till
	    res.sendRedirect("AdminRandomTexts") ;
	    return;

	} else if ((req.getParameter("add")).equals("add")){
	    //h�mta parametrar
	    date1 = req.getParameter("date1").trim();
	    date2 = req.getParameter("date2").trim();
	    text  = req.getParameter("text").trim();

	    boolean ok = true;
	    if( !checkDate(date1) )	{
		date1=errMsgDate;
		ok = false;
	    }

	    if( !checkDate(date2) )	{
		date2=errMsgDate;
		ok = false;
	    }

	    if( text.length()<1 ){
		text=errMsgTxt;
		ok = false;
	    }

	    if( ok ){
		addLineToList(req,lines);
		date1 = "";
		date2 = "";
		text  = "";
	    }
	} else if (req.getParameter("edit")!=null){
	    //h�mta raden som �r markerad
	    String row = req.getParameter("AdminFile") ;

	    //l�gg till en eventuellt redan uppflyttad rad
	    addLineToList(req,lines);

	    if (row != null){
		int theRow = Integer.parseInt(row);
		Quote quote = (Quote)lines.get(theRow);
		DateRange dates = quote.getDateRange();

		date1 = dateForm.format(dates.getStartDate());
		date2 = dateForm.format(dates.getEndDate());
		text  = quote.getText();
		lines.remove(quote);
	    }

	} else if (req.getParameter("remove")!=null) {

	    // retrieve list of rows to remove
	    String rows[] = req.getParameterValues("AdminFile") ;

	    try {
		// put the lines-list in a Map, keyed by list-index.
		Map rowMap = new HashMap() ;
		for(int i=0;i<lines.size();i++){
		    rowMap.put(new Integer(i), lines.get(i)) ;
		}

		// remove the given list-indexes.
		for(int i=0;i<rows.length;i++){
		    if (!"".equals(rows[i])) {
			rowMap.remove(Integer.decode(rows[i])) ;
		    }
		}

		// put the map back into a list, sorted by index.
		Integer[] listIndexes = new Integer[rowMap.size()] ;
		listIndexes = (Integer[])rowMap.keySet().toArray(listIndexes) ;
		Arrays.sort(listIndexes) ;

		lines = new ArrayList(rowMap.size()) ;
		for (int i = 0; i < listIndexes.length; ++i) {
		    lines.add(rowMap.get(listIndexes[i])) ;
		}
	    } catch (NullPointerException ignored) {
		// No rows were selected
	    } catch (NumberFormatException ignored) {
		// Invalid selection
	    }
	}

	session.setAttribute("lines",lines);

	StringBuffer buff = createOptionList(req,lines, imcServer, user );

	//Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
	Vector values = new Vector();
	values.add("#date1#");
	values.add(date1);
	values.add("#date2#");
	values.add(date2);
	values.add("#text#");
	values.add(text);
	values.add("#file#");
	values.add(whichFile);
	values.add("#options#");
	values.add(buff.toString());

	String parsed = IMCServiceRMI.parseExternalDoc(imcServer, values, HTML_TEMPLATE  , user.getLangPrefix(), DOCTYPE_FORTUNES+"");
	out.print(parsed);
	return;
    }

    private void addLineToList(HttpServletRequest req, List lines) throws ServletException, IOException{
	String	date1 = (req.getParameter("date1")).trim();
	String	date2 = (req.getParameter("date2")).trim();
	String	text =	(req.getParameter("text")).trim();

	if( checkDate(date1) && checkDate(date2) && text.length()>1 ){
	    try {
		DateRange range = new DateRange(dateForm.parse(date1), dateForm.parse(date2));
		Quote quote = new Quote(text,range);

		lines.add(quote);
	    }catch(ParseException pe) {
		//this will newer happen sense we already succeded parsing the dates
	    }
	}
    }

    private StringBuffer createOptionList(HttpServletRequest req, List lines, String server, imcode.server.User user ) throws ServletException, IOException {
	StringBuffer buff = new StringBuffer();
	int counter = 0;
	Iterator iter = lines.iterator();
	while (iter.hasNext()) {
	    Quote quote = (Quote) iter.next();
	    DateRange dates = quote.getDateRange();
	    buff.append("<option value=\""  + counter++ + "\">"+dateForm.format(dates.getStartDate()) +" "+dateForm.format(dates.getEndDate())+" "+ quote.getText() + "</option>");
	}
	return buff;
    }

    private boolean checkDate(String dateStr){
	try
	    {
		dateForm.parse(dateStr);
	    }catch(java.text.ParseException pe){
		return false;
	    }
	return true;
    }

    /**
       Log function, will work for both servletexec and Apache
    **/
    public void log( String str){
	super.log(str) ;
	System.out.println("AdminRandomTextsFile: " + str ) ;
    }

} // End of class
