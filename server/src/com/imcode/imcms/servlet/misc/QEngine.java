package com.imcode.imcms.servlet.misc;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.util.fortune.* ;
import imcode.server.* ;

/**
 * @author  Monika Hurtig
 * @version 1.0
 * Date : 2001-09-05
 */

public class QEngine extends HttpServlet
{
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{


        ImcmsServices imcref = Imcms.getServices() ;

		//get parameters
		String type = req.getParameter("type");
		String inFile = req.getParameter("file");

		if (inFile == null) {
		    inFile = "quotes.txt" ;
		}

		//gets the filecontent
		List quoteList = imcref.getQuoteList(inFile);

		Utility.setDefaultHtmlContentType( res );
		Writer out = res.getWriter();

		Date   now                = new Date() ;
		Random random             = new Random() ;
		int    quoteCount         = 0 ;
		int    matchingQuoteCount = 0 ;
		String theText            = "" ;

		// Loop through all the quotes,
		// and select one randomly from
		// the quotes that are to be used
		// for the current date.
		for (Iterator quoteIterator = quoteList.iterator() ; quoteIterator.hasNext() ; )
		{
		    ++quoteCount ;
		    Quote aQuote = (Quote)quoteIterator.next() ;
		    DateRange aQuoteDateRange = aQuote.getDateRange() ;

		    if ( aQuoteDateRange.contains( now ) ) {

			++matchingQuoteCount ;

			// The first matching quote is always stored
			// because random.nextInt(1) is always 0.
			// As quotes are read, the quote in 'theText' may or may not be replaced,
			// with decreasing probability for each quote.
			// The last quote stored (in 'theText') is used.
			// See Perl Cookbook, Recipe 8.6 (Picking a random line from a file)
			// for a better explanation.
			if (random.nextInt(matchingQuoteCount) == 0) { // Store away this quote?
			    theText = aQuote.getText() ;
			}
		    }

		}

		theText = HTMLConv.toHTMLSpecial(theText) ;

		if( "pic".equals(type) )
		{
		    theText = "<img src=\"" + theText + "\">" ;
		}
		else if( "quot".equals(type) )
		{
		    theText = theText
			+ "<input type=\"hidden\" name=\"quotrow\" value=\"" + quoteCount + "\">"
			+ "<input type=\"hidden\" name=\"quot\" value=\"" + theText + "\">" ;
		}
		else if( "ques".equals(type) )
		{
			theText = theText
			    + "<input type=\"hidden\" name=\"quesrow\" value=\"" + quoteCount + "\">"
			    + "<input type=\"hidden\" name=\"question\" value=\"" + theText + "\">" ;
		}

		out.write(theText) ;

	} // End doGet

} // End class
