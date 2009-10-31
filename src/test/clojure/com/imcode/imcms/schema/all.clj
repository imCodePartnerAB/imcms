(ns com.imcode.imcms.schema.all
  (:use
    clojure.contrib.test-is)
  (:require
    (com.imcode.imcms.schema diff-builder upgrade)))

(run-tests
  'com.imcode.imcms.schema.diff-builder
  'com.imcode.imcms.schema.upgrade)
