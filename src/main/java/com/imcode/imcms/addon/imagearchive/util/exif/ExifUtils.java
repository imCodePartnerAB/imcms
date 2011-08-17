package com.imcode.imcms.addon.imagearchive.util.exif;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.common.RationalNumber;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.apache.sanselan.formats.tiff.constants.TiffFieldTypeConstants;
import org.apache.sanselan.formats.tiff.fieldtypes.FieldType;
import org.apache.sanselan.formats.tiff.write.TiffOutputField;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

public class ExifUtils {
    /* spec. used: http://www.exif.org/Exif2-2.PDF */
    public static final SimpleDateFormat tiffExifDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    private static final Log log = LogFactory.getLog(ExifUtils.class);
    
    private static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("&#[0-9a-z]+;");
    
    public static ExifData getExifData(File file) {
        try {
            ImageFormat imageFormat = Sanselan.guessFormat(file);
            if (imageFormat.equals(ImageFormat.IMAGE_FORMAT_UNKNOWN)) {
                return null;
            }
            
            IImageMetadata metadata = Sanselan.getMetadata(file);
            
            String imageDescription;
            String userComment = null;
            String copyright;
            String artist;
            RationalNumber xResolution;
            RationalNumber yResolution;
            String manufacturer;
            String model;
            String compression;
            Integer resolutionUnit;
            RationalNumber exposure;
            String exposureProgram;
            RationalNumber fStop;
            String dateDigitized;
            String dateOriginal;
            Flash flash;
            RationalNumber focalLength;
            String colorSpace;
            Integer pixelXDimension;
            Integer pixelYDimension;
            Integer ISO;
            
            if (metadata instanceof TiffImageMetadata) {
                TiffImageMetadata tiffMetadata = (TiffImageMetadata) metadata;
                
                imageDescription = getTiffTagValue(TiffConstants.TIFF_TAG_IMAGE_DESCRIPTION, tiffMetadata);
                copyright = getTiffTagValue(TiffConstants.TIFF_TAG_COPYRIGHT, tiffMetadata);
                artist = getTiffTagValue(TiffConstants.TIFF_TAG_ARTIST, tiffMetadata);
                xResolution = getTiffTagValue(TiffConstants.TIFF_TAG_XRESOLUTION, tiffMetadata);
                yResolution = getTiffTagValue(TiffConstants.TIFF_TAG_YRESOLUTION, tiffMetadata);
                manufacturer = getTiffTagValue(TiffConstants.TIFF_TAG_MAKE, tiffMetadata);
                model = getTiffTagValue(TiffConstants.TIFF_TAG_MODEL, tiffMetadata);
                compression = getTiffCompressionName((Integer) getTiffTagValue(TiffConstants.TIFF_TAG_COMPRESSION, tiffMetadata));
                resolutionUnit = getTiffTagValue(TiffConstants.TIFF_TAG_RESOLUTION_UNIT, tiffMetadata);
                exposure = getTiffTagValue(TiffConstants.EXIF_TAG_EXPOSURE_TIME, tiffMetadata);
                exposureProgram = getTiffExposureProgram((Integer) getTiffTagValue(TiffConstants.EXIF_TAG_EXPOSURE_PROGRAM, tiffMetadata));
                fStop = getTiffTagValue(TiffConstants.EXIF_TAG_FNUMBER, tiffMetadata);
                dateDigitized = getTiffTagValue(TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL, tiffMetadata);
                dateOriginal = getTiffTagValue(TiffConstants.EXIF_TAG_CREATE_DATE, tiffMetadata);
                flash = getTiffFlash((Integer) getTiffTagValue(TiffConstants.EXIF_TAG_FLASH, tiffMetadata));
                focalLength = getTiffTagValue(TiffConstants.EXIF_TAG_FOCAL_LENGTH, tiffMetadata);
                colorSpace = getTiffColorSpace((Integer) getTiffTagValue(TiffConstants.EXIF_TAG_COLOR_SPACE, tiffMetadata));
                pixelXDimension = getTiffTagValue(TiffConstants.EXIF_TAG_EXIF_IMAGE_WIDTH, tiffMetadata);
                pixelYDimension = getTiffTagValue(TiffConstants.EXIF_TAG_EXIF_IMAGE_LENGTH, tiffMetadata);
                ISO = getTiffTagValue(TiffConstants.EXIF_TAG_ISO, tiffMetadata);
            } else if (metadata instanceof JpegImageMetadata) {
                JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
                
                imageDescription = getExifTagValue(ExifTagConstants.EXIF_TAG_IMAGE_DESCRIPTION, jpegMetadata);
                userComment = getExifTagValue(ExifTagConstants.EXIF_TAG_USER_COMMENT, jpegMetadata);
                copyright = getExifTagValue(ExifTagConstants.EXIF_TAG_COPYRIGHT, jpegMetadata);
                artist = getExifTagValue(ExifTagConstants.EXIF_TAG_ARTIST, jpegMetadata);
                manufacturer = getExifTagValue(ExifTagConstants.EXIF_TAG_MAKE, jpegMetadata);
                model = getExifTagValue(ExifTagConstants.EXIF_TAG_MODEL, jpegMetadata);
                compression = getExifCompressionName((Integer) getExifTagValue(ExifTagConstants.EXIF_TAG_COMPRESSION, jpegMetadata));
                xResolution = getExifTagValue(ExifTagConstants.EXIF_TAG_XRESOLUTION, jpegMetadata);
                yResolution = getExifTagValue(ExifTagConstants.EXIF_TAG_YRESOLUTION, jpegMetadata);
                resolutionUnit = getExifTagValue(ExifTagConstants.EXIF_TAG_RESOLUTION_UNIT, jpegMetadata);
                exposure = getExifTagValue(ExifTagConstants.EXIF_TAG_EXPOSURE_TIME, jpegMetadata);
                exposureProgram = getExifExposureProgram((Integer) getExifTagValue(ExifTagConstants.EXIF_TAG_EXPOSURE_PROGRAM, jpegMetadata));
                fStop = getExifTagValue(ExifTagConstants.EXIF_TAG_FNUMBER, jpegMetadata);
                dateDigitized = getExifTagValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, jpegMetadata);
                dateOriginal = getExifTagValue(ExifTagConstants.EXIF_TAG_CREATE_DATE, jpegMetadata);
                flash = getExifFlash((Integer) getExifTagValue(ExifTagConstants.EXIF_TAG_FLASH, jpegMetadata));
                focalLength = getExifTagValue(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH, jpegMetadata);
                colorSpace = getExifColorSpace((Integer) getExifTagValue(ExifTagConstants.EXIF_TAG_COLOR_SPACE, jpegMetadata));
                pixelXDimension = getExifTagValue(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH, jpegMetadata);
                pixelYDimension = getExifTagValue(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH, jpegMetadata);
                ISO = getExifTagValue(ExifTagConstants.EXIF_TAG_ISO, jpegMetadata);
            } else {
                return null;
            }
            
            ExifData data = new ExifData();
            if (imageDescription != null || userComment != null) {
                if (imageDescription != null && userComment != null) {
                    imageDescription = imageDescription.trim();
                    userComment = userComment.trim();
                    
                    String value = userComment.length() > imageDescription.length() ? userComment : imageDescription;
                    if (containsHtmlEntities(value)) {
                    	value = StringEscapeUtils.unescapeHtml(value);
                    }
                    
                    data.setDescription(value);
                } else if (imageDescription != null) {
                	imageDescription = imageDescription.trim();
                	
                	if (containsHtmlEntities(imageDescription)) {
                		imageDescription = StringEscapeUtils.unescapeHtml(imageDescription);
                	}
                	
                    data.setDescription(imageDescription);
                } else {
                	userComment = userComment.trim();
                	
                	if (containsHtmlEntities(userComment)) {
                		userComment = StringEscapeUtils.unescapeHtml(userComment);
                	}
                	
                    data.setDescription(userComment);
                }
            }
            if (copyright != null) {
            	copyright = copyright.trim();
            	
            	if (containsHtmlEntities(copyright)) {
            		copyright = StringEscapeUtils.unescapeHtml(copyright);
            	}
            	
                data.setCopyright(copyright);
            }
            if (artist != null) {
            	artist = artist.trim();
            	
            	if (containsHtmlEntities(artist)) {
            		artist = StringEscapeUtils.unescapeHtml(artist);
            	}
            	
                data.setArtist(artist);
            }
            if (xResolution != null && xResolution.intValue() > 0) {
                data.setxResolution(xResolution.intValue());
            }

            if (yResolution != null && yResolution.intValue() > 0) {
                data.setyResolution(xResolution.intValue());
            }

            if(manufacturer != null) {
                manufacturer = manufacturer.trim();

                if(containsHtmlEntities(manufacturer)) {
                    manufacturer = StringEscapeUtils.unescapeHtml(manufacturer);
                }

                data.setManufacturer(manufacturer);
            }

            if(model != null) {
                model = model.trim();

                if(containsHtmlEntities(model)) {
                    model = StringEscapeUtils.unescapeHtml(model);
                }

                data.setModel(model);
            }

            data.setCompression(compression);
            data.setResolutionUnit(resolutionUnit);
            data.setExposure(exposure);
            data.setExposureProgram(exposureProgram);
            data.setfStop(fStop);

            if(dateDigitized != null) {
                Date tmpDate = null;
                try{
                    tmpDate = tiffExifDateFormat.parse(dateDigitized);
                }catch(ParseException e){}
                
                data.setDateDigitized(tmpDate);
            }

            if(dateOriginal != null) {
                Date tmpDate = null;
                try{
                    tmpDate = tiffExifDateFormat.parse(dateOriginal);
                }catch(ParseException e){}

                data.setDateOriginal(tmpDate);
            }

            data.setFlash(flash);
            data.setFocalLength(focalLength);
            data.setColorSpace(colorSpace);
            data.setPixelXDimension(pixelXDimension);
            data.setPixelYDimension(pixelYDimension);
            data.setISO(ISO);
            
            return data;
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return null;
    }


