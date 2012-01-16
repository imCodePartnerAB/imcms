package com.imcode.imcms.api;

import imcode.server.LanguageMapper;

/**
 * Represents Language in imcms
 * @author kreiger
 */
public class Language {

    private String isoCode639_2 ;

    private Language( String isoCode639_2 ) {
        this.isoCode639_2 = isoCode639_2 ;
    }

    /**
     * Returns Language by given ISO 639-2 code
     * @param isoCode639_2 language code
     * @return language mapped to the given code or null if the given code is not mapped to any language
     * @see <a href="http://en.wikipedia.org/wiki/List_of_ISO_639-2_codes">List_of_ISO_639-2_codes</a>
     */
    public static Language getLanguageByISO639_2( String isoCode639_2 ) {
        if (LanguageMapper.existsIsoCode639_2(isoCode639_2)) {
            return new Language( isoCode639_2 );
        } else {
            return null ;
        }
    }

    /**
     * Returns Language by given ISO 639-1 code
     * @param isoCode639_1 language code
     * @return language mapped to the given code or null if the given code is not mapped to any language
     * @see <a href="http://en.wikipedia.org/wiki/List_of_ISO_639-2_codes">List_of_ISO_639-2_codes</a>
     */
    public static Language getLanguageByISO639_1( String isoCode639_1 ) {
        try {
            return getLanguageByISO639_2(LanguageMapper.convert639_1to639_2(isoCode639_1)) ;
        } catch ( LanguageMapper.LanguageNotSupportedException e ) {
            return null ;
        }
    }

    /**
     * Returns a string representation of this object. In the form of "ISO 639-2: " + {@link Language#getIsoCode639_2()}
     * @return a string representation of this Language
     */
    public String toString() {
        return "ISO 639-2: "+isoCode639_2 ;
    }

    /**
     * Returns ISO 639-2 string of this Language
     * @return ISO 639-2 string of this Language
     */
    public String getIsoCode639_2() {
        return isoCode639_2;
    }

    /**
     * Returns ISO 639-1 code of this Language.
     * @return ISO 639-1 code of this Language or null if not found.
     */
    public String getIsoCode639_1() {
        try {
            return LanguageMapper.convert639_2to639_1(isoCode639_2) ;
        } catch ( LanguageMapper.LanguageNotSupportedException e ) {
            return null ;
        }
    }

}
