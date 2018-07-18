package com.imcode.imcms.domain.component;

import com.imcode.imcms.model.AuthenticationProvider;
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
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.StringUtils;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static imcode.server.DefaultImcmsServices.EXTERNAL_AUTHENTICATOR_AZURE_AD;

/**
 * Authentication provider for Azure Active Directory
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AzureAuthenticationProvider extends AuthenticationProvider implements AuthenticationDataStorage {

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

    {
        providerName = "Azure Active Directory";
        providerId = EXTERNAL_AUTHENTICATOR_AZURE_AD;
        iconPath = "/images_new/external_identifiers/azure-active-directory.svg";
    }

    public AzureAuthenticationProvider(Properties properties) {
        authenticationURL = "https://login.microsoftonline.com/";

        tenant = properties.getProperty("aad.tenant.name");
        clientId = properties.getProperty("aad.client.id");
        secretKey = properties.getProperty("aad.secret.key");

        Objects.requireNonNull(tenant, "Azure Active Directory tenant is null!");
        Objects.requireNonNull(clientId, "Azure Active Directory client id is null!");
        Objects.requireNonNull(secretKey, "Azure Active Directory secret directory is null!");
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
    public String processAuthentication(HttpServletRequest request) throws Exception {
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
            setSessionPrincipal(request, authData);

            return stateData.nextUrl;
        }

        final AuthenticationErrorResponse oidcResponse = (AuthenticationErrorResponse) authResponse;
        final ErrorObject oidcError = oidcResponse.getErrorObject();

        throw new Exception(String.format(
                "Request for auth code failed: %s - %s",
                oidcError.getCode(),
                oidcError.getDescription()
        ));
    }

    private void setSessionPrincipal(HttpServletRequest httpRequest, AuthenticationResult result) {
        httpRequest.getSession().setAttribute(AuthHelper.PRINCIPAL_SESSION_NAME, result);
    }

    private Map<String, String> extractRequestParams(Map<String, String[]> parameterMap) {
        final Map<String, String> params = new HashMap<>();

        for (String key : parameterMap.keySet()) {
            params.put(key, parameterMap.get(key)[0]);
        }

        return params;
    }

    private void validateNonce(StateHolder stateData, String nonce) throws Exception {
        if (StringUtils.isEmpty(nonce) || !nonce.equals(stateData.getNonce())) {
            throw new Exception(FAILED_TO_VALIDATE_MESSAGE + "could not validate nonce");
        }
    }

    private String getClaimValueFromIdToken(String idToken, String claimKey) throws ParseException {
        return (String) JWTParser.parse(idToken).getJWTClaimsSet().getClaim(claimKey);
    }

    private StateHolder validateState(String sessionId, String state) throws Exception {
        return extractState(sessionId, state).orElseThrow(
                () -> new Exception(FAILED_TO_VALIDATE_MESSAGE + "could not validate state")
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
        Iterator<Map.Entry<String, StateHolder>> it = STATES.entrySet().iterator();

        final Date currTime = new Date();

        while (it.hasNext()) {
            final long expirationTime = it.next().getValue().getExpirationDate().getTime();
            final long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(currTime.getTime() - expirationTime);

            if (diffInSeconds > STATE_TTL) {
                it.remove();
            }
        }
    }

    private void validateAuthRespMatchesCodeFlow(AuthenticationSuccessResponse oidcResponse) throws Exception {
        if ((oidcResponse.getIDToken() != null)
                || (oidcResponse.getAccessToken() != null)
                || (oidcResponse.getAuthorizationCode() == null))
        {
            throw new Exception(FAILED_TO_VALIDATE_MESSAGE + "unexpected set of artifacts received");
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
