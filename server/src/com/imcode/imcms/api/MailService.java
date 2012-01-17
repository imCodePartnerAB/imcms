package com.imcode.imcms.api;

import imcode.util.net.SMTP;

/**
 * Class used to send out {@link Mail}
 */
public class MailService {

    private SMTP smtp;

    MailService( SMTP smtp ) {
        this.smtp = smtp;
    }

    /**
     * Sends out the given email
     * @param mail {@link Mail} to send
     * @throws MailException if the email can't be sent due to unavailability of a mail server or invalid
     * attributes in the {@link Mail} object, such as sender address, destination addresses, cc, bcc addresses etc.
     */
    public void sendMail(Mail mail) throws MailException {
        try {
            smtp.sendMail( mail.getInternal() );
        } catch ( Exception e ) {
            throw new MailException( e );
        }
    }
}
