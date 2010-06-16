package com.imcode.imcms.util.clojure;

public class ClojureResource {
	
    public static final ClojureResource SERVER_SCOKET = new ClojureResource("clojure.contrib.server-socket");

    public static final ClojureResource SWANK = new ClojureResource("swank.swank");

	/**
	 * Namespace name.
	 */
	public final String nsName;
	
	/**
	 * Namespace resource path relative to classpath.
	 */
	public final String scriptBase;


    public ClojureResource(String nsName) {
		this(nsName, nsName.replace('.', '/').replace('-', '_'));
	}


    public ClojureResource(String nsName, String scriptBase) {
		this.nsName = nsName;
		this.scriptBase = scriptBase;
	}


    @Override
    public String toString() {
        return "ClojureResource{" +
                "nsName='" + nsName + '\'' +
                ", scriptBase='" + scriptBase + '\'' +
                '}';
    }
}