package imcode.util.jscalendar;

import javax.servlet.http.HttpServletRequest;

public class JSCalendar {
    public String dateFieldId ;
    public String timeFieldId ;
    public String langIso639_2 ;
    public HttpServletRequest request ;

    private static final String PATH_CALENDAR = "/imcms/jscalendar/" ;

    public JSCalendar( String langIso639_2, HttpServletRequest request ) {
        this.langIso639_2 = langIso639_2 ;
        this.request     = request ;
    }
    public JSCalendar getInstance( String dateFieldId, String timeFieldId ) {
        this.dateFieldId = dateFieldId ;
        this.timeFieldId = timeFieldId ;
        return this ;
    }
    public JSCalendar getInstance( String dateFieldId ) {
        this.dateFieldId = dateFieldId ;
        this.timeFieldId = null ;
        return this ;
    }
    public String getHeadTagScripts() {
        String scripts =
            "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"" + request.getContextPath() + PATH_CALENDAR + "skins/aqua/theme.css.jsp\" />\n" +
            "<script type=\"text/javascript\" src=\"" + request.getContextPath() + PATH_CALENDAR + "calendar.js\"></script>\n" +
            "<script type=\"text/javascript\" src=\"" + request.getContextPath() + PATH_CALENDAR + "lang/calendar-" + langIso639_2 + ".js\"></script>\n" +
            "<script type=\"text/javascript\" src=\"" + request.getContextPath() + PATH_CALENDAR + "calendar-setup.js\"></script>\n"  ;
        return scripts ;
    }
    public String getButton(String title) {

        String calenderTitle = title;
        String button =
            "<img" +
            " src=\"" + request.getContextPath() + PATH_CALENDAR + "images/img.gif\"" +
            " id=\"" + dateFieldId + "_btn\"" +
            " style=\"cursor: pointer; cursor: hand;\"" +
            " onmouseover=\"this.style.background='#000099';\"" +
            " onmouseout=\"this.style.background=''\"" +
            " title=\"" + calenderTitle + "\" />" ;

        button += "<script type=\"text/javascript\">\n" +
        "Calendar.setup({\n" +
        "	inputField   : \"" + dateFieldId + "\",\n" ;
        if (timeFieldId != null && timeFieldId.length() > 0) {
            button +=
        "	inputFieldTime : \"" + timeFieldId + "\",\n" +
        "	showsTime   : true,\n" ;
        }
        button +=
        "	button     : \"" + dateFieldId + "_btn\"\n" +
        "}) ;\n" +
        "</script>"  ;

        return button ;
    }
}


