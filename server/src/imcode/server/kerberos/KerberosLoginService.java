package imcode.server.kerberos;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class KerberosLoginService {
    private static final Logger log = LogManager.getLogger(KerberosLoginService.class);

    private static final String NEGOTIATE_PREFIX = "Negotiate ";

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String AUTHENTICATE_HEADER = "WWW-Authenticate";


    private Config config;

    private transient boolean loggedIn;

    private transient LoginContext loginContext;


    public KerberosLoginService(Config config) {
        this.config = config;

        if (config.isSsoEnabled()) {
            initLoginContext();
        }
    }

    private static KerberosLoginResult resultNegotiate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader(AUTHENTICATE_HEADER, "Negotiate");

        return resultContinue(request, response);
    }

    private static KerberosLoginResult resultContinue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Utility.forwardToLogin(request, response, HttpServletResponse.SC_UNAUTHORIZED);

        return new KerberosLoginResult(KerberosLoginStatus.CONTINUE);
    }

    private static KerberosLoginResult resultFailed(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Utility.forwardToLogin(request, response);

        return new KerberosLoginResult(KerberosLoginStatus.FAILED);
    }

    private synchronized void initLoginContext() {
        dispose();

        if (log.isDebugEnabled()) {
            log.debug("Logging in to the KDC");
        }

//        if (loggedIn) {
//            if (log.isDebugEnabled()) {
//                log.debug("Already logged in to the KDC");
//            }
//
//            return;
//        }

        try {
            LoginCallbackHandler callback = new LoginCallbackHandler(config.getSsoJaasPrincipalPassword());
            loginContext = new LoginContext(config.getSsoJaasConfigName(), callback);
            loginContext.login();

            loggedIn = true;

            Subject serviceSubject = loginContext.getSubject();

            for (KerberosTicket ticket : serviceSubject.getPrivateCredentials(KerberosTicket.class)) {
                log.info("AuthTime: " + ticket.getAuthTime());
                log.info("StartTime: " + ticket.getStartTime());
                log.info("EndTime: " + ticket.getEndTime());
                log.info("RenewTill: " + ticket.getRenewTill());

                KerberosPrincipal client = ticket.getClient();
                log.info("Client: name=" + client.getName() + ", realm=" + client.getRealm());

                KerberosPrincipal server = ticket.getServer();
                log.info("Server: name=" + server.getName() + ", realm=" + server.getRealm());
            }

            if (log.isDebugEnabled()) {
                log.debug("Successfully logged in to the KDC");
            }
        } catch (LoginException ex) {
            log.error("Failed to login to the KDC" + ex.getMessage(), ex);
        }
    }

    public void dispose() {
        disposeLoginContext();
    }

    private void disposeLoginContext() {
        if (loginContext != null && loggedIn) {
            log.debug("Logging out from KDC");
            try {
                loginContext.logout();
                loginContext = null;
                loggedIn = false;
            } catch (LoginException ex) {
                log.error("KDC logout failed: " + ex.getMessage(), ex);
            }
        }
    }

    public KerberosLoginResult login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (log.isDebugEnabled()) {
            log.debug("Authenticating web user");
        }

        initLoginContext();

        if (!loggedIn) {
            if (log.isDebugEnabled()) {
                log.error("Unable to authenticating web user - not logged it to the KDC");
            }

            // Couldn't authenticate to KDC as a Kerberos service.
            return resultFailed(request, response);
        }

        String auth = StringUtils.trimToNull(request.getHeader(AUTHORIZATION_HEADER));

        if (auth == null || !auth.startsWith(NEGOTIATE_PREFIX)) {
            // Missing SPNEGO Negotiate token
            log.info(String.format("Missing SPNEGO Negotiate token, returning NEGOTIATE. %s is %s",
                    AUTHORIZATION_HEADER, auth));
            return resultNegotiate(request, response);
        }

        auth = auth.substring(NEGOTIATE_PREFIX.length());

        byte[] spnegoReqToken = Base64.decodeBase64(auth.getBytes(StandardCharsets.UTF_8));

        if (spnegoReqToken == null || spnegoReqToken.length == 0) {
            // Missing SPNEGO Negotiate token
            log.info("Missing SPNEGO Negotiate token, returning NEGOTIATE");
            return resultNegotiate(request, response);
        }

        if (isNTLMSSPBlob(spnegoReqToken, 0)) {
            log.warn("Client sent an NTLMSSP security blob");
        }

        EstablishContextResult authResult = authenticate(spnegoReqToken);

        if (authResult == null) {
            log.warn("Web user authentication failed (authResult is null)");
            return resultFailed(request, response);
        }

        if (authResult.getSpnegoResponseToken() != null) {
            // Send back the SPNEGO response token if we have one
            byte[] data = Base64.encodeBase64(authResult.getSpnegoResponseToken());
            String authenticateHeader = NEGOTIATE_PREFIX + new String(data, StandardCharsets.UTF_8);
            response.setHeader(AUTHENTICATE_HEADER, authenticateHeader);

            if (log.isDebugEnabled()) {
                log.debug(String.format("Sending back SPNEGO response token: %s=%s ", AUTHENTICATE_HEADER, authenticateHeader));
            }
        }

        if (!authResult.isEstablished()) {
            log.warn("Web user authentication failed (authResult.isEstablished() is false)");
            return resultContinue(request, response);
        }

        return loginToImcms(authResult, request, response);
    }

    private KerberosLoginResult loginToImcms(EstablishContextResult authResult, HttpServletRequest request,
                                             HttpServletResponse response) throws ServletException, IOException {

        String principalName = authResult.getClientPrincipalName();
        log.info("web user (client) principal name: " + principalName);

        ImcmsServices services = Imcms.getServices();
        UserDomainObject user = services.verifyUser(principalName);

        if (user == null || user.isDefaultUser()) {
            log.warn(String.format("Unable to authenticate web user (principal) %s. Outcome: %s", principalName, user));
            return resultFailed(request, response);
        }

        ContentManagementSystem cms = Utility.initRequestWithApi(request, user);

        if (config.isDenyMultipleUserLogin()) {
            User currentUser = cms.getCurrentUser();
            currentUser.setSessionId(request.getSession().getId());
            cms.getUserService().updateUserSession(currentUser);
        }

        Utility.makeUserLoggedIn(request, response, user);

        KerberosLoginResult loginResult = new KerberosLoginResult(KerberosLoginStatus.SUCCESS);
        loginResult.setContentManagementSystem(cms);

        return loginResult;
    }

    private EstablishContextResult authenticate(byte[] spnegoReqToken) {
        try {
            EstablishContextAction action = new EstablishContextAction(spnegoReqToken);

            return Subject.doAs(loginContext.getSubject(), action);

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * Check if a security blob starts with the NTLMSSP signature
     *
     * @param byts   byte[]
     * @param offset int
     * @return boolean
     */
    private boolean isNTLMSSPBlob(byte[] byts, int offset) {
        // Check if the blob has the NTLMSSP signature

        boolean isNTLMSSP = false;

        if ((byts.length - offset) >= NTLM.Signature.length) {

            if (log.isDebugEnabled())
                log.debug("Checking if the blob has the NTLMSSP signature.");
            // Check for the NTLMSSP signature

            int idx = 0;
            while (idx < NTLM.Signature.length && byts[offset + idx] == NTLM.Signature[idx])
                idx++;

            if (idx == NTLM.Signature.length)
                isNTLMSSP = true;
        }

        return isNTLMSSP;
    }

    private static class NTLM {

        // Signature

        public static final byte[] Signature = "NTLMSSP\u0000".getBytes();

        // Message types

        public static final int Type1 = 1;
        public static final int Type2 = 2;
        public static final int Type3 = 3;

        // NTLM flags

        public static final int FlagNegotiateUnicode = 0x00000001;
        public static final int FlagNegotiateOEM = 0x00000002;
        public static final int FlagRequestTarget = 0x00000004;
        public static final int FlagNegotiateSign = 0x00000010;
        public static final int FlagNegotiateSeal = 0x00000020;
        public static final int FlagDatagramStyle = 0x00000040;
        public static final int FlagLanManKey = 0x00000080;
        public static final int FlagNegotiateNetware = 0x00000100;
        public static final int FlagNegotiateNTLM = 0x00000200;
        public static final int FlagDomainSupplied = 0x00001000;
        public static final int FlagWorkstationSupplied = 0x00002000;
        public static final int FlagLocalCall = 0x00004000;
        public static final int FlagAlwaysSign = 0x00008000;
        public static final int FlagChallengeInit = 0x00010000;
        public static final int FlagChallengeAccept = 0x00020000;
        public static final int FlagChallengeNonNT = 0x00040000;
        public static final int FlagNTLM2Key = 0x00080000;
        public static final int FlagTargetInfo = 0x00800000;
        public static final int FlagRequestVersion = 0x02000000;
        public static final int Flag128Bit = 0x20000000;
        public static final int FlagKeyExchange = 0x40000000;
        public static final int Flag56Bit = 0x80000000;

        // Target information types

        public static final int TargetServer = 0x0001;
        public static final int TargetDomain = 0x0002;
        public static final int TargetFullDNS = 0x0003;
        public static final int TargetDNSDomain = 0x0004;
    }
}
