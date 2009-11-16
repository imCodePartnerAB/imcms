(ns com.imcode.imcms.compojure)

(use 'compojure)

(defn html-doc
  [title & body]
  (html
    (doctype :html4)
    [:html
      [:head
        [:title title]]
      [:body
       [:div
        [:h2
         ;; Pass a map as the first argument to be set as attributes of the element
         [:a {:href "/"} "Home"]]]
              body]]))


(def sum-form
  (html-doc "Sum"
    (form-to [:post "/"]
      (text-field {:size 3} :x)
      "+"
      (text-field {:size 3} :y)
      "+"
      (text-field {:size 3} :z)      
      (submit-button "="))))

(defn result
  [x y z]
  (let [x (Integer/parseInt x)
        y (Integer/parseInt y)
        z (Integer/parseInt z)]
    (html-doc "Result"
      x " + " y " + " z " == " (+ x y z))))

(defroutes webservice
  (GET "/"
    sum-form)

  (GET "/test"
    (html-doc "Tst" (html (drop-down "species" [:cat :dog :buffalo :bunny :lion]))))  

  (GET "/public/:ax/:bx/:cx"
    (str "Loading file: "
         request))

  (GET "/image"
    (java.io.File. "/Users/ajosua/Pictures/ant.jpg"))  

  (ANY "*"
    [404 "Page Not Found"])   

  (POST "/"
    (result (params :x) (params :y) (params :z))))





(def srv (atom nil))

(defn srv-start
  ([]
    (srv-start 8080))

  ([port]
    (reset! srv
      (run-server
        {:port port}
        "/*"
        (servlet webservice)))))

(defn srv-stop []
  (stop @srv))
