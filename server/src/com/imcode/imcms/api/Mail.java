package com.imcode.imcms.api;

import imcode.util.net.SMTP;

import javax.activation.DataSource;

/**
 * Represents an email sent using {@link MailService}
 */
public class Mail {

    private SMTP.Mail internal ;

    /**
     * Constructs email with the given string used as the sender address
     * @param fromAddress sender address
     */
    public Mail(String fromAddress) {
        this.internal = new SMTP.Mail( fromAddress );
    }

    /**
     * Constructs email with the given from, to addresses, subject and body
     * @param fromAddress sender email address
     * @param toAddresses array of recipient addresses
     * @param subject email subject
     * @param body email body
     */
    public Mail( String fromAddress, String[] toAddresses, String subject, String body ) {
        this.internal = new SMTP.Mail( fromAddress, toAddresses, subject, body );
    }

    SMTP.Mail getInternal() {
        return internal ;
    }

    /**
     * Sets attachments for this email
     * @param attachments attachments, not null
     */
    public void setAttachments( DataSource[] attachments ) {
        internal.setAttachments( attachments );
    }

    /**
     * Sets bcc addresses
     * @param bccAddresses an array of Strings with bcc addresses, not null
     */
    public void setBccAddresses( String[] bccAddresses ) {
        internal.setBccAddresses( bccAddresses );
    }

    /**
     * Sets the text version of body of this email
     * @param body mail body
     */
    public void setBody( String body ) {
        internal.setBody( body );
    }

    /**
     * Sets the html version of body of this email
     * @param htmlBody mail body
     */
    public void setHtmlBody( String htmlBody ) {
        internal.setHtmlBody( htmlBody );
    }

    /**
     * Sets cc addresses
     * @param ccAddresses an array of Strings with cc addresses, not null
     */
    public void setCcAddresses( String[] ccAddresses ) {
        internal.setCcAddresses( ccAddresses );
    }

    /**
     * Sets the subject of this email
     * @param subject subject of this email
     */
    public void setSubject( String subject ) {
        internal.setSubject( subject );
    }

    /**
     * Sets the recipient addresses of this email
     * @param toAddresses an array of String with recipient addresses, not null
     */
    public void setToAddresses( String[] toAddresses ) {
        internal.setToAddresses( toAddresses );
    }

}
