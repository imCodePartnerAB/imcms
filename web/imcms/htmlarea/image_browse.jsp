<%

//out.print(request.getContextPath() + "/servlet/ChangeImage?meta_id=1001&img=0&editor_image") ;

request.getRequestDispatcher( request.getContextPath() + "/servlet/ChangeImage?editor_image=true&meta_id=1001&img=0" ).include( request, response ) ;


%>