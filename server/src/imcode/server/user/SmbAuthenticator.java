package imcode.server.user;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;

public class SmbAuthenticator implements Authenticator {
   private final static String DOMAIN_SERVER = "loke";
   private final static String DOMAIN_NAME = "imcode";
   private UniAddress domainServerAddress;

   public SmbAuthenticator() {
      try {
         this.domainServerAddress = UniAddress.getByName( DOMAIN_SERVER );
      } catch( UnknownHostException e ) {
         getLogger().error( "Domain server not found.", e );
      }
   }

   public boolean authenticate( String loginName, String password ) {
      boolean result = false;
      try {
         SmbSession.logon( domainServerAddress, new NtlmPasswordAuthentication( DOMAIN_NAME, loginName, password ) );
         result = true;
      } catch( SmbAuthException e ) {
         result = false;
      } catch( SmbException e ) {
         getLogger().error( "Error logging on to domain with login " + DOMAIN_NAME + "\\" + loginName + " and password " + password, e );
         result = false;
      }
      return result;
   }

   private Logger getLogger() {
      return Logger.getLogger( this.getClass() );
   }

}
