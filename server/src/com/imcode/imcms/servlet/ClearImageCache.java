package com.imcode.imcms.servlet;

import com.imcode.imcms.api.ContentManagementSystem;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

public class ClearImageCache extends HttpServlet {
    private static final long serialVersionUID = -1300585302771072394L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        
        if (!cms.getCurrentUser().isSuperAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            
            return;
        }
        
        boolean all = BooleanUtils.toBoolean(request.getParameter("all"));
        
        Integer metaId = null;
        Integer no = null;
        String fileNo = null;
        
        try {
            metaId = Integer.parseInt(StringUtils.trimToNull(request.getParameter("meta_id")));
        } catch (NumberFormatException ex) {
        }
        
        try {
            no = Integer.parseInt(StringUtils.trimToNull(request.getParameter("no")));
        } catch (NumberFormatException ex) {
        }
        
        try {
            fileNo = StringUtils.trimToNull(request.getParameter("file_no"));
        } catch (NumberFormatException ex) {
        }
        
        
        String message;
        
        if (!all && metaId == null) {
            message = "Invalid parameters";
            
        } else if (all && metaId != null) {
            message = "Specify one of: all or meta_id";
            
        } else if (all) {
            message = "Deleted all";
            
            ImageCacheManager.clearAllCacheEntries();
        } else {
            message = "Deleted meta_id=" + metaId;
            
            if (no != null) {
                message += ", " + "no=" + no;
                ImageCacheManager.clearCacheEntries(metaId, no);
                
            } else if (fileNo != null) {
                message += ", " + "file_no=" + fileNo;
                ImageCacheManager.clearCacheEntries(metaId, fileNo);
                
            } else {
                ImageCacheManager.clearCacheEntries(metaId);
                
            }
        }
        
        byte[] data = message.getBytes("UTF-8");
        
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.setContentLength(data.length);
        
        OutputStream output = null;
        try {
            output = response.getOutputStream();
            
            output.write(data);
            output.flush();
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
}
