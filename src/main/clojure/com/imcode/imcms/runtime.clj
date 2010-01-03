(ns
  #^{:doc "Provides functions for accessing Imcms runtime."}
  com.imcode.imcms.runtime  
  (:import
    [imcode.server Imcms]
    [imcode.server.document DocumentDomainObject]
    [imcode.server.user UserDomainObject]))

(defmacro invoke
  "Invokes Imcms static method
  "[method & args]
  `(. Imcms ~method ~@args))

(defn start []
  (. Imcms start))

(defn stop []
  (. Imcms stop))

(defn get-start-ex []
  (. Imcms getStartEx))

(defn get-mode []
  (. Imcms getMode))

(defn set-normal-mode []
  (. Imcms setNormalMode))

(defn set-maintenance-mode []
  (. Imcms setMaintenanceMode))

(defn get-services []
  (. Imcms getServices))

(defn get-i18n-support []
  (. Imcms getI18nSupport))

(defn get-doc-mapper []
  (.getDocumentMapper (get-services)))

(defn get-langs []
  (.getLanguages (get-i18n-support)))

(defn get-default-lang []
  (.getDefaultLanguage (get-i18n-support)))


(defn find-lang-by-code [#^String code]
  (if-let [lang (.getByCode (get-i18n-support) code)]
    lang
    (throw (Exception. (format "Language with code [%s] can not be found." code)))))

(defmulti  to-lang class)

(defmethod to-lang com.imcode.imcms.api.I18nLanguage [lang] lang)
(defmethod to-lang String                            [lang] (find-lang-by-code lang))
(defmethod to-lang clojure.lang.Keyword              [lang] (find-lang-by-code (name lang)))


(defn #^DocumentDomainObject get-working-doc
  [id lang]
  (.getWorkingDocument (get-doc-mapper) id (to-lang lang)))

(defn #^DocumentDomainObject get-default-doc
  [id lang]
  (.getDefaultDocument (get-doc-mapper) id (to-lang lang)))

(defn #^DocumentDomainObject get-custom-doc
  [id version-no lang]
  (.getCustomDocument (get-doc-mapper) id version-no (to-lang lang)))

(defn get-doc-ids []
  (seq (.getAllDocumentIds (get-doc-mapper))))

(defn get-doc-cache []
  (.getDocumentLoaderCachingProxy (get-doc-mapper)))

(defn #^java.util.Map get-loaded-default-docs
  "Returns loaded default documents Map."
  [lang]
  (-> (get-doc-cache) .getDefaultDocuments (.get ,, (to-lang lang))))

(defn #^java.util.Map get-loaded-working-docs
  "Returns loaded working documents Map."
  [lang]
  (-> (get-doc-cache) .getWorkingDocuments (.get ,, (to-lang lang))))


(defn load-all-docs
  "Loads all documents from database to the cache. Use with care.
   Returns nil."
  []
  (doseq [get-doc-fn [get-working-doc get-default-doc], id (get-doc-ids), lang (get-langs)]
    (get-doc-fn id lang)))


(defn unload-docs
  "Unloads doc(s) with the given id(s) from the cache.
   Returns nil."
  [id & ids]
  (let [cache (get-doc-cache)]
    (doseq [doc-id (cons id ids)] (.removeDocumentFromCache cache doc-id))))


(defn clear-cache []
  (.clearCache (get-doc-cache)))


(defn get-loaded-docs-info
  "Retuns loaded docs info as a map - doc ids mapped to language code set:
   {1001 #{:en :sv}, 1002 #{:en :sv}, 1003 #{:en :sv}, ...}"
  []
  (let [fs [get-loaded-working-docs, get-loaded-default-docs]
        langs (get-langs)
        info-maps (for [f fs, lang langs, [doc-id doc] (f lang)]
                    (sorted-map doc-id, #{(keyword (.getCode lang))}))]

    ;; unions info maps: {1001 #{:en}}, {1001 #{:sv}} -> {1001 #{:en :sv}} 
    (apply merge-with clojure.set/union info-maps)))


(defn #^com.imcode.imcms.api.DocumentVersionInfo get-doc-version-info [id]
  (.getDocumentVersionInfo (get-doc-cache) id))


(defn #^UserDomainObject login
  "Returns user or null if there is no such user.
   Login and password can be keywords."
  [login password]
  (let [login (if (keyword? login) (name login) login)
        password (if (keyword? login) (name login) login)]
    (.verifyUser (get-services) login password)))


(defn get-conf
  "Returns configuration read from server.properties as a map sorted by property name."
  []
  (into (sorted-map) (Imcms/getServerProperties)))