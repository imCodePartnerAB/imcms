package imcode.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.icc.IccDirectory;
import com.imcode.imcms.components.exception.CompressionImageException;
import com.imcode.imcms.components.impl.compressor.image.DefaultImageCompressor;
import com.imcode.imcms.domain.dto.ExifDTO;
import com.imcode.imcms.domain.dto.ImageCropRegionDTO;
import com.imcode.imcms.domain.dto.ImageData;
import com.imcode.imcms.domain.dto.ImageData.RotateDirection;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.ImageCropRegion;
import com.imcode.imcms.persistence.entity.ImageCacheDomainObject;
import com.imcode.imcms.persistence.entity.ImageJPA;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.*;
import imcode.util.image.*;
import imcode.util.io.FileUtility;
import imcode.util.io.InputStreamSource;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;

@Component
public class ImcmsImageUtils {

    private static final Log log = LogFactory.getLog(ImcmsImageUtils.class);
    private static final int GEN_FILE_LENGTH = 255;

    public static String imagesPath;
    public static String imageMagickPath;

    @Value("${ImagePath}")
    private String imgPath;
    @Value("${ImageMagickPath}")
    private String imgMagickPath;

    private static final Set<Integer> unnecessaryExifInfo = new HashSet<>();
    static {
        unnecessaryExifInfo.add(IccDirectory.TAG_TAG_rTRC);
        unnecessaryExifInfo.add(IccDirectory.TAG_TAG_bTRC);
        unnecessaryExifInfo.add(IccDirectory.TAG_TAG_gTRC);
        unnecessaryExifInfo.add(IccDirectory.TAG_TAG_kTRC);
    }

    public static String getImageUrl(ImageDomainObject image, String contextPath) {
        return getImageUrl(image, contextPath, false);
    }

    @Deprecated
    public static String getImageHandlingUrl(ImageDomainObject image, String contextPath) {
        return contextPath + "/imagehandling" + getImageQueryString(image, false);
    }

    public static String getImageETag(String path, File imageFile, Format format, int width, int height,
                                      ImageCropRegion cropRegion, RotateDirection rotateDirection) {

        StringBuilder builder = new StringBuilder();
        builder.append(path);
        builder.append(imageFile.length());
        builder.append(imageFile.lastModified());
        builder.append(width);
        builder.append(height);
        builder.append(rotateDirection.name());

        if (format != null) {
            builder.append(format.getOrdinal());
        }
        if (cropRegion != null) {
            builder.append(cropRegion.getCropX1());
            builder.append(cropRegion.getCropY1());
            builder.append(cropRegion.getCropX2());
            builder.append(cropRegion.getCropY2());
        }

        return "W/\"" + DigestUtils.md5Hex(builder.toString()) + "\"";
    }

    private static String getImageQueryString(ImageDomainObject image, boolean forPreview) {
        StringBuilder builder = new StringBuilder("?");

        if (!forPreview && image.getSource() instanceof FileDocumentImageSource) {
            FileDocumentImageSource source = (FileDocumentImageSource) image.getSource();
            builder.append("file_id=");
            builder.append(source.getFileDocument().getId());
        } else {
            builder.append("path=");
            builder.append(Utility.encodeUrl(image.getUrlPathRelativeToContextPath()));
        }

        builder.append("&width=");
        builder.append(image.getWidth());
        builder.append("&height=");
        builder.append(image.getHeight());

        if (image.getFormat() != null) {
            builder.append("&format=");
            builder.append(image.getFormat().getExtension());
        }

        ImageCropRegionDTO region = image.getCropRegion();
        if (region.isValid()) {
            builder.append("&crop_x1=");
            builder.append(region.getCropX1());
            builder.append("&crop_y1=");
            builder.append(region.getCropY1());
            builder.append("&crop_x2=");
            builder.append(region.getCropX2());
            builder.append("&crop_y2=");
            builder.append(region.getCropY2());
        }

        builder.append("&rangle=");
        builder.append(image.getRotateDirection().getAngle());

        if (!forPreview && image.getGeneratedFilename() != null) {
            builder.append("&gen_file=");
            builder.append(image.getGeneratedFilename());
        }

        if (image.getResize() != null) {
            builder.append("&resize=");
            builder.append(image.getResize().name().toLowerCase());
        }

        return builder.toString();
    }

