/*
 *
 * @(#)Html.java
 *
 *
 * Copyright (c)
 *
 */

package imcode.external.diverse;

import java.util.*;

/**
 * Takes care of creating Html code.
 * 
 * @version 1.1 23 Oct 2000
 */

public class Html {
    private final static String CVS_REV = "$Revision$";
    private final static String CVS_DATE = "$Date$";


    /**
     * CreateHtml code, can mark up several values as selected
     */

    public static String createHtmlCode(String format, List selected, List data) {
        String htmlStr = "";

        if (format.equals("ID_OPTION")) {
            for (int i = 0; i < data.size(); i += 2) {
                htmlStr += "<option value=\"" + data.get(i).toString() + "\"";
                if (selected.size() > 0) {
                    String lookFor = data.get(i).toString();
                    for (int j = 0; j < selected.size(); j++) {
                        if (lookFor.equals(selected.get(j).toString())) {
                            htmlStr += " selected ";
                        }
                    } // end for
                    htmlStr += ">";
                    htmlStr += data.get(i + 1).toString() + "</option>\n";
                } else {
                    htmlStr += ">";
                    htmlStr += data.get(i + 1).toString() + "</option>\n";
                } // end if
            } // end for
        } // end if

        return htmlStr;
    }


    /**
     * CreateHtml code. Generates the following HTML code snippets. Format should be
     * one of the arguments below.
     * <p/>
     * ID_OPTION	--> can only update one choice as selected,
     * A_HREF_LIST --> Returns a list with links NOT IMPLEMENTED YET
     */
    public static String createHtmlCode(String format, String selected, Vector data) {
        String htmlStr = "";
		
        if (format.equalsIgnoreCase("ID_OPTION")) {
            for (int i = 0; i < data.size(); i += 2) {
                htmlStr += "<option value=\"" + data.elementAt(i).toString() + "\"";
                if (selected != null) {
                    if (data.elementAt(i).toString().equals(selected)) {
                        htmlStr += " selected ";
                    }
                }

                htmlStr += ">";
                htmlStr += data.elementAt(i + 1).toString() + "</option>\n";
            }
            
            // A_HREF_LIST
        } else if (format.equalsIgnoreCase("A_HREF_LIST")) {


        }
        return htmlStr;
    }

    public static String createRadioButton(String buttonName, Vector data, String selected) {
        String htmlStr = "";
        for (int i = 0; i < data.size(); i++) {
            htmlStr += "<input type=\"radio\" name=\"" + buttonName + "\" value=\"";
            htmlStr += data.elementAt(i).toString() + "\"";
            if (selected != null) {
                if (data.elementAt(i).toString().equals(selected)) {
                    htmlStr += " checked ";
                }
            }

            htmlStr += ">\n";
        }

        return htmlStr;
    }

    
    /**
     * creats list of options, no selected options.
     * 
     * @param options must be in order name, value.
     */
    public String createListOfOptions(String[][] options) {

        StringBuffer optionList = new StringBuffer();

        for (int i = 0; i < options.length; i++) {
            boolean selected = options[i].length == 3;

            optionList.append(createOption(options[i][0], options[i][1], selected));
        }

        return optionList.toString();
    }

    /**
     * creats list of options.
     * 
     * @param options  must be in order value, name.
     * @param selected 
     */
    public String createListOfOptions(String[][] options, boolean selected) {

        StringBuffer optionList = new StringBuffer();

        for (int i = 0; i < options.length; i++) {

            optionList.append(createOption(options[i][0], options[i][1], selected));
        }

        return optionList.toString();
    }


    /**
     * creats option.
     * 
     * @param elementValue - option value
     * @param elementValue - option string
     * @param selected     - true or falsee
     */
    public String createOption(String elementValue, String elementName, boolean selected) {
        StringBuffer option = new StringBuffer();

        option.append("<option value=\"" + elementValue + "\"");
        if (selected) {
            option.append(" selected");
        }
        option.append(">" + elementName + "</option>");

        return option.toString();
    }

    /**
     * Loops throug a vector and looks out for a character and replaces this
     * character to a string .
     */
    public static StringBuffer replace(StringBuffer strBuff, char lookFor, String replacement) {
        for (int i = 0; i < strBuff.length(); i++) {
            char aChar = strBuff.charAt(i);
            if ('\n' == aChar) {
                strBuff = strBuff.replace(i, i, replacement);
                i += replacement.length();
            }
        }

        return strBuff;
    } // End of replace
    

} // End of class