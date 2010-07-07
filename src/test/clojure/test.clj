(def namespaces '[com.imcode.imcms.conf-utils-test com.imcode.imcms.db-test])

(use 'clojure.test)

(let [results (atom [])]
  (let [report-orig report]
    (binding [report (fn [x] (report-orig x)
                       (swap! results conj (:type x)))]

      (doseq [namespace namespaces]
        (require namespace)

         (run-tests namespace)))
  (shutdown-agents)
  (System/exit (if (empty? (filter {:fail :error} @results)) 0 -1))))