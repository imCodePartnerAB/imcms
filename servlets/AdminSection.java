import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.external.diverse.* ;

public class AdminSection extends Administrator {
        private final static String CVS_REV = "$Revision$" ;
        private final static String CVS_DATE = "$Date$" ;
		
		public final static String ADMIN_TEMPLATE 			= "sections/admin_section.html";
		public final static String ADD_TEMPLATE 			= "sections/admin_section_add.html";
		public final static String NAME_TEMPLATE 			= "sections/admin_section_name.html";
		public final static String DELETE_TEMPLATE 			= "sections/admin_section_delete.html";
		public final static String LINE_TEMPLATE 			= "sections/admin_section_line.html";
 		public final static String DELETE_CONFIRM_TEMPLATE 	= "sections/admin_section_delete_confirm.html";       
        
        public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        	String host			= req.getHeader("Host") ;
			String imcserver	= imcode.util.Utility.getDomainPref("adminserver",host) ;
			String start_url	= imcode.util.Utility.getDomainPref( "start_url",host ) ;

			res.setContentType("text/html") ;
			
			imcode.server.User user ;
			// Check if user logged on
			if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
				return ;
			}
			
			//lets see if its a super admin we got outherwise get rid of him fast
			if (super.checkAdminRights(imcserver, user) == false) { 
				log("the user wasn't a administrator so lets get rid of him");
			    res.sendRedirect("StartDoc");
			    return ;
			}
			
			
			//lets see if one button was punched or if we shal get the start page for section admin
			
			int user_id = user.getInt("user_id") ;
			String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
			//ok so far lets load the admin page
			ServletOutputStream out = res.getOutputStream() ;
			out.print ( IMCServiceRMI.parseDoc(imcserver,null,ADMIN_TEMPLATE,lang_prefix ) ) ;
			out.flush();out.close();
			return;
		
		}//end doGet()
		
		public void doPost ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
			String host			= req.getHeader("Host") ;
			String imcserver	= imcode.util.Utility.getDomainPref("adminserver",host) ;
			String start_url	= imcode.util.Utility.getDomainPref( "start_url",host ) ;
			
			imcode.server.User user ;
			// Check if user logged on
			if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
				return ;
			}
			
			//lets see if its a super admin we got outherwise get rid of him fast
			if (super.checkAdminRights(imcserver, user) == false) { 
				log("the user wasn't a administrator so lets get rid of him");
			    res.sendRedirect("StartDoc");
			    return ;
			}
			
			
			StringBuffer htmlToSend = new StringBuffer(); 
			
			int user_id = user.getInt("user_id") ;
			String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
			
			//**** lets handle the add_section-case ****
			if (req.getParameter("add_section")!= null) {
				//lets start and see if we need to save anything to the db
				String new_section = req.getParameter("new_section_name")==null ? "":req.getParameter("new_section_name").trim();
				if ( !new_section.equals("") ) {
					//ok we have a new name lets save it to db, but only if it's not exists in db
					IMCServiceRMI.sqlUpdateProcedure(imcserver, "SectionAdd "+new_section);					 
				}
			
				//now we needs a list of the created ones in db
				String[][] section_arr = IMCServiceRMI.sqlProcedureMulti(imcserver, "SectionGetAllCount");			
				Vector vec = new Vector();
				vec.add("#section_list#"); vec.add(createOptionList(req, section_arr, lang_prefix, null)); 
				//ok lets parse the page with right template
				htmlToSend.append(IMCServiceRMI.parseDoc(imcserver,vec,ADD_TEMPLATE,lang_prefix ));								
			}//end if(req.getParameter("add_section")!= null)
			//**** end add_section-case ****
			
			//**** lets handle the edit_section-case ****
			if (req.getParameter("edit_section")!= null) {
				//ok first we need to see if we need to shange name on any
				String new_section = req.getParameter("new_section_name")==null ? "":req.getParameter("new_section_name").trim();
				String section_id = req.getParameter("section_list")==null ? "-1":req.getParameter("section_list");
				if ( (!new_section.equals("")) && (!section_id.equals("-1")) ) {
					//ok we have a new name lets save it to db, but only if it's not exists in db
					IMCServiceRMI.sqlUpdateProcedure(imcserver, "SectionChangeName "+section_id+", '"+new_section+"'");					 
				}
			
				//now we needs a list of the created ones in db
				String[][] section_arr = IMCServiceRMI.sqlProcedureMulti(imcserver, "SectionGetAllCount");		
				Vector vec = new Vector();
				vec.add("#section_list#"); vec.add(createOptionList(req, section_arr, lang_prefix, null)); 
				//ok lets parse the page with right template
				htmlToSend.append(IMCServiceRMI.parseDoc(imcserver,vec,NAME_TEMPLATE,lang_prefix ));	
				
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
			
			if (req.getParameter("delete_section_confirm")!= null) {
				//ok we have been at the confirm-page so lets rock
			  	String new_sections = req.getParameter("new_section_confirm");
				String del_section = req.getParameter("delete_section");
				if (del_section == null || new_sections == null) {
					log("new_section_confirm or delete_section was null so return");
					return;
				}
				if (new_sections.equals("-1")) {
					//ok the admin wants to get rid of all the section connections
					IMCServiceRMI.sqlUpdateProcedure(imcserver, "SectionDelete "+del_section );						
				}else {
					//ok the admin wants to conect a nother section and then delete the one						
					IMCServiceRMI.sqlUpdateProcedure(imcserver, "SectionChangeAndDeleteCrossref "+new_sections+", "+ del_section);	
				}			
			}
			
			if (req.getParameter("delete_section")!= null) {	
				boolean got_confirm_page = false; //keeps track of whitch page to send "delete" or "confirm delete"
				//ok first we need to see if any section was choosen 			
				String section_id = req.getParameter("section_list")==null ? "-1":req.getParameter("section_list");
				if ( !section_id.equals("-1") ) {
					//ok we have a request for delete lets se if there is any docs connected to that section_id					
					String doc_nrs = IMCServiceRMI.sqlProcedureStr(imcserver, "SectionCount "+section_id);
					int doc_int = 0;
					if (doc_nrs != null) {
						try {
							doc_int = Integer.parseInt(doc_nrs);
						}catch(NumberFormatException nfe) {
							//if we end up here something is realy vired because it would never happens I think
							//so lets do nothing at all, Just be happy and let it rain!
						}
					}
					
					if (doc_int > 0 ) {
						//ok we have documents connected to that section id so lets get a page to handle that
						String[][] section_arr = IMCServiceRMI.sqlProcedureMulti(imcserver, "SectionGetAllCount");						
						Vector vec = new Vector();
						vec.add("#section_list#"); 	vec.add(createOptionList(req, section_arr, lang_prefix,section_id));
						vec.add("#docs#");			vec.add(doc_nrs) ;
						vec.add("#delete_section#");vec.add(section_id) ;
						htmlToSend.append(IMCServiceRMI.parseDoc(imcserver,vec,DELETE_CONFIRM_TEMPLATE,lang_prefix ));
						got_confirm_page = true;
					}else {
						//ok we can delete it right a way an carry on with it
						IMCServiceRMI.sqlUpdateProcedure(imcserver, "SectionDelete "+section_id );					 
					}
				}
			
				
				if ( ! got_confirm_page ) {
					//now we needs a list of the created ones in db
					String[][] section_arr = IMCServiceRMI.sqlProcedureMulti(imcserver, "SectionGetAllCount");						
					Vector vec = new Vector();
					vec.add("#section_list#"); vec.add(createOptionList(req, section_arr, lang_prefix, null)); 
					//ok lets parse the page with right template
					htmlToSend.append(IMCServiceRMI.parseDoc(imcserver,vec,DELETE_TEMPLATE,lang_prefix ));
				}	
				
			}//end if (req.getParameter("edit_section")!= null)
			//**** end delete_section-case ****
				
			
			
			//**** ok lets rapp it all up *****
			//lets see if we got anything to send, if not lets send it to doGet-method instead
			if (htmlToSend.length()==0) {
				doGet(req, res);
				return;
			}			
			//ok now lets send the page to browser
			res.setContentType("text/html") ;
			ServletOutputStream out = res.getOutputStream() ;
			out.print ( htmlToSend.toString() ) ;
			out.flush();out.close();	
		}//end doPost()
		
		
		//method that creates an option list of all the sections in db
		private String createOptionList(HttpServletRequest req, String[][] arr, String lang_prefix, String not_id) throws ServletException, IOException {
			String host			= req.getHeader("Host") ;
			String imcserver	= imcode.util.Utility.getDomainPref("adminserver",host) ;
			
			StringBuffer buff = new StringBuffer("");
			if (arr != null) {
				for ( int i = 0 ; i<arr.length ; i++ ) {
					if (not_id != null) {
						if(not_id.equals(arr[i][0]))
							continue;
					}
					Vector vec = new Vector() ;
					vec.add("#section_id#") ;
					vec.add(arr[i][0]) ;
					vec.add("#section_name#") ;
					vec.add(arr[i][1]) ;
					vec.add("#docs#") ;
					vec.add(arr[i][2]) ;
					buff.append(IMCServiceRMI.parseDoc(imcserver,vec,LINE_TEMPLATE,lang_prefix));
				}
			}	
			return buff.toString();
		}//end createOptionList
		
		public void log( String str ){
			super.log(str) ;
			//System.out.println("AdminSection: " + str ) ;
		}
		
		
}//end servlet