    public static String getImageUrl(ImageDomainObject image, String contextPath, boolean includeQueryParams) {
        String generatedFilename = image.getGeneratedFilename();

        if (generatedFilename == null) {
            return getImageHandlingUrl(image, contextPath);
        }

        String url = image.getUrlPathRelativeToContextPath();

        if (includeQueryParams) {
            url += getImageQueryString(image, false);
        }

        return url;
    }

    public static String generateImageFileName(ImageData imageData) {
        String suffix = "_" + UUID.randomUUID().toString();

        Format fmt = imageData.getFormat();
        if (fmt != null) {
            suffix += "." + fmt.getExtension();
        }

        final int maxLength = GEN_FILE_LENGTH - suffix.length();

        String filename = imageData.getSource().getNameWithoutExt();

        if (filename.length() > maxLength) {
            filename = filename.substring(0, maxLength);
        }

        filename = Utility.normalizeString(filename);

        return filename + suffix;
    }

    public static Dimension getImageDimension(InputStream imgInputStream, Format format) {
        Dimension imageDimension = null;

        try {
            final InputStream bufferedInputStream = new BufferedInputStream(imgInputStream, imgInputStream.available());
            bufferedInputStream.mark(Integer.MAX_VALUE);

            imageDimension = getImageDimension(bufferedInputStream);

            // Check Exif tag Orientation. If it shows the image is rotated to the left or right, we need to correct
            // the resulting width and height.
            if (imageDimension != null && (format == Format.JPEG || format == Format.JPG)) {

                bufferedInputStream.reset();
                final ExifOrientation exifOrientation = getExifOrientation(bufferedInputStream);
                if (exifOrientation == ExifOrientation.LEFT || exifOrientation == ExifOrientation.FLIPPED_LEFT ||
                        exifOrientation == ExifOrientation.RIGHT || exifOrientation == ExifOrientation.FLIPPED_RIGHT) {

                    imageDimension.setSize(imageDimension.getHeight(), imageDimension.getWidth());  //swap width and height
                }
            }
        } catch (Exception e) {
            log.error("Error reading image dimension", e);
        }

        return imageDimension;
    }

    public static ExifOrientation getExifOrientation(InputStream imgInputStream)
            throws ImageProcessingException, MetadataException, IOException {

        final Metadata metadata = ImageMetadataReader.readMetadata(imgInputStream);
        final ExifIFD0Directory exifDirectory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if (exifDirectory != null && exifDirectory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
            final int orientation = exifDirectory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            return ExifOrientation.fromValue(orientation);
        }

        return null;
    }

