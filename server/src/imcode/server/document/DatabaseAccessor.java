package imcode.server.document;

import imcode.server.IMCService;
import imcode.server.IMCServiceInterface;
import imcode.server.IMCText;
import imcode.server.Table;
import imcode.server.util.DateHelper;
import imcode.server.db.DBConnect;
import imcode.server.db.DatabaseService;
import imcode.server.user.UserDomainObject;
import imcode.util.Parser;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 *  This is the only class in this package that are allowed to call the database direct.
 */

public class DatabaseAccessor {
    /**
     * Stored procedure names used in this class
     */
    // These are only used within this class.
    private static final String SPROC_GET_TEMPLATES_IN_GROUP = "GetTemplatesInGroup";
    private static final String SPROC_GET_TEMPLATE_GROUPS_FOR_USER = "GetTemplategroupsForUser";
    private static final String SPROC_GET_FILE_NAME = "GetFileName";
    private static final String SPROC_INSERT_TEXT = "InsertText";
    private static final String SPROC_UPDATE_PARENTS_DATE_MODIFIED = "UpdateParentsDateModified";
    private static final String SPROC_SECTION_ADD_CROSSREF = "SectionAddCrossref";
    private static final String SPROC_INHERIT_PERMISSONS = "InheritPermissions";
    private static final String SPROC_CLASSIFICATION_FIX = "Classification_Fix";
    private static final String SPROC_GET_PERMISSION_SET = "GetPermissionSet";
    private static final String SPROC_GET_DOC_TYPES_WITH_PERMISSIONS = "GetDocTypesWithPermissions";
    private static final String SPROC_GET_TEMPLATE_GROUPS_WITH_PERMISSIONS = "getTemplateGroupsWithPermissions";
    private static final String SPROC_GET_TEMPLATE_ID = "GetTemplateId";
    private static final String SPROC_GET_IMGS = "GetImgs";

    // todo make sure all the following is only used in one and only sprocMethod and nowhere else
    // these are found to be used elseware in
    private static final String SPROC_SECTION_GET_INHERIT_ID = "SectionGetInheritId";
    private static final String SPROC_GET_DOCUMENT_INFO = "GetDocumentInfo";
    private static final String SPROC_GET_TEXT = "GetText";
    private static final String SPROC_GET_DOC_TYPES_FOR_USER = "GetDocTypesForUser";
    private final static String SPROC_GET_USER_ROLES_DOC_PERMISSONS = "GetUserRolesDocPermissions";
    // These are not checked yet:
    // All checked for now!
    // Add new sprocs here.

    private static String makeBooleanSQL( String columnName, boolean field_isArchived ) {
        String str = columnName + " = " + (field_isArchived ? 1 : 0);
        return str;
    }

    private static String makeDateSQL( String columnName, Date date ) {
        if( null != date ) {
            String dateStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( date );
            return makeStringSQL( columnName, dateStr );
        }
        else {
            return makeStringSQL( columnName, null );
        }
    }

    private static String makeStringSQL( String columnName, Object value ) {
        String s = (value!=null?"'" + value + "'":"NULL");
        String str = columnName + " = " + s + ", ";
        return str;
    }

    /** @return the filename for a fileupload-document, or null if the document isn't a fileupload-docuemnt. **/
    static String sprocGetFilename( IMCService service, int meta_id ) {
        String[] params = new String[]{String.valueOf( meta_id )};
        return service.sqlProcedureStr( SPROC_GET_FILE_NAME, params );
    }

    static DatabaseService.Table_meta sprocGetDocumentInfo( IMCService service, int metaId ) {
        DatabaseService.Table_meta result = service.getDatabaseService().sproc_GetDocumentInfo( metaId );
        return result;
    }

    static HashSet sprocGetDocTypesForUser( IMCServiceInterface imcref, UserDomainObject user, String parent_meta_id, String lang_prefix ) {
        String[] user_dt = imcref.sqlProcedure( SPROC_GET_DOC_TYPES_FOR_USER + " " + parent_meta_id + "," + user.getUserId() + ",'" + lang_prefix + "'" );
        HashSet user_doc_types = new HashSet();
        for( int i = 0; i < user_dt.length; i += 2 ) {
            user_doc_types.add( user_dt[i] );
        }
        return user_doc_types;
    }

