package imcode.server.document;

import imcode.server.*;
import imcode.server.db.DatabaseService;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.poll.PollHandlingSystem;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

public class DocumentMapper {
    private Logger log = Logger.getLogger( DocumentMapper.class );
    private final IMCService service;
    private final DatabaseService databaseService;
    private final ImcmsAuthenticatorAndUserMapper imcmsAAUM;

    public DocumentMapper( IMCService service, ImcmsAuthenticatorAndUserMapper imcmsAAUM ) {
        this.service = service;
        this.databaseService = service.getDatabaseService();
        this.imcmsAAUM = imcmsAAUM;
    }

    public DocumentDomainObject getDocument( int metaId ) {
        DocumentDomainObject document;
        try {
            DatabaseService.Table_meta metaData = DatabaseAccessor.sprocGetDocumentInfo( service, metaId );

            //lets start and do some controlls of the resulted data
            if( metaData == null ) {
                throw new IndexOutOfBoundsException( "No such document: " + metaId );
            }

            //ok lets set all the document stuff
            try {
                document = new DocumentDomainObject();
                document.setMetaId( metaData.meta_id );
                document.setDocumentType( metaData.doc_type );
            } catch( NumberFormatException nfe ) {
                throw new SQLException( "SQL: GetDocumentInfo " + metaId + " returned corrupt data! '" + metaData.meta_id + "' '" + metaData.doc_type + "'" );
            }
            document.setHeadline( metaData.meta_headline );
            document.setText( metaData.meta_text );
            document.setImage( metaData.meta_image );
            document.setTarget( metaData.target );

            document.setArchived( metaData.archive );

            DatabaseService.Table_section section_data = DatabaseAccessor.sprocSectionGetInheritId( service, metaId );

            if( null != section_data ) {
                document.setSection( section_data.section_name );
            }

            document.setCreatedDatetime( metaData.date_created );
            document.setModifiedDatetime( metaData.date_modified );
            document.setActivatedDatetime( metaData.activated_datetime );
            document.setArchivedDatetime( metaData.archived_datetime );

            if( document.getDocumentType() == DocumentDomainObject.DOCTYPE_FILE ) {
                document.setFilename( DatabaseAccessor.sprocGetFilename( service, metaId ) );
            }
            if( document.getDocumentType() == DocumentDomainObject.DOCTYPE_TEXT ) {
                static_initTextDoc( service, document );
            }
        } catch( SQLException ex ) {
            log.error( ex );
            throw new IndexOutOfBoundsException();
        }
        return document;

    }

    public boolean hasAdminPermissions( DocumentDomainObject document, UserDomainObject user ) {

        boolean result = false;

        boolean userHasSuperAdminRole = imcmsAAUM.hasSuperAdminRole( user );

        if( userHasSuperAdminRole ) {
            result = true;
        } else {

            DatabaseService.JoinedTables_permissions perms = service.getDatabaseService().getUserPermissionSetForDocument( document.getMetaId(), user.getUserId() );

            if( null != perms ) {
                int userPermissionSetId = perms.set_id;
                switch( userPermissionSetId ) {
                    case IMCConstants.DOC_PERM_SET_FULL:
                    case IMCConstants.DOC_PERM_SET_RESTRICTED_1:
                    case IMCConstants.DOC_PERM_SET_RESTRICTED_2:
                        result = true;
                        break;
                    default:
                        result = false;
                }
            }
        }

        return result;
    }

    public IMCText getTextField( DocumentDomainObject document, int textFieldIndexInDocument ) {
        return service.getText( document.getMetaId(), textFieldIndexInDocument );
    }

    public IMCText getText( int meta_id, int no ) {
        try {
            String[] results = DatabaseAccessor.sprocGetText( service, meta_id, no );

            if( results == null || results.length == 0 ) {
                /* There was no text. Return null. */
                return null;
            }

            /* Return the text */
            String text = results[0];
            int type = Integer.parseInt( results[1] );

            return new IMCText( text, type );

        } catch( NumberFormatException ex ) {
            /* There was no text, but we shouldn't come here unless the db returned something wrong. */
            log.error( "SProc 'sprocGetText()' returned an invalid text-type.", ex );
            return null;
        }
    }

