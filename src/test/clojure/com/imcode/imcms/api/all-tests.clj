(ns com.imcode.imcms.api.all-tests
  (:use
    clojure.contrib.test-is)
  (:require
    (com.imcode.imcms.api ContentLoopDaoTest)))

(run-tests
  'com.imcode.imcms.api.ContentLoopDaoTest)
