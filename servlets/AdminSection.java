
import imcode.server.IMCServiceInterface;
import imcode.util.Check;
import imcode.util.IMCServiceRMI;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Vector;

public class AdminSection extends Administrator {

    private final static String ADMIN_TEMPLATE = "sections/admin_section.html";
    private final static String ADD_TEMPLATE = "sections/admin_section_add.html";
    private final static String NAME_TEMPLATE = "sections/admin_section_name.html";
    private final static String DELETE_TEMPLATE = "sections/admin_section_delete.html";
    private final static String LINE_TEMPLATE = "sections/admin_section_line.html";
    private final static String DELETE_CONFIRM_TEMPLATE = "sections/admin_section_delete_confirm.html";

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req);
        String start_url = imcref.getStartUrl();

        res.setContentType("text/html");

        imcode.server.User user;
        // Check if user logged on
        if ((user = Check.userLoggedOn(req, res, start_url)) == null) {
            return;
        }

        //lets see if its a super admin we got outherwise get rid of him fast
        if (imcref.checkAdminRights(user) == false) {
            log("the user wasn't a administrator so lets get rid of him");
            res.sendRedirect("StartDoc");
            return;
        }


        //lets see if one button was punched or if we shal get the start page for section admin

        String lang_prefix = user.getLangPrefix();
        //ok so far lets load the admin page
        ServletOutputStream out = res.getOutputStream();
        out.print(imcref.parseDoc(null, ADMIN_TEMPLATE, lang_prefix));
        out.flush();
        out.close();
        return;

    }//end doGet()

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req);
        String start_url = imcref.getStartUrl();

        imcode.server.User user;
        // Check if user logged on
        if ((user = Check.userLoggedOn(req, res, start_url)) == null) {
            return;
        }

        //lets see if its a super admin we got outherwise get rid of him fast
        if (imcref.checkAdminRights(user) == false) {
            log("the user wasn't a administrator so lets get rid of him");
            res.sendRedirect("StartDoc");
            return;
        }


        StringBuffer htmlToSend = new StringBuffer();

        String lang_prefix = user.getLangPrefix();

        //**** lets handle the add_section-case ****
        if (req.getParameter("add_section") != null) {
            //lets start and see if we need to save anything to the db
            String new_section = req.getParameter("new_section_name") == null ? "" : req.getParameter("new_section_name").trim();
            if (!new_section.equals("")) {
                //ok we have a new name lets save it to db, but only if it's not exists in db
                imcref.sqlUpdateProcedure("SectionAdd", new String[]{new_section});
            }

            //now we needs a list of the created ones in db
            String[][] section_arr = imcref.sqlProcedureMulti("SectionGetAllCount", new String[0]);
            Vector vec = new Vector();
            vec.add("#section_list#");
            vec.add(createOptionList(req, section_arr, lang_prefix, null));
            //ok lets parse the page with right template
            htmlToSend.append(imcref.parseDoc(vec, ADD_TEMPLATE, lang_prefix));
        }//end if(req.getParameter("add_section")!= null)
        //**** end add_section-case ****

        //**** lets handle the edit_section-case ****
        if (req.getParameter("edit_section") != null) {
            //ok first we need to see if we need to shange name on any
            String new_section = req.getParameter("new_section_name") == null ? "" : req.getParameter("new_section_name").trim();
            String section_id = req.getParameter("section_list") == null ? "-1" : req.getParameter("section_list");
            if ((!new_section.equals("")) && (!section_id.equals("-1"))) {
                //ok we have a new name lets save it to db, but only if it's not exists in db
                imcref.sqlUpdateProcedure("SectionChangeName", new String[]{section_id, new_section});
            }

            //now we needs a list of the created ones in db
            String[][] section_arr = imcref.sqlProcedureMulti("SectionGetAllCount", new String[0]);
            Vector vec = new Vector();
            vec.add("#section_list#");
            vec.add(createOptionList(req, section_arr, lang_prefix, null));
            //ok lets parse the page with right template
            htmlToSend.append(imcref.parseDoc(vec, NAME_TEMPLATE, lang_prefix));

        }//end if (req.getParameter("edit_section")!= null)
        //**** lend edit_section-case ****

        //**** lets handle the delete_section-case ****
        //This is a bit ugly so I think I need to explain it a bit!
        //Ok the delete procedure works like this
        //first time we need to get the delete-page
        //second time we need to se if there is any document connected to the one we intend to delete
        //if not we just delete it and then get the delete-page again
        //but if ther is any connections we need to ask the admin what to do (move or get rid of connections)
        //before we can delete it and get the delate page again

        if (req.getParameter("delete_section_confirm") != null) {
            //ok we have been at the confirm-page so lets rock
            String new_sections = req.getParameter("new_section_confirm");
            String del_section = req.getParameter("delete_section");
            if (del_section == null || new_sections == null) {
                log("new_section_confirm or delete_section was null so return");
                return;
            }
            if (new_sections.equals("-1")) {
                //ok the admin wants to get rid of all the section connections
                imcref.sqlUpdateProcedure("SectionDelete", new String[]{del_section});
            } else {
                //ok the admin wants to conect a nother section and then delete the one
                imcref.sqlUpdateProcedure("SectionChangeAndDeleteCrossref", new String[]{new_sections, del_section});
            }
        }

        if (req.getParameter("delete_section") != null) {
            boolean got_confirm_page = false; //keeps track of whitch page to send "delete" or "confirm delete"
            //ok first we need to see if any section was choosen
            String section_id = req.getParameter("section_list") == null ? "-1" : req.getParameter("section_list");
            if (!section_id.equals("-1")) {
                //ok we have a request for delete lets se if there is any docs connected to that section_id
                String doc_nrs = imcref.sqlProcedureStr("SectionCount", new String[]{section_id});
                int doc_int = 0;
                if (doc_nrs != null) {
                    try {
                        doc_int = Integer.parseInt(doc_nrs);
                    } catch (NumberFormatException nfe) {
                        //if we end up here something is realy vired because it would never happens I think
                        //so lets do nothing at all, Just be happy and let it rain!
                    }
                }

                if (doc_int > 0) {
                    //ok we have documents connected to that section id so lets get a page to handle that
                    String[][] section_arr = imcref.sqlProcedureMulti("SectionGetAllCount", new String[0]);
                    Vector vec = new Vector();
                    vec.add("#section_list#");
                    vec.add(createOptionList(req, section_arr, lang_prefix, section_id));
                    vec.add("#docs#");
                    vec.add(doc_nrs);
                    vec.add("#delete_section#");
                    vec.add(section_id);
                    htmlToSend.append(imcref.parseDoc(vec, DELETE_CONFIRM_TEMPLATE, lang_prefix));
                    got_confirm_page = true;
                } else {
                    //ok we can delete it right a way an carry on with it
                    imcref.sqlUpdateProcedure("SectionDelete", new String[]{section_id});
                }
            }


            if (!got_confirm_page) {
                //now we needs a list of the created ones in db
                String[][] section_arr = imcref.sqlProcedureMulti("SectionGetAllCount", new String[0]);
                Vector vec = new Vector();
                vec.add("#section_list#");
                vec.add(createOptionList(req, section_arr, lang_prefix, null));
                //ok lets parse the page with right template
                htmlToSend.append(imcref.parseDoc(vec, DELETE_TEMPLATE, lang_prefix));
            }

        }//end if (req.getParameter("edit_section")!= null)
        //**** end delete_section-case ****



        //**** ok lets rapp it all up *****
        //lets see if we got anything to send, if not lets send it to doGet-method instead
        if (htmlToSend.length() == 0) {
            doGet(req, res);
            return;
        }
        //ok now lets send the page to browser
        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        out.print(htmlToSend.toString());
        out.flush();
        out.close();
    }//end doPost()


    //method that creates an option list of all the sections in db
    private String createOptionList(HttpServletRequest req, String[][] arr, String lang_prefix, String not_id) throws IOException {
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req);

        StringBuffer buff = new StringBuffer("");
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                if (not_id != null) {
                    if (not_id.equals(arr[i][0]))
                        continue;
                }
                Vector vec = new Vector();
                vec.add("#section_id#");
                vec.add(arr[i][0]);
                vec.add("#section_name#");
                vec.add(arr[i][1]);
                vec.add("#docs#");
                vec.add(arr[i][2]);
                buff.append(imcref.parseDoc(vec, LINE_TEMPLATE, lang_prefix));
            }
        }
        return buff.toString();
    }//end createOptionList

    public void log(String str) {
        super.log(str);
        //System.out.println("AdminSection: " + str ) ;
    }
}//end servlet
