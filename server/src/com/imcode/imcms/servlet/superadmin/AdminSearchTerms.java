package com.imcode.imcms.servlet.superadmin;

import com.imcode.db.Database;
import com.imcode.db.DatabaseCommand;
import com.imcode.db.commands.SqlQueryDatabaseCommand;
import com.imcode.db.handlers.CollectionHandler;
import com.imcode.db.handlers.RowTransformer;
import com.imcode.imcms.flow.OkCancelPage;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminSearchTerms extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        if ( !user.isSuperAdmin() ) {
            Utility.forwardToLogin(request, response);
            return;
        }

        doView(null, null, false, request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        if ( !user.isSuperAdmin() ) {
            Utility.forwardToLogin(request, response);
            return;
        }

        if (null != request.getParameter(OkCancelPage.REQUEST_PARAMETER__CANCEL)) {
            response.sendRedirect(request.getContextPath()+"/servlet/AdminManager");
            return;
        }
        Date fromDate = getDateParameter(request, "from_date");
        Date toDate = Utility.addDate(getDateParameter(request, "to_date"), 1);
        doView(fromDate, toDate, true, request, response);
    }

    private Date getDateParameter(HttpServletRequest request, String parameterName) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String parameterValue = request.getParameter(parameterName);
        Date date = null;
        try {
            date = dateFormat.parse(parameterValue);
        } catch ( ParseException e ) {

        }
        return date;
    }

    private List<TermCount> getTermCounts(Date fromDate, Date toDate) {
        Database database = Imcms.getServices().getDatabase();
        List<String> whereClauses = new ArrayList<String>();
        List<Date> parameters = new ArrayList<Date>();
        if (null != fromDate) {
            whereClauses.add("datetime >= ?");
            parameters.add(new Timestamp(fromDate.getTime()));
        }
        if (null != toDate) {
            whereClauses.add("datetime < ?");
            parameters.add(new Timestamp(toDate.getTime()));
        }
        String whereClausesString = whereClauses.isEmpty() ? "" : " WHERE "+StringUtils.join(whereClauses.iterator(), " AND ");
        DatabaseCommand queryCommand = new SqlQueryDatabaseCommand("SELECT term, COUNT(term) c FROM document_search_log"+whereClausesString+
                                                                   " GROUP BY term ORDER BY c DESC, term", 
                                                                   parameters.toArray(new Object[parameters.size()]),
                                                                   new CollectionHandler(new ArrayList(), new TermCountFactory()));
        return (List<TermCount>) database.execute(queryCommand);
    }

    private void doView(Date fromDate, Date toDate, boolean search, HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException {
        if (search) {
            request.setAttribute("fromDate", fromDate);
            request.setAttribute("toDate", Utility.addDate(toDate,-1));
            request.setAttribute("termCounts", getTermCounts(fromDate, toDate));
        }
        request.getRequestDispatcher( "/WEB-INF/jsp/imcms/document_search_terms.jsp" ).forward( request, response );
    }

    public static class TermCount {
        private String term;
        private int count;

        public TermCount(String term, int count) {
            this.term = term;
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public String getTerm() {
            return term;
        }
    }

    private static class TermCountFactory implements RowTransformer {

        public Object createObjectFromResultSetRow(ResultSet resultSet) throws SQLException {
            return new TermCount(resultSet.getString(1), resultSet.getInt(2));
        }

        public Class getClassOfCreatedObjects() {
            return TermCount.class;
        }
    }
}