    public void saveText( IMCText text, int meta_id, int txt_no, UserDomainObject user, String text_type ) {
        String textstring = text.getText();

        // update text
        DatabaseAccessor.sprocUpdateInsertText( service, meta_id, txt_no, text, textstring );

        // update the date
        DatabaseAccessor.sqlUpdateTouchDocument( service, meta_id );

        service.updateLogs( "Text " + txt_no + " in  " + "[" + meta_id + "] modified by user: [" + user.getFullName() + "]" );

        if( !("").equals( text_type ) ) {

            if( text_type.startsWith( "poll" ) ) {
                PollHandlingSystem poll = service.getPollHandlingSystem();
                poll.savePollparameter( text_type, meta_id, txt_no, textstring );
            }
        }
    }

    public void copyTemplateData( UserDomainObject user, String parent_meta_id, String meta_id ) {
        //ok now lets see what to do with the templates
        String[] temp = DatabaseAccessor.sqlSelectTemplateInfoFromTextDocs( service, parent_meta_id );

        //lets get the users greatest permission_set for this dokument
        final int perm_set = service.getUserHighestPermissionSet( Integer.parseInt( meta_id ), user.getUserId() );
        //ok now we have to setup the template too use

        if( perm_set == IMCConstants.DOC_PERM_SET_RESTRICTED_1 ) {
            //ok restricted_1 permission lets see if we have a default template fore this one
            //and if so lets put it as the orinary template instead of the parents
            try {
                int tempInt = Integer.parseInt( temp[3] );
                if( tempInt >= 0 )
                    temp[0] = String.valueOf( tempInt );
            } catch( NumberFormatException nfe ) {

                //there wasn't a number but we dont care, we just catch the exeption and moves on.
            }
        } else if( perm_set == IMCConstants.DOC_PERM_SET_RESTRICTED_2 ) { //ok we have a restricted_2 permission lets see if we have default template fore this one
            //and if soo lets put it as ordinary instead of the parents
            try {
                int tempInt = Integer.parseInt( temp[4] );
                if( tempInt >= 0 )
                    temp[0] = String.valueOf( tempInt );
            } catch( NumberFormatException nfe ) {
                //there wasn't a number but we dont care, we just catch the exeption and moves on.
            }
        }
        //ok were set, lets update db
        DatabaseAccessor.sqlInsertIntoTemplateInfoIntoTextDocs( service, meta_id, temp );
    }

    /**
     * Inspired by the SaveNewMeta servlet... I went throu the code and tried to extract the nessesary parts. Hasse
     * todo: make the SaveNewMeta to use this method instead.
     * @param parentId
     * @param parentMenuNumber
     * @return
     */
    public synchronized DocumentDomainObject createNewTextDocument( UserDomainObject user, int parentId, int parentMenuNumber ) {
        // general for all types of documents
        int docType = DocumentDomainObject.DOCTYPE_TEXT;
        Date nowDateTime = new Date();

        int newMetaId = DatabaseAccessor.sqlCreateNewRowInMetaCopyParentData( service, parentId );
        // fix the data that is unique for this document
        DatabaseAccessor.sqlUpdateCreatedDate( service, newMetaId, nowDateTime );
        DatabaseAccessor.sqlUpdateModifiedDate( service, newMetaId, nowDateTime );
        DatabaseAccessor.sqlUpdateDockType( service, newMetaId, docType );

        // inherit all the different data that's not in meta from parent.
        DatabaseAccessor.sprocUpdateInheritPermissions( service, newMetaId, parentId, docType );
        static_inheritClassifications( parentId, newMetaId );
        static_inheritSection( parentId, newMetaId );

        // update parents, why? what is this? /Hasse
        DatabaseAccessor.sqlSelectAddSortorderToParentsChildList( service, String.valueOf( parentId ), String.valueOf( newMetaId ), String.valueOf( parentMenuNumber ) );
        // update parents modfied date because it has gotten an new link
        DatabaseAccessor.sqlUpdateModifiedDate( service, parentId, nowDateTime );

        // Text document
        copyTemplateData( user, String.valueOf( parentId ), String.valueOf( newMetaId ) );
        DatabaseAccessor.sqlUpdateActivateTheTextField( service, newMetaId );

        return getDocument( newMetaId );
    }

