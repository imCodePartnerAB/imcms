package imcode.server;

import java.io.File;

public class WebAppGlobalConstants {

    public final static String USER_LOGIN_NAME_PARAMETER_NAME = "loginname";
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String LOGGED_IN_USER = "logon.isDone";

    private static WebAppGlobalConstants singletonInstance;

    private final File absoluteWebappRoot;

    /**
     * This must be called before any other method is called.
     * When the first Servlet is loaded.
     */
    public static void init(File webAppRealPath) {
        singletonInstance = new WebAppGlobalConstants(webAppRealPath);
    }

    public static WebAppGlobalConstants getInstance() {
        return singletonInstance;
    }

    public File getAbsoluteWebAppPath() {
        return absoluteWebappRoot;
    }

    private WebAppGlobalConstants(File webAppRealPath) {
        absoluteWebappRoot = webAppRealPath;
    }
}
