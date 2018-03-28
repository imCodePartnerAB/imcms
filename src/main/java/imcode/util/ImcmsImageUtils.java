package imcode.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.imcode.imcms.domain.dto.ImageCropRegionDTO;
import com.imcode.imcms.domain.dto.ImageData;
import com.imcode.imcms.domain.dto.ImageData.RotateDirection;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.servlet.ImcmsSetupFilter;
import imcode.server.Imcms;
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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.Dimension;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
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
        final String suffix = FilenameUtils.getExtension(imgFile.getName());
        final Iterator<ImageReader> imageReaders = ImageIO.getImageReadersBySuffix(suffix);

        while (imageReaders.hasNext()) {
            final ImageReader reader = imageReaders.next();
            try (ImageInputStream stream = new FileImageInputStream(imgFile)) {
                reader.setInput(stream);
                final int width = reader.getWidth(reader.getMinIndex());
                final int height = reader.getHeight(reader.getMinIndex());
                return new Dimension(width, height);

            } catch (IOException e) {
                log.warn("Error reading: " + imgFile.getAbsolutePath(), e);

            } finally {
                reader.dispose();
            }
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

    public static void generateImage(ImageData image, boolean overwrite) {
        File genFile = new File(imagesPath, "generated/" + image.getGeneratedFilename());

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

        setSize(image, operation);
        setRotateDirection(image, operation);
        setCropRegion(image, operation);
        setFormat(image.getFormat(), operation);

        operation.processToFile(destFile);
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

    public static ImageDomainObject toDomainObject(Image image) {
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

    private static ImageDomainObject initImageSource(Image jpaImage, ImageDomainObject imageDO) {
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
                        String.format("Illegal image source type - IMAGE_TYPE_ID__FILE_DOCUMENT. Image: %s", image)
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
