import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.text.*;
import java.lang.*;
import imcode.util.* ;
import imcode.server.* ;

/**
   Overview class for handling the overview part of the calender.
   @author Robert Engzell


*/
public class CalenderOverview
{
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /**
       Head function discerning between different requests and returning the correct html.
       @param req servlet request object
       @param res servlet response object
       @param prop system properties
       @param user user properties
       @param variables vector of tags and information for the month overview
       @return html ready for viewing the overview
       @throws IOException if detected when handling the request
       @throws NumberFormatException if thrown when handling the request
    */
    public String makeOverview(HttpServletRequest req, HttpServletResponse res,
			       Properties prop, imcode.server.user.UserDomainObject user, Vector variables)
	throws IOException, NumberFormatException {

	HttpSession session = req.getSession(true);

	int player_id = 50000;

	// If no player is chosen then let them view the default page
	if(session.getAttribute("calender_player") == null && req.getParameter("addPlayer") == null
	   && req.getParameter("usersubmit") == null )
	    return parseOverview(req, res, null, "overview_default.html", user);

	if ( session.getAttribute("calender_player") != null )
	    player_id = Integer.parseInt( "" + session.getAttribute("calender_player") );

	CalenderDbManager dbmanager = new CalenderDbManager(req);
	Calendar greger = (Calendar)session.getAttribute("calendar");

	// Check the permission status
	int meta_Id = Integer.parseInt("" + prop.getProperty("calender_meta_id"));
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
	int highestPermission = imcref.getUserHighestPermissionSet(meta_Id, user.getUserId());


	Date time = greger.getTime();
	Vector var = new Vector();
	String view = new String();
	Vector users = new Vector();
	boolean savable = true;
	Date chkdate = new Date();
	Date chkstarttime = new Date();
	Date chkendtime = new Date();
	String chkstrdate = new String();
	String chkstrstarttime = new String();
	String chkstrendtime = new String();
	StringBuffer rows = new StringBuffer();

	// Check if user should view an appointment or edit an appointment
	if (session.getAttribute("calender_view").equals("app") ||
	    session.getAttribute("calender_view").equals("edit")){

	    String appId = req.getParameter("app");
	    view = "" + session.getAttribute("calender_view");
	    if ( view.equalsIgnoreCase("edit") )
		view = "newapp";

	    // Check which appointment has been chosen
	    if (appId == null){
		return parseOverview(req, res, null, "overview_default.html", user);
	    }

	    String[] app = dbmanager.getAppointment(appId);
	    String[] player = dbmanager.getPlayerInfo(player_id);

	    SimpleDateFormat formatter;
	    // onsdag, maj 8, 2002  format
	    if (view.equalsIgnoreCase("app"))
		formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy");
	    else
		formatter = new SimpleDateFormat("yyyy-MM-dd");


	    Date app2 = new Date();
	    Date app3 = new Date();


	    // 2002-05-08 19:35:15.00 format
	    DateFormat dateform = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS") ;

	    try{
		app2 = dateform.parse(app[2]);
		app3 = dateform.parse(app[3]);
	    }
	    catch (ParseException pe){
		pe.printStackTrace();
	    }

	    greger.setTime(app2);
	    String strDate = formatter.format(greger.getTime());
	    session.setAttribute("calendar", greger);
	    //String strDate = formatter.format(time);

	    // 19:35 format
	    formatter.applyPattern("HH:mm");

	    String appStart = formatter.format( app2 );
	    String appEnd = formatter.format( app3 );

	    // Add some tags to empty or preset the input boxes
	    var.add("#MAILTO#");
	    var.add("<a href=" + '"' + "mailto:" + player[12] + '"' + ">" + player[3] + " " + player[4] + "</a>");
	    var.add("#APPTITEL#");
	    var.add( app[1] );
	    var.add("#APPPLACE#");
	    var.add( app[4] );
	    var.add("#APPDATE#");
	    var.add( strDate );
	    var.add("#APPSTART#");
	    var.add( appStart );
	    var.add("#APPEND#");
	    var.add( appEnd );
	    var.add("#APPNOTES#");
	    var.add( app[5] );
	    var.add("#APPID#");

	    if (view.equalsIgnoreCase("app")){

		var.add( "" );


		String editLink = new String();
		String delLink = new String();

		String id = dbmanager.translateId(prop, user.getUserId());
		int userPlayer = 5001;
		if ( id != null )
		    userPlayer = Integer.parseInt( id );

		if ( highestPermission == 0 || userPlayer == player_id && highestPermission <= 1){
		    editLink = "<a href=" + '"' + "CalenderManager?action=view&view=edit&app=" + appId + '"' + ">Edit</a>";
		    delLink = "<a href=" + '"' + "CalenderManager?action=view&view=del&app=" + appId + '"' + ">Delete</a>";
		}
		else{
		    editLink = "Edit";
		    delLink = "Delete";
		}

		var.add("#EDIT#");
		var.add( editLink );
		var.add("#DELETE#");
		var.add( delLink );
	    }

	    if (view.equalsIgnoreCase("newapp")){
		var.add( appId );
	    }
	    session.setAttribute("calender_view", "month");

	} else if ( session.getAttribute("calender_view").equals("del") ){ // Check if delete link was pressed

	    String appId = req.getParameter("app");

	    if ( appId.length() < 1 )
		return parseOverview(req, res, null, "overview_default.html", user);


	    dbmanager.deleteAppointment(prop, appId);
	    res.sendRedirect("CalenderManager?action=view&view=month");
	}


	// Check if Save button was pushed
	else if ( req.getParameter("Save") != null ){

	    String startTime = req.getParameter("appstart").trim();
	    String endTime = req.getParameter("append").trim();
	    String date = req.getParameter("appdate").trim();
	    String notes = req.getParameter("appnotes").trim();
	    String titel = req.getParameter("apptitel").trim();
	    String place = req.getParameter("appplace").trim();
	    String appId = req.getParameter("app_id").trim();

	    SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd");
	    SimpleDateFormat formattime = new SimpleDateFormat("HH:mm");

	    if ((startTime.length() < 1) || (date.length() < 1)){
		addTagsOnNewApp(dbmanager, var, prop, player_id, time);
		view = "newapp";
		savable = false;
	    } else {

		try {
		    chkdate = formatdate.parse(date);
		    chkstarttime = formattime.parse(startTime);
		    chkendtime = formattime.parse(endTime);
		    chkstrdate = formatdate.format(chkdate);
		    chkstrstarttime = formattime.format(chkstarttime);
		    chkstrendtime = formattime.format(chkendtime);

		} catch(Exception e){
		    savable = false;
		    addTagsOnNewApp(dbmanager, var, prop, player_id, time);
		    view = "newapp";

		}

		if (savable){

		    String dateTime1 = chkstrdate + " " + chkstrstarttime + ":00.000";
		    String dateTime2 = chkstrdate + " " + chkstrendtime + ":00.000";


		    if ( appId.length() > 0 )
			dbmanager.deleteAppointment(prop, appId);
		    // Write to database
		    String app_id = dbmanager.newAppointment(prop, dateTime1, dateTime2, place, notes, titel, session);
		    res.sendRedirect("CalenderManager?action=view&view=app&app=" + app_id);
		}
	    }

	} else if (session.getAttribute("calender_view").equals("NewApp")){	// Check if user should view the new appointment window


	    if ( req.getParameter("Save") == null ){

		addTagsOnNewApp(dbmanager, var, prop, player_id, time);
		view = "newapp";

	    }


	} else if ( session.getAttribute("calender_view").equals("admin") ){  // Check if any button in the admin panel was pressed

	    if ( req.getParameter("addPlayer") != null ){

		String[] calUsers = dbmanager.getCalenderUsers(prop);
		String[] result = dbmanager.getAllUsers();

		// Remove all users that are already in the calender
		boolean flag = false;
		for ( int i = 0; i < result.length; i = i + 5 ){
		    flag = false;
		    for ( int j = 0; j < calUsers.length; j = j + 20 ){
			if ( calUsers[j].equals(result[i]) ){
			    flag = true;
			}
		    }
		    if (!flag){
			for ( int h = 0; h < 5; h++)
			    users.add( result[ i + h] );
		    }

		}


		var.add("#HEADER#");
		var.add("Välj användare du vill lägga till");
		var.add("#PLAYERACTION#");
		var.add("add");
		view = "listplayers";

	    } else if ( req.getParameter("removePlayer") != null ){

		String[] usersAll = dbmanager.getCalenderUsers(prop);

		for ( int i = 0; i < usersAll.length; i = i + 20 ){

		    users.add(usersAll[i]);
		    users.add(usersAll[i + 3]);
		    users.add(usersAll[i + 4]);
		    users.add(usersAll[i + 5]);
		    users.add(usersAll[i + 12]);

		}

		var.add("#HEADER#");
		var.add("Välj användare du vill ta bort");
		var.add("#PLAYERACTION#");
		var.add("del");
		view = "listplayers";
	    } else if ( req.getParameter("editTitle") != null ){

		view = "title";
		String titel = dbmanager.getTitel(prop);

		var.add("#TITLE#");
		if (titel.length() < 1)
		    var.add("");
		else
		    var.add(titel);
	    }

	    if ( view.equalsIgnoreCase("listplayers")){
		var.add("#USER#");

		for ( int i = 0; i < users.size(); i = i + 5 ){

		    rows.append("<tr><td><input type=" + '"' + "radio" + '"' +
				" name=" + '"' + "userId" + '"' + " value=" +
				'"' + users.elementAt(i) + '"' + " checked></td>");
		    rows.append("<td>" + users.elementAt(i) + "</td>");
		    rows.append("<td>" + users.elementAt(i+3) + "</td>");
		    rows.append("<td>" + users.elementAt(i+1) + "</td>");
		    rows.append("<td>" + users.elementAt(i+2) + "</td>");
		    rows.append("<td>" + users.elementAt(i+4) + "</td>");
		}

		var.add( rows.toString() );
	    }

	} else if ( req.getParameter("usersubmit") != null ) { // Check if button for choosing users was pushed

	    String userId = req.getParameter("userId");
	    String action = req.getParameter("player_action");

	    if ( action.equalsIgnoreCase("add") ){
		dbmanager.addPlayer(prop, userId);
	    }
	    else if ( action.equalsIgnoreCase("del") ){
		dbmanager.deletePlayer(prop, userId);
		session.removeAttribute("calender_player");
	    }

	    res.sendRedirect("CalenderManager?action=view&view=month");
	}
	// Check if edit title button was pressed
	else if ( req.getParameter("title_save") != null ){

	    String title = req.getParameter("txt_title");
	    dbmanager.newTitle(prop, title);

	    res.sendRedirect("CalenderManager?action=view&view=month");
	} else {

	    if ( session.getAttribute("calender_player") == null )
		return parseOverview(req, res, null, "overview_default.html", user);
	    // Check if we should view day or month
	    view = (session.getAttribute("calender_view").equals("day") )? "day":"month";


	    // Month specific tags
	    if (view.equalsIgnoreCase("month")){
		// Copy the almanac from the menu bar
		var = (Vector)variables.clone();

		// Add the month
		SimpleDateFormat formatter = new SimpleDateFormat ("MMMM");
		String strMonth = formatter.format(time);
		var.add("#MONTH#");
		var.add(strMonth);

		// Add year
		var.add("#YEAR#");
		var.add("" + greger.get(Calendar.YEAR));

		// Add appointments
		String meta_id = prop.getProperty("calender_meta_id");
		String[] apps = dbmanager.getMyAppointments(meta_id,
							    "" + player_id, "" + greger.get(Calendar.YEAR),"" + (greger.get(Calendar.MONTH)+1));

		addAppointmentsToMonth(var, session, apps);

	    } else if (view.equalsIgnoreCase("day")){
		// Add date
		SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy");
		String strDate = formatter.format(time);
		var.add("#DATE#");
		var.add(strDate);
	    }

	    // Add the players name
	    String info[] = dbmanager.getPlayerInfo(player_id);
	    if (info.length > 1){
		var.add("#PLAYER#");
		var.add(info[3] + " " + info[4]);
	    }

	    // Add the titel
	    var.add("#TITEL#");
	    String titel = dbmanager.getTitel(prop);
	    if (titel == null || titel.equals(""))
		var.add("&nbsp;");
	    else{
		var.add( titel );
	    }

	    String meta_id = prop.getProperty("calender_meta_id");
	    String[] dayApps = dbmanager.getMyAppointments(meta_id, "" + player_id, session);

	    addAppointmentsToDay(var, session, dayApps);

	}

	// Choose the correct template
	String template_name = "overview_" + view + ".html";

	return parseOverview(req, res, var, template_name, user);


    }




