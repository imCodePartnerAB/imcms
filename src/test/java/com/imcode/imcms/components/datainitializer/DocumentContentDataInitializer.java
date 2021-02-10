package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Function;

@Component
public class DocumentContentDataInitializer extends TestDataCleaner{

    private final TextDataInitializer textDataInitializer;
    private final ImageDataInitializer imageDataInitializer;
    private final MenuDataInitializer menuDataInitializer;
    private final LoopDataInitializer loopDataInitializer;
    private final LanguageDataInitializer languageDataInitializer;
    private final Function<ImageJPA, ImageDTO> imageJPAToImageDTO;

    public DocumentContentDataInitializer(TextDataInitializer textDataInitializer,
                                          ImageDataInitializer imageDataInitializer,
                                          MenuDataInitializer menuDataInitializer,
                                          LoopDataInitializer loopDataInitializer,
                                          LanguageDataInitializer languageDataInitializer, Function<ImageJPA, ImageDTO> imageJPAToImageDTO) {
        this.textDataInitializer = textDataInitializer;
        this.imageDataInitializer = imageDataInitializer;
        this.menuDataInitializer = menuDataInitializer;
        this.loopDataInitializer = loopDataInitializer;
        this.languageDataInitializer = languageDataInitializer;
        this.imageJPAToImageDTO = imageJPAToImageDTO;
    }

    public DocumentDataDTO createData(Version version) {
        Imcms.setLanguage(languageDataInitializer.createData().get(0));

        TextDTO textDTO = new TextDTO(textDataInitializer.createText(1, version, "test Text"));
        TextDTO textDTO2 = new TextDTO(textDataInitializer.createText(2, version, "test Text2"));
        ImageDTO imageDTO = imageJPAToImageDTO.apply(imageDataInitializer.createData(1, version));
        ImageDTO imageDTO2 = imageJPAToImageDTO.apply(imageDataInitializer.createData(2, version));
        MenuDTO menuDTO = menuDataInitializer.createData(true,1, version, null, 3);
        LoopDTO loopDTO = new LoopDTO(version.getDocId(), 1, Collections.emptyList());
        LoopDTO loopDTO2 = new LoopDTO(version.getDocId(), 2, Collections.emptyList());
        loopDataInitializer.createData(loopDTO, version.getNo());
        loopDataInitializer.createData(loopDTO2, version.getNo());

        return new DocumentDataDTO(Arrays.asList(textDTO, textDTO2), Arrays.asList(imageDTO, imageDTO2),
                                    Collections.singletonList(menuDTO), new HashSet(Arrays.asList(loopDTO, loopDTO2)));
        }

    public DocumentDataDTO createData() {
        Imcms.setLanguage(languageDataInitializer.createData().get(0));

        return new DocumentDataDTO(Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptySet());
    }

}
