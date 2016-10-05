package com.imcode.imcms.servlet.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.mapping.TextDocumentContentSaver;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.mapping.container.TextDocImageContainer;
import com.imcode.imcms.mapping.container.TextDocImagesContainer;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.ConcurrentDocumentModificationException;
import imcode.server.document.DocumentDomainObject;
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

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Map;
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

    private String getPreviewLink(String url, String width, String height) {
        return new StringBuilder("/servlet/ImagePreview?")
                .append("path=")
                .append(Utility.encodeUrl(url))

                .append("&width=")
                .append(width)
                .append("&height=")
                .append(height)

                .append("&format=")
                .append(Format.PNG.name().toLowerCase())
                .append("&resize=")
                .append(Resize.FORCE.name().toLowerCase())
                .toString();
    }

    @RequestMapping("/**/{name}-{width}-{height}.{extension}")
    public String getImage(HttpServletRequest request,
                           @PathVariable("name") String name,
                           @PathVariable("width") String width,
                           @PathVariable("height") String height,
                           @PathVariable("extension") String extension) {
        AbstractFileSource[] urls = fileController.read(request, extension, name);

        return (urls.length > 0)
                ? getPreviewLink(urls[0].getUrlPathRelativeToContextPath(), width, height)
                : "";
    }

    @RequestMapping("/**/{name}.{extension}")
    public ImageSource getImage(HttpServletRequest request,
                                @PathVariable("name") String name,
                                @PathVariable("extension") String extension) {
        AbstractFileSource[] urls = fileController.read(request, extension, name);
        return (ImageSource) urls[0];
    }

    @RequestMapping(value = "/{docId}-{id}", method = RequestMethod.GET)
    public ImageDomainObject getImage(@PathVariable("id") Integer id,
                                      @PathVariable("docId") Integer docId,
                                      @RequestParam(value = "loopId", required = false) Integer loopId,
                                      @RequestParam(value = "entryId", required = false) Integer entryId,
                                      @RequestParam(value = "langCode", required = false) String langCode,
                                      HttpServletRequest request) {

        TextDocumentDomainObject textDocument = getDocWithLanguageAndVersion(docId, langCode, request);

        return (loopId != null && entryId != null)
                ? textDocument.getImage(TextDocumentDomainObject.LoopItemRef.of(loopId, entryId, id))
                : textDocument.getImage(id);
    }

    @RequestMapping(value = "/**/{docId}-{id}", method = RequestMethod.POST)
    public boolean updateImage(
            @PathVariable("id") Integer id,
            @PathVariable("docId") Integer docId,
            @RequestParam(value = "loopId", required = false) Integer loopId,
            @RequestParam(value = "entryId", required = false) Integer loopRefId,
            @RequestParam(value = "sharedMode", required = false, defaultValue = "false") boolean sharedMode,
            @RequestParam(value = "langCode", required = false) String langCode,
            @RequestParam("imageDomainObject") ImageDomainObject imageDomainObject,
            HttpServletRequest request) throws DocumentSaveException {

        final DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        TextDocumentDomainObject textDocument = getDocWithLanguageAndVersion(docId, langCode, request);

        LoopEntryRef entryRef = (loopId != null && loopRefId != null)
                ? LoopEntryRef.of(loopId, loopRefId)
                : null;

        if (StringUtils.isNotBlank(imageDomainObject.getGeneratedFilename())) {
            imageDomainObject.getGeneratedFile().delete();
        }

        imageDomainObject.generateFilename();
        ImcmsImageUtils.generateImage(imageDomainObject, false);

        try {
            if (sharedMode) {
                final Map<DocumentLanguage, ImageDomainObject> langToImage = Imcms.getServices()
                        .getDocumentLanguages()
                        .getAll()
                        .stream()
                        .collect(Collectors.toMap((val) -> val, (val) -> imageDomainObject));

                final TextDocImagesContainer textDocImagesContainer = TextDocImagesContainer
                        .of(textDocument.getVersionRef(), entryRef, id, langToImage);

                documentMapper.saveTextDocImages(textDocImagesContainer, Imcms.getUser());

            } else {
                final TextDocImageContainer textDocImageContainer = TextDocImageContainer
                        .of(textDocument.getRef(), entryRef, id, imageDomainObject);

                documentMapper.saveTextDocImage(textDocImageContainer, Imcms.getUser());
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

    @RequestMapping(value = "/{docId}-{id}", method = RequestMethod.DELETE)
    public boolean deleteImage(@PathVariable("id") Integer id,
                               @PathVariable("docId") Integer docId,
                               @RequestParam(value = "loopId", required = false) Integer loopId,
                               @RequestParam(value = "entryId", required = false) Integer entryId,
                               @RequestParam(value = "langCode", required = false) String langCode,
                               HttpServletRequest request) {

        TextDocumentDomainObject textDocument = getDocWithLanguageAndVersion(docId, langCode, request);

//        Required to delete generated image later
        ImageDomainObject imageDomainObject;

        if (loopId != null && entryId != null) {
            imageDomainObject = textDocument.getImage(TextDocumentDomainObject.LoopItemRef.of(loopId, entryId, id));
            textDocument.deleteImage(TextDocumentDomainObject.LoopItemRef.of(loopId, entryId, id));
        } else {
            imageDomainObject = textDocument.getImage(id);
            textDocument.deleteImage(id);
        }

//        Removing previously generated file
        if (StringUtils.isNotBlank(imageDomainObject.getGeneratedFilename())) {
            imageDomainObject.getGeneratedFile().delete();
        }

        Imcms.getServices().getManagedBean(TextDocumentContentSaver.class).updateContent(textDocument, Imcms.getUser());

        return true;
    }

    private <T extends DocumentDomainObject> T getDocWithLanguageAndVersion(int docId,
                                                                            String langCode,
                                                                            ServletRequest request) {
        final ImcmsServices services = Imcms.getServices();
        final boolean containsLang = services.getDocumentLanguages()
                .getAll()
                .stream()
                .anyMatch(e -> langCode.equals(e.getCode()));

        return (containsLang)
                ? services.getDocumentMapper().getVersionedDocument(docId, langCode, request)
                : services.getDocumentMapper().getVersionedDocument(docId, request);
    }
}