    static void sprocUpdateInsertText( IMCService service, int meta_id, int txt_no, IMCText text, String textstring ) {
        String[] params = new String[]{"" + meta_id, "" + txt_no, "" + text.getType(), textstring};
        service.sqlUpdateProcedure( SPROC_INSERT_TEXT, params );
    }

    static void sqlInsertIntoTemplateInfoIntoTextDocs( IMCServiceInterface imcref, String meta_id, String[] temp ) {
        String sqlStr;
        sqlStr = "insert into text_docs (meta_id,template_id,sort_order,group_id,default_template_1,default_template_2) values (" + meta_id + "," + temp[0] + "," + temp[1] + "," + temp[2] + "," + temp[3] + "," + temp[4] + ")";
        imcref.sqlUpdateQuery( sqlStr );
    }

    static String[] sqlSelectTemplateInfoFromTextDocs( IMCServiceInterface imcref, String parent_meta_id ) {
        String sqlStr = "select template_id, sort_order,group_id,default_template_1,default_template_2 from text_docs where meta_id = " + parent_meta_id;
        String temp[] = imcref.sqlQuery( sqlStr );
        return temp;
    }

    static String[] sqlSelectGetClassificationStrings( IMCServiceInterface imcref, int meta_id ) {
        String sqlStr;
        sqlStr = "select code from classification c join meta_classification mc on mc.class_id = c.class_id where mc.meta_id = " + meta_id;
        String[] classifications = imcref.sqlQuery( sqlStr );
        return classifications;
    }

    static int sqlCreateNewRowInMetaCopyParentData( IMCService service, int parentId ) {
        // todo: All this chould propably be done in a singel sql query but i dont have time to think how that should be done right now. Hasse
        // Or at least calld in a batch update (JDBC 2.0)?
        final String columnsToBeCopied = "description,doc_type,meta_headline,meta_text,meta_image,owner_id,permissions,shared,expand,show_meta,help_text_id,archive,status_id,lang_prefix,classification,date_created,date_modified,sort_position,menu_position,disable_search,target,frame_name,activate,activated_datetime,archived_datetime";
        String sqlStatmentGetAllParentData = "select " + columnsToBeCopied + " from meta where meta_id = " + parentId;
        String[] parentDataRow = service.sqlQuery( sqlStatmentGetAllParentData );
        String values = "'" + parentDataRow[0] + "'," + parentDataRow[1] + "," + "'" + parentDataRow[2] + "'," + "'" + parentDataRow[3] + "'," + "'" + parentDataRow[4] + "'," + parentDataRow[5] + "," + parentDataRow[6] + "," + parentDataRow[7] + "," + parentDataRow[8] + "," + parentDataRow[9] + "," + parentDataRow[10] + "," + parentDataRow[11] + "," + parentDataRow[12] + "," + "'" + parentDataRow[13] + "'," + "'" + parentDataRow[14] + "'," + "'" + parentDataRow[15] + "'," + "'" + parentDataRow[16] + "'," + parentDataRow[17] + "," + parentDataRow[18] + "," + parentDataRow[19] + "," + "'" + parentDataRow[20] + "'," + "'" + parentDataRow[21] + "'," + parentDataRow[22];

        // The above query returns "" of some reason instead of NULL, and I don't have time to think of why, Hasse
        if( parentDataRow[23].equals( "" ) ) {
            values += ",NULL";
        } else {
            values += ",'" + parentDataRow[23] + "'";
        }

        // Same reson here.
        if( parentDataRow[24].equals( "" ) ) {
            values += ",NULL";
        } else {
            values += ",'" + parentDataRow[24] + "'";
        }

        String sqlStatmentInsertAllButMetaId = "insert into meta (" + columnsToBeCopied + ") " + " values (" + values.toString() + ")";
        service.sqlUpdateQuery( sqlStatmentInsertAllButMetaId );
        String meta_id = service.sqlQueryStr( "select @@IDENTITY" );
        return Integer.parseInt( meta_id );
    }

