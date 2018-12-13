package imcode.server.user.saml2;

import imcode.server.user.saml2.store.SAMLRequestStore;
import imcode.server.user.saml2.store.SAMLSessionInfo;
import imcode.server.user.saml2.utils.SAMLUtils;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.SessionIndex;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.LogoutRequestBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.SessionIndexBuilder;
import org.opensaml.util.URLBuilder;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.transport.http.HTTPTransportUtils;
import org.opensaml.ws.transport.http.HttpServletResponseAdapter;
import org.opensaml.xml.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class SAMLRequestSender {
    private static Logger log = LoggerFactory.getLogger(SAMLRequestSender.class);
    private SAMLAuthnRequestBuilder samlAuthnRequestBuilder =
            new SAMLAuthnRequestBuilder();
    private MessageEncoder messageEncoder = new MessageEncoder();
    private SAMLLogoutRequestBuilder samlLogoutRequestBuilder = new SAMLLogoutRequestBuilder();

    public void sendSAMLAuthRequest(HttpServletRequest request, HttpServletResponse
            servletResponse, String spId, String acsUrl, String idpSSOUrl) throws Exception {
        String redirectURL;
        AuthnRequest authnRequest = samlAuthnRequestBuilder.buildRequest(spId, acsUrl,
                idpSSOUrl);
        // store SAML 2.0 authentication request
        String key = SAMLRequestStore.getInstance().storeRequest();
        authnRequest.setID(key);
        log.debug("SAML Authentication message : {} ",
                SAMLUtils.SAMLObjectToString(authnRequest));
        redirectURL = messageEncoder.encode(authnRequest, idpSSOUrl, request.getRequestURI());

        HttpServletResponseAdapter responseAdapter =
                new HttpServletResponseAdapter(servletResponse, request.isSecure());
        HTTPTransportUtils.addNoCacheHeaders(responseAdapter);
        HTTPTransportUtils.setUTF8Encoding(responseAdapter);
        responseAdapter.sendRedirect(redirectURL);
    }

    public void sendSAMLLogoutRequest(HttpServletRequest request, HttpServletResponse servletResponse, String spId, String idpSSOLogoutUrl, SAMLSessionInfo sessionInfo) throws Exception {
        LogoutRequest logoutRequest = this.samlLogoutRequestBuilder.buildRequest(spId, sessionInfo);
        String key = SAMLRequestStore.getInstance().storeRequest();
        logoutRequest.setID(key);
        log.debug("SAML Logout message : {} ", SAMLUtils.SAMLObjectToString(logoutRequest));
        String redirectURL = this.messageEncoder.encode(logoutRequest, idpSSOLogoutUrl, request.getRequestURI());
        HttpServletResponseAdapter responseAdapter = new HttpServletResponseAdapter(servletResponse, request.isSecure());
        HTTPTransportUtils.addNoCacheHeaders(responseAdapter);
        HTTPTransportUtils.setUTF8Encoding(responseAdapter);
        responseAdapter.sendRedirect(redirectURL);
    }

    private static class SAMLAuthnRequestBuilder {

        public AuthnRequest buildRequest(String spProviderId, String acsUrl, String idpUrl) {
            /* Building Issuer object */
            IssuerBuilder issuerBuilder = new IssuerBuilder();
            Issuer issuer =
                    issuerBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion",
                            "Issuer", "saml2p");
            issuer.setValue(spProviderId);

            /* Creation of AuthRequestObject */
            DateTime issueInstant = new DateTime();
            AuthnRequestBuilder authRequestBuilder = new AuthnRequestBuilder();

            AuthnRequest authRequest =
                    authRequestBuilder.buildObject(SAMLConstants.SAML20P_NS,
                            "AuthnRequest", "saml2p");
            authRequest.setForceAuthn(false);
            authRequest.setIssueInstant(issueInstant);
            authRequest.setProtocolBinding(SAMLConstants.SAML2_POST_BINDING_URI);
            authRequest.setAssertionConsumerServiceURL(acsUrl);
            authRequest.setIssuer(issuer);
            authRequest.setVersion(SAMLVersion.VERSION_20);
            authRequest.setDestination(idpUrl);

            return authRequest;
        }
    }

    private static class MessageEncoder extends HTTPRedirectDeflateEncoder {
        public String encode(SAMLObject message, String endpointURL, String relayState)
                throws MessageEncodingException {
            String encodedMessage = deflateAndBase64Encode(message);
            return buildRedirectURL(endpointURL, relayState, encodedMessage);
        }

        public String buildRedirectURL(String endpointURL, String relayState, String message) {
            URLBuilder urlBuilder = new URLBuilder(endpointURL);
            List<Pair<String, String>> queryParams = urlBuilder.getQueryParams();
            queryParams.clear();
            queryParams.add(new Pair<>("mgvhostparam", "0"));
            queryParams.add(new Pair<>("SAMLRequest", message));
            if (checkRelayState(relayState)) {
                queryParams.add(new Pair<>("RelayState", relayState));
            }
            return urlBuilder.buildURL();
        }
    }

    private static class SAMLLogoutRequestBuilder {
        public LogoutRequest buildRequest(String spProviderId, SAMLSessionInfo info) {
            IssuerBuilder issuerBuilder = new IssuerBuilder();
            Issuer issuer = issuerBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion", "Issuer", "saml2p");
            issuer.setValue(spProviderId);
            DateTime issueInstant = new DateTime();
            LogoutRequest logoutRequest = (new LogoutRequestBuilder()).buildObject("urn:oasis:names:tc:SAML:2.0:protocol", "LogoutRequest", "saml2p");
            logoutRequest.setIssueInstant(issueInstant);
            logoutRequest.setIssuer(issuer);
            NameID nameID = (new NameIDBuilder()).buildObject("urn:oasis:names:tc:SAML:2.0:assertion", "NameID", "saml2");
            nameID.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
            nameID.setValue(info.getNameId());
            SessionIndex sessionIndex = (new SessionIndexBuilder()).buildObject();
            sessionIndex.setSessionIndex(info.getSessionIndex());
            logoutRequest.setNameID(nameID);
            logoutRequest.getSessionIndexes().add(sessionIndex);
            logoutRequest.setVersion(SAMLVersion.VERSION_20);
            return logoutRequest;
        }
    }
}
