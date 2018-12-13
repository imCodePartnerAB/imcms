package imcode.server.user.saml2.store;

import org.opensaml.common.IdentifierGenerator;
import org.opensaml.common.impl.RandomIdentifierGenerator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Shadowgun on 20.11.2014.
 */
final public class SAMLRequestStore {
    private static SAMLRequestStore instance = new SAMLRequestStore();
    private Set<String> samlRequestStorage = new HashSet<>();
    private IdentifierGenerator identifierGenerator = new RandomIdentifierGenerator();

    private SAMLRequestStore() {
    }

    public static SAMLRequestStore getInstance() {
        return instance;
    }

    public synchronized void storeRequest(String key) {
        if (samlRequestStorage.contains(key))
            throw new RuntimeException("SAML request storage has already contains key " + key);

        samlRequestStorage.add(key);
    }

    public synchronized String storeRequest() {
        String key = null;
        while (true) {
            key = identifierGenerator.generateIdentifier(20);
            if (!samlRequestStorage.contains(key)) {
                storeRequest(key);
                break;
            }
        }
        return key;
    }

    public synchronized boolean exists(String key) {
        return samlRequestStorage.contains(key);
    }

    public synchronized void removeRequest(String key) {
        samlRequestStorage.remove(key);
    }
}