    static void sqlUpdateDockType( IMCService service, int metaId, int docType ) {
        String sql = "update meta set doc_type = " + docType + " where meta_id = " + metaId;
        service.sqlUpdateQuery( sql );
    }

    static class RolePermissionTuple {
        String roleName;
        int permissionId;
    }

    static RolePermissionTuple[] sprocGetUserRolesDocPermissions( IMCService service, int metaId ) {
        String[] params = {String.valueOf( metaId ), null};
        String[] sprocResult = service.sqlProcedure( SPROC_GET_USER_ROLES_DOC_PERMISSONS, params );
        int noOfColumns = 4;
        RolePermissionTuple[] result = new RolePermissionTuple[sprocResult.length / noOfColumns];
        for( int i = 0, k = 0; i < sprocResult.length; i = i + noOfColumns, k++ ) {
            //String roleId = sprocResult[i];
            String roleName = sprocResult[i + 1];
            String rolePermissionSetId = sprocResult[i + 2];
            result[k] = new RolePermissionTuple();
            result[k].roleName = roleName;
            result[k].permissionId = Integer.parseInt( rolePermissionSetId );
        }
        return result;
    }

    static class DocumentIdEditablePermissionsTuple {
        boolean hasRights;
        String documentTypeName;
    }


    static class GroupPermissionTuple {
        String groupName;
        boolean hasPermission;
    }

    static class PermissionTuple {
        int permissionId;
        boolean hasPermission;
    }

     /**
     * @param metaId
     * @param permissionSetId
     * @param langPrefix
     * @return PermissionTuple[]
     */
    static PermissionTuple[] sprocGetPermissionSet( IMCService service, int metaId, int permissionSetId, String langPrefix ) {
        String[] sqlParams = {String.valueOf( metaId ), String.valueOf( permissionSetId ), langPrefix};
        String[] sqlResult = service.sqlProcedure( SPROC_GET_PERMISSION_SET, sqlParams );
        PermissionTuple[] result = new PermissionTuple[sqlResult.length / 3];
        for( int i = 0, r = 0; i < sqlResult.length; i = i + 3, r++ ) {
            int permissionId = Integer.parseInt( sqlResult[i] );
            //String permissionDescriptionStr = sqlResult[i + 1];
            boolean hasPermission = Integer.parseInt( sqlResult[i + 2] ) == 1;
            result[r] = new PermissionTuple();
            result[r].permissionId = permissionId;
            result[r].hasPermission = hasPermission;
        }
        return result;
    }

    static DocumentIdEditablePermissionsTuple[] sprocGetDocTypesWithPermissions( IMCService service, int metaId, int permissionType, String langPrefix ) {
        String[] params = new String[]{String.valueOf( metaId ), String.valueOf( permissionType ), langPrefix};
        String[] sprocResult = service.sqlProcedure( SPROC_GET_DOC_TYPES_WITH_PERMISSIONS, params );
        int numberOfColumns = 3;
        DocumentIdEditablePermissionsTuple[] result = new DocumentIdEditablePermissionsTuple[ sprocResult.length/numberOfColumns ];
        for( int i = 0, k = 0; i < sprocResult.length; i = i+numberOfColumns, k++ ){
            //int documentType = Integer.parseInt(sprocResult[i]);
            String documentTypeName = sprocResult[i+1];
            boolean permission = -1 != Integer.parseInt(sprocResult[i+2]);
            result[k] = new DocumentIdEditablePermissionsTuple();
            result[k].hasRights = permission;
            result[k].documentTypeName = documentTypeName;
        }
        return result;
    }

    static GroupPermissionTuple[] sprocGetTemplateGroupsWithPermissions( IMCService service, int metaId, int permissionType ) {
        String[] params = new String[]{ String.valueOf(metaId), String.valueOf( permissionType ) };
        String[] sprocResult = service.sqlProcedure( SPROC_GET_TEMPLATE_GROUPS_WITH_PERMISSIONS, params );
        int numberOfColumns = 3;
        GroupPermissionTuple[] result = new GroupPermissionTuple[sprocResult.length/numberOfColumns];
        for( int i = 0, k=0; i < sprocResult.length; i=i+numberOfColumns, k++) {
            //String groupId = sprocResult[i];
            String groupName = sprocResult[i+1];
            boolean hasPermission = -1 != Integer.parseInt(sprocResult[i+2]);
            result[k] = new GroupPermissionTuple();
            result[k].groupName = groupName;
            result[k].hasPermission = hasPermission;
        }
        return result;
    }


