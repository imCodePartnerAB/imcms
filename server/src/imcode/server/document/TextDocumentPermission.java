package imcode.server.document;

public class TextDocumentPermission extends DocumentPermission {

    public static final TextDocumentPermission EDIT_TEXTS = new TextDocumentPermission( "editTexts" );
    public static final TextDocumentPermission EDIT_MENUS = new TextDocumentPermission( "editMenus" );
    public static final TextDocumentPermission EDIT_TEMPLATE = new TextDocumentPermission( "editTemplates" );
    public static final TextDocumentPermission EDIT_INCLUDES = new TextDocumentPermission( "editIncludes" ) ;
    public static final TextDocumentPermission EDIT_IMAGES = new TextDocumentPermission( "editImages" );

    public TextDocumentPermission( String name ) {
        super( name );
    }
}
