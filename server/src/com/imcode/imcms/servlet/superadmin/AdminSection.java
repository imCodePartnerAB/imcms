package com.imcode.imcms.servlet.superadmin;

import com.imcode.db.commands.DeleteWhereColumnsEqualDatabaseCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.db.handlers.ObjectFromFirstRowResultSetHandler;
import com.imcode.imcms.db.StringArrayArrayResultSetHandler;
import com.imcode.imcms.db.StringFromRowFactory;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

public class AdminSection extends HttpServlet {

    private final static Logger log = Logger.getLogger( AdminSection.class.getName() );

    private final static String ADMIN_TEMPLATE = "sections/admin_section.html";
    private final static String ADD_TEMPLATE = "sections/admin_section_add.html";
    private final static String NAME_TEMPLATE = "sections/admin_section_name.html";
    private final static String DELETE_TEMPLATE = "sections/admin_section_delete.html";
    private final static String LINE_TEMPLATE = "sections/admin_section_line.html";
    private final static String DELETE_CONFIRM_TEMPLATE = "sections/admin_section_delete_confirm.html";

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ImcmsServices imcref = Imcms.getServices();

        Utility.setDefaultHtmlContentType( res );

        UserDomainObject user = Utility.getLoggedOnUser(req);

        //lets see if its a super admin we got otherwise get rid of him fast
        if ( !user.isSuperAdmin() ) {
            log.debug("the user wasn't a administrator so lets get rid of him");
            res.sendRedirect("StartDoc");
            return;
        }

        //ok so far lets load the admin page
        ServletOutputStream out = res.getOutputStream();
        String content = imcref.getAdminTemplate( ADMIN_TEMPLATE, user, null );
        out.write(content.getBytes(Imcms.DEFAULT_ENCODING));
        out.flush();
        out.close();
        return;

    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ImcmsServices imcref = Imcms.getServices();

        //lets see if its a super admin we got outherwise get rid of him fast
        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (user.isSuperAdmin() == false) {
            log.debug("the user wasn't a administrator so lets get rid of him");
            res.sendRedirect("StartDoc");
            return;
        }


        StringBuffer htmlToSend = new StringBuffer();

        //**** lets handle the add_section-case ****
        if (req.getParameter("add_section") != null) {

            String new_section_name = req.getParameter("new_section_name") == null ? "" : req.getParameter("new_section_name").trim();
            final Object[] parameters2 = new String[0];
            String[][] section_arr = section_arr = (String[][]) imcref.getProcedureExecutor().executeProcedure("SectionGetAllCount", parameters2, new StringArrayArrayResultSetHandler());

            //now we needs a list of the created ones in db
            Vector vec = new Vector();
            String errormsg = "";

            //lets start and see if we need to save anything to the db
            if (!new_section_name.equals("")) {
                boolean section_exists = false;
                for( int i=0; i < section_arr.length; i++){
                    if( new_section_name.equals( section_arr[i][1]) ){
                        section_exists = true;
                        Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                        errormsg = langproperties.getProperty("error/servlet/AdminSection/section_exists");
                        break;
                    }
                }

                //ok we have a new name lets save it to db, but only if it's not exists in db
                if(!section_exists){
                    final Object[] parameters = new String[] {new_section_name};
                    imcref.getProcedureExecutor().executeUpdateProcedure("SectionAdd", parameters);
                    imcref.getDocumentMapper().initSections();
                    final Object[] parameters1 = new String[0];
                    section_arr = (String[][]) imcref.getProcedureExecutor().executeProcedure("SectionGetAllCount", parameters1, new StringArrayArrayResultSetHandler());
                }
            }

            vec.add("#errormsg#");
            vec.add( errormsg );
            vec.add("#section_list#");
            vec.add(createOptionList(section_arr, user, null));
            //ok lets parse the page with right template
            htmlToSend.append(imcref.getAdminTemplate( ADD_TEMPLATE, user, vec ));
        }//end if(req.getParameter("add_section")!= null)
        //**** end add_section-case ****

        //**** lets handle the edit_section-case ****
        if (req.getParameter("edit_section") != null) {
            //ok first we need to see if we need to shange name on any
            String new_section = req.getParameter("new_section_name") == null ? "" : req.getParameter("new_section_name").trim();
            String section_id = req.getParameter("section_list") == null ? "-1" : req.getParameter("section_list");
            if ((!new_section.equals("")) && (!section_id.equals("-1"))) {
                //ok we have a new name lets save it to db, but only if it's not exists in db
                final Object[] parameters = new String[] {section_id,
                                                                                                new_section};
                imcref.getProcedureExecutor().executeUpdateProcedure("SectionChangeName", parameters);
                imcref.getDocumentMapper().initSections();
            }

            //now we needs a list of the created ones in db
            final Object[] parameters1 = new String[0];
            String[][] section_arr = (String[][]) imcref.getProcedureExecutor().executeProcedure("SectionGetAllCount", parameters1, new StringArrayArrayResultSetHandler());
            Vector vec = new Vector();
            vec.add("#section_list#");
            vec.add(createOptionList(section_arr, user, null));
            //ok lets parse the page with right template
            htmlToSend.append(imcref.getAdminTemplate( NAME_TEMPLATE, user, vec ));

        }//end if (req.getParameter("edit_section")!= null)
        //**** lend edit_section-case ****

