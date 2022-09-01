package com.imcode.imcms.domain.component.cgi;

import com.imcode.imcms.domain.dto.cgi.CGIUserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.common.SAMLException;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.core.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CGIResponseVerifier {
	private static final Logger logger = LogManager.getLogger(CGIResponseVerifier.class);

	private CGIResponseVerifier() {
	}

	public static CGIUserDTO verifyAndGet(SAMLMessageContext<Response, SAMLObject, NameID> samlMessageContext) throws SAMLException {
		verify(samlMessageContext);

		final List<Assertion> assertions = samlMessageContext.getInboundSAMLMessage().getAssertions();
		final NameID nameId = (!assertions.isEmpty() && assertions.get(0).getSubject() != null)
				? assertions.get(0).getSubject().getNameID()
				: null;
		final String nameValue = nameId == null
				? null
				: nameId.getValue();


		return new CGIUserDTO(nameValue,
				getAttributesMap(assertions),
				getSAMLSessionValidTo(assertions),
				getSamlSessionIndex(assertions));
	}

	private static Map<String, String> getAttributesMap(List<Assertion> assertions) {
		final List<Attribute> attributes = new ArrayList<>();

		if (assertions != null) {
			assertions.stream()
					.map(Assertion::getAttributeStatements)
					.flatMap(Collection::stream)
					.forEach(attributeStatement -> {
						attributes.addAll(attributeStatement.getAttributes());
					});
		}

		return attributes.stream().collect(Collectors.toMap(Attribute::getName, attribute -> attribute.getDOM().getTextContent()));
	}

	private static DateTime getSAMLSessionValidTo(List<Assertion> assertions) {
		DateTime sessionNotOnOrAfter = null;
		if (assertions != null) {
			for (Assertion assertion : assertions) {
				sessionNotOnOrAfter = assertion.getConditions().getNotOnOrAfter();
			}
		}

		return sessionNotOnOrAfter != null ? sessionNotOnOrAfter.toDateTime(DateTimeZone.getDefault()) : null;
	}

	private static String getSamlSessionIndex(Collection<Assertion> assertions) {
		for (Assertion assertion : assertions) {
			for (Statement statement : assertion.getStatements()) {
				if (statement instanceof AuthnStatement) {
					return ((AuthnStatement) statement).getSessionIndex();
				}
			}
		}

		return "";
	}

	private static void verify(SAMLMessageContext<Response, SAMLObject, NameID> samlMessageContext) throws SAMLException {
		final Response samlResponse = samlMessageContext.getInboundSAMLMessage();

		if (logger.isDebugEnabled())
			logger.debug("SAML Response message : {}", CGIUtils.CGIResponseToString(samlResponse));

		verifyInResponseTo(samlResponse);

		final StatusCode statusCode = samlResponse.getStatus().getStatusCode();
		final String statusCodeURI = statusCode.getValue();
		if (!statusCodeURI.equals(StatusCode.SUCCESS_URI)) {
			logger.warn("Incorrect SAML message code : {} ", statusCode.getStatusCode().getValue());
			throw new SAMLException("Incorrect SAML message code : " + statusCode.getValue());
		}

		if (samlResponse.getAssertions().isEmpty()) {
			logger.error("Response does not contain any acceptable assertions");
			throw new SAMLException("Response does not contain any acceptable assertions");
		}

		final Assertion assertion = samlResponse.getAssertions().get(0);
		final NameID nameId = assertion.getSubject().getNameID();
		if (nameId == null) {
			logger.error("Name ID not present in subject");
			throw new SAMLException("Name ID not present in subject");
		}

		logger.debug("SAML authenticated user " + nameId.getValue());
		verifyConditions(assertion.getConditions());
	}

	private static void verifyInResponseTo(Response samlResponse) {
		final CGIRequestStore cgiRequestStore = CGIRequestStore.getInstance();
		final String key = samlResponse.getInResponseTo();

		if (!cgiRequestStore.exists(key)) {
			logger.error("Response does not match an authentication request");
			throw new RuntimeException("Response does not match an authentication request");
		}

		cgiRequestStore.removeRequest(samlResponse.getInResponseTo());
	}

	private static void verifyConditions(Conditions conditions) throws SAMLException {
		logger.debug("Verifying conditions");

		final DateTime currentTime = new DateTime(DateTimeZone.UTC);
		logger.debug("Current time in UTC : " + currentTime);

		final DateTime notBefore = conditions.getNotBefore();
		logger.debug("Not before condition : " + notBefore);

		if ((notBefore != null) && currentTime.isBefore(notBefore)) {
			logger.error("Assertion is not conformed with notBefore condition");
			throw new SAMLException("Assertion is not conformed with notBefore condition");
		}

		final DateTime notOnOrAfter = conditions.getNotOnOrAfter();
		logger.debug("Not on or after condition : " + notOnOrAfter);

		if ((notOnOrAfter != null) && currentTime.isAfter(notOnOrAfter)) {
			logger.error("Assertion is not conformed with notOnOrAfter condition");
			throw new SAMLException("Assertion is not conformed with notOnOrAfter condition");
		}
	}
}
