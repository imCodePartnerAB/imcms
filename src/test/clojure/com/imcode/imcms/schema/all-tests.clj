(ns com.imcode.imcms.schema.all-tests
  (:use
    clojure.contrib.test-is)
  (:require
    (com.imcode.imcms.schema DiffBuilderTest SchemaUpgradeTest)))

(run-tests
  'com.imcode.imcms.schema.DiffBuilderTest
  'com.imcode.imcms.schema.SchemaUpgradeTest)
