package imcode.server.user.saml2.utils;

import org.opensaml.Configuration;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.binding.decoding.HTTPPostDecoder;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.ws.security.SecurityPolicy;
import org.opensaml.ws.security.SecurityPolicyResolver;
import org.opensaml.ws.security.SecurityPolicyRule;
import org.opensaml.ws.security.provider.BasicSecurityPolicy;
import org.opensaml.ws.security.provider.HTTPRule;
import org.opensaml.ws.security.provider.MandatoryIssuerRule;
import org.opensaml.ws.security.provider.StaticSecurityPolicyResolver;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.opensaml.ws.transport.http.HttpServletResponseAdapter;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by Shadowgun on 20.11.2014.
 */
public class SAMLUtils {

    public static SAMLMessageContext<Response, SAMLObject, NameID> decodeSamlMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {

        SAMLMessageContext<Response, SAMLObject, NameID> samlMessageContext =
                new BasicSAMLMessageContext<>();

        HttpServletRequestAdapter httpServletRequestAdapter =
                new HttpServletRequestAdapter(request);
        samlMessageContext.setInboundMessageTransport(httpServletRequestAdapter);
        samlMessageContext.setInboundSAMLProtocol(SAMLConstants.SAML20P_NS);
        HttpServletResponseAdapter httpServletResponseAdapter =
                new HttpServletResponseAdapter(response, request.isSecure());
        samlMessageContext.setOutboundMessageTransport(httpServletResponseAdapter);
        samlMessageContext.setPeerEntityRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);

        SecurityPolicyResolver securityPolicyResolver = getSecurityPolicyResolver(request.isSecure());

        samlMessageContext.setSecurityPolicyResolver(securityPolicyResolver);
        HTTPPostDecoder samlMessageDecoder = new HTTPPostDecoder();
        samlMessageDecoder.decode(samlMessageContext);
        return samlMessageContext;
    }

    private static SecurityPolicyResolver getSecurityPolicyResolver(boolean isSecured) {
        SecurityPolicy securityPolicy = new BasicSecurityPolicy();
        HTTPRule httpRule = new HTTPRule(null, null, isSecured);
        MandatoryIssuerRule mandatoryIssuerRule = new MandatoryIssuerRule();
        List<SecurityPolicyRule> securityPolicyRules = securityPolicy.getPolicyRules();
        securityPolicyRules.add(httpRule);
        securityPolicyRules.add(mandatoryIssuerRule);
        return new StaticSecurityPolicyResolver(securityPolicy);
    }

    public static String SAMLObjectToString(XMLObject samlObject) {
        try {
            Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(samlObject);
            Element authDOM = marshaller.marshall(samlObject);
            StringWriter rspWrt = new StringWriter();
            XMLHelper.writeNode(authDOM, rspWrt);
            return rspWrt.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
