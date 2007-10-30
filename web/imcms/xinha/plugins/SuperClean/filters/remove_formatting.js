function(html) {
	html = html
		.replace(/\r\n/g, " ")
		.replace(/\n/g, " ")
		.replace(/\r/g, " ")
		.replace(/(\&|\&amp\;)nbsp\;/g, " ") ;
	
	html = html
		.replace(/[ ]+/g, " ") ;
	
	html = html
		.replace(/<\/(P|DIV|TD|LI|H[1-6])>/gi, "\n\n")
		.replace(/<br[^>]*?>/gi, "\n") ;
	
	html = html
		.replace(/<[^>]+?>/g, "") ;
	
	html = html
		.replace(/(\r?\n)\s*(\r?\n)+\s*/g, "$1$1") ;
	
	html = html.trim() ;
	
	html = html
		.replace(/(\r?\n)/g, "<br/>$1")
		.replace(/(\r?\n)[ ]+/g, "$1") ;
	
	return html.trim() ;
}
String.prototype.trim = function() {
	return this.replace(/^\s+/g, "").replace(/\s+$/g, "") ;
} ;