    /**
     * Gets image dimensions for given image input stream
     *
     * @param imgInputStream input stream
     * @return dimensions of image
     * @see <a href="https://stackoverflow.com/questions/1559253/java-imageio-getting-image-dimensions-without-reading-the-entire-file/1560052#1560052">method source</a>
     */
    public static Dimension getImageDimension(InputStream imgInputStream) {
        try (ImageInputStream in = ImageIO.createImageInputStream(imgInputStream)) {
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    return new Dimension(reader.getWidth(0), reader.getHeight(0));
                } finally {
                    reader.dispose();
                }
            }
        } catch (IOException e) {
            log.warn("Error reading image dimension", e);
        }

        return null;
    }

    public static ImageSource getImageSource(String imagePath) {
        if(StringUtils.isBlank(imagePath)) return new NullImageSource();

        if (imagePath.startsWith("/")) {
            imagePath = imagePath.substring(1);
        }
        return new FileStorageImageSource(imagePath);
    }

    public static ImageSource createImageSourceFromString(String imageUrl) {
        ImageSource imageSource = new NullImageSource();
        if (StringUtils.isNotBlank(imageUrl)) {
            ImcmsServices services = Imcms.getServices();
            DocumentMapper documentMapper = services.getDocumentMapper();
            String documentIdString = Utility.extractDocumentIdentifier(imageUrl);
            DocumentDomainObject document = documentMapper.getDocument(documentIdString);
            if (document instanceof FileDocumentDomainObject) {
                imageSource = new FileDocumentImageSource(documentMapper.getDocumentReference(document));
            } else {
                    if (imageUrl.startsWith(imagesPath)) {
                        imageUrl = imageUrl.substring(imagesPath.length());
                    }

                    if (StringUtils.isNotBlank(imageUrl)) {
                        imageSource = new FileStorageImageSource(imageUrl);
                    }
            }
        }
        return imageSource;
    }

    public static boolean generateImage(File imageFile, File destFile, ImageCacheDomainObject imageCache, boolean withoutCropOperation) {

        ImageOp operation = new ImageOp(imageMagickPath).input(imageFile);

        final int width = imageCache.getWidth();
        final int height = imageCache.getHeight();

        if (width > 0 || height > 0) {
            Integer w = (width > 0 ? width : null);
            Integer h = (height > 0 ? height : null);

            Resize resize = (width > 0 && height > 0 ? Resize.FORCE : Resize.DEFAULT);

            operation.filter(Filter.LANCZOS);
            operation.resize(w, h, resize);
        }

        final RotateDirection rotateDir = imageCache.getRotateDirection();

        if (rotateDir != RotateDirection.NORTH) {
            operation.rotate(rotateDir.getAngle());
        }

        if (!withoutCropOperation) {

            final ImageCropRegionDTO cropRegion = new ImageCropRegionDTO(imageCache.getCropRegion());

            if (cropRegion.isValid()) {
                int cropWidth = cropRegion.getWidth();
                int cropHeight = cropRegion.getHeight();

                operation.crop(cropRegion.getCropX1(), cropRegion.getCropY1(), cropWidth, cropHeight);
            }
        }

        final Format format = imageCache.getFormat();

        if (format != null) {
            operation.outputFormat(format);
        }

        return operation.processToFile(destFile);
    }

    public static byte[] generateImage(ImageData image) {
        ImageSource source = image.getSource();

        if (source instanceof NullImageSource) {
            return null;
        }

        InputStream input = null;
        OutputStream output = null;
        File tempFile = null;

        try {
            tempFile = File.createTempFile("genimg", null);

            input = source.getInputStreamSource().getInputStream();
            output = new BufferedOutputStream(new FileOutputStream(tempFile));

            IOUtils.copy(input, output);
            IOUtils.closeQuietly(output);

            return generateImage(tempFile, image);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
            return null;
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);

            if (tempFile != null) {
                try {
                    FileUtility.forceDelete(tempFile);
                } catch (IOException e) {
                    log.error("Can't delete file " + tempFile, e);
                }
            }
        }
    }

    private static byte[] generateImage(File imageFile, ImageData image) {
        final ImageOp operationGenerateImage = new ImageOp(imageMagickPath).input(imageFile);

        if(image.getFormat() == Format.JPEG){
            operationGenerateImage.autoOrient();    //reset EXIF "Orientation" setting to generate correctly
        }

        setCropRegion(image, operationGenerateImage);
        setSize(image, operationGenerateImage);
        setRotateDirection(image, operationGenerateImage);
        setFormat(image.getFormat(), operationGenerateImage);

        // Set the number of colors as in the original (the size becomes huge due to the overlay of colors after changing the GIF).
        if(image.getFormat() == Format.GIF){
            Integer colors = getNumberOfColors(imageFile);
            if(colors != null) setNumberOfColors(colors, operationGenerateImage);
        }

        byte[] imageContent = operationGenerateImage.processToByteArray();

        return image.isCompress() ? compressImage(imageContent, image.getFormat()) : imageContent;
    }

    public static byte[] editCommentMetadata(String comment, ImageSource imageSource){
        InputStreamSource inputStreamSource = imageSource.getInputStreamSource();
        try(final InputStream inputStream = inputStreamSource.getInputStream()){
            final ImageOp operationGenerateImage = new ImageOp(imageMagickPath).input(inputStream);
            operationGenerateImage.comment(comment);
            return operationGenerateImage.processToByteArray();
        }catch (Exception e){
            log.error("Error when editing metadata in an image file");
            return null;
        }
    }

    public static String getCommentMetadata(InputStream inputStream){
        final byte[] result = new ImageOp(imageMagickPath).identify().format("%c").input(inputStream, 0).identifyProcess();
        return Optional.ofNullable(result)
                .map(String::new)
                .orElse("");
    }

    public static ExifDTO getExif(ImageSource imageSource){
        if (imageSource instanceof FileStorageImageSource && !imageSource.isEmpty()) {
            InputStreamSource inputStreamSource = imageSource.getInputStreamSource();
            try(final InputStream inputStream = inputStreamSource.getInputStream()){

                return getExif(inputStream);

            } catch (Exception e) {
                log.error("Error when receiving an EXIF");
            }
        }

        return null;
    }

    private static Integer getNumberOfColors(File imageFile) {
        final String separator = ";";

        final ImageOp operationGetColors = new ImageOp(imageMagickPath).identify();
        setNumberOfColorsToGet(separator, operationGetColors);
        operationGetColors.input(imageFile);

        final byte[] result = operationGetColors.identifyProcess();

        if (result != null) {
            return Arrays.stream(new String(result).split(separator))
                    .map(Integer::parseInt)
                    .max(Integer::compareTo)
                    .orElse(null);
        }

        return null;
    }

    /**
     * Compress the image using a compressor selected in the properties.
     * Compress using a default compressor if selected one fails.
     */
    @SneakyThrows
    public static byte[] compressImage(byte[] imageContent, Format format) {
        try {
            return Imcms.getServices().getImageCompressor().compressImage(imageContent, format);
        } catch (CompressionImageException e) {
            return new DefaultImageCompressor(imageMagickPath).compressImage(imageContent, format);
        }
    }

    private static void setFormat(Format format, ImageOp operation) {
        Optional.ofNullable(format).ifPresent(operation::outputFormat);
    }

    private static void setCropRegion(ImageData image, ImageOp operation) {
        final ImageCropRegionDTO cropRegion = image.getCropRegion();

        if (cropRegion.isValid()) {
            operation.crop(
                    cropRegion.getCropX1(),
                    cropRegion.getCropY1(),
                    cropRegion.getWidth(),
                    cropRegion.getHeight()
            );
        }
    }

    private static void setSize(ImageData image, ImageOp operation) {
        final int height = image.getHeight();
        final int width = image.getWidth();

        if (width > 0 || height > 0) {
            Integer w = (width > 0 ? width : null);
            Integer h = (height > 0 ? height : null);

            final Resize resize = Optional.ofNullable(image.getResize())
                    .orElse(width > 0 && height > 0 ? Resize.FORCE : Resize.DEFAULT);

            operation.filter(Filter.LANCZOS);
            operation.resize(w, h, resize);
        }
    }

    private static void setNumberOfColors(int numberOfColors, ImageOp operation) {
        operation.colors(numberOfColors);
    }

    private static void setNumberOfColorsToGet(String separator, ImageOp operation) {
        operation.format("%k" + separator);
    }

    private static void setRotateDirection(ImageData image, ImageOp operation) {
        final RotateDirection rotateDir = image.getRotateDirection();

        if (rotateDir != RotateDirection.NORTH) {
            operation.rotate(rotateDir.getAngle());
        }
    }

    public static ExifDTO getExif(InputStream inputStream) throws ImageProcessingException, IOException {
        final ExifDTO exifDTO = new ExifDTO();

        try {
            inputStream = new BufferedInputStream(inputStream, inputStream.available());
            inputStream.mark(Integer.MAX_VALUE);

            exifDTO.setAllExifInfo(getExifInfo(inputStream));

            inputStream.reset();
            exifDTO.setCustomExif(ExifDTO.CustomExifDTO.mapToCustomExif(getCommentMetadata(inputStream)));
        } catch (Exception e) {
            if (exifDTO.getAllExifInfo() == null) exifDTO.setAllExifInfo(Collections.emptyList());
            exifDTO.setCustomExif(new ExifDTO.CustomExifDTO());

            log.error("Exception while getting Exif info", e);
        }

        return exifDTO;
    }

    public static List<String> getExifInfo(ImageSource imageSource) {
        if (imageSource instanceof FileStorageImageSource && !imageSource.isEmpty()) {
            InputStreamSource inputStreamSource = imageSource.getInputStreamSource();

            try(final InputStream inputStream = inputStreamSource.getInputStream()){
                return getExifInfo(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Collections.emptyList();
    }

    public static List<String> getExifInfo(InputStream inputStream) throws ImageProcessingException, IOException {
        final List<String> exifInfo = new ArrayList<>();

        final Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                exifInfo.add(tag.toString());
            }
            for (String error : directory.getErrors()) {
                exifInfo.add("ERROR: " + error);
            }
        }

        return exifInfo;
    }

    public static long getSize(ImageSource imageSource){
        try{
            return imageSource.getInputStreamSource().getSize();
        }catch (IOException e){
            return 0;
        }
    }

    public static ImageDomainObject toDomainObject(ImageJPA image) {
        if (image == null) return null;

        final ImageDomainObject imageDO = new ImageDomainObject();

        imageDO.setAlign(image.getAlign());
        imageDO.setAlternateText(image.getAlternateText());
        imageDO.setArchiveImageId(image.getArchiveImageId());
        imageDO.setBorder(image.getBorder());

        imageDO.setCropRegion(new ImageCropRegionDTO(image.getCropRegion()));
        imageDO.setGeneratedFilename(image.getGeneratedFilename());
        imageDO.setHeight(image.getHeight());
        imageDO.setSpaceAround(image.getSpaceAround());
        imageDO.setLinkUrl(image.getLinkUrl());
        imageDO.setLowResolutionUrl(image.getLowResolutionUrl());
        imageDO.setName(image.getName());
        imageDO.setResize(Resize.getByOrdinal(image.getResize()));
        imageDO.setTarget(image.getTarget());
        imageDO.setWidth(image.getWidth());

        return initImageSource(image, imageDO);
    }

    private static ImageDomainObject initImageSource(ImageJPA jpaImage, ImageDomainObject imageDO) {
        String url = jpaImage.getUrl();
        Integer type = jpaImage.getType();

        Objects.requireNonNull(url);
        Objects.requireNonNull(type);

        imageDO.setSource(createImageSource(imageDO, url.trim(), type));

        return imageDO;
    }

    private static ImageSource createImageSource(ImageDomainObject image, String url, int type) {
        switch (type) {
            case ImageSource.IMAGE_TYPE_ID__FILE_DOCUMENT:
                throw new IllegalStateException(
                        String.format("Illegal image source type - IMAGE_TYPE_ID__FILE_DOCUMENT. ImageJPA: %s", image)
                );
            case ImageSource.IMAGE_TYPE_ID__FILE_STORAGE:
                return new FileStorageImageSource(url);
            default:
                return new NullImageSource();
        }
    }

    @PostConstruct
    public void init() {
        ImcmsImageUtils.imagesPath = imgPath;
        ImcmsImageUtils.imageMagickPath = imgMagickPath;
    }
}
