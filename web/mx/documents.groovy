import imcode.server.*;
import com.imcode.imcms.mapping.*;

DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
CachingDocumentGetter cachingDocumentGetter = documentMapper.getDocumentGetter();

// Execute command if any
def cmd = request.getParameter("cmd")
if (cmd) {
    def metaId = request.getParameter("metaId")
    
    switch(cmd) {
        case "remove":
            def document = documentMapper.getDocument(Integer.parseInt(metaId))
            documentMapper.invalidateDocument(document)
            break
            
        case "removeAll":
            break
    }
}

def caches = [
	"Published": cachingDocumentGetter.publishedDocumentsCache,  
	"Working": cachingDocumentGetter.workingDocumentsCache
]

html.html {
    head {
        title("Cahced documents")
    }
    body {
        // Shows cached documents in each cache
        caches.each{cacheName, cache ->
        	br()
	        div(cacheName)
	        table(border:1) {
	            tr {
	                th(align:"left", "Meta id")
	                th(align:"left", "Version")
	                th(align:"left", "Headline")
	                th(align:"left", "Versions")
	                th(align:"left", "Options") 
	            }
	            cache.each { metaId, document ->
	                tr {        	                
	                    td(align:"right", "${metaId}")
	                    td(align:"right", "${document.meta.version.version}")
	                    td("${document.headline}")
	                    td {
	                        table(border:1) {
	                        
	                             documentMapper.getDocumentVersions(metaId).each { v ->
	                                 tr {
	                                    td(v.version)
	                                    td(v.versionTag)
	                                 }
	                             }
	                            
	                        }                        
	                    }
	                    
	                    td {
	                        a(href:"?cmd=remove&metaId=${metaId}", "Remove from cache")
	                    }
	                } //tr
	            } // cache.each  	    	  
	        } //table
	        
	    } // caches.each   
    } // body        
} //html