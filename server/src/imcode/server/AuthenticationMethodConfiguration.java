package imcode.server;

/**
 * Created by Shadowgun on 01.12.2014.
 */
public class AuthenticationMethodConfiguration {

    public static final String AUTHENTICATION_METHOD_NAMING_PATTERN = "authentication-method-{replace}";
    public static final String AUTHENTICATION_REPLACEABLE = "{replace}";
    public static final String AUTHENTICATION_URL_NAMING_PATTERN = "{replace}-authentication-method-url";
    private String mName = "";
    private String mUrl = "";
    private int mOrder = 1;

    public String getName() {
        return mName;
    }

    public AuthenticationMethodConfiguration setName(String name) {
        this.mName = name;
        return this;
    }

    public int getOrder() {
        return mOrder;
    }

    public AuthenticationMethodConfiguration setOrder(int order) {
        this.mOrder = order;
        return this;
    }


    public String getUrl() {
        return mUrl;
    }

    public AuthenticationMethodConfiguration setUrl(String url) {
        this.mUrl = url;
        return this;
    }

}
