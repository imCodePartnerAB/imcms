package imcode.server.user.saml2;

import imcode.server.user.saml2.store.SAMLRequestStore;
import imcode.server.user.saml2.utils.SAMLUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.common.SAMLException;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.core.*;

/**
 * Created by Shadowgun on 20.11.2014.
 */
public class SAMLResponseVerifier {
    private static Logger log = LogManager.getLogger(SAMLResponseVerifier.class);
    private SAMLRequestStore samlRequestStore = SAMLRequestStore.getInstance();

    public void verify(SAMLMessageContext<Response, SAMLObject, NameID> samlMessageContext)
            throws SAMLException {
        Response samlResponse = samlMessageContext.getInboundSAMLMessage();
        log.debug("SAML Response message : {}", SAMLUtils.SAMLObjectToString(samlResponse));
        verifyInResponseTo(samlResponse);
        Status status = samlResponse.getStatus();
        StatusCode statusCode = status.getStatusCode();
        String statusCodeURI = statusCode.getValue();
        if (!statusCodeURI.equals(StatusCode.SUCCESS_URI)) {
            log.warn("Incorrect SAML message code : {} ",
                    statusCode.getStatusCode().getValue());
            throw new SAMLException("Incorrect SAML message code : " + statusCode.getValue());
        }
        if (samlResponse.getAssertions().size() == 0) {
            log.error("Response does not contain any acceptable assertions");
            throw new SAMLException("Response does not contain any acceptable assertions");
        }

        Assertion assertion = samlResponse.getAssertions().get(0);
        NameID nameId = assertion.getSubject().getNameID();
        if (nameId == null) {
            log.error("Name ID not present in subject");
            throw new SAMLException("Name ID not present in subject");
        }
        log.debug("SAML authenticated user " + nameId.getValue());
        verifyConditions(assertion.getConditions(), samlMessageContext);
    }

    private void verifyInResponseTo(Response samlResponse) {
        String key = samlResponse.getInResponseTo();
        if (!samlRequestStore.exists(key)) {
            log.error("Response does not match an authentication request");
            throw new RuntimeException("Response does not match an authentication request");
        }
        samlRequestStore.removeRequest(samlResponse.getInResponseTo());
    }

    private void verifyConditions(Conditions conditions, SAMLMessageContext samlMessageContext) throws SAMLException {
        verifyExpirationConditions(conditions);
        // verifyAudienceRestrictions(conditions.getAudienceRestrictions(), samlMessageContext);
    }

    private void verifyExpirationConditions(Conditions conditions) throws SAMLException {
        log.debug("Verifying conditions");
        DateTime currentTime = new DateTime(DateTimeZone.UTC);
        log.debug("Current time in UTC : " + currentTime);
        DateTime notBefore = conditions.getNotBefore();
        log.debug("Not before condition : " + notBefore);
        if ((notBefore != null) && currentTime.isBefore(notBefore))
            throw new SAMLException("Assertion is not conformed with notBefore condition");

        DateTime notOnOrAfter = conditions.getNotOnOrAfter();
        log.debug("Not on or after condition : " + notOnOrAfter);
        if ((notOnOrAfter != null) && currentTime.isAfter(notOnOrAfter))
            throw new SAMLException("Assertion is not conformed with notOnOrAfter condition");
    }

   /* private void verifyAudienceRestrictions(
			List<AudienceRestriction> audienceRestrictions,
            SAMLMessageContext<?, ?, ?> samlMessageContext)
            throws SAMLException {
    }*/
}