    /* methods for getting human-readable exif values */
    // for tiff
    public static String getTiffCompressionName(Integer compressionValue) {
        if(compressionValue == null) {
            return null;
        }

        String compressionName = null;
        switch (compressionValue) {
           case TiffConstants.COMPRESSION_VALUE_CCITT_1D:
               compressionName = "CCITT 1D";
               break;
           case TiffConstants.COMPRESSION_VALUE_T4_GROUP_3_FAX:
               compressionName = "T4 GROUP 3 FAX";
               break;
           case TiffConstants.COMPRESSION_VALUE_T6_GROUP_4_FAX:
               compressionName = "T6 GROUP 4 FAX";
               break;
           case TiffConstants.COMPRESSION_VALUE_LZW:
               compressionName = "LZW";
               break;
           case TiffConstants.COMPRESSION_VALUE_JPEG_OLD_STYLE:
               compressionName = "JPEG OLD STYLE";
               break;
           case TiffConstants.COMPRESSION_VALUE_JPEG:
               compressionName = "JPEG";
               break;
           case TiffConstants.COMPRESSION_VALUE_ADOBE_DEFLATE:
               compressionName = "ADOBE DEFLATE";
               break;
           case TiffConstants.COMPRESSION_VALUE_JBIG_B_AND_W:
               compressionName = "JBIG B AND W";
               break;
           case TiffConstants.COMPRESSION_VALUE_JBIG_COLOR:
               compressionName = "JBIG COLOR";
               break;
           case TiffConstants.COMPRESSION_VALUE_NEXT:
               compressionName = "NEXT";
               break;
           case TiffConstants.COMPRESSION_VALUE_EPSON_ERF_COMPRESSED:
               compressionName = "EPSON ERF COMPRESSED";
               break;
           case TiffConstants.COMPRESSION_VALUE_CCIRLEW:
               compressionName = "CCIRLEW";
               break;
           case TiffConstants.COMPRESSION_VALUE_PACK_BITS:
               compressionName = "PACK BITS";
               break;
           case TiffConstants.COMPRESSION_VALUE_IT8CTPAD:
               compressionName = "IT8CTPAD";
               break;
           case TiffConstants.COMPRESSION_VALUE_IT8LW:
               compressionName = "IT8LW";
               break;
           case TiffConstants.COMPRESSION_VALUE_IT8MP:
               compressionName = "IT8MP";
               break;
           case TiffConstants.COMPRESSION_VALUE_IT8BL:
               compressionName = "IT8BL";
               break;
           case TiffConstants.COMPRESSION_VALUE_PIXAR_FILM:
               compressionName = "PIXAR FILM";
               break;
           case TiffConstants.COMPRESSION_VALUE_PIXAR_LOG:
               compressionName = "PIXAR LOG";
               break;
           case TiffConstants.COMPRESSION_VALUE_DEFLATE:
               compressionName = "DEFLATE";
               break;
           case TiffConstants.COMPRESSION_VALUE_DCS:
               compressionName = "DCS";
               break;
           case TiffConstants.COMPRESSION_VALUE_JBIG:
               compressionName = "JBIG";
               break;
           case TiffConstants.COMPRESSION_VALUE_SGILOG:
               compressionName = "SGILOG";
               break;
           case TiffConstants.COMPRESSION_VALUE_SGILOG_24:
               compressionName = "SGILOG 24";
               break;
           case TiffConstants.COMPRESSION_VALUE_JPEG_2000:
               compressionName = "JPEG 2000";
               break;
           case TiffConstants.COMPRESSION_VALUE_NIKON_NEF_COMPRESSED:
               compressionName = "NIKON NEF COMPRESSED";
               break;
           case TiffConstants.COMPRESSION_VALUE_KODAK_DCR_COMPRESSED:
               compressionName = "KODAK DCR COMPRESSED";
               break;
           case TiffConstants.COMPRESSION_VALUE_PENTAX_PEF_COMPRESSED:
               compressionName = "PENTAX PEF COMPRESSED";
               break;
           case TiffConstants.COMPRESSION_VALUE_THUNDERSCAN:
               compressionName = "THUNDERSCAN";
               break;
        }

        return compressionName;
   }

