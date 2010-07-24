(ns
  #^{:doc "Initialized ImCMS instance services access fns."}

  com.imcode.imcms.instance.services
  
  (:import
    (imcode.server Imcms)
    (imcode.server.document DocumentDomainObject)
    (imcode.server.user UserDomainObject))

  (:require
    (com.imcode.imcms
      [db :as db-lib]
      [fs :as fs-lib]
      [spring :as spring-lib])

    (clojure.contrib
      [logging :as log]))

  (:use
    [com.imcode.imcms.instance :as instance :only (langs to-lang)]

    (com.imcode.imcms
      [misc :only (dump)])

    (clojure.contrib
      [except :only (throw-if throw-if-not throwf)])))

(defn services []
  (instance/invoke getServices))

(defn doc-mapper []
  (.getDocumentMapper (services)))

(defn auth-mapper []
  (.getImcmsAuthenticatorAndUserAndRoleMapper (services)))


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
        password (if (keyword? password) (name password) password)]
    (.verifyUser (services) login password)))