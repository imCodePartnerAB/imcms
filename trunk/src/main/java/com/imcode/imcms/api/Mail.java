package com.imcode.imcms.api;

import imcode.util.net.SMTP;

import javax.activation.DataSource;

public class Mail {

    private SMTP.Mail internal ;

    public Mail(String fromAddress) {
        this.internal = new SMTP.Mail( fromAddress );
    }

    public Mail( String fromAddress, String[] toAddresses, String subject, String body ) {
        this.internal = new SMTP.Mail( fromAddress, toAddresses, subject, body );
    }

    SMTP.Mail getInternal() {
        return internal ;
    }

    public void setAttachments( DataSource[] attachments ) {
        internal.setAttachments( attachments );
    }

    public void setBccAddresses( String[] bccAddresses ) {
        internal.setBccAddresses( bccAddresses );
    }

    public void setBody( String body ) {
        internal.setBody( body );
    }
    
    public void setHtmlBody( String htmlBody ) {
        internal.setHtmlBody( htmlBody );
    }

    public void setCcAddresses( String[] ccAddresses ) {
        internal.setCcAddresses( ccAddresses );
    }

    public void setSubject( String subject ) {
        internal.setSubject( subject );
    }

    public void setToAddresses( String[] toAddresses ) {
        internal.setToAddresses( toAddresses );
    }

}
