package imcode.server;

import java.util.StringTokenizer;

public class HTMLConv {

    private final static String[] h_code = {"&nbsp;", "&iexcl;", "&cent;", "&pound;", "&curren;", "&yen;", "",
                                            "&sect;", "&uml;", "&copy;", "&ordf;", "&laquo;", "&not;", "&shy;",
                                            "&reg;", "&macr;", "&deg;", "&plusmn;", "", "",
                                            "&acute;", "&micro;", "&para;", "&middot;", "&cedil;", "",
                                            "&ordm;", "&raquo;", "", "", "",
                                            "&iquest;", "&Agrave;", "&Aacute;", "&Acirc;", "&Atilde;", "&Auml;",
                                            "&Aring;", "&AElig;", "&Ccedil;", "&Egrave;", "&Eacute;", "&Ecirc;",
                                            "&Euml;", "&Igrave;", "&Iacute;", "&Icirc;", "&Iuml;", "",
                                            "&Ntilde;", "&Ograve;", "&Oacute;", "&Ocirc;", "&Otilde;", "&Ouml;", "",
                                            "&Oslash;", "&Ugrave;", "&Uacute;", "&Ucirc;", "&Uuml;", "", "",
                                            "&szlig;", "&agrave;", "&aacute;", "&acirc;", "&atilde;", "&auml;", "&aring;",
                                            "&aelig;", "&ccedil;", "&egrave;", "&eacute;", "&ecirc;", "&euml;",
                                            "&igrave;", "&iacute;", "&icirc;", "&iuml;", "",
                                            "&ntilde;", "&ograve;", "&oacute;", "&ocirc;", "&otilde;", "&ouml;",
                                            "&divide;", "&oslash;", "&ugrave;", "&uacute;", "&ucirc;", "&uuml;", "", "",
                                            "&yuml;"};



    /* Används ej:
       &brvbar;	&#166;	broken (vertical) bar	NA
       &sup2;	&#178;	superscript two	NA
       &sup3;	&#179;	superscript three	NA
       &sup1;	&#185;	superscript one	NA
       &frac14;	&#188;	fraction one-quarter	NA
       &frac12;	&#189;	fraction one-half	NA
       &frac34;	&#190;	fraction three-quarters	NA
       &ETH;	&#208;	capital Eth, Icelandic	NA
       &times;	&#215;	multiply sign	x
       &Yacute;	&#221;	capital Y, acute accent	´Y
       &THORN;	&#222;	capital THORN, Icelandic	NA
       &eth;	&#240;	small eth, Icelandic	NA
       &yacute;	&#253;	small y, acute accent	´y
       &thorn;	&#254;	small thorn, Icelandic	NA
    */


    private HTMLConv() {

    }

    private static String toHTML( char ch ) {
        if ( (int)ch > 159 ) {
            return h_code[(int)ch - 160];
        } else if ( ch == '\'' ) {
            return "''";
        } else {
            return "" + ch;
        }
    }

    public static String toHTML( String str ) {
        StringBuffer htmlStr = new StringBuffer();
        for ( int i = 0; i < str.length(); ++i ) {
            htmlStr.append( toHTML( str.charAt( i ) ) );
        }
        return htmlStr.toString();
    }

    public static String toHTMLSpecial( String string ) {

        StringTokenizer stok = new StringTokenizer( string, "\n&<>\"'", true );
        StringBuffer result = new StringBuffer();

        while ( stok.hasMoreTokens() ) {
            String token = stok.nextToken();
            if ( token.length() == 1 ) {
                char ch = token.charAt( 0 );
                switch ( ch ) {
                    default    :
                        token = "" + ch;
                        break;
                    case '\n':
                        token = "\n<BR>";
                        break;
                    case '&':
                        token = "&amp;";
                        break;
                    case '>':
                        token = "&gt;";
                        break;
                    case '<':
                        token = "&lt;";
                        break;
                    case '\"':
                        token = "&quot;";
                        break;
                    case '\'':
                        token = "&apos;";
                        break;
                }
            }
            result.append( token );
        }

        return result.toString();
    }

}
