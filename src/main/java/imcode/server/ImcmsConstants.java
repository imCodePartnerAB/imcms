package imcode.server;

import java.util.Arrays;
import java.util.List;

public class
ImcmsConstants {

    public static final int DEFAULT_START_DOC_ID = 1001;

    /* Permissions for restricted permission-sets 1 and 2, applicable for all document-types. */

    /**
     * Permission to edit the headline, text, and image of a document.
     */
    public final static int PERM_EDIT_HEADLINE = 1; //(1 << 0); // 1

    /**
     * Permission to edit all docinfo for a document.
     */
    public final static int PERM_EDIT_DOCINFO = PERM_EDIT_HEADLINE;

    /**
     * Permission to set permissions for a document.
     */
    public final static int PERM_EDIT_PERMISSIONS = (1 << 2); // 4

    /* Permissions for restricted permission-sets 1 and 2, only applicable to non-text-documents. */

    public final static int PERM_EDIT_DOCUMENT = (1 << 16); // 65536

    /**
     * Permission to edit the url of an url-document.
     */
    public final static int PERM_EDIT_URL_DOCUMENT = PERM_EDIT_DOCUMENT;

    /**
     * Permission to edit a html-document.
     */
    public final static int PERM_EDIT_HTML_DOCUMENT = PERM_EDIT_DOCUMENT;

    /**
     * Permission to change content and mime-type for a file.
     */
    public final static int PERM_EDIT_FILE_DOCUMENT = PERM_EDIT_DOCUMENT;

    /* Permissions for restricted permission-sets 1 and 2, only applicable to text-documents. */

    /**
     * Permission to change the texts of a text-document.
     */
    public final static int PERM_EDIT_TEXT_DOCUMENT_TEXTS = PERM_EDIT_DOCUMENT;

    /**
     * Permission to change the images of a text-document.
     */
    public final static int PERM_EDIT_TEXT_DOCUMENT_IMAGES = (1 << 17); // 131072

    /**
     * Permission to change the menus of a text-document.
     */
    public final static int PERM_EDIT_TEXT_DOCUMENT_MENUS = (1 << 18); // 262144

    /**
     * Permission to change the template of a text-document.
     */
    public final static int PERM_EDIT_TEXT_DOCUMENT_TEMPLATE = (1 << 19); // 524288

    /**
     * Permission to change the includes of a text-document.
     */
    public final static int PERM_EDIT_TEXT_DOCUMENT_INCLUDES = (1 << 20); // 1048576

    /**
     * Permission to change content loops of a text-document.
     */
    public final static int PERM_EDIT_TEXT_DOCUMENT_CONTENT_LOOPS = (1 << 21); //  2097152

    /**
     * Permission to make document version.
     */
    public final static int PERM_PUBLISH = (1 << 22); //  4194304

    /**
     * Permission set default document version.
     */
    public final static int SET_DEFAULT_VERSION = (1 << 23); //  8388608

    /* Log instances. */

    /**
     * The access-log, used for keeping track of page hits.
     */
    public final static String ACCESS_LOG = "com.imcode.imcms.log.access";

    public final static String MAIN_LOG = "com.imcode.imcms.log.main";

    public final static String GDPR_LOG = "com.imcode.imcms.log.gdpr";

    public final static int DISPATCH_FLAG__DOCINFO_PAGE = PERM_EDIT_HEADLINE;
    public final static int DISPATCH_FLAG__EDIT_HTML_DOCUMENT = PERM_EDIT_HTML_DOCUMENT;
    public final static int DISPATCH_FLAG__EDIT_URL_DOCUMENT = PERM_EDIT_URL_DOCUMENT;
    public final static int DISPATCH_FLAG__EDIT_FILE_DOCUMENT = PERM_EDIT_FILE_DOCUMENT;
    public final static int DISPATCH_FLAG__EDIT_MENU = PERM_EDIT_TEXT_DOCUMENT_MENUS;
    public final static int DISPATCH_FLAG__EDIT_TEXT_DOCUMENT_IMAGES = PERM_EDIT_TEXT_DOCUMENT_IMAGES;
    public final static int DISPATCH_FLAG__DOCUMENT_PERMISSIONS_PAGE = PERM_EDIT_PERMISSIONS;
    public final static int DISPATCH_FLAG__PUBLISH = PERM_PUBLISH;
    public final static int DISPATCH_FLAG__SET_DEFAULT_VERSION = SET_DEFAULT_VERSION;

    @SuppressWarnings("unused")
    public final static int DISPATCH_FLAG__EDIT_TEXT_DOCUMENT_TEXTS = PERM_EDIT_HTML_DOCUMENT;

    @SuppressWarnings("unused")
    public final static int DISPATCH_FLAG__EDIT_TEXT_DOCUMENT_LOOPS = PERM_EDIT_TEXT_DOCUMENT_CONTENT_LOOPS;

    /**
     * Doc's id; 'meta_id' is the legacy identifier used across the project.
     */
    public static final String REQUEST_PARAM__DOC_ID = "meta_id";

    /**
     * Doc's language code.
     */
    public static final String REQUEST_PARAM__DOC_LANGUAGE = "lang";

    /**
     * Doc's version no or an alias.
     * {@link #REQUEST_PARAM_VALUE__DOC_VERSION__ALIAS_DEFAULT} or {@link #REQUEST_PARAM_VALUE__DOC_VERSION__ALIAS_WORKING}.
     */
    public static final String REQUEST_PARAM__DOC_VERSION = "v";

    public static final String REQUEST_PARAM_VALUE__DOC_VERSION__ALIAS_WORKING = "w";

    public static final String REQUEST_PARAM_VALUE__DOC_VERSION__ALIAS_DEFAULT = "d";

    /**
     * Overrides default return URL which is used when a user leaves the editor or an add-on page.
     */
    public static final String REQUEST_PARAM__RETURN_URL = "imcms.return.url";
    /**
     * Request param to preview working document version
     */
    public static final String REQUEST_PARAM__WORKING_PREVIEW = "working-preview";
    /**
     * Single element editor view name, file is %name%.jsp
     */
    public static final String SINGLE_EDITOR_VIEW = "editElement";

    public static final String ENG_CODE = "en";
    public static final String SWE_CODE = "sv";
    public static final String NOR_CODE = "no";
    public static final List<String> LANGUAGES = Arrays.asList("en", "sv", "be", "bs", "bg", "zh", "hr", "cs", "da", "nl", "et", "fi", "fr", "de", "el", "hu", "ga", "is", "it", "ja", "kk", "lb", "lt", "lv", "mn", "no", "pl", "pt", "ro", "ru", "sr", "gd", "sk", "sl", "es", "tr", "uk", "sma", "smj");

    public static final String ENG_CODE_ISO_639_2 = "eng";
    public static final String SWE_CODE_ISO_639_2 = "swe";

    public static final String API_PREFIX = "/api";
    public static final String LOGIN_URL = "/login";
    public static final String LOGOUT_URL = "/login/logged_out.jsp";

    public static final int MAXIMUM_PASSWORD_LENGTH = 250;
    public static final int MINIMUM_PASSWORD_LENGTH = 4;

    public static final String VIEW_DOC_PATH = "/viewDoc";
    public static final String API_VIEW_DOC_PATH = API_PREFIX + "/viewDoc";

    public static final String IMAGE_GENERATED_FOLDER = "generated";

    public static final String IMAGE_FOLDER_CACHE_NAME = "ImageFolderCache";
    public static final String LANGUAGE_CACHE_NAME = "LanguageCache";
    //Should be same as at web.xml to support cache clean at TemporalDataService.
    public static final String OTHER_CACHE_NAME = "OtherContentCachingFilter";
    public static final String STATIC_CACHE_NAME = "StaticContentCachingFilter";
    public static final String PUBLIC_CACHE_NAME = "PublicDocumentsCache";

    public static final String REINDEX_NAME = "ReIndexing";
    public static final String BUILD_CACHE_NAME = "BuildCaching";

    public static final String IMCMS_HEADER_CACHING_ACTIVE = "Process-Caching-Active";

    private ImcmsConstants() {
        throw new AssertionError();
    }
}
