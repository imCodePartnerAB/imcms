package com.imcode.imcms.servlet.superadmin;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.server.*;
import imcode.server.user.UserDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.util.*;
import imcode.util.fortune.*;

import java.text.*;

import com.imcode.imcms.servlet.superadmin.Administrator;
import com.imcode.imcms.servlet.superadmin.Administrator;

public class AdminQuestionsFile extends Administrator implements imcode.server.IMCConstants {

    private final static String ADMIN_TEMPLATE = "admin_questions_file.html";
    private final static String DATE_ERROR = "date_err_msg.frag";
    private final static String TEXT_ERROR = "text_err_msg.frag";

    private final static SimpleDateFormat dateForm = new SimpleDateFormat( "yyMMdd" );

    private final static long ONE_DAY = 86400000;

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        this.doPost( req, res );
    } // End doGet

    /**
     * doPost
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        // Lets get the server this request was aimed for

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        HttpSession session = req.getSession();

        res.setContentType( "text/html" );
        Writer out = res.getWriter();

        String whichFile = (String)session.getAttribute( "file" );

        if ( req.getParameter( "back" ) != null || null == whichFile || "".equals( whichFile ) ) {
            res.sendRedirect( "AdminQuestions" );
            return;
        }

        UserDomainObject user = Utility.getLoggedOnUser( req ) ;
        List lines = (List)session.getAttribute( "lines" );
        String date1 = "";
        String date2 = "";
        String text = "";
        String errMsgDate = imcref.parseExternalDoc( null, DATE_ERROR, user, DocumentDomainObject.DOCTYPE_FORTUNES + "" );
        String errMsgTxt = imcref.parseExternalDoc( null, TEXT_ERROR, user, DocumentDomainObject.DOCTYPE_FORTUNES + "" );

        if ( req.getParameter( "save" ) != null ) {

            addLineToList( req, lines );

            imcref.setPollList( whichFile + ".poll.txt", lines );

            // Get the current poll
            List currentPollList = imcref.getPollList( whichFile + ".current.txt" );

            if ( currentPollList.isEmpty() ) {
                // There was no poll, get one.
                currentPollList = this.getNewQuestion( imcref, whichFile );
                imcref.setPollList( whichFile + ".current.txt", currentPollList );
            }
            Poll currentPoll = (Poll)currentPollList.get( 0 );

            // Get a new poll
            List newPollList = this.getNewQuestion( imcref, whichFile );
            Poll newCurrentPoll = (Poll)newPollList.get( 0 );

            // Replace the current poll if it changed.
            if ( !newCurrentPoll.getQuestion().equals( currentPoll.getQuestion() ) ) {
                imcref.setPollList( whichFile + ".current.txt", newPollList );
                if ( !"".equals( currentPoll.getQuestion() ) && currentPoll.getTotalAnswerCount() > 0 ) {
                    List statsList = imcref.getPollList( whichFile + ".stat.txt" );
                    statsList.add( currentPoll );
                    imcref.setPollList( whichFile + ".stat.txt", statsList );
                }
            }

            //tillbaks till
            res.sendRedirect( "AdminQuestions" );
            return;

        } else if ( req.getParameter( "add" ) != null ) {
            //hämta parametrar
            date1 = ( req.getParameter( "date1" ) ).trim();
            date2 = ( req.getParameter( "date2" ) ).trim();
            text = ( req.getParameter( "text" ) ).trim();

            boolean ok = true;
            if ( !checkDate( date1 ) ) {
                date1 = errMsgDate;
                ok = false;
            }

            if ( !checkDate( date2 ) ) {
                date2 = errMsgDate;
                ok = false;
            }

            try {
                DateRange range = new DateRange( dateForm.parse( date1 ), new Date( dateForm.parse( date2 ).getTime() + ONE_DAY ) );
                if ( !checkDates( req, range ) ) {
                    date1 = errMsgDate;
                    date2 = errMsgDate;
                    ok = false;
                }
            } catch ( ParseException ignored ) {
            }

            if ( text.length() < 1 ) {
                text = errMsgTxt;
                ok = false;
            }

            if ( ok ) {
                addLineToList( req, lines );
                date1 = "";
                date2 = "";
                text = "";
            }
        } else if ( req.getParameter( "edit" ) != null ) {
            //hämta raden som är markerad
            String row = req.getParameter( "AdminFile" );

            if ( row != null ) {
                //lägg till en eventuellt redan uppflyttad rad
                addLineToList( req, lines );
                int theRow = Integer.parseInt( row );
                Poll poll = (Poll)lines.get( theRow );
                DateRange dates = poll.getDateRange();

                date1 = dateForm.format( dates.getStartDate() );
                date2 = dateForm.format( new Date( dates.getEndDate().getTime() - ONE_DAY ) );
                text = poll.getQuestion();
                lines.remove( poll );
            } else {
                date1 = ( req.getParameter( "date1" ) ).trim();
                date2 = ( req.getParameter( "date2" ) ).trim();
                text = ( req.getParameter( "text" ) ).trim();
            }
        } else if ( req.getParameter( "remove" ) != null ) {
            // retrieve list of rows to remove
            String rows[] = req.getParameterValues( "AdminFile" );

            try {
                // put the lines-list in a Map, keyed by list-index.
                Map rowMap = new HashMap();
                for ( int i = 0; i < lines.size(); i++ ) {
                    rowMap.put( new Integer( i ), lines.get( i ) );
                }

                // remove the given list-indexes.
                for ( int i = 0; i < rows.length; i++ ) {
                    if ( !"".equals( rows[i] ) ) {
                        rowMap.remove( Integer.decode( rows[i] ) );
                    }
                }

                // put the map back into a list, sorted by index.
                Integer[] listIndexes = new Integer[rowMap.size()];
                listIndexes = (Integer[])rowMap.keySet().toArray( listIndexes );
                Arrays.sort( listIndexes );

                lines = new ArrayList( rowMap.size() );
                for ( int i = 0; i < listIndexes.length; ++i ) {
                    lines.add( rowMap.get( listIndexes[i] ) );
                }
            } catch ( NullPointerException ignored ) {
                // No rows were selected
            } catch ( NumberFormatException ignored ) {
                // Invalid selection
            }

        }

        //this part is always done its the creation and sending of the page to the browser
        session.setAttribute( "lines", lines );

        StringBuffer buff = createOptionList( lines );


        //Add info for parsing to a Vector and parse it with a template to a htmlString that is writeed
        Vector values = new Vector();
        values.add( "#date1#" );
        values.add( date1 );
        values.add( "#date2#" );
        values.add( date2 );
        values.add( "#text#" );
        values.add( text );
        values.add( "#file#" );
        values.add( whichFile );
        values.add( "#options#" );
        values.add( buff.toString() );

        String parsed = imcref.parseExternalDoc( values, ADMIN_TEMPLATE, user, DocumentDomainObject.DOCTYPE_FORTUNES + "" );
        out.write( parsed );
        return;

    }//end doPost()

    private StringBuffer createOptionList( List lines ) {
        StringBuffer buff = new StringBuffer();
        int counter = 0;
        Iterator iter = lines.iterator();
        while ( iter.hasNext() ) {
            Poll poll = (Poll)iter.next();
            DateRange dates = poll.getDateRange();
            buff.append( "<option value=\"" + counter++ + "\">" + dateForm.format( dates.getStartDate() ) + " " + dateForm.format( new Date( dates.getEndDate().getTime() - ONE_DAY ) ) + " " + poll.getQuestion() + "</option>" );
        }
        return buff;
    }

    private void addLineToList( HttpServletRequest req, List lines ) {
        String date1 = ( req.getParameter( "date1" ) ).trim();
        String date2 = ( req.getParameter( "date2" ) ).trim();
        String text = ( req.getParameter( "text" ) ).trim();

        if ( text.length() > 1 ) {
            try {
                DateRange range = new DateRange( dateForm.parse( date1 ), new Date( dateForm.parse( date2 ).getTime() + ONE_DAY ) );
                Poll poll = new Poll( text, range );
                lines.add( poll );
            } catch ( ParseException ignored ) {
                // ignored
            }
        }
    }

    private boolean checkDate( String dateStr ) {
        try {
            dateForm.parse( dateStr );
        } catch ( java.text.ParseException pe ) {
            return false;
        }
        return true;
    }

    private boolean checkDates( HttpServletRequest req, DateRange range ) {
        HttpSession session = req.getSession();
        List questionList = (List)session.getAttribute( "lines" );

        Iterator qIterator = questionList.iterator();
        while ( qIterator.hasNext() ) {
            Poll aPollQuestion = (Poll)qIterator.next();
            if ( ( range ).overlap( aPollQuestion.getDateRange() ) ) {
                return false;
            }
        }

        return true;
    }

    private List getNewQuestion( IMCServiceInterface imcref, String whichFile ) {

        List questionList = imcref.getQuoteList( whichFile + ".poll.txt" );

        Date date = new Date();
        Iterator qIterator = questionList.iterator();

        while ( qIterator.hasNext() ) {
            Quote aPollQuestion = (Quote)qIterator.next();

            if ( aPollQuestion.getDateRange().contains( date ) ) {
                String questionString = aPollQuestion.getText();

                List newPollList = new LinkedList();
                newPollList.add( new Poll( questionString, aPollQuestion.getDateRange() ) );
                return newPollList;
            }
        }

        // FIXME: We didn't find a question/poll... what to do, what to do?
        List newPollList = new LinkedList();
        DateRange dateRange = new DateRange( new Date( 0 ), new Date( 0 ) );
        newPollList.add( new Poll( "", dateRange ) );
        return newPollList;
    }

} // End of class
