<%@ page import="com.imcode.imcms.servlet.admin.ImageEditPage, com.imcode.imcms.servlet.admin.EditImage"%><%@page contentType="text/javascript" %>
Xinha.prototype._insertImage = function(image)
{
    var editor = this;	// for nested functions
    var outparam = null;
    if ( typeof image == "undefined" )
    {
        image = this.getParentElement();
        if ( image && image.tagName.toLowerCase() != 'img' )
        {
            image = null;
        }
    }
    if ( image )
    {
        var src = image.getAttribute("src"),
            url, format, width, height, cropX1, cropY1, cropX2, cropY2,
            rotateAngle, queryIndex, genFile;
            
        if (src && (queryIndex = src.indexOf("?")) != -1) {
            var parts = src.substring(queryIndex + 1).split("&");
            
            for (var i = 0; i < parts.length; ++i) {
                var keyValue = parts[i].split("=");
                
                if (keyValue.length != 2) {
                    continue;
                }
                
                var key = keyValue[0], 
                    value = keyValue[1];
                
                switch (key) {
                    case "path":
                        url = "<%= request.getContextPath() %>" + decodeURIComponent(value);
                        break;
                    case "file_id":
                        url = "<%= request.getContextPath() %>/" + value;
                        break;
                    case "width":
                        width = value;
                        break;
                    case "height":
                        height = value;
                        break;
                    case "format":
                        format = value;
                        break;
                    case "crop_x1":
                        cropX1 = value;
                        break;
                    case "crop_y1":
                        cropY1 = value;
                        break;
                    case "crop_x2":
                        cropX2 = value;
                        break;
                    case "crop_y2":
                        cropY2 = value;
                        break;
                    case "rangle":
                        rotateAngle = value;
                        break;
                    default:
                        break;
                }
            }
        }
    
        outparam =
        {
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_URL %>' : url, 
            '<%= ImageEditPage.REQUEST_PARAMETER__FORMAT_EXTENSION %>' : format, 
            '<%= ImageEditPage.REQUEST_PARAMETER__CROP_X1 %>' : cropX1, 
            '<%= ImageEditPage.REQUEST_PARAMETER__CROP_Y1 %>' : cropY1, 
            '<%= ImageEditPage.REQUEST_PARAMETER__CROP_X2 %>' : cropX2, 
            '<%= ImageEditPage.REQUEST_PARAMETER__CROP_Y2 %>' : cropY2, 
            '<%= ImageEditPage.REQUEST_PARAMETER__ROTATE_ANGLE %>' : rotateAngle || 0, 
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALT %>' : image.alt || image.title,
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_WIDTH %>'  : width || 0,
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_HEIGHT %>'  : height || 0,
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_BORDER %>' : (image.style.borderWidth || image.border || '').replace(/px/, ''),
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALIGN %>'  : image.align,
            '<%= ImageEditPage.REQUEST_PARAMETER__VERTICAL_SPACE %>'   : (image.style.marginTop || image.vspace || '').replace(/px/, ''),
            '<%= ImageEditPage.REQUEST_PARAMETER__HORIZONTAL_SPACE %>'  : (image.style.marginRight || image.hspace || '').replace(/px/, ''),
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_NAME %>'  : image.id || image.name
        };
    }
    var queryString = '';
    for ( var i in outparam )
    {
        if (outparam[i]) {
            queryString += '&' + i + '=' + encodeURIComponent(outparam[i]);
        }
    }
    this._popupDialog(
            '<%= EditImage.linkTo(request, "/imcms/xinha/plugins/ImcmsIntegration/return_image.jsp")%>'+queryString,
            function(param)
            {
                // user must have pressed Cancel
                if ( !param )
                {
                    return false;
                }
                var img = image;
                if ( !img )
                {
                    if ( Xinha.is_ie )
                    {
                        var sel = editor._getSelection();
                        var range = editor._createRange(sel);
                        editor._doc.execCommand("insertimage", false, param.src);
                        img = range.parentElement();
                        // wonder if this works...
                        if ( img.tagName.toLowerCase() != "img" )
                        {
                            img = img.previousSibling;
                        }
                    }
                    else
                    {
                        img = document.createElement('img');
                        img.src = param.src;
                        editor.insertNodeAtSelection(img);
                        if ( !img.tagName )
                        {
                            // if the cursor is at the beginning of the document
                            img = range.startContainer.firstChild;
                        }
                    }
                }
                else
                {
                    img.src = param.src;
                }

                img.removeAttribute("rel");

                for ( var field in param )
                {
                    var value = param[field];
                    switch (field)
                            {
                        case "alt":
                            img.alt = value || " ";
                            img.title = value;
                            break;
                        case "border":
                            img.style.borderWidth = parseInt(value || "0", 10)+"px";
                            break;
                        case "align":
                            img.align = value;
                            break;
                        case "vert":
                            img.style.marginTop = parseInt(value || "0", 10)+"px";
                            img.style.marginBottom = parseInt(value || "0", 10)+"px";
                            break;
                        case "horiz":
                            img.style.marginLeft = parseInt(value || "0", 10)+"px";
                            img.style.marginRight = parseInt(value || "0", 10)+"px";
                            break;
                        case "width":
                            img.style.width = parseInt(value, 10)+"px";
                            img.width = parseInt(value, 10);
                            break;
                        case "height":
                            img.style.height = parseInt(value, 10)+"px";
                            img.height = parseInt(value, 10);
                            break;
                        case "name":
                            img.id = value;
                            break;
                    }
                }
            },
            outparam, {
                scrollbars: "yes", 
                width: screen.availWidth, 
                height: screen.availHeight
            });
};
