package imcode.server.kerberos;

import java.security.PrivilegedAction;
import org.apache.log4j.Logger;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;


class EstablishContextAction implements PrivilegedAction<EstablishContextResult> {
    private static final Logger log = Logger.getLogger(EstablishContextAction.class);

    private final byte[] requestToken;


    public EstablishContextAction(byte[] requestToken) {
        this.requestToken = requestToken;
    }

    
    public EstablishContextResult run() {
        GSSContext context = null;

        try {
            GSSManager manager = GSSManager.getInstance();
            context = manager.createContext((GSSCredential) null);

            byte[] responseToken = context.acceptSecContext(requestToken, 0, requestToken.length);
            
            EstablishContextResult result = new EstablishContextResult();
            result.setSpnegoResponseToken(responseToken);
            result.setEstablished(context.isEstablished());

            if (context.isEstablished()) {
                result.setClientPrincipalName(context.getSrcName().toString());
            }

            return result;

        } catch (GSSException ex) {
            if (ex.getMajor() == GSSException.DEFECTIVE_TOKEN) {
                log.warn("Major: " + ex.getMajorString());
                log.warn("Minor: " + ex.getMinorString());
                return null;
            }

            log.error(ex.getMessage(), ex);

        } finally {
            if (context != null) {
                try {
                    context.dispose();
                } catch (GSSException ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        }

        return null;
    }
}
