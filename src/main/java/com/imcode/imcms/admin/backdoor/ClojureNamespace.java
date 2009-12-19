package com.imcode.imcms.admin.backdoor;

public class ClojureNamespace {
	
	public static final ClojureNamespace BACKDOOR_CONTROLLER = new ClojureNamespace("com.imcode.imcms.backdoor.controller", "/com/imcode/imcms/backdoor/controller");
    public static final ClojureNamespace SERVER_SCOKET = new ClojureNamespace("clojure.contrib.server-socket", "clojure.contrib.server_socket");



	/**
	 * Namespace name.
	 */
	public final String name;
	
	/**
	 * Namespace resource path relative to classpath.
	 */
	public final String resourcePath;

    public ClojureNamespace(String name, String resourcePath) {
		this.name = name;
		this.resourcePath = resourcePath;
	}
}