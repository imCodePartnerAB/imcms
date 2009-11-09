(ns com.imcode.imcms.sandbox

(:require [dk.bestinclass.clojureql :as cql]
          [dk.bestinclass.clojureql.backend.mysql :as cql-mysql]
          [clojure.contrib.str-utils :as str-utils]
          [clojure.contrib.str-utils2 :as str-utils2]
          com.imcode.imcms.boot))

(def *conn-info*
     (cql/make-connection-info "mysql"              ; Type
                               "//localhost/imcms"    ; Adress and db (cql)
                               "root"                ; Username
                               ""))              ; Password

(cql/load-driver "com.mysql.jdbc.Driver")

(defn run [query]
  (cql/run [*conn-info* rows] query
    (doseq [row rows]
      (prn row))))


(defmacro query [& sql]
  (str-utils/str-join " " sql))

(defmacro pquery [q & params]
  (let [plist# (map str params)]
    `(format (str-utils2/replace ~q "?" "%s") ~@plist#)))


(defn make-counter [n]
  (let [init n
        current (atom n)]

    (fn ([] @current)
        ([key]
          (condp = key
            :reset (reset! current init)
            :next (swap! current inc))))))



(def compositions #{{:name "The Art of the Fugue" :composer "J. S. Bach"}
                    {:name "Musical Offering" :composer "J. S. Bach"}
                    {:name "Requiem" :composer "Giuseppe Verdi"}
                    {:name "Requiem" :composer "W. A. Mozart"}})

(def composers #{{:composer "J. S. Bach" :country "Germany"}
                 {:composer "W. A. Mozart" :country "Austria"}
                 {:composer "Giuseppe Verdi" :country "Italy"}})

(def nations #{{:nation "Germany" :language "German"}
               {:nation  "Austria" :language "German"}
               {:nation "Italy" :language "Italian"}})


(defn doit []
  (println @com.imcode.imcms.project/base-dir))