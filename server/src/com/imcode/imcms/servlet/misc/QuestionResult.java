package com.imcode.imcms.servlet.misc;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.fortune.Poll;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.List;
import java.util.Vector;

/**
 * @author Monika Hurtig
 * @version 1.0
 *          Date : 2001-09-05
 */

public class QuestionResult extends HttpServlet {
    private final static String RESULTTEMPLATE = "QuestionResult.htm";

    private final static Logger log = Logger.getLogger( QuestionResult.class.getName() );

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser(req);

        //get answer
        String file = req.getParameter("file");
        String answer = req.getParameter("answer");

        if (file == null) {
            file = "poll";
        }

        if (answer == null) {
            // FIXME: What to do?
            return;
        }

        List currentPollList = imcref.getPollList(file + ".current.txt");

        if (currentPollList.isEmpty()) {
            log.error("QuestionResult: No current poll!");
            return;
        }

        // So... the Poll.
        Poll thePoll = (Poll) currentPollList.get(0);

        thePoll.addAnswer(answer);

        imcref.setPollList(file + ".current.txt", currentPollList);

        double totalAnswerCount = thePoll.getTotalAnswerCount();

        double yesRatio = (double) thePoll.getAnswerCount("yes") / totalAnswerCount;
        double noRatio = (double) thePoll.getAnswerCount("no") / totalAnswerCount;

        NumberFormat pf = NumberFormat.getPercentInstance();
        pf.setMaximumFractionDigits(0);

        String yesProcent = pf.format(yesRatio);
        String noProcent = pf.format(noRatio);

        //Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
        Vector values = new Vector(8);
        values.add("#question#");
        values.add(thePoll.getQuestion());
        values.add("#yesProcent#");
        values.add(yesProcent);
        values.add("#noProcent#");
        values.add(noProcent);
        values.add("#total#");
        values.add("" + (int) totalAnswerCount);

        String parsed = imcref.getTemplateFromDirectory( RESULTTEMPLATE, user, values, "106");

        Utility.setDefaultHtmlContentType( res );
        Writer out = res.getWriter();
        out.write(parsed);

        return;

    } // End doGet

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doGet(req, res);
    }


} // End class
