package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.ImageHistoryJPA;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class ImageHistoryDTO extends ImageHistoryJPA {
}
