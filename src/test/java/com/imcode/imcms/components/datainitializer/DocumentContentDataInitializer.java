package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import org.springframework.stereotype.Component;

import java.util.*;
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
                                          LanguageDataInitializer languageDataInitializer,
                                          Function<ImageJPA, ImageDTO> imageJPAToImageDTO) {
        this.textDataInitializer = textDataInitializer;
        this.imageDataInitializer = imageDataInitializer;
        this.menuDataInitializer = menuDataInitializer;
        this.loopDataInitializer = loopDataInitializer;
        this.languageDataInitializer = languageDataInitializer;
        this.imageJPAToImageDTO = imageJPAToImageDTO;
    }

    public DocumentDataDTO createData(Version version) {
        Language language = languageDataInitializer.createData().get(0);
        Imcms.setLanguage(language);

        final int indexLoopWithContent = 1;

        final LoopDTO loopDTO = new LoopDTO(version.getDocId(), indexLoopWithContent, Collections.emptyList());
        final LoopDTO loopDTO2 = new LoopDTO(version.getDocId(), 2, Collections.emptyList());
        loopDataInitializer.createData(loopDTO, version.getNo());
        loopDataInitializer.createData(loopDTO2, version.getNo());

        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(indexLoopWithContent, 1);

        final TextDTO textDTO = new TextDTO(textDataInitializer.createText(1, version, "test Text"));
        final TextDTO textDTO2 = new TextDTO(textDataInitializer.createText(2, version, "test Text2"));

        final TextDTO textInLoop = new TextDTO(textDataInitializer.createText(3, new LanguageJPA(language), version, "test Text3 in Loop", loopEntryRef));

        final ImageDTO imageDTO = imageJPAToImageDTO.apply(imageDataInitializer.createData(1, version));
        final ImageDTO imageDTO2 = imageJPAToImageDTO.apply(imageDataInitializer.createData(2, version));

        final ImageDTO imageInLoop = imageJPAToImageDTO.apply(imageDataInitializer.generateImage(3, new LanguageJPA(language), version, loopEntryRef));

        MenuDTO menuDTO = menuDataInitializer.createData(true,1, version, null, 3);

        List<TextDTO> textsDTOList = Arrays.asList(textDTO, textDTO2);
        List<ImageDTO> imagesDTOList = Arrays.asList(imageDTO, imageDTO2);
        List<MenuDTO> menusDTOList = Collections.singletonList(menuDTO);
        Set<LoopDTO> loopsDTOList = new HashSet(Arrays.asList(loopDTO, loopDTO2));
        LoopDataDTO loopDataDTO = new LoopDataDTO(Collections.singletonList(textInLoop), Collections.singletonList(imageInLoop));

        return new DocumentDataDTO(textsDTOList, imagesDTOList, menusDTOList, loopsDTOList, loopDataDTO, Collections.emptySet());
    }

    public DocumentDataDTO createData() {
        Imcms.setLanguage(languageDataInitializer.createData().get(0));

        return new DocumentDataDTO(Collections.emptyList(), Collections.emptyList(),
                            Collections.emptyList(), Collections.emptySet(),
                            new LoopDataDTO(Collections.emptyList(), Collections.emptyList()),
                            Collections.emptySet());
    }

}
