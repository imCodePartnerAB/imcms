' **********************************
' *   By: Tommy Ullberg, imCode
' *   www.imcode.com
' *   Copyright © imCode AB
' **********************************

Dim getDocPath, pixelSrc

' ***** Configuration *****
' * Installation catalogue of imCMS GetDoc servlet, with trailing slash
' * /imcms/servlet/ el. http://www.domain.com/servlet/


getDocPath = "<%= request.getContextPath() %>/servlet/"

' * Path/source for transparent pixel (NO leading slash):

pixelSrc = "images/1x1.gif"
'pixelSrc = "imcms/images/transpix.gif"

