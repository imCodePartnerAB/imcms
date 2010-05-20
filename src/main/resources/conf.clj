;;; Application configuration
;;; Consists of a single record (clojure map)
{
    ;; Database configuration
    :db {
        ;; Required database version
        :version 6.2


        ;; SQL scripts directory path relative to application home
        :scripts-dir "WEB-INF/sql"


        ;; Database init.
        ;; A db-init is a record of the following fields:
        ;;   :version - db version which is set after database is initialized.
        ;;   :scripts - a vector of SQL scripts locations.
        :init {
            :version 4.11
            :scripts ["imcms_rb4.sql"]
        }


        ;; Database diffs - a set of diffs.
        ;; A diff is a record of the following fields:
        ;;   :from - db version to upgrade from
        ;;   :to - db version to upgrade
        ;;   :scripts - a vector of SQL scripts locations.        
        :diffs #{
            {
                :from 4.11
                :to 6.2
                :scripts ["diff/mysql-schema-diff-4.11-6.2.sql"]
            }
        }
    }
}