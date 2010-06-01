;;; Application configuration file
;;; Consists of a single record (clojure map)
{
    ;; Application basedir
    ;; Replaced with a real basedir after this configuration has been read
    :base-dir ""

    ;; Database configuration
    :db {
        ;; Required database version
        :version 6.2


        ;; SQL scripts directory path relative to the ':base-dir'
        :scripts-dir "WEB-INF/sql"


        ;; Database init.
        ;; A db-init is a record of the following fields:
        ;;   :version - db version which is set after database is initialized.
        ;;   :scripts - a vector of SQL scripts locations relative to the ':scripts-dir'.
        :init {
            :version 4.11
            :scripts ["imcms_rb4.sql"]
        }


        ;; Database diffs - a set of diffs.
        ;; A diff is a record of the following fields:
        ;;   :from - db version to upgrade from
        ;;   :to - db version to upgrade
        ;;   :scripts - a vector of SQL scripts locations relative to the ':scripts-dir'.
        :diffs #{
            {
                :from 4.11
                :to 6.2
                :scripts ["diff/mysql-schema-diff-4.11-6.2.sql"]
            }
        }
    }
}