
import imcode.external.diverse.MetaInfo;
import imcode.external.diverse.VariableManager;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

public class ConfViewer extends Conference {

    private final static String HTML_TEMPLATE = "Conf_Set.htm";         // the relative path from web root to where the servlets are

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        // Lets get the standard parameters and validate them
        // Properties params = super.getParameters(req) ;

        // Lets get the standard SESSION parameters and validate them
        Properties params = MetaInfo.createPropertiesFromMetaInfoParameters( super.getConferenceSessionParameters( req ) );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        // Lets get all parameters in a string which we'll send to every servlet in the frameset
        String paramStr = MetaInfo.passMeta( params );

        // Lets build the Responsepage
        VariableManager vm = new VariableManager();
        vm.addProperty( "CONF_FORUM", "ConfForum?" + paramStr );
        vm.addProperty( "CONF_DISC_VIEW", "ConfDiscView?" + paramStr );
        this.sendHtml( req, res, vm, HTML_TEMPLATE );
        return;
    }

} // End of class
