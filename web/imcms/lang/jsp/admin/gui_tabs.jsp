<%!

final static String COLOR_ACT   = "#f5f5f7" ;
final static String COLOR_INACT = "#4076ad" ;


private String getTabs( String[][] tabTextsLinks, int actTabIdx ) {
	String retVal = "" ;
	if (tabTextsLinks != null && tabTextsLinks.length > 0) {
		retVal += createNavBar("", "", "START"  , false, (actTabIdx == 0), 25) ;
		for (int i = 0; i < tabTextsLinks.length; i++) {
			boolean thisAct     = (i == actTabIdx) ;
			boolean nextAct     = ((i+1) == actTabIdx) ;
			String leftMidRight = (i == 0) ? "L" : (i == tabTextsLinks.length - 1) ? "R" : "M" ;
	    retVal += createNavBar(tabTextsLinks[i][0], tabTextsLinks[i][1], leftMidRight, thisAct , nextAct, 0) ;
		}
	  retVal += createNavBar("", "", "END", false, false, 0) ;
	}
	return retVal ;
}


private String createNavBar( String text, String url, String pos, boolean thisAct, boolean nextAct, int width) {
	String sRet, topImg, midRightImg, sClass, sEvent ;
	int btnH, totH, row1height, row2height, midRightImgW, itype ;
	pos = pos.toUpperCase() ;
	itype = 2 ; // all grey
	if (width <= 0) { // ***** width depends on text-length if 0
		width = (text.length() * 7) + 30 ;
	}
	btnH = 20 ;
	totH = btnH ;
	if (url.length() > 0 && !thisAct) {
		text = "<a href=\"" + url + "\">" + text + "</a>" ;
		sEvent = " onClick=\"document.location = '" + url + "';\"" ;
	} else {
		sEvent = "" ;
	}
	midRightImgW = 5 ;
	if (thisAct) {
		row1height = 1 ;
		topImg = "1x1_ffffff.gif" ;
		sClass = "NavBtnTextAct" ;
		if (pos.equals("R")) {
			midRightImg = "nav_right_act" + itype + ".gif" ;
			midRightImgW = 3 ;
		} else {
			midRightImg = "nav_mid_act_inact" + itype + ".gif" ;
		}
	} else {
		row1height = 3 ;
		topImg = "nav_top_line.gif" ;
		sClass = "NavBtnTextInact" ;
		if (pos.equals("R")) {
			midRightImg = "nav_right_inact" + itype + ".gif" ;
			midRightImgW = 3 ;
		} else {
			if (nextAct) {
				midRightImg = "nav_mid_inact_act" + itype + ".gif" ;
			} else {
				midRightImg = "nav_mid_inact_inact" + itype + ".gif" ;
			}
		}
	}
	row2height = btnH - row1height ;
	sRet = "" ;
	if (pos.equals("START")) {
		sRet += "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" background=\"" + IMG_PATH + "/tabs/nav_bg_line" + itype + ".gif\">\n" ;
		sRet += "<tr>\n" ;
		sRet += "	<td>\n" ;
		sRet += "	<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" ;
		sRet += "	<tr>\n" ;
		if (width > 0) {
			sRet += "		<td><img src=\"" + IMG_PATH + "/tabs/1x1.gif\" width=\"" + width + "\" height=\"1\"></td>\n" ;
		}
	}
	if (!pos.equals("START") && !pos.equals("END")) {
		sRet += "		<td>\n" ;
		sRet += "		<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"" + totH + "\">\n" ;
		sRet += "		<tr>\n" ;
		if (pos.equals("L") && thisAct) {
			sRet += "			<td rowspan=\"2\"><img src=\"" + IMG_PATH + "/tabs/nav_left_act" + itype + ".gif\" width=\"5\" height=\"" + totH + "\"></td>\n" ;
		} else if (pos.equals("L")) {
			sRet += "			<td rowspan=\"2\"><img src=\"" + IMG_PATH + "/tabs/nav_left_inact" + itype + ".gif\" width=\"5\" height=\"" + totH + "\"></td>\n" ;
		}
		sRet += "			<td height=\"" + row1height + "\"><img src=\"" + IMG_PATH + "/tabs/" + topImg + "\" width=\"" + width + "\" height=\"" + row1height + "\"></td>\n" ;
		sRet += "			<td rowspan=\"2\"><img src=\"" + IMG_PATH + "/tabs/" + midRightImg + "\" width=\"" + midRightImgW + "\" height=\"" + totH + "\"></td>\n" ;
		sRet += "		</tr>\n" ;
		sRet += "		<tr>\n" ;
		sRet += "			<td height=\"" + row2height + "\" align=\"center\" bgcolor=\"" + (thisAct ? COLOR_ACT : COLOR_INACT) + "\" class=\"" + sClass + "\"" + sEvent + ">" ;
		sRet +=      "<span class=\"" + sClass + "\">" + text + "</span></td>\n" ;
		sRet += "		</tr>\n" ;
		sRet += "		</table></td>\n" ;
	}
	if (pos.equals("END")) {
		sRet += "	</tr>\n" ;
		sRet += "	</table></td>\n" ;
		sRet += "	<td><img src=\"" + IMG_PATH + "/tabs/1x1.gif\" width=\"1\" height=\"1\"></td>\n" ;
		sRet += "</tr>\n" ;
		sRet += "</table>" ;
	}
	return sRet ;
}

%>