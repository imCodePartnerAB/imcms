
import imcode.server.HTMLConv;
import imcode.server.IMCServiceInterface;
import imcode.util.IMCServiceRMI;
import imcode.util.fortune.Quote;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class QuoteLineCollector implements imcode.external.GetDocControllerInterface {

    public String createString( HttpServletRequest req ) throws IOException {
        //lets get the stuff we need to get the quote file
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

        String fileName = req.getParameter( "qFile" );
        if ( fileName == null ) {
            fileName = (String)req.getAttribute( "qFile" );
        }

        String qLine = req.getParameter( "qLine" );

        if ( qLine == null ) {
            qLine = (String)req.getAttribute( "qLine" );
        }

        List quoteList = imcref.getQuoteList( fileName );

        int qInt;
        try {
            qInt = Integer.parseInt( qLine );
        } catch ( NumberFormatException nfe ) {
            return null;
        }

        if ( quoteList.size() > qInt && qInt >= 0 ) {
            return HTMLConv.toHTMLSpecial( ( (Quote)quoteList.get( qInt ) ).getText() );
        }

        return null;
    }
}
