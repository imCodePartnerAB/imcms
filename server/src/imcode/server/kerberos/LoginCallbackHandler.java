package imcode.server.kerberos;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;


class LoginCallbackHandler implements CallbackHandler {
    private String username;
    private String password;

    public LoginCallbackHandler(String password) {
        this(null, password);
    }

    public LoginCallbackHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback && username != null) {
                NameCallback nc = (NameCallback) callback;
                nc.setName(username);
            } else if (callback instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) callback;
                pc.setPassword(password.toCharArray());

            } else {
                throw new UnsupportedCallbackException(callback, "Unrecognized callback");

            }
        }
    }

}
