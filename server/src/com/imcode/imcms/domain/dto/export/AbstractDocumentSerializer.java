package com.imcode.imcms.domain.dto.export;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.imcode.imcms.mapping.CategoryMapper;
import imcode.server.Imcms;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.RoleGetter;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public abstract class AbstractDocumentSerializer<T extends DocumentDomainObject> extends StdSerializer<T> {
	protected final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601);

	public AbstractDocumentSerializer() {
		this(null);
	}

	protected AbstractDocumentSerializer(Class<T> t) {
		super(t);
	}

	protected abstract void serializeContent(T value, JsonGenerator gen, SerializerProvider provider) throws IOException;

	protected void writeNullable(String name, String value, JsonGenerator gen) throws IOException {
		if (StringUtils.isNotBlank(value)) {
			gen.writeStringField(name, value);
		}
	}

	@Override
	public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		final ImcmsAuthenticatorAndUserAndRoleMapper userMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();

		gen.writeStartObject();
		gen.writeNumberField("id", value.getId());
		gen.writeStringField("type", value.getDocumentType().getName().toLocalizedString("eng"));
		gen.writeStringField("default_language", Imcms.getDefaultLanguage());
		writeNullable("headline", value.getHeadline(), gen);
		writeNullable("menu_text", value.getMenuText(), gen);
		writeNullable("menu_image", value.getMenuImage(), gen);
		writeNullable("alias", value.getAlias(), gen);
		gen.writeBooleanField("search_disabled", value.isSearchDisabled());
		gen.writeBooleanField("linkable_for_unauthorized_users", value.isLinkedForUnauthorizedUsers());
		gen.writeBooleanField("linkable_by_other_users", value.isLinkableByOtherUsers());
		gen.writeStringField("target", value.getTarget());

		gen.writeStringField("creator", Utility.formatUser(userMapper.getUser(value.getCreatorId())));

		final Integer publisherId = value.getPublisherId();
		if (publisherId != null)
			gen.writeStringField("publisher", Utility.formatUser(userMapper.getUser(publisherId)));

		final Date createdAt = value.getCreatedDatetime();
		if (createdAt != null)
			gen.writeStringField("created_at", dateTimeFormat.format(createdAt));

		final Date changedAt = value.getModifiedDatetime();
		if (changedAt != null)
			gen.writeStringField("changed_at", dateTimeFormat.format(changedAt));

		final Date archivedAt = value.getArchivedDatetime();
		if (archivedAt != null)
			gen.writeStringField("archived_at", dateTimeFormat.format(archivedAt));

		final Date expireAt = value.getPublicationEndDatetime();
		if (expireAt != null)
			gen.writeStringField("expires_at", dateTimeFormat.format(expireAt));

		final Date publishedAt = value.getPublicationStartDatetime();
		if (publishedAt != null)
			gen.writeStringField("published_at", dateTimeFormat.format(publishedAt));

		writeCategories(value, gen, provider);
		writeKeywords(value, gen, provider);
		writeRolePermissions(value, gen, provider);
		writeProperties(value, gen, provider);

		serializeContent(value, gen, provider);

		gen.writeEndObject();
	}

	private void writeCategories(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		final Set<Integer> categoryIds = value.getCategoryIds();

		if (categoryIds.isEmpty()) return;

		final CategoryMapper categoryMapper = Imcms.getServices().getCategoryMapper();
		gen.writeArrayFieldStart("categories");

		for (Integer categoryId : categoryIds) {
			final CategoryDomainObject category = categoryMapper.getCategoryById(categoryId);
			gen.writeStartObject();

			gen.writeStringField("name", category.getName());
			writeNullable("description", category.getDescription(), gen);
			writeNullable("image_url", category.getImageUrl(), gen);

			final CategoryTypeDomainObject type = category.getType();
			gen.writeObjectFieldStart("category_type");

			gen.writeStringField("name", type.getName());
			gen.writeBooleanField("multiselect", type.getMaxChoices() != 0);
			gen.writeBooleanField("inherited", type.isInherited());

			gen.writeEndObject();

			gen.writeEndObject();
		}
		gen.writeEndArray();
	}

	private void writeKeywords(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		final Set<String> keywords = value.getKeywords();

		if (keywords.isEmpty()) return;

		gen.writeArrayFieldStart("keywords");
		for (String keyword : keywords) {
			gen.writeString(keyword);
		}
		gen.writeEndArray();
	}

	private void writeRolePermissions(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		final RoleIdToDocumentPermissionSetTypeMappings r2pMappings = value.getRoleIdsMappedToDocumentPermissionSetTypes();
		if (r2pMappings == null || r2pMappings.getMappings().length == 0) {
			return;
		}

		final RoleGetter roleGetter = Imcms.getServices().getRoleGetter();
		gen.writeArrayFieldStart("roles");

		for (RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping : r2pMappings.getMappings()) {
			final RoleDomainObject role = roleGetter.getRole(mapping.getRoleId());
			gen.writeStartObject();

			gen.writeStringField("name", role.getName());
			gen.writeStringField("permission", mapping.getDocumentPermissionSetType().name());

			gen.writeEndObject();
		}

		gen.writeEndArray();
	}

	private void writeProperties(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		final Map<String, String> properties = value.getProperties();

		if (properties.isEmpty()) return;

		gen.writeArrayFieldStart("properties");

		for (Map.Entry<String, String> entry : properties.entrySet()) {
			gen.writeStartObject();

			gen.writeStringField("key", entry.getKey());
			gen.writeStringField("value", entry.getValue());

			gen.writeEndObject();
		}

		gen.writeEndArray();
	}
}
