package com.imcode.imcms.servlet.superadmin;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;

import imcode.server.*;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import com.imcode.imcms.servlet.superadmin.Administrator;


public class AdminCounter extends Administrator {

    private final static String HTML_TEMPLATE = "AdminCounter.htm";

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (imcref.checkAdminRights(user) == false) {
            String header = "Error in AdminCounter.";
            Properties langproperties = imcref.getLangProperties( user );
            String msg = langproperties.getProperty("error/servlet/global/no_administrator")+ "<BR>";
            this.log(header + "- user is not an administrator");
            new AdminError(req, res, header, msg);
            return;
        }

        // Lets get the countervalue
        String counterValue = "" + imcref.getCounter();

        // Lets get the servers startdate
        String startDate = imcref.getCounterDate();

        // Lets generate the html page
        VariableManager vm = new VariableManager();
        //  vm.addProperty("STATUS","..." ) ;
        vm.addProperty("COUNTER_VALUE", counterValue);
        vm.addProperty("DATE_VALUE", startDate);
        this.sendHtml(req, res, vm, HTML_TEMPLATE);

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
            this.doGet(req, res);
            return;
        }

        // ***** SET COUNTER DATE *****
        if (req.getParameter("setDate") != null) {
            // Lets get the parameter and validate it
            String date = (req.getParameter("date_value") == null) ? "" : (req.getParameter("date_value"));
            imcref.setCounterDate(date);
            this.doGet(req, res);
            return;
        }

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
