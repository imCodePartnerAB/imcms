package imcode.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.imcode.imcms.domain.dto.ImageCropRegionDTO;
import com.imcode.imcms.domain.dto.ImageData;
import com.imcode.imcms.domain.dto.ImageData.RotateDirection;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.ImageCropRegion;
import com.imcode.imcms.persistence.entity.ImageCacheDomainObject;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.servlet.ImcmsSetupFilter;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.FileDocumentImageSource;
import imcode.server.document.textdocument.ImageArchiveImageSource;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import imcode.server.document.textdocument.NullImageSource;
import imcode.util.image.Filter;
import imcode.util.image.Format;
import imcode.util.image.ImageOp;
import imcode.util.image.Resize;
import imcode.util.io.FileUtility;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class ImcmsImageUtils {

    private static final Log log = LogFactory.getLog(ImcmsImageUtils.class);
    private static final int GEN_FILE_LENGTH = 255;

    public static File imagesPath;
    public static String imageMagickPath;

    @Value("${ImagePath}")
    private File imgPath;

    @Value("${ImageMagickPath}")
    private String imgMagickPath;

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

    /**
     * Gets image dimensions for given file
     *
     * @param imgFile image file
     * @return dimensions of image
     * @see <a href="https://stackoverflow.com/questions/672916/how-to-get-image-height-and-width-using-java#answer-12164026">method source</a>
     */
    public static Dimension getImageDimension(File imgFile) {
        try {
            BufferedImage bufferedImg = ImageIO.read(imgFile);

            final int width = bufferedImg.getWidth();
            final int height = bufferedImg.getHeight();
            return new Dimension(width, height);

        } catch (IOException e) {
            log.warn("Error reading: " + imgFile.getAbsolutePath(), e);
        }
        return null;
    }

    public static ImageSource getImageSource(String imagePath) {
        ImageSource imageSource = new NullImageSource();

        if (imagePath.startsWith("/")) {
            imagePath = imagePath.substring(1);
        }

        if (StringUtils.isNotBlank(imagePath)) {
            imageSource = new ImagesPathRelativePathImageSource(imagePath);
        }
        return imageSource;
    }

    public static ImageSource createImageSourceFromString(String imageUrl) {
        ImageSource imageSource = new NullImageSource();
        if (StringUtils.isNotBlank(imageUrl)) {
            ImcmsServices services = Imcms.getServices();
            DocumentMapper documentMapper = services.getDocumentMapper();
            String documentIdString = ImcmsSetupFilter.getDocumentIdString(services, imageUrl);
            DocumentDomainObject document = documentMapper.getDocument(documentIdString);
            if (document instanceof FileDocumentDomainObject) {
                imageSource = new FileDocumentImageSource(documentMapper.getDocumentReference(document));
            } else {
                String imageArchiveImagesUrl = ImageArchiveImageSource.getImagesUrlPath();
                String imagesPath = ImagesPathRelativePathImageSource.getImagesUrlPath();
                if (imageUrl.startsWith(imageArchiveImagesUrl)) {
                    imageUrl = imageUrl.substring(imageArchiveImagesUrl.length());

                    if (StringUtils.isNotBlank(imageUrl)) {
                        imageSource = new ImageArchiveImageSource(imageUrl);
                    }
                } else {
                    if (imageUrl.startsWith(imagesPath)) {
                        imageUrl = imageUrl.substring(imagesPath.length());
                    }

                    if (StringUtils.isNotBlank(imageUrl)) {
                        imageSource = new ImagesPathRelativePathImageSource(imageUrl);
                    }
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

    public static void generateImage(ImageData image, boolean overwrite) {
        File genFile = new File(imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER + File.separator + image.getGeneratedFilename());

        if (!overwrite && genFile.exists()) {
            return;
        }

        ImageSource source = image.getSource();

        if (source instanceof NullImageSource) {
            return;
        }

        InputStream input = null;
        OutputStream output = null;
        File tempFile = null;

        try {
            String imagePathCanon = imagesPath.getCanonicalPath();
            String genFileCanon = genFile.getCanonicalPath();

            if (!genFileCanon.startsWith(imagePathCanon)) {
                return;
            }

            File parentFile = genFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdir();
            }

            tempFile = File.createTempFile("genimg", null);

            input = source.getInputStreamSource().getInputStream();
            output = new BufferedOutputStream(new FileOutputStream(tempFile));

            IOUtils.copy(input, output);
            IOUtils.closeQuietly(output);

            generateImage(tempFile, genFile, image);

        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);

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

    private static void generateImage(File imageFile, File destFile, ImageData image) {

        final ImageOp operation = new ImageOp(imageMagickPath).input(imageFile);

        setCropRegion(image, operation);
        setSize(image, operation);
        setRotateDirection(image, operation);
        setFormat(image.getFormat(), operation);
        setQuality(image.isCompress(), image.getFormat(), operation);
        operation.processToFile(destFile);
    }

    private static void setQuality(boolean compress, Format format, ImageOp operation) {
        if (compress && format == Format.JPEG) {
            operation.quality(75);
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

    private static void setRotateDirection(ImageData image, ImageOp operation) {
        final RotateDirection rotateDir = image.getRotateDirection();

        if (rotateDir != RotateDirection.NORTH) {
            operation.rotate(rotateDir.getAngle());
        }
    }

    public static List<String> getExifInfo(String imageUrl) {
        final List<String> exifInfo = new ArrayList<>();
        final ImageSource imageSource = ImcmsImageUtils.getImageSource(imageUrl);

        if (imageSource instanceof ImagesPathRelativePathImageSource) {

            final boolean exists = ((ImagesPathRelativePathImageSource) imageSource).getFile().exists();

            if (!exists) {
                return exifInfo;
            }

            try (final InputStream inputStream = imageSource.getInputStreamSource().getInputStream()) {
                final Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

                for (Directory directory : metadata.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        exifInfo.add(tag.toString());
                    }
                    for (String error : directory.getErrors()) {
                        exifInfo.add("ERROR: " + error);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return exifInfo;
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

            case ImageSource.IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH:
                return new ImagesPathRelativePathImageSource(url);

            case ImageSource.IMAGE_TYPE_ID__IMAGE_ARCHIVE:
                return new ImageArchiveImageSource(url);

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
