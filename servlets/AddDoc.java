
import imcode.external.diverse.Html;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.MetaDataParser;
import imcode.util.Parser;
import imcode.util.Utility;
import imcode.util.DateConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

/**
 Adds a new document to a menu.
 Shows an empty metadata page, which calls SaveNewMeta
 */
public class AddDoc extends HttpServlet {

    private static final String DOCINFO_TEMPLATE_NAME_PREFIX = "docinfo/";
    static final String SESSION__DATA__IDENTIFIER = "AddDoc.session.data";


    class SessionData {
        String meta_id;
        String edit_menu;
        String item_selected;
        String doc_menu_no;
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        res.setContentType("text/html");
        Writer out = res.getWriter();

        HttpSession session = req.getSession(true);
        SessionData sessionData = (SessionData)session.getAttribute(SESSION__DATA__IDENTIFIER);
        if( null == sessionData ) {
            sessionData = new SessionData();
            session.setAttribute( SESSION__DATA__IDENTIFIER, sessionData );

            sessionData.meta_id = req.getParameter("meta_id");
            sessionData.item_selected = req.getParameter("edit_menu");
            sessionData.doc_menu_no = req.getParameter("doc_menu_no");
        }

        int meta_id_int = Integer.parseInt(sessionData.meta_id);
        String doc_type = "2";

        UserDomainObject user = Utility.getLoggedOnUser(req);

        String lang_prefix = user.getLangPrefix();

        boolean userHasRights = DocumentMapper.checkUsersRights(imcref, user, sessionData.meta_id, lang_prefix, doc_type);

        if (!"0".equals(sessionData.item_selected) && !userHasRights) {
            String output = AdminDoc.adminDoc(meta_id_int, meta_id_int, user, req, res);
            if (output != null) {
                out.write(output);
            }
            return;
        }

        // Lets detect the doctype were gonna add
        if (sessionData.item_selected.equals("2")) {
            doc_type = "2";
        } else if (sessionData.item_selected.equals("8")) {
            doc_type = "8";
        } else if (sessionData.item_selected.equals("6")) {
            doc_type = "6";
        } else if (sessionData.item_selected.equals("7")) {
            doc_type = "7";
        } else if (sessionData.item_selected.equals("0")) { // its an existing document
            createExistingDocPage( sessionData, imcref, user, lang_prefix, out );
            return;

        } else if (sessionData.item_selected.equals("5")) {
            doc_type = "5";
        } else {
            doc_type = sessionData.item_selected;
        }

        final int NORMAL = 0;
        final int CHECKBOX = 1;
        final int OPTION = 2;

        String[] metatable = {/*  Nullable			Nullvalue */
            "shared", "0",
            "disable_search", "0",
            "archive", "0",
            "show_meta", "0",
            "permissions", "1",
            "meta_image", null,
            "frame_name", null,
            "target", null,
            "lang_prefix", null,
            "publisher_id",null
        };

        int metatabletype[] = {
            CHECKBOX,
            CHECKBOX,
            CHECKBOX,
            CHECKBOX,
            NORMAL,
            NORMAL,
            NORMAL,
            OPTION,
            NORMAL,
            NORMAL,
        };

        // Lets get the meta information
        String sqlStr = "select * from meta where meta_id = ?";
        Hashtable hash = imcref.sqlQueryHash( sqlStr, new String[] { sessionData.meta_id } );

        String meta_image = AdminDoc.getImageUri(req);
        if( null != meta_image ) {
            hash.put("meta_image", new String[]{meta_image});
        }

        // Lets get the html template file

        String htmlStr;

        String advanced = "";

        if (imcref.checkDocAdminRights(meta_id_int, user, 2)) {
            advanced = "adv_";
        }

        if (sessionData.item_selected.equals("2")) {
            htmlStr = imcref.parseDoc(null, DOCINFO_TEMPLATE_NAME_PREFIX + advanced + "new_meta_text.html", user);
        } else {
            htmlStr = imcref.parseDoc(null, DOCINFO_TEMPLATE_NAME_PREFIX + advanced + "new_meta.html", user);
        }

        Vector vec = new Vector();
        MetaDataParser.addLanguageRelatedTagsForDocInfoPageToParseList(vec, hash, imcref, user);
        MetaDataParser.addPublisherRelatedTagsForDocInfoPageToParseList(vec, hash, imcref, user);

