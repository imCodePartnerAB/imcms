<%@ page import="com.imcode.imcms.servlet.admin.ImageEditPage"%><%@page contentType="text/javascript" %>
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
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALT %>' : image.alt,
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_WIDTH %>'  : image.width,
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_HEIGHT %>'  : image.height,
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_BORDER %>' : image.border,
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALIGN %>'  : image.align,
            '<%= ImageEditPage.REQUEST_PARAMETER__VERTICAL_SPACE %>'   : image.vspace,
            '<%= ImageEditPage.REQUEST_PARAMETER__HORIZONTAL_SPACE %>'  : image.hspace,
            '<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_NAME %>'  : image.name
        };
    }
    var queryString = '';
    for ( var i in outparam )
    {
        if (outparam[i]) {
            queryString += (queryString.length ? '&' : '?') + i + '=' + encodeURIComponent(outparam[i]);
        }
    }
    this._popupDialog(
            '<%= request.getContextPath() %>/servlet/InsertImage'+queryString,
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
                            img.alt = value;
                            break;
                        case "border":
                            img.border = parseInt(value || "0", 10);
                            break;
                        case "align":
                            img.align = value;
                            break;
                        case "vert":
                            img.vspace = parseInt(value || "0", 10);
                            break;
                        case "horiz":
                            img.hspace = parseInt(value || "0", 10);
                            break;
                        case "width":
                            img.width = parseInt(value, 10);
                            break;
                        case "height":
                            img.height = parseInt(value, 10);
                            break;
                        case "name":
                            img.name = value;
                            break;
                    }
                }
            },
            outparam);
};
