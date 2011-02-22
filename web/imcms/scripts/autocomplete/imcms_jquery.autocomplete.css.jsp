<%@ page
	
	contentType="text/css"
	pageEncoding="UTF-8"
	
%><%

String cp = request.getContextPath() ;

%>
.ac_results {
	padding: 0;
	border: 1px solid #ccc;
	border-top: 0;
	background-color: white;
	overflow: hidden;
	z-index: 99999;
}

.ac_results ul {
	width: 100%;
	list-style-position: outside;
	list-style: none;
	padding: 0;
	margin: 0;
}

.ac_results li {
	margin: 0;
	padding: 2px 5px;
	border-top: 1px solid #ccc;
	cursor: pointer;
	display: block;
	/* 
	if width will be 100% horizontal scrollbar will apear 
	when scroll mode will be used
	*/
	/*width: 100%;*/
	font: 10px Verdana,Geneva,sans-serif !important;
	/* 
	it is very important, if line-height not setted or setted 
	in relative units scroll will be broken in firefox
	*/
	line-height: 16px !important;
	overflow: hidden;
	text-align: left;
}

.ac_loading {
	background: white url('<%= cp %>/imcms/images/icons/ajax-loader.gif') right center no-repeat;
}

.ac_odd {
	background-color: #fff;
}
.ac_even {
	background-color: #fff;
}

.ac_over {
	background-color: #dfebfc;
	color: #000;
}
