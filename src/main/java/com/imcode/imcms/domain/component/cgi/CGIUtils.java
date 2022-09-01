package com.imcode.imcms.domain.component.cgi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.binding.decoding.HTTPPostDecoder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.security.SecurityPolicy;
import org.opensaml.ws.security.SecurityPolicyResolver;
import org.opensaml.ws.security.SecurityPolicyRule;
import org.opensaml.ws.security.provider.BasicSecurityPolicy;
import org.opensaml.ws.security.provider.HTTPRule;
import org.opensaml.ws.security.provider.MandatoryIssuerRule;
import org.opensaml.ws.security.provider.StaticSecurityPolicyResolver;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;
import java.util.List;

public final class CGIUtils {
	private static final Logger logger = LogManager.getLogger(CGIUtils.class);
	private static final HTTPPostDecoder decoder = new HTTPPostDecoder();

	public static AuthnRequest buildRequest(String id, String redirectUrl) {
		final CGIConfig cgiConfig = CGIConfig.getInstance();
		/* Building Issuer object */
		IssuerBuilder issuerBuilder = new IssuerBuilder();
		Issuer issuer = issuerBuilder.buildObject(
				"urn:oasis:names:tc:SAML:2.0:assertion", "Issuer", "saml2p");
		issuer.setValue(cgiConfig.getSpProviderId());

		/* Creation of AuthRequestObject */
		AuthnRequestBuilder authRequestBuilder = new AuthnRequestBuilder();

		AuthnRequest authRequest = authRequestBuilder.buildObject(
				SAMLConstants.SAML20P_NS, "AuthnRequest", "saml2p");
		authRequest.setID(id);
		authRequest.setForceAuthn(false);
		authRequest.setIssueInstant(DateTime.now());
		authRequest.setProtocolBinding(SAMLConstants.SAML2_POST_BINDING_URI);
		authRequest.setAssertionConsumerServiceURL(redirectUrl);
		authRequest.setIssuer(issuer);
		authRequest.setVersion(SAMLVersion.VERSION_20);
		authRequest.setDestination(cgiConfig.getIdpSSOLoginUrl());

		return authRequest;
	}

	public static SAMLMessageContext<Response, SAMLObject, NameID> decodeCGIResponse(HttpServletRequest request) {
		try {
			final SAMLMessageContext<Response, SAMLObject, NameID> samlMessageContext = new BasicSAMLMessageContext<>();

			samlMessageContext.setInboundMessageTransport(new HttpServletRequestAdapter(request));
			samlMessageContext.setInboundSAMLProtocol(SAMLConstants.SAML20P_NS);
			samlMessageContext.setPeerEntityRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
			samlMessageContext.setSecurityPolicyResolver(getSecurityPolicyResolver(request.isSecure()));
			samlMessageContext.setLocalEntityId(CGIConfig.getInstance().getSpProviderId());

			decoder.decode(samlMessageContext);

			return samlMessageContext;
		} catch (MessageDecodingException | SecurityException e) {
			logger.error("Error while decoding CGI response", e);
			throw new RuntimeException(e);
		}
	}

	private static SecurityPolicyResolver getSecurityPolicyResolver(boolean isSecured) {
		final SecurityPolicy securityPolicy = new BasicSecurityPolicy();
		final MandatoryIssuerRule mandatoryIssuerRule = new MandatoryIssuerRule();

		final List<SecurityPolicyRule> securityPolicyRules = securityPolicy.getPolicyRules();

		securityPolicyRules.add(new HTTPRule(null, null, isSecured));
		securityPolicyRules.add(mandatoryIssuerRule);

		return new StaticSecurityPolicyResolver(securityPolicy);
	}

	public static String CGIResponseToString(XMLObject samlObject) {
		try {
			final Element authDOM = Configuration.getMarshallerFactory().getMarshaller(samlObject).marshall(samlObject);
			final StringWriter rspWrt = new StringWriter();

			XMLHelper.writeNode(authDOM, rspWrt);

			return rspWrt.toString();
		} catch (MarshallingException e) {
			logger.error("Error while parsing CGI response to String", e);
			return null;
		}
	}
}