package com.imcode.imcms.domain.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.domain.dto.AzureActiveDirectoryGroupDTO;
import com.imcode.imcms.domain.dto.AzureActiveDirectoryGroupsHolderDTO;
import com.imcode.imcms.domain.dto.AzureActiveDirectoryUserDTO;
import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.model.AuthenticationProvider;
import com.imcode.imcms.model.ExternalUser;
import com.imcode.imcms.util.AuthHelper;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Authentication provider for Azure Active Directory
 */
@EqualsAndHashCode(callSuper = true)
public class AzureAuthenticationProvider extends AuthenticationProvider
        implements AuthenticationDataStorage {

    private static final Logger log = LogManager.getLogger(AzureAuthenticationProvider.class);

    public static final String EXTERNAL_AUTHENTICATOR_AZURE_AD = "aad";
    public static final String EXTERNAL_USER_AND_ROLE_AZURE_AD = "aad";
    /**
     * Session id to state data
     */
    private static final Map<String, StateHolder> STATES = new LRUMap<>(512);
    private static final String STATE = "state";
    private static final String FAILED_TO_VALIDATE_MESSAGE = "Failed to validate data received from Authorization service - ";
    private static final long STATE_TTL = TimeUnit.HOURS.toSeconds(1);

    private final String tenant;
    private final String clientId;
    private final String secretKey;
    private final String authority = "https://login.microsoftonline.com/";

    private AuthenticationResultHolder applicationAccessToken = new NullAuthenticationResultHolder();

    public AzureAuthenticationProvider(Properties properties) {
        super(
                "https://login.microsoftonline.com/",
                EXTERNAL_AUTHENTICATOR_AZURE_AD,
                "Azure Active Directory",
                "/imcms/images/external_identifiers/azure-active-directory.svg"
        );

        tenant = properties.getProperty("aad.tenant.name");
        clientId = properties.getProperty("aad.client.id");
        secretKey = properties.getProperty("aad.secret.key");

        Objects.requireNonNull(tenant, "Azure Active Directory tenant is null!");
        Objects.requireNonNull(clientId, "Azure Active Directory client id is null!");
        Objects.requireNonNull(secretKey, "Azure Active Directory secret directory is null!");
    }

    @Override
    public void updateAuthData(HttpServletRequest request) {
        if (AuthHelper.isAuthDataExpired(AuthHelper.getAuthenticationResult(request))) {
            updateAuthDataUsingRefreshToken(request);
        }
    }

    private void updateAuthDataUsingRefreshToken(HttpServletRequest request) {
        final AuthenticationResult authData = getAccessTokenFromRefreshToken(
                AuthHelper.getAuthenticationResult(request).getRefreshToken()
        );
        AuthHelper.setAuthenticationResult(request, authData);
    }

    @SneakyThrows
    private AuthenticationResult getAccessTokenFromRefreshToken(String refreshToken) {

        final ExecutorService service = Executors.newSingleThreadExecutor();

        try {
            final AuthenticationContext context = new AuthenticationContext(
                    authority + tenant + "/", true, service
            );
            final Future<AuthenticationResult> future = context.acquireTokenByRefreshToken(
                    refreshToken, new ClientCredential(clientId, secretKey), null, null
            );

            return Optional.ofNullable(future.get())
                    .orElseThrow(() -> new ServiceUnavailableException("authentication result was null"));
        } finally {
            service.shutdown();
        }
    }

    @Override
    public void storeAuthenticationData(String sessionId, String nextUrl) {
        // use state parameter to validate response from Authorization server
        final String state = UUID.randomUUID().toString();

        // use nonce parameter to validate idToken
        final String nonce = UUID.randomUUID().toString();

        STATES.put(sessionId, new StateHolder(state, nonce, nextUrl));
    }

    @Override
    public String buildAuthenticationURL(String redirectURL, String sessionId, String nextUrl) {
        storeAuthenticationData(sessionId, nextUrl);

        final StateHolder stateHolder = STATES.get(sessionId);

        String encodedURL;

        try {
            encodedURL = URLEncoder.encode(redirectURL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encodedURL = redirectURL; // stupid situation
        }
        return authority + tenant
                + "/oauth2/authorize?response_type=code&scope=directory.read.all&response_mode=form_post"
                + "&redirect_uri=" + encodedURL
                + "&client_id=" + clientId
                + "&resource=https%3a%2f%2fgraph.microsoft.com"
                + "&state=" + stateHolder.state
                + "&nonce=" + stateHolder.nonce;
    }

    @Override
    public ExternalUser getUser(HttpServletRequest request) {
        final AuthenticationResult result = AuthHelper.getAuthenticationResult(request);

        if (result == null) {
            throw new RuntimeException("AuthenticationResult not found in session.");
        }

        return getUserFromGraph(result.getAccessToken()).toDomainObject();
    }

    private AzureActiveDirectoryUserDTO getUserFromGraph(String accessToken) {
        final AzureActiveDirectoryUserDTO userDTO = getObjectFromGraph(
                "me", accessToken, AzureActiveDirectoryUserDTO.class
        );

        final List<AzureActiveDirectoryGroupDTO> userGroups = getObjectFromGraph(
                "me/memberOf", accessToken, AzureActiveDirectoryGroupsHolderDTO.class
        ).getGroups();

        userDTO.setUserGroups(new HashSet<>(userGroups));

        return userDTO;
    }

    @SneakyThrows
    private <T> T getObjectFromGraph(String resourceName, String accessToken, Class<T> result) {
        final URL url = new URL("https://graph.microsoft.com/v1.0/" + resourceName);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        log.error(String.format("Url - %s", url.toString()));
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Accept", "application/json");

        return new ObjectMapper().readValue(conn.getInputStream(), result);
    }

    @Override
    @SneakyThrows
    public String processAuthentication(HttpServletRequest request) {
        final String currentUri = request.getRequestURL().toString();
        final String queryStr = request.getQueryString();
        final String fullUrl = currentUri + (queryStr != null ? "?" + queryStr : "");

        final Map<String, String> params = extractRequestParams(request.getParameterMap());
        final String sessionId = request.getSession().getId();

        eliminateExpiredStates();
        // validate that state in response equals to state in request
        final StateHolder stateData = validateState(sessionId, params.get(STATE));
        final AuthenticationResponse authResponse = AuthenticationResponseParser.parse(new URI(fullUrl), params);

        if (AuthHelper.isAuthenticationSuccessful(authResponse)) {
            final AuthenticationSuccessResponse oidcResponse = (AuthenticationSuccessResponse) authResponse;
            // validate that OIDC Auth Response matches Code Flow (contains only requested artifacts)
            validateAuthRespMatchesCodeFlow(oidcResponse);

            final AuthenticationResult authData = getAccessToken(oidcResponse.getAuthorizationCode(), currentUri);
            // validate nonce to prevent reply attacks (code maybe substituted to one with broader access)
            validateNonce(stateData, getClaimValueFromIdToken(authData.getIdToken(), "nonce"));
            AuthHelper.setAuthenticationResult(request, authData);

            return stateData.nextUrl;
        }

        final AuthenticationErrorResponse oidcResponse = (AuthenticationErrorResponse) authResponse;
        final ErrorObject oidcError = oidcResponse.getErrorObject();

        throw new RuntimeException(String.format(
                "Request for auth code failed: %s - %s",
                oidcError.getCode(),
                oidcError.getDescription()
        ));
    }

    private Map<String, String> extractRequestParams(Map<String, String[]> parameterMap) {
        final Map<String, String> params = new HashMap<>();

        for (String key : parameterMap.keySet()) {
            params.put(key, parameterMap.get(key)[0]);
        }

        return params;
    }

    private void validateNonce(StateHolder stateData, String nonce) {
        if (StringUtils.isEmpty(nonce) || !nonce.equals(stateData.getNonce())) {
            throw new RuntimeException(FAILED_TO_VALIDATE_MESSAGE + "could not validate nonce");
        }
    }

    private String getClaimValueFromIdToken(String idToken, String claimKey) throws ParseException {
        return (String) JWTParser.parse(idToken).getJWTClaimsSet().getClaim(claimKey);
    }

    private StateHolder validateState(String sessionId, String state) {
        return extractState(sessionId, state).orElseThrow(
                () -> new RuntimeException(FAILED_TO_VALIDATE_MESSAGE + "could not validate state")
        );
    }

    @SuppressWarnings("unchecked")
    private Optional<StateHolder> extractState(String sessionId, String state) {
        final StateHolder stateHolder = STATES.remove(sessionId);

        if ((stateHolder == null) || (StringUtils.isEmpty(state)) || (!state.equals(stateHolder.state))) {
            return Optional.empty();
        }

        return Optional.of(stateHolder);
    }

    private void eliminateExpiredStates() {
        final Iterator<Map.Entry<String, StateHolder>> it = STATES.entrySet().iterator();
        final Date currTime = new Date();

        while (it.hasNext()) {
            final long expirationTime = it.next().getValue().getExpirationDate().getTime();
            final long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(currTime.getTime() - expirationTime);

            if (diffInSeconds > STATE_TTL) {
                it.remove();
            }
        }
    }

    private void validateAuthRespMatchesCodeFlow(AuthenticationSuccessResponse oidcResponse) {
        if ((oidcResponse.getIDToken() != null)
                || (oidcResponse.getAccessToken() != null)
                || (oidcResponse.getAuthorizationCode() == null))
        {
            throw new RuntimeException(FAILED_TO_VALIDATE_MESSAGE + "unexpected set of artifacts received");
        }
    }

    private AuthenticationResult getAccessToken(AuthorizationCode authorizationCode, String currentUri)
            throws Exception {

        final String authCode = authorizationCode.getValue();
        final ClientCredential credential = new ClientCredential(clientId, secretKey);
        final ExecutorService service = Executors.newSingleThreadExecutor();

        try {
            final AuthenticationContext context = new AuthenticationContext(
                    authority + tenant + "/", true, service
            );
            final Future<AuthenticationResult> future = context.acquireTokenByAuthorizationCode(
                    authCode, new URI(currentUri), credential, null
            );
            final AuthenticationResult result = future.get();

            if (result == null) {
                throw new ServiceUnavailableException("authentication result was null");
            }

            return result;

        } finally {
            service.shutdown();
        }
    }

    @Override
    public List<ExternalRole> getRoles() {
        final AuthenticationResultHolder token = getApplicationAccessToken();
        final List<AzureActiveDirectoryGroupDTO> groups = getObjectFromGraph(
                "groups", token.getAccessToken(), AzureActiveDirectoryGroupsHolderDTO.class
        ).getGroups();

        if (!groups.isEmpty()) log.error(String.format("The first group - %s", groups.get(0)));
        return new ArrayList<>(groups);
    }

    private AuthenticationResultHolder getApplicationAccessToken() {
        if (applicationAccessToken.getExpiresOn().before(new Date())) {
            applicationAccessToken = getNewApplicationAccessToken();
        }

        return applicationAccessToken;
    }

    @SneakyThrows
    private AuthenticationResultHolder getNewApplicationAccessToken() {
        final String spec = authority + tenant + "/oauth2/v2.0/token";
        final String params = "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8.name())
                + "&client_secret=" + URLEncoder.encode(secretKey, StandardCharsets.UTF_8.name())
                + "&scope=" + URLEncoder.encode("https://graph.microsoft.com/.default", StandardCharsets.UTF_8.name())
                + "&grant_type=client_credentials";

        final OkHttpClient client = new OkHttpClient();
        final MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        final RequestBody body = RequestBody.create(mediaType, params);
        final Request request = new Request.Builder()
                .url(spec)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cache-Control", "no-cache")
                .build();

        final String result = Objects.requireNonNull(client.newCall(request).execute().body()).string();

        return new ObjectMapper().readValue(result, AuthenticationResultHolder.class);
    }

    @Data
    private class StateHolder {

        private final String state;
        private final String nonce;
        private final String nextUrl;
        private final Date expirationDate;

        StateHolder(String state, String nonce, String nextUrl) {
            this.state = state;
            this.nonce = nonce;
            this.nextUrl = nextUrl;
            this.expirationDate = new Date();
        }
    }
}
