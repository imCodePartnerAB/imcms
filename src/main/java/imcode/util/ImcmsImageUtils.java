package imcode.util;

import com.imcode.imcms.domain.dto.ImageData;
import com.imcode.imcms.domain.dto.ImageData.CropRegion;
import com.imcode.imcms.domain.dto.ImageData.RotateDirection;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.ImageCropRegion;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.servlet.ImcmsSetupFilter;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.*;
import imcode.util.image.Filter;
import imcode.util.image.Format;
import imcode.util.image.ImageOp;
import imcode.util.image.Resize;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Objects;

//fixme: image no + image in a loop
@Component
public class ImcmsImageUtils {
    private static final Log log = LogFactory.getLog(ImcmsImageUtils.class);

    public static File imagesPath;
    public static String imageMagickPath;

    @Value("${ImagePath}")
    private File imgPath;

    @Value("${ImageMagickPath}")
    private String imgMagickPath;

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

            generateImage(tempFile, genFile, image.getFormat(), image.getWidth(), image.getHeight(), image.getResize(),
                    image.getCropRegion(), image.getRotateDirection());

        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);

        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);

            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

    public static boolean generateImage(File imageFile, File destFile, Format format, int width, int height,
                                        Resize resize, CropRegion cropRegion, RotateDirection rotateDir) {

        ImageOp operation = new ImageOp(imageMagickPath).input(imageFile);


        if (rotateDir != RotateDirection.NORTH) {
            operation.rotate(rotateDir.getAngle());
        }

        if (cropRegion.isValid()) {
            int cropWidth = cropRegion.getWidth();
            int cropHeight = cropRegion.getHeight();

            operation.crop(cropRegion.getCropX1(), cropRegion.getCropY1(), cropWidth, cropHeight);
        }

        if (width > 0 || height > 0) {
            Integer w = (width > 0 ? width : null);
            Integer h = (height > 0 ? height : null);

            if (resize == null) {
                resize = (width > 0 && height > 0 ? Resize.FORCE : Resize.DEFAULT);
            }

            operation.filter(Filter.LANCZOS);
            operation.resize(w, h, resize);
        }

        if (format != null) {
            operation.outputFormat(format);
        }

        return operation.processToFile(destFile);
    }

    public static ImageDomainObject toDomainObject(Image image) {
        if (image == null) return null;

        ImageDomainObject imageDO = new ImageDomainObject();

        imageDO.setAlign(image.getAlign());
        imageDO.setAlternateText(image.getAlternateText());
        imageDO.setArchiveImageId(image.getArchiveImageId());
        imageDO.setBorder(image.getBorder());

        ImageCropRegion cropRegion = image.getCropRegion();
        ImageDomainObject.CropRegion cropRegionDO = new ImageDomainObject.CropRegion(
                cropRegion.getCropX1(), cropRegion.getCropY1(), cropRegion.getCropX2(), cropRegion.getCropY2()
        );
        imageDO.setCropRegion(cropRegionDO);
        imageDO.setGeneratedFilename(image.getGeneratedFilename());
        imageDO.setHeight(image.getHeight());
        imageDO.setHorizontalSpace(image.getHorizontalSpace());
        imageDO.setLinkUrl(image.getLinkUrl());
        imageDO.setLowResolutionUrl(image.getLowResolutionUrl());
        imageDO.setName(image.getName());
        imageDO.setResize(Resize.getByOrdinal(image.getResize()));
        imageDO.setTarget(image.getTarget());
        imageDO.setVerticalSpace(image.getVerticalSpace());
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
