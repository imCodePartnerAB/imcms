package imcode.util.fortune ;

import java.io.Reader ;
import java.io.IOException ;
import java.io.BufferedReader ;

import java.text.ParseException ;
import java.text.SimpleDateFormat ;

import java.util.Date ;
import java.util.StringTokenizer ;
import java.util.NoSuchElementException ;

import imcode.util.Parser ;
import imcode.util.DateRange ;

public class QuoteReader extends BufferedReader {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    public QuoteReader(Reader reader) {
	super(reader) ;
    }

    /**
       Read and return one Quote

       @return A Quote, or null if none could be read.

       @throws IOException If there was an error reading the underlying reader.
    **/
    public Quote readQuote() throws IOException {

	try {
	    return parseQuoteFromString(readLine()) ;
	} catch (NullPointerException npe) {
	    return null ;
	} catch (ParseException pe) {
	    return null ;
	} catch (NoSuchElementException nsee) {
	    return null ;
	}
    }

    /**
       Parse a Quote from a String.

       @return A Quote, or null if none could be read.
    **/
    protected Quote parseQuoteFromString(String fortune) throws ParseException {
	// Tokenize the line
	StringTokenizer tokens = new StringTokenizer(fortune,"#",true) ;

	// Read the first date.
	String date1string = tokens.nextToken() ;

	// Skip '#'.
	tokens.nextToken() ;

	// Read the second date.
	String date2string = tokens.nextToken() ;

	// Get a (non-thread-safe?) DateFormat.
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd") ;

	// Parse the dates.
	Date date1 = dateFormat.parse(date1string) ;
	Date date2 = dateFormat.parse(date2string) ;

	// We want to include the end-date too,
	// so add one day to it.
	date2.setTime(date2.getTime()+86400000) ;

	// Create the DateRange.
	DateRange dateRange = new DateRange(date1,date2) ;

	// Skip '#'.
	tokens.nextToken() ;

	StringBuffer textBuffer = new StringBuffer(tokens.nextToken()) ;

	// Read the text
	// Since the text may contain #'s,
	// We read until there are no more tokens.
	while ( tokens.hasMoreTokens() ) {
	    textBuffer.append(tokens.nextToken()) ;
	}

	// Cut off an '#' at the end.
	if ( '#' == textBuffer.charAt(textBuffer.length()-1) ) {
	    textBuffer.deleteCharAt(textBuffer.length()-1) ;
	}

	// Set up replacements for decoding the string.
	String[] replacements = {
	    "<BR>",   "\r\n",   // newline
	    "&lt;",   "<",
	    "&gt;",   ">",
	    "&amp;",  "&"
	} ;

	// Decode the String
	Parser.parseDoc(textBuffer,replacements) ;

	// Create the Quote.
	Quote theQuote = new Quote() ;

	theQuote.setDateRange(dateRange) ;
	theQuote.setText(textBuffer.toString()) ;

	// Return the Quote.
	return theQuote ;
    }

}
