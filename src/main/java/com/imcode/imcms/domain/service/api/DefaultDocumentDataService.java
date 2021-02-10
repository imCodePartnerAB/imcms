package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.service.*;
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
    private final Function<ImageJPA, ImageDTO> imageJPAToImageDTO;
    private final Function<Menu, MenuDTO> menuToMenuDTO;

    public DefaultDocumentDataService(TextService textService, ImageService imageService,
                                      MenuService menuService, LoopService loopService,
                                      Function<ImageJPA, ImageDTO> imageJPAToImageDTO,
                                      Function<Menu, MenuDTO> menuToMenuDTO) {
        this.textService = textService;
        this.imageService = imageService;
        this.menuService = menuService;
        this.loopService = loopService;
        this.imageJPAToImageDTO = imageJPAToImageDTO;
        this.menuToMenuDTO = menuToMenuDTO;
    }

    @Override
    public DocumentDataDTO getDataByDocId(Integer id) {

        List<TextDTO> textDTOList = textService.getByDocId(id).stream().map(TextDTO::new).collect(Collectors.toList());
        List<ImageDTO> imageDTOList = imageService.getByDocId(id).stream().map(imageJPAToImageDTO).collect(Collectors.toList());
        List<MenuDTO> menuDTOList = menuService.getByDocId(id).stream().map(menuToMenuDTO).collect(Collectors.toList());
        Set<LoopDTO> loopDTOList = loopService.getByDocId(id).stream().map(LoopDTO::new).collect(Collectors.toSet());

        return new DocumentDataDTO(textDTOList, imageDTOList, menuDTOList, loopDTOList);
    }

}
