(ns com.imcode.imcms.sandbox
  #^{:doc "Sandbox."}
  (:require
    [clojure.contrib.str-utils :as su]
    [clojure.contrib.str-utils2 :as su2]
    [clojure.contrib.shell-out :as shell]
    [com.imcode.imcms
      [reflect-utils :as ru]
      [project :as project]
      [spring :as spring]
      [runtime :as rt]])

  (:import (imcode.server Imcms)))

(defn init-imcms []
  (Imcms/setPath (project/subdir "src/test/resources"))
  (Imcms/setPrefsConfigPath ".")
  (Imcms/setApplicationContext spring/spring-app-context)
  (Imcms/setUpgradeDatabaseSchemaOnStart false)
  (Imcms/start))