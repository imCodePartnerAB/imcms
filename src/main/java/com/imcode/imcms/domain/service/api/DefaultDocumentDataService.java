package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.Menu;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
class DefaultDocumentDataService implements DocumentDataService {

    private final TextService textService;
    private final ImageService imageService;
    private final MenuService menuService;
    private final LoopService loopService;
    private final DelegatingByTypeDocumentService documentService;
    private final LanguageService languageService;
    private final Function<ImageJPA, ImageDTO> imageJPAToImageDTO;
    private final Function<Menu, MenuDTO> menuToMenuDTO;

    public DefaultDocumentDataService(TextService textService, ImageService imageService,
                                      MenuService menuService, LoopService loopService,
                                      DelegatingByTypeDocumentService documentService,
                                      LanguageService languageService,
                                      Function<ImageJPA, ImageDTO> imageJPAToImageDTO, Function<Menu, MenuDTO> menuToMenuDTO) {
        this.textService = textService;
        this.imageService = imageService;
        this.menuService = menuService;
        this.loopService = loopService;
        this.documentService = documentService;
        this.languageService = languageService;
        this.imageJPAToImageDTO = imageJPAToImageDTO;
        this.menuToMenuDTO = menuToMenuDTO;
    }

    @Override
    public DocumentDataDTO getDataByDocIdAndAvailableLangs(Integer id) {
        final Set<String> availableLanguageCodes = languageService.getAvailableLanguages().stream()
                .map(Language::getCode)
                .collect(Collectors.toSet());

        List<TextDTO> textDTOList = textService.getByDocId(id).stream()
                .filter(textJPA -> availableLanguageCodes.contains(textJPA.getLangCode()))
                .map(TextDTO::new)
                .collect(Collectors.toList());
        List<TextDTO> textsDTOInLoop = textDTOList.stream()
                .filter(textJPA -> textJPA.getLoopEntryRef() != null)
                .collect(Collectors.toList());
        textDTOList.removeAll(textsDTOInLoop);

        List<ImageDTO> imageDTOList = imageService.getByDocId(id).stream()
                .filter(imageJPA -> availableLanguageCodes.contains(imageJPA.getLanguage().getCode()))
                .map(imageJPAToImageDTO)
                .collect(Collectors.toList());
        List<ImageDTO> imagesDTOInLoop = imageDTOList.stream()
                .filter(imageJPA -> imageJPA.getLoopEntryRef() != null)
                .collect(Collectors.toList());
        imageDTOList.removeAll(imagesDTOInLoop);

        Set<LoopDTO> loopDTOList = loopService.getByDocId(id).stream().map(LoopDTO::new).collect(Collectors.toSet());
        LoopDataDTO loopDataDTO = new LoopDataDTO(loopDTOList, textsDTOInLoop, imagesDTOInLoop);

        List<MenuDTO> menuDTOList = menuService.getByDocId(id).stream().map(menuToMenuDTO).collect(Collectors.toList());
        Set<CategoryDTO> categoriesDTO = documentService.get(id).getCategories().stream().map(CategoryDTO::new).collect(Collectors.toSet());

        return new DocumentDataDTO(textDTOList, imageDTOList, menuDTOList, loopDataDTO, categoriesDTO);
    }

}
