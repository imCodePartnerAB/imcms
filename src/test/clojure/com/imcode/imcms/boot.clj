(ns
  #^{:doc "Tests fixtures."}

  com.imcode.imcms.boot

  (:require
    [com.imcode.imcms
      [spring :as spring]
      [project :as project]])

  (:import
    [imcode.server Imcms]))

(defn init-imcms
  "Initializes Imcms for tests."
  []
  (Imcms/setPath (project/subdir "src/test/resources"))
  (Imcms/setPrefsConfigPath ".")
  (Imcms/setApplicationContext spring/spring-app-context)
  (Imcms/setUpgradeDatabaseSchemaOnStart false))

