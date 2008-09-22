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
        outparam =
        {
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_URL %>' : Xinha.is_ie ? editor.stripBaseURL(image.src) : image.getAttribute("src"),
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALT %>' : image.alt || image.title,
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_WIDTH %>'  : image.style.width.replace(/px/, '') || image.width,
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_HEIGHT %>'  : image.style.height.replace(/px/, '') || image.height,
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
            outparam);
};