        //**** lets handle the delete_section-case ****
        //This is a bit ugly so I think I need to explain it a bit!
        //Ok the delete procedure works like this
        //first time we need to get the delete-page
        //second time we need to see if there is any document connected to the one we intend to delete
        //if not we just delete it and then get the delete-page again
        //but if ther is any connections we need to ask the admin what to do (move or get rid of connections)
        //before we can delete it and get the delate page again

        if (req.getParameter("delete_section_confirm") != null) {
            //ok we have been at the confirm-page so lets rock
            String new_sections = req.getParameter("new_section_confirm");
            String del_section = req.getParameter("delete_section");
            if (del_section == null || new_sections == null) {
                log.debug("new_section_confirm or delete_section was null so return");
                return;
            }
            if (new_sections.equals("-1")) {
                deleteSection( imcref, del_section );
            } else {
                final Object[] parameters = new String[] {new_sections,
                                                                                            del_section};
                imcref.getDatabase().execute(new SqlUpdateCommand("update meta_section set section_id = ? where section_id = ?", parameters));
                deleteSection( imcref, del_section );
            }
        }

        if (req.getParameter("delete_section") != null) {
            boolean got_confirm_page = false; //keeps track of whitch page to send "delete" or "confirm delete"
            //ok first we need to see if any section was choosen
            String section_id = req.getParameter("section_list") == null ? "-1" : req.getParameter("section_list");
            if (!section_id.equals("-1")) {
                //ok we have a request for delete lets see if there is any docs connected to that section_id
                final Object[] parameters = new String[] {section_id};
                String doc_nrs = (String) imcref.getProcedureExecutor().executeProcedure("SectionCount", parameters, new ObjectFromFirstRowResultSetHandler(new StringFromRowFactory()));
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
                    final Object[] parameters1 = new String[0];
                    String[][] section_arr = (String[][]) imcref.getProcedureExecutor().executeProcedure("SectionGetAllCount", parameters1, new StringArrayArrayResultSetHandler());
                    Vector vec = new Vector();
                    vec.add("#section_list#");
                    vec.add(createOptionList(section_arr, user, section_id));
                    vec.add("#docs#");
                    vec.add(doc_nrs);
                    vec.add("#delete_section#");
                    vec.add(section_id);
                    htmlToSend.append(imcref.getAdminTemplate( DELETE_CONFIRM_TEMPLATE, user, vec ));
                    got_confirm_page = true;
                } else {
                    //ok we can delete it right a way an carry on with it
                    deleteSection( imcref, section_id );
                }
            }


            if (!got_confirm_page) {
                //now we needs a list of the created ones in db
                final Object[] parameters = new String[0];
                String[][] section_arr = (String[][]) imcref.getProcedureExecutor().executeProcedure("SectionGetAllCount", parameters, new StringArrayArrayResultSetHandler());
                Vector vec = new Vector();
                vec.add("#section_list#");
                vec.add(createOptionList(section_arr, user, null));
                //ok lets parse the page with right template
                htmlToSend.append(imcref.getAdminTemplate( DELETE_TEMPLATE, user, vec ));
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
        Utility.setDefaultHtmlContentType( res );
        ServletOutputStream out = res.getOutputStream();
        out.write(htmlToSend.toString().getBytes(Imcms.DEFAULT_ENCODING));
        out.flush();
        out.close();
    }//end doPost()

    private void deleteSection( ImcmsServices imcref, String del_section ) {
        imcref.getDatabase().execute(new DeleteWhereColumnsEqualDatabaseCommand( "meta_section", "section_id", del_section) ) ;
        imcref.getDatabase().execute(new DeleteWhereColumnsEqualDatabaseCommand( "sections", "section_id", del_section) ) ;
        imcref.getDocumentMapper().initSections();
    }

    //method that creates an option list of all the sections in db
    private String createOptionList(String[][] arr, UserDomainObject user, String not_id) {
        ImcmsServices imcref = Imcms.getServices();

        StringBuffer buff = new StringBuffer("");
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                if (not_id != null) {
                    if (not_id.equals(arr[i][0])) {
                        continue;
                    }
                }
                Vector vec = new Vector();
                vec.add("#section_id#");
                vec.add(arr[i][0]);
                vec.add("#section_name#");
                vec.add(arr[i][1]);
                vec.add("#docs#");
                vec.add(arr[i][2]);
                buff.append(imcref.getAdminTemplate( LINE_TEMPLATE, user, vec ));
            }
        }
        return buff.toString();
    }//end createOptionList

}//end servlet
