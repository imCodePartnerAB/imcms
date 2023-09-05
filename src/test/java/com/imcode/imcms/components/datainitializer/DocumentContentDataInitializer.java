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
        return createData(version, List.of(language));
    }

    public DocumentDataDTO createData(Version version, List<Language> languages) {
        final int indexLoopWithContent = 1;

        final LoopDTO loopDTO = new LoopDTO(version.getDocId(), indexLoopWithContent, Collections.emptyList());
        final LoopDTO loopDTO2 = new LoopDTO(version.getDocId(), 2, Collections.emptyList());
        loopDataInitializer.createData(loopDTO, version.getNo());
        loopDataInitializer.createData(loopDTO2, version.getNo());

        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(indexLoopWithContent, 1);

        final List<TextDTO> textsDTOList = new ArrayList<>();
        final List<TextDTO> textsDTOLoopList = new ArrayList<>();

        final List<ImageDTO> imagesDTOList = new ArrayList<>();
        final List<ImageDTO> imagesDTOLoopList = new ArrayList<>();

        for(Language language: languages){
            textsDTOList.add(new TextDTO(textDataInitializer.createText(1, new LanguageJPA(language), version, "test Text" + language.getCode())));
            textsDTOList.add(new TextDTO(textDataInitializer.createText(2, new LanguageJPA(language), version, "test Text2" + language.getCode())));

            textsDTOLoopList.add(new TextDTO(textDataInitializer.createText(3, new LanguageJPA(language), version, "test Text3 in Loop" + language.getCode(), loopEntryRef)));

            imagesDTOList.add(imageJPAToImageDTO.apply(imageDataInitializer.generateImage(1, new LanguageJPA(language), version, null)));
            imagesDTOList.add(imageJPAToImageDTO.apply(imageDataInitializer.generateImage(2, new LanguageJPA(language), version, null)));

            imagesDTOLoopList.add(imageJPAToImageDTO.apply(imageDataInitializer.generateImage(3, new LanguageJPA(language), version, loopEntryRef)));
        }

        final List<MenuDTO> menusDTOList = Collections.singletonList(menuDataInitializer.createData(true, 1, version, null, 3));
        final Set<LoopDTO> loopsDTOList = Set.of(loopDTO, loopDTO2);
        final LoopDataDTO loopDataDTO = new LoopDataDTO(loopsDTOList, textsDTOLoopList, imagesDTOLoopList);

        return new DocumentDataDTO(textsDTOList, imagesDTOList, menusDTOList, loopDataDTO, Collections.emptySet());
    }

    public DocumentDataDTO createData() {
        Imcms.setLanguage(languageDataInitializer.createData().get(0));

        return new DocumentDataDTO(Collections.emptyList(), Collections.emptyList(),
                            Collections.emptyList(),
                            new LoopDataDTO(Collections.emptySet(), Collections.emptyList(), Collections.emptyList()),
                            Collections.emptySet());
    }

}
