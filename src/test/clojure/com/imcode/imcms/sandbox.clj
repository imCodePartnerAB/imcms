(ns com.imcode.imcms.sandbox
  #^{:doc "Sandbox."}
  (:require
    [clojure.contrib.str-utils :as su]
    [clojure.contrib.str-utils2 :as su2]
    [clojure.contrib.shell-out :as shell]
    [com.imcode.imcms
      [reflect-utils :as ru]
      [project :as project]
      [spring :as spring]
      [runtime :as rt]])

  (:import
    (imcode.server Imcms)
    (imcode.server.document.textdocument TextDomainObject)
    (com.imcode.imcms.api ContentLoop Content)))

(def *text-doc-id* 1001)
(def *lang* :en)
(def *text-no* 1000)
(def *content-loop-no* 1000)
(def *working-version-no* 0)


