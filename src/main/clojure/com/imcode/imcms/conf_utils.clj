(ns
  #^{:doc "Configuration utils."}
  com.imcode.imcms.conf-utils

  (:use
    clojure.test    
    [clojure.walk :only (postwalk)]))  


;;;;
;;;; Pure fns.
;;;;

(defn rewrite-expression
  "Replace ${xxx} claluses in an expression with real param's values."
  ; (re-find #"\$\{([\w\.]+?)\}" "/aaa/${xxx.yyy.zzz}/bbb") => ["${xxx.yyy.zzz}" "xxx.yyy.zzz"]
  [#^String expression, params]
  (if-let [[clause, param-name] (re-find #"\$\{([\w\.]+?)\}" expression)]
    (if-let [param-value (get params (keyword param-name))]
      (recur (.replace expression clause param-value) params)
      (throw (RuntimeException. (format  "Can not rewrite expression %s. Parameter %s does not exists in params %s.",
                                         expression, param-name, params))))
    expression))

                      
(defn rewrite-params
  "Rewrites all string expressions containing ${xxx} clauses found in conf with corresponding values."
  [conf]
  (postwalk #(if (string? %) (rewrite-expression % conf) %)
            conf))


;;;;
;;;; Side effect fns.
;;;;

(defn read-conf
  "Reads and returns conf map from a conf file."
  [conf-file-path]
  (read-string (slurp conf-file-path)))


(defn create-conf
  "Args
    conf-file-path configuration file path.
    params - additional and/or overloaded parameters merged with a conf."
  [conf-file-path params]
  (let [conf (read-conf conf-file-path)]
    (rewrite-params (merge conf params))))


;;;;
;;;; Tests
;;;;

(deftest test-rewrite-expression
  (is (= "/home/xxx/project/src"
         (rewrite-expression "${base.dir}/src" {:base.dir "/home/xxx/project"})))

  (is (= "/home/xxx/project/src"
      (rewrite-expression "${home.dir}/xxx/${project.dir}/src" {:home.dir "/home"
                                                                :project.dir "project"}))))

(deftest test-read-conf
  (is (map? (read-conf "src/main/resources/conf.clj"))))


(deftest test-create-conf
  (let [conf (create-conf "src/main/resources/conf.clj" {:base.dir "/basedir"})]
    (is (= "/basedir" (get conf :base.dir)))
    (is (= "/basedir/WEB-INF/sql" (get conf :db.scripts.dir)))))

