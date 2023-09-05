package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDataDTO {

    private List<TextDTO> textsDTO;
    private List<ImageDTO> imagesDTO;
    private List<MenuDTO> menusDTO;
    private LoopDataDTO loopDataDTO;
    private Set<CategoryDTO> categoriesDTO;
}
