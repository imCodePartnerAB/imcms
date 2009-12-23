(ns com.imcode.imcms.maintenance.MaintenanceFilter
  #^{:doc "Maintenance filter. See mapping configuration in WEB-INF/web.xml."}
  (:gen-class
    :implements [javax.servlet.Filter]
    :init init-instance)

  (:require
    [com.imcode.imcms.backdoor.controller :as controller]))

(defn -init-instance []
  [[] nil])

(defn -init [this filterConfig])
(defn -destroy [this])

(defn -doFilter [this request response filterChain]
  (controller/handle request response))