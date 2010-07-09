(ns
  #^{:doc "Provides fns for accessing and altering initialized imCMS instance.
           Plase note that the imcode.server.Imcms class must be properly initialized
           before any call to this namespace fn."}
  
  com.imcode.imcms.instance

  (:import
    (imcode.server Imcms)
    (imcode.server.document DocumentDomainObject)
    (imcode.server.user UserDomainObject))

  (:require
    [com.imcode.imcms.db :as db]

    (com.imcode.cljlib
      [db :as db-lib]
      [fs :as fs-lib]
      [spring :as spring-lib])

    (clojure.contrib
      [logging :as log]))  
    
  (:use
    com.imcode.imcms.conf-utils
    
    (com.imcode.cljlib
      [misc :only (dump)])

    (clojure.contrib
      [except :only (throw-if throw-if-not throwf)])))


(defmacro invoke
  "Invokes Imcms class static method."
  [method & args]
  `(. Imcms ~method ~@args))


(defn spring-context []
  (invoke getApplicationContext))

(defn start []
  (invoke start))

(defn stop []
  (invoke stop))

(defn start-ex []
  (invoke getStartEx))

(defn mode []
  (invoke getMode))

(defn set-normal-mode []
  (invoke setNormalMode))

(defn set-maintenance-mode []
  (invoke setMaintenanceMode))

(defn path []
  (invoke getPath))

(defn base-dir []
  (.getCanonicalPath (path))) 

(defn services []
  (invoke getServices))

(defn i18n-support []
  (invoke getI18nSupport))

(defn langs []
  (.getLanguages (i18n-support)))

(defn default-lang []
  (.getDefaultLanguage (i18n-support)))

(defn find-lang-by-code [#^String code]
  (if-let [lang (.getByCode (i18n-support) code)]
    lang
    (throwf "Language with code [%s] can not be found." code)))

(defmulti  to-lang class)

(defmethod to-lang com.imcode.imcms.api.I18nLanguage [lang] lang)
(defmethod to-lang String                            [lang] (find-lang-by-code lang))
(defmethod to-lang clojure.lang.Named                [lang] (find-lang-by-code (name lang)))


(defn server-properties
  "Returns server.properties content as a map sorted by property name."
  []
  (into (sorted-map) (Imcms/getServerProperties)))


(defn print-server-properties []
  (dump (server-properties)))


(defn conf-file
  "Returns conf file.
   Throws an exception if conf file can not be found."
  []
  (log/debug "Looking for configuration file conf.clj in the classpath.")
  (if-let [conf-file-url (ClassLoader/getSystemResource "conf.clj")]
    (java.io.File. (.getPath conf-file-url))

    (let [conf-file-path (str (base-dir) "/WEB-INF/classes")
          conf-file (java.io.File. conf-file-path "conf.clj")]

      (log/debug "Looking for configuration file conf.clj in %s." conf-file-path)
      (if (.isFile conf-file)
        conf-file
        (let [msg "Configuration file conf.clj can not be found."]
          (log/error msg)
          (throwf msg))))))


(defn conf
  "Reads and returns conf map from the conf.clj file.
   Throws an exception if conf file can not be found."
  []
  (create-conf (.getCanonicalPath (conf-file))
               {:base.dir (base-dir)}))


(defn db-ds []
  (spring-lib/get-bean (spring-context) "dataSource"))


(defn db-spec []
  (db-lib/create-spec (db-ds)))


(defn prepare-db []
  (db/prepare (conf) (db-spec) ))