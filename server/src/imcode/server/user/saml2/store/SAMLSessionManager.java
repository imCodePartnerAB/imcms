package imcode.server.user.saml2.store;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import imcode.server.Imcms;
import imcode.server.user.*;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.core.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

public class SAMLSessionManager {
    private static final String SESSION_ATTRIBUTE__NEXT_URL = "next_url";
    private static String SAML_SESSION_INFO = "SAML_SESSION_INFO";
    private static SAMLSessionManager instance = new SAMLSessionManager();

    private SAMLSessionManager() {
    }

    public static SAMLSessionManager getInstance() {
        return instance;
    }

    public void createSAMLSession(HttpServletRequest request, HttpServletResponse response, SAMLMessageContext<Response,
            SAMLObject, NameID> samlMessageContext) {
        List<Assertion> assertions = samlMessageContext.getInboundSAMLMessage().getAssertions();

        NameID nameId = (assertions.size() != 0 && assertions.get(0).getSubject() != null)
                ? assertions.get(0).getSubject().getNameID()
                : null;

        String nameValue = nameId == null
                ? null
                : nameId.getValue();

        SAMLSessionInfo samlSessionInfo = new SAMLSessionInfo(nameValue,
                getAttributesMap(getSAMLAttributes(assertions)),
                getSAMLSessionValidTo(assertions),
                getSamlSessionIndex(assertions));

        request.getSession().setAttribute(SAML_SESSION_INFO, samlSessionInfo);
        loginUser(samlSessionInfo, request, response);
    }

    public void loginUser(SAMLSessionInfo samlSessionInfo, HttpServletRequest request, HttpServletResponse response) {
        UserDomainObject user = this.prepareUser(samlSessionInfo);
        ImcmsAuthenticatorAndUserAndRoleMapper userAndRoleMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        UserDomainObject dbUser = userAndRoleMapper.getUser(user.getLoginName());

        if (dbUser != null) {

            for (RoleId roleId : dbUser.getRoleIds()) {
                user.addRoleId(roleId);
            }

            user.setId(dbUser.getId());
        } else {
            try {
                String cgiUserRoleName = Imcms.getServices().getConfig().getCgiUserRoleName();
                RoleDomainObject role = userAndRoleMapper.getRoleByName(cgiUserRoleName);

                RoleId roleId = role == null
                        ? userAndRoleMapper.addRole(cgiUserRoleName).getId()
                        : role.getId();

                user.addRoleId(roleId);
                userAndRoleMapper.addUser(user);
            } catch (UserAlreadyExistsException e) {
                e.printStackTrace();
            }
        }

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
        Utility.setUserToken(request, response, user);
        Utility.makeUserLoggedIn(request, user);
    }

    private String getSamlSessionIndex(Collection<Assertion> assertions) {

        for (Assertion assertion : assertions) {
            for (Statement statement : assertion.getStatements()) {
                if (statement instanceof AuthnStatement) {
                    return ((AuthnStatement) statement).getSessionIndex();
                }
            }
        }

        return "";
    }

    private UserDomainObject prepareUser(SAMLSessionInfo sessionInfo) {
        UserDomainObject user = new UserDomainObject();
        Map<String, String> attributes = sessionInfo.getAttributes();
        user.setActive(true);
        user.setCompany(attributes.get("Subject_OrganisationName"));
        user.setSessionId(attributes.get("CertificateSerialNumber"));
        user.setCountry(attributes.get("Subject_CountryName"));
        user.setFirstName(attributes.get("Subject_GivenName"));
        user.setLastName(attributes.get("Subject_Surname"));
        user.setLoginName(attributes.get("Subject_SerialNumber"));
        user.setImcmsExternal(true);
        user.isImcmsExternal();
        user.setLanguageIso639_2("swe");
        return user;
    }

    public boolean isSAMLSessionValid(HttpSession session) {
        SAMLSessionInfo samlSessionInfo = (SAMLSessionInfo)
                session.getAttribute(SAML_SESSION_INFO);
        return samlSessionInfo != null && (samlSessionInfo.getValidTo() == null || new
                Date().before(samlSessionInfo.getValidTo()));
    }

    public SAMLSessionInfo getSAMLSession(HttpSession session) {
        return (SAMLSessionInfo)
                session.getAttribute(SAML_SESSION_INFO);
    }

    public void destroySAMLSession(HttpSession session) {
        session.removeAttribute(SAML_SESSION_INFO);
    }

    public List<Attribute> getSAMLAttributes(List<Assertion> assertions) {
        List<Attribute> attributes = new ArrayList<>();
        if (assertions != null) {
            for (Assertion assertion : assertions) {
                for (AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
                    attributes.addAll(attributeStatement.getAttributes());
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
        Map<String, String> result = new HashMap<>();
        for (Attribute attribute : attributes) {
            result.put(attribute.getName(), attribute.getDOM().getTextContent());
        }
        return result;
    }
}
