package com.imcode.util;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.ArrayList;

/**
 * Utilities for preparing data for human consumption.
 */
public class HumanReadable {

    private static final int ONE_KILOBYTE = 1024;
    private static final int ONE_MEGABYTE = ONE_KILOBYTE * ONE_KILOBYTE;
    private static final int ONE_GIGABYTE = ONE_MEGABYTE*ONE_KILOBYTE;

    /**
     * Format a byte size like for example "1 kB" or "1.5 MB".
     *
     * @param size The size to be formatted
     **/
    public static String getHumanReadableByteSize( long size ) {
        double displaySize = size ;
        DecimalFormat df = new DecimalFormat( "#.#" );
        DecimalFormatSymbols decimalFormatSymbols = df.getDecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator( '.' );
        df.setDecimalFormatSymbols( decimalFormatSymbols );
        String sizeSuffix = "B";
        if (displaySize >= ONE_GIGABYTE) {
            displaySize /= ONE_GIGABYTE ;
            sizeSuffix = "GB";
        } else if ( displaySize >= ONE_MEGABYTE ) {
            displaySize /= ONE_MEGABYTE;
            sizeSuffix = "MB";
        } else if ( displaySize >= ONE_KILOBYTE ) {
            displaySize /= ONE_KILOBYTE;
            sizeSuffix = "kB";
        }
        return df.format(displaySize)+" "+sizeSuffix ;
    }

    /**
     * Format a time interval like for example "3h, 4m, 5s, 60ms"
     *
     * @param milliseconds
     * @return
     */
    public static String getHumanReadableTimeLength(long milliseconds) {
        long ms = milliseconds ;
        TimeLengthSuffixPair[] pairs = new TimeLengthSuffixPair[] {
            new TimeLengthSuffixPair( DateUtils.MILLIS_IN_HOUR, "h" ),
            new TimeLengthSuffixPair( DateUtils.MILLIS_IN_MINUTE, "m" ),
            new TimeLengthSuffixPair( DateUtils.MILLIS_IN_SECOND, "s" ),
            new TimeLengthSuffixPair( 1, "ms" ),
        };
        List resultList = new ArrayList() ;
        for ( int i = 0; i < pairs.length; i++ ) {
            TimeLengthSuffixPair pair = pairs[i];
            long timeLength = pair.timeLength;
            if ( ms >= timeLength ) {
                long unitTime = ms / timeLength;
                ms %= timeLength;
                resultList.add( unitTime + pair.suffix );
            }
        }
        return StringUtils.join( resultList.iterator(), ", " ) ;
    }

    private static class TimeLengthSuffixPair {
        long timeLength;
        String suffix;

        TimeLengthSuffixPair( long timeLength, String suffix ) {
            this.timeLength = timeLength;
            this.suffix = suffix;
        }
    }
}
