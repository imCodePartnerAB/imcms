(ns
  #^{:doc "Frequently used objects's factory."} 
  com.imcode.imcms.factory
  (:import
    (imcode.server.document.textdocument TextDomainObject)
    (com.imcode.imcms.api ContentLoop Content)))

(defn new-content-loop
  "Creates content loop instance with a single content."
  [doc-id doc-version-no no]
  (let [loop (doto (ContentLoop.)
               (.setDocId doc-id)
               (.setNo no)
               (.setDocVersionNo doc-version-no))

        content (doto (Content.)
                  (.setIndex 0)
                  (.setOrderIndex 0)
                  (.setEnabled true))]

    (-> (.getContents loop) (.add content))

    loop))


(defn new-text
  "Creates text domain object instance."
  [doc-id doc-version-no no text]
  (doto (TextDomainObject.)
    (.setDocId doc-id)
    (.setDocVersionNo doc-version-no)
    (.setNo no)
    (.setText text)))