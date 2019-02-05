package com.imcode.imcms.api;

import imcode.util.PropertyManager;
import imcode.util.Utility;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

@Service
public class MailService {

    private String host;
    private int port;

    /**
     * @param host The address of the server.
     * @param port The port of the server, usually 25.
     * @throws IllegalArgumentException Thrown when given a timeout of zero or less.
     */
    public MailService(@Value("${SmtpServer}") String host, @Value("${SmtpPort}") int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Sends out the given email
     *
     * @param mail {@link Mail} to send
     * @throws MailException if the email can't be sent due to unavailability of a mail server or invalid
     *                       attributes in the {@link Mail} object, such as sender address, destination addresses, cc, bcc addresses etc.
     */
    public void sendMail(Mail mail) throws MailException {
        final Email email = mail.getMail();

        try {
            setEncryption(email);

            email.setHostName(host);
            email.setSmtpPort(port);
            email.setCharset("UTF-8");
            email.send();

        } catch (EmailException e) {
            if (Utility.throwableContainsMessageContaining(e, "no object DCH")) {
                throw new MailException(new UnhandledException("\"no object DCH\" Likely cause: the activation" +
                        " jar-file cannot see the mail jar-file. Different ClassLoaders?", e));
            } else {
                throw new MailException(new UnhandledException(e));
            }
        }
    }

    private void setEncryption(Email mail) {
        final String encryptionProtocol = getServerProperty("encryption.protocol");
        final String accountMail = getServerProperty("mail.address");
        final String accountMailPassword = getServerProperty("mail.password");

        if ((encryptionProtocol != null) && (accountMail != null) && (accountMailPassword != null)) {
            switch (encryptionProtocol.toLowerCase()) {
                case "tls":
                    mail.setStartTLSEnabled(true);
                    break;
                case "ssl":
                    mail.setSSLOnConnect(true);
                    break;
                default:
                    return;
            }

            mail.setAuthenticator(new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(accountMail, accountMailPassword);
                }
            });
        }
    }

    private String getServerProperty(String key) {
        return StringUtils.trimToNull(PropertyManager.getServerProperties().getProperty(key));
    }
}