    public static Flash getTiffFlash(Integer flashValue) {
        Flash flash;

        if(null == flashValue) {
            flash = Flash.NOT_FIRED;
        } else if(flashValue == TiffConstants.FLASH_VALUE_FIRED
                || flashValue == TiffConstants.FLASH_VALUE_FIRED_RETURN_NOT_DETECTED
                || flashValue == TiffConstants.FLASH_VALUE_FIRED_RETURN_DETECTED
                || flashValue == TiffConstants.FLASH_VALUE_ON
                || flashValue == TiffConstants.FLASH_VALUE_ON_RETURN_NOT_DETECTED
                || flashValue == TiffConstants.FLASH_VALUE_ON_RETURN_DETECTED
                || flashValue == TiffConstants.FLASH_VALUE_AUTO_FIRED
                || flashValue == TiffConstants.FLASH_VALUE_AUTO_FIRED_RETURN_NOT_DETECTED
                || flashValue == TiffConstants.FLASH_VALUE_AUTO_FIRED_RETURN_DETECTED) {
            flash = Flash.FIRED;
        } else if(flashValue == TiffConstants.FLASH_VALUE_FIRED_RED_EYE_REDUCTION
                || flashValue == TiffConstants.FLASH_VALUE_FIRED_RED_EYE_REDUCTION_RETURN_DETECTED
                || flashValue == TiffConstants.FLASH_VALUE_FIRED_RED_EYE_REDUCTION_RETURN_NOT_DETECTED
                || flashValue == TiffConstants.FLASH_VALUE_AUTO_FIRED_RED_EYE_REDUCTION
                || flashValue == TiffConstants.FLASH_VALUE_AUTO_FIRED_RED_EYE_REDUCTION_RETURN_NOT_DETECTED
                || flashValue == TiffConstants.FLASH_VALUE_AUTO_FIRED_RED_EYE_REDUCTION_RETURN_DETECTED
                || flashValue == TiffConstants.FLASH_VALUE_ON_RED_EYE_REDUCTION
                || flashValue == TiffConstants.FLASH_VALUE_ON_RED_EYE_REDUCTION_RETURN_NOT_DETECTED
                || flashValue == TiffConstants.FLASH_VALUE_ON_RED_EYE_REDUCTION_RETURN_DETECTED) {
            flash = Flash.FIRED_WITH_RED_EYES_REDUCTION;
        } else {
            flash = Flash.NOT_FIRED;
        }

        return flash;
    }

