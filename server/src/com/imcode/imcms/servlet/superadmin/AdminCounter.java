package com.imcode.imcms.servlet.superadmin;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormat;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;

import imcode.server.*;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.DateConstants;



public class AdminCounter extends Administrator {

    private final static String HTML_TEMPLATE = "AdminCounter.htm";


    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        doPost(req,res);

    } // End doGet


    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Lets validate the session
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if (imcref.checkAdminRights(user) == false) {
            String header = "Error in AdminCounter.";
            Properties langproperties = imcref.getLangProperties( user );
            String msg = langproperties.getProperty("error/servlet/global/no_administrator")+ "<BR>";
            this.log(header + "- user is not an administrator");
            new AdminError(req, res, header, msg);
            return;
        }

        // ***** RETURN TO ADMIN MANAGER *****
        if (req.getParameter("CancelCounter") != null) {
            res.sendRedirect("AdminManager");
            return;
        }

        // ***** SET COUNTER *****
        if (req.getParameter("setCounter") != null) {
            // Lets get the parameter and validate it
            Properties props = this.getParameters(req);
            String userVal = props.getProperty("COUNTER_VALUE");
            //this.log("The user values was: " + userVal) ;
            int theUserInt = 0;

            boolean ok = true;
            try {
                if (userVal.equals("")) {
                    ok = false;
                }
                theUserInt = Integer.parseInt(userVal);

            } catch (Exception e) {
                ok = false;
            }

            if (ok) imcref.setCounter(theUserInt);

        }

        // ***** SET COUNTER DATE *****

        // Lets get the servers startdate
        String errormsg = "";
        Date currentDate = imcref.getCounterDate();
        DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATE_FORMAT_STRING);
        String newDateStr = dateFormat.format(currentDate);

        if (req.getParameter("setDate") != null) {
            // Lets get the parameter and validate it

            String dateStr = req.getParameter("date_value");

            try{
                Date date= dateFormat.parse(dateStr);
                newDateStr = dateFormat.format(date);
                if ( !newDateStr.equals(dateStr) ){
                    Properties langproperties = imcref.getLangProperties( user );
                    errormsg = langproperties.getProperty("error/servlet/AdminCounter/no_valid_date");
                    newDateStr = dateStr;
                }else{
                    imcref.setCounterDate(date);
                }

            }catch (ParseException pe){
                   Properties langproperties = imcref.getLangProperties( user );
                   errormsg = langproperties.getProperty("error/servlet/AdminCounter/no_valid_date");
                   newDateStr = dateStr;
            }
        }

        String counterValue = "" + imcref.getCounter();
        currentDate = imcref.getCounterDate();

        // Lets generate the html page
        VariableManager vm = new VariableManager();
        vm.addProperty("COUNTER_VALUE", counterValue);
        vm.addProperty("CURRENT_DATE_VALUE", dateFormat.format(currentDate));
        vm.addProperty("NEW_DATE_VALUE", newDateStr);
        vm.addProperty("ERRORMSG", errormsg);
        this.sendHtml(req, res, vm, HTML_TEMPLATE);

    } // End of doPost


    /**
     * Collects the parameters from the request object
     */

    private Properties getParameters(HttpServletRequest req) {

        Properties reqParams = new Properties();
        // Lets get the parameters we know we are supposed to get from the request object
        String counterVal = (req.getParameter("counter_value") == null) ? "" : (req.getParameter("counter_value"));
        reqParams.setProperty("COUNTER_VALUE", counterVal);

        return reqParams;
    }

    /**
     * Log function, will work for both servletexec and Apache
     */

    public void log(String str) {
        super.log(str);
        System.out.println("AdminCounter: " + str);
    }

} // End of class
