(ns com.imcode.imcms.svn-utils
  #^{:doc "Tiny wrapper around some SVNKit functions."}
  (:import
    (org.tmatesoft.svn.core SVNURL ISVNDirEntryHandler SVNDirEntry SVNNodeKind)
    (org.tmatesoft.svn.core.wc SVNClientManager ISVNOptions SVNWCUtil SVNLogClient SVNRevision)
    (org.tmatesoft.svn.core.io SVNRepositoryFactory SVNRepository)
    (org.tmatesoft.svn.core.internal.io.dav DAVRepositoryFactory)))


(defn #^SVNRepository login
  "Creates DAV repository instance for provided url and credentials."
  [#^String url, #^String username, #^String password]
  (DAVRepositoryFactory/setup)
  (let [options (SVNWCUtil/createDefaultOptions true)
        manager (SVNClientManager/newInstance options username password)
        parsed-url (SVNURL/parseURIDecoded url)]

    (.createRepository manager parsed-url true)))


(defn dir
  "Returns coll of svn dir entries."
  [#^SVNRepository repo, #^String path]
  (.getDir repo path (long -1) nil (java.util.LinkedList.)))


(defn directory?
  "Returns if given svn dir entry is a directory."
  [#^SVNDirEntry dirEntry]
  (= (.getKind dirEntry) SVNNodeKind/DIR))

