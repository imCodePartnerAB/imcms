package com.imcode.imcms.api;

import imcode.server.LanguageMapper;

/**
 * @author kreiger
 */
public class Language {

    private String isoCode639_2 ;

    private Language( String isoCode639_2 ) {
        this.isoCode639_2 = isoCode639_2 ;
    }

    public static Language getLanguageByISO639_2( String isoCode639_2 ) {
        if (LanguageMapper.existsIsoCode639_2(isoCode639_2)) {
            return new Language( isoCode639_2 );
        } else {
            return null ;
        }
    }

    public static Language getLanguageByISO639_1( String isoCode639_1 ) {
        try {
            return getLanguageByISO639_2(LanguageMapper.convert639_1to639_2(isoCode639_1)) ;
        } catch ( LanguageMapper.LanguageNotSupportedException e ) {
            return null ;
        }
    }

    public String toString() {
        return "ISO 639-2: "+isoCode639_2 ;
    }

    public String getIsoCode639_2() {
        return isoCode639_2;
    }

    public String getIsoCode639_1() {
        try {
            return LanguageMapper.convert639_2to639_1(isoCode639_2) ;
        } catch ( LanguageMapper.LanguageNotSupportedException e ) {
            return null ;
        }
    }

}
