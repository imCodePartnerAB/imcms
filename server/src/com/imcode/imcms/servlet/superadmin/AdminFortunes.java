package com.imcode.imcms.servlet.superadmin;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.*;
import imcode.server.*;
import imcode.server.user.UserDomainObject;
import com.imcode.imcms.servlet.superadmin.Administrator;


public class AdminFortunes extends Administrator {

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Lets get the server this request was aimed for
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // Lets verify that the user who tries to admin a fortune is an admin
        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (imcref.checkAdminRights(user) == false) {
            String header = "Error in AdminFortunes.";
            Properties langproperties = imcref.getLangProperties( user );
            String msg = langproperties.getProperty("error/servlet/global/no_administrator")+"<br>";
            this.log(header + msg);
            new AdminError(req, res, header, msg);
            return;
        }

        //get fortunefiles

        File fortune_path = Utility.getDomainPrefPath("FortunePath");
        File[] files = fortune_path.listFiles();

        String options = "<option value=\"No_Choice\" selected>-- V&auml;lj Fil --</option>";

        for (int i = 0; i < files.length; i++) {
            //remove suffixes and create optionstring
            String totalFile = files[i].toString();
            int index = totalFile.lastIndexOf("\\");
            String filename = totalFile.substring(index + 1);
            index = filename.lastIndexOf(".");
            String name = filename.substring(0, index);
            if ((!name.endsWith("current")) && (!name.endsWith("statistics"))) {
                options = options + "<option value=\"" + name + "\" > " + name + "</option>";
            }
        }

        //Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
        Vector values = new Vector();
        values.add("#options#");
        values.add(options);
        values.add("#SERVLET_URL#");
        values.add("");


        String parsed = imcref.getAdminTemplate("AdminFortunes.htm", user, values);
        out.print(parsed);


    } // End doGet

    /**
     * doPost
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Lets get the parameters and validate them, we dont have any own
        // parameters so were just validate the metadata
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Lets get the server this request was aimed for
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        HttpSession session = req.getSession();

        Map lines = Collections.synchronizedMap(new TreeMap());
        Map results = Collections.synchronizedMap(new TreeMap());


        String whichFile = req.getParameter("AdminFile");


        if (req.getParameter("back") != null) {
            res.sendRedirect("AdminManager");
            return;
        }

        if (whichFile.equals("No_Choice")) {
            //rita om sidan  utan att ngt händer
            res.sendRedirect("AdminFortunes");
            return;
        }

        session.setAttribute("file", whichFile);
        UserDomainObject user = Utility.getLoggedOnUser( req );

        if (req.getParameter("result") != null) {
            String options = "";


            File fortune_path = Utility.getDomainPrefPath("FortunePath");
            File file = new File(fortune_path, whichFile + "statistics.txt");
            if (file.exists()) {	//öppna filen med detta namnet, om det finns en statisticsfil, annars skriv felmeddelande	//öppna filen med detta namnet, om det finns en statisticsfil, annars skriv felmeddelande
                String openFile = imcref.getFortune(whichFile + "statistics.txt");
                BufferedReader readFile = new BufferedReader(new StringReader(openFile));

                String line = readFile.readLine();
                int row = 0;
                while (line != null && !(line.length() <= 12)) {
                    String fullLine = line.replace('#', ' ');
                    options = options + "<option value=\"" + row + "\" > " + fullLine + "</option>";
                    results.put(new Integer(row), fullLine);
                    //	originalLines.put( new Integer(row) , fullLine );
                    line = readFile.readLine();
                    row++;
                }


                //Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
                Vector values = new Vector();
                values.add("#options#");
                values.add(options);


                String parsed = imcref.getAdminTemplate("ShowQuestions.htm", user, values);
                out.print(parsed);

                session.setAttribute("results", results);

                return;
            } else {
                res.sendRedirect("AdminFortunes");
                return;
            }

        }

        if (req.getParameter("edit") != null) {
            String options = "<option value=\"No_Choice\" selected>-- V&auml;lj Rad --</option>";

            //öppna filen med detta namnet
            String openFile = imcref.getFortune(whichFile + ".txt");
            BufferedReader readFile = new BufferedReader(new StringReader(openFile));

            String line = readFile.readLine();
            int row = 0;
            while (line != null && !(line.length() <= 12)) {
                String fullLine = line.replace('#', ' ');
                int stop = fullLine.length();

                if (fullLine.indexOf("<BR>") != -1) {
                    stop = fullLine.indexOf("<BR>");
                }
                if (fullLine.indexOf("<br>") != -1) {
                    stop = fullLine.indexOf("<br>");
                }

                options = options + "<option value=\"" + row + "\" > " + fullLine.substring(0, stop) + "</option>";
                lines.put(new Integer(row), fullLine);
                line = readFile.readLine();
                row++;
            }

            String date1 = " ";
            String date2 = " ";
            String text = " ";

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
            values.add(options);


            String parsed = imcref.getAdminTemplate("AdminFortuneFile.htm", user, values);
            out.print(parsed);

            session.setAttribute("lines", lines);

            return;
        }


    }

} // End of class
