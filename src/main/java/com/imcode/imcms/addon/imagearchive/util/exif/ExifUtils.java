package com.imcode.imcms.addon.imagearchive.util.exif;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
    private static final Log log = LogFactory.getLog(ExifUtils.class);
    
    private static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("&#[0-9a-z]+;");
    
    public static ExifData getExifData(File file) {
        try {
            ImageFormat imageFormat = Sanselan.guessFormat(file);
            if (imageFormat.equals(ImageFormat.IMAGE_FORMAT_UNKNOWN)) {
                return null;
            }
            
            IImageMetadata metadata = Sanselan.getMetadata(file);
            
            String imageDescription = null;
            String userComment = null;
            String copyright = null;
            String artist = null;
            RationalNumber resolution = null;
            
            if (metadata instanceof TiffImageMetadata) {
                TiffImageMetadata tiffMetadata = (TiffImageMetadata) metadata;
                
                imageDescription = getTiffTagValue(TiffConstants.TIFF_TAG_IMAGE_DESCRIPTION, tiffMetadata);
                copyright = getTiffTagValue(TiffConstants.TIFF_TAG_COPYRIGHT, tiffMetadata);
                artist = getTiffTagValue(TiffConstants.TIFF_TAG_ARTIST, tiffMetadata);
                resolution = getTiffTagValue(TiffConstants.TIFF_TAG_XRESOLUTION, tiffMetadata);
            } else if (metadata instanceof JpegImageMetadata) {
                JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
                
                imageDescription = getExifTagValue(ExifTagConstants.EXIF_TAG_IMAGE_DESCRIPTION, jpegMetadata);
                userComment = getExifTagValue(ExifTagConstants.EXIF_TAG_USER_COMMENT, jpegMetadata);
                copyright = getExifTagValue(ExifTagConstants.EXIF_TAG_COPYRIGHT, jpegMetadata);
                artist = getExifTagValue(ExifTagConstants.EXIF_TAG_ARTIST, jpegMetadata);
                resolution = getExifTagValue(ExifTagConstants.EXIF_TAG_XRESOLUTION, jpegMetadata);
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
            if (resolution != null && resolution.intValue() > 0) {
                data.setResolution(resolution.intValue());
            }
            
            return data;
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return null;
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
