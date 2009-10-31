(ns com.imcode.imcms.pdf-utils
  (:import
    ;(org.pdfbox.pdmodel PDDocument)                                              ;
    ;(org.pdfbox.util PDFTextStripper)))
    (org.apache.pdfbox.pdmodel PDDocument)
    (org.apache.pdfbox.util PDFTextStripper)
    (org.apache.pdfbox.searchengine.lucene LucenePDFDocument)))

(defn get-text
  "Extracts text from pdf file."
  [filepath]
  (let [pdf (PDDocument/load filepath)
        stripper (PDFTextStripper.)]
    (.getText stripper pdf)))


;(defn to-lucene-doc [filename]
;  (LucenePDFDocument/getDocument (java.io.File. "filename")))