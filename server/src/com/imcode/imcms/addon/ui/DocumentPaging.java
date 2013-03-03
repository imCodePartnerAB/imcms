package com.imcode.imcms.addon.ui;

/**
 * Created by IntelliJ IDEA.
 * User: Tommy Ullberg, imCode
 * Mail: tommy@imcode.com
 * Date: 2010-mar-31
 * Time: 15:12:27
 */
public class DocumentPaging {
	String thisPagesPath = "" ;
	int currentPageNumber = 0 ;
	int docListSize = 0 ;
	int hitsPerPage = 10 ;
	int shortVisPagesCount = 3 ;
	int maxVisPagesCount = 12 ;
	String spaceBetween = "" ;
	String divId = "" ;
	String divClass = "paging" ;
	String textPrev = "&laquo;" ;
	String titlePrev = "F\u00f6reg\u00e5ende" ;
	String textNext = "&raquo;" ;
	String titleNext = "N\u00e4sta" ;

	public DocumentPaging(String thisPagesPath, int currentPageNumber, int docListSize, int hitsPerPage) {
		this.thisPagesPath = thisPagesPath;
		this.currentPageNumber = currentPageNumber;
		this.docListSize = docListSize;
		this.hitsPerPage = hitsPerPage;
	}

	public void setThisPagesPath(String thisPagesPath) {
		this.thisPagesPath = thisPagesPath;
	}

	public void setCurrentPageNumber(int currentPageNumber) {
		this.currentPageNumber = currentPageNumber;
	}

	public void setDocListSize(int docListSize) {
		this.docListSize = docListSize;
	}

	public void setHitsPerPage(int hitsPerPage) {
		this.hitsPerPage = hitsPerPage;
	}

	public void setShortVisPagesCount(int shortVisPagesCount) {
		this.shortVisPagesCount = shortVisPagesCount;
	}

	public void setMaxVisPagesCount(int maxVisPagesCount) {
		this.maxVisPagesCount = maxVisPagesCount;
	}

	public void setSpaceBetween(String spaceBetween) {
		this.spaceBetween = spaceBetween;
	}

	public void setDivId(String divId) {
		this.divId = divId;
	}

	public void setDivClass(String divClass) {
		this.divClass = divClass;
	}

	public void setTextPrev(String textPrev) {
		this.textPrev = textPrev;
	}

	public void setTitlePrev(String titlePrev) {
		this.titlePrev = titlePrev;
	}

	public void setTextNext(String textNext) {
		this.textNext = textNext;
	}

	public void setTitleNext(String titleNext) {
		this.titleNext = titleNext;
	}

