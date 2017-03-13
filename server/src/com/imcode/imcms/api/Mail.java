package com.imcode.imcms.api;

import imcode.util.net.SMTP;

import javax.activation.DataSource;
import java.util.Map;

/**
 * Represents an email sent using {@link MailService}
 */
public class Mail {

	private SMTP.Mail internal;

	/**
	 * Constructs email with the given string used as the sender address
	 *
	 * @param fromAddress sender address
	 */
	public Mail(String fromAddress) {
		this.internal = new SMTP.Mail(fromAddress);
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
		this.internal = new SMTP.Mail(fromAddress, toAddresses, subject, body);
	}

	SMTP.Mail getInternal() {
		return internal;
	}

	/**
	 * Sets attachments for this email
	 *
	 * @param attachments attachments, not null
	 */
	public void setAttachments(DataSource[] attachments) {
		internal.setAttachments(attachments);
	}

	/**
	 * Sets bcc addresses
	 *
	 * @param bccAddresses an array of Strings with bcc addresses, not null
	 */
	public void setBccAddresses(String[] bccAddresses) {
		internal.setBccAddresses(bccAddresses);
	}

	/**
	 * Sets the text version of body of this email
	 *
	 * @param body mail body
	 */
	public void setBody(String body) {
		internal.setBody(body);
	}

	/**
	 * Sets the html version of body of this email
	 *
	 * @param htmlBody mail body
	 */
	public void setHtmlBody(String htmlBody) {
		internal.setHtmlBody(htmlBody);
	}

	/**
	 * Sets cc addresses
	 *
	 * @param ccAddresses an array of Strings with cc addresses, not null
	 */
	public void setCcAddresses(String[] ccAddresses) {
		internal.setCcAddresses(ccAddresses);
	}

	/**
	 * Sets the subject of this email
	 *
	 * @param subject subject of this email
	 */
	public void setSubject(String subject) {
		internal.setSubject(subject);
	}

	/**
	 * Sets the recipient addresses of this email
	 *
	 * @param toAddresses an array of String with recipient addresses, not null
	 */
	public void setToAddresses(String[] toAddresses) {
		internal.setToAddresses(toAddresses);
	}

	/**
	 * Sets the 'reply to' addresses of this email
	 *
	 * @param replyToAddresses an array of String with addresses which will be in 'Reply to:' field.
	 */
	public void setReplyToAddresses(String[] replyToAddresses) {
		internal.setReplyToAddresses(replyToAddresses);
	}

    /**
     * Simply adds email header with specified name and value
     *
     * @param name header name
     * @param value header value
     */
    public void addHeader(String name, String value) {
        internal.addHeader(name, value);
    }

    /**
     * Set email headers and values as kay-value map.
     * Note that this methods will replace all previous headers that was set.
     *
     * @param headers header name and header value pairs map
     */
    public void setHeaders(Map<String, String> headers) {
        internal.setHeaders(headers);
    }
}
