package com.imcode.imcms.api;

import com.imcode.util.ChainableReversibleNullComparator;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.SectionDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Document implements Serializable {

    private final DocumentDomainObject internalDocument;
    ContentManagementSystem contentManagementSystem;

    private final static Logger log = Logger.getLogger( com.imcode.imcms.api.Document.class.getName() );
    public static final int STATUS_NEW = DocumentDomainObject.STATUS_NEW;
    public static final int STATUS_PUBLICATION_DISAPPROVED = DocumentDomainObject.STATUS_PUBLICATION_DISAPPROVED;
    public static final int STATUS_PUBLICATION_APPROVED = DocumentDomainObject.STATUS_PUBLICATION_APPROVED;

    protected Document( DocumentDomainObject document, ContentManagementSystem contentManagementSystem ) {
        this.internalDocument = document;
        this.contentManagementSystem = contentManagementSystem;
    }

    DocumentDomainObject getInternal() {
        return internalDocument;
    }

    public int getId() {
        return internalDocument.getId();
    }

    /**
     * @return map of roles Role -> DocumentPermissionSet instances.
     */
    public Map getRolesMappedToPermissions() {
        Map rolesMappedToPermissionSetIds = internalDocument.getRolesMappedToPermissionSetIds();

        Map result = new HashMap();
        for ( Iterator it = rolesMappedToPermissionSetIds.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry rolePermissionTuple = (Map.Entry)it.next();
            RoleDomainObject role =  (RoleDomainObject)rolePermissionTuple.getKey() ;
            int permissionType = ( (Integer)rolePermissionTuple.getValue() ).intValue();
            switch ( permissionType ) {
                case DocumentPermissionSetDomainObject.TYPE_ID__FULL:
                    result.put( role, DocumentPermissionSetDomainObject.FULL );
                    break;
                case DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1:
                    result.put( role.getName(), internalDocument.getPermissionSetForRestrictedOne()) ;
                    break;
                case DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2:
                    result.put( role.getName(), internalDocument.getPermissionSetForRestrictedTwo()) ;
                    break;
                case DocumentPermissionSetDomainObject.TYPE_ID__READ:
                    result.put( role, DocumentPermissionSetDomainObject.READ );
                    break;
                case DocumentPermissionSetDomainObject.TYPE_ID__NONE:
                    break;
                default:
                    log.warn( "A missing mapping in DocumentPermissionSetMapper" );
                    break;
            }
        }

        return wrapDomainObjectsInMap( result );

    }

    private static Map wrapDomainObjectsInMap( Map rolesMappedToPermissionsIds ) {
        Map result = new HashMap();
        Set keys = rolesMappedToPermissionsIds.keySet();
        Iterator keyIterator = keys.iterator();
        while ( keyIterator.hasNext() ) {
            RoleDomainObject role = (RoleDomainObject)keyIterator.next();
            DocumentPermissionSetDomainObject documentPermissionSetDO = (DocumentPermissionSetDomainObject)rolesMappedToPermissionsIds.get( role );
            DocumentPermissionSet documentPermissionSet = new DocumentPermissionSet( documentPermissionSetDO );
            result.put( new Role(role), documentPermissionSet );
        }
        return result;
    }

    public boolean isSearchDisabled() {
        return internalDocument.isSearchDisabled();
    }

    public void setSearchDisabled(boolean searchDisabled) {
        internalDocument.setSearchDisabled(searchDisabled);
    }

    public DocumentPermissionSet getDocumentPermissionSetForUser() {
        return new DocumentPermissionSet( contentManagementSystem.getCurrentUser().getInternal().getPermissionSetFor( internalDocument ) );
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Document ) ) {
            return false;
        }

        final Document document = (Document)o;

        return internalDocument.equals(document.internalDocument);

    }

    public int hashCode() {
        return internalDocument.hashCode();
    }

    public Set getKeywords() {
        return internalDocument.getKeywords();
    }

    public void setKeywords(Set keywords) {
        internalDocument.setKeywords(keywords);
    }

    public DocumentPermissionSet getPermissionSetRestrictedOne() {
        DocumentPermissionSetDomainObject restrictedOne = internalDocument.getPermissionSetForRestrictedOne() ;
        DocumentPermissionSet result = new DocumentPermissionSet( restrictedOne );
        return result;
    }

    public DocumentPermissionSet getPermissionSetRestrictedTwo() {
        DocumentPermissionSetDomainObject restrictedTwo = internalDocument.getPermissionSetForRestrictedTwo() ;
        DocumentPermissionSet result = new DocumentPermissionSet( restrictedTwo );
        return result;
    }

    public String getHeadline() {
        return internalDocument.getHeadline();
    }

    public String getMenuText() {
        return internalDocument.getMenuText();
    }

    public String getMenuImageURL() {
        return internalDocument.getMenuImage();
    }

    public void setHeadline( String headline ) {
        internalDocument.setHeadline( headline );
    }

    public void setMenuText( String menuText ) {
        internalDocument.setMenuText( menuText );
    }

    public void setMenuImageURL( String imageUrl ) {
        internalDocument.setMenuImage( imageUrl );
    }

    public User getCreator() {
        return new User( internalDocument.getCreator() );
    }

    public void setCreator( User creator ) {
        internalDocument.setCreator( creator.getInternal() );
    }

    public Language getLanguage() {
        return Language.getLanguageByISO639_2( internalDocument.getLanguageIso639_2() );
    }

    /**
        @deprecated Use {@link #setLanguage(Language)}      
     **/
    public void setLanguage( int lang_id ) {
        String lang_prefix = contentManagementSystem.getInternal().getLanguagePrefixByLangId( lang_id );
        internalDocument.setLanguageIso639_2( lang_prefix );
    }

    public void addCategory( Category category ) {
        internalDocument.addCategory( category.getInternal() );
    }

    public void removeCategory( Category category ) {
        internalDocument.removeCategory( category.getInternal() );
    }

    /**
     * @return An array of Categories, an empty if no one found.
     */
    public Category[] getCategories() {
        CategoryDomainObject[] categoryDomainObjects = internalDocument.getCategories();
        return getCategoryArrayFromCategoryDomainObjectArray( categoryDomainObjects );
    }

    private Category[] getCategoryArrayFromCategoryDomainObjectArray( CategoryDomainObject[] categoryDomainObjects ) {
        Category[] categories = new Category[categoryDomainObjects.length];

        for ( int i = 0; i < categories.length; i++ ) {
            CategoryDomainObject categoryDomainObject = categoryDomainObjects[i];
            categories[i] = new Category( categoryDomainObject );
        }
        return categories;
    }

    /**
        @param permissionSetId One of the constants in {@link DocumentPermissionSet}.
        @since 2.0
     **/
    public void setPermissionSetIdForRole( Role role, int permissionSetId ) {
        if ( null != role ) {
            internalDocument.setPermissionSetIdForRole( role.getInternal(), permissionSetId );
        }
    }

    /**
         @since 2.0
     */
    public int getPermissionSetIdForRole( Role role ) {
        return internalDocument.getPermissionSetIdForRole( role.getInternal() );
    }

    /**
     * @param categoryType
     * @return an array of Categories, empty array if no one found.
     */
    public Category[] getCategoriesOfType( CategoryType categoryType ) {
        CategoryDomainObject[] categoryDomainObjects = internalDocument.getCategoriesOfType( categoryType.getInternal() );
        return getCategoryArrayFromCategoryDomainObjectArray( categoryDomainObjects );
    }

    public User getPublisher() {
        UserDomainObject publisher = internalDocument.getPublisher();
        if ( null != publisher ) {
            return new User( publisher );
        } else {
            return null;
        }
    }

    public String getTarget() {
        return internalDocument.getTarget();
    }

    public Date getPublicationStartDatetime() {
        return internalDocument.getPublicationStartDatetime();
    }

    public void setPublicationStartDatetime( Date datetime ) {
        internalDocument.setPublicationStartDatetime( datetime );
    }

    public Date getArchivedDatetime() {
        return internalDocument.getArchivedDatetime();
    }

    public void setArchivedDatetime( Date datetime ) {
        internalDocument.setArchivedDatetime( datetime );
    }

    public void setPublisher( User user ) {
        internalDocument.setPublisher( user.getInternal() );
    }

    /**
     * @return An array of Sections, an empty arrya if no one found.
     */
    public Section[] getSections() {
        SectionDomainObject[] sectionDomainObjects = internalDocument.getSections();
        Section[] sections = new Section[sectionDomainObjects.length];
        for ( int i = 0; i < sectionDomainObjects.length; i++ ) {
            SectionDomainObject sectionDomainObject = sectionDomainObjects[i];
            sections[i] = new Section( sectionDomainObject );
        }
        return sections;
    }

    public void setSections( Section[] sections ) {
        SectionDomainObject[] internalSections = new SectionDomainObject[sections.length];
        for ( int i = 0; i < sections.length; i++ ) {
            Section section = sections[i];
            internalSections[i] = section.internalSection;
        }
        internalDocument.setSections( internalSections );
    }

    public Date getModifiedDatetime() {
        return internalDocument.getModifiedDatetime();
    }

    public void setModifiedDatetime( Date date ) {
        internalDocument.setModifiedDatetime( date );
    }

    public Date getCreatedDatetime() {
        return internalDocument.getCreatedDatetime();
    }

    public void addSection( Section section ) {
        internalDocument.addSection( section.internalSection );
    }

    public void setStatus( int status ) {
        internalDocument.setStatus( status );
    }

    public void setLanguage( Language language ) {
        internalDocument.setLanguageIso639_2( language.getIsoCode639_2() );
    }

    public void setPublicationEndDatetime( Date datetime ) {
        internalDocument.setPublicationEndDatetime( datetime );
    }

    public Date getPublicationEndDatetime() {
        return internalDocument.getPublicationEndDatetime();
    }

    public int getStatus() {
        return internalDocument.getStatus();
    }

    public void setVisibleInMenusForUnauthorizedUsers( boolean visibleInMenusForUnauthorizedUsers ) {
        internalDocument.setVisibleInMenusForUnauthorizedUsers( visibleInMenusForUnauthorizedUsers );
    }

    public boolean isVisibleInMenusForUnauthorizedUsers() {
        return internalDocument.isVisibleInMenusForUnauthorizedUsers();
    }

    public boolean isLinkableByOtherUsers() {
        return internalDocument.isLinkableByOtherUsers();
    }

    public void setLinkableByOtherUsers(boolean linkableByOtherUsers) {
        internalDocument.setLinkableByOtherUsers(linkableByOtherUsers);
    }

    public static class LifeCyclePhase {
        public static final LifeCyclePhase NEW = new LifeCyclePhase(DocumentDomainObject.LifeCyclePhase.NEW);
        public static final LifeCyclePhase DISAPPROVED = new LifeCyclePhase(DocumentDomainObject.LifeCyclePhase.DISAPPROVED);
        public static final LifeCyclePhase PUBLISHED = new LifeCyclePhase(DocumentDomainObject.LifeCyclePhase.PUBLISHED);
        public static final LifeCyclePhase UNPUBLISHED = new LifeCyclePhase(DocumentDomainObject.LifeCyclePhase.UNPUBLISHED);
        public static final LifeCyclePhase ARCHIVED = new LifeCyclePhase(DocumentDomainObject.LifeCyclePhase.ARCHIVED);
        public static final LifeCyclePhase APPROVED = new LifeCyclePhase(DocumentDomainObject.LifeCyclePhase.APPROVED);

        private DocumentDomainObject.LifeCyclePhase phase ;

        private LifeCyclePhase( DocumentDomainObject.LifeCyclePhase phase ) {
            this.phase = phase;
        }

        public String toString() {
            return phase.toString() ;
        }
    }

    public abstract static class Comparator extends ChainableReversibleNullComparator {

        public int compare( Object o1, Object o2 ) {
            final Document d1 = (Document)o1;
            final Document d2 = (Document)o2;
            try {
                return compareDocuments( d1, d2 );
            } catch ( NullPointerException npe ) {
                throw new NullPointerException( "Tried sorting on null fields! You need to call .nullsFirst() or .nullsLast() on your Comparator." );
            }
        }

        protected abstract int compareDocuments( Document d1, Document d2 );

        public final static Comparator ID = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) {
                return d1.getId() - d2.getId();
            }
        };

        public final static Comparator HEADLINE = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) {
                return d1.getHeadline().compareToIgnoreCase( d2.getHeadline() );
            }
        };

        public final static Comparator CREATED_DATETIME = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) {
                return d1.getCreatedDatetime().compareTo( d2.getCreatedDatetime() );
            }
        };

        public final static Comparator MODIFIED_DATETIME = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) {
                return d1.getModifiedDatetime().compareTo( d2.getModifiedDatetime() );
            }
        };

        public final static Comparator PUBLICATION_START_DATETIME = new Comparator() {
            protected int compareDocuments( Document document1, Document document2 ) {
                return document1.getPublicationStartDatetime().compareTo( document2.getPublicationStartDatetime() );
            }
        };

        public final static Comparator PUBLICATION_END_DATETIME = new Comparator() {
            protected int compareDocuments( Document document1, Document document2 ) {
                return document1.getPublicationEndDatetime().compareTo( document2.getPublicationEndDatetime() );
            }
        };

        public final static Comparator ARCHIVED_DATETIME = new Comparator() {
            protected int compareDocuments( Document document1, Document document2 ) {
                return document1.getArchivedDatetime().compareTo( document2.getArchivedDatetime() );
            }
        };

    }
}
