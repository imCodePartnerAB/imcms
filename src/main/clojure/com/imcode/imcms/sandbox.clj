(ns com.imcode.imcms.sandbox
  (:import
    ;(org.pdfbox.pdmodel PDDocument)                                              ;
    ;(org.pdfbox.util PDFTextStripper)))
    (org.apache.pdfbox.pdmodel PDDocument)
    (org.apache.pdfbox.util PDFTextStripper)
    (org.apache.pdfbox.searchengine.lucene LucenePDFDocument)))

(defn get-text [filename]
  (let [filepath (str "/Users/ajosua/Downloads/" filename ".pdf")
        pdf (PDDocument/load filepath)
        stripper (PDFTextStripper.)]
    (.getText stripper pdf)))

(def pst #(.printStackTrace *e))

(defn test-get-text []
  (map get-text ["/Users/ajosua/Downloads/test.pdf"]))

(defn to-lucene-doc [filename]
  (LucenePDFDocument/getDocument (java.io.File. (str "/Users/ajosua/Downloads/" filename))))