package imcode.server.document;

import org.apache.commons.collections.MapUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BrowserDocumentDomainObject extends DocumentDomainObject {

    private Map browserDocumentIdMap = MapUtils.typedMap( new HashMap(), Browser.class, Integer.class ) ;

    public Map getBrowserDocumentIdMap() {
        return Collections.unmodifiableMap( browserDocumentIdMap );
    }

    public DocumentTypeDomainObject getDocumentType() {
        return DocumentTypeDomainObject.BROWSER;
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitBrowserDocument(this) ;
    }

    public void setBrowserDocumentId( Browser browser, int documentId ) {
        this.browserDocumentIdMap.put( browser, new Integer( documentId ) );
    }

    public void setBrowserDocuments( Map browserDocuments ) {
        this.browserDocumentIdMap.clear();
        this.browserDocumentIdMap.putAll( browserDocuments );
    }

    public static class Browser implements Comparable, Serializable {

        /* Null object */
        public final static Browser DEFAULT = new Browser( 0, "", 0 );

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
            Browser browser = (Browser)o;
            int comparison = browser.specificity - specificity;
            if ( 0 == comparison ) {
                comparison = name.compareTo( browser.name );
            }
            return comparison;
        }

    }

}