import imcode.server.*;
import com.imcode.imcms.mapping.*;

DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
CachingDocumentGetter cachingDocumentGetter = documentMapper.getDocumentGetter();

def cmd = request.getParameter("cmd")

if (cmd) {
    def metaId = request.getParameter("metaId")
    def document = documentMapper.getDocument(Integer.parseInt(metaId))
    
    documentMapper.invalidateDocument(document)
}


html.html {
    head {
        title("Cahced documents")
    }
    body {
        div("Published documents cache")
        table(border:1) {
            tr {
                th(align:"left", "Meta id")
                th(align:"left", "Version")
                th(align:"left", "Headline")
                th(align:"left", "Options")                
            }
            tr {        
                cachingDocumentGetter.publishedDocumentsCache.each {metaId, document ->
                    td(align:"right", "${metaId}")
                    td(align:"right", "${document.meta.version.version}")
                    td("${document.headline}")
                    td {
                        a(href:"?cmd=remove&metaId=${metaId}", "Remove from cache")
                    }
                }    
            } //tr  	    	  
        } //table
    } // body        
} //html