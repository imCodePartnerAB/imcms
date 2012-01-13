package com.imcode.imcms.api;

/**
 * Usually thrown when something wrong happens when sending out emails. Like {@link MailService} not being able to contact
 * the mail server etc.
 */
public class MailException extends Exception {

    public MailException( Exception e ) {
        super(e) ;
    }
}
