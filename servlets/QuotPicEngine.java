
import imcode.server.ApplicationServer;
import imcode.server.HTMLConv;
import imcode.server.IMCServiceInterface;
import imcode.util.fortune.DateRange;
import imcode.util.fortune.Poll;
import imcode.util.fortune.Quote;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

/**
 * @author Monika Hurtig
 * @version 1.0
 *          Date : 2001-09-05
 */

public class QuotPicEngine extends HttpServlet {

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        res.setContentType( "text/html" );
        Writer out = res.getWriter();

        //get parameters
        String type = req.getParameter( "type" );
        String inFile = req.getParameter( "file" );

        //lets get a list of all questions/citat/pictures
        List lines;
        if ( type.equals( "pic" ) || type.equals( "quot" ) ) {
            lines = imcref.getQuoteList( inFile + ".txt" );
        } else {
            lines = imcref.getPollList( inFile + ".txt" );
        }

        //ok lets loop and store the ones to use
        Date date = new Date();
        Hashtable hash = new Hashtable();
        int counter = 0;
        for ( int i = 0; i < lines.size(); i++ ) {
            Object obj = lines.get( i );
            DateRange dates;
            if ( obj instanceof Poll ) {
                dates = ( (Poll)obj ).getDateRange();
            } else {
                dates = ( (Quote)obj ).getDateRange();
            }
            if ( dates.contains( date ) ) {
                hash.put( new Integer( counter++ ), new Integer( i ) );
            }
        }


        //get the text and row to return
        String theText = "<!-- Error in QuotPicEngine, nothing was returned! -->";
        int the_row = -1;

        if ( counter > 0 ) {
            //lets get the pos in the list to get
            Random random = new Random();
            the_row = random.nextInt( counter );
            the_row = ( (Integer)hash.get( new Integer( the_row ) ) ).intValue();
        }

        if ( type.equals( "pic" ) ) {
            if ( the_row != -1 ) {
                theText = "<img src=\"" + HTMLConv.toHTMLSpecial( ( (Quote)lines.get( the_row ) ).getText() ) + "\">";
            }
        } else if ( type.equals( "quot" ) ) {
            if ( the_row != -1 ) {
                theText = HTMLConv.toHTMLSpecial( ( (Quote)lines.get( the_row ) ).getText() )
                        + "<input type=\"hidden\" name=\"quotrow\" value=\"" + the_row + "\">"
                        + "<input type=\"hidden\" name=\"quot\" value=\"" + HTMLConv.toHTMLSpecial( ( (Quote)lines.get( the_row ) ).getText() ) + "\">";
            }
        } else {
            if ( the_row != -1 ) {
                theText = HTMLConv.toHTMLSpecial( ( (Poll)lines.get( the_row ) ).getQuestion() );
            }
        }

        out.write( theText );

        out.close();
        return;

    } // End doGet

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {
        this.doGet( req, res );
        return;
    }

} // End class
