package imcode.server ;


class Util {
	private final static String CVS_REV = "$Revision" ;
	private final static String CVS_DATE = "$Date$" ;

	public static String convertDate(String date_str,boolean max) {
		String temp_str = "" ;
		for(int i = 0 ; i < date_str.length() ; i++) {

			switch(date_str.charAt(i)) {
			case '-': 
				break;
			case ':':
				break;
			case ' ':
				break;
			case '.':
				break;
			default: temp_str += date_str.charAt(i) ;
				break;
			}
		}

		if (temp_str.length() > 0)
			return temp_str ;
		else {

			if (max)
				return "999912312359" ;
			else
				return "00000000000";

		}

	}

}
