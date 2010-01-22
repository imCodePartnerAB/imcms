(ns com.imcode.imcms.sandbox
  #^{:doc "Sandbox."}
  (:require
    [clojure.contrib.str-utils :as su]
    [clojure.contrib.str-utils2 :as su2]
    [clojure.contrib.shell-out :as shell])

  (:import                                                                     
    (org.tmatesoft.svn.core SVNURL ISVNDirEntryHandler SVNDirEntry SVNNodeKind)
    (org.tmatesoft.svn.core.wc SVNClientManager ISVNOptions SVNWCUtil SVNLogClient SVNRevision)
    (org.tmatesoft.svn.core.io SVNRepositoryFactory SVNRepository)
    (org.tmatesoft.svn.core.internal.io.dav DAVRepositoryFactory)))


      ;handler (proxy [ISVNDirEntryHandler] []
      ;          (handleDirEntry [#^SVNDirEntry dirEntry])

(def *svn-url-tags* "https://repo.imcode.com/imcode/imcms/tags")

(def *tag-names*
  ["RB-5-ImageArchive-pre" "2.0.0" "2a.1.0-beta1" "RB-4-ImageArchive-image-handling-merge-pre" "2a.1.0-beta2" "3.2.0" "1.6.0" "2a.0.0" "rb4-image-handling-merge-pre" "4.0.0-beta29-img-beta10" "4.0.0-beta29-img-beta11" "4.0.0-beta29-img-beta12" "4.0.0-beta29-img-beta13" "4.0.0-alpha10" "4.0.0-alpha11" "6.0.0-alpha10" "4.0.0-alpha12" "6.0.0-alpha11" "4.0.0-alpha13" "4.0.0-beta1" "6.0.0-alpha12" "4.0.0-alpha14" "4.0.0-beta2" "6.0.0-alpha13" "4.0.0-alpha15" "4.0.0-beta3" "6.0.0-alpha14" "4.0.0-alpha16" "4.0.0-beta4" "6.0.0-alpha15" "4.0.0-alpha17" "4.0.0-beta5" "6.0.0-alpha16" "4.0.0-alpha18" "4.0.0-beta6" "6.0.0-alpha17" "4.0.0-alpha19" "4.0.0-beta7" "6.0.0-alpha18" "4.0.0-beta8" "3.2.0-beta10" "6.0.0-alpha19" "4.0.0-beta9" "3.2.0-beta11" "3.2.0-beta12" "3.2.0-beta13" "5.0.0-alpha1" "5.0.0-alpha2" "3.1.0" "5.0.0-alpha3" "3.1.1" "5.0.0-alpha4" "1.5.0" "3.1.2" "5.0.0-alpha5" "5.0.0-alpha6" "5.0.0-alpha7" "5.0.0-alpha8" "1.9.0" "5.0.0-alpha9" "1.9.1" "1.9.2" "1.9.3" "4.0.0-alpha20" "4.0.0-alpha21" "4.0.0-alpha22" "6.0.0-alpha20" "4.0.0-alpha23" "6.0.0-alpha21" "4.0.0-alpha24" "6.0.0-alpha22" "4.0.0-alpha25" "6.0.0-alpha23" "4.0.0-alpha26" "6.0.0-alpha24" "4.0.0-alpha27" "6.0.0-alpha25" "4.0.0-alpha28" "6.0.0-alpha26" "4.0.0-alpha29" "6.0.0-alpha27" "6.0.0-alpha28" "6.0.0-alpha29" "4.0.0-beta10" "4.0.0-beta11" "4.0.0-beta12" "4.0.0-beta13" "4.0.0-beta14" "4.0.0-beta15" "4.0.0-beta16" "4.0.0-beta17" "4.0.0-beta18" "4.0.0-beta19" "4.0.0-alpha30" "4.0.0-alpha31" "4.0.0-alpha32" "6.0.0-alpha30" "4.0.0-alpha33" "6.0.0-alpha31" "4.0.0-alpha34" "6.0.0-alpha32" "4.0.0-alpha35" "6.0.0-alpha33" "4.0.0-alpha36" "6.0.0-alpha34" "3.2.0-beta1" "4.0.0-alpha37" "6.0.0-alpha35" "3.2.0-beta2" "4.0.0-alpha38" "6.0.0-alpha36" "5.0.0-versioning-frozen" "3.2.0-beta3" "4.0.0-alpha39" "6.0.0-alpha37" "2.2.3-beta2" "3.2.0-beta4" "2.2.3-beta3" "3.2.0-beta5" "3.2.0-beta6" "4.0.0-beta20" "3.2.0-beta7" "4.0.0-beta21" "3.2.0-beta8" "4.0.0-beta22" "3.2.0-beta9" "1.3" "4.0.0-beta23" "3.0.0" "4.0.0-beta24" "3.0.1" "2.2.0" "4.0.0-beta25" "2.2.1" "4.0.0-beta26" "2.2.2" "4.0.0-beta27" "4.0.0-beta28" "4.0.0-beta29" "1.8.0" "1.8.1" "1.8.2" "1.8.3" "1.8.4" "4.0.0-alpha40" "4.0.0-alpha41" "4.0.0-alpha42" "4.0.0-alpha43" "4.0.0-alpha44" "4.0.0-alpha45" "4.0.0-alpha46" "4.0.0-alpha47" "5.0.0-test3" "4.0.0-alpha48" "5.0.0-test4" "4.0.0-alpha49" "1.11.0" "5.0.0-test5" "1.11.1" "5.0.0-test6" "5.0.0-test7" "5.0.0-test8" "4.0.0-beta30" "new_ui_v1__RB-4-alpha-1" "5.0.0-test9" "4.0.0-beta31" "issue_9013_pre" "4.0.0-beta32" "4.0.0-beta33" "6.0.0-alpha1" "4.0.0-beta34" "6.0.0-alpha2" "4.0.0-beta35" "6.0.0-alpha3" "4.0.0-beta36" "6.0.0-alpha4" "4.0.0-beta37" "4.0.0-beta38" "4.0.0-beta39" "6.0.0-alpha7" "6.0.0-alpha8" "6.0.0-alpha9" "4.0.0-alpha50" "4.0.0-alpha51" "4.0.0-alpha52" "4.0.0-alpha53" "4.0.0-beta29-img-beta2" "4.0.0-alpha54" "4.0.0-beta29-img-beta3" "5.0.0-alpha10" "4.0.0-alpha55" "4.0.0-beta29-img-beta4" "5.0.0-alpha11" "4.0.0-beta29-img-beta5" "5.0.0-alpha12" "4.0.0-beta29-img-beta6" "5.0.0-alpha13" "4.0.0-beta29-img-beta7" "5.0.0-alpha14" "4.0.0-beta29-img-beta8" "5.0.0-alpha15" "4.0.0-beta29-img-beta9" "5.0.0-alpha16" "5.0.0-alpha17" "5.0.0-alpha18" "4.0.0_new_ui_v1-alpha2" "4.0.0-beta40" "5.0.0-alpha19" "4.0.0-beta41" "2.1.0" "2.1.1" "1.3.3" "1.7.0" "1.7.1" "1.7.2" "1.7.3" "1.7.4" "1.7.5" "5.0.0-alpha20" "5.0.0-alpha21" "5.0.0-alpha22" "5.0.0-alpha23" "6.0.0-apha35" "1.10.0" "1.10.1" "1.10.2" "1.10.3" "5.0.0-test10" "5.0.0-test11" "5.0.0-test12" "5.0.0-test13" "4.0.0-alpha1" "5.0.0-test14" "4.0.0-alpha2" "5.0.0-test15" "4.0.0-alpha3" "4.0.0-alpha4" "4.0.0-alpha5" "4.0.0-alpha6" "4.0.0-alpha7" "4.0.0-alpha8" "4.0.0-alpha9"])

(defn #^SVNRepository login
  [#^String url, #^String username, #^String password]
  (DAVRepositoryFactory/setup)
  (let [options (SVNWCUtil/createDefaultOptions true)
        manager (SVNClientManager/newInstance options username password)
        parsed-url (SVNURL/parseURIDecoded url)]

    (.createRepository manager parsed-url true))) 


(defn dir [#^SVNRepository repo, #^String path]
  (.getDir repo "" (long -1) nil (java.util.LinkedList.)))    


(defn dir-entry? [#^SVNDirEntry dirEntry]
  (= (.getKind dirEntry) SVNNodeKind/DIR))


(defn tag-names [username password]
  (let [repo (login *svn-url-tags* username password)
        entries (filter dir-entry? (dir repo ""))]

    (map #(.getName %) entries)))



