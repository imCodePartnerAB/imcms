package com.imcode.imcms.addon.imagearchive.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.service.file.ThumbSize;
import com.imcode.imcms.addon.imagearchive.util.Utils;

@Controller
public class ThumbnailController {
    @Autowired
    private Facade facade;
    
    
    @RequestMapping("/archive/thumb")
    public String thumbHandler(
            @RequestParam(required=false) Long id, 
            @RequestParam(required=false) String size, 
            @RequestParam(required=false) Boolean tmp, 
            HttpServletResponse response) {
        size = StringUtils.trimToNull(size);
        ThumbSize thumbSize = null;
        
        if (id == null || size == null || (thumbSize = ThumbSize.findByName(size)) == null) {
            Utils.sendErrorCode(response, HttpServletResponse.SC_NOT_FOUND);
            
            return null;
        }
        
        tmp = (tmp != null ? tmp.booleanValue() : false);
        
        InputStream inputStream = null;
        try {
            Object[] pair = facade.getFileService().getImageThumbnail(id, thumbSize, tmp);
            if (pair == null) {
                Utils.sendErrorCode(response, HttpServletResponse.SC_NOT_FOUND);
                
                return null;
            }
            
            long length = (Long) pair[0];
            inputStream = (InputStream) pair[1];
            
            response.setContentType("image/jpeg");
            response.setContentLength((int) length);
            response.setHeader("Content-Disposition", String.format("inline; file=thumb_small_%d.jpg", id));
            
            OutputStream output = null;
            try {
                output = new BufferedOutputStream(response.getOutputStream());
                IOUtils.copy(inputStream, output);
                output.flush();
            } catch (IOException ex) {
            } finally {
                IOUtils.closeQuietly(output);
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        
        return null;
    }
    
    @RequestMapping("/archive/preview")
    public String previewHandler(
            @RequestParam(required=false) Long id, 
            @RequestParam(required=false) Boolean tmp, 
            HttpServletResponse response) {
        if (id == null) {
            Utils.sendErrorCode(response, HttpServletResponse.SC_NOT_FOUND);
            
            return null;
        }
        
        tmp = (tmp != null ? tmp.booleanValue() : false);
        
        InputStream inputStream = null;
        try {
            Object[] pair = facade.getFileService().getImageFull(id, tmp);
            if (pair == null) {
                Utils.sendErrorCode(response, HttpServletResponse.SC_NOT_FOUND);
                
                return null;
            }
            
            long length = (Long) pair[0];
            inputStream = (InputStream) pair[1];
            
            response.setContentType("image/jpeg");
            response.setContentLength((int) length);
            response.setHeader("Content-Disposition", String.format("inline; file=preview_%d.jpg", id));
            
            OutputStream output = null;
            try {
                output = new BufferedOutputStream(response.getOutputStream());
                IOUtils.copy(inputStream, output);
                output.flush();
            } catch (Exception ex) {
            } finally {
                IOUtils.closeQuietly(output);
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        
        return null;
    }
}