	public String getPagingLinksAsHtml () {
	
		if (currentPageNumber < 1) {
			currentPageNumber = 1 ;
		}
		int iNbrOfPages = (int) Math.ceil((double) docListSize / (double) hitsPerPage) ;
		
		//int shortVisPagesCount ; // this = N => NNN...nnnnnnnnnnn OR NNN...nnnnnnnn...NNN
		int midVisPagesCount  = (maxVisPagesCount - (2 * shortVisPagesCount) - 2) ; //  // this = N => nnn...NNNNNNNN...nnn
		int longVisPagesCount = (maxVisPagesCount - shortVisPagesCount - 1) ; // this = N => nnn...NNNNNNNNNNN OR NNNNNNNNNNN...nnn
		
		StringBuffer sb = new StringBuffer() ;
		int i ;
		
		String debug = "" ;
		
		// hide paging if only one page
		if (iNbrOfPages >= 1) {
			sb.append("<div" + (!"".equals(divId) ? " id=\"" + divId + "\"" : "") + (!"".equals(divClass) ? " class=\"" + divClass + "\"" : "") + ">") ;
			
			/* *******************************************************************************************
			 *         Prev button                                                                       *
			 ******************************************************************************************* */
			
			if (currentPageNumber != 1) {
				sb.append(spaceBetween + "<a href=\"" + thisPagesPath + "?idx=" + (currentPageNumber - 1) + "\" class=\"prevBtn toolTip\" title=\"" + titlePrev + "\">" + textPrev + "</a>") ;
			} else {
				sb.append(spaceBetween + "<span class=\"prevBtn dim\">" + textPrev + "</span>") ;
			}
			
			/* *******************************************************************************************
			 *         No special paging - Less than MAX                                                                *
			 ******************************************************************************************* */
			
			if (iNbrOfPages <= maxVisPagesCount) {
				debug += "<br/>group1" ;
				// numbers
				for (i = 0; i < iNbrOfPages; i++) {
					String singleDigitClass = (1 == ((i + 1) + "").length()) ? " class=\"oneDigit\"" : "" ;
					String singleDigitCl    = (1 == ((i + 1) + "").length()) ? " oneDigit" : "" ;
					if (i != currentPageNumber - 1) {
						sb.append(spaceBetween + "<a href=\"" + thisPagesPath + "?idx=" + (i + 1) + "\"" + singleDigitClass + ">" + (i + 1) + "</a>") ;
					} else {
						sb.append(spaceBetween + "<span class=\"active" + singleDigitCl + "\">" + (i + 1) + "</span>") ;
					}
				}
				
			} else {
				
				/* *******************************************************************************************
				 *         2 group paging - First - LONG FIRST + SHORT LAST                                  *
				 ******************************************************************************************* */
				
				if (currentPageNumber < longVisPagesCount) {
					debug += "<br/>group2" ;
					// numbers
					for (i = 0; i < longVisPagesCount; i++) {
						String singleDigitClass = (1 == ((i + 1) + "").length()) ? " class=\"oneDigit\"" : "" ;
						String singleDigitCl    = (1 == ((i + 1) + "").length()) ? " oneDigit" : "" ;
						if (i != currentPageNumber - 1) {
							sb.append(spaceBetween + "<a href=\"" + thisPagesPath + "?idx=" + (i + 1) + "\"" + singleDigitClass + ">" + (i + 1) + "</a>") ;
						} else {
							sb.append(spaceBetween + "<span class=\"active" + singleDigitCl + "\">" + (i + 1) + "</span>") ;
						}
					}
					
					sb.append(spaceBetween + "<span class=\"paging_dots\">...</span>") ;
					
					// numbers
					for (i = iNbrOfPages - shortVisPagesCount; i < iNbrOfPages; i++) {
						String singleDigitClass = (1 == ((i + 1) + "").length()) ? " class=\"oneDigit\"" : "" ;
						String singleDigitCl    = (1 == ((i + 1) + "").length()) ? " oneDigit" : "" ;
						if (i != currentPageNumber - 1) {
							sb.append(spaceBetween + "<a href=\"" + thisPagesPath + "?idx=" + (i + 1) + "\"" + singleDigitClass + ">" + (i + 1) + "</a>") ;
						} else {
							sb.append(spaceBetween + "<span class=\"active" + singleDigitCl + "\">" + (i + 1) + "</span>") ;
						}
					}
				
				/* *******************************************************************************************
				 *         2 group paging - Last - SHORT FIRST + LONG LAST                                   *
				 ******************************************************************************************* */
				
				} else if (currentPageNumber >= iNbrOfPages - shortVisPagesCount) {
					debug += "<br/>group3" ;
					// numbers
					for (i = 0; i < shortVisPagesCount; i++) {
						String singleDigitClass = (1 == ((i + 1) + "").length()) ? " class=\"oneDigit\"" : "" ;
						String singleDigitCl    = (1 == ((i + 1) + "").length()) ? " oneDigit" : "" ;
						if (i != currentPageNumber - 1) {
							sb.append(spaceBetween + "<a href=\"" + thisPagesPath + "?idx=" + (i + 1) + "\"" + singleDigitClass + ">" + (i + 1) + "</a>") ;
						} else {
							sb.append(spaceBetween + "<span class=\"active" + singleDigitCl + "\">" + (i + 1) + "</span>") ;
						}
					}
					
					sb.append(spaceBetween + "<span class=\"paging_dots\">...</span>") ;
					
					// numbers
					for (i = iNbrOfPages - longVisPagesCount; i < iNbrOfPages; i++) {
						String singleDigitClass = (1 == ((i + 1) + "").length()) ? " class=\"oneDigit\"" : "" ;
						String singleDigitCl    = (1 == ((i + 1) + "").length()) ? " oneDigit" : "" ;
						if (i != currentPageNumber - 1) {
							sb.append(spaceBetween + "<a href=\"" + thisPagesPath + "?idx=" + (i + 1) + "\"" + singleDigitClass + ">" + (i + 1) + "</a>") ;
						} else {
							sb.append(spaceBetween + "<span class=\"active" + singleDigitCl + "\">" + (i + 1) + " </span>") ;
						}
					}
				
				/* *******************************************************************************************
				 *         3 group paging - SHORT FIRST + MID + SHORT LAST                                   *
				 ******************************************************************************************* */
				
				} else {
					debug += "<br/>group4" ;
					// numbers
					for (i = 0; i < shortVisPagesCount; i++) {
						String singleDigitClass = (1 == ((i + 1) + "").length()) ? " class=\"oneDigit\"" : "" ;
						String singleDigitCl    = (1 == ((i + 1) + "").length()) ? " oneDigit" : "" ;
						if (i != currentPageNumber - 1) {
							sb.append(spaceBetween + "<a href=\"" + thisPagesPath + "?idx=" + (i + 1) + "\"" + singleDigitClass + ">" + (i + 1) + "</a>") ;
						} else {
							sb.append(spaceBetween + "<span class=\"active" + singleDigitCl + "\">" + (i + 1) + "</span>") ;
						}
					}
					
					sb.append(spaceBetween + "<span class=\"paging_dots\">...</span>") ;
					
					// numbers
					for (i = currentPageNumber - (midVisPagesCount / 2); i < currentPageNumber + (midVisPagesCount / 2); i++) {
						String singleDigitClass = (1 == ((i + 1) + "").length()) ? " class=\"oneDigit\"" : "" ;
						String singleDigitCl    = (1 == ((i + 1) + "").length()) ? " oneDigit" : "" ;
						if (i != currentPageNumber - 1) {
							sb.append(spaceBetween + "<a href=\"" + thisPagesPath + "?idx=" + (i + 1) + "\"" + singleDigitClass + ">" + (i + 1) + "</a>") ;
						} else {
							sb.append(spaceBetween + "<span class=\"active" + singleDigitCl + "\">" + (i + 1) + "</span>") ;
						}
					}
					
					sb.append(spaceBetween + "<span class=\"paging_dots\">...</span>") ;
					
					// numbers
					for (i = iNbrOfPages - shortVisPagesCount; i < iNbrOfPages; i++) {
						String singleDigitClass = (1 == ((i + 1) + "").length()) ? " class=\"oneDigit\"" : "" ;
						String singleDigitCl    = (1 == ((i + 1) + "").length()) ? " oneDigit" : "" ;
						if (i != currentPageNumber - 1) {
							sb.append(spaceBetween + "<a href=\"" + thisPagesPath + "?idx=" + (i + 1) + "\"" + singleDigitClass + "> " + (i + 1) + "</a>") ;
						} else {
							sb.append(spaceBetween + "<span class=\"active" + singleDigitCl + "\">" + (i + 1) + " </span>") ;
						}
					}
				
				}
			}
			
			/* *******************************************************************************************
			 *         Next button                                                                       *
			 ******************************************************************************************* */
			
			if (currentPageNumber < iNbrOfPages) {
				sb.append(spaceBetween + "<a href=\"" + thisPagesPath + "?idx=" + (currentPageNumber + 1) + "\" class=\"nextBtn toolTip\" title=\"" + titleNext + "\">" + textNext + "</a>") ;
			} else {
				sb.append(spaceBetween + "<span class=\"nextBtn dim\">" + textNext + "</span>") ;
			}
			//sb.append("</div>iNbrOfPages: " + iNbrOfPages + "<br/>currentPageNumber: " + currentPageNumber + "<br/>debug: " + debug) ;
			sb.append("</div>") ;
	
			// builds string
			return sb.toString() ;
		}
		return "" ;
	}
}
