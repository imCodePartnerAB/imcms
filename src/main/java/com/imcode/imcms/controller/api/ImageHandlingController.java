package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import imcode.util.ImcmsImageUtils;
import imcode.util.image.Resize;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for generation images.
 */
@Controller
@RequestMapping("/imagehandling")
public class ImageHandlingController {

    /**
     * Controller for generation images.
     */
    @GetMapping
    @ResponseBody
    public ResponseEntity<byte[]> getResizedImage(@RequestParam String path,
                                                  @RequestParam(required = false) Integer width,
                                                  @RequestParam(required = false) Integer height) {

        final ImageDTO imageDTO = new ImageDTO();
        imageDTO.setSource(ImcmsImageUtils.getImageSource(path));

        imageDTO.setCompress(false);

        width = width != null ? width : 0;
        height = height != null ? height : 0;
        imageDTO.setWidth(width);
        imageDTO.setHeight(height);

        Resize resize = width == 0 || height == 0 ? Resize.DEFAULT : Resize.FORCE;
        imageDTO.setResize(resize);

        byte[] generatedImage = ImcmsImageUtils.generateImage(imageDTO);

        if(generatedImage == null || generatedImage.length == 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(new Tika().detect(path)))
                .body(generatedImage);
    }

}
