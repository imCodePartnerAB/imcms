package imcode.server ;

public interface ImcmsConstants {

    /* Permissions for restricted permission-sets 1 and 2, applicable for all document-types. */

    /** Permission to edit the headline, text, and image of a document. **/
    public final static int PERM_EDIT_HEADLINE           = (1 << 0) ; // 1

    /** Permission to edit all docinfo for a document. **/
    public final static int PERM_EDIT_DOCINFO            = PERM_EDIT_HEADLINE ;

    /** Permission to set permissions for a document. **/
    public final static int PERM_EDIT_PERMISSIONS        = (1 << 2) ; // 4


    /* Permissions for restricted permission-sets 1 and 2, only applicable to non-text-documents. */

    public final static int PERM_EDIT_DOCUMENT  = (1 << 16) ; // 65536

    /** Permission to edit the url of an url-document. **/
    public final static int PERM_EDIT_URL_DOCUMENT             = PERM_EDIT_DOCUMENT ;

    /** Permission to edit the mappings of a browser-document. **/
    public final static int PERM_EDIT_BROWSER_DOCUMENT         = PERM_EDIT_DOCUMENT ;

    /** Permission to edit a html-document. **/
    public final static int PERM_EDIT_HTML_DOCUMENT            = PERM_EDIT_DOCUMENT ;

    /** Permission to change content and mime-type for a file. **/
    public final static int PERM_EDIT_FILE_DOCUMENT            = PERM_EDIT_DOCUMENT ;

    /* Permissions for restricted permission-sets 1 and 2, only applicable to text-documents. */

    /** Permission to change the texts of a text-document. **/
    public final static int PERM_EDIT_TEXT_DOCUMENT_TEXTS      = PERM_EDIT_DOCUMENT ;

    /** Permission to change the images of a text-document. **/
    public final static int PERM_EDIT_TEXT_DOCUMENT_IMAGES     = (1 << 17) ; // 131072

    /** Permission to change the menus of a text-document. **/
    public final static int PERM_EDIT_TEXT_DOCUMENT_MENUS      = (1 << 18) ; // 262144

    /** Permission to change the template of a text-document. **/
    public final static int PERM_EDIT_TEXT_DOCUMENT_TEMPLATE = (1 << 19) ; // 524288

    /** Permission to change the includes of a text-document. **/
    public final static int PERM_EDIT_TEXT_DOCUMENT_INCLUDES   = (1 << 20) ; // 1048576
    
    /** Permission to use images in image archive */
    public final static int PERM_IMAGE_ARCHIVE_USE_IMAGE = (1 << 22);
    
    /** Permission to change images in image archive */
    public final static int PERM_IMAGE_ARCHIVE_CHANGE_IMAGE = (1 << 23);


    /* Log instances. */

    /** The access-log, used for keeping track of page hits. **/
    public final static String ACCESS_LOG		= "com.imcode.imcms.log.access";

    public final static String MAIN_LOG         = "com.imcode.imcms.log.main";

    final static int PASSWORD_MINIMUM_LENGTH = 4;

    int DISPATCH_FLAG__DOCINFO_PAGE = PERM_EDIT_HEADLINE;
    int DISPATCH_FLAG__EDIT_BROWSER_DOCUMENT = PERM_EDIT_BROWSER_DOCUMENT;
    int DISPATCH_FLAG__EDIT_HTML_DOCUMENT = PERM_EDIT_HTML_DOCUMENT;
    int DISPATCH_FLAG__EDIT_URL_DOCUMENT = PERM_EDIT_URL_DOCUMENT;
    int DISPATCH_FLAG__EDIT_FILE_DOCUMENT = PERM_EDIT_FILE_DOCUMENT;
    int DISPATCH_FLAG__EDIT_MENU = PERM_EDIT_TEXT_DOCUMENT_MENUS;
    int DISPATCH_FLAG__EDIT_TEXT_DOCUMENT_IMAGES = PERM_EDIT_TEXT_DOCUMENT_IMAGES;
    int DISPATCH_FLAG__DOCUMENT_PERMISSIONS_PAGE = PERM_EDIT_PERMISSIONS;

    /** Overrides default return URL which is used when a user leaves the editor or an add-on page. */
    public static final String REQUEST_PARAM__RETURN_URL = "imcms.return.url";
	
		public static final String FILE_ADMIN_ROOT_PATHS_MANDATORY_IF_EXISTS = "css/;images/;images_gui/;imcms/;javascript/;jsp/;login/;user/;WEB-INF/jsp/;WEB-INF/logs;WEB-INF/templates/" ;
}
