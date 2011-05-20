package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.ConcurrentDocumentModificationException;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.Utility;
import imcode.util.ShouldNotBeThrownException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public final class SaveText extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws IOException {
        req.setCharacterEncoding( Imcms.DEFAULT_ENCODING );
        Utility.setDefaultHtmlContentType( res );

        // Check if user has permission to be here
        ImcmsServices imcref = Imcms.getServices();
        int meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );
        UserDomainObject user = Utility.getLoggedOnUser( req );
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument( meta_id );

        TextDocumentPermissionSetDomainObject permissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor( document );
        String returnURL = req.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL);

        if ( permissionSet.getEditTexts()
             && req.getParameter( "cancel" ) == null ) {
            // get text_no
            int txt_no = Integer.parseInt( req.getParameter( "txt_no" ) );

            // get textdocument
            String text_string = req.getParameter( "text" );
            text_string = text_string
							.replace(StringEscapeUtils.escapeHtml("<?imcms:contextpath?>"), "<?imcms:contextpath?>")
							.replace(URLEncoder.encode("<?imcms:contextpath?>", Imcms.ISO_8859_1_ENCODING), "<?imcms:contextpath?>")
							.replace(URLEncoder.encode("<?imcms:contextpath?>", Imcms.UTF_8_ENCODING), "<?imcms:contextpath?>")
							.replace("%3C?imcms:contextpath?%3E", "<?imcms:contextpath?>") ;

            int text_format = Integer.parseInt( req.getParameter( "format_type" ) );

            TextDomainObject text = new TextDomainObject( text_string, text_format );

            saveText( documentMapper, text, document, txt_no, imcref, meta_id, user );
	          
						String label = StringUtils.defaultString(req.getParameter("label")) ;
						String[] formats = req.getParameterValues("format") ;
						String rows = StringUtils.defaultString(req.getParameter("rows")) ;
						String width = StringUtils.defaultString(req.getParameter("width")) ;
	        
						String redirPath = "ChangeText?meta_id="+meta_id+"&txt="+txt_no ;
	          
	          if (!"".equals(label)) {
		          redirPath += "&label=" + URLEncoder.encode(label, Imcms.UTF_8_ENCODING) ;
	          }
	          if (null != formats && formats.length > 0) {
		          for (String format : formats) {
			          redirPath += "&format=" + URLEncoder.encode(format, Imcms.UTF_8_ENCODING) ;
		          }
	          }
	          if (!"".equals(rows)) {
		          redirPath += "&rows=" + URLEncoder.encode(rows, Imcms.UTF_8_ENCODING) ;
	          }
	          if (!"".equals(width)) {
		          redirPath += "&width=" + URLEncoder.encode(width, Imcms.UTF_8_ENCODING) ;
	          }

            if (returnURL != null) {
                redirPath += "&" + ImcmsConstants.REQUEST_PARAM__RETURN_URL + "=" + returnURL;
            }

            if (null != req.getParameter( "save" )) {
                res.sendRedirect( redirPath );
                return ;
            }
        }

        if (returnURL != null) {
            res.sendRedirect(returnURL);
        } else {
            res.sendRedirect( "AdminDoc?meta_id=" + meta_id + "&flags="
                              + imcode.server.ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS );
        }

    }

    private void saveText(DocumentMapper documentMapper, TextDomainObject text, TextDocumentDomainObject document,
                          int txt_no, ImcmsServices imcref, int meta_id,
                          UserDomainObject user) {
        document.setText( txt_no, text );
        document.addModifiedTextIndex(txt_no, true);

        try {
            documentMapper.saveDocument( document, user );
        } catch ( NoPermissionToEditDocumentException e ) {
            throw new ShouldHaveCheckedPermissionsEarlierException(e);
        } catch ( NoPermissionToAddDocumentToMenuException e ) {
            throw new ConcurrentDocumentModificationException(e);
        } catch (DocumentSaveException e) {
            throw new ShouldNotBeThrownException(e);
        }

        imcref.updateMainLog( "Text " + txt_no + " in [" + meta_id + "] modified by user: [" + user.getFullName()
                              + "]" );
    }
}
