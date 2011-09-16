package imcode.server.kerberos;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import java.io.IOException;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class KerberosLoginService {
    private static final Logger log = Logger.getLogger(KerberosLoginService.class);

    private static final String NEGOTIATE_PREFIX = "Negotiate ";

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String AUTHENTICATE_HEADER = "WWW-Authenticate";


    private Config config;

    private boolean loggedIn;

    private LoginContext loginContext;


    public KerberosLoginService(Config config) {
        this.config = config;

        if (config.isSsoEnabled()) {
            initLoginContext();
        }
    }
    
    private void initLoginContext() {
        if (loggedIn) {
            return;
        }

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

        } catch (LoginException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void dispose() {
        disposeLoginContext();
    }

    private void disposeLoginContext() {
        if (loginContext != null && loggedIn) {

            try {
                loginContext.logout();

            } catch (LoginException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    public KerberosLoginResult login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        initLoginContext();

        if (!loggedIn) {
            // Couldn't authenticate to KDC as a Kerberos service.
            return resultFailed(request, response);
        }

        String auth = StringUtils.trimToNull(request.getHeader(AUTHORIZATION_HEADER));

        if (auth == null || !auth.startsWith(NEGOTIATE_PREFIX)) {
            // Missing SPNEGO Negotiate token
            return resultNegotiate(request, response);
        }

        auth = auth.substring(NEGOTIATE_PREFIX.length());

        byte[] spnegoReqToken = Base64.decodeBase64(auth.getBytes("UTF-8"));

        if (spnegoReqToken == null || spnegoReqToken.length == 0) {
            // Missing SPNEGO Negotiate token
            return resultNegotiate(request, response);
        }

        EstablishContextResult authResult = authenticate(spnegoReqToken);

        if (authResult == null) {
            return resultFailed(request, response);
        }

        if (authResult.getSpnegoResponseToken() != null) {
            // Send back the SPNEGO response token if we have one
            byte[] data = Base64.encodeBase64(authResult.getSpnegoResponseToken());
            response.setHeader(AUTHENTICATE_HEADER, NEGOTIATE_PREFIX + new String(data, "UTF-8"));
        }

        if (!authResult.isEstablished()) {
            return resultContinue(request, response);
        }

        return loginToImcms(authResult, request, response);
    }

    private KerberosLoginResult loginToImcms(EstablishContextResult authResult, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        log.info("Client principal name: " + authResult.getClientPrincipalName());

        ImcmsServices services = Imcms.getServices();
        UserDomainObject user = services.verifyUser(authResult.getClientPrincipalName());

        if (user == null || user.isDefaultUser()) {
            return resultFailed(request, response);
        }

        ContentManagementSystem cms = Utility.initRequestWithApi(request, user);

        if (config.isDenyMultipleUserLogin()) {
            User currentUser = cms.getCurrentUser();
            currentUser.setSessionId(request.getSession().getId());
            cms.getUserService().updateUserSession(currentUser);
        }

        Utility.makeUserLoggedIn(request, user);

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
}
