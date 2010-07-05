(ns com.imcode.imcms.SupervisorServlet
  (:gen-class
     :extends javax.servlet.http.HttpServlet)

  (:require
     [compojure.route :as route])
  
  (:use compojure.core ring.util.servlet hiccup.core))



(defroutes main-routes
  (GET "*/xyz" {request :servlet-request, response :servlet-response, uri :uri} []
    (html)))

(defservice main-routes)

