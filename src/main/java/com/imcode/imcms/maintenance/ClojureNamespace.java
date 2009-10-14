package com.imcode.imcms.maintenance;

public enum ClojureNamespace {
	
	MAINTENANCE_CONTROLLER("com.imcode.imcms.maintenance.controller", "/com/imcode/imcms/maintenance/controller"),
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