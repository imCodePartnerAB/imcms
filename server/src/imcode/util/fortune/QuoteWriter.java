package imcode.util.fortune;

import imcode.util.Parser;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class for writing Quotes to a Writer.
 */
public class QuoteWriter {

    private Writer writer;
    private String eol;

    /**
     * Construct a QuoteWriter on the given Writer
     */
    public QuoteWriter( Writer writer ) {
        this.writer = writer;
        this.eol = System.getProperty( "line.separator" );
    }

    public void writeQuote( Quote theQuote ) throws IOException {
        // Get the DateRange of the Quote
        DateRange dateRange = theQuote.getDateRange();

        Date startDate = dateRange.getStartDate();
        Date endDate = dateRange.getEndDate();

        // Remove one day from the end-date
        // Since DateRange excludes the end-date,
        // and this representation doesn't.
        endDate = new Date( endDate.getTime() - 86400000 );

        dateRange = new DateRange( startDate, endDate );

        // Get a (possibly non-thread-safe) DateFormat
        DateFormat dateFormat = new SimpleDateFormat( "yyMMdd" );

        // Get and format the dates.
        String startDateString = dateFormat.format( dateRange.getStartDate() );
        String endDateString = dateFormat.format( dateRange.getEndDate() );

        // Set up replacements for encoding the string.
        String[] replacements = {
            "&", "&amp;",
            "<", "&lt;",
            ">", "&gt;",
            "\r\n", "\n",
            "\r", "\n",
            "\n", "<BR>",
        };

        // Get and prepare the text.
        String text = Parser.parseDoc( theQuote.getText(), replacements );

        writer.write( startDateString + "#" + endDateString + "#" + text + "#" + eol );
    }

}
