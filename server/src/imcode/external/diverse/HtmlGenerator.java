package imcode.external.diverse;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.server.* ;
import imcode.util.* ;

/**
 *  Description of the Class
 *
 *@author     kreiger
 *@created    den 30 augusti 2001
 */
public class HtmlGenerator {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

    File HTML_TEMPLATE;


    /**
     *  Constructor for the HtmlGenerator object
     *
     *@param  path  Description of Parameter
     *@param  file  Description of Parameter
     */
    public HtmlGenerator(File path, String file) {
        this(new File(path, file));
    }


    /**
     *  Constructor for the HtmlGenerator object
     *
     *@param  templateFile  Description of Parameter
     */
    public HtmlGenerator(File templateFile) {
        HTML_TEMPLATE = templateFile;
    }

    /**
     *  Constructor for the HtmlGenerator object
     */
    public HtmlGenerator() {
        HTML_TEMPLATE = null;
    }


    /**
     *  Gets the rowEmpty attribute of the HtmlGenerator object
     *
     *@param  str  Description of Parameter
     *@return      The rowEmpty value
     */
    public boolean isRowEmpty(String str) {
        StringManager aRow = new StringManager(str, ";");
        int nbrOfItems = aRow.getTotalItems();

        for (int i = 0; i < nbrOfItems; i++) {
            String aStr = aRow.getItem(i);
            //	System.out.println("En item är:" + aStr) ;
            if (!aStr.trim().equals("")) {
                return false;
            }
        }
        return true;
    }


    // ************************** CREATE HTML STRING *******************


    /**
     *  Opens a tableFile, creating a vector and returns a complete table
     *
     *@param  params     Description of Parameter
     *@param  dbPath     Description of Parameter
     *@param  tablePath  Description of Parameter
     *@return            Description of the Returned Value
     */

    public String createTable(Properties params, String dbPath, String tablePath) {

        // Lets get the fileName
        String metaId = params.getProperty("META_ID");
        MetaTranslator meta = new MetaTranslator(dbPath + "DIAGRAM_DB.INI");
        synchronized (meta) {
            meta.loadSettings();
        }

        String aTableFile = meta.getFileName("TABLE_DATA", metaId);
        if (aTableFile.equals("")) {
            String aMsg = "MetaID: " + metaId + " kunde inte hittas i databasen!";
            log(aMsg);
            return "";
        }

        // Ok, Lets get the values from the file into a vector

        ValueAccessor valAcc = new ValueAccessor(tablePath + aTableFile);
        valAcc.load();
        Vector v = valAcc.getAllValues();
        valAcc = null;
        if (v.isEmpty()) {
            String anMsg = "Ingen data i värdefilen: " + tablePath + aTableFile;
            this.log(anMsg);
            return "";
        }

        // Ok, lets generate a table from the vector

        int rowCount = v.size();
        StringManager strMan = new StringManager(v.get(0).toString(), "|");
        int columnCount = strMan.getTotalItems();

        // this.log("rows:"+ rowCount) ;
        // this.log("columnCount:"+ rowCount) ;

        //	String table = " " ;
        String aTableStr = generateTable(v, rowCount, columnCount);
        //	if(aTableStr.equals(""))
        //		aTableStr = "-" ;
        return aTableStr;
    }
    // End getTable


    /**
     *  Opens a tableFile and returns a string. Takes two path parameters. One
     *  is the path to the dbfile, the other is the path to the actual file
     *  containing the tableheader
     *
     *@param  params     Description of Parameter
     *@param  dbPath     Description of Parameter
     *@param  tablePath  Description of Parameter
     *@return            Description of the Returned Value
     */

    public String createTableHeader(Properties params, String dbPath, String tablePath) {

        String retStr = "";

        // Lets get the fileName
        String metaId = params.getProperty("META_ID");
        MetaTranslator meta = new MetaTranslator(dbPath + "DIAGRAM_DB.INI");
        synchronized (meta) {
            meta.loadSettings();
        }

        retStr += "MetaId: " + metaId + "\n";
        String aTablePrefsFile = meta.getFileName("TABLE_PREFS", metaId);
        if (aTablePrefsFile.equals("")) {
            String msg = "MetaID: " + metaId + " kunde inte hittas i databasen!";
            this.log(msg);
            return "";
        }

        //	retStr += "String + TablePrefsFile: " + path + aTablePrefsFile + "\n";

        // Ok, Lets get the values from the file into a vector
        SettingsAccessor mySetAcc = new SettingsAccessor( new File(tablePath,aTablePrefsFile));
        synchronized (mySetAcc) {
            mySetAcc.loadSettings();
        }

        String header = mySetAcc.getSetting("TABLEHEADER");
        retStr += "header: " + header;

        if (header == null) {
            header = " ";
        }
        this.log(retStr);
        //	return retStr ;
        return header;
    }
    // End getTableHeader


    /**
     *  This function is probably used in the diagramplugin. DONT USE it!!
     *
     *@param  src      Description of Parameter
     *@param  rows     Description of Parameter
     *@param  columns  Description of Parameter
     *@return          Description of the Returned Value
     */

