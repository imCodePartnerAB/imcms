package imcode.external.diverse;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

public class MetaInfo extends HttpServlet {

    private MetaInfo(){

    }

    /**
     * Collects the parameters from the request object
     **/

    public static Parameters getParameters( HttpServletRequest req ) {

        // Lets get the META ID PARAMETERS
        String metaId = ( req.getParameter( "meta_id" ) == null ) ? "" : ( req.getParameter( "meta_id" ) );

        Parameters metaInfoParameters = new Parameters(Integer.parseInt(metaId) ) ;

        return metaInfoParameters ;
    }


    /**
     * Creates a parameterstring with the standard metadata
     **/

    public static String passMeta( Parameters params ) {
        String args = "meta_id=" + params.getMetaId();
        return args;
    }

    public static String passMeta( Properties params ) {
        String args = "meta_id=" + params.getProperty("META_ID");
        return args;
    }

    public static Properties createPropertiesFromMetaInfoParameters( Parameters metaParams ) {
        Properties params = new Properties() ;
        params.setProperty("META_ID", ""+metaParams.getMetaId()) ;
        return params;
    }


    public static class Parameters {

        private int metaId;

        public Parameters( int metaId ) {
            this.metaId = metaId;
        }

        public int getMetaId() {
            return metaId;
        }

    }


} // end class
