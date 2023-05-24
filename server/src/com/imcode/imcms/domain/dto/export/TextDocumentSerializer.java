package com.imcode.imcms.domain.dto.export;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import imcode.server.document.textdocument.*;
import imcode.util.image.Format;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TextDocumentSerializer extends AbstractDocumentSerializer<TextDocumentDomainObject> {

	@Override
	protected void serializeContent(TextDocumentDomainObject value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStringField("template", value.getTemplateName());
		writeTexts(value, gen, provider);
		writeImages(value, gen, provider);
		writeMenus(value, gen, provider);
	}

	private void writeTexts(TextDocumentDomainObject value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		final Map<Integer, TextDomainObject> texts = value.getTexts();

		if (texts.isEmpty()) return;

		gen.writeArrayFieldStart("texts");
		for (Map.Entry<Integer, TextDomainObject> textEntry : texts.entrySet()) {
			gen.writeStartObject();

			gen.writeNumberField("index", textEntry.getKey());
			gen.writeStringField("type", TextDomainObject.TEXT_TYPE_PLAIN == textEntry.getValue().getType() ? "TEXT" : "HTML");
			gen.writeStringField("text", textEntry.getValue().getText());

			gen.writeEndObject();
		}
		gen.writeEndArray();
	}

	private void writeImages(TextDocumentDomainObject value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		final Map<Integer, ImageDomainObject> images = value.getImages();

		if (images.isEmpty()) return;

		gen.writeArrayFieldStart("images");

		final Map<Integer, ImageDomainObject> map = images;
//				.entrySet()
//				.stream()
//				.filter(entry -> !entry.getValue().getSource().getClass().isInstance(NullImageSource.class))
//				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		for (Map.Entry<Integer, ImageDomainObject> imageEntry : map.entrySet()) {
			final ImageDomainObject image = imageEntry.getValue();
			gen.writeStartObject();

			gen.writeNumberField("index", imageEntry.getKey());
			writeNullable("name", image.getName(), gen);
			writeNullable("generated_filename", image.getGeneratedFilename(), gen);
			gen.writeNumberField("border", image.getBorder());
			writeNullable("format", Optional.ofNullable(image.getFormat()).map(Format::getFormat).orElse(null), gen);
			gen.writeStringField("align", image.getAlign());
			gen.writeStringField("target", image.getTarget());
			writeNullable("path", image.getUrlPath(""), gen);
			gen.writeNumberField("width", image.getWidth());
			gen.writeNumberField("height", image.getHeight());
			gen.writeNumberField("horizontal_space", image.getHorizontalSpace());
			gen.writeNumberField("angle", image.getRotateDirection().getAngle());
//			gen.writeNumberField("", image.getSource().getName());
//			gen.writeNumberField("", image.getRotateDirection().getLeftDirection().getAngle());
//			gen.writeNumberField("", image.getRotateDirection().getRightDirection().getAngle());
			writeNullable("alt_text", image.getAlternateText(), gen);
//			writeNullable("archive_image_id", image.getArchiveImageId(), null, gen);
			gen.writeObjectField("archive_image_id", image.getArchiveImageId());
			writeNullable("link_url", image.getLinkUrl(), gen);
			writeNullable("low_resolution_url", image.getLowResolutionUrl(), gen);

			gen.writeEndObject();
		}
		gen.writeEndArray();
	}

	private void writeMenus(TextDocumentDomainObject value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		final Map<Integer, MenuDomainObject> menus = value.getMenus();

		if (menus.isEmpty()) return;

		gen.writeArrayFieldStart("menus");
		for (Map.Entry<Integer, MenuDomainObject> menuEntry : menus.entrySet()) {
			final MenuDomainObject menu = menuEntry.getValue();
			gen.writeStartObject();

			gen.writeNumberField("index", menuEntry.getKey());

			final int sortOrderInt = menu.getSortOrder();
			gen.writeStringField("type_sort", getTypeOrderName(sortOrderInt));
			gen.writeArrayFieldStart("menu_items");

			final MenuItemDomainObject[] menuItems = menu.getMenuItems();
			final Map<Integer, String> sortOrderMap = getSortOrder(sortOrderInt, menuItems);

			for (MenuItemDomainObject menuItem : menuItems) {
				gen.writeStartObject();

				final int documentId = menuItem.getDocumentId();

				gen.writeNumberField("document_id", documentId);
				gen.writeStringField("sort_order", sortOrderMap.get(documentId));

				gen.writeEndObject();
			}

			gen.writeEndArray();
			gen.writeEndObject();
		}
		gen.writeEndArray();
	}

	private String getTypeOrderName(int menuTypeOrder) {
		switch (menuTypeOrder) {
			case 2:
				return "MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED";
			case 3:
				return "MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED";
			case 4:
				return "MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER";
			case 5:
				return "MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED";
			default:
				return "MENU_SORT_ORDER__BY_HEADLINE";
		}
	}

	private Map<Integer, String> getSortOrder(int menuTypeOrder, MenuItemDomainObject[] menuItems) {
		final HashMap<Integer, String> documentIdToSortOrderMap = new HashMap<>();

		for (int i = 0; i < menuItems.length; i++) {
			final MenuItemDomainObject menuItem = menuItems[i];
			final int documentId = menuItem.getDocumentId();

			String sortOrder;
			if (menuTypeOrder == 2) {
				sortOrder = String.valueOf(menuItem.getSortKey() == null ? i : menuItem.getSortKey());
			} else if (menuTypeOrder == 4) {
				sortOrder = StringUtils.defaultIfBlank(menuItem.getTreeSortKey().toString(), String.valueOf(i));
			} else {
				sortOrder = String.valueOf(i);

			}

			documentIdToSortOrderMap.put(documentId, sortOrder);
		}

		return documentIdToSortOrderMap;
	}
}
