package com.imcode.imcms.util.clojure;

import clojure.lang.RT;
import clojure.lang.Var;
import org.apache.log4j.Logger;

public class ClojureUtils {

    private static Logger logger = Logger.getLogger(ClojureUtils.class.getName());

	public static void load(ClojureResource cn) {
        logger.info("Loading clojure resource: " + cn);

		try {
			RT.load(cn.scriptBase);
		} catch (Exception e) {
            logger.error("Unable to load clojure resource: " + cn, e);
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

    
	
	
	public static Var var(ClojureResource cn, String symbolName, Object value) {
        return RT.var(cn.nsName, symbolName, value);
	}

    public static Var var(ClojureResource cn, String symbolName) {
        return RT.var(cn.nsName, symbolName);
    }


    public static void startReplServer(int port) {
        logger.info("Starting REPL on port " + port + ".");

        load(ClojureResource.SERVER_SCOKET);

        Var fn = var(ClojureResource.SERVER_SCOKET, "create-repl-server");

        try {
            fn.invoke(port);
        } catch (Exception e) {
            logger.error("Unable to start REPL.", e);
            throw new RuntimeException(e);
        }
    }

    
    public static void startSwankServer(int port) {
        logger.info("Starting Swank server on port " + port + ".");

        load(ClojureResource.SWANK);

        Var fn = RT.var("swank.swank", "start-repl");

        try {
            fn.invoke(port);
        } catch (Exception e) {
            logger.error("Unable to start Swank server.", e);
            throw new RuntimeException(e);
        }
    }


    public static void prepareDB() {
        ClojureResource r = new ClojureResource("com.imcode.imcms.runtime");
        
        load(r);

        try {
            var(r, "prepare-db").invoke();
        } catch (Exception e) {
            String msg = "Invocation error.";
            
            logger.error(msg, e);
            
            throw new RuntimeException(msg, e);
        }
    }
}