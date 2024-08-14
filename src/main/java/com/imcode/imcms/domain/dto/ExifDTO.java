package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import imcode.util.DateConstants;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ExifDTO {

    private CustomExifDTO customExif;
    private List<String> allExifInfo;

    @Data
    @NoArgsConstructor
    public static class CustomExifDTO{
        public enum Key {
            PHOTOGRAPHER,
            UPLOADED_BY,
            COPYRIGHT,
            LICENSE_PERIOD_START,
            LICENSE_PERIOD_END,
            ALT_TEXT,
            DESCRIPTION_TEXT;
        }

        private String photographer;
        private String uploadedBy;
        private String copyright;
        @JsonFormat(pattern = DateConstants.DATE_FORMAT_STRING)
        private LocalDate licensePeriodStart;
        @JsonFormat(pattern = DateConstants.DATE_FORMAT_STRING)
        private LocalDate licensePeriodEnd;
        private String alternateText;
        private String descriptionText;

        public static String mapToString(CustomExifDTO customExif){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_STRING);

            List<String> keyValuePairs = new ArrayList<>();
            if(StringUtils.isNotBlank(customExif.getPhotographer()))
                keyValuePairs.add(Key.PHOTOGRAPHER.name() + "/=" + customExif.getPhotographer());
            if(StringUtils.isNotBlank(customExif.getCopyright()))
                keyValuePairs.add(Key.COPYRIGHT.name() + "/=" + customExif.getCopyright());
            if(StringUtils.isNotBlank(customExif.getUploadedBy()))
                keyValuePairs.add(Key.UPLOADED_BY.name() + "/=" + customExif.getUploadedBy());
            if(customExif.getLicensePeriodStart() != null)
                keyValuePairs.add(Key.LICENSE_PERIOD_START.name() + "/=" + customExif.getLicensePeriodStart().format(formatter));
            if(customExif.getLicensePeriodEnd() != null)
                keyValuePairs.add(Key.LICENSE_PERIOD_END.name() + "/=" + customExif.getLicensePeriodEnd().format(formatter));
            if (customExif.getAlternateText()!=null)
                keyValuePairs.add(Key.ALT_TEXT.name()+"/="+customExif.getAlternateText());
	        if (customExif.getDescriptionText()!=null)
		        keyValuePairs.add(Key.DESCRIPTION_TEXT.name() + "/=" + customExif.getDescriptionText());

            return String.join("/;", keyValuePairs);
        }

        public static CustomExifDTO mapToCustomExif(String string) throws DateTimeParseException, IllegalArgumentException {
            CustomExifDTO customExif = new CustomExifDTO();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_STRING);

            final String[] keyValuePairs = string.split("/;");
            for (String keyValuePair: keyValuePairs){
                String[] keyAndValue = keyValuePair.split("/=");

                if(keyAndValue.length != 2) continue;

                String key = keyAndValue[0];
                String value = keyAndValue[1];

                switch (Key.valueOf(key)) {
                    case PHOTOGRAPHER -> customExif.setPhotographer(value);
                    case COPYRIGHT -> customExif.setCopyright(value);
                    case UPLOADED_BY -> customExif.setUploadedBy(value);
                    case LICENSE_PERIOD_START -> customExif.setLicensePeriodStart(LocalDate.parse(value, formatter));
                    case LICENSE_PERIOD_END -> customExif.setLicensePeriodEnd(LocalDate.parse(value, formatter));
	                case ALT_TEXT -> customExif.setAlternateText(value);
	                case DESCRIPTION_TEXT -> customExif.setDescriptionText(value);
				}
            }

            return customExif;
        }

    }

}
