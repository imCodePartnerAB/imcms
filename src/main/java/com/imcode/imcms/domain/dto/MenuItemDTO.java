package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.Meta;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Data
public class MenuItemDTO implements Serializable {

    private static final long serialVersionUID = 8297109006105427219L;

    private String title;

    private Meta.DocumentType type;

    private Integer documentId;

    private String target;

    private String link;

    private DocumentStatus documentStatus;

    private Date publishedDate;

    private Date modifiedDate;

    private boolean hasNewerVersion;

    private Integer dataLevel;

    private Integer dataIndex;

    private List<MenuItemDTO> children = new ArrayList<>();

    public Stream<MenuItemDTO> flattened() {
        return Stream.concat(
                Stream.of(this),
                children.stream().flatMap(MenuItemDTO::flattened));
    }

}
