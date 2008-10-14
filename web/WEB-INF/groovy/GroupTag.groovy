import imcode.server.parser.ParserParametersdef request = groupTag.pageContext.requestdef response = groupTag.pageContext.responsedef parserParameters = ParserParameters.fromRequest(request)

if (!parserParameters.groupMode) {	} else {
    }