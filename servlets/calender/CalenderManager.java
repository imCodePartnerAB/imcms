import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.text.*;
import imcode.util.* ;


/*

Session variables:
	calender_meta_id	the calenders meta_id
	calender_view		view day or month
	calendar			a Calendar set to the day/month/year to be viewed
	calender_player		person to view
	calender_app		appointment to view
	
GET variables:
	action				new, view
	view				month, day, app, newapp
	month				+1, -1
	day					1-31
	app					0-infinity

*/


/**
	The servlet handling all requests from the users for the calender plugin.
	Processes the toolbar itself and lets CalenderOverview take care of the overview part.
	@author Robert Engzell
*/
public class CalenderManager extends Calender
{
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
	/**
		Sends all requests on to the doPost method.
		@param req servlet request object
		@param res servlet response object
		@throws ServletException if the request could not be handled
		@throws IOException if detected when handling the request
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse res)
						throws ServletException, IOException
	{
		doPost(req, res);	
	}
	
	/**
		Handles all requests made to the servlet.
		Parses and prepares the templates before sending to the user.
		@param req servlet request object
		@param res servlet response object
		@throws ServletException if the request could not be handled
		@throws IOException if detected when handling the request
	*/
	public void doPost(HttpServletRequest req, HttpServletResponse res)
						throws ServletException, IOException {
		// Get info and check login
		HttpSession session = req.getSession(true);

		Properties prop = super.getSessionParams(req, res);
		imcode.server.User user = super.checkLogin(req, res, prop.getProperty("start_url"));
		
		if (user == null)
			return;

		
		String action = req.getParameter("action");
/********************* NEW ******************************************************************/
		if (action.equalsIgnoreCase("NEW"))
		{
			res.sendRedirect("CalenderCreator?action=NEW");
			return;
		}
/********************* VIEW *****************************************************************/
		else if (action.equalsIgnoreCase("VIEW")){
		
			Calendar greger;
			handleViews(req, session);
			handleDate(req, session);

			
			// Make sure we have a calendar in the session
			if (session.getAttribute("calendar") == null){
				greger = Calendar.getInstance();
				session.setAttribute("calendar", greger);
			}
			else
				greger = (Calendar)session.getAttribute("calendar");
				
			// Check if we should change month
			if (req.getParameter("month") != null) {
				greger.add(Calendar.MONTH, Integer.parseInt(req.getParameter("month")));
				int month = greger.get(Calendar.MONTH);
				session.setAttribute("calendar", greger);
			}

			// Get the path to the images
			imcode.server.IMCServiceInterface imc = IMCServiceRMI.getInterface(prop.getProperty("imcserver"));
			String imagePath = imc.getImageHome() + user.getLangPrefix() + "/107";
			CalenderDbManager dbmanager = new CalenderDbManager();
			int player_id;
			
			// Check the permission status
			int meta_id = Integer.parseInt("" + prop.getProperty("calender_meta_id"));
			String server = "" + prop.getProperty("imcserver");
			int highestPermission = IMCServiceRMI.getUserHighestPermissionSet(server, meta_id, user.getUserId());

			if ( highestPermission >= 3 ){
				res.sendRedirect("GetDoc?meta_id=1001");
				return ;
			}
			
			// Retreive the literal month
			SimpleDateFormat formatter = new SimpleDateFormat ("MMMM");
			Date time = greger.getTime();
			String strMonth = formatter.format(time);
			String disabled = new String();
			
			// prepare overview for parsing
			String players_menu = req.getParameter("players_menu");
			if (players_menu != null)
				session.setAttribute("calender_player", players_menu);

							
			CalenderOverview calOverview = new CalenderOverview();
			
			// Prepare main menu for parsning
			Vector variables = miniAlmanac(session);
			String select = selectBox(prop);
			String overview = new String();
			
			// parses the overview part
			overview = calOverview.makeOverview(req, res, prop, user, variables);

			
			variables.add("#MONTHVIEW#");
			variables.add("CalenderManager?action=view&view=month");
			variables.add("#DAYVIEW#");
			variables.add("CalenderManager?action=view&view=day");
			variables.add("#MONTH#");
			variables.add(strMonth);
			variables.add("#YEAR#");
			variables.add("" + greger.get(Calendar.YEAR));
			variables.add("#BACKPIC#");
			variables.add(imagePath + "/back.gif");
			variables.add("#BACKLINK#");
			variables.add("CalenderManager?action=view&month=-1");
			variables.add("#NEXTPIC#");
			variables.add(imagePath + "/next.gif");
			variables.add("#NEXTLINK#");
			variables.add("CalenderManager?action=view&month=1");
			variables.add("#NEW_APPOINTMENT#");

			// Check if the user is viewing their own calendar or have full rights			
			String id = dbmanager.translateId(prop, user.getUserId());
			if ( id == null)
				id = "50001";
			
			if ( session.getAttribute("calender_player") == null )
				disabled = "disabled";
			else if ( session.getAttribute("calender_player") != null && highestPermission == 0 )
				disabled = "";	
			else{
				player_id = Integer.parseInt( "" + session.getAttribute("calender_player"));
				int userPlayer = Integer.parseInt (id);
				disabled = ( (player_id == userPlayer && highestPermission <= 1) || highestPermission == 0) ? "" : "disabled";

			}
		
			
			variables.add("<input type=" + '"' + "submit" + '"' + "name=" + '"' +
							"newapp" + '"' + " value=" + '"' + "New Appointment" + '"' + disabled +
							"><input type=" + '"' + "hidden" +
							'"' + " name=" + '"' + "action" + '"' + " value=" + '"' + "view" + '"' + "><input type=" + '"' +
							"hidden" + '"' + " name=" + '"' + "view" + '"' + " value=" + '"' + "NewApp" + '"' + ">"); 
			variables.add("#SIZE#");
			variables.add( selectBoxSize(prop) );
		
			variables.add("#SELECTBOX#");
			variables.add(select);
						 
				 
			String menu = parseCalender(req, res, variables, "main_menu.html", user);
			String admin = parseCalender(req, res, null, "adminpanel.html", user);
			
			// Parse the menu and overview into a 2 cell template
			Vector var = new Vector();
			var.add("#MENU#");
			var.add(menu);
			var.add("#OVERVIEW#");
			var.add(overview);
			
			// Check if user is an administrator			
			var.add("#ADMIN#");
			
			if (highestPermission == 0)
				var.add(admin);
			else if (highestPermission > 0)
				var.add("&nbsp");
			
			String all = parseCalender(req, res, var, "main_table.html", user);
			sendCalender(res, all);
			
			return;

		}
		
	}
	
