
import imcode.external.diverse.VariableManager;
import imcode.server.IMCPoolInterface;
import imcode.server.IMCServiceInterface;
import imcode.util.IMCServiceRMI;
import imcode.util.Parser;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;

/**
 * Lists chats who has debates that has requared dates. (create or modified)
 * <p/>
 * Html template in use:
 * AdminChat.html
 * AdminChat_list_tool.html
 * AdminChat_list.html
 * AdminChat_list_Chat_element.html
 * AdminChat_list_debate_element.html
 * Error.html
 * <p/>
 * Html parstags in use:
 * #META_ID#
 * #CONFERENCE_LIST#
 * #CONFERENCE#
 * #FORUM_LIST
 * #FORUM#
 * #DEBAT_LIST#
 * #DEBATE#
 * <p/>
 * stored procedures in use:
 * -
 * 
 * @author Jerker Drottenmyr
 * @version 1.02 11 Nov 2000
 */
public class ChatAdmin extends Administrator {

    private static final String TEMPLATE_CONF = "AdminChat.html";
    private static final String TEMPLATE_LIST_TOOL = "AdminChat_list_tool.html";
    private static final String TEMPLATE_LIST = "AdminChat_list.html";
    private static final String TEMPLATE_CONF_ELEMENT = "AdminChat_list_Chat_element.html";
    private static final String TEMPLATE_FORUM_ELEMENT = "AdminChat_list_forum_element.html";
    private static final String ERROR_HEADER = "AdminChat";

    //required date format
    private static final String DATE_FORMATE = "yyyy-MM-dd";

    // lets dispatches all requests to doPost()
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(request);
        String host = request.getHeader("host");
        String eMailServerMaster = Utility.getDomainPref("servermaster_email", host);

        // lets get ready for errors
        String deafultLanguagePrefix = imcref.getLanguage();

        // Lets validate the session
        if (checkSession(request, response) == false) {
            return;
        }

        // Lets get an user object
        imcode.server.User user = getUserObj(request, response);
        if (user == null) {
            sendErrorMessage(imcref, eMailServerMaster, deafultLanguagePrefix, ERROR_HEADER, 1, response);
            return;
        }

        // Lets verify that the user who tries to add a new user is an admin
        if (imcref.checkAdminRights(user) == false) {
            sendErrorMessage(imcref, eMailServerMaster, deafultLanguagePrefix, ERROR_HEADER, 2, response);
            return;
        }

        /* User has right lets do the request */
        String languagePrefix = user.getLangPrefix();
        VariableManager vm = new VariableManager();

