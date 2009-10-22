package com.imcode.imcms.admin.backdoor;

public enum ClojureNamespace {
	
	MAINTENANCE_CONTROLLER("com.imcode.imcms.backdoor.controller", "/com/imcode/imcms/backdoor/controller"),
    SERVER_SCOKET("clojure.contrib.server-socket", "clojure.contrib.server_socket");



	/**
	 * Namespace name.
	 */
	public final String name;
	
	/**
	 * Namespace resource path relative to classpath.
	 */
	public final String resourcePath;
	
	ClojureNamespace(String name, String resourcePath) {
		this.name = name;
		this.resourcePath = resourcePath;
	}
}