    public String generateTable(Vector src, int rows, int columns) {

        //	this.log("Antal rader: " + rows) ;
        //	this.log("Antal kolumner: " + columns) ;

        // Lets adjust the nbr of columns, really strange why we'll have to do it...?
        columns += 1;

        String htmlStr = " ";
        htmlStr += "<TABLE BORDER=1 CELLSPACING=1 CELLPADDING=2 width=\"*\" align=\"center\">\n";
        String color = "";
        String alignStart = "";
        String alignStop = "";
        String fontStart = " font face=\"Arial,Helvetica\"><font size=2";
        String fontStop = "</font>";

        // For each row...
        for (int i = 0; i < rows; i++) {
            StringManager aRow = new StringManager(src.get(i).toString(), "|");
            //		this.log("aRow innehåller:" + aRow.toString()) ;

            // Lets verify if the row is empty
            if (this.isRowEmpty(aRow.toString()) != false) {
                htmlStr += "<TR>\n";
                // for each column
                for (int j = 1; j < columns; j++) {

                    double anDouble = (java.lang.Math.IEEEremainder(i, 2));
                    if (j == 1) {
                        alignStart = "<div align=\"left\"";
                    } else {
                        alignStart = "<div align=\"right\"";
                    }

                    alignStop = "</div>";
                    if (anDouble == 0) {
                        color = "bgcolor=\"#CCCCCC\"";
                    } else {
                        color = "bgcolor=\"#FFFFFF\"";
                    }

                    String aColumnItem = aRow.getItem(j).toString();
                    if (!aColumnItem.trim().equals("")) {
                        htmlStr += "<TD " + color + ">" + alignStart + fontStart + ">" +
                                aColumnItem + alignStop + fontStop + "</TD>\n";
                    }
                }
                htmlStr += "</TR>\n";
            }
            //else
            //		this.log("denna rad var tom: " + i) ;

        }
        htmlStr += "</TABLE>\n";
        return htmlStr;
    }


    /**
     *  Description of the Method
     *
     *@param  req                   Description of Parameter
     *@param  res                   Description of Parameter
     *@param  str                   Description of Parameter
     *@exception  ServletException  Description of Exception
     *@exception  IOException       Description of Exception
     */
    public void sendToBrowser(HttpServletRequest req, HttpServletResponse res, String str)
             throws ServletException, IOException {

        // Lets send settings to a browser
        PrintWriter out = res.getWriter();
        res.setContentType("Text/html");
        out.println(str);
    }


    /**
     *  Description of the Method
     *
     *@param  msg  Description of Parameter
     */
    public void log(String msg) {
        //	super.log(msg) ;
        System.out.println("HtmlGenerator: " + msg);
        //	saveLog(msg) ;
    }
    // End getRmiCaller


    /**
     *  Creates an html string from a Variable Manager object
     *
     *@param  vMan                  Description of Parameter
     *@param  req                   Description of Parameter
     *@return                       Description of the Returned Value
     *@exception  ServletException  Description of Exception
     *@exception  IOException       Description of Exception
     */

    public String createHtmlString(VariableManager vMan, HttpServletRequest req)
             throws ServletException, IOException {

        Vector htmlTags = vMan.getAllProps();
        Vector data = vMan.getAllValues();
        return createHtmlString(htmlTags, data, req);
    }
    // End of createHtmlString


    /**
     *  Creates an html string from a properties object This function is
     *  probably not in use at all, the vector data is for example never used in
     *  this code.
     *
     *@param  props                 Description of Parameter
     *@param  data                  Description of Parameter
     *@param  req                   Description of Parameter
     *@return                       Description of the Returned Value
     *@exception  ServletException  Description of Exception
     *@exception  IOException       Description of Exception
     */

    public String createHtmlString(Properties props, Vector data, HttpServletRequest req)
             throws ServletException, IOException {

        // String htmlFile = HTML_TEMPLATE ;
        // Lets convert the properties to 2 vectors
        Enumeration enumValues = props.elements();
        Enumeration enumKeys = props.keys();
        Vector propVector = new Vector();
        Vector valueVector = new Vector();

        while ((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
            Object oKeys = (enumKeys.nextElement());
            Object oValue = (enumValues.nextElement());
            //String aLine = new String(oKeys.toString() + "=" + oValue.toString());
            propVector.add(oKeys);
            valueVector.add(oValue);
        }
        return createHtmlString(propVector, valueVector, req);

    }
    // End of createHtmlString


    /**
     *  Creates an html string from 2 vector object
     *
     *@param  htmlTags              Description of Parameter
     *@param  data                  Description of Parameter
     *@param  req                   Description of Parameter
     *@return                       Description of the Returned Value
     *@exception  ServletException  Description of Exception
     *@exception  IOException       Description of Exception
     */

    public String createHtmlString(Vector htmlTags, Vector data, HttpServletRequest req)
             throws ServletException, IOException {

        // Lets validate the input data, if input data is not correct
        if (htmlTags.isEmpty() || data.isEmpty()) {
            String msg = "Not valid input to HtmlGenerator.createHtmlString";
            msg += "The HTML-File was " + HTML_TEMPLATE;
            return msg;
        }

        // Lets insert hertzmarks to the tags vector
        String tmp = new String();
        for (int i = 0; i < htmlTags.size(); i++) {
            tmp = htmlTags.elementAt(i).toString();
            tmp = "#" + tmp + "#";
            htmlTags.set(i, tmp);
        }

        // Lets get the html file into an string
        String srcHtml = ReadTextFile.getFile(HTML_TEMPLATE);

        // Lets check that we really got something from ReadTextFile, if the template is not
        // found for example
        if (srcHtml.equals("")) {
            String msg = "Nothing was returned from ReadTextFile. Template probably not found ";
            msg += "The html file was: " + HTML_TEMPLATE;
            return msg;
        }

        String host = req.getHeader("Host");
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterfaceByHost(host) ;
        String theHtml = imcref.parseDoc(srcHtml, htmlTags, data);
        return theHtml;
    }
    // End of createHtmlString


}
