(ns com.imcode.imcms.document)

; default title is set to id

; (defsite )

; json

; (defdoc)

; (gen-page)

; (gen-site)

; (gen-template)

(textdoc :id :estonia, :title "Welocom to Estonia", :language "eng"
  
  :texts {1 "text field 1"
          2 "text field 2"}

  :images {1 "images/image1"
           2 "images/image2"})


(textdoc :id :sweden, :title "Welcome to Sweden", :language "eng"

  :texts {1 "text field 1"
          2 "text field 2"}

  :images {1 "images/image1"
           2 "images/image2"})


(textdoc :id :main, :title "Country catalogue", :language "eng"
  
  :texts {1 "text field 1"
          2 "text field 2"}

  :images {1 "images/image1"
           2 "images/image2"}

  :menus {1 :estonia
          2 :sweden})

  

(defn textdoc [& data])