    public static String getTiffExposureProgram(Integer exposureProgramValue) {
        if(exposureProgramValue == null || exposureProgramValue == 0) {
            // not defined or unknown
            return null;
        }

        switch(exposureProgramValue) {
            case TiffConstants.EXPOSURE_PROGRAM_VALUE_PROGRAM_AE:
                return "Program AE";
            case TiffConstants.EXPOSURE_PROGRAM_VALUE_APERTURE_PRIORITY_AE:
                return "Aperture priority AE";
            case TiffConstants.EXPOSURE_PROGRAM_VALUE_SHUTTER_SPEED_PRIORITY_AE:
                return "Shutter speed priority AE";
            case TiffConstants.EXPOSURE_PROGRAM_VALUE_CREATIVE_SLOW_SPEED:
                return "Creative slow speed";
            case TiffConstants.EXPOSURE_PROGRAM_VALUE_ACTION_HIGH_SPEED:
                return "Action high speed";
            case TiffConstants.EXPOSURE_PROGRAM_VALUE_PORTRAIT:
                return "Portrait";
            case TiffConstants.EXPOSURE_PROGRAM_VALUE_LANDSCAPE:
                return "Landscape";
            default:
                // not defined or unknown
                return null;
        }
    }

    public static String getTiffColorSpace(Integer colorSpaceValue) {
        if(colorSpaceValue == null) {
            return null;
        }

        switch(colorSpaceValue) {
            case TiffConstants.COLOR_SPACE_VALUE_ADOBE_RGB:
                return "Adobe RGB";
            default:
                return "sRGB";
        }
    }

