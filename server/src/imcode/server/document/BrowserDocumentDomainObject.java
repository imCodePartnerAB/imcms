/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-28
 * Time: 20:48:17
 */
package imcode.server.document;

import com.imcode.imcms.servlet.admin.DocumentComposer;
import imcode.server.user.UserDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class BrowserDocumentDomainObject extends DocumentDomainObject {

    private Map browserDocumentIdMap = new TreeMap();

    public Map getBrowserDocumentIdMap() {
        return Collections.unmodifiableMap( browserDocumentIdMap );
    }

    public int getDocumentTypeId() {
        return DOCTYPE_BROWSER;
    }

    public void processNewDocumentInformation( DocumentComposer documentInformation,
                                               DocumentComposer.NewDocumentParentInformation newDocumentParentInformation,
                                               UserDomainObject user, HttpServletRequest request,
                                               HttpServletResponse response ) throws IOException, ServletException {
        documentInformation.forwardToCreateNewBrowserDocumentPage( request, response, user );

    }

    public void saveNewDocument( DocumentMapper documentMapper ) throws IOException {
        documentMapper.saveNewBrowserDocument( this );
    }

    public void initDocumentFromDb( DocumentMapper documentMapper ) {
        documentMapper.initBrowserDocumentFromDb( this );
    }

    public void setBrowserDocumentId( Browser browser, int toMetaId ) {
        browserDocumentIdMap.put( browser, new Integer( toMetaId ) );
    }

    public static class Browser implements Comparable {

        private int id;
        private String name;
        private int specificity;

        public Browser( int id, String name, int specificity ) {
            this.id = id;
            this.name = name;
            this.specificity = specificity;
        }

        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( !( o instanceof Browser ) ) {
                return false;
            }

            final Browser browser = (Browser)o;

            if ( id != browser.id ) {
                return false;
            }

            return true;
        }

        public int hashCode() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public int compareTo( Object o ) {
            Browser browser = (Browser)o ;
            int comparison = specificity - browser.specificity ;
            if (0 == comparison) {
                comparison = name.compareTo( browser.name ) ;
            }
            return comparison ;
        }

    }

}