package imcode.util;

import com.imcode.imcms.servlet.admin.AdminDoc;
import imcode.server.ApplicationServer;
import imcode.server.WebAppGlobalConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.user.UserDomainObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;

public class Utility {

    private Utility() {

    }

    /**
     * Takes a path-string and returns a file. The path is prepended with the webapp dir if the path is relative.
     */
    public static File getAbsolutePathFromString( String pathString ) {
        File path = new File( pathString );
        if ( !path.isAbsolute() ) {
            path = new File( imcode.server.WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath(), pathString );
        }
        return path;
    }

    /**
     * Transforms a long containing an ip into a String.
     */
    public static String ipLongToString( long ip ) {
        return ( ( ip >>> 24 ) & 255 ) + "." + ( ( ip >>> 16 ) & 255 ) + "." + ( ( ip >>> 8 ) & 255 ) + "."
               + ( ip & 255 );
    }

    /**
     * Transforms a String containing an ip into a long.
     */
    public static long ipStringToLong( String ip ) {
        long ipInt = 0;
        StringTokenizer ipTok = new StringTokenizer( ip, "." );
        for ( int exp = 3; ipTok.hasMoreTokens(); --exp ) {
            int ipNum = Integer.parseInt( ipTok.nextToken() );
            ipInt += ( ipNum * Math.pow( 256, exp ) );
        }
        return ipInt;
    }

    /**
     * Make a HttpServletResponse non-cacheable
     */
    public static void setNoCache( HttpServletResponse res ) {
        res.setHeader( "Cache-Control", "no-cache; must-revalidate;" );
        res.setHeader( "Pragma", "no-cache;" );
    }

    public static UserDomainObject getLoggedOnUser( HttpServletRequest req ) {
        HttpSession session = req.getSession( true );
        UserDomainObject user = (UserDomainObject)session.getAttribute( WebAppGlobalConstants.LOGGED_IN_USER );
        return user;
    }

    public static int compareDatesWithNullFirst( Date date1, Date date2 ) {
        if ( null == date1 && null == date2 ) {
            return 0;
        } else if ( null == date1 ) {
            return -1;
        } else if ( null == date2 ) {
            return +1;
        } else {
            return date1.compareTo( date2 );
        }
    }

    public static void setDefaultHtmlContentType( HttpServletResponse res ) {
        res.setContentType( "text/html; charset=" + WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252 );
    }

    public static void redirectToStartDocument( HttpServletRequest req, HttpServletResponse res ) throws IOException {
        res.sendRedirect( req.getContextPath() + "/servlet/StartDoc" );
    }

    public static String getLinkedStatusIconTemplate( DocumentDomainObject document, UserDomainObject user ) {
        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
        String statusIconTemplate = documentMapper.getStatusIconTemplate( document, user );
        if ( documentMapper.userHasMoreThanReadPermissionOnDocument( user, document ) ) {
            statusIconTemplate = "<a href=\"AdminDoc?meta_id=" + document.getId() + "&"
                                 + AdminDoc.PARAMETER__DISPATCH_FLAGS
                                 + "=1\">" +
                                 statusIconTemplate +
                                 "</a>";
        }
        return statusIconTemplate;
    }

    public static boolean isValidEmail( String email ) {
        return Pattern.compile( "\\w+@\\w+" ).matcher( email ).find();
    }

    public static void removeNullValuesFromMap( Map map ) {
        Collection documentIds = map.values();
        for ( Iterator iterator = documentIds.iterator(); iterator.hasNext(); ) {
            if ( null == iterator.next() ) {
                iterator.remove();
            }
        }
    }

    public static String getImageTag( ImageDomainObject image ) {
        StringBuffer imageTagBuffer = new StringBuffer( 96 );
        if ( !"".equals( image.getUrl() ) ) {

            if ( StringUtils.isNotBlank( image.getLinkUrl() ) ) {
                imageTagBuffer.append( "<a href=\"" ).append( StringEscapeUtils.escapeHtml( image.getLinkUrl() ) ).append( "\"" );
                if ( !"".equals( image.getTarget() ) ) {
                    imageTagBuffer.append( " target=\"" ).append( StringEscapeUtils.escapeHtml( image.getTarget() ) ).append( "\"" );
                }
                imageTagBuffer.append( '>' );
            }

            String imageUrl = ApplicationServer.getIMCServiceInterface().getConfig().getImageUrl() + image.getUrl();

            imageTagBuffer.append( "<img src=\"" + StringEscapeUtils.escapeHtml( imageUrl ) + "\"" ); // FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
            if ( 0 != image.getWidth() ) {
                imageTagBuffer.append( " width=\"" + image.getWidth() + "\"" );
            }
            if ( 0 != image.getHeight() ) {
                imageTagBuffer.append( " height=\"" + image.getHeight() + "\"" );
            }
            imageTagBuffer.append( " border=\"" + image.getBorder() + "\"" );

            if ( 0 != image.getVerticalSpace() ) {
                imageTagBuffer.append( " vspace=\"" + image.getVerticalSpace() + "\"" );
            }
            if ( 0 != image.getHorizontalSpace() ) {
                imageTagBuffer.append( " hspace=\"" + image.getHorizontalSpace() + "\"" );
            }
            if ( !"".equals( image.getName() ) ) {
                imageTagBuffer.append( " name=\"" + StringEscapeUtils.escapeHtml( image.getName() ) + "\"" );
            }
            if ( !"".equals( image.getAlternateText() ) ) {
                imageTagBuffer.append( " alt=\"" + StringEscapeUtils.escapeHtml( image.getAlternateText() ) + "\"" );
            }
            if ( !"".equals( image.getLowResolutionUrl() ) ) {
                imageTagBuffer.append( " lowscr=\"" + StringEscapeUtils.escapeHtml( image.getLowResolutionUrl() ) + "\"" );
            }
            if ( !"".equals( image.getAlign() ) && !"none".equals( image.getAlign() ) ) {
                imageTagBuffer.append( " align=\"" + StringEscapeUtils.escapeHtml( image.getAlign() ) + "\"" );
            }
            imageTagBuffer.append( ">" );
            if ( StringUtils.isNotBlank( image.getLinkUrl() ) ) {
                imageTagBuffer.append( "</a>" );
            }
        }
        String imageTag = imageTagBuffer.toString();
        return imageTag;
    }
}