    static Vector sqlSelectGetTemplate( IMCService service, int template_id ) {
        String sqlStr = "select template_id,template_name,simple_name from templates where template_id = " + template_id;
        DBConnect dbc = new DBConnect( service.getConnectionPool() );
        dbc.getConnection();
        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        Vector queryResult = dbc.executeQuery();
        dbc.closeConnection();
        return queryResult;
    }


    static int sqlGetSectionId( IMCService service, String section ) {
        String sql = "select section_id from sections where section_name = '" + section + "'";
        String[] querryResult = service.sqlQuery( sql );
        int sectionId = Integer.parseInt(querryResult[0]);
        return sectionId;
    }

    static void sqlUpdateMeta( IMCService service, int meta_id, Date activatedDatetime, Date archivedDateTime, Date createdDatetime, String headline, String image, Date modifiedDateTime, String target, String text, boolean isArchived ) {

        StringBuffer sqlStr = new StringBuffer( "update meta set " );

        sqlStr.append( makeDateSQL("activated_datetime", activatedDatetime )  );
        sqlStr.append( makeDateSQL("archived_datetime", archivedDateTime)  );
        sqlStr.append( makeDateSQL("date_created", createdDatetime)  );
        sqlStr.append( makeStringSQL("meta_headline", headline)  );
        sqlStr.append( makeStringSQL("meta_image", image)  );
        sqlStr.append( makeDateSQL("date_modified", modifiedDateTime)  );
        sqlStr.append( makeStringSQL("target", target)  );
        sqlStr.append( makeStringSQL("meta_text", text)  );
        String str = makeBooleanSQL( "archive", isArchived );

        sqlStr.append( str );
        // todo: Remove from the meta table all collumns that are not used.
        // Candidates: All not used above.

        sqlStr.append( " where meta_id = " + meta_id );
        service.sqlUpdateQuery( sqlStr.toString() );
    }

    static String[] sprocGetText( IMCService service, int meta_id, int no ) {
        String[] params = new String[]{"" + meta_id, "" + no};
        String[] results = service.sqlProcedure( SPROC_GET_TEXT, params, false );
        return results;
    }

    static void sqlUpdateCreatedDate( IMCService service, int metaId, Date dateTime ) {
        String dateTimeStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( dateTime );
        String sql = "update meta set date_created = '" + dateTimeStr + "' where meta_id = " + metaId;
        service.sqlUpdateQuery( sql );
    }

    static void sqlUpdateTextDoc( IMCService service, int meta_id, TemplateDomainObject template, int menuSortOrder, int templateGroupId ) {
        String tabelName = "text_docs";
        String sqlStr = "update " + tabelName + " set ";

        sqlStr += "template_id = " + template.getId() + ", ";
        sqlStr += "sort_order = " + menuSortOrder + ", ";
        sqlStr += "group_id = " + templateGroupId;

        sqlStr += " where meta_id = " + meta_id;
        service.sqlUpdateQuery( sqlStr );
    }

    /**
     Set the modified datetime of a document to now
     @param meta_id The id of the document
     **/
    static void sqlUpdateTouchDocument( IMCService service, int meta_id ) {
        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat( DateHelper.DATE_TIME_SECONDS_FORMAT_STRING );
        service.sqlUpdateQuery( "update meta set date_modified = '" + dateformat.format( date ) + "' where meta_id = " + meta_id );
    }


    static DatabaseService.Table_text_docs sprocTextDocData( IMCService service, DocumentDomainObject inout_document ) {
        DatabaseService.Table_text_docs textdoc_data = service.getDatabaseService().sproc_GetTextDocData( inout_document.getMetaId() );
        return textdoc_data;
    }