    /**
       Adds player information, date and time to a new appointment to be viewed.
       @param dbmanager database object handling all database transactions
       @param var vector where all tags are stored
       @param prop system properties
       @param player_id active players id
       @param time chosen date and time
       @throws IOException if detected when handling request

    */
    private void addTagsOnNewApp(CalenderDbManager dbmanager, Vector var, Properties prop, int player_id, Date time)
	throws IOException{

	String[] player = dbmanager.getPlayerInfo(player_id);
	SimpleDateFormat exformat = new SimpleDateFormat("yyyy-MM-dd");
	String date = exformat.format(time);
	exformat.applyPattern("HH:mm");
	String clock = exformat.format(time);

	var.add("#MAILTO#");
	var.add("<a href=" + '"' + "mailto:" + player[12] + '"' + ">" + player[3] + " " + player[4] + "</a>");
	var.add("#APPTITEL#");
	var.add("");
	var.add("#APPPLACE#");
	var.add("");
	var.add("#APPDATE#");
	var.add(date);
	var.add("#APPSTART#");
	var.add(clock);
	var.add("#APPEND#");
	var.add(clock);
	var.add("#APPNOTES#");
	var.add("");

    }

    /**
       Adds the appointments to the month overview.
       @param var vector with the tags to be parsed
       @param session containing the relevent Calendar
       @param apps[] array with the appointments to add

    */
    private void addAppointmentsToMonth(Vector var, HttpSession session, String[] apps){

	Calendar greger = (Calendar)session.getAttribute("calendar");
	int oldDay = greger.get(Calendar.DAY_OF_MONTH);
	int month = greger.get(Calendar.MONTH);
	greger.set(Calendar.DAY_OF_MONTH, 1);
	Date appDate = new Date();
	Calendar time = Calendar.getInstance();
	DateFormat dateform = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat timeform = new SimpleDateFormat("HH:mm");

	//Correct the weekday because swedish week starts with monday, not sunday.
	int weekday = greger.get(Calendar.DAY_OF_WEEK) - 1;
	if (weekday == 0)
	    weekday = 7;

	int week = greger.get(Calendar.WEEK_OF_YEAR);
	int count = Calendar.SUNDAY;
	int day;


	// Indent first line of almanac
	for (int i = count; i < weekday; i++){
	    var.add("#a" + i + "#");
	    var.add("&nbsp;");
	    count++;;
	}

	do{
	    day = greger.get(Calendar.DAY_OF_MONTH);

	    var.add("#a" + count + "#");
	    boolean flag = false;
	    StringBuffer links = new StringBuffer();
	    // Check if the day has an appointment
	    for (int i = 2; i<apps.length; i = i + 7){

		time.setTime(makeCalendar(apps[i]));

		if (dateform.format(greger.getTime()).equals(dateform.format(time.getTime())) ){
		    links.append("<a href=" + '"' + "CalenderManager?action=view&view=app&app=" +
				 apps[i-2] + '"' + ">" + timeform.format(time.getTime()) + " " + apps[i-1] + "</a><br>");

		    flag = true;
		}
	    }
	    if (!flag)
		var.add("&nbsp;");
	    else
		var.add("" + links);

	    greger.add(Calendar.DAY_OF_MONTH, 1);
	    count++;

	} while (greger.get(Calendar.MONTH) == month);

	greger.add(Calendar.MONTH, -1);

	for (int i = count; i<=42; i++){
	    var.add("#a" + i + "#");
	    var.add("&nbsp;");

	}

	greger.set(Calendar.DAY_OF_MONTH, oldDay);


    }

