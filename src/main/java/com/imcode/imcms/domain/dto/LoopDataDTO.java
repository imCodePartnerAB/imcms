package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class LoopDataDTO {
    private static final long serialVersionUID = 2674993018443685749L;

    private List<TextDTO> textsDTO;
    private List<ImageDTO> imagesDTO;

}
