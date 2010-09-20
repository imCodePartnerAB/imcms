(ns
  ^{:doc "Vaadin test application."}
  
  com.imcode.imcms.vaadin-app
  (:gen-class
   :extends com.vaadin.Application)
  
  (:import
    org.eclipse.jetty.server.Server
    (org.eclipse.jetty.servlet ServletContextHandler ServletHolder))
  
  (:use
    com.imcode.imcms.vaadin-app-handler
    (clojure (main :only [load-script]))))


(def apps (atom []))


(defn -init[^com.vaadin.Application this]
  (init-app this)
  (swap! apps conj this))


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


(defn restart-server [server] (doto server .stop .start))


(defn reload []
  (load-script "@com/imcode/imcms/vaadin_app_handler.clj")
  (doseq [app @apps] (.close app))
  (reset! apps []))

