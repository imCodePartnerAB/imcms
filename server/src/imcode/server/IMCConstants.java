package imcode.server ;

public interface IMCConstants {

    /* Documenttypes */
    
    /** Text-doc **/
    public final static int DOCTYPE_TEXT      = 2 ;

    /** URL-doc **/
    public final static int DOCTYPE_URL       = 5 ;

    /** Browser-doc **/
    public final static int DOCTYPE_BROWSER   = 6 ;

    /** HTML-doc **/
    public final static int DOCTYPE_HTML      = 7 ;

    /** File **/
    public final static int DOCTYPE_FILE      = 8 ;


    /* Permissions */

    public final static int PERM_EDIT_HEADLINE           = (1 << 0) ; // 1

    public final static int PERM_EDIT_DOCINFO            = (1 << 1) ; // 2

    public final static int PERM_EDIT_PERMISSIONS        = (1 << 2) ; // 4

    public final static int PERM_CREATE_DOCUMENT         = (1 << 3) ; // 8
 
    /* Text-doc */

    public final static int PERM_DT_TEXT_EDIT_TEXTS      = (1 << 16) ; // 65536

    public final static int PERM_DT_TEXT_EDIT_IMAGES     = (1 << 17) ; // 131072

    public final static int PERM_DT_TEXT_EDIT_MENUS      = (1 << 18) ; // 262144

    public final static int PERM_DT_TEXT_CHANGE_TEMPLATE = (1 << 19) ; // 524288

    public final static int PERM_DT_TEXT_EDIT_INCLUDES   = (1 << 20) ; // 1048576

    /* Url-doc */

    public final static int PERM_DT_URL_EDIT             = (1 << 16) ; // 65536

    /* Browser-doc */

    public final static int PERM_DT_BROWSER_EDIT         = (1 << 16) ; // 65536

    /* Html-doc */

    public final static int PERM_DT_HTML_EDIT            = (1 << 16) ; // 65536

    /* File */

    public final static int PERM_DT_FILE_EDIT            = (1 << 16) ; // 65536


    /* Document-Permission-sets */

    public final static int DOC_PERM_SET_FULL            = 0 ;
    
    public final static int DOC_PERM_SET_RESTRICTED_1    = 1 ;
    
    public final static int DOC_PERM_SET_RESTRICTED_2    = 2 ;
    
    public final static int DOC_PERM_SET_READ            = 3 ;

    public final static int DOC_PERM_SET_NONE            = 4 ;

    /* Document-Permissions */

    public final static int DOC_PERM_RESTRICTED_1_ADMINISTRATES_RESTRICTED_2 = (1 << 0) ; // 1
    

}
