(ns com.imcode.imcms.compojure
  (:use compojure.ns-utils clojure.contrib.def)
  (:gen-class :extends javax.servlet.http.HttpServlet))

(immigrate
  'compojure.control
  'compojure.html.gen
  'compojure.html.page-helpers
  'compojure.html.form-helpers
  'compojure.http.helpers
  'compojure.http.middleware
  'compojure.http.multipart
  'compojure.http.routes
  'compojure.http.servlet
  'compojure.http.session
  'compojure.str-utils
  'compojure.map-utils
  'compojure.validation)

(comment import
  'org.mortbay.jetty.Server
  'org.mortbay.jetty.servlet.Context
  'org.mortbay.jetty.servlet.ServletHolder)

(defn html-doc
  [title & body]
  (html
    (doctype :html4)
    [:html
      [:head
        [:title title]
      ]
      [:body
        [:div
          [:h2
            [:a {:href "/"} "Main (welcome page)"]
          ]
          [:h2
            [:a {:href "/doc"} "Document"]
          ]
          [:h2
            [:a {:href "/user"} "Users"]
          ]
          [:h2
            [:a {:href "/role"} "Roles"]
          ]
        ]
      ]
      body
    ]))

(defn handle-doc
  [action]
  (let []
    (html-doc "Result"
      "Processing: " action)))

(defn undefined [request]
  (html-doc "Undefined"
    request))

(defn welcome-page
  []
  (html-doc "imCMS admin" ">> Please pick a task <<"))


(defroutes admin-services

    (GET "/"
      ;(welcome-page))
      (undefined request))

    (GET "/doc"
      (html-doc "Document"
        (form-to [:post "/doc"]
          (drop-down "action" [:new :delete :search])
          (submit-button "Go"))))

    (POST "/doc"
      (handle-doc (params :action)))

    (GET "/user"
      (undefined request))

    (GET "/role"
       (undefined request))

    (GET "/image"
      (java.io.File. "/Users/ajosua/Pictures/143.jpg"))

  (ANY "*"
    [404 "Page Not Found"])                           )


(defservice (with-context admin-services "/admin/backdoor"))

(comment
  
(defn r [& args]
  (println "args:" args)
  (apply admin-services args))




(defvar- srv (atom nil))

(defn srv-start
  ([]
    (srv-start 8888))

  ([port]
    (reset! srv
      (run-server
        {:port port}
        "/admin/backdoor/*" (servlet (with-context admin-services "/admin/backdoor"))))))
        ;"/*" (servlet admin-services)))))
        ;"/*" (servlet r)))))

)
( comment

(defn srv-stop []
  (stop @srv))


(defn server []
  (let [server (Server. 8888)
        context (Context. server "/" Context/SESSIONS)]
    (.addServlet context (ServletHolder. (servlet admin-services)) "/*")
    ;(.addServlet context servletHolder "/*")
    ;(.addServlet context (ServletHolder. (servlet admin-services)) "/test")
    server))

;(def s (server))

  )