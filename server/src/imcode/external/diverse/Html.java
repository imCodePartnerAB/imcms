/*
 *
 * @(#)Html.java
 *
 *
 * Copyright (c)
 *
 */

package imcode.external.diverse ;

import java.util.*;

/**
 * Takes care of creating Html code.
 *
 *
 * @version 1.1 23 Oct 2000
 */

public class Html {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
    
    
        /**
         * CreateHtml code, can mark up several values as selected
         */
    
    public static String createHtmlCode(String format, Vector selected, Vector data ) {
        String htmlStr = "" ;
        
        if (format.equals("ID_OPTION")) {
            for( int i = 0 ; i < data.size() ; i+=2) {
                htmlStr += "<option value=\"" + data.elementAt(i).toString() + "\"" ;
                if (selected.size() > 0) {
                    String lookFor = data.elementAt(i).toString() ;
                    for(int j=0; j< selected.size(); j++) {
                        if (lookFor.equals(selected.elementAt(j).toString())) htmlStr += " selected " ;
                    } // end for
                    htmlStr += ">" ;
                    htmlStr += data.elementAt(i+1).toString() + "\n" ;
                } else {
                    htmlStr += ">" ;
                    htmlStr += data.elementAt(i+1).toString() + "\n" ;
                } // end if
            } // end for
        } // end if
        
        
        
        
                /*
                                THIS CODE WILL GENERATE LIST WIHT A HREF REFERENCES
                // Lets prepare HTML code
                        Html ht = new Html() ;
                String optionList = ht.createHtmlCode("ID_OPTION", "", repliesV ) ;
                 
                // Lets generate a list with a hrefs links
                String aHrefList = "" ;
                String start = "<a href=\"" ;
                String middle1 = "\"" ;
                String middle2 = ">" ;
                String end = "</a><br>" ;
                String targetFrame = "ConfRepliesFrame";
                String targetStart = " target=\"_" + targetFrame ;
                String targetStop = "\" ";
                 
                for(int i = 0 ; i < repliesV.size(); i+=2) {
                        String argument = repliesV.elementAt(i).toString() ;
                        String visibleTxt = repliesV.elementAt(i+1).toString() ;
                //	String tmp = start + argument + middle1 + middle2 + visibleTxt + end ;
                                String tmp = start + argument + middle1 + targetStart + middle1+ middle2 +  visibleTxt + end ;
                 
                        aHrefList += tmp ;
                        log("Här är en aHref: " + tmp) ;
                }
                 */
        return htmlStr ;
    }
    
    
    
    
    
    
        /**
         * CreateHtml code. Generates the following HTML code snippets. Format should be
         * one of the arguments below.
         *
         * ID_OPTION	--> can only update one choice as selected,
         * A_HREF_LIST --> Returns a list with links NOT IMPLEMENTED YET
         */
    
    public static String createHtmlCode(String format, String selected, Vector data ) {
        String htmlStr = "" ;
		
		/*
		<select name="roomList">
<option value=" ">hgdj</option>
<option value=" ">htfs</option>

</select>*/
        
        if (format.equalsIgnoreCase("ID_OPTION")) {
            for( int i = 0 ; i < data.size() ; i+=2 ) {
                htmlStr += "<option value=\"" + data.elementAt(i).toString() + "\"" ;
                if (selected != null)
                    if (data.elementAt(i).toString().equals(selected))
                        htmlStr += " selected " ;
                
                htmlStr += ">" ;
                htmlStr += data.elementAt(i+1).toString() + "\n" ;
            }
            
            // A_HREF_LIST
        }	else if(format.equalsIgnoreCase("A_HREF_LIST")) {
            
            
            
        }
        return htmlStr ;
    }
	
