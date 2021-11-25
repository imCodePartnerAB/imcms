package com.imcode.imcms.api;

import com.imcode.imcms.util.l10n.LocalizedMessage;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import javax.activation.DataSource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mail {

    private HtmlEmail mail = new HtmlEmail();
    private String textBody;

    /**
     * Constructs email with the given string used as the sender address
     *
     * @param fromAddress sender address
     */
    public Mail(String fromAddress) {
        try {
            mail.setFrom(fromAddress);
        } catch (EmailException e) {
            LocalizedMessage errorMessage = new LocalizedMessage("error/missing_email_fromAdress");
            throw new UnhandledException(errorMessage.toLocalizedStringByIso639_2("eng"), e);
        }
    }

    /**
     * Constructs email with the given from, to addresses, subject and body
     *
     * @param fromAddress sender email address
     * @param toAddresses array of recipient addresses
     * @param subject     email subject
     * @param body        email body
     */
    public Mail(String fromAddress, String[] toAddresses, String subject, String body) {
        this(fromAddress);
        setToAddresses(toAddresses);
        setSubject(subject);
        setBody(body);
    }

    private static Collection<InternetAddress> stringsToInternetAddresses(String[] addresses) {
        return Stream.of(addresses).map(Mail::stringToInternetAddress).collect(Collectors.toList());
    }

    private static InternetAddress stringToInternetAddress(String input) {
        try {
            return new InternetAddress(input, false);
        } catch (AddressException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Sets bcc addresses
     *
     * @param bccAddresses an array of Strings with bcc addresses, not null
     */
    public void setBccAddresses(String[] bccAddresses) {
        try {
            mail.setBcc(stringsToInternetAddresses(bccAddresses));
        } catch (EmailException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Sets the text version of body of this email
     *
     * @param body mail body
     */
    public void setBody(String body) {
        try {
            textBody = body;
            mail.setTextMsg(body);
        } catch (EmailException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Sets the html version of body of this email
     *
     * @param htmlBody mail body
     */
    public void setHtmlBody(String htmlBody) {
        try {
            mail.setHtmlMsg(htmlBody);
            if (null == textBody) {
                setBody(htmlBody.replaceAll("<[^>]*>", ""));
            }
        } catch (EmailException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Sets cc addresses
     *
     * @param ccAddresses an array of Strings with cc addresses, not null
     */
    public void setCcAddresses(String[] ccAddresses) {
        try {
            mail.setCc(stringsToInternetAddresses(ccAddresses));
        } catch (EmailException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Sets the subject of this email
     *
     * @param subject subject of this email
     */
    public void setSubject(String subject) {
        mail.setSubject(subject);
    }

    /**
     * Sets the recipient addresses of this email
     *
     * @param toAddresses an array of String with recipient addresses, not null
     */
    public void setToAddresses(String[] toAddresses) {
        try {
            mail.setTo(stringsToInternetAddresses(toAddresses));
        } catch (EmailException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Sets attachments for this email
     *
     * @param attachments attachments, not null
     */
    public void setAttachments(DataSource[] attachments) {
        try {
            for (DataSource attachment : attachments) {
                mail.attach(attachment, attachment.getName(), "");
            }
        } catch (EmailException e) {
            throw new UnhandledException(e);
        }
    }

    public HtmlEmail getMail() {
        return mail;
    }
}
