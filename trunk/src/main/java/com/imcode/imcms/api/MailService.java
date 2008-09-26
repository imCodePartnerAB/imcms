package com.imcode.imcms.api;

import imcode.util.net.SMTP;

public class MailService {

    private SMTP smtp;

    MailService( SMTP smtp ) {
        this.smtp = smtp;
    }

    public void sendMail(Mail mail) throws MailException {
        try {
            smtp.sendMail( mail.getInternal() );
        } catch ( Exception e ) {
            throw new MailException( e );
        }
    }
}
