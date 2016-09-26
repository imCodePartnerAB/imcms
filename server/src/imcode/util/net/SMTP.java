package imcode.util.net;

import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.util.Utility;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.Arrays;

/**
 * class SMTP - Provides methods for sending mail.
 *
 * @author Kreiger
 * @version $Revision$
 */
public class SMTP {

	private String host;
	private int port;

	/**
	 * Connects to an SMTP-server
	 *
	 * @param host The address of the server.
	 * @param port The port of the server, usually 25.
	 * @throws IllegalArgumentException Thrown when given a timeout of zero or less.
	 */
	public SMTP(String host, int port) {
		this.host = host;
		this.port = port;
	}

    public void sendMail(Mail mail)
            throws IOException {
        Email email = mail.getMail();

        try {
            setEncryption(email);

            email.setHostName(host);
            email.setSmtpPort(port);
            email.setCharset("UTF-8");
            email.send();
        } catch (EmailException e) {
            if (Utility.throwableContainsMessageContaining(e, "no object DCH")) {
                throw new UnhandledException("\"no object DCH\" Likely cause: the activation jar-file cannot see the mail jar-file. Different ClassLoaders?", e);
            } else {
                throw new UnhandledException(e);
            }
        }
    }

    private void setEncryption(Email mail) {
        final String encryptionProtocol = getServerProperty("encryption.protocol");
        final String accountMail = getServerProperty("mail.address");
        final String accountMailPassword = getServerProperty("mail.password");

        if ((encryptionProtocol != null) && (accountMail != null) && (accountMailPassword != null)) {
            if (encryptionProtocol.toLowerCase().equals("tls")) {
                mail.setStartTLSEnabled(true);

            } else if (encryptionProtocol.toLowerCase().equals("ssl")) {
                mail.setSSLOnConnect(true);

            } else {
                return;
            }

            mail.setAuthenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(accountMail, accountMailPassword);
                }
            });
        }
    }

    private String getServerProperty(String key) {
        return StringUtils.trimToNull(Imcms.getServerProperties().getProperty(key));
    }

	public static class Mail {

		private HtmlEmail mail = new HtmlEmail();
		private String textBody;

		public Mail(String fromAddress) {
			try {
				mail.setFrom(fromAddress);
			} catch (EmailException e) {
				LocalizedMessage errorMessage = new LocalizedMessage("error/missing_email_fromAdress");
				throw new UnhandledException(errorMessage.toLocalizedString("eng"), e);
			}
		}

		public Mail(String fromAddress, String[] toAddresses, String subject, String body) {
			this(fromAddress);
			setToAddresses(toAddresses);
			setSubject(subject);
			setBody(body);
		}

		@SuppressWarnings("unchecked")
		public void setBccAddresses(String[] bccAddresses) {
			try {
				mail.setBcc(CollectionUtils.collect(Arrays.asList(bccAddresses), new StringToInternetAddressTransformer()));
			} catch (EmailException e) {
				throw new UnhandledException(e);
			}
		}

		public void setBody(String body) {
			try {
				textBody = body;
				mail.setTextMsg(body);
			} catch (EmailException e) {
				throw new UnhandledException(e);
			}
		}

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

		@SuppressWarnings("unchecked")
		public void setCcAddresses(String[] ccAddresses) {
			try {
				mail.setCc(CollectionUtils.collect(Arrays.asList(ccAddresses), new StringToInternetAddressTransformer()));
			} catch (EmailException e) {
				throw new UnhandledException(e);
			}
		}

		public void setSubject(String subject) {
			mail.setSubject(subject);
		}

		@SuppressWarnings("unchecked")
		public void setToAddresses(String[] toAddresses) {
			try {
				mail.setTo(CollectionUtils.collect(Arrays.asList(toAddresses), new StringToInternetAddressTransformer()));
			} catch (EmailException e) {
				throw new UnhandledException(e);
			}
		}

		@SuppressWarnings("unchecked")
		public void setReplyToAddresses(String[] replyToAddresses) {
			try {
				mail.setReplyTo(CollectionUtils.collect(Arrays.asList(replyToAddresses), new StringToInternetAddressTransformer()));
			} catch (EmailException e) {
				throw new UnhandledException(e);
			}
		}

		public void setAttachments(DataSource[] attachments) {
			try {
				for (DataSource attachment : attachments) {
					mail.attach(attachment, attachment.getName(), "");
				}
			} catch (EmailException e) {
				throw new UnhandledException(e);
			}
		}

		private HtmlEmail getMail() {
			return mail;
		}

		private static class StringToInternetAddressTransformer implements Transformer {

			public Object transform(Object input) {
				try {
					return new InternetAddress((String) input, false);
				} catch (AddressException e) {
					throw new UnhandledException(e);
				}
			}
		}
	}
}
