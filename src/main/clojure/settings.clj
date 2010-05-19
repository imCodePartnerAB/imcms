(ns
  #^{:doc "imCMS settings."}
  settings

  (use
    [clojure.contrib.def :only (defvar)]))    


(defvar db-version 6.2

  "Required database version.")


(defvar db-scripts-dir "WEB-INF/sql"

  "SQL scripts directory path relative to application path.")


(defvar db-init
  {:version 4.11
   :scripts ["imcms_rb4.sql"]
  }

  "Database init.
   A db-init is a record of the following fields:
   :version - db version which is set after database is initialized.
   :scripts - a vector of SQL scripts locations.")


(defvar db-diffs
  #{
    {:from 4.11
     :to 6.2
     :scripts ["diff/mysql-schema-diff-4.11-6.2.sql"]
    }   
  }

  "Database diffs - a set of diffs.
   A db-diff is a record of the following fields:
   :from - db version to upgrade from
   :to - db version to upgrade
   :scripts - a vector of SQL scripts locations.")