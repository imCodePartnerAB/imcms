import java.io.*;
import java.util.*;
import java.text.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.server.* ;


/**
   Database class handling all communication with the imCMS database and the plugin database.
   @author Robert Engzell
*/
public class CalenderDbManager
{
    private IMCServiceInterface imcref ;
    private IMCPoolInterface calref ;

    public CalenderDbManager (HttpServletRequest req) throws IOException {
	imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	calref = IMCServiceRMI.getCalendarIMCPoolInterface(req) ;
    }

    /**
       Creates a new Calender in the plugin database.
       @param prop   system properties
       @param user   user properties
       @throws IOException if detected when handling the request
    */
    public void newCalender(Properties prop, imcode.server.user.User user) throws IOException
    {
	String args[] = new String[] {prop.getProperty("calender_meta_id")};
	String meta[] = sqlMainProc("GetDocumentInfo", args);
	String plArgs[] = new String[3];
	plArgs[0] = meta[0];
	plArgs[1] = meta[3];
	plArgs[2] = meta[4];
	sqlUpdatePluginProc("D_newCalender", plArgs);

	// Activate the link between the internalDocument and the calender
	imcref.activateChild(Integer.parseInt(plArgs[0]), user) ;
    }

    /**
       Creates a new appointment in the plugin database.
       @param prop system properties
       @param start start date and time in the "yyyy-MM-dd HH:mm:00.000" format
       @param end end date and time int the "yyyy-MM-dd HH:mm:00.000" format
       @param place place of appointment
       @param notes the appointments notes
       @param titel viewed in the month overview and as a header elsewhere
       @param session session object with player id and meta id
       @return appointments new id nr
       @throws IOException if detected when handling the request
    */
    public String newAppointment(Properties prop, String start, String end, String place, String notes, String titel, HttpSession session)
	throws IOException
    {
	String[] args = new String[6];
	args[0] = start;

	String[] tArgs = new String[]{ "" + session.getAttribute("calender_player"), "" + session.getAttribute("calender_meta_id") };
	String[] result = sqlPluginProc("D_getPage", tArgs);
	args[1] = result[0];
	args[2] = end;
	args[3] = place;
	args[4] = notes;
	args[5] = titel;
	String[] answer = sqlPluginProc("D_newAppointment", args);
	return answer[0];

    }

    /**
       Changes the calendars titel in the plugin database.
       @param prop system properties
       @param title string to be set as title
       @throws IOException if detected when handling the request
    */
    public void newTitle(Properties prop, String title) throws IOException{

	String[] args = new String[] {prop.getProperty("calender_meta_id"), title };

	sqlUpdatePluginProc("D_setTitle", args);
    }

    /**
       Adds a user from imCMS into the plugin database.
       @param prop system properties
       @param userId imCMS user id for the user that is to be added
       @throws IOException if detected when handling the request
    */
    public void addPlayer(Properties prop, String userId) throws IOException{

	String[] args = new String[] {userId, prop.getProperty("calender_meta_id")};

	sqlUpdatePluginProc("D_newPlayer", args);
    }

    /**
       Deletes an appointment in the plugin database.
       @param prop system properties
       @param appId id of appointment to be deleted
       @throws IOException if detected when handling the request
    */
    public void deleteAppointment(Properties prop, String appId) throws IOException {

	String[] args = new String[] { appId };
	sqlUpdatePluginProc("D_removeAppointment", args);
    }

    /**
       Deletes a player and all appointments from plugin database.
       @param prop system properties
       @param userId imCMS user id
       @throws IOException if detected when handling the request
    */
    public void deletePlayer(Properties prop, String userId) throws IOException{

	String[] args = new String[] {userId, "" + prop.getProperty("calender_meta_id")};
	String[] playerId = sqlPluginProc("D_getPlayerId", args);
	String[] argsDel = new String[] {playerId[0], prop.getProperty("calender_meta_id")};
	sqlUpdatePluginProc("D_removePlayer", argsDel);
    }

    /**
       Retrieves all users from the imCMS database.
       @return array with users id, first_name, last_name, title and email
       @throws IOException if detected when handling the request
    */
    public String[] getAllUsers() throws IOException{

	String[] users = sqlMainProc("GetAllUsers", null);
	String[] info = new String[ users.length / 4 ];
	int cnt = 0;

	// Extract the data we need
	for ( int i = 0; i < users.length; i = i + 20 ){

	    info[cnt] = users[i];
	    info[cnt + 1] = users[i + 3];
	    info[cnt + 2] = users[i + 4];
	    info[cnt + 3] = users[i + 5];
	    info[cnt + 4] = users[i + 12];
	    cnt = cnt + 5;
	}

	return info;
    }


    /**
       Gets all information about an appointment in the plugin database.
       @param prop system properties
       @param appId id for the appointment to retrieve
       @return array containing the information about the appointment
       @throws IOException if detected when handling the request
    */
    public String[] getAppointment(String appId) throws IOException{

	String[] args = new String[] { appId };
	return sqlPluginProc("D_getAppointment", args);


    }

    /**
       Gets all appointments for a player a certain year and month, from the plugin database.
       @param prop system properties
       @param meta_id calenders meta_id
       @param player_id players id
       @param year the year of the appointments
       @param month the month of the appointments to retreive
       @return array with all information about the appointments
       @throws IOException if detected when handling the request
    */
    public String[] getMyAppointments(String meta_id, String player_id,
				      String year, String month) throws IOException{

	String[] args = new String[] { year, month, player_id, meta_id };
	return sqlPluginProc("D_getMonthsAppointments", args);
    }

