' **********************************
' *   By: Tommy Ullberg, imCode
' *   www.imcode.com
' *   Copyright © imCode AB
' **********************************


Dim objMyRange

Sub test_onclick
	Set objSelRange = document.selection.createRange
	strTagName = objSelRange.parentElement.tagName
	If IsEmpty(objMyRange) Then 
		Set objMyRange = objSelRange.duplicate
	End If
	Set objMyRange = objSelRange.duplicate
	strNewText = "<font CLASS=""heading"">" & objMyRange.text & "</font>" 
	objMyRange.pasteHTML strNewText
End Sub


' * Top Format Selector *

Sub topClassSelect_onchange
	Set objSelRange = document.selection.createRange
	strTagName = objSelRange.parentElement.tagName
	sClassName = editorForm.topClassSelect.options(editorForm.topClassSelect.selectedIndex).value
	If IsEmpty(objMyRange) Then 
		Set objMyRange = objSelRange.duplicate
	End If
	Set objMyRange = objSelRange.duplicate
	if objMyRange.text <> "" then
		strNewText = "<font CLASS=""" & sClassName & """>" & objMyRange.text & "</font>"
	else
		strNewText = objMyRange.text
	end if 
	objMyRange.pasteHTML strNewText
	editorForm.topClassSelect.selectedIndex = 0
	editorDiv.focus
End Sub


' * Top Format Selector - MS-Word classes *

Sub topClassWordSelect_onchange
	Set objSelRange = document.selection.createRange
	strTagName = objSelRange.parentElement.tagName
	sClassName = editorForm.topClassWordSelect.options(editorForm.topClassWordSelect.selectedIndex).value
	If IsEmpty(objMyRange) Then 
		Set objMyRange = objSelRange.duplicate
	End If
	Set objMyRange = objSelRange.duplicate
	if objMyRange.text <> "" then
		strNewText = "<font CLASS=""" & sClassName & """>" & objMyRange.text & "</font>"
	else
		strNewText = objMyRange.text
	end if 
	objMyRange.pasteHTML strNewText
	editorForm.topClassWordSelect.selectedIndex = 0
	editorDiv.focus
End Sub


' * Create FONT style="x"

Sub styleBtn_onclick
	' * font-family
	sFontFamily = editorForm.FontFamily.options(editorForm.FontFamily.selectedIndex).value
	select case sFontFamily
		case "Verdana"
			sFontFamily = "font-family: Verdana,Geneva,Helvetica,sans-serif; "
		case "Arial"
			sFontFamily = "font-family: Arial,Helvetica,Geneva,sans-serif; "
		case "Times"
			sFontFamily = "font-family: Times New Roman,Times,serif; "
		case "Courier"
			sFontFamily = "font-family: Courier New,Courier,monospace; "
		case "Comic"
			sFontFamily = "font-family: Comic Sans MS,fantasy; "
		case else
			sFontFamily = ""
	end select
	' * font-weight / font-style
	sFontFormat = editorForm.FontFormat.options(editorForm.FontFormat.selectedIndex).value
	select case sFontFormat
		case "normal"
			sFontFormat = "font-weight:normal; font-style:normal; "
		case "bold"
			sFontFormat = "font-weight:bold; font-style:normal; "
		case "italic"
			sFontFormat = "font-weight:normal; font-style:italic; "
		case "both"
			sFontFormat = "font-weight:bold; font-style:italic; "
		case else
			sFontFormat = ""
	end select
	' * font-size
	sFontSize = editorForm.FontSize.options(editorForm.FontSize.selectedIndex).value
	if sFontSize <> "" then
		sFontSize = "font-size:" & sFontSize & "; "
	end if
	' * line-height
	sLineHeight = editorForm.LineHeight.options(editorForm.LineHeight.selectedIndex).value
	if sLineHeight <> "" then
		sLineHeight = "line-height:" & sLineHeight & "; "
	end if
	' * color
	if (editorForm.usecolor.checked) then
		iRed = colorForm.fRed.value
		iGreen = colorForm.fGreen.value
		iBlue = colorForm.fBlue.value
		if iRed < 0 then
			iRed = 0 
		elseif iRed > 255 then
			iRed = 255
		elseif Not IsNumeric(iRed) then
			iRed = 0
		end if
		if iGreen < 0 then
			iGreen = 0
		elseif iGreen > 255 then
			iGreen = 255
		elseif Not IsNumeric(iGreen) then
			iGreen = 0
		end if
		if iBlue < 0 then
			iBlue = 0
		elseif iBlue > 255 then
			iBlue = 255
		elseif Not IsNumeric(iBlue) then
			iBlue = 0
		end if
		sColor = "color: rgb(" & iRed & "," & iGreen & "," & iBlue & "); "
	else
		sColor = ""
	end if
	
	' * Set the style to the current selection
	Set objSelRange = document.selection.createRange
	strTagName = objSelRange.parentElement.tagName
	If IsEmpty(objMyRange) Then 
		Set objMyRange = objSelRange.duplicate
	End If
	Set objMyRange = objSelRange.duplicate
	strNewText = "<font style=""" & sFontSize & sLineHeight & sFontFormat & sFontFamily & sColor & """>" & objMyRange.text & "</font>" 
	objMyRange.pasteHTML strNewText
End Sub




' * Insert code
Sub codeBtn_onclick
	' * Start code
	sStartCode = editorForm.startCode.value
	if sStartCode <> "" then
		sStartCode = sStartCode
	else
		sStartCode = ""
	end if
	' * End code
	sEndCode = editorForm.endCode.value
	if sEndCode <> "" then
		sEndCode = sEndCode
	else
		sEndCode = ""
	end if
	
	' * Set the style to the current selection
	Set objSelRange = document.selection.createRange
	strTagName = objSelRange.parentElement.tagName
	If IsEmpty(objMyRange) Then 
		Set objMyRange = objSelRange.duplicate
	End If
	Set objMyRange = objSelRange.duplicate
	strNewText = sStartCode & objMyRange.text & sEndCode
	objMyRange.pasteHTML strNewText
End Sub


Dim iElem

' * Insert class
Sub classBtn_onclick
	' * Selected class
	For iElem = 0 To 10
		If editorForm.useClass(iElem).Checked = True Then
			iSelClass = iElem + 1
		End If
	Next
	'MsgBox iSelClass	
	
	iSelClassField = "useClass" & iSelClass
	sClass = editorForm(iSelClassField).value
	
	if not sClass = "" then
		sStartCode = "<font class=""" & sClass & """>"
		sEndCode = "</font>"
		' * Set the class to the current selection
		Set objSelRange = document.selection.createRange
		If IsEmpty(objMyRange) Then 
			Set objMyRange = objSelRange.duplicate
		End If
		Set objMyRange = objSelRange.duplicate
		strNewText = sStartCode & objMyRange.text & sEndCode
		objMyRange.pasteHTML strNewText
	end if
End Sub




' * Insert link
Sub CreateLink_onclick
	
	createLinkTarget = editorForm.createLinkTarget.value
	createLinkValue = editorForm.createLinkValue.value
	createLinkCss = editorForm.createLinkCss.value
	createLinkType = editorForm.createLinkType.options(editorForm.createLinkType.selectedIndex).value
	select case createLinkType
		case "GetDoc"
			sHref = getDocPath & "GetDoc?meta_id=" & createLinkValue
		case "http", "ftp"
			sHref = createLinkValue
		case "mailto"
			sHref = "mailto:" & createLinkValue
	end select
	
	' * Start link
	sStartCode = "<A HREF=""" & sHref & """"
	if createLinkTarget <> "" then
		sStartCode = sStartCode & " TARGET=""" & createLinkTarget & """"
	end if
	if createLinkCss <> "" then
		sStartCode = sStartCode & " CLASS=""" & createLinkCss & """"
	end if
	sStartCode = sStartCode & ">"
	' * End link
	sEndCode = "</A>"
	
	' * Set the style to the current selection
	Set objSelRange = document.selection.createRange
	If isObject(objSelRange) Then
		strTagName = objSelRange.parentElement.tagName
		If IsEmpty(objMyRange) Then 
			Set objMyRange = objSelRange.duplicate
		End If
		Set objMyRange = objSelRange.duplicate
		theText = objMyRange.text
		If len(theText) = 0 Then theText = "&nbsp;"
		strNewText = sStartCode & theText & sEndCode
		'MsgBox(len(objMyRange.text) & " / " & strTagName & " / " & isObject(objSelRange) & " - " & isObject(objMyRange) & " / " & IsEmpty(objSelRange) & " - " & IsEmpty(objMyRange))
		If strTagName = "INPUT" Then
			MsgBox("<? install/htdocs/sv/htmleditor/scripts/editor_functions.vbs/1 ?>" & vbNewLine & "<? install/htdocs/sv/htmleditor/scripts/editor_functions.vbs/2 ?>!")
		Else
			objMyRange.pasteHTML strNewText
		End If
	End If
End Sub




' * Insert list
Sub CreateList_onclick
	sListCode = ""
	sClass = ""
	iCountUL = 0
	iCountOL = 0
	iCountDL = 0
	createListCount = editorForm.createListCount.options(editorForm.createListCount.selectedIndex).value
	'MsgBox createListCount
	createListCss = editorForm.createListCss.value
	if createListCss <> "" then
		sClass = " class=""" & createListCss & """"
	end if
	createListType = editorForm.createListType.options(editorForm.createListType.selectedIndex).value
	select case createListType
		case "UL"
			sStartCode = "<UL" & sClass & ">" & vbCrLf
			for iCountUL = 1 to int(createListCount)
				sListCode = sListCode & "<LI></LI>" & vbCrLf
			next
			sEndCode = "</UL>"
			
			
		case "OL"
			createListOLType = editorForm.createListOLType.options(editorForm.createListOLType.selectedIndex).value
			select case createListOLType
				case "A"
					sStartCode = "<OL TYPE=""A""" & sClass & ">" & vbCrLf
				case "a"
					sStartCode = "<OL TYPE=""a""" & sClass & ">" & vbCrLf
				case "I"
					sStartCode = "<OL TYPE=""I""" & sClass & ">" & vbCrLf
				case "i"
					sStartCode = "<OL TYPE=""i""" & sClass & ">" & vbCrLf
				case else
					sStartCode = "<OL TYPE=""1""" & sClass & ">" & vbCrLf
			end select
			for iCountOL = 1 to int(createListCount)
				sListCode = sListCode & "<LI></LI>" & vbCrLf
			next
			sEndCode = "</OL>"
			
			
		case "DL"
			sStartCode = "<DL COMPACT" & sClass & ">"
			for iCountDL = 1 to int(createListCount)
				sListCode = sListCode & "<DT>Term:" & iCountDL & "</DT><DD>Fakta:" & iCountDL & "</DD>" & vbCrLf
			next
			sEndCode = "</DL>"
			
	end select
	
	
	' * Set the style to the current selection
	Set objSelRange = document.selection.createRange
	strTagName = objSelRange.parentElement.tagName
	If IsEmpty(objMyRange) Then 
		Set objMyRange = objSelRange.duplicate
	End If
	Set objMyRange = objSelRange.duplicate
	strNewText = sStartCode & sListCode & sEndCode
	objMyRange.pasteHTML strNewText