    // for jpeg
    public static String getExifCompressionName(Integer compressionValue) {
        if(compressionValue == null) {
            return null;
        }

        String compressionName = null;
        switch (compressionValue) {
           case ExifTagConstants.COMPRESSION_VALUE_CCITT_1D:
               compressionName = "CCITT 1D";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_T4_GROUP_3_FAX:
               compressionName = "T4 GROUP 3 FAX";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_T6_GROUP_4_FAX:
               compressionName = "T6 GROUP 4 FAX";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_LZW:
               compressionName = "LZW";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_JPEG_OLD_STYLE:
               compressionName = "JPEG OLD STYLE";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_JPEG:
               compressionName = "JPEG";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_ADOBE_DEFLATE:
               compressionName = "ADOBE DEFLATE";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_JBIG_B_AND_W:
               compressionName = "JBIG B AND W";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_JBIG_COLOR:
               compressionName = "JBIG COLOR";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_NEXT:
               compressionName = "NEXT";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_EPSON_ERF_COMPRESSED:
               compressionName = "EPSON ERF COMPRESSED";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_CCIRLEW:
               compressionName = "CCIRLEW";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_PACK_BITS:
               compressionName = "PACK BITS";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_IT8CTPAD:
               compressionName = "IT8CTPAD";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_IT8LW:
               compressionName = "IT8LW";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_IT8MP:
               compressionName = "IT8MP";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_IT8BL:
               compressionName = "IT8BL";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_PIXAR_FILM:
               compressionName = "PIXAR FILM";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_PIXAR_LOG:
               compressionName = "PIXAR LOG";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_DEFLATE:
               compressionName = "DEFLATE";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_DCS:
               compressionName = "DCS";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_JBIG:
               compressionName = "JBIG";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_SGILOG:
               compressionName = "SGILOG";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_SGILOG_24:
               compressionName = "SGILOG 24";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_JPEG_2000:
               compressionName = "JPEG 2000";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_NIKON_NEF_COMPRESSED:
               compressionName = "NIKON NEF COMPRESSED";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_KODAK_DCR_COMPRESSED:
               compressionName = "KODAK DCR COMPRESSED";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_PENTAX_PEF_COMPRESSED:
               compressionName = "PENTAX PEF COMPRESSED";
               break;
           case ExifTagConstants.COMPRESSION_VALUE_THUNDERSCAN:
               compressionName = "THUNDERSCAN";
               break;
        }

        return compressionName;
   }

