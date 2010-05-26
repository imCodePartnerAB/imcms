(ns
  #^{:doc "Provides functions for accessing imCMS at runtime.      
           The imcode.server.Imcms singleton must be initialized."}
  
  com.imcode.imcms.runtime  
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


(defn home []
  (.getCanonicalPath (path))) 


(defn services []
  (invoke getServices))

(defn i18n-support []
  (invoke getI18nSupport))

(defn doc-mapper []
  (.getDocumentMapper (services)))

(defn auth-mapper []
  (.getImcmsAuthenticatorAndUserAndRoleMapper (services)))

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


(defn #^DocumentDomainObject working-doc
  [id lang]
  (.getWorkingDocument (doc-mapper) id (to-lang lang)))

(defn #^DocumentDomainObject default-doc
  [id lang]
  (.getDefaultDocument (doc-mapper) id (to-lang lang)))

(defn #^DocumentDomainObject custom-doc
  [id version-no lang]
  (.getCustomDocument (doc-mapper) id version-no (to-lang lang)))

(defn doc-ids []
  (seq (.getAllDocumentIds (doc-mapper))))

(defn doc-cache []
  (.getDocumentLoaderCachingProxy (doc-mapper)))

(defn #^java.util.Map loaded-default-docs
  "Returns loaded default documents Map."
  [lang]
  (-> (doc-cache) .getDefaultDocuments (.get ,, (to-lang lang))))

(defn #^java.util.Map loaded-working-docs
  "Returns loaded working documents Map."
  [lang]
  (-> (doc-cache) .getWorkingDocuments (.get ,, (to-lang lang))))


(defn load-all-docs
  "Loads all documents from database to the cache. Use with care.
   Returns nil."
  []
  (doseq [doc-fn [working-doc default-doc], id (doc-ids), lang (langs)]
    (doc-fn id lang)))


(defn unload-docs
  "Unloads doc(s) with the given id(s) from the cache.
   Returns nil."
  [id & ids]
  (let [cache (doc-cache)]
    (doseq [doc-id (cons id ids)] (.removeDocumentFromCache cache doc-id))))


(defn clear-cache []
  (.clearCache (doc-cache)))


(defn loaded-docs-info
  "Retuns loaded docs info as a map - doc ids mapped to language code set:
   {1001 #{:en :sv}, 1002 #{:en :sv}, 1003 #{:en :sv}, ...}"
  []
  (let [fs [loaded-working-docs, loaded-default-docs]
        langs (langs)
        info-maps (for [f fs, lang langs, [doc-id doc] (f lang)]
                    (sorted-map doc-id, #{(keyword (.getCode lang))}))]

    ;; unions info maps: {1001 #{:en}}, {1001 #{:sv}} -> {1001 #{:en :sv}} 
    (apply merge-with clojure.set/union info-maps)))


(defn #^com.imcode.imcms.api.DocumentVersionInfo doc-version-info [id]
  (.getDocumentVersionInfo (doc-cache) id))


(defn #^UserDomainObject login
  "Returns user or null if there is no such user.
   Login and password can be keywords."
  [login password]
  (let [login (if (keyword? login) (name login) login)
        password (if (keyword? login) (name login) login)]
    (.verifyUser (services) login password)))


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

    (let [conf-file-home (str (home) "/WEB-INF/classes")
          conf-file (java.io.File. conf-file-home "conf.clj")]

      (log/debug "Looking for configuration file conf.clj in %s." conf-file-home)
      (if (.isFile conf-file)
        conf-file
        (let [msg "Configuration file conf.clj can not be found."]
          (log/error msg)
          (throwf msg))))))


(defn conf
  "Reads and returns conf map from the conf.clj file.
   Throws an exception if conf file can not be found."
  []
  (create-conf (path) (conf-file)))
  

(defn roles []
  (.getAllRoles (auth-mapper)))

(defn users []
  (.getUsers (auth-mapper) true true))

(defn users-info []
  (for [u (users)] {:id (.getId u), :name (.getLoginName u), :password (.getPassword u)}))



(defn db-ds []
  (spring-lib/get-bean (spring-context) "dataSource"))


(defn db-spec []
  (db-lib/create-spec (db-ds)))


(defn prepare-db []
  (db/prepare (create-conf) (db-spec) ))