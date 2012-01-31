package com.imcode.imcms.api.contrib;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.TextDocument;
import imcode.util.DateConstants;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Compares text documents by a date in a textfield.
 *
 * @author Anders Flodell, Vetenskapsrådet
 * @author Pontus Amberg, Vetenskapsrådet
 */
public class DateTextFieldTextDocumentComparator extends Document.Comparator {

    private String datePattern = DateConstants.DATE_FORMAT_STRING;
    private int textFieldNumber;

    /**
     * Constructs DateTextFieldTextDocumentComparator using the given {@link com.imcode.imcms.api.TextDocument.TextField}
     * index.
     * @param textFieldNumber {@link com.imcode.imcms.api.TextDocument.TextField} index in a {@link TextDocument}
     */
    public DateTextFieldTextDocumentComparator( int textFieldNumber ) {
        this.textFieldNumber = textFieldNumber;
    }

    /**
     * Constructs DateTextFieldTextDocumentComparator using the given {@link com.imcode.imcms.api.TextDocument.TextField}
     * index and a string representing a pattern used for parsing dates.
     * @param textFieldNumber {@link com.imcode.imcms.api.TextDocument.TextField} index in a {@link TextDocument}
     * @param datePattern a String representing a pattern used for parsing dates.
     */
    public DateTextFieldTextDocumentComparator( int textFieldNumber, String datePattern ) {
        this(textFieldNumber) ;
        this.datePattern = datePattern;
    }

    private int compareDate( Date date1, Date date2 ) {
        if ( date1 == null && date2 == null ) {
            return 0;
        }
        if ( date1 == null ) {
            return -1;
        }
        if ( date2 == null ) {
            return 1;
        }
        return date1.compareTo( date2 );
    }

    private int compareDateStrings( String s1, String s2 ) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( datePattern );
        Date date1 = null;
        Date date2 = null;
        if ( StringUtils.isBlank( s1 ) && StringUtils.isBlank( s2 ) ) {
            return 0;
        }
        if ( StringUtils.isBlank( s1 ) ) {
            return -1;
        }
        if ( StringUtils.isBlank( s2 ) ) {
            return 1;
        }
        try {
            date1 = simpleDateFormat.parse( s1 );
        } catch ( ParseException ex ) { }
        try {
            date2 = simpleDateFormat.parse( s2 );
        } catch ( ParseException ex1 ) { }
        return compareDate( date1, date2 );
    }

    protected int compareDocuments( Document d1, Document d2 ) {
        TextDocument textDocument1 = (TextDocument) d1;
        TextDocument textDocument2 = (TextDocument) d2;
        int result;
        if ( textDocument1 == null && textDocument2 == null ) {
            return 0;
        }
        if ( textDocument1 == null ) {
            return -1;
        }
        if ( textDocument2 == null ) {
            return 1;
        }
        result = compareDateStrings(textDocument1.getTextField(this.textFieldNumber).getText(),
                                    textDocument2.getTextField(this.textFieldNumber).getText());
        if ( result != 0 ) {
            return result;
        }

        if ( textDocument1.getId() > textDocument2.getId() ) {
            return 1;
        } else if ( textDocument1.getId() < textDocument2.getId() ) {
            return -1;
        } else {
            return 0;
        }
    }
}
