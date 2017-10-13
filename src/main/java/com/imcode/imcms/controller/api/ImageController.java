package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.service.api.ImageService;
import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageController {

    public static final Logger LOG = Logger.getLogger(ImageController.class);

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public ImageDTO getImage(@ModelAttribute ImageDTO imageDTO) {
        return imageService.getImage(imageDTO);
    }

    @PostMapping
    public void saveImage(@RequestBody ImageDTO image) throws IllegalAccessException {

        // todo: create annotation instead of copying this each time!
        if (!Imcms.getUser().isSuperAdmin()) {
            throw new IllegalAccessException("User do not have access to change image structure.");
        }

        imageService.saveImage(image);
    }

    // fixme: moved from another class, should not be used at all!!!1
    private void writeJSON(Object object, HttpServletResponse response) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        MediaType jsonMimeType = MediaType.parseMediaType("application/json");

        if (jsonConverter.canWrite(object.getClass(), jsonMimeType)) {
            try {
                jsonConverter.write(object, jsonMimeType, new ServletServerHttpResponse(response));
            } catch (IOException e) {
                LOG.fatal(e.getMessage(), e);
            }
        }
    }

    private boolean isIndexOccupied(TextDocumentDomainObject document, Integer result) {
        return (document.getImage(result).getGeneratedFilename() != null);
    }

    /**
     * Returns empty upper or lower image index.
     * For example, if we have images in document under indexes -3, -1, 1, 2, 10,
     * calling this method for lower indexes we will get -2, for upper it will be 3.
     *
     * @param docId     interested document id
     * @param direction String parameter "UPPER" or "LOWER"
     */
    // todo: update to work with new client API
    @RequestMapping("/emptyNo/{docId}/{upperOrLower}")
    public void getFreeImageNo(@PathVariable("docId") int docId,
                               @PathVariable("upperOrLower") Direction direction,
                               HttpServletResponse response) {

        final TextDocumentDomainObject document = Imcms.getServices()
                .getDocumentMapper()
                .getWorkingDocument(docId);

        Integer result = null;

        if (document != null) {

            switch (direction) {
                case LOWER: {
                    result = -1;

                    while ((result > Integer.MIN_VALUE) && (isIndexOccupied(document, result))) {
                        result--;
                    }
                    break;
                }

                case UPPER: {
                    result = 1;

                    while ((result < Integer.MAX_VALUE) && (isIndexOccupied(document, result))) {
                        result++;
                    }
                    break;
                }
            }
        }

        if ((result != null) && (result.equals(Integer.MAX_VALUE) || result.equals(Integer.MIN_VALUE))) {
            result = null;
        }

        writeJSON(result, response);
    }

    private enum Direction {
        UPPER, LOWER
    }
}
