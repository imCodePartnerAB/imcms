package com.imcode.imcms.servlet.admin;

/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-23
 * Time: 16:19:25
 */

import imcode.server.document.*;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserAndRoleMapper;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SaveDocument extends HttpServlet {

    private static final String DOCINFO_PAGE = "/imcms/swe/jsp/docinfo.jsp";

    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        int meta_id = Integer.parseInt( request.getParameter("meta_id") );
        final IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
        final DocumentMapper documentMapper = service.getDocumentMapper();

        DocumentDomainObject document = documentMapper.getDocument( meta_id );

        String headline = request.getParameter( "meta_headline" );
        document.setHeadline( headline );

        String menuText = request.getParameter( "meta_text" );
        document.setMenuText( menuText );

        String imageUrl = request.getParameter( "meta_image" );
        document.setImage( imageUrl );

        SimpleDateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING );
        SimpleDateFormat timeFormat = new SimpleDateFormat( DateConstants.TIME_FORMAT_NO_SECONDS_STRING );

        Date activatedDatetime = parseDatetimeParameters( request, "activated_date", "activated_time", dateFormat, timeFormat );
        Date archivedDatetime = parseDatetimeParameters( request, "archived_date", "archived_time", dateFormat, timeFormat );

        document.setActivatedDatetime( activatedDatetime );
        document.setArchivedDatetime( archivedDatetime );

        document.removeAllSections() ;
        String[] sectionIds = request.getParameterValues("change_section") ;
        for ( int i = 0; i < sectionIds.length; i++ ) {
            int sectionId = Integer.parseInt(sectionIds[i]);
            SectionDomainObject section = documentMapper.getSectionById(sectionId);
            document.addSection( section );
        }

        String languageIso639_2 = request.getParameter("lang_prefix") ;
        document.setLanguageIso639_2( languageIso639_2 );

        document.removeAllCategories();
        String[] categoryIds = request.getParameterValues( "categories" ) ;
        for ( int i = 0; i < categoryIds.length; i++ ) {
            try {
                int categoryId = Integer.parseInt(categoryIds[i]);
                CategoryDomainObject category = documentMapper.getCategoryById( categoryId ) ;
                document.addCategory( category ) ;
            } catch (NumberFormatException ignored) {
                // OK, empty category id
            }
        }

        boolean visibleInMenuForUnauthorizedUsers = "1".equals(request.getParameter( "show_meta" )) ;
        document.setVisibleInMenuForUnauthorizedUsers( visibleInMenuForUnauthorizedUsers );

        boolean linkableByOtherUsers = "1".equals(request.getParameter( "shared" )) ;
        document.setLinkableByOtherUsers( linkableByOtherUsers );

        String keywordsString = request.getParameter( "classification" ) ;
        String[] keywords = keywordsString.split( "\\W+" ) ;
        document.setKeywords( keywords );

        boolean searchDisabled = "1".equals( request.getParameter( "disable_search" )) ;
        document.setSearchDisabled( searchDisabled );

        String[] possibleTargets = request.getParameterValues( "target" ) ;
        String target = null ;
        for ( int i = 0; i < possibleTargets.length; i++ ) {
            target = possibleTargets[i];
            boolean targetIsPredefinedTarget
                    =  "_self".equalsIgnoreCase( target)
                    || "_blank".equalsIgnoreCase( target)
                    || "_top".equalsIgnoreCase( target);
            if (targetIsPredefinedTarget) {
                break ;
            }
        }
        document.setTarget( target );

        Date createdDatetime = parseDatetimeParameters( request, "date_created", "created_time", dateFormat,timeFormat) ;
        Date modifiedDatetime = parseDatetimeParameters( request, "date_modified", "modified_time", dateFormat,timeFormat) ;
        document.setCreatedDatetime( createdDatetime );
        document.setModifiedDatetime( modifiedDatetime );

        UserDomainObject publisher = null ;
        try {
            int publisherId = Integer.parseInt(request.getParameter( "publisher_id" )) ;
            ImcmsAuthenticatorAndUserMapper userAndRoleMapper = service.getImcmsAuthenticatorAndUserAndRoleMapper() ;
            publisher = userAndRoleMapper.getUser( publisherId ) ;
        } catch( NumberFormatException ignored ) {
            // OK, no publisher
        }
        document.setPublisher( publisher );

        try {
            documentMapper.saveDocument( document );
        } catch ( MaxCategoryDomainObjectsOfTypeExceededException e ) {
            throw new RuntimeException( e ) ;
        }

        request.getRequestDispatcher( DOCINFO_PAGE ).forward( request, response ) ;
    }

    static Date parseDatetimeParameters( HttpServletRequest req, final String dateParameterName,
                                      final String timeParameterName, DateFormat dateformat,
                                      DateFormat timeformat ) {
        String dateStr = req.getParameter( dateParameterName );
        String timeStr = req.getParameter( timeParameterName );

        Date date = null;
        try {
            date = dateformat.parse( dateStr );
        } catch ( Exception e ) {
            return null ;
        }

        Date time = null;
        try {
            timeformat.setTimeZone( TimeZone.getTimeZone( "GMT" ));
            time = timeformat.parse( timeStr );
        } catch ( Exception e ) {
            return date ;
        }

        Calendar calendar = Calendar.getInstance() ;
        calendar.setTime( date );
        calendar.add(Calendar.MILLISECOND, (int)time.getTime()) ;
        return calendar.getTime();
    }
}