    public static Flash getExifFlash(Integer flashValue) {
        Flash flash;

        /* strobe counts as flash here, can differenciate if needed. */
        if(null == flashValue) {
            flash = Flash.NOT_FIRED;
        } else if(flashValue == ExifTagConstants.FLASH_VALUE_FIRED
                || flashValue == ExifTagConstants.FLASH_VALUE_FIRED_RETURN_NOT_DETECTED
                || flashValue == ExifTagConstants.FLASH_VALUE_FIRED_RETURN_DETECTED
                || flashValue == ExifTagConstants.FLASH_VALUE_ON
                || flashValue == ExifTagConstants.FLASH_VALUE_ON_RETURN_NOT_DETECTED
                || flashValue == ExifTagConstants.FLASH_VALUE_ON_RETURN_DETECTED
                || flashValue == ExifTagConstants.FLASH_VALUE_AUTO_FIRED
                || flashValue == ExifTagConstants.FLASH_VALUE_AUTO_FIRED_RETURN_NOT_DETECTED
                || flashValue == ExifTagConstants.FLASH_VALUE_AUTO_FIRED_RETURN_DETECTED) {
            flash = Flash.FIRED;
        } else if(flashValue == ExifTagConstants.FLASH_VALUE_FIRED_RED_EYE_REDUCTION
                || flashValue == ExifTagConstants.FLASH_VALUE_FIRED_RED_EYE_REDUCTION_RETURN_DETECTED
                || flashValue == ExifTagConstants.FLASH_VALUE_FIRED_RED_EYE_REDUCTION_RETURN_NOT_DETECTED
                || flashValue == ExifTagConstants.FLASH_VALUE_AUTO_FIRED_RED_EYE_REDUCTION
                || flashValue == ExifTagConstants.FLASH_VALUE_AUTO_FIRED_RED_EYE_REDUCTION_RETURN_NOT_DETECTED
                || flashValue == ExifTagConstants.FLASH_VALUE_AUTO_FIRED_RED_EYE_REDUCTION_RETURN_DETECTED
                || flashValue == ExifTagConstants.FLASH_VALUE_ON_RED_EYE_REDUCTION
                || flashValue == ExifTagConstants.FLASH_VALUE_ON_RED_EYE_REDUCTION_RETURN_NOT_DETECTED
                || flashValue == ExifTagConstants.FLASH_VALUE_ON_RED_EYE_REDUCTION_RETURN_DETECTED) {
            flash = Flash.FIRED_WITH_RED_EYES_REDUCTION;
        } else {
            flash = Flash.NOT_FIRED;
        }

        return flash;
    }

    public static String getExifExposureProgram(Integer exposureProgramValue) {
        if(exposureProgramValue == null || exposureProgramValue == 0) {
            // not defined or unknown
            return null;
        }

        switch(exposureProgramValue) {
            case ExifTagConstants.EXPOSURE_PROGRAM_VALUE_PROGRAM_AE:
                return "Normal Program";
            case ExifTagConstants.EXPOSURE_PROGRAM_VALUE_APERTURE_PRIORITY_AE:
                return "Aperture priority";
            case ExifTagConstants.EXPOSURE_PROGRAM_VALUE_SHUTTER_SPEED_PRIORITY_AE:
                return "Shutter priority";
            case ExifTagConstants.EXPOSURE_PROGRAM_VALUE_CREATIVE_SLOW_SPEED:
                return "Creative program";
            case ExifTagConstants.EXPOSURE_PROGRAM_VALUE_ACTION_HIGH_SPEED:
                return "Action program";
            case ExifTagConstants.EXPOSURE_PROGRAM_VALUE_PORTRAIT:
                return "Portrait mode";
            case ExifTagConstants.EXPOSURE_PROGRAM_VALUE_LANDSCAPE:
                return "Landscape mode";
            default:
                // not defined or unknown
                return null;
        }
    }

