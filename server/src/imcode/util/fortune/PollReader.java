package imcode.util.fortune;

import java.io.Reader ;
import java.io.IOException ;

import java.util.StringTokenizer ;

public class PollReader {

    private QuoteReader quoteReader;

    public PollReader( Reader reader ) {
        quoteReader = new QuoteReader( reader );
    }

    /**
     * Read and return one Poll
     * 
     * @return A Poll, or null if none could be read.
     * @throws IOException If there was an error reading the underlying reader.
     */
    public Poll readPoll() throws IOException {

        try {
            return parsePollFromQuote( quoteReader.readQuote() );
        } catch ( NullPointerException npe ) {
            return null;
        }
    }

    /**
     * Parse a Poll from a String.
     * 
     * @return A Poll, or null if none could be read.
     */
    private Poll parsePollFromQuote( Quote aQuote ) {

        // Tokenize the text of the quote.
        StringTokenizer tokenizer = new StringTokenizer( aQuote.getText(), "#" );

        // The first token is the question.
        String question = "";
        if ( tokenizer.hasMoreTokens() ) {
            question = tokenizer.nextToken();
        }

        // Create the poll
        Poll thePoll = new Poll( question, aQuote.getDateRange() );

        // Collect and parse all answers with their counts
        while ( tokenizer.hasMoreTokens() ) {

            // Get the next answer-token.
            String token = tokenizer.nextToken();

            // Find the split between the answer and the answercount
            // in the token.
            int tokenIndex = token.indexOf( ": " );

            // Check if the token looks like a poll-answer.
            if ( token.length() > 3 && tokenIndex != -1 ) {
                try {
                    // Split the token into an answer and a count.
                    String answer = token.substring( 0, tokenIndex );
                    String answerCountString = token.substring( tokenIndex + 2 );
                    int answerCount = Integer.parseInt( answerCountString );
                    // Set the answercount in this poll.
                    thePoll.setAnswerCount( answer, answerCount );
                } catch ( NumberFormatException ignored ) {
                    // Exception ignored
                }
            }
        }

        // Return the Poll.
        return thePoll;
    }

}