        StringBuffer checks = new StringBuffer();
        for (int i = 0; i < metatable.length; i += 2) {
            String value = ((String[]) hash.get(metatable[i]))[0];
            value = escapeForHtml(value);
            String tag = "#" + metatable[i] + "#";
            if (metatabletype[i / 2] == NORMAL) {			// This is not a checkbox or an optionbox
                if (htmlStr.indexOf(tag) == -1) {
                    checks.append("<input type=hidden name=\"").append(metatable[i]).append("\" value=\"").append(value).append("\">") ;
                } else {
                    vec.add(tag);							// Replace its corresponding tag
                    vec.add(value);
                }
            } else if (metatabletype[i / 2] == CHECKBOX) {	// This is a checkbox
                if (!value.equals(metatable[i + 1])) {	// If it is equal to the nullvalue, it must not appear (i.e. equal null)
                    if (htmlStr.indexOf(tag) == -1) {
                        checks.append("<input type=hidden name=\"").append(metatable[i]).append("\" value=\"").append(value).append("\">") ;
                    } else {
                        vec.add(tag);
                        vec.add("checked");
                    }
                }
            } else if (metatabletype[i / 2] == OPTION) {	// This is an optionbox
                if (htmlStr.indexOf("#" + value + "#") == -1) {	// There is no tag equal to the value of this
                    if (htmlStr.indexOf(tag) == -1) {
                        checks.append("<input type=hidden name=\"").append(metatable[i]).append("\" value=\"").append(value).append("\">") ;
                    } else {
                        vec.add(tag);							// Replace its corresponding tag
                        vec.add(value);
                    }
                } else {
                    vec.add("#" + value + "#");
                    vec.add("checked");
                }
            }
        }

        // Lets add the standard meta information
        vec.add("#parent_meta_id#");
        vec.add(((String[]) hash.get("meta_id"))[0]);

        String keywords = imcref.getDocumentMapper().getKeywordsAsOneString(meta_id_int);

        vec.add("#classification#");
        vec.add(keywords);

        // Lets fix the date information (date_created, modified etc)
        Date dt = imcref.getCurrentDate();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

        vec.add("#activated_date#");
        vec.add(dateformat.format(dt));
        dateformat = new SimpleDateFormat("HH:mm");
        vec.add("#activated_time#");
        vec.add(dateformat.format(dt));

        vec.add("#checks#");
        vec.add(checks.toString());

        // Lets add the document informtion, the creator etc
        vec.add("#doc_menu_no#");
        vec.add(sessionData.doc_menu_no);
        vec.add("#doc_type#");
        vec.add(doc_type);

        MetaDataParser.getSectionDataFromDbAndAddSectionRelatedTagsToParseList( imcref, sessionData.meta_id, vec, user);

        vec.add("#categories#");
        vec.add(MetaDataParser.createHtmlListBoxesOfCategoriesForEachCategoryType(imcref.getDocumentMapper(), meta_id_int, imcref, user));

        // Lets parse the information and send it to the browser
        if (sessionData.item_selected.equals("2")) {
            out.write(imcref.parseDoc(vec, DOCINFO_TEMPLATE_NAME_PREFIX + advanced + "new_meta_text.html", user));
        } else {
            out.write(imcref.parseDoc(vec, DOCINFO_TEMPLATE_NAME_PREFIX + advanced + "new_meta.html", user));
        }

    }

    private void createExistingDocPage( SessionData sessionData, IMCServiceInterface imcref,
                                        UserDomainObject user, String lang_prefix, Writer out ) throws IOException {
        Vector vec = new Vector();
        vec.add("#meta_id#");
        vec.add(sessionData.meta_id);
        vec.add("#doc_menu_no#");
        vec.add(sessionData.doc_menu_no);

        // Lets get todays date
        SimpleDateFormat formatter = new SimpleDateFormat(DateConstants.DATE_FORMAT_STRING);
        Date toDay = new Date();
        vec.add( "#start_date#" );
        vec.add( null );
        vec.add( "#end_date#" );
        vec.add( formatter.format( toDay ) );

        vec.add("#searchstring#");
        vec.add("");

        vec.add("#searchResults#");
        vec.add("");

        // Lets fix the sortby list, first get the displaytexts from the database
        String[] sortOrder = imcref.sqlProcedure( "SortOrder_GetExistingDocs", new String[] { user.getLangPrefix() } );
        String sortOrderStr = Html.createOptionList("", Arrays.asList(sortOrder));
        vec.add("#sortBy#");
        vec.add(sortOrderStr);

        // Lets set all the the documenttypes as selected in the html file
        String[][] allDocTypesArray = imcref.getDocumentTypesInList(lang_prefix);
        for (int i = 0; i < allDocTypesArray.length; ++i) {
            vec.add("#checked_" + allDocTypesArray[i][0] + "#");
            vec.add("checked");
        }

        // Lets set the create/ change types as selected in the html file
        String[] allPossibleIncludeDocsValues = {"created", "changed"};
        for (int i = 0; i < allPossibleIncludeDocsValues.length; i++) {
            vec.add("#include_check_" + allPossibleIncludeDocsValues[i] + "#");
            vec.add("checked");
        }

        // Lets set the and / or search preposition
        String[] allPossibleSearchPreps = {"and", "or"};
        for (int i = 0; i < allPossibleSearchPreps.length; i++) {
            vec.add("#search_prep_check_" + allPossibleSearchPreps[i] + "#");
            if (i == 0) {
                vec.add("checked");
            } else {
                vec.add("");
            }
        }
        // Lets parse the html page which consists of the add an existing doc
        out.write(imcref.parseDoc(vec, "existing_doc.html", user));
        return;
    }

    private static String escapeForHtml(String text) {
        String[] pd = {"<", "&lt;",
                       ">", "&gt;",
                       "\"", "&quot;",
                       "&", "&amp;"};
        text = Parser.parseDoc(text, pd);
        return text;
    }

}
