package imcode.util.fortune ;

import java.io.* ;
import java.text.* ;
import java.util.* ;

import imcode.util.* ;

/**
   A class for writing Polls to a Writer.
**/
public class PollWriter {

    private QuoteWriter writer ;

    /**
       Construct a PollWriter on the given Writer
    **/
    public PollWriter(Writer writer) {
	this.writer = new QuoteWriter(writer) ;
    }

    public void writePoll(Poll thePoll) throws IOException {
	// Get the DateRange of the Poll
	Quote aQuote = new Quote(null,thePoll.getDateRange()) ;
	aQuote.setDateRange(thePoll.getDateRange()) ;

	StringBuffer textBuffer = new StringBuffer(thePoll.getQuestion()) ;

	Iterator iterator = thePoll.getAnswersIterator() ;
	while (iterator.hasNext()) {
	    String answer = (String)iterator.next() ;
	    textBuffer.append("#"+answer+": "+thePoll.getAnswerCount(answer)) ;
	}

	aQuote.setText(textBuffer.toString()) ;

	writer.writeQuote(aQuote) ;
    }

}
