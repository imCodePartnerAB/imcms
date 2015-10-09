package com.imcode.imcms.servlet.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.mapping.container.TextDocImageContainer;
import com.imcode.imcms.mapping.container.TextDocImagesContainer;
import imcode.server.Imcms;
import imcode.server.document.ConcurrentDocumentModificationException;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.textdocument.*;
import imcode.util.ImcmsImageUtils;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.ShouldNotBeThrownException;
import imcode.util.Utility;
import imcode.util.image.Format;
import imcode.util.image.ImageInfo;
import imcode.util.image.Resize;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Provide access to processing images
 */
@RestController
@RequestMapping("/content/image")
public class ImageController {

    @Autowired
    private FolderController folderController;

    @Autowired
    private FileController fileController;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(ImageDomainObject.CropRegion.class, new PropertyEditorSupport() {
            ImageDomainObject.CropRegion region;

            @Override
            public Object getValue() {
                return region;
            }

            @Override
            public void setAsText(String text) {
                try {
                    region = new ObjectMapper().readValue(text, ImageDomainObject.CropRegion.class);
                    region.updateValid();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        dataBinder.registerCustomEditor(ImageInfo.class, new PropertyEditorSupport() {
            Object value;

            @Override
            public Object getValue() {
                return value;
            }

            @Override
            public void setAsText(String text) {
                try {
                    value = new ObjectMapper().readValue(text, ImageInfo.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        dataBinder.registerCustomEditor(ImageDomainObject.class, new PropertyEditorSupport() {
            ImageDomainObject value;

            @Override
            public Object getValue() {
                return value;
            }

            @Override
            public void setAsText(String text) {
                try {
                    value = new ObjectMapper().readValue(text, ImageDomainObject.class);
                    value.getCropRegion().updateValid();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    String getPreviewLink(String url,
                          String width,
                          String height) {

        StringBuilder builder = new StringBuilder("/servlet/ImagePreview?");

        builder.append("path=");
        builder.append(Utility.encodeUrl(url));

        builder.append("&width=");
        builder.append(width);
        builder.append("&height=");
        builder.append(height);

        builder.append("&format=");
        builder.append(Format.PNG.name().toLowerCase());

        /*ImageDomainObject.CropRegion region = image.getCropRegion();
        if (region.isValid()) {
            builder.append("&crop_x1=");
            builder.append(region.getCropX1());
            builder.append("&crop_y1=");
            builder.append(region.getCropY1());
            builder.append("&crop_x2=");
            builder.append(region.getCropX2());
            builder.append("&crop_y2=");
            builder.append(region.getCropY2());
        }

        builder.append("&rangle=");
        builder.append(image.getRotateDirection().getAngle());

       /* if (!forPreview && image.getGeneratedFilename() != null) {
            builder.append("&gen_file=");
            builder.append(image.getGeneratedFilename());
        }*/

        builder.append("&resize=");
        builder.append(Resize.FORCE.name().toLowerCase());

        return builder.toString();

    }

    @RequestMapping("/**/{name}-{width}-{height}.{extension}")
    public String getImage(
            HttpServletRequest request,
            @PathVariable("name") String name,
            @PathVariable("width") String width,
            @PathVariable("height") String height,
            @PathVariable("extension") String extension) {
        AbstractFileSource[] urls = fileController.read(request, extension, name);
        if (urls.length > 0) {
            return getPreviewLink(urls[0].getUrlPathRelativeToContextPath(), width, height);
        }
        return "";
    }

    @RequestMapping("/**/{name}.{extension}")
    public ImageSource getImage(
            HttpServletRequest request,
            @PathVariable("name") String name,
            @PathVariable("extension") String extension) {
        AbstractFileSource[] urls = fileController.read(request, extension, name);
        return (ImageSource) urls[0];
    }

    @RequestMapping(value = "/{docId}-{id}", method = RequestMethod.GET)
    public ImageDomainObject getImage(
            @PathVariable("id") Integer id, @PathVariable("docId") Integer docId,
            @RequestParam(value = "loopId", required = false) Integer loopId,
            @RequestParam(value = "entryId", required = false) Integer entryId) {
        TextDocumentDomainObject textDocument = Imcms.getServices().getDocumentMapper().getDocument(docId);

        if (loopId != null && entryId != null) {
            return textDocument.getImage(TextDocumentDomainObject.LoopItemRef.of(loopId, entryId, id));
        }

        return textDocument.getImage(id);
    }

    @RequestMapping(value = "/**/{docId}-{id}", method = RequestMethod.POST)
    public boolean updateImage(
            @PathVariable("id") Integer id, @PathVariable("docId") Integer docId,
            @RequestParam(value = "loopId", required = false) Integer loopId,
            @RequestParam(value = "entryId", required = false) Integer loopRefId,
            @RequestParam(value = "sharedMode", required = false, defaultValue = "false") boolean sharedMode,
            @RequestParam("imageDomainObject") ImageDomainObject imageDomainObject) throws DocumentSaveException {
        TextDocumentDomainObject textDocument = Imcms.getServices().getDocumentMapper().getDocument(docId);
        LoopEntryRef entryRef = loopId != null && loopRefId != null ?
                LoopEntryRef.of(loopId, loopRefId) : null;


        if (!StringUtils.isBlank(imageDomainObject.getGeneratedFilename())) {
            imageDomainObject.getGeneratedFile().delete();
        }

        imageDomainObject.generateFilename();
        ImcmsImageUtils.generateImage(imageDomainObject, false);

        try {
            if (sharedMode) {
                Imcms.getServices()
                        .getDocumentMapper()
                        .saveTextDocImages(
                                TextDocImagesContainer.of(textDocument.getVersionRef(), entryRef, id, Imcms.getServices().getDocumentLanguages()
                                        .getAll()
                                        .stream()
                                        .collect(Collectors.toMap((val) -> val, (val) -> imageDomainObject))),
                                Imcms.getUser()
                        );
            } else {
                Imcms.getServices()
                        .getDocumentMapper()
                        .saveTextDocImage(
                                TextDocImageContainer.of(textDocument.getRef(), entryRef, id, imageDomainObject),
                                Imcms.getUser()
                        );
            }
        } catch (NoPermissionToEditDocumentException e) {
            throw new ShouldHaveCheckedPermissionsEarlierException(e);
        } catch (NoPermissionToAddDocumentToMenuException e) {
            throw new ConcurrentDocumentModificationException(e);
        } catch (DocumentSaveException e) {
            throw new ShouldNotBeThrownException(e);
        }
        return true;
    }
}
