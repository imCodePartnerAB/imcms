<%@ page
	
	import="com.imcode.imcms.servlet.admin.EditLink"
	
	contentType="text/javascript"
	
%>

function CreateLink(editor) {
}

Xinha.prototype._createLink = function(link)
{
  var editor = this;
  var outparam = null;
  if ( typeof link == "undefined" )
  {
    link = this.getParentElement();
    if ( link )
    {
      while (link && !/^a$/i.test(link.tagName))
      {
        link = link.parentNode;
      }
    }
  }
  if ( !link )
  {
    var sel = editor.getSelection();
    var range = editor.createRange(sel);
    var compare = 0;
    if ( Xinha.is_ie )
    {
      if ( sel.type == "Control" )
      {
        compare = range.length;
      }
      else
      {
        compare = range.compareEndPoints("StartToEnd", range);
      }
    }
    else
    {
      compare = range.compareBoundaryPoints(range.START_TO_END, range);
    }
    if ( compare === 0 )
    {
      alert(Xinha._lc("You need to select some text before creating a link"));
      return;
    }
    outparam =
    {
      '<%= EditLink.Parameter.HREF %>'       : '',
      '<%= EditLink.Parameter.TITLE %>'      : '',
      '<%= EditLink.Parameter.TARGET %>'     : '',
      '<%= EditLink.Parameter.USE_TARGET %>' : editor.config.makeLinkShowsTarget
    };
  }
  else
  {
    outparam =
    {
      '<%= EditLink.Parameter.HREF %>'       : Xinha.is_ie ? editor.stripBaseURL(link.href) : link.getAttribute("href"),
      '<%= EditLink.Parameter.TITLE %>'      : link.title,
      '<%= EditLink.Parameter.TARGET %>'     : link.target,
      '<%= EditLink.Parameter.USE_TARGET %>' : editor.config.makeLinkShowsTarget
    };
  }
  var queryString = '';
  for ( var i in outparam )
  {
      if (outparam[i]) {
          queryString += '&' + i + '=' + encodeURIComponent(outparam[i]);
      }
  }
  editor.config.URIs.link = '<%= EditLink.linkTo(request, "/imcms/xinha/plugins/ImcmsIntegration/return_link.jsp") %>'+queryString ;
  this._popupDialog(
    editor.config.URIs.link,
    function(param)
    {
      if ( !param )
      {
        return false;
      }
      var a = link;
      if ( !a )
      {
        try
        {
          var tmp = Xinha.uniq('http://www.example.com/Link');
          editor._doc.execCommand('createlink', false, tmp);

          // Fix them up
          var anchors = editor._doc.getElementsByTagName('a');
          for(var i = 0; i < anchors.length; i++)
          {
            var anchor = anchors[i];
            if(anchor.href == tmp)
            {
              // Found one.
              if (!a) a = anchor;
              anchor.href =  param.href;
              if (param.target) anchor.target = param.target;
              if (param.title)  anchor.title  = param.title;
            }
          }
        } catch(ex) {}
      }
      else
      {
        var href = param.href.trim();
        editor.selectNodeContents(a);
        if ( href == '' )
        {
          editor._doc.execCommand("unlink", false, null);
          editor.updateToolbar();
          return false;
        }
        else
        {
          a.href = href;
        }
      }
      if ( ! ( a && a.tagName.toLowerCase() == 'a' ) )
      {
        return false;
      }
      a.target = param.target.trim();
      a.title = param.title.trim();
      editor.selectNodeContents(a);
      editor.updateToolbar();
    },
    outparam);
};