        /* lets get which request to do */
        // generate htmlpage for listing chats
        if (request.getParameter("VIEW_CHAT_LIST_TOOL") != null) {
            sendHtml(request, response, vm, TEMPLATE_LIST_TOOL);
            // generate list off chats
        } else if (request.getParameter("VEIW_CHAT_LIST") != null) {
            listChats(request, response, languagePrefix);
            // go to AdminManager
        } else if (request.getParameter("CANCEL") != null) {
            Utility.redirect(request, response, "AdminManager");
            // go to htmlpage for listing Chats
        } else if (request.getParameter("CANCEL_CHAT_LIST") != null) {
            Utility.redirect(request, response, "ChatAdmin");
            // go to AdminChat page
        } else {
            sendHtml(request, response, vm, TEMPLATE_CONF);
        }
    }

    /**
     * check for right date form
     */
    private boolean isDateInRightFormat(String date) {

        // Format the current time.
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMATE);

        try {
            formatter.parse(date);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    /*
     *
     */
    private void listChats(HttpServletRequest request, HttpServletResponse response, String languagePrefix) throws IOException {
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(request);
        IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface(request);

        String host = request.getHeader("Host");
        String eMailServerMaster = Utility.getDomainPref("servermaster_email", host);
        boolean noErrors = true;

        /*
         * 0 = startDate to endDate
         * 1 = all
         * 2 = all upp to endDate
         * 3 = all down to startDate
         */
        //int listByDateMode = 0;

        /*
         * 0 = all date !not in use
         * 1 = create date
         * 2 = modified date
         */
        String listMode = request.getParameter("LISTMOD");
        String startDate = request.getParameter("START_DATE");
        String endDate = request.getParameter("END_DATE");

        /* lets se if any errors in requared fields or if some is missing */
        try {
            if (listMode != null) {
                int mode = Integer.parseInt(listMode);
                if (!(mode == 1 || mode == 2)) {
                    noErrors = false;
                }
            } else {
                noErrors = false;
            }
        } catch (NumberFormatException e) {
            noErrors = false;
        }

        if (startDate != null) {
            if (startDate.length() > 0) {
                if (!isDateInRightFormat(startDate)) {
                    noErrors = false;
                }
            } else {
                startDate = "0"; // Stored Procedure expects 0 then no startDate
            }
        } else {
            noErrors = false; // no startDate field submited
        }

        if (endDate != null) {
            if (endDate.length() > 0) {
                if (!isDateInRightFormat(endDate)) {
                    noErrors = false;
                }
            } else {
                endDate = "0"; // Stored Procedure expects 0 then no endDate
            }
        } else {
            noErrors = false; // no endDate field submited
        }

        // lets generate response page
        if (noErrors) {

            //lets get htmltemplate for chatrow
            String htmlChatElement = imcref.parseDoc(null, TEMPLATE_CONF_ELEMENT, languagePrefix);
            String htmlForumElement = imcref.parseDoc(null, TEMPLATE_FORUM_ELEMENT, languagePrefix);

            String[][] listOfChats = imcref.sqlProcedureMulti("ListChats", new String[0]);

            // lets create chatlist
            StringBuffer chatListTag = new StringBuffer();

            Hashtable chatTags = new Hashtable();
            Hashtable forumTags = new Hashtable();
            Hashtable debateTags = new Hashtable();

            for (int i = 0; i < listOfChats.length; i++) {

                String metaId = listOfChats[i][0];
                String[][] queryResultForum = chatref.sqlProcedureMulti("C_AdminStatistics1", new String[]{metaId, startDate, endDate, listMode});

                //lets create forumList for this chat
                StringBuffer forumList = new StringBuffer();

                for (int j = 0; j < queryResultForum.length; j++) {

                    String forumId = queryResultForum[j][0];
                    String[][] queryResultDebate = chatref.sqlProcedureMulti("C_AdminStatistics2", new String[]{metaId, forumId, startDate, endDate, listMode});

                    // lets create debatelist for this forum
                    StringBuffer debateList = new StringBuffer();
                    for (int k = 0; k < queryResultDebate.length; k++) {
                        debateTags.put("DEBATE", queryResultDebate[k][1]);
                        debateTags.put("DATE", queryResultDebate[k][2]);
                    }

                    forumTags.put("FORUM", queryResultForum[j][1]);
                    forumTags.put("DEBATE_LIST", debateList.toString());
                    forumList.append((Parser.parseTags(new StringBuffer(htmlForumElement), '#', " <>\n\r\t", forumTags, true, 1)).toString());
                }

                if (queryResultForum.length > 0) {
                    chatTags.put("SERVLET_URL", "");
                    chatTags.put("META_ID", metaId);
                    chatTags.put("CONFERENCE", listOfChats[i][1]);
                    chatTags.put("FORUM_LIST", forumList.toString());
                    chatListTag.append((Parser.parseTags(new StringBuffer(htmlChatElement), '#', " <>\n\r\t", chatTags, true, 1)).toString());
                }
            }

            //Lets generate the html page
            VariableManager vm = new VariableManager();
            vm.addProperty("CHAT_LIST", chatListTag.toString());

            this.sendHtml(request, response, vm, TEMPLATE_LIST);

        } else {
            sendErrorMessage(imcref, eMailServerMaster, languagePrefix, ERROR_HEADER, 10, response);
        }
    }

}