    // todo make sure all following sproc and sql mehtods has "package" visability and that the callers use the "API" instead.
    public static DatabaseService.Table_section sprocSectionGetInheritId( IMCServiceInterface service, int meta_id ) {
        DatabaseService.Table_section sectionData = service.getDatabaseService().sproc_SectionGetInheritId( meta_id );
        return sectionData;
    }

    public static void sprocUpdateInheritPermissions( IMCServiceInterface imcref, int meta_id, int parent_meta_id, int doc_type ) {
        imcref.sqlUpdateProcedure( SPROC_INHERIT_PERMISSONS + " " + meta_id + "," + parent_meta_id + "," + doc_type );
    }

    public static void sprocClassification_Fix( IMCServiceInterface imcref, int meta_id, String classification ) {
        sprocSaveClassification( imcref, meta_id, classification );
    }

    public static void sprocSaveClassification( IMCServiceInterface imcref, int meta_id, String classification ) {
        imcref.sqlUpdateProcedure( SPROC_CLASSIFICATION_FIX + " " + meta_id + ",'" + classification + "'" );
    }

    public static void sprocSectionAddCrossref( IMCServiceInterface imcref, int meta_id, int section_id ) {
        imcref.sqlUpdateProcedure( SPROC_SECTION_ADD_CROSSREF + " " + meta_id + ", " + section_id );
    }

    public static Vector sprocGetDocTypeForUser( DBConnect dbc, UserDomainObject user, int meta_id, String lang_prefix ) {
        String sqlStr = SPROC_GET_DOC_TYPES_FOR_USER;
        String[] sqlAry2 = {String.valueOf( meta_id ), String.valueOf( user.getUserId() ), lang_prefix};
        dbc.setProcedure( sqlStr, sqlAry2 );
        Vector doc_types_vec = dbc.executeProcedure();
        dbc.clearResultSet();
        return doc_types_vec;
    }

    public static void sprocUpdateParentsDateModified( IMCServiceInterface imcref, int meta_id ) {
        imcref.sqlUpdateProcedure( SPROC_UPDATE_PARENTS_DATE_MODIFIED + " " + meta_id );
    }

    public static Vector sprocGetTemplateId( DBConnect dbc, String template_name ) {
        dbc.setProcedure( SPROC_GET_TEMPLATE_ID + " " + template_name );
        Vector vectT = dbc.executeProcedure();
        return vectT;
    }


    public static Vector sprocGetTemplateGroupsForUser( DBConnect dbc, UserDomainObject user, int meta_id ) {
        String sqlStr = SPROC_GET_TEMPLATE_GROUPS_FOR_USER;
        String[] sqlAry2 = {String.valueOf( meta_id ), String.valueOf( user.getUserId() )};
        dbc.setProcedure( sqlStr, sqlAry2 );
        Vector templategroups = dbc.executeProcedure();
        dbc.clearResultSet();
        return templategroups;
    }

    public static Vector sprocGetTemplatesInGroup( DBConnect dbc, int selected_group ) {
        String sqlStr = SPROC_GET_TEMPLATES_IN_GROUP;
        dbc.setProcedure( sqlStr, String.valueOf( selected_group ) );
        Vector templates = dbc.executeProcedure();
        dbc.clearResultSet();
        return templates;
    }

    public static Vector sprocGetImgs( DBConnect dbc, int meta_id ) {
        // Get the images from the db
        // sqlStr = "select '#img'+convert(varchar(5), name)+'#',name,imgurl,linkurl,width,height,border,v_space,h_space,image_name,align,alt_text,low_scr,target,target_name from images where meta_id = " + meta_id ;
        //					0                    1    2      3       4     5      6      7       8       9          10    11       12      13     14
        dbc.setProcedure( SPROC_GET_IMGS, String.valueOf( meta_id ) );
        Vector images = dbc.executeProcedure();
        return images;
    }

    public static void sqlUpdateMetaDateCreated( IMCServiceInterface imcref, String meta_id, String created_datetime ) {
        String sqlStr;
        sqlStr = "update meta set date_created ='" + created_datetime + "' where meta_id = " + meta_id;
        imcref.sqlUpdateQuery( sqlStr );
    }

