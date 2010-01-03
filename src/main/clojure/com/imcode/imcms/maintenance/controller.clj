(ns com.imcode.imcms.maintenance.controller)
  ;;; (:require [com.imcode.imcms.maintenance.cms :as cms]))

(def index-page "/WEB-INF/maintenance/index.jsp")

(defn handle [request response]
  (-> request (.getRequestDispatcher ,, index-page) (.forward ,, request response)))


