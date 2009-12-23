(ns com.imcode.imcms.backdoor.controller
  (:require [com.imcode.imcms.backdoor.cms :as cms]))

(def index-page "/WEB-INF/maintenance/index.jsp")

(defn handle [request response]
  (-> request (.getRequestDispatcher ,, index-page) (.forward ,, request response)))