    /**
       Adds the appointments to the day overview.
       @param var vector with the tags and information
       @param session containing the Calendar with the relevent date and time
       @param apps[] array with the appointments to add
    */
    private void addAppointmentsToDay(Vector var, HttpSession session, String[] apps){

	StringBuffer strHtml = new StringBuffer();
	DateFormat timeform = new SimpleDateFormat("HH:mm");
	Calendar time = Calendar.getInstance();

	var.add("#APPOINTMENTS#");

	if (apps.length != 0){

	    for (int i = 0; i<apps.length; i = i + 7){

		time.setTime(makeCalendar(apps[i+2]));

		strHtml.append("<tr><td width=" + '"' + "33" + '"' + " nowrap>&nbsp;</td>");
		strHtml.append("<td width=" + '"' + "87" + '"' + " nowrap align=" + '"' + "left" + '"' + ">");
		strHtml.append(timeform.format(time.getTime()) + " - " + "</td>");
		strHtml.append("<td width=" + '"' + "104" + '"' + " nowrap align=" + '"' + "left" + '"' + ">");
		time.setTime(makeCalendar(apps[i+3]));

		strHtml.append(timeform.format(time.getTime()) + "</td>");
		strHtml.append("<td width=" + '"' + "797" + '"' + ">");
		strHtml.append("<a href=" + '"' + "CalenderManager?action=view&view=app&app=");
		strHtml.append(apps[i] + '"' + ">" + apps[i+1] + "</a></td></tr>");

	    }
	    var.add(strHtml.toString());
	}
	else
	    var.add("&nbsp;");

    }

    /**
       Parses the tags with the templates.
       @param req servlet request object
       @param res servlet response object
       @param variables tags and information
       @param template_name name of the template to be parsed
       @param user user properties
       @return html ready for viewing
       @throws IOException if detected when handling the request
    */
    private String parseOverview(HttpServletRequest req, HttpServletResponse res,
				 java.util.Vector variables, String template_name,
				 imcode.server.user.UserDomainObject user) throws IOException
    {
	String lang_prefix	= user.getLangPrefix();
	String host		= req.getHeader("Host");
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

	return imcref.parseExternalDoc(variables, template_name, lang_prefix, "107");
    }

    /**
       Formats dates.
       @param strDate date to be formated
       @return formatted date
    */
    private Date makeCalendar(String strDate){

	Date appDate = new Date();
	Calendar time = Calendar.getInstance();
	DateFormat longform = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS") ;

	try{
	    appDate = longform.parse(strDate);
	}catch(ParseException pe){
	    pe.printStackTrace();
	}

	return appDate;
    }
}
