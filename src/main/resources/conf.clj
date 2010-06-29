;;; Configuration file - consists of a single clojure record
;;; Parameters such as base.dir, etc., must be defined ONLY at the top level ot the conf.
{
    ;; Application basedir
    ;; Replaced with a real basedir after this configuration has been read
    :base.dir ""

    ;; SQL scripts directory path relative to the 'base.dir'
    :db.scripts.dir "${base.dir}/WEB-INF/sql"

    ;; Database configuration
    :db {      
        ;; Required database version
        ;; If database version is lower than required it will be updated using diffs (see below).
        :version 6.2

        ;; Database init.
        ;; A db-init is a record of the following fields:
        ;;   :version - db version which is set after database is initialized.
        ;;   :scripts - a vector of SQL scripts locations.
        :init {
            :version 4.11
            :scripts ["${db.scripts.dir}/imcms_rb4.sql"]
        }

        ;; Database diffs - a set of diffs.
        ;; A diff is a record of the following fields:
        ;;   :from - db version to upgrade from
        ;;   :to - db version after upgrade
        ;;   :scripts - a vector of SQL scripts locations.
        :diffs #{
            {
                :from 4.11
                :to 6.2
                :scripts ["${db.scripts.dir}/diff/mysql-schema-diff-4.11-6.2.sql"]
            }
        }
    }
}