    public void saveTextDocument( DocumentDomainObject document ) {
        Date now = new Date();
        document.setModifiedDatetime( now );

        // Bokstavsorning på attributen, så man lättare kan hitta om något läggs till eller tagits bort
        // använder attributen direkt, för att inte förvirras av de get-metoder som saknar motsvarande
        // underliggande data.
        // Denna långa uppraddning gör jag för att IntelliJ kan visa vilka variabler jag missar att använda.
        // Denna kommentar blev på svenska för jag kom inte på vad bokstavsordning är på engelska.../Hasse
        Date activatedDatetime = document.activatedDatetime;
        Date archivedDatetime = document.archivedDatetime;
        Date createdDatetime = document.createdDatetime;
        // String filename = document.filename; // todo: only in file docks, not implemented yet.
        String headline = document.headline;
        String image = document.image;
        int menuSortOrder = document.menuSortOrder;
        Date modifiedDatetime = document.modifiedDatetime;
        String section = document.section;
        String target = document.target;
        TemplateDomainObject template = document.template;
        int templateGroupId = document.templateGroupId;
        String text = document.text;
        boolean archived = document.archived;

        databaseService.update_meta( document.getMetaId(), activatedDatetime, archivedDatetime, createdDatetime, headline, image, modifiedDatetime, target, text, archived);

        if( null != section ) {
            Integer sectionId = databaseService.selectFrom_section_getSectionIdFromSectionName( section );
            databaseService.sproc_SectionAddCrossref( document.getMetaId(), sectionId.intValue() );
        }

        // Restricted One and Two
        // todo: Save the restriction for a page.

        // TEXT_DOC
        DatabaseAccessor.sqlUpdateTextDoc( service, document.getMetaId(), template, menuSortOrder, templateGroupId );


        // todo: Mark parent as modified
        /*
                int parentId = ???;
                sqlUpdateModifiedDate( service, parentId , now );
        */
    }

    public boolean checkUsersRights( UserDomainObject user, String parent_meta_id, String lang_prefix, String doc_type ) {
        HashSet user_doc_types = DatabaseAccessor.sprocGetDocTypesForUser( service, user, parent_meta_id, lang_prefix );
        boolean userHasRights = user_doc_types.contains( doc_type );
        return userHasRights;
    }

    public String getClassificationsAsOneString( int meta_id ) {
        String[] classifications = DatabaseAccessor.sqlSelectGetClassificationStrings( service, meta_id );
        String classification = "";
        if( classifications.length > 0 ) {
            classification += classifications[0];
            for( int i = 1; i < classifications.length; ++i ) {
                classification += "; " + classifications[i];
            }
        }
        return classification;
    }

    private void static_inheritSection( int from_parentId, int to_metaId ) {
        DatabaseService.Table_section sectionData = DatabaseAccessor.sprocSectionGetInheritId( service, from_parentId );
        DatabaseAccessor.sprocSectionAddCrossref( service, to_metaId, sectionData.section_id );
    }

    private void static_inheritClassifications( int from_parentId, int to_newMetaId ) {
        String classifications = getClassificationsAsOneString( from_parentId );
        DatabaseAccessor.sprocSaveClassification( service, to_newMetaId, classifications );
    }

    private static void static_initTextDoc( IMCService service, DocumentDomainObject inout_document ) {
        // all from the table text_doc
        DatabaseService.Table_text_docs textdoc_data = DatabaseAccessor.sprocTextDocData( service, inout_document );
        if( null != textdoc_data ) {
            int template_id = textdoc_data.template_id;
            int sort_order = textdoc_data.sort_order;
            int group_id = textdoc_data.group_id;
            TemplateDomainObject template = TemplateMapper.getTemplate( service, template_id );
            inout_document.setTemplate( template );
            inout_document.setMenuSortOrder( sort_order );
            inout_document.setTemplateGroupId( group_id );
        }
    }
}

