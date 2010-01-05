(ns com.imcode.imcms.mapping.all-tests
  (:use
    clojure.contrib.test-is)
  (:require
    (com.imcode.imcms.mapping DocumentMapperTest)))

(run-tests
  'com.imcode.imcms.mapping.DocumentMapperTest)
