;; sandbox
(ns com.imcode.clojure.sandbox
  (:import [java.util HashMap] [java.sql Connection ResultSet]))


;(def dataSourceProperties {}
;    <property name="driverClassName" value="${JdbcDriver}"/>
;    <property name="url" value="${JdbcUrl}"/>
;    <property name="username" value="${User}"/>
;    <property name="password" value="${Password}"/>
;    <property name="testOnBorrow" value="true"/>
;    <property name="validationQuery" value="select 1"/>    
;    <property name="defaultAutoCommit" value="false"/>
;    <property name="maxActive" value="${MaxConnectionCount}"/>
      
(defn createDataSource []
	(doto (org.apache.commons.dbcp.BasicDataSource.)
		(.setDriverClassName "com.mysql.jdbc.Driver")
		(.setUrl "jdbc:mysql://localhost")
		(.setUsername "root")
		(.setTestOnBorrow true)
		(.setValidationQuery "select 1")
		(.setDefaultAutoCommit false)
		(.setMaxActive 5)))
		
(defn sql [query]
	(let [ds (createDataSource)]
	  (with-open 
	    [c (.getConnection ds)
	     s (.createStatement c)
	     r (.executeQuery s query)]
	    
	    ;(loop [result []]
	    ;  (if (false? (.next r)) 
	    ;    result
	    ;    (recur (conj result (.getString r 1))))))))
	    (doall (resultset-seq r))))) 	  					
  
(defn createProxy [target]
	(proxy [HashMap] []
	  (put [k v] 
	    (println "Before put")
	    (let [result (.put target k v)]
	      (println "After put")
	      result))))
	      
	      

