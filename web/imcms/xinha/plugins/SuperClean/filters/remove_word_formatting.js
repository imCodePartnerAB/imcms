function(html) {
	// make one line
	html = html.replace(/\r\n/g, ' ').
		replace(/\n/g, ' ').
		replace(/\r/g, ' ').
		replace(/\&nbsp\;/g,' ');
	
	// keep tags, strip attributes
	html = html.replace(/ class=[^\s|>]*/gi,'').
		replace(/ style=\"[^>]*\"/gi,'').
		replace(/ align=[^\s|>]*/gi,'');
	
	//clean up tags
	html = html.replace(/<b [^>]*>/gi,'<b>').
		replace(/<i [^>]*>/gi,'<i>').
		replace(/<li [^>]*>/gi,'<li>').
		replace(/<ul [^>]*>/gi,'<ul>');
	
	// replace b tags
	html = html.replace(/(<\/?)strong(>)/gi,'$1b$2') ;
	
	// mozilla doesn't like <em> tags
	html = html.replace(/(<\/?)em(>)/gi,'$1i$2') ;
	
	// kill unwanted tags
	html = html.replace(/<\?xml:[^>]*>/g, ''). // Word xml
		replace(/<\/?st1:[^>]*>/g,'').           // Word SmartTags
		replace(/<\/?[a-z]\:[^>]*>/g,'').        // All other funny Word non-HTML stuff
		replace(/<\/?font[^>]*>/gi,'').          // Disable if you want to keep font formatting
		replace(/<\/?span[^>]*>/gi,' ').
		replace(/<\/?div[^>]*>/gi,' ').
		replace(/<\/?pre[^>]*>/gi,' ').
		replace(/<\/?h[1-6][^>]*>/gi,' ');
	
	//remove empty tags
	//html = html.replace(/<strong><\/strong>/gi,'').
	//replace(/<i><\/i>/gi,'').
	//replace(/<P[^>]*><\/P>/gi,'');
	
	// nuke double tags
	oldlen = html.length + 1;
	while(oldlen > html.length) {
		oldlen = html.length;
		html = html.replace(/<([a-z][a-z]*)> *<\/\1>/gi,' ').
			replace(/<([a-z][a-z]*)> *<([a-z][^>]*)> *<\/\1>/gi,'<$2>');
	}
	html = html.replace(/<([a-z][a-z]*)><\1>/gi,'<$1>').
		replace(/<\/([a-z][a-z]*)><\/\1>/gi,'<\/$1>');
	
	// nuke double spaces
	html = html.replace(/  */gi,' ');
	
	return html ;
}