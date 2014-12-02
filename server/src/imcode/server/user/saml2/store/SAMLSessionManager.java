package imcode.server.user.saml2.store;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import imcode.server.Imcms;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.core.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * Created by Shadowgun on 20.11.2014.
 */
public class SAMLSessionManager {
    private static String SAML_SESSION_INFO = "SAML_SESSION_INFO";
    private static SAMLSessionManager instance = new SAMLSessionManager();
    private static final String SESSION_ATTRIBUTE__NEXT_URL = "next_url";

    /*public static final String REQUEST_PARAMETER__NEXT_URL = SESSION_ATTRIBUTE__NEXT_URL;
    public static final String REQUEST_PARAMETER__NEXT_META = "next_meta";
    private static final String SESSION_ATTRIBUTE__NEXT_META = "next_meta";
    private static final String SESSION_ATTRIBUTE__LOGIN_TARGET = "login.target";
    public static final String REQUEST_PARAMETER__EDIT_USER = "edit_user";
    public static final String REQUEST_PARAMETER__USERNAME = "name";
    public static final String REQUEST_PARAMETER__PASSWORD = "passwd";
    public static final String REQUEST_ATTRIBUTE__ERROR = "error";*/
    private SAMLSessionManager() {
    }

    public static SAMLSessionManager getInstance() {
        return instance;
    }

    public void createSAMLSession(HttpServletRequest request, HttpServletResponse response, SAMLMessageContext<Response,
            SAMLObject, NameID> samlMessageContext) {
        List<Assertion> assertions =
                samlMessageContext.getInboundSAMLMessage().getAssertions();
        NameID nameId = (assertions.size() != 0 && assertions.get(0).getSubject() != null) ?
                assertions.get(0).getSubject().getNameID() : null;
        String nameValue = nameId == null ? null : nameId.getValue();
        SAMLSessionInfo samlSessionInfo = new SAMLSessionInfo(nameValue,
                getAttributesMap(getSAMLAttributes(assertions)),
                getSAMLSessionValidTo(assertions));
        request.getSession().setAttribute(SAML_SESSION_INFO, samlSessionInfo);


        UserDomainObject user = prepareUser(samlSessionInfo);
        ContentManagementSystem cms = Utility.initRequestWithApi(request, user);

        if (Imcms.getServices().getConfig().isDenyMultipleUserLogin()) {
            User currentUser = cms.getCurrentUser();
            currentUser.setSessionId(request.getSession().getId());
            cms.getUserService().updateUserSession(currentUser);
        }

        String rememberCd = user.getRememberCd();
        if (StringUtils.isEmpty(rememberCd)) {
            cms.getUserService().updateUserRememberCd(user);
        }
        Utility.setRememberCdCookie(request, response, user.getRememberCd());

        Utility.makeUserLoggedIn(request, user);

        try {
            response.sendRedirect("StartDoc");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private UserDomainObject prepareUser(SAMLSessionInfo sessionInfo) {
        UserDomainObject user = new UserDomainObject();
        Map<String, String> attributes = sessionInfo.getAttributes();
        user.setActive(true);
        user.setCompany(attributes.get("Subject_OrganisationName"));
        user.addRoleId(RoleId.BANKIDUSER);
        user.setSessionId(attributes.get("CertificateSerialNumber"));
        user.setCountry(attributes.get("Subject_CountryName"));
        user.setFirstName(attributes.get("Subject_GivenName"));
        user.setLastName(attributes.get("Subject_Surname"));
        user.setLoginName(attributes.get("Subject_SerialNumber"));
        user.setImcmsExternal(true);
        user.setLanguageIso639_2("swe");
        return user;
    }

    public boolean isSAMLSessionValid(HttpSession session) {
        SAMLSessionInfo samlSessionInfo = (SAMLSessionInfo)
                session.getAttribute(SAML_SESSION_INFO);
        return samlSessionInfo != null && (samlSessionInfo.getValidTo() == null || new
                Date().before(samlSessionInfo.getValidTo()));
    }

    public void destroySAMLSession(HttpSession session) {
        session.removeAttribute(SAML_SESSION_INFO);
    }

    public List<Attribute> getSAMLAttributes(List<Assertion> assertions) {
        List<Attribute> attributes = new ArrayList<Attribute>();
        if (assertions != null) {
            for (Assertion assertion : assertions) {
                for (AttributeStatement attributeStatement :
                        assertion.getAttributeStatements()) {
                    for (Attribute attribute : attributeStatement.getAttributes()) {
                        attributes.add(attribute);
                    }
                }
            }
        }
        return attributes;
    }

    public Date getSAMLSessionValidTo(List<Assertion> assertions) {
        org.joda.time.DateTime sessionNotOnOrAfter = null;
        if (assertions != null) {
            for (Assertion assertion : assertions) {
                for (AuthnStatement statement : assertion.getAuthnStatements()) {
                    sessionNotOnOrAfter = statement.getSessionNotOnOrAfter();
                }
            }
        }

        return sessionNotOnOrAfter != null ?
                sessionNotOnOrAfter.toCalendar(Locale.getDefault()).getTime() : null;
    }

    public Map<String, String> getAttributesMap(List<Attribute> attributes) {
        Map<String, String> result = new HashMap<String, String>();
        for (Attribute attribute : attributes) {
            result.put(attribute.getName(), attribute.getDOM().getTextContent());
        }
        return result;
    }
}