	/**
		Makes sure that the day of the month exists this month.
		Since all months don't have the same nr of days in them.
		@param req servlet request object
		@param session object containing Calendar set to correct date
	*/
	private void handleDate(HttpServletRequest req, HttpSession session){
	
		Calendar greger = (Calendar)session.getAttribute("calendar");
		
		String inDay = req.getParameter("day");
		
		if (inDay != null){
			int day = Integer.parseInt(inDay);
			if ( day <= greger.getActualMaximum(Calendar.DAY_OF_MONTH) ){
				greger.set(Calendar.DAY_OF_MONTH, day);
				session.setAttribute("calendar", greger);
			}
		}
	}
	
	/**
		Sets the request view in the session object.
		Default is month.
		@param req servlet request object
		@param session object containing the view setting
	*/
	private void handleViews(HttpServletRequest req, HttpSession session){
	
		String view = req.getParameter("view");
		
		if (view != null)
			session.setAttribute("calender_view", view);
		else if (session.getAttribute("calender_view") == null)
			session.setAttribute("calender_view", "month");		
	}
	
	/**
		Constructs the tags for an almanac grid.
		Both the small almanac in the menu bar and the month overview uses this method.
		@param session object containing the Calendar set to the correct date
		@return vector with the tags and information for parsing later on
	*/
	private Vector miniAlmanac(HttpSession session){
		Vector var = new Vector();
		Calendar greger = (Calendar)session.getAttribute("calendar");
		int oldDay = greger.get(Calendar.DAY_OF_MONTH);
		int month = greger.get(Calendar.MONTH);
		greger.set(Calendar.DAY_OF_MONTH, 1);
		
		//Correct the weekday because swedish week starts with monday, not sunday.
		int weekday = greger.get(Calendar.DAY_OF_WEEK) - 1;
		if (weekday == 0)
			weekday = 7;
		
		int week = greger.get(Calendar.WEEK_OF_YEAR);	
		int count = Calendar.SUNDAY;
		int day, tempweek;
			
		// First week number
		var.add("#w1#");
		var.add("" + week);
		
		// Indent first line of almanac
		for (int i = count; i < weekday; i++){
			var.add("#" + i + "#");
			var.add("&nbsp;");
			count++;;
		}
		
		do{
		day = greger.get(Calendar.DAY_OF_MONTH);
		tempweek = greger.get(Calendar.WEEK_OF_YEAR);
		if (tempweek != week){
				var.add("#w" + ((count-1)/7 + 1) + "#");
				var.add("" + tempweek);
				week = tempweek;
		}
		var.add("#" + count + "#");
		var.add("<a href=" + '"' + "CalenderManager?action=view&view=day&day=" + day + '"' + ">" + day + "</a>");
		
		greger.add(Calendar.DAY_OF_MONTH, 1);
		count++;
		
		} while (greger.get(Calendar.MONTH) == month);
		
		// Fill in the last row where no values where set
		if (((count-1)/7) != 6){
			var.add("#w6#");
			var.add("&nbsp");
		} 
		
		greger.add(Calendar.MONTH, -1);
	
		for (int i = count; i<=42; i++){
			var.add("#" + i + "#");
			var.add("&nbsp;");
			
		}
		
		greger.set(Calendar.DAY_OF_MONTH, oldDay);
		return var;
	}
		
	/**
		Adds players to the selectbox in the menu bar.
		@param prop system properties
		@return html for the selectbox content
		@throws IOException if detected when handling the request
	*/
	private String selectBox(Properties prop) throws IOException{

		CalenderDbManager dbManager = new CalenderDbManager();
		String[] info = dbManager.getPlayers(prop);
		
		
		StringBuffer strHtml = new StringBuffer();
		if (info.length != 0){
			strHtml.append("<option value=" + '"');
			strHtml.append(info[0] + '"' + " selected>");
			strHtml.append(info[1] + "</option>");
		}
		
		for (int i = 2; i<info.length; i = i + 2){
				strHtml.append("<option value=" + '"' + info[i] + '"' + ">");
				strHtml.append(info[i + 1] + "</option>");
		}

		return "" + strHtml;
	}
	
	/**
		Retrieves the correct size for the selectbox.
		@param prop system properties
		@return html string for the size tag in the selectbox
		@throws IOException if detected when handling the request
	*/
	private String selectBoxSize(Properties prop) throws IOException{
	
		CalenderDbManager dbManager = new CalenderDbManager();
		String[] info = dbManager.getPlayers(prop);
		
		return " size=" + '"' + info.length/2 + '"';
	}
}










