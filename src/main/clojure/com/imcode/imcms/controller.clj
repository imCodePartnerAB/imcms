(ns com.imcode.imcms.maintenance.controller
  (:require
    [clojure.contrib
      [server-socket :as ss]])

  (:use
    clojure.contrib.def)

  (:import
    [imcode.server Imcms]
    [com.imcode.imcms ImcmsMode]))


(defvar conf-files []
   "Application configuration files.")


(defn start-repl-server [port]
  (dosync
    (if-not @repl-server
      (ref-set repl-server (ss/create-repl-server port)))))


(defn stop-repl-server []
  (dosync
    (if @repl-server
      (do
        (.close @repl-server)   ; no stop method - it is a map
        (ref-set repl-server nil)))))


;(defn start-imcms [] )
;(defn stop-imcms [] )

(defn get-prop []
  "Returns server properties."
  (doseq [[k v]
    (imcode.server.Imcms/getServerProperties)] (println k "->" v)))
