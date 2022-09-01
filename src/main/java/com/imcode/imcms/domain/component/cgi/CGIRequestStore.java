package com.imcode.imcms.domain.component.cgi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensaml.common.IdentifierGenerator;
import org.opensaml.common.impl.RandomIdentifierGenerator;

import java.util.HashSet;
import java.util.Set;

public class CGIRequestStore {
	private final Logger logger = LogManager.getLogger(CGIRequestStore.class);
	private static final CGIRequestStore instance = new CGIRequestStore();
	private final Set<String> samlRequestStorage = new HashSet<>();
	private final IdentifierGenerator identifierGenerator = new RandomIdentifierGenerator();

	private CGIRequestStore() {
	}

	public static CGIRequestStore getInstance() {
		return instance;
	}

	public synchronized void storeRequest(String key) {
		if (samlRequestStorage.contains(key))
			logger.error("SAML request storage has already contains key " + key);
		samlRequestStorage.add(key);
	}

	public synchronized String storeRequest() {
		String key;
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
