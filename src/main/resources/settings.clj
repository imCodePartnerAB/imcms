(ns
  #^{:doc "imCMS clojure based settings"}
  settings

  (use
    [clojure.contrib.def :only (defvar)]))    


(defvar sql-scripts-home "WEB-INF/sql"

  "SQL scripts dir path relative to application path.")


(defvar db-diffs
  {4.11 ["imcms_rb4.sql"]
   6.2  ["diff/mysql-schema-diff-4.11-6.2.sql"]}

  "Databse diffs map. Version no -> diff script names vector.")