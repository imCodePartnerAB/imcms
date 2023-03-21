package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImportDocumentDTO {
	private Integer id;
	private String type;
	private String defaultLanguage;
	private String headline;
	private String menuText;
	private String menuImage;
	private String alias;
	private boolean searchDisabled;
	private boolean linkableForUnauthorizedUsers;
	private boolean linkableByOtherUsers;
	private String target;
	private String creator;
	private String publisher;
	private Date createdAt;
	private Date changedAt;
	private Date archivedAt;
	private Date expiresAt;
	private Date publishedAt;

	private String url;
	private String template;

	private List<ImportTextDTO> texts = new ArrayList<>();
	private List<ImportImageDTO> images = new ArrayList<>();
	private List<ImportMenuDTO> menus = new ArrayList<>();
	private List<ImportFileDTO> files = new ArrayList<>();
	private List<ImportCategoryDTO> categories = new ArrayList<>();
	private List<ImportRoleDTO> roles = new ArrayList<>();
	private Set<String> keywords = new HashSet<>();
	private List<ImportPropertyDTO> properties = new ArrayList<>();

}