    /**
       Gets all appointments for a player a certain day, from the plugin database.
       @param meta_id calenders meta_id
       @param player_id players id
       @param session session object containing the calender set to the correct day
       @return array with all information about the appointments
       @throws IOException if detected when handling the request
    */
    public String[] getMyAppointments(String meta_id, String player_id, HttpSession session)
	throws IOException{

	Calendar greger = (Calendar)session.getAttribute("calendar");
	SimpleDateFormat dateform = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	String sqlDate = dateform.format(greger.getTime());

	String[] args = new String[] { sqlDate, player_id, meta_id };
	return sqlPluginProc("D_getDaysAppointments", args);
    }


    /**
       Gets all users from the imCMS database that are in the specific calender.
       @param prop system properties
       @return array with the users in the form UserId, surname, lastname
       @throws IOException if detected when handling the request

       !!!! Let this function call getCalenderUsers and disregard som info !!!!!!
    */
    public String[] getPlayers(Properties prop)throws IOException{

	String args[] = new String[] {prop.getProperty("calender_meta_id")};
	// Syntax: 0=user_id, 1=players_id ...and so on
	String user_id[] = sqlPluginProc("D_getPlayerNames", args);
	String[] user = new String[user_id.length];


	for (int i = 0; i<user_id.length; i = i + 2){
	    String temp[] = new String[] {user_id[i]};
	    String user_info[] = sqlMainProc("GetUserInfo", temp);
	    user[i] = user_id[i + 1];
	    user[i + 1] = user_info[3] + " " + user_info[4];
	}

	return user;
    }

    /**
       Translates user_id to player_id using the plugin database.
       @param prop system properties
       @param player_id players id
       @return user id matching the user id in the imCMS database
       @throws IOException if detected when handling the request
    */
    public String translateId (Properties prop, int player_id) throws IOException{

	String[] args = new String[] { "" + player_id, "" + prop.getProperty("calender_meta_id") };

	String[] result = sqlPluginProc("D_getPlayerId", args);
	if ( result.length < 1 )
	    return null;
	else
	    return result[0];
    }

    /**
       Gets all information about all users in a specific calender from the imCMS database.
       @param prop system properties
       @return array with users information after one another
       @throws IOException if detected when handling the request
    */
    public String[] getCalenderUsers(Properties prop) throws IOException{

	String args[] = new String[] {prop.getProperty("calender_meta_id")};
	// Syntax: 0=user_id, 1=players_id ...and so on
	String user_id[] = sqlPluginProc("D_getPlayerNames", args);
	String[] user = new String[(user_id.length / 2) * 20];
	int cnt = 0;

	for (int i = 0; i<user_id.length; i = i + 2){
	    String temp[] = new String[] {user_id[i]};
	    String user_info[] = sqlMainProc("GetUserInfo", temp);
	    for ( int j = 0; j < 20; j++ ){
		user[cnt] = user_info[j];
		cnt++;
	    }
	}

	return user;
    }

    /**
       Retreives user information for one user from the imCMS database.
       @param prop system properties
       @param player_id players id
       @return array with the users information
       @throws IOException if detected when handling the request
    */
    public String[] getPlayerInfo(int player_id) throws IOException{

	String args[] = new String[] { "" + player_id };
	String[] userId = sqlPluginProc("D_getPlayer", args);
	return sqlMainProc("GetUserInfo", userId);
    }

    /**
       Retreives the calender titel from the plugin database.
       @param prop system properties
       @return calenders title
       @throws IOException if detected when handling the request
    */
    public String getTitel(Properties prop) throws IOException{

	String[] args = new String[] { prop.getProperty("calender_meta_id") };
	String[] calenderMeta = sqlPluginProc("D_GetCalender", args);
	if (calenderMeta != null && calenderMeta.length >= 3) {
	    return calenderMeta[2];
	} else {
	    return "" ;
	}
    }


    /**
       Calls Stored Procedures in the plugin database that update and insert values.
       @param sProc name of the Stored Procedure
       @param args[] array with the parameters
       @throws IOException if detected when handling the request
    */
    private void sqlUpdatePluginProc(String sProc, String args[]) throws IOException
    {
	String sql = sqlConcat(sProc, args);
	calref.sqlUpdateProcedure(sql);
    }


    /**
       Calls Stored Procedures in the imCMS database that return values.
       @param sProc name of the Stored Procedure
       @param args[] array with the parameters
       @return array with the information retrieved from the called Stored Procedure
       @throws IOException if detected when handling the request
    */
    private String[] sqlMainProc(String sProc, String args[]) throws IOException
    {
	String sql;
	if ( args != null ) {
	    sql = sqlConcat(sProc, args);
	} else {
	    sql = sProc;
	}
	return imcref.sqlProcedure(sql);
    }

    /**
       Calls Stored Procedures in the plugin database that return values.
       @param sProc name of the Stored Procedure
       @param args array with the parameters
       @return array with the information retrieved from the called Stored Procedure
       @throws IOException if detected when handling the request
    */
    private String[] sqlPluginProc(String sProc, String args[]) throws IOException
    {
	String sql;
	if ( args != null )
	    sql = sqlConcat(sProc, args);
	else
	    sql = sProc;
	return calref.sqlProcedure(sql);
    }


    // Structure the sql string
    /**
       Concatenates the Stored Procedure name with its parameters.
       @param sProc name of the Stored Procedure
       @param args[] array with the parameters
       @return the methods parameters concatenated
    */
    private String sqlConcat (String sProc, String args[])
    {
	String sql = sProc.trim() + " '" + args[0].trim() + "'";
	for (int i = 1; i<args.length; i++)
	    {
		sql = sql + ", '" + args[i].trim()+ "'";
	    }
	return sql;
    }
}
