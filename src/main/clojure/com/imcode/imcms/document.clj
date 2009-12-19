(ns com.imcode.imcms.document)

; default title is set to id

; (defsite )

; json

; (defdoc)

; (gen-page)

; (gen-site)

; (gen-template)


(defstruct doc :type :sys-alias :version :meta :fields)

(defstruct textdoc-struct :doc-type :sys-alias :title :language :texts :images)

(defn textdoc
  [& args]
  (apply struct-map textdoc-struct :doc-type :textdoc args))


(comment
(textdoc :sys-id "country/estonia", :title "Welcome to Estonia", :language "eng"
  
  :texts {1 "text field 1"
          2 "text field 2"}

  :images {1 "images/image1"
           2 "images/image2"})


(textdoc :sys-id "country/estonia", :title "Welcome to Sweden", :language "eng"

  :texts {1 "text field 1"
          2 "text field 2"}

  :images {1 "images/image1"
           2 "images/image2"})


(textdoc :sys-id "main", :title "Country catalogue", :language "eng"
  
  :texts {1 "text field 1"
          2 "text field 2"}

  :images {1 "images/image1"
           2 "images/image2"}

  :menus {1 "country/estonia"
          2 "country/estonia"})
)  