    public static void sqlSelectAddSortorderToParentsChildList( IMCServiceInterface imcref, String parent_meta_id, String meta_id, String doc_menu_no ) {
        String sqlStr = "declare @new_sort int\n" + "select @new_sort = max(manual_sort_order)+10 from childs where meta_id = " + parent_meta_id + " and menu_sort = " + doc_menu_no + "\n" + "if @new_sort is null begin set @new_sort = 500 end\n" + "insert into childs (meta_id, to_meta_id, menu_sort, manual_sort_order) values (" + parent_meta_id + "," + meta_id + "," + doc_menu_no + ",@new_sort)\n";
        imcref.sqlUpdateQuery( sqlStr );
    }

    public static void sqlUpdateModifiedDate( IMCServiceInterface service, int meta_id, Date date ) {
        String dateModifiedStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( date );
        String sqlStr = "update meta set date_modified = '" + dateModifiedStr + "' where meta_id = " + meta_id;
        service.sqlUpdateQuery( sqlStr );
    }

    public static void sqlUpdateActivateTheTextField( IMCServiceInterface imcref, int meta_id ) {
        String sqlStr = "update meta set activate = 1 where meta_id = " + meta_id;
        imcref.sqlUpdateQuery( sqlStr );
    }

    public static void sqlInsertIntoTexts( IMCServiceInterface imcref, String meta_id, String mHeadline, String mText ) {
        String sqlStr = "insert into texts (meta_id,name,text,type) values (" + meta_id + ", 1, '" + mHeadline + "', 1)";
        imcref.sqlUpdateQuery( sqlStr );
        sqlStr = "insert into texts (meta_id,name,text,type) values (" + meta_id + ", 2, '" + mText + "', 1)";
        imcref.sqlUpdateQuery( sqlStr );
    }

    public static synchronized String sqlInsertIntoMeta( IMCServiceInterface imcref, String doc_type, String activated_datetime, String archived_datetime, Properties metaprops ) {
        // Lets build the sql statement to add a new meta id
        String sqlStr = "insert into meta (doc_type,activate,classification,activated_datetime,archived_datetime";
        String sqlStr2 = ")\nvalues (" + doc_type + ",0,''," + (null == activated_datetime ? "NULL" : "'" + activated_datetime + "'") + "," + (null == archived_datetime ? "NULL" : "'" + archived_datetime + "'");
        Enumeration propkeys = metaprops.propertyNames();
        while( propkeys.hasMoreElements() ) {
            String temp = (String)propkeys.nextElement();
            String val = metaprops.getProperty( temp );
            String[] vp = {"'", "''"};
            sqlStr += "," + temp;
            sqlStr2 += ",'" + Parser.parseDoc( val, vp ) + "'";
        }
        sqlStr += sqlStr2 + ")";
        imcref.sqlUpdateQuery( sqlStr );
        String meta_id = imcref.sqlQueryStr( "select @@IDENTITY" );
        return meta_id;
    }

    public static void sqlUpdateModifiedDatesOnDocumentAndItsParent( IMCService service, int meta_id, Date dateTime ) {
        String modifiedDateTimeStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( dateTime );
        String sqlStr = "update meta set date_modified ='" + modifiedDateTimeStr + "' where meta_id = " + meta_id;
        service.sqlUpdateQuery( sqlStr );
    }

    public static void sqlSaveTextDoc( IMCServiceInterface service, int meta_id, Table doc ) {
        String sqlStr = "";
        sqlStr = "update text_docs\n";
        sqlStr += "set template_id= " + doc.getString( "template" );
        sqlStr += ", group_id= " + doc.getString( "group_id" );
        sqlStr += " where meta_id = " + meta_id;
        service.sqlUpdateQuery( sqlStr );
    }
    public static Vector sqlSelectGroupName( DBConnect dbc, String group_id ) {
        String sqlStr = "select group_name from templategroups where group_id = " + group_id;
        dbc.setSQLString( sqlStr );
        Vector groupnamevec = dbc.executeQuery();
        dbc.clearResultSet();
        return groupnamevec;
    }

}
