
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;

public class SavePermissions extends HttpServlet {

    /**
     * doPost()
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        String start_url = imcref.getStartUrl();

        imcode.server.user.UserDomainObject user;

        // Check if user logged on
        if ( ( user = Utility.getLoggedOnUserOrRedirect( req, res, start_url ) ) == null ) {
            return;
        }

        int meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );
        int set_id = Integer.parseInt( req.getParameter( "set_id" ) );

        res.setContentType( "text/html" );
        Writer out = res.getWriter();

        if ( !imcref.checkDocAdminRights( meta_id, user, 4 ) ) {	// Checking to see if user may edit this
            String output = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;
        }

        String[] perms = req.getParameterValues( "permissions" );
        String[] perms_ex = req.getParameterValues( "permissions_ex" );

        int permissions = 0;

        String newstr = "";

        // Check if this is the permissions for new documents, or for this one.
        if ( req.getParameter( "new" ) != null ) {
            newstr = "New";
        }

        if ( req.getParameter( "ok" ) != null ) {

            // User pressed ok.

            // Here i fetch the current users set-id and the document-permissions for this document (Whether set-id 1 is more privileged than set-id 2.)
            String[] current_permissions = imcref.sqlProcedure( "GetUserPermissionSet", new String[]{"" + meta_id, "" + user.getUserId()} );
            int user_set_id = Integer.parseInt( current_permissions[0] );
            int user_perm_set = Integer.parseInt( current_permissions[1] );
            int currentdoc_perms = Integer.parseInt( current_permissions[2] );

            // I'll make a hashmap to store the users extended permissions in.
            // The hashmap will map permission_ids to hashsets containing permission_data.
            HashMap perm_ex_data_map = new HashMap();

            // Get an array containing perm_id, perm_data, perm_id, perm_data, and so on.
            String[] user_permission_data = imcref.sqlProcedure( "GetUserPermissionSetEx", new String[]{"" + meta_id, "" + user.getUserId()} );
            for ( int i = 0; i < user_permission_data.length; i += 2 ) {
                // Check if the map contains a set for this permission_id
                HashSet temp_set = (HashSet)perm_ex_data_map.get( user_permission_data[i] );
                if ( temp_set == null ) {     // If not, add it.
                    temp_set = new HashSet();
                    perm_ex_data_map.put( user_permission_data[i], temp_set );
                }
                // put the permission_data in the set.
                temp_set.add( user_permission_data[i + 1] );
            }

            // Delete all extended permissions for this permissionset.
            imcref.sqlUpdateProcedure( "Delete" + newstr + "DocPermissionSetEx", new String[]{"" + meta_id, "" + set_id} );

            // Read checkboxes and OR the values into an int, which is stored in the db.
            for ( int i = 0; perms != null && i < perms.length; ++i ) {
                int perm = Integer.parseInt( perms[i] );
                if ( user_set_id == 0			// If current user has full rights,
                        || ( user_set_id == 1 	// or has set-id 1
                        && set_id == 2 		// and is changing set-id 2
                        && ( user_perm_set & perm ) != 0	// and the user has this permission himself
                        && ( currentdoc_perms & 1 ) != 0// and set-id 1 is more privleged than set-id 2 for this document. (Bit 0)
                        ) ) {
                    permissions |= perm;
                }
            }
            imcref.sqlUpdateProcedure( "Set" + newstr + "DocPermissionSet", new String[]{"" + meta_id, "" + set_id, "" + permissions} );

            // Read the select-lists for the new extended permissions, and store the values in the db.

            for ( int i = 0; perms_ex != null && i < perms_ex.length; ++i ) {
                // We have an array of all extended permissions,
                // in the form permission_value. I.e. 8_1, 524288_5, and so on.
                String perm_ex_str = perms_ex[i];
                int us_index = perm_ex_str.indexOf( "_" );
                // Get the permission...
                String perm_str = perm_ex_str.substring( 0, us_index );
                // ...and the value for the permission.
                String value_str = perm_ex_str.substring( us_index + 1 );
                int perm = Integer.parseInt( perm_str );
                int value = Integer.parseInt( value_str );
                HashSet temp_set = null;
                if ( user_set_id == 0			// If current user has full rights,
                        || ( user_set_id == 1 	// or has set-id 1
                        && set_id == 2 		// and is changing set-id 2
                        // And the user has this particular extended permission.
                        // Get the hashset for the permission_id from the map, and check if it contains the value.
                        && ( ( temp_set = (HashSet)perm_ex_data_map.get( perm_str ) ) != null ? temp_set.contains( value_str ) : false ) && ( currentdoc_perms & 1 ) != 0// and set-id 1 is more privleged than set-id 2 for this document. (Bit 0)
                        ) ) {

                    imcref.sqlUpdateProcedure( "Set" + newstr + "DocPermissionSetEx", new String[]{"" + meta_id, "" + set_id, "" + perm, "" + value} );
                }
            }
        }

        user.put( "flags", new Integer( 4 ) );
        String output = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
        if ( output != null ) {
            out.write( output );
        }
    }
}
