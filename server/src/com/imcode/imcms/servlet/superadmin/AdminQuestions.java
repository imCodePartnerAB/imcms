package com.imcode.imcms.servlet.superadmin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.fortune.DateRange;
import imcode.util.fortune.Poll;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;


public class AdminQuestions extends Administrator implements imcode.server.ImcmsConstants {

    private final static Logger log = Logger.getLogger( AdminQuestions.class.getName() );

    private final static String ADMIN_QUESTION_FILE = "admin_questions_file.html";
    private final static String ADMIN_QUESTION = "admin_questions.html";
    private final static String QUESTION_RESULT = "show_questions.html";
    private final static String RESULT_ERR_MSG = "qustion_result_err_msg.frag";

    private final static long ONE_DAY = 86400000;

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Utility.setDefaultHtmlContentType( res );
        Writer out = res.getWriter();

        // Lets verify that the user who tries to admin a fortune is an admin
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if (user.isSuperAdmin() == false) {
            String header = "Error in AdminQuestions.";
            Properties langproperties = imcref.getLanguageProperties( user );
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            log.debug(header + "- user is not an administrator");

            new AdminError(req, res, header, msg);
            return;
        }

        //get fortunefiles

        File fortune_path = imcref.getConfig().getFortunePath();
        File[] files = fortune_path.listFiles();

        StringBuffer options = new StringBuffer();


        for (int i = 0; i < files.length; i++) {
            //remove suffixes and create optionstring
            String filename = files[i].getName();
            int index = filename.lastIndexOf(".");
            filename = filename.substring(0, index);
            if (filename.endsWith(".poll")) {
                options.append("<option value=\"" + filename.substring(0, filename.lastIndexOf(".poll")) + "\">" + filename.substring(0, filename.lastIndexOf(".poll")) + "</option>");
            }
        }


        //Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
        Vector values = new Vector();
        values.add("#options#");
        values.add(options.toString());

        String parsed = imcref.getTemplateFromDirectory( ADMIN_QUESTION, user, values, DocumentTypeDomainObject.PSEUDO_DOCTYPE_ID_FORTUNES + "");
        out.write(parsed);

    } // End doGet

    /**
     * doPost
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Lets get the parameters and validate them, we dont have any own
        // parameters so were just validate the metadata
        Utility.setDefaultHtmlContentType( res );
        PrintWriter out = res.getWriter();

        // Lets get the server this request was aimed for
        ImcmsServices imcref = Imcms.getServices();

        HttpSession session = req.getSession();

        String whichFile = req.getParameter("AdminFile");

        if (req.getParameter("back") != null) {
            res.sendRedirect("AdminManager");
            return;
        }

        if (null == whichFile || "".equals(whichFile)) {
            res.sendRedirect("AdminQuestions");
            return;
        }

        session.setAttribute("file", whichFile);

        UserDomainObject user = Utility.getLoggedOnUser(req);

        if (req.getParameter("result") != null) {

            StringBuffer buff = new StringBuffer();

            try {
                List list;
                list = imcref.getPollList(whichFile + ".stat.txt");
                Iterator iter = list.iterator();
                int counter = 0;
                SimpleDateFormat dateForm = new SimpleDateFormat("yyMMdd");
                while (iter.hasNext()) {
                    Poll poll = (Poll) iter.next();
                    DateRange dates = poll.getDateRange();
                    buff.append("<option value=\"" + counter++ + "\">" + dateForm.format(dates.getStartDate()) + " " + dateForm.format(new Date(dates.getEndDate().getTime() - ONE_DAY)) + " " + poll.getQuestion());
                    Iterator answerIter = poll.getAnswersIterator();
                    while (answerIter.hasNext()) {
                        String answer = (String) answerIter.next();
                        buff.append(" " + answer + " = ");
                        buff.append(poll.getAnswerCount(answer) + " ");
                    }

                    buff.append("</option>");
                }


                //Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
                Vector values = new Vector();
                values.add("#options#");
                values.add(buff.toString());

                String parsed = imcref.getTemplateFromDirectory( QUESTION_RESULT, user, values, DocumentTypeDomainObject.PSEUDO_DOCTYPE_ID_FORTUNES + "");
                out.print(parsed);

                session.setAttribute("results", list);
                return;
            } catch (NoSuchElementException ex) {
                StringBuffer buff2 = new StringBuffer("<option>");
                buff.append(imcref.getTemplateFromDirectory( RESULT_ERR_MSG, user, null, DocumentTypeDomainObject.PSEUDO_DOCTYPE_ID_FORTUNES + ""));
                buff2.append("</option>");
                Vector values = new Vector();
                values.add("#options#");
                values.add(buff2.toString());
                String parsed = imcref.getTemplateFromDirectory( QUESTION_RESULT, user, values, DocumentTypeDomainObject.PSEUDO_DOCTYPE_ID_FORTUNES + "");
                out.print(parsed);
                return;
            }
        } else if (req.getParameter("edit") != null) {

            StringBuffer buff = new StringBuffer();

            List lines = imcref.getPollList(whichFile + ".poll.txt");
            Iterator iter = lines.iterator();
            int counter = 0;
            SimpleDateFormat dateForm = new SimpleDateFormat("yyMMdd");
            while (iter.hasNext()) {
                Poll poll = (Poll) iter.next();
                DateRange dates = poll.getDateRange();
                buff.append("<option value=\"" + counter++ + "\">" + dateForm.format(dates.getStartDate()) + " " + dateForm.format(new Date(dates.getEndDate().getTime() - ONE_DAY)) + " " + poll.getQuestion() + "</option>");
            }


            String date1 = "";
            String date2 = "";
            String text = "";


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


            String parsed = imcref.getTemplateFromDirectory( ADMIN_QUESTION_FILE, user, values, DocumentTypeDomainObject.PSEUDO_DOCTYPE_ID_FORTUNES + "");
            out.print(parsed);

            session.setAttribute("lines", lines);

            return;
        }

    }


} // End of class
