(ns
  ^{:doc "Vaadin test/prototype application."}  
  com.imcode.imcms.vaadin-app

  (:gen-class
   :extends com.vaadin.Application)
  
  (:import
    org.eclipse.jetty.server.Server
    (org.eclipse.jetty.servlet ServletContextHandler ServletHolder))

  (:require
    [com.imcode.imcms.vaadin-app-handler :as vaadin-app-handler])
  
  (:use    
    [clojure.main :only [load-script]]))


(defn -init[^com.vaadin.Application this]
  (vaadin-app-handler/init this))


(defn create-server [port]
  (let [server (Server. port)
        context (ServletContextHandler. ServletContextHandler/SESSIONS)
        servlet-holder (ServletHolder. (com.vaadin.terminal.gwt.server.ApplicationServlet.))]

    (.setInitParameter servlet-holder "application" "com.imcode.imcms.vaadin_app")

    (doto context
      (.setContextPath "/")
      (.addServlet servlet-holder "/*"))

    (doto server
      (.setHandler context))))


(defn restart [server]
  (load-script "@com/imcode/imcms/vaadin_app_handler.clj")  
  (doto server .stop .start))


(comment "Copy-paste into repl."
  (use 'com.imcode.imcms.vaadin-app)
  (def server (create-server 9999))
  (restart server)
)