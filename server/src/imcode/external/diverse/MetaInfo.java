package imcode.external.diverse;

import org.apache.log4j.Category;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

public class MetaInfo extends HttpServlet {

    static Category log = Category.getInstance( "server" );


    /**
     * Collects the parameters from the request object
     **/

    public static Parameters getParameters( HttpServletRequest req ) {

        // Lets get the META ID PARAMETERS
        String metaId = ( req.getParameter( "meta_id" ) == null ) ? "" : ( req.getParameter( "meta_id" ) );
        String parentId = ( req.getParameter( "parent_meta_id" ) == null ) ? "" : ( req.getParameter( "parent_meta_id" ) );

        Parameters metaInfoParameters = new Parameters(Integer.parseInt(metaId), Integer.parseInt(parentId)) ;

        return metaInfoParameters ;
    }


    /**
     * Creates a parameterstring with the standard metadata
     **/

    public static String passMeta( Parameters params ) {
        String args = "meta_id=" + params.getMetaId() + "&"
                + "parent_meta_id=" + params.getParentMetaId() ;
        return args;
    }

    public static String passMeta( Properties params ) {
        String args = "meta_id=" + params.getProperty("META_ID") + "&"
                + "parent_meta_id=" + params.getProperty("PARENT_META_ID") ;
        return args;
    }

    public static Properties createPropertiesFromMetaInfoParameters( Parameters metaParams ) {
        Properties params = new Properties() ;
        params.setProperty("META_ID", ""+metaParams.getMetaId()) ;
        params.setProperty("PARENT_META_ID", ""+metaParams.getParentMetaId()) ;
        return params;
    }


    public static class Parameters {

        private int metaId;
        private int parentMetaId;

        public Parameters( int metaId, int parentMetaId ) {
            this.metaId = metaId;
            this.parentMetaId = parentMetaId;
        }

        public int getMetaId() {
            return metaId;
        }

        public int getParentMetaId() {
            return parentMetaId;
        }
    }


} // end class
