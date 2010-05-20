package com.imcode.imcms.util.clojure;

public class ClojureResource {
	
	public static final ClojureResource BACKDOOR_CONTROLLER = new ClojureResource("com.imcode.imcms.maintenance.controller", "/com/imcode/imcms/maintenance/controller");
    public static final ClojureResource SERVER_SCOKET = new ClojureResource("clojure.contrib.server-socket", "clojure.contrib.server_socket");

	/**
	 * Namespace name.
	 */
	public final String name;
	
	/**
	 * Namespace resource path relative to classpath.
	 */
	public final String resourcePath;


    public ClojureResource(String name, String resourcePath) {
		this.name = name;
		this.resourcePath = resourcePath;
	}


    @Override
    public String toString() {
        return "ClojureResource{" +
                "name='" + name + '\'' +
                ", resourcePath='" + resourcePath + '\'' +
                '}';
    }
}