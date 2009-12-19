(ns com.imcode.imcms.runtime
  #^{:doc "Imcms static class must be configured."}
  (:import
    [imcode.server Imcms]))

(defmacro invoke [f & args]
  `(. Imcms ~f ~@args))

(defn start []
  (.start Imcms))

(defn stop []
  (.stop Imcms))

(defn get-start-ex []
  (.getStartEx Imcms))

(defn get-mode []
  (.getMode Imcms))

(defn set-normal-mode []
  (.setNormalMode Imcms))

(defn set-maintenance-mode []
  (.getMaintenanceMode Imcms))

(defn get-services []
  (.. Imcms getServices))

(defn get-i18n-support []
  (.. Imcms getI18nSupport))

(defn get-doc-mapper []
  (.. Imcms getServices getDocumentMapper))

(defn get-langs []
  (.getLanguages (get-i18n-support)))

(defn get-default-lang []
  (.getDefaultLanguage (get-i18n-support)))

(defn get-lang-by-code [code]
  (.getByCode (get-i18n-support) (if (keyword? code) (name code) code)))

(defn get-working-doc [id lang-code]
  (.getWorkingDocument (get-doc-mapper) id (get-lang-by-code lang-code)))

(defn get-default-doc [id lang-code]
  (.getDefaultDocument (get-doc-mapper) id (get-lang-by-code lang-code)))

(defn get-custom-doc [id version-no lang-code]
  (.getCustomDocument (get-doc-mapper) id version-no (get-lang-by-code lang-code)))

(defn get-doc-ids []
  (seq (.getAllDocumentIds (get-doc-mapper))))

(defn get-doc-cache []
  (.getDocumentLoaderCachingProxy (get-doc-mapper)))

(defn get-cached-default-docs [lang-code]
  (-> (get-doc-cache) .getDefaultDocuments (.get ,, (get-lang-by-code lang-code))))

(defn get-cached-working-docs [lang-code]
  (-> (get-doc-cache) .getWorkingDocuments (.get ,, (get-lang-by-code lang-code))))



