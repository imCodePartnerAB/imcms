package com.imcode.imcms.admin.backdoor;

import clojure.lang.RT;
import clojure.lang.Var;
import org.apache.log4j.Logger;

public class ClojureUtils {

    private static Logger logger = Logger.getLogger(ClojureUtils.class.getName());

	public static void load(ClojureNamespace cn) {
        logger.info("Loading clojure namespace: " + cn);

		try {
			RT.load(cn.resourcePath);
		} catch (Exception e) {
            logger.error("Unable to load clojure namespace: " + cn, e);
			throw new RuntimeException(e);
		}
	}

	public static void load(String resource) {
        logger.info("Loading clojure resource: " + resource);

		try {
			RT.load(resource);
		} catch (Exception e) {
            logger.error("Unable to load clojure resource: " + resource, e);
			throw new RuntimeException(e);
		}
	}

    
	
	
	public static Var var(ClojureNamespace cn, String symbolName, Object value) {
        return RT.var(cn.name, symbolName, value);
	}

    public static Var var(ClojureNamespace cn, String symbolName) {
        return RT.var(cn.name, symbolName);
    }


    public static void startReplServer(int port) {
        logger.info("Starting Clojure Remote REPL on port " + port + ".");

        load(ClojureNamespace.SERVER_SCOKET);

        Var fn = var(ClojureNamespace.SERVER_SCOKET, "create-repl-server");

        try {
            fn.invoke(port);
        } catch (Exception e) {
            logger.error("Unable to start Clojure remote REPL.", e);
            throw new RuntimeException(e);
        }
    }

    public static void startSwankServer(int port) {
        logger.info("Starting Clojure swank on port " + port + ".");

        load("swank/swank");

        Var fn = RT.var("swank.swank", "start-repl");

        try {
            fn.invoke(port);
        } catch (Exception e) {
            logger.error("Unable to start Clojure remote REPL.", e);
            throw new RuntimeException(e);
        }
    }



}