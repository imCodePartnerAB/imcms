package imcode.server.user;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;

public class SmbAuthenticator implements Authenticator {
   private UniAddress domainServerAddress;
   private String domainName;

   public SmbAuthenticator( String domainServer, String domainName ) {
      this.domainName = domainName;
      try {
         this.domainServerAddress = UniAddress.getByName( domainServer );
      } catch( UnknownHostException e ) {
         getLogger().error( "Domain server not found.", e );
      }
   }

   public boolean authenticate( String loginName, String password ) {
      boolean result = false;
      try {
         SmbSession.logon( domainServerAddress,
                           new NtlmPasswordAuthentication( domainName, loginName, password ) );
         result = true;
      } catch( SmbAuthException e ) {
         result = false;
      } catch( SmbException e ) {
         getLogger().error( "Error logging on to domain with login " + domainName + "\\" + loginName + " and password " + password, e );
         result = false;
      }
      return result;
   }

   private Logger getLogger() {
      return Logger.getLogger( this.getClass() );
   }

}
