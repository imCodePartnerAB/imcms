(ns com.imcode.imcms.release
  #^{:doc ""}
  (:require
    [com.imcode.imcms
      [svn-utils :as svn]
      [release-utils :as rel]]))


(defonce *SVN-URL-IMCMS-TAGS* "https://repo.imcode.com/imcode/imcms/tags")


(defn get-imcms-tag-names [username password]
  "Returns imcms tag names."
  (let [repo (svn/login *SVN-URL-IMCMS-TAGS* username password)
        releases-entries (filter svn/directory? (svn/dir repo ""))]
    (map #(.getName %) releases-entries)))


;;; TODO:
;;; Next release name generation/check
;;;   --final --v A     -> A.B.(inc revision-no) - generates
;;;   --final --v A.B.C -> A.B.C                 - checks

;;;   --pre --v A                   -> A.B.C-[alpha|beta](inc build-no)
;;;   --pre --v A.B.C-[alpha|beta]D -> A.B.C-[alpha|beta]D

;(defn print-usage-and-exit
;  "Prints usage and exit with code 1."
;  []
;  (println)
;  (println "Generates next release name based on provided major version no.")
;  (println "USAGE:")
;  (println "  -u `svn login` -p `svn password` -r `release type` -v `major version no`")
;  (println "  Where `release type` is 'pre' for pre-relase (alpha or beta) or 'final' for final release.")
;  (println "  Examples: -r pre -v 4; -r final -v 6")
;  (System/exit 1))
;
;(defn version? [s]
;  (try
;    (pos? (Integer/valueOf s))
;    (catch Exception e
;      false)))
;
;
;(defn main [& args]
;  (println (map type args))
;  (when-not (== 8 (count args))
;    (print-usage-and-exit))
;
;  (let [args-map (apply assoc {} (map str args))]
;    (when-not (and
;                (== 4 (count (select-keys args-map ["-u" "-p" "-r" "-v"])))
;                (version? (args-map "-v"))
;      (print-usage-and-exit)))))


