package com.imcode.imcms.servlet.superadmin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.ImcmsConstants;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.Utility;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import imcode.util.jscalendar.JSCalendar;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class AdminCounter extends HttpServlet {

    private final static Logger mainLog = Logger.getLogger( ImcmsConstants.MAIN_LOG );
    private final static String JSP_TEMPLATE = "admin_session_counter.jsp";


    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        doPost(req,res);

    } // End doGet


    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.isSuperAdmin() ) {
            Utility.forwardToLogin( req, res );
            return;
        }


        // ***** SET COUNTER *****
        if ( req.getParameter("setSessionCounter") != null ) {
            String userVal = (req.getParameter("counter_value") == null) ? "" : (req.getParameter("counter_value"));
            int theUserInt = 0;
            String oldCounterValue = "" + imcref.getSessionCounter();

            boolean ok = true;
            try {
                if ( userVal.equals("") ) {
                    ok = false;
                }
                theUserInt = Integer.parseInt(userVal);

            } catch ( Exception e ) {
                ok = false;
            }

            if ( ok ) {
                imcref.setSessionCounter(theUserInt);
                mainLog.info("Session counter value updated by user: " + user.getLoginName() + ", new value = " + theUserInt + ", old value = " + oldCounterValue );
            }

        }

        // ***** SET COUNTER DATE *****

        String errormsg = "";
        DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATE_FORMAT_STRING);
        Date currentDate = imcref.getSessionCounterDate();
        String newDateStr = dateFormat.format(currentDate);

        if ( req.getParameter("setDate") != null ) {

            String dateStr = req.getParameter("date_value");

            try {
                Date date = dateFormat.parse(dateStr);
                newDateStr = dateFormat.format(date);
                if ( !newDateStr.equals(dateStr) ) {
                    Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                    errormsg = langproperties.getProperty("error/servlet/AdminCounter/no_valid_date");
                    newDateStr = dateStr;
                } else {
                    imcref.setSessionCounterDate(date);
                    mainLog.info("Session counter startdate value updated by user: " + user.getLoginName() + ", new date = " + newDateStr + ", old date = " + dateFormat.format(currentDate));
                }

            } catch ( ParseException pe ) {
                Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                errormsg = langproperties.getProperty("error/servlet/AdminCounter/no_valid_date");
                newDateStr = dateStr;
            }
        }

        String counterValue = "" + imcref.getSessionCounter();

        AdminSessionCounterPage adminSessionCounterPage = new AdminSessionCounterPage();
        adminSessionCounterPage.setCounterValue(Integer.parseInt(counterValue));
        adminSessionCounterPage.setNewDateStr(newDateStr);
        adminSessionCounterPage.setErrormsg(errormsg);

        adminSessionCounterPage.forward( req, res, user );

    }


    public static class AdminSessionCounterPage implements Serializable {

        public static final String REQUEST_ATTRIBUTE__PAGE = "sessioncounterpage";
        public static final String REQUEST_PARAMETER__COUNTER_VALUE= "counter_value";
        public static final String REQUEST_PARAMETER__DATE_VALUE= "date_value";

        private int counterValue ;
        private String newDateStr ;
        private String errormsg;


        public void forward( HttpServletRequest request, HttpServletResponse response, UserDomainObject user ) throws IOException, ServletException {
            putInRequest( request );
            String forwardPath = "/imcms/" + user.getLanguageIso639_2() + "/jsp/" + JSP_TEMPLATE;
            request.getRequestDispatcher( forwardPath ).forward( request, response );
        }

        public void putInRequest( HttpServletRequest request ) {
            request.setAttribute( REQUEST_ATTRIBUTE__PAGE, this );
        }

        public int getCounterValue() {
            return counterValue;
        }

        public void setCounterValue(int counterValue) {
            this.counterValue = counterValue;
        }

        public String getNewDateStr() {
            return newDateStr;
        }

        public void setNewDateStr(String newDateStr) {
            this.newDateStr = newDateStr;
        }

        public void setErrormsg(String errormsg) {
            this.errormsg = errormsg;
        }

        public String getErrormsg() {
            return errormsg;
        }

        public JSCalendar getJSCalendar(HttpServletRequest request) {
            return new JSCalendar( Utility.getLoggedOnUser(request).getLanguageIso639_2(), request ) ;
        }
     }

}