	public static String createRadioButton(String buttonName,Vector data,String selected )
	{
		String htmlStr = "" ;

	/*
		<input type="radio" name="reload" value="1">
		<input type="radio" name="reload" value="2">
		<input type="radio" name="reload" value="3" checked>
	*/

		for( int i = 0 ; i < data.size() ; i++)
		{
			htmlStr += "<input type=\"radio\" name=\"" + buttonName + "\" value=\"" ;
			htmlStr += data.elementAt(i).toString() + "\"" ;
			if (selected != null)
			{
				if (data.elementAt(i).toString().equals(selected))	
				{
					htmlStr += " checked " ;
				}
			}	

			htmlStr += ">\n" ;
		}

		return htmlStr ;
	}
         /*
          
                 if (format.indexOf("TABLE") !=-1) {
                  int iNumCount = dbc.getColumnCount() ;
                         htmlStr += "<TABLE BORDER=1 width=\"75%\" align=\"center\">\n" ;
                          for( int i = 0 ; i < tableHeader.size() ; i++)
                            htmlStr += "<TH>" + tableHeader.elementAt(i).toString() ;
                           for( int i = 0 ; i < data.size() ; i+=iNumCount) {
                                htmlStr += "<TR>\n" ;
                                 for( int j = 0 ; j < iNumCount ; j++)
                                   htmlStr += "<TD>" + data.elementAt(i+j).toString() + "</TD>\n" ;
                                   htmlStr += "</TR>\n" ;
                          }
                        htmlStr += "</TABLE>\n" ;
                 }
          
          
                if (format.indexOf("CHECKBOX_LIST") !=-1) {
                  int iNumCount = dbc.getColumnCount() ;
                         htmlStr += "<TABLE BORDER=1 width=\"75%\" align=\"center\">\n" ;
          
                          htmlStr += "<TH>&nbsp;" ;
                          for( int i = 0 ; i < tableHeader.size() ; i++)
                            htmlStr += "<TH>" + tableHeader.elementAt(i).toString() ;
                            int counter = 1 ;
                           for( int i = 0 ; i < data.size() ; i+=iNumCount) {
                                htmlStr += "<TR><TD>\n" ;
                                htmlStr += "<input type=\"checkbox\" name=\"id" + counter++  + "\"" ;
                                htmlStr += " value=\"" + data.elementAt(i).toString() + "\"></TD>";
                                 for( int j = 0 ; j < iNumCount ; j++)
                                   htmlStr += "<TD>" + data.elementAt(i+j).toString().trim() + "</TD>\n" ;
                                   htmlStr += "</TR>\n" ;
                          }
                        htmlStr += "</TABLE>\n" ;
                 }
          
          
                if (format.indexOf("CLICK_LIST") !=-1) {
                  int iNumCount = dbc.getColumnCount() ;
                         htmlStr += "<TABLE BORDER=1 width=\"75%\" align=\"center\">\n" ;
          
                          htmlStr += "<TH>&nbsp;" ;
                          for( int i = 0 ; i < tableHeader.size() ; i++)
                            htmlStr += "<TH>" + tableHeader.elementAt(i).toString() ;
          
                           for( int i = 0 ; i < data.size() ; i+=iNumCount) {
                                htmlStr += "<TR><TD>\n" ;
                                htmlStr += "<a href=\"/servlet/GetUser?user_id=" + data.elementAt(i).toString() + "\">" ;
                                htmlStr += "Ändra</A>"	;
                                htmlStr += "</TD>";
                                 for( int j = 0 ; j < iNumCount ; j++)
                                   htmlStr += "<TD>" + data.elementAt(i+j).toString().trim() + "</TD>\n" ;
                                   htmlStr += "</TR>\n" ;
                          }
                        htmlStr += "</TABLE>\n" ;
                 }
          
                 dbc.clearResultSet() ;
                 dbc.closeConnection() ;
                 dbc = null ;
                 return htmlStr ;
         }
          
          */
    
    
        /**
         * creats list of options, no selected options.
         * @param options must be in order name, value.
         */
    public String createListOfOptions( String[][] options ) {
        
        StringBuffer optionList = new StringBuffer();
        
        for ( int i = 0 ; i < options.length ; i++ ) {
            boolean selected = options[i].length == 3;
            
            optionList.append( createOption( options[i][0], options[i][1], selected ) );
        }
        
        return optionList.toString();
    }
    
        /**
         * creats list of options.
         * @param options must be in order value, name.
         * @param selected
         */
    public String createListOfOptions( String[][] options, boolean selected ) {
        
        StringBuffer optionList = new StringBuffer();
        
        for ( int i = 0 ; i < options.length ; i++ ) {
            
            optionList.append( createOption( options[i][0], options[i][1], selected ) );
        }
        
        return optionList.toString();
    }
    
    
        /**
         * creats option.
         * @param elementValue - option value
         * @param elementValue - option string
         * @param selected - true or falsee
         */
    public String createOption( String elementValue, String elementName, boolean selected ) {
        StringBuffer option = new StringBuffer();
        
        option.append( "<option value=\"" + elementValue + "\"" );
        if ( selected ) {
            option.append( " selected" );
        }
        option.append( ">" + elementName + "</option>");
        
        return option.toString();
    }
    
/**
 * Loops throug a vector and looks out for a character and replaces this
 * character to a string .
 **/
    public static StringBuffer replace (StringBuffer strBuff, char lookFor, String replacement) {
        for( int i = 0 ; i < strBuff.length(); i++ ) {
            char aChar = strBuff.charAt(i) ;
            if('\n' == aChar) {
                strBuff = strBuff.replace(i,i,replacement) ;
                i+=replacement.length() ;
            }
        }
        
        return strBuff ;
    } // End of replace
    
/**
 * Loops throug a vector and looks out for a character and replaces this
 * character to a string .
 *
 * public static Vector replace(Vector v, char lookFor, String replacement) {
 * StringBuffer strBuff = new StringBuffer(v.toString()) ;
 * strBuff = replace(strBuff, lookFor, replacement) ;
 * Vector returnV = new Vector() ;
 * for( int i = 0 ; i < strBuff.length(); i++ ) {
 * returnV.add(""+ strBuff.charAt(i), i);
 * }
 *
 * return returnV ;
 * } // End of replace
 **/
    
} // End of class