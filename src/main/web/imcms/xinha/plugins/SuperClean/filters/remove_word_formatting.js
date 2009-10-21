function(html) {
	html = html
		.replace(/\r\n/g, " ")
		.replace(/\n/g, " ")
		.replace(/\r/g, " ")
		.replace(/\&nbsp\;/g, " ") ;
	
	html = html
		.replace(/ class=[^\s|>]*/gi, "")
		.replace(/ style=\"[^>]*\"/gi, "")
		.replace(/ align=[^\s|>]*/gi, "") ;
	
	html = html
		.replace(/<b [^>]*>/gi, "<b>")
		.replace(/<i [^>]*>/gi, "<i>")
		.replace(/<li [^>]*>/gi, "<li>")
		.replace(/<ul [^>]*>/gi, "<ul>") ;
	
	html = html
		.replace(/(<\/?)strong(>)/gi, "$1b$2") ;
	
	html = html
		.replace(/(<\/?)em(>)/gi, "$1i$2") ;
	
	html = html
		.replace(/<\?xml:[^>]*>/g, "")
		.replace(/<\/?st1:[^>]*>/g, "")
		.replace(/<\/?[a-z]\:[^>]*>/g, "")
		.replace(/<\/?font[^>]*>/gi, "")
		.replace(/<\/?span[^>]*>/gi, " ")
		.replace(/<\/?div[^>]*>/gi, " ")
		.replace(/<\/?pre[^>]*>/gi, " ")
		.replace(/<\/?h[1-6][^>]*>/gi, " ") ;
	
	oldlen = html.length + 1 ;
	
	while (oldlen > html.length) {
		oldlen = html.length ;
		html = html
			.replace(/<([a-z][a-z]*)> *<\/\1>/gi, " ")
			.replace(/<([a-z][a-z]*)> *<([a-z][^>]*)> *<\/\1>/gi, "<$2>") ;
	}
	
	html = html
		.replace(/<([a-z][a-z]*)><\1>/gi, "<$1>")
		.replace(/<\/([a-z][a-z]*)><\/\1>/gi,"</$1>") ;
	
	html = html
		.replace(/  */gi, " ") ;
	
	return html.trim() ;
}
String.prototype.trim = function() {
	return this.replace(/^\s+/g, "").replace(/\s+$/g, "") ;
} ;