    public static String getExifColorSpace(Integer colorSpaceValue) {
        if(colorSpaceValue == null) {
            return null;
        }

        switch(colorSpaceValue) {
            case ExifTagConstants.COLOR_SPACE_VALUE_ADOBE_RGB:
                return "Adobe RGB";
            default:
                return "sRGB";
        }
    }

    
    public static boolean writeExifData(File input, ExifData data, File output) {
        try {
            TiffOutputSet outputSet = null;
            
            IImageMetadata metadata = Sanselan.getMetadata(input);
            if (metadata != null && (metadata instanceof JpegImageMetadata)) {
                JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
                
                TiffImageMetadata exif = jpegMetadata.getExif();
                if (exif != null) {
                    outputSet = exif.getOutputSet();
                }
            }
            
            if (outputSet == null) {
                outputSet = new TiffOutputSet();
            }
            
            removeTagIfExists(ExifTagConstants.EXIF_TAG_IMAGE_DESCRIPTION, outputSet);
            removeTagIfExists(ExifTagConstants.EXIF_TAG_USER_COMMENT, outputSet);
            removeTagIfExists(ExifTagConstants.EXIF_TAG_ARTIST, outputSet);
            removeTagIfExists(ExifTagConstants.EXIF_TAG_COPYRIGHT, outputSet);
            
            String description = data.getDescription();
            if (description != null) {
                addAsciiTag(ExifTagConstants.EXIF_TAG_IMAGE_DESCRIPTION, description, outputSet);
                addAsciiTag(ExifTagConstants.EXIF_TAG_USER_COMMENT, description, outputSet);
            }
            
            String artist = data.getArtist();
            if (artist != null) {
                addAsciiTag(ExifTagConstants.EXIF_TAG_ARTIST, artist, outputSet);
            }
            
            String copyright = data.getCopyright();
            if (copyright != null) {
                addAsciiTag(ExifTagConstants.EXIF_TAG_COPYRIGHT, copyright, outputSet);
            }
            
            OutputStream outputStream = null;
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(output));
                
                new ExifRewriter().updateExifMetadataLossless(input, outputStream, outputSet);
                outputStream.flush();
                
                return true;
            } catch (Exception ex) {
                log.warn(ex.getMessage(), ex);
                
                return false;
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return false;
    }
    
    private static void addAsciiTag(TagInfo tagInfo, String tagValue, TiffOutputSet outputSet) {
        try {
            FieldType fieldType = TiffFieldTypeConstants.FIELD_TYPE_ASCII;
            byte[] bytes = fieldType.writeData(tagValue, outputSet.byteOrder);

            TiffOutputField outputField = new TiffOutputField(tagInfo.tag, tagInfo, fieldType, tagValue.length(), bytes);
            outputSet.getOrCreateExifDirectory().add(outputField);
            outputSet.getOrCreateRootDirectory().add(outputField);
        } catch (ImageWriteException ex) {
            log.warn(ex.getMessage(), ex);
        }
    }
    
    private static void removeTagIfExists(TagInfo tagInfo, TiffOutputSet outputSet) {
        if (outputSet.findField(tagInfo) != null) {
            outputSet.removeField(tagInfo);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T getTiffTagValue(TagInfo tagInfo, TiffImageMetadata metadata) {
        try {
            TiffField field = metadata.findField(tagInfo);
            if (field != null) {
                return (T) field.getValue();
            }
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T getExifTagValue(TagInfo tagInfo, JpegImageMetadata metadata) {
        try {
            TiffField field = metadata.findEXIFValue(tagInfo);
            if (field != null) {
                return (T) field.getValue();
            }
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return null;
    }
    
    private static boolean containsHtmlEntities(String value) {
    	return value != null && HTML_ENTITY_PATTERN.matcher(value).find();
    }
    
    private ExifUtils() {
    }
}
