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
        servlet-holder (ServletHolder. (com.vaadin.terminal.gwt.server.ApplicationServlet.))
        servlet-holder2 (ServletHolder. (com.vaadin.terminal.gwt.server.ApplicationServlet.))]

    (.setInitParameter servlet-holder "application" "com.imcode.imcms.vaadin_app")
    (.setInitParameter servlet-holder2 "application" "com.imcode.imcms.vaadin_app")

    (doto context
      (.setContextPath "/")
      (.addServlet servlet-holder "/*")
      (.addServlet servlet-holder "/ui/*"))

    (doto server
      (.setHandler context))))


(defn restart [server]
  (load-script "@com/imcode/imcms/vaadin_app_handler.clj")  
  (doto server .stop .start))


(comment "Copy-paste into a repl."
  (use 'com.imcode.imcms.vaadin-app)
  (def server (create-server 9999))
  (restart server)
)


(defmacro def-watched [name & value]
  `(do
     (def ~name ~@value)
     (add-watch (var ~name)
                :re-bind
                (fn [~'key ~'r old# new#]
                  (println old# " -> " new#)))))