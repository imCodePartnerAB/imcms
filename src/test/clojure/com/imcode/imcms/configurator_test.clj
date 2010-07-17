(ns
  #^{:doc "Tests."}
  com.imcode.imcms.configurator-test

  (:use
    clojure.test
    com.imcode.imcms.configurator))


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