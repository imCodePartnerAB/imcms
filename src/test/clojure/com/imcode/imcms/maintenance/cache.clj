(ns com.imcode.imcms.maintenance.cache
  (:use 'compojure 'clojure.contrib.def)

  (:import
    'org.mortbay.jetty.Server
    'org.mortbay.jetty.servlet.Context
    'org.mortbay.jetty.servlet.ServletHolder
    'com.imcode.imcms.admin.backdoor.CompojureServlet))

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
    (welcome-page))

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
    [404 "Page Not Found"]))


(defvar- srv (atom nil))

(defn srv-start
  ([]
    (srv-start 8888))

  ([port]
    (reset! srv
      (run-server
        {:port port}
        ;"/ctx/*" (servlet (with-context admin-services "/ctx"))))))
        "/*" (servlet admin-services)))))



(defn srv-stop []
  (stop @srv))


(defn server []
  (let [server (Server. 8888)
        context (Context. server "/" Context/SESSIONS)
        servletHolder (ServletHolder. (CompojureServlet. ))]
    (.addServlet context (ServletHolder. (servlet admin-services)) "/*")
    ;(.addServlet context servletHolder "/*")
    ;(.addServlet context (ServletHolder. (servlet admin-services)) "/test")
    server))

(def s (server))