End Sub




' * Insert pixel image
Sub CreatePixel_onclick
	sPixelCode = ""
	blnPixelBreakStart = Cbool(editorForm.pixelBreakStart.checked)
	sPixelWidth = editorForm.pixelWidth.value
	sPixelHeight = editorForm.pixelHeight.value
	sPixelBorder = editorForm.pixelBorder.value
	sPixelAlign = editorForm.pixelAlign.options(editorForm.pixelAlign.selectedIndex).value
	blnPixelBreakEnd = Cbool(editorForm.pixelBreakEnd.checked)
	
	' * Build the code...
	if blnPixelBreakStart then
		sPixelCode = "<br>" & vbCrLf
	else
		sPixelCode = ""
	end if
	
	sPixelCode = sPixelCode & "<img src=""" & pixelSrc & """"
	
	if sPixelWidth <= 0 then sPixelWidth = 1 end if
	if sPixelWidth <> "" and IsNumeric(sPixelWidth) then
		sPixelCode = sPixelCode & " width=""" & sPixelWidth & """"
	end if
	
	if sPixelHeight <= 0 then sPixelHeight = 1 end if
	if sPixelHeight <> "" and IsNumeric(sPixelHeight) then
		sPixelCode = sPixelCode & " height=""" & sPixelHeight & """"
	end if
	
	if sPixelBorder <= 0 then sPixelBorder = "" end if
	if sPixelBorder <> "" and IsNumeric(sPixelBorder) then
		sPixelCode = sPixelCode & " border=""" & sPixelBorder & """"
	end if
	
	if sPixelAlign <> "" then
		sPixelCode = sPixelCode & " align=""" & sPixelAlign & """"
	end if
	
	sPixelCode = sPixelCode & ">"
	
	if blnPixelBreakEnd then
		sPixelCode = sPixelCode & "<br>"
	end if
	
	'MsgBox sPixelCode
	Set objSelRange = document.selection.createRange
	strTagName = objSelRange.parentElement.tagName
	If IsEmpty(objMyRange) Then 
		Set objMyRange = objSelRange.duplicate
	End If
	Set objMyRange = objSelRange.duplicate
	strNewText = sPixelCode
	If strTagName = "INPUT" Then
		MsgBox("<? install/htdocs/sv/htmleditor/scripts/editor_functions.vbs/3 ?>!")
	Else
		objMyRange.pasteHTML strNewText
	End If
End Sub




Sub findBtnClick
	textToFind = InputBox("<? install/htdocs/sv/htmleditor/scripts/editor_functions.vbs/4 ?>")
	set r = document.body.createTextRange()
	blnFound = r.findText(textToFind)
	if blnFound then
		r.select()
	else
		MsgBox "<? install/htdocs/sv/htmleditor/scripts/editor_functions.vbs/5 ?>!"
	end if
End Sub