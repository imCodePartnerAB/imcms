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

    /* Text-doc */

    public final static int PERM_DT_TEXT_EDIT_TEXTS      = (1 << 16) ;

    public final static int PERM_DT_TEXT_EDIT_IMAGES     = (1 << 17) ;

    public final static int PERM_DT_TEXT_EDIT_MENUS      = (1 << 18) ;

    public final static int PERM_DT_TEXT_CHANGE_TEMPLATE = (1 << 19) ;

    public final static int PERM_DT_TEXT_EDIT_INCLUDES   = (1 << 20) ;

    /* Url-doc */

    public final static int PERM_DT_URL_EDIT             = (1 << 16) ;

    /* Browser-doc */

    public final static int PERM_DT_BROWSER_EDIT         = (1 << 16) ;

    /* Html-doc */

    public final static int PERM_DT_HTML_EDIT            = (1 << 16) ;

    /* File */

    public final static int PERM_DT_FILE_EDIT            = (1 << 16) ;


    /* Document-Permission-sets */

    public final static int DOC_PERM_SET_FULL            = 0 ;
    
    public final static int DOC_PERM_SET_RESTRICTED_1    = 1 ;
    
    public final static int DOC_PERM_SET_RESTRICTED_2    = 2 ;
    
    public final static int DOC_PERM_SET_READ            = 3 ;

    public final static int DOC_PERM_SET_NONE            = 4 ;

    /* Document-Permissions */

    public final static int DOC_PERM_RESTRICTED_1_ADMINISTRATES_RESTRICTED_2 = (1 << 0) ;
    

}
