package imcode.server ;

public class HTMLConv {
	private final static String CVS_REV="$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	final static String h_code[] = { "&nbsp;","&iexcl;","&cent;","&pound;","&curren;","&yen;","",
											"&sect;","&uml;","&copy;","&ordf;","&laquo;","&not;","",
											"&reg;","&macr;","&deg;","&plusmn;","","",
											"&acute;","&micro;","&para;","&middot;","&cedil;","",
											"&ordm;","&raquo;","","","",
											"&iquest;","&Agrave;","&Aacute;","&Acirc;","&Atilde;","&Auml;",
											"&Aring;","&AElig;","&Ccedil;","&Egrave;","&Eacute;","&Ecirc;",
											"&Euml;","&Igrave;","&Iacute;","&Icirc;","&Iuml;","",
										  "&Ntilde;","&Ograve;","&Oacute;","&Ocirc;","&Otilde;","&Ouml;","",
										  "&Oslash;","&Ugrave;","&Uacute;","&Ucirc;","&Uuml;","","",
										  "&szlig;","&agrave;","&aacute;","&acirc;","&atilde;","&auml;","&aring;",
										  "&aelig;","&ccedil;","&egrave;","&eacute;","&ecirc;","&euml;",
										  "&igrave;","&iacute;","&icirc;","&iuml;","",
										  "&ntilde;","&ograve;","&oacute;","&ocirc;","&otilde;","&ouml;",
										  "&divide;","&oslash;","&ugrave;","&uacute;","&ucirc;","&uuml;","","",
										  "&yuml;" } ;


	/* Används ej:
	&brvbar;	&#166;	broken (vertical) bar	NA
	&shy;	&#173;	soft hyphen	-
	&sup2;	&#178;	superscript two	NA
	&sup3;	&#179;	superscript three	NA
	&sup1;	&#185;	superscript one	NA
	&frac14;	&#188;	fraction one-quarter	NA
	&frac12;	&#189;	fraction one-half	NA
	&frac34;	&#190;	fraction three-quarters	NA
	&ETH;	&#208;	capital Eth, Icelandic	NA
	&times;	&#215;	multiply sign	x	
	&Yacute;	&#221;	capital Y, acute accent	´Y
	&THORN;	&#222;	capital THORN, Icelandic 	NA
	&eth;	&#240;	small eth, Icelandic	NA
	&yacute;	&#253;	small y, acute accent	´y
	&thorn;	&#254;	small thorn, Icelandic	NA
	*/





	private HTMLConv() {

	}


	public static String addBR(char ch) {
		String htmlStr = "" ;

		if ((int)ch == 10)
			htmlStr += "<BR>" ; 

		return htmlStr ;
	}	


	public static String addBR(String str) {
		String htmlStr = "" ;

		for(int i = 0 ; i < str.length() ; i++)
			htmlStr += HTMLConv.addBR(str.charAt(i)) ;


		return htmlStr ;
	}	



	public static String removeBR(String str) {
		String newStr = "" ;
		int startPos = 0 ;
		int endPos = 0 ;

			endPos = str.indexOf("<BR>") ;
		while(endPos != -1) {
			newStr += str.substring(startPos,endPos) ;
			startPos = endPos + 4 ;
			endPos = str.indexOf("<BR>",startPos) ;
		}
		newStr += str.substring(startPos,str.length()) ;

		return newStr ;
	}



	public static String toHTML(char ch) {
		String htmlStr = "" ;

		if ((int)ch > 159) {
			htmlStr += h_code[(int)ch - 160] ;	
		} else if (ch == '\'')
			htmlStr += "''" ; 
		else
			htmlStr += ch ;


		return htmlStr ;
	}


	public static String toHTML(String str) {
		String htmlStr = "" ;
		int htmlStart = str.indexOf("<!--HTML-->") ;
		int htmlStop  = str.indexOf("<!--/HTML-->") ;

		if (htmlStart != -1) {
			for(int i = 0 ; i < htmlStart  ; i++)
				htmlStr += HTMLConv.toHTML(str.charAt(i)) ;

				htmlStr += str.substring(htmlStart,htmlStop + 12) ;

			for(int i = htmlStop + 13  ; i < str.length() ; i++)
				htmlStr += HTMLConv.toHTML(str.charAt(i)) ;
		} else
			for(int i = 0  ; i < str.length() ; i++)
				htmlStr += HTMLConv.toHTML(str.charAt(i)) ;

			return htmlStr ;
	}	


	public static String toHTMLSpecial(char ch) {
		String htmlStr = "" ;

		switch(ch) {
		case '\n'  : htmlStr += "\n<BR>" ; break ;
		case '&'   : htmlStr += "&amp;"  ; break ;
		case '>'   : htmlStr += "&gt;"   ; break ;
		case '<'   : htmlStr += "&lt;"   ; break ;
		case '\"'  : htmlStr += "&quot;" ; break ;
		default    : htmlStr += ch       ; break ;

		}

		return htmlStr ;
	}	


	public static String toHTMLSpecial(String str) {
		String htmlStr = "" ;
		int htmlStart = str.indexOf("<!--HTML-->") ;
		int htmlStop  = str.indexOf("<!--/HTML-->") ;

		if (htmlStart != -1) {
			for(int i = 0 ; i < htmlStart  ; i++)
				htmlStr += HTMLConv.toHTMLSpecial(str.charAt(i)) ;

				htmlStr += str.substring(htmlStart,htmlStop + 12) ;

			for(int i = htmlStop + 13  ; i < str.length() ; i++)
				htmlStr += HTMLConv.toHTMLSpecial(str.charAt(i)) ;
		} else
			for(int i = 0  ; i < str.length() ; i++)
				htmlStr += HTMLConv.toHTMLSpecial(str.charAt(i)) ;


			return htmlStr ;

	}	



	public static String to2quote(char ch) {
		String htmlStr = "" ;

		if (ch == '\'')
			htmlStr += "''" ; 
		else
			htmlStr += ch ;

		return htmlStr ;
	}	


	public static String to2quote(String str) {
		String htmlStr = "" ;

		for(int i = 0 ; i < str.length() ; i++)
			htmlStr += HTMLConv.to2quote(str.charAt(i)) ;


		return htmlStr ;
	}	



}
	
	
