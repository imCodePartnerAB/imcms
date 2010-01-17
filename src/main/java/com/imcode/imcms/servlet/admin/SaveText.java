package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.ConcurrentDocumentModificationException;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.ShouldNotBeThrownException;
import imcode.util.Utility;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;

/**
 * Saves edited text. 
 */
public final class SaveText extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws IOException {
        req.setCharacterEncoding( Imcms.DEFAULT_ENCODING );
        Utility.setDefaultHtmlContentType( res );

        // Check if user has permission to be here
        ImcmsServices imcref = Imcms.getServices();
        int meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );
        UserDomainObject user = Utility.getLoggedOnUser( req );
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        //TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument( meta_id, user.getDocumentShowSettings().getVersionSelector() );
        TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument(meta_id);
        TextDocumentPermissionSetDomainObject permissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor( document );
        I18nLanguage language = Imcms.getRequestInfo().getLanguage();

        if (permissionSet.getEditTexts() && req.getParameter( "cancel" ) == null ) {
            int txt_no = Integer.parseInt( req.getParameter( "txt_no" ) );
            String text_string = req.getParameter( "text" );
            int text_format = Integer.parseInt( req.getParameter( "format_type" ) );

            String[] formatParameterValues = req.getParameterValues("format") ;
            String rowsParameterValue = (null != formatParameterValues) ? req.getParameter("rows") : null ;
            String formatRowsQueryString = "" ;
            if (null != formatParameterValues) {
                for (String formatParameter : formatParameterValues) {
                    formatRowsQueryString += "&format=" + formatParameter ;
                }
                
                if (null != rowsParameterValue) {
                    formatRowsQueryString += "&rows=" + rowsParameterValue ;
                }
            }

            String label = StringUtils.defaultString(req.getParameter("label")) ;


            String loopNoStr = req.getParameter("loop_no");
            String contentIndexStr = req.getParameter("content_index");

            Integer loopNo = loopNoStr == null ? null : Integer.valueOf(loopNoStr);
            Integer contentIndex = contentIndexStr == null ? null : Integer.valueOf(contentIndexStr);            

            //TextDomainObject text = loopNo == null
            //       ? document.getText(language, txt_no)
            //        : document.getText(language, loopNo, contentIndex, txt_no);

            TextDomainObject text = loopNo == null
                    ? document.getText(txt_no)
                    : document.getText(loopNo, contentIndex, txt_no);            

            text = text == null ? new TextDomainObject() : text.clone();

    		text.setDocId(document.getId());
    		text.setNo(txt_no);
            text.setDocVersionNo(document.getVersion().getNo());
    		text.setLanguage(Imcms.getRequestInfo().getLanguage());
            text.setText(text_string);
            text.setType(text_format);
            text.setContentLoopNo(loopNo);
            text.setContentIndex(contentIndex);

            saveText( documentMapper, text, document, txt_no, imcref, meta_id, user );

            if (null != req.getParameter( "save" )) {
                String url = "ChangeText?meta_id="+meta_id+"&txt="+txt_no + formatRowsQueryString + "&label=" + label;

                if (loopNo != null) {
                    url += "&loop_no="+loopNo+"&content_index="+contentIndex;
                }

                res.sendRedirect(url);
                return ;
            }
        }

        res.sendRedirect( "AdminDoc?meta_id=" + meta_id + "&flags="
                          + imcode.server.ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS );

    }

    private void saveText(DocumentMapper documentMapper, TextDomainObject text, TextDocumentDomainObject document,
                          final int txt_no, final ImcmsServices imcref, int meta_id,
                          final UserDomainObject user) {
        try {
        	documentMapper.saveText(document, text, user);        
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
