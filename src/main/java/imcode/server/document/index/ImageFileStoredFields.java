package imcode.server.document.index;

import com.imcode.imcms.domain.dto.ExifDTO;
import imcode.util.Utility;
import org.apache.solr.common.SolrDocument;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ImageFileStoredFields {

	private final SolrDocument solrDocument;

	public ImageFileStoredFields(SolrDocument solrDocument) {
		this.solrDocument = solrDocument;
	}

	public String id() {
		return solrDocument.getFieldValue(ImageFileIndex.FIELD__ID).toString();
	}

	public String name(){
		return solrDocument.getFieldValue(ImageFileIndex.FIELD__NAME).toString();
	}

	public String path(){
		return solrDocument.getFieldValue(ImageFileIndex.FIELD__PATH).toString();
	}

	public Date uploaded(){
		return (Date) solrDocument.getFieldValue(ImageFileIndex.FIELD__UPLOADED);
	}

	public String size(){
		return solrDocument.getFieldValue(ImageFileIndex.FIELD__SIZE).toString();
	}

	public Integer width(){
		return (Integer) solrDocument.getFieldValue(ImageFileIndex.FIELD__WIDTH);
	}

	public Integer height(){
		return (Integer) solrDocument.getFieldValue(ImageFileIndex.FIELD__HEIGHT);
	}

	public String photographer() {
		return (String) solrDocument.getFieldValue(ImageFileIndex.FIELD__PHOTOGRAPHER);
	}

	public String uploadedBy() {
		return (String) solrDocument.getFieldValue(ImageFileIndex.FIELD__UPLOADED_BY);
	}

	public String copyright() {
		return (String) solrDocument.getFieldValue(ImageFileIndex.FIELD__COPYRIGHT);
	}

	public LocalDate licensePeriodStart() {
		return Utility.convertDateToLocalDate(
				(Date) solrDocument.getFieldValue(ImageFileIndex.LICENSE_PERIOD_START)
		);
	}

	public LocalDate licensePeriodEnd() {
		return Utility.convertDateToLocalDate(
				(Date) solrDocument.getFieldValue(ImageFileIndex.LICENSE_PERIOD_END)
		);
	}

	public String altText() {
		return (String) solrDocument.getFieldValue(ImageFileIndex.ALT_TEXT);
	}

	public String descriptionText() {
		return (String) solrDocument.getFieldValue(ImageFileIndex.DESCRIPTION_TEXT);
	}

	public ExifDTO.CustomExifDTO customExif() {
		final ExifDTO.CustomExifDTO customExifDTO = new ExifDTO.CustomExifDTO();

		customExifDTO.setPhotographer(photographer());
		customExifDTO.setUploadedBy(uploadedBy());
		customExifDTO.setCopyright(copyright());
		customExifDTO.setLicensePeriodStart(licensePeriodStart());
		customExifDTO.setLicensePeriodEnd(licensePeriodEnd());
		customExifDTO.setAlternateText(altText());
		customExifDTO.setDescriptionText(descriptionText());

		return customExifDTO;
	}

	public List<String> allExif() {
		return Optional.ofNullable(solrDocument.getFieldValues(ImageFileIndex.FIELD__ALL_EXIF))
				.map(values -> values.stream()
						.map(String::valueOf)
						.collect(Collectors.toList()))
				.orElseGet(List::of);
	}

	public ExifDTO exifInfo() {
		final ExifDTO exifDTO = new ExifDTO();
		exifDTO.setAllExifInfo(allExif());
		exifDTO.setCustomExif(customExif());

		return exifDTO;
	}
}
