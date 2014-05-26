package com.imcode.imcms.admin.docadmin.image;

import static imcode.util.Utility.i;
import static imcode.util.Utility.f;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.mapping.TextDocumentContentLoader;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.mapping.container.TextDocImagesContainer;
import com.imcode.util.ImageSize;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.server.*;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.ConcurrentDocumentModificationException;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.*;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.server.user.UserDomainObject;
import imcode.util.*;
import imcode.util.Utility;
import imcode.util.image.Format;
import imcode.util.image.ImageInfo;
import imcode.util.image.ImageOp;
import imcode.util.image.Resize;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import scala.runtime.AbstractFunction0;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Theme("imcms")
public class EditImageUI extends UI {
    /**
     * A lenient {@link com.vaadin.data.util.converter.StringToIntegerConverter} that doesn't throw
     * exceptions when the converted value is not a valid integer and instead uses an error value;
     */
    private static class LenientStringToIntegerConverter extends StringToIntegerConverter {
        private final int errorValue;

        private LenientStringToIntegerConverter(int errorValue) {
            this.errorValue = errorValue;
        }

        @Override
        public Integer convertToModel(String value, Class<? extends Integer> targetType, Locale locale) throws ConversionException {
            Integer val = null;
            try {
                val = super.convertToModel(value, targetType, locale);

            } catch (Exception ex) {
                // ignore
            }

            return (val != null ? val : errorValue);
        }

        @Override
        public String convertToPresentation(Integer value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            String val = null;

            try {
                val = super.convertToPresentation(value, targetType, locale);

            } catch (Exception ex) {
                // ignore
            }

            return (val != null ? val : Integer.toString(errorValue));
        }
    }

    /**
     * A {@link java.lang.String} to {@link java.lang.String} converter that trims the value to an empty string
     * when converting to model.
     */
    private static class TrimConverter implements Converter<String, String> {
        @Override
        public String convertToModel(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            return StringUtils.trimToEmpty(value);
        }

        @Override
        public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            return value;
        }

        @Override
        public Class<String> getModelType() {
            return String.class;
        }

        @Override
        public Class<String> getPresentationType() {
            return String.class;
        }
    }

    /**
     * A {@link java.lang.String} to {@link java.lang.String} that makes it possible to type just digits
     * of a document ID in the presentation and get a URL to the document stored as a model value.
     */
    private static class LinkUrlConverter implements Converter<String, String> {
        private static final String GET_DOC_URL = "GetDoc?meta_id=";
        private static final Pattern DIGITS_PATTERN = Pattern.compile("^\\d+$");
        private static final Pattern GET_DOC_PATTTERN = Pattern.compile("^" + Pattern.quote(GET_DOC_URL) + "(\\d+)$");

        @Override
        public String convertToModel(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            value = StringUtils.trimToEmpty(value);

            // If the value consist of only digits then it's an ID of a document. In this
            // case we prefix the model value with a URL of the GetDoc servlet.
            Matcher matcher = DIGITS_PATTERN.matcher(value);

            if (matcher.matches()) {
                value = GET_DOC_URL + value;
            }

            return value;
        }

        @Override
        public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            value = StringUtils.trimToEmpty(value);

            // Remove the GetDoc servlet URL prefix from the model value (if any).
            Matcher matcher = GET_DOC_PATTTERN.matcher(value);

            if (!matcher.matches()) {
                return value;
            }

            return matcher.group(1);
        }

        @Override
        public Class<String> getModelType() {
            return String.class;
        }

        @Override
        public Class<String> getPresentationType() {
            return String.class;
        }
    }

    public static final String REQUEST_PARAMETER__DOCUMENT_ID = "meta_id";
    public static final String REQUEST_PARAMETER__IMAGE_INDEX = "img";
    public static final String REQUEST_PARAMETER__LOOP_ENTRY_REFERENCE = "loop_ref";
    public static final String REQUEST_PARAMETER__LABEL = "label";
    public static final String REQUEST_PARAMETER__WIDTH = "width";
    public static final String REQUEST_PARAMETER__HEIGHT = "height";
    public static final String REQUEST_PARAMETER__MAX_WIDTH = "max-width";
    public static final String REQUEST_PARAMETER__MAX_HEIGHT = "max-height";

    public static final Format[] ALLOWED_FORMATS = new Format[] { Format.GIF, Format.JPEG, Format.PNG };

    private int maxWidth;
    private int maxHeight;
    private TextDocImagesContainer imagesContainer;
    private ObjectProperty<Boolean> shareImages = new ObjectProperty<>(false);
    private boolean linkable = true;

    @Override
    protected void init(VaadinRequest request) {

        final String contextPath = request.getContextPath();
        HttpServletRequest httpRequest = VaadinServletService.getCurrentServletRequest();
        final ImcmsServices imcref = Imcms.getServices();

        final UserDomainObject user = Utility.getLoggedOnUser(httpRequest);
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        TextDocumentContentLoader docContentLoader = imcref.getManagedBean(TextDocumentContentLoader.class);

        int documentId = Integer.parseInt(request.getParameter(REQUEST_PARAMETER__DOCUMENT_ID));

        int imageNo = Integer.parseInt(request.getParameter(REQUEST_PARAMETER__IMAGE_INDEX));

        Optional<LoopEntryRef> loopEntryRefOpt = LoopEntryRef.parse(request.getParameter(REQUEST_PARAMETER__LOOP_ENTRY_REFERENCE));

        // the resulting image must exactly have these dimensions
        int forcedWidth = Math.max(NumberUtils.toInt(request.getParameter(REQUEST_PARAMETER__WIDTH)), 0);
        int forcedHeight = Math.max(NumberUtils.toInt(request.getParameter(REQUEST_PARAMETER__HEIGHT)), 0);

        // the resulting image should fit into the bounds given by maxWidth and maxHeight
        maxWidth = Math.max(NumberUtils.toInt(request.getParameter(REQUEST_PARAMETER__MAX_WIDTH)), 0);
        maxHeight = Math.max(NumberUtils.toInt(request.getParameter(REQUEST_PARAMETER__MAX_HEIGHT)), 0);

        if (forcedWidth > 0) {
            maxWidth = 0;
        }
        if (forcedHeight > 0) {
            maxHeight = 0;
        }

        // a lable to be shown along each image in the editor
        String imageLabelText =  StringUtils.defaultString(request.getParameter(REQUEST_PARAMETER__LABEL));

        final String returnURL = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL);


        final TextDocumentDomainObject document = documentMapper.getDocument(documentId);

        // Check if user has mainImage rights
        if (!userHasImagePermissionsOnDocument(user, document)) {
            Utility.redirectToStartDocument(request);
            return;
        }

        final Map<DocumentLanguage, ImageDomainObject> images = docContentLoader.getImages(document.getVersionRef(),
                imageNo, loopEntryRefOpt);

        // Add an empty ImageDomainObject for any missing supported language
        for (DocumentLanguage language : imcref.getDocumentLanguages().getAll()) {
            if (!images.containsKey(language)) {
                images.put(language, new ImageDomainObject());
            }
        }

        imagesContainer = TextDocImagesContainer.of(document.getVersionRef(), loopEntryRefOpt.orElse(null), imageNo, images);
        shareImages.setValue(isHasSameSource(imagesContainer));


        final TreeMap<DocumentLanguage, BeanItem<ImageDomainObject>> imageBeansByLanguage = new TreeMap<>(DocumentLanguage.NATIVE_NAME_COMPARATOR);

        for (Entry<DocumentLanguage, ImageDomainObject> entry : images.entrySet()) {
            BeanItem<ImageDomainObject> imageBean = new BeanItem<>(entry.getValue());

            imageBeansByLanguage.put(entry.getKey(), imageBean);
        }

        Collection<BeanItem<ImageDomainObject>> imageBeans = imageBeansByLanguage.values();

        final MultiSetBeanItemProperty<ImageSource> multiSourceProp = new MultiSetBeanItemProperty("source", ImageSource.class, imageBeans);
        final MultiSetBeanItemProperty<CropRegion> multiCropRegionProp = new MultiSetBeanItemProperty("cropRegion", CropRegion.class, imageBeans);
        final MultiSetBeanItemProperty<RotateDirection> multiRotateDirectionProp = new MultiSetBeanItemProperty("rotateDirection", RotateDirection.class, imageBeans);
        final MultiSetBeanItemProperty<String> multiAlternateTextProp = new MultiSetBeanItemProperty("alternateText", String.class, imageBeans);
        final MultiSetBeanItemProperty<String> multiNameProp = new MultiSetBeanItemProperty("name", String.class, imageBeans);
        final MultiSetBeanItemProperty<Integer> multiWidthProp = new MultiSetBeanItemProperty("width", Integer.class, imageBeans);
        final MultiSetBeanItemProperty<Integer> multiHeightProp = new MultiSetBeanItemProperty("height", Integer.class, imageBeans);
        final MultiSetBeanItemProperty<Integer> multiBorderProp = new MultiSetBeanItemProperty("border", Integer.class, imageBeans);
        final MultiSetBeanItemProperty<Format> multiFormatProp = new MultiSetBeanItemProperty("format", Format.class, imageBeans);
        final MultiSetBeanItemProperty<Integer> multiVerticalSpaceProp = new MultiSetBeanItemProperty("verticalSpace", Integer.class, imageBeans);
        final MultiSetBeanItemProperty<Integer> multiHorizontalSpaceProp = new MultiSetBeanItemProperty("horizontalSpace", Integer.class, imageBeans);
        final MultiSetBeanItemProperty<String> multiAlignProp = new MultiSetBeanItemProperty("align", String.class, imageBeans);
        final MultiSetBeanItemProperty<String> multiLinkUrlProp = new MultiSetBeanItemProperty("linkUrl", String.class, imageBeans);
        final MultiSetBeanItemProperty<String> multiTargetProp = new MultiSetBeanItemProperty("target", String.class, imageBeans);
        final MultiSetBeanItemProperty<Resize> multiResizeProp = new MultiSetBeanItemProperty("resize", Resize.class, imageBeans);

        if (forcedWidth > 0) {
            multiWidthProp.setValue(forcedWidth);
        } else if (maxWidth > 0) {
            multiWidthProp.setValue(maxWidth);
        }

        if (forcedHeight > 0) {
            multiHeightProp.setValue(forcedHeight);
        } else if (maxHeight > 0) {
            multiHeightProp.setValue(maxHeight);
        }

        if (maxWidth > 0 || maxHeight > 0) {
            multiResizeProp.setValue(Resize.GREATER_THAN);
        } else {
            multiResizeProp.setValue(null);
        }

        // set a default image format value if none set
        if (multiFormatProp.getValue() == null) {
            multiFormatProp.setValue(Format.PNG);
        }

        if (StringUtils.isBlank(multiTargetProp.getValue())) {
            multiTargetProp.setValue("_self");
        }


        VerticalLayout vlayout = new VerticalLayout();
        setContent(vlayout);


        Page.getCurrent().setTitle(i("image_editor.title"));


        final int rowsPerImage = 9;
        final int rowsAfterAllImages = 9;
        final int rowsIfLinkable = 3;

        int rows = images.size() * rowsPerImage + rowsAfterAllImages + (linkable ? rowsIfLinkable : 0);
        // the current row index
        int row = 0;

        final GridLayout grid = new GridLayout(2, rows);
        grid.setSpacing(true);
        grid.setMargin(true);
        vlayout.addComponent(grid);
        vlayout.setComponentAlignment(grid, Alignment.TOP_CENTER);


        String titleText = f("image_editor.sub_title", imageNo, documentId);
        Label title = getLineLabel();
        title.setValue(titleText);

        grid.addComponent(title, 0, row, 1, row);
        ++row;


        final TrimConverter trimConverter = new TrimConverter();

        // is the current iteration on the first image
        boolean firstImage = true;

        for (Entry<DocumentLanguage, BeanItem<ImageDomainObject>> entry : imageBeansByLanguage.entrySet()) {
            DocumentLanguage language = entry.getKey();
            final BeanItem<ImageDomainObject> imageBean = entry.getValue();
            final ImageDomainObject image = imageBean.getBean();

            final AbstractProperty<ImageSource> sourceProp = (AbstractProperty<ImageSource>) imageBean.getItemProperty("source");
            final AbstractProperty<CropRegion> cropRegionProp = (AbstractProperty<CropRegion>) imageBean.getItemProperty("cropRegion");
            final AbstractProperty<RotateDirection> rotateDirectionProp = (AbstractProperty<RotateDirection>) imageBean.getItemProperty("rotateDirection");

            final AbstractProperty<Integer> widthProp = (AbstractProperty<Integer>) imageBean.getItemProperty("width");
            final AbstractProperty<Integer> heightProp = (AbstractProperty<Integer>) imageBean.getItemProperty("height");
            final AbstractProperty<Integer> borderProp = (AbstractProperty<Integer>) imageBean.getItemProperty("border");
            final AbstractProperty<Format> formatProp = (AbstractProperty<Format>) imageBean.getItemProperty("format");

            final AbstractProperty<String> altTextProp = (AbstractProperty<String>) imageBean.getItemProperty("alternateText");

            // language flag
            String flagUrl = contextPath + "/imcms/images/icons/flags_iso_639_1/" + language.getCode() + ".gif";
            Image flagImage = new Image();
            flagImage.setSource(new ExternalResource(flagUrl));
            flagImage.setWidth(16, Unit.PIXELS);
            flagImage.setHeight(11, Unit.PIXELS);

            // language name
            Label langName = new Label(language.getNativeName(), ContentMode.TEXT);

            HorizontalLayout langHoriz = new HorizontalLayout(flagImage, langName);
            langHoriz.setSpacing(true);
            langHoriz.setComponentAlignment(flagImage, Alignment.MIDDLE_LEFT);

            grid.addComponent(langHoriz, 0, row);
            grid.setComponentAlignment(langHoriz, Alignment.MIDDLE_LEFT);

            // current zoom value label in percent
            String zoomText = f("image_editor.zoom_value", 100);
            final Label zoomValueLabel = new Label(zoomText, ContentMode.TEXT);

            // zoom in button
            Resource zoomInRes = new ExternalResource(contextPath + "/imcms/images/zoom_in.gif");
            final Button zoomInBtn = new Button(f("image_editor.zoom_in"));
            zoomInBtn.setData(ZoomHandler.ZoomAction.IN);
            zoomInBtn.setIcon(zoomInRes, f("image_editor.zoom_in"));
            zoomInBtn.setStyleName(BaseTheme.BUTTON_LINK + " imcms-no-underline");

            // zoom out button
            Resource zoomOutRes = new ExternalResource(contextPath + "/imcms/images/zoom_out.gif");
            final Button zoomOutBtn = new Button(f("image_editor.zoom_out"));
            zoomOutBtn.setData(ZoomHandler.ZoomAction.OUT);
            zoomOutBtn.setIcon(zoomOutRes, f("image_editor.zoom_out"));
            zoomOutBtn.setStyleName(BaseTheme.BUTTON_LINK + " imcms-no-underline");

            // zoom to actual size button (100%)
            Resource actualSizeRes = new ExternalResource(contextPath + "/imcms/images/zoom_actual_size.gif");
            final Button zoomActualSizeBtn = new Button(f("image_editor.zoom_actual_size"));
            zoomActualSizeBtn.setData(ZoomHandler.ZoomAction.ACTUAL);
            zoomActualSizeBtn.setIcon(actualSizeRes, f("image_editor.zoom_actual_size"));
            zoomActualSizeBtn.setStyleName(BaseTheme.BUTTON_LINK + " imcms-no-underline");

            // zoom to best fit button
            Resource bestFitRes = new ExternalResource(contextPath + "/imcms/images/zoom_fit.gif");
            final Button zoomBestFitBtn = new Button(f("image_editor.zoom_fit"));
            zoomBestFitBtn.setData(ZoomHandler.ZoomAction.FIT);
            zoomBestFitBtn.setIcon(bestFitRes, f("image_editor.zoom_fit"));
            zoomBestFitBtn.setStyleName(BaseTheme.BUTTON_LINK + " imcms-no-underline");
            zoomBestFitBtn.setEnabled(false);

            final HorizontalLayout zoomHoriz = new HorizontalLayout(zoomValueLabel, new Label(), zoomInBtn,
                    zoomOutBtn, zoomActualSizeBtn, zoomBestFitBtn);
            zoomHoriz.setComponentAlignment(zoomValueLabel, Alignment.MIDDLE_LEFT);
            zoomHoriz.setSpacing(true);

            grid.addComponent(zoomHoriz, 1, row);
            grid.setComponentAlignment(zoomHoriz, Alignment.MIDDLE_RIGHT);
            ++row;


            // label for the image as written in image tag (can be empty)
            Label imageLabel = new Label(imageLabelText, ContentMode.TEXT);
            grid.addComponent(imageLabel, 0, row);
            grid.setComponentAlignment(imageLabel, Alignment.MIDDLE_LEFT);

            final Image img = new Image();

            VerticalLayout imgVert = new VerticalLayout(img);
            imgVert.setComponentAlignment(img, Alignment.MIDDLE_CENTER);

            final int viewportWidth = 600;
            final int viewportHeight = 400;

            final Panel imageViewport = new Panel(imgVert);
            imageViewport.setStyleName(Reindeer.PANEL_LIGHT);
            imageViewport.setWidth(viewportWidth, Unit.PIXELS);

            final ZoomHandler zoomHandler = new ZoomHandler();
            zoomHandler.setViewportWidth(viewportWidth);
            zoomHandler.setViewportHeight(viewportHeight);

            zoomHandler.addZoomChangeListener(new ZoomHandler.ZoomChangeListener() {
                @Override
                public void zoomChanged(int zoom, int displayWidth, int displayHeight, boolean heightOverflow) {
                    zoomValueLabel.setValue(f("image_editor.zoom_value", zoom));

                    img.setWidth(displayWidth, Unit.PIXELS);
                    img.setHeight(displayHeight, Unit.PIXELS);

                    if (heightOverflow) {
                        imageViewport.setHeight(viewportHeight, Unit.PIXELS);
                    } else {
                        imageViewport.setHeight(Sizeable.SIZE_UNDEFINED, Unit.PIXELS);
                    }
                }

                @Override
                public void actionStateChanged(ZoomHandler.ZoomAction action, boolean enabled) {
                    Button button;

                    switch (action) {
                        case IN:
                            button = zoomInBtn;
                            break;
                        case OUT:
                            button = zoomOutBtn;
                            break;
                        case ACTUAL:
                            button = zoomActualSizeBtn;
                            break;
                        case FIT:
                            button = zoomBestFitBtn;
                            break;
                        default:
                            throw new IllegalStateException("Unhandled action = " + action);
                    }

                    button.setEnabled(enabled);
                }
            });

            Button.ClickListener zoomBtnListener = event -> {
                ZoomHandler.ZoomAction action = (ZoomHandler.ZoomAction) event.getButton().getData();

                zoomHandler.zoomAction(action);
            };

            zoomInBtn.addClickListener(zoomBtnListener);
            zoomOutBtn.addClickListener(zoomBtnListener);
            zoomActualSizeBtn.addClickListener(zoomBtnListener);
            zoomBestFitBtn.addClickListener(zoomBtnListener);

            grid.addComponent(imageViewport, 1, row);
            ++row;

            // URL to the image
            Label imageSrcLabel = new Label(i("image_editor.image_path"), ContentMode.TEXT);
            grid.addComponent(imageSrcLabel, 0, row);
            grid.setComponentAlignment(imageSrcLabel, Alignment.MIDDLE_LEFT);

            final TextField imageSrcText = new TextField();
            imageSrcText.setWidth(260, Unit.PIXELS);
            imageSrcText.setReadOnly(true);

            final Property.ValueChangeListener sourceValListener = event -> {
                ImageSource source = sourceProp.getValue();

                if (source.isEmpty()) {
                    img.setSource(null);
                    zoomHoriz.setVisible(false);
                } else {
                    String imageUrl = ImcmsImageUtils.getImagePreviewUrl(image, contextPath);

                    Resource resource = new ExternalResource(imageUrl);
                    ImageSize displaySize = image.getDisplayImageSize();

                    img.setSource(resource);

                    zoomHandler.setImageWidth(displaySize.getWidth());
                    zoomHandler.setImageHeight(displaySize.getHeight());
                    zoomHandler.zoomBestFit();

                    zoomHoriz.setVisible(true);
                }

                imageSrcText.setReadOnly(false);
                imageSrcText.setValue(image.getUrlPath(contextPath));
                imageSrcText.setReadOnly(true);
            };

            sourceProp.addValueChangeListener(sourceValListener);
            cropRegionProp.addValueChangeListener(sourceValListener);
            rotateDirectionProp.addValueChangeListener(sourceValListener);
            widthProp.addValueChangeListener(sourceValListener);
            heightProp.addValueChangeListener(sourceValListener);
            borderProp.addValueChangeListener(sourceValListener);
            formatProp.addValueChangeListener(sourceValListener);
            sourceValListener.valueChange(null);


            // choose image button
            Button chooseFileBtn = new Button(i("image_editor.choose_file"));
            chooseFileBtn.addClickListener(event -> {

                final ImageSelectDialog dialog = new ImageSelectDialog(i("image_editor.choose_file"));
                dialog.setWidth(600, Unit.PIXELS);
                dialog.setHeight(500, Unit.PIXELS);

                dialog.setOkButtonHandler(new AbstractFunction0() {
                    public Object apply() {
                        File file = dialog.imageSelect().selectionOpt().get();
                        if (file == null) {
                            return null;
                        }

                        File imagesRoot = imcref.getConfig().getImagePath();
                        String rootPath = imagesRoot.getAbsolutePath() + File.separatorChar;
                        String path = file.getAbsolutePath();

                        if (!path.startsWith(rootPath)) {
                            return null;
                        }

                        String imageUrl = path.substring(rootPath.length());

                        ImageInfo info = ImageOp.getImageInfo(file);
                        if (info == null) {
                            return null;
                        }

                        boolean share = shareImages.getValue();

                        Property<ImageSource> imgSourceProp = (share ? multiSourceProp : sourceProp);
                        Property<CropRegion> imgCropRegionProp = (share ? multiCropRegionProp : cropRegionProp);
                        Property<RotateDirection> imgRotateDirectionProp = (share ? multiRotateDirectionProp : rotateDirectionProp);

                        ImageSource imgSource = new ImagesPathRelativePathImageSource(imageUrl);

                        imgSourceProp.setValue(imgSource);
                        imgCropRegionProp.setValue(new CropRegion());
                        imgRotateDirectionProp.setValue(RotateDirection.NORTH);

                        dialog.close();
                        dialog.setOkButtonHandler(null);

                        return null;
                    }
                });

                UI.getCurrent().addWindow(dialog);
            });

            // choose image from archive button
            Button chooseFromImageArchiveBtn = new Button(i("image_ditor.choose_image_archive"));
            chooseFromImageArchiveBtn.setData(language);

            // TODO: implements this
            chooseFromImageArchiveBtn.addClickListener(event -> {
                Notification.show("Not implemented", Notification.Type.WARNING_MESSAGE);
            });

            // clear chosen image button
            Button clearBtn = new Button(i("btn_caption.clear"));
            clearBtn.addClickListener(event -> {
                NullImageSource nullSource = new NullImageSource();
                CropRegion nullCropRegion = new CropRegion();

                Property<ImageSource> imgSourceProp = (shareImages.getValue() ? multiSourceProp : sourceProp);
                imgSourceProp.setValue(nullSource);

                Property<CropRegion> imgCropProp = (shareImages.getValue() ? multiCropRegionProp : cropRegionProp);
                imgCropProp.setValue(nullCropRegion);

                Property<RotateDirection> imgRotateDirProp = (shareImages.getValue() ? multiRotateDirectionProp : rotateDirectionProp);
                imgRotateDirProp.setValue(RotateDirection.NORTH);
            });

            HorizontalLayout imageSrcHoriz = new HorizontalLayout(imageSrcText, chooseFileBtn, chooseFromImageArchiveBtn, clearBtn);
            imageSrcHoriz.setSpacing(true);
            grid.addComponent(imageSrcHoriz, 1, row);
            ++row;


            Label altLabel = new Label(i("image_editor.alt_text"), ContentMode.TEXT);
            grid.addComponent(altLabel, 0, row);
            grid.setComponentAlignment(altLabel, Alignment.MIDDLE_LEFT);

            TextField altText = new TextField();
            altText.setMaxLength(255);
            altText.setWidth(100, Unit.PERCENTAGE);
            altText.setConverter(trimConverter);
            altText.setPropertyDataSource(altTextProp);
            grid.addComponent(altText, 1, row);
            ++row;


            Label cropLabel = new Label(i("image_editor.cropped_image"), ContentMode.TEXT);
            grid.addComponent(cropLabel, 0, row);
            grid.setComponentAlignment(cropLabel, Alignment.MIDDLE_LEFT);

            // crop region coordinates
            final TextField cropX1Text = new TextField("X1");
            cropX1Text.setWidth(46, Unit.PIXELS);
            cropX1Text.setReadOnly(true);

            final TextField cropY1Text = new TextField("Y1");
            cropY1Text.setWidth(46, Unit.PIXELS);
            cropY1Text.setReadOnly(true);

            final TextField cropX2Text = new TextField("X2");
            cropX2Text.setWidth(46, Unit.PIXELS);
            cropX2Text.setReadOnly(true);

            final TextField cropY2Text = new TextField("Y2");
            cropY2Text.setWidth(46, Unit.PIXELS);
            cropY2Text.setReadOnly(true);

            final Button changeCropBtn = new Button(i("image_editor.change_crop"));
            changeCropBtn.addClickListener(event -> {
                CropImageWindow cropWindow = new CropImageWindow(image, !(maxWidth > 0 || maxHeight > 0));

                cropWindow.addCropSelectionListener((region, rotateDirection) -> {

                    Property<CropRegion> imgCropRegionProp = (shareImages.getValue() ? multiCropRegionProp : cropRegionProp);
                    Property<RotateDirection> imgRotateDirProp = (shareImages.getValue() ? multiRotateDirectionProp : rotateDirectionProp);

                    imgCropRegionProp.setValue(region);
                    imgRotateDirProp.setValue(rotateDirection);
                });

                UI.getCurrent().addWindow(cropWindow);
            });

            final Property.ValueChangeListener disableCropListener = event -> {
                changeCropBtn.setEnabled(!image.isEmpty());
            };

            sourceProp.addValueChangeListener(disableCropListener);
            disableCropListener.valueChange(null);


            final Button resetCropBtn = new Button(i("btn_caption.reset"));
            resetCropBtn.addClickListener(event -> {
                CropRegion emptyRegion = new CropRegion();

                Property<CropRegion> imgCropRegionProp = (shareImages.getValue() ? multiCropRegionProp : cropRegionProp);
                imgCropRegionProp.setValue(emptyRegion);

                Property<RotateDirection> imgRotateDirProp = (shareImages.getValue() ? multiRotateDirectionProp : rotateDirectionProp);
                imgRotateDirProp.setValue(RotateDirection.NORTH);
            });


            Property.ValueChangeListener cropRegionValListener = event -> {
                CropRegion cropRegion = cropRegionProp.getValue();

                if (cropRegion.isValid()) {
                    cropX1Text.setReadOnly(false);
                    cropX1Text.setVisible(true);
                    cropX1Text.setValue(Integer.toString(cropRegion.getCropX1()));
                    cropX1Text.setReadOnly(true);

                    cropY1Text.setReadOnly(false);
                    cropY1Text.setVisible(true);
                    cropY1Text.setValue(Integer.toString(cropRegion.getCropY1()));
                    cropY1Text.setReadOnly(true);

                    cropX2Text.setReadOnly(false);
                    cropX2Text.setVisible(true);
                    cropX2Text.setValue(Integer.toString(cropRegion.getCropX2()));
                    cropX2Text.setReadOnly(true);

                    cropY2Text.setReadOnly(false);
                    cropY2Text.setVisible(true);
                    cropY2Text.setValue(Integer.toString(cropRegion.getCropY2()));
                    cropY2Text.setReadOnly(true);

                    resetCropBtn.setVisible(true);

                } else {
                    cropX1Text.setVisible(false);
                    cropY1Text.setVisible(false);
                    cropX2Text.setVisible(false);
                    cropY2Text.setVisible(false);

                    resetCropBtn.setVisible(false);
                }
            };

            cropRegionProp.addValueChangeListener(cropRegionValListener);
            cropRegionValListener.valueChange(null);

            HorizontalLayout cropHoriz = new HorizontalLayout(cropX1Text, cropY1Text, cropX2Text, cropY2Text,
                    changeCropBtn, resetCropBtn);
            cropHoriz.setComponentAlignment(changeCropBtn, Alignment.BOTTOM_LEFT);
            cropHoriz.setComponentAlignment(resetCropBtn, Alignment.BOTTOM_LEFT);
            cropHoriz.setSpacing(true);
            grid.addComponent(cropHoriz, 1, row);
            ++row;


            final Label actualSizeLabel = new Label(i("image_editor.actual_size"), ContentMode.TEXT);
            grid.addComponent(actualSizeLabel, 0, row);
            grid.setComponentAlignment(actualSizeLabel, Alignment.MIDDLE_LEFT);

            final Label actualSizeValLabel = new Label("", ContentMode.TEXT);
            grid.addComponent(actualSizeValLabel, 1, row);
            grid.setComponentAlignment(actualSizeValLabel, Alignment.MIDDLE_LEFT);
            ++row;

            Property.ValueChangeListener realSizeValListener = event -> {
                if (image.isEmpty()) {
                    actualSizeValLabel.setValue(i("image_editor.not_applicable"));

                    return;
                }

                ImageSize realImageSize = image.getRealImageSize();

                actualSizeValLabel.setValue(realImageSize.getWidth() + " X " + realImageSize.getHeight());
            };

            sourceProp.addValueChangeListener(realSizeValListener);
            realSizeValListener.valueChange(null);


            final Label croppedSizeLabel = new Label(i("image_editor.cropped_size"), ContentMode.TEXT);
            grid.addComponent(croppedSizeLabel, 0, row);
            grid.setComponentAlignment(croppedSizeLabel, Alignment.MIDDLE_LEFT);

            final Label croppedSizeValLabel = new Label("", ContentMode.TEXT);
            grid.addComponent(croppedSizeValLabel, 1, row);
            grid.setComponentAlignment(croppedSizeValLabel, Alignment.MIDDLE_LEFT);
            ++row;

            Property.ValueChangeListener croppedSizeValListener = event -> {
                CropRegion region = image.getCropRegion();

                if (!region.isValid()) {
                    croppedSizeValLabel.setValue(i("image_editor.not_applicable"));

                    return;
                }

                croppedSizeValLabel.setValue(region.getWidth() + " X " + region.getHeight());
            };

            cropRegionProp.addValueChangeListener(croppedSizeValListener);
            croppedSizeValListener.valueChange(null);


            final Label displaySizeLabel = new Label(i("image_editor.display_size"), ContentMode.TEXT);
            grid.addComponent(displaySizeLabel, 0, row);
            grid.setComponentAlignment(displaySizeLabel, Alignment.MIDDLE_LEFT);

            final Label displaySizeValLabel = new Label("", ContentMode.TEXT);
            grid.addComponent(displaySizeValLabel, 1, row);
            grid.setComponentAlignment(displaySizeValLabel, Alignment.MIDDLE_LEFT);
            ++row;

            Property.ValueChangeListener displaySizeValListener = event -> {
                if (image.isEmpty()) {
                    displaySizeValLabel.setValue(i("image_editor.not_applicable"));

                    return;
                }

                ImageSize displayImageSize = image.getDisplayImageSize();

                String displaySizeText = displayImageSize.getWidth() + " X " + displayImageSize.getHeight();

                if (image.getBorder() > 0) {
                    int newWidth = displayImageSize.getWidth() + 2 * image.getBorder();
                    int newHeight = displayImageSize.getHeight() + 2 * image.getBorder();

                    displaySizeText += " " + f("image_editor.display_size_border", image.getBorder(), newWidth, newHeight);
                }

                displaySizeValLabel.setValue(displaySizeText);
            };

            sourceProp.addValueChangeListener(displaySizeValListener);
            cropRegionProp.addValueChangeListener(displaySizeValListener);
            widthProp.addValueChangeListener(displaySizeValListener);
            heightProp.addValueChangeListener(displaySizeValListener);
            borderProp.addValueChangeListener(displaySizeValListener);
            displaySizeValListener.valueChange(null);


            if (firstImage && images.size() > 1) {
                CheckBox shareImagesChk = new CheckBox(i("image_editor.all_share_images"), shareImages);
                grid.addComponent(shareImagesChk, 0, row, 1, row);
                grid.setComponentAlignment(shareImagesChk, Alignment.MIDDLE_LEFT);
                ++row;

                shareImagesChk.addValueChangeListener(event -> {
                    Boolean share = (Boolean) event.getProperty().getValue();

                    if (!share) {
                        return;
                    }

                    multiSourceProp.setValue(sourceProp.getValue());
                    multiCropRegionProp.setValue(cropRegionProp.getValue());
                    multiRotateDirectionProp.setValue(rotateDirectionProp.getValue());
                });
            }


            grid.addComponent(getLineLabel(), 0, row, 1, row);
            ++row;

            if (firstImage) {
                firstImage = false;
            }
        }


        Label imageNameLabel = new Label(i("image_editor.image_name"), ContentMode.TEXT);

        // this horizontal layout is used to give the grid's left column
        // a minimum width of 158 px
        HorizontalLayout imageNameHoriz = new HorizontalLayout(imageNameLabel);
        imageNameHoriz.setComponentAlignment(imageNameLabel, Alignment.MIDDLE_LEFT);
        imageNameHoriz.setSpacing(true);
        imageNameHoriz.setWidth(158, Unit.PIXELS);

        grid.addComponent(imageNameHoriz, 0, row);
        grid.setComponentAlignment(imageNameHoriz, Alignment.MIDDLE_LEFT);

        TextField imageNameText = new TextField();
        imageNameText.setMaxLength(ImageDomainObject.IMAGE_NAME_LENGTH);
        imageNameText.setWidth(350, Unit.PIXELS);
        imageNameText.setConverter(trimConverter);
        imageNameText.setPropertyDataSource(multiNameProp);
        grid.addComponent(imageNameText, 1, row);
        ++row;


        Label formatSizeLabel = new Label(i("image_editor.format_size"), ContentMode.TEXT);
        grid.addComponent(formatSizeLabel, 0, row);
        grid.setComponentAlignment(formatSizeLabel, Alignment.MIDDLE_LEFT);

        LenientStringToIntegerConverter intConverter = new LenientStringToIntegerConverter(0);

        TextField widthText = new TextField(i("image_editor.width"));
        widthText.setPropertyDataSource(multiWidthProp);
        widthText.setReadOnly(forcedWidth > 0 || maxWidth > 0);
        widthText.setMaxLength(4);
        widthText.setWidth(48, Unit.PIXELS);
        widthText.setImmediate(true);
        widthText.setConverter(intConverter);

        Label xLabel = new Label("X", ContentMode.TEXT);

        TextField heightText = new TextField(i("image_editor.height"));
        heightText.setPropertyDataSource(multiHeightProp);
        heightText.setReadOnly(forcedHeight > 0 || maxHeight > 0);
        heightText.setMaxLength(4);
        heightText.setWidth(48, Unit.PIXELS);
        heightText.setImmediate(true);
        heightText.setConverter(intConverter);

        if (maxWidth > 0 || maxHeight > 0) {
            widthText.setCaption(i("image_editor.max_width"));
            heightText.setCaption(i("image_editor.max_height"));
        }

        TextField borderText = new TextField(i("image_editor.border"));
        borderText.setPropertyDataSource(multiBorderProp);
        borderText.setMaxLength(4);
        borderText.setWidth(48, Unit.PIXELS);
        borderText.setImmediate(true);
        borderText.setConverter(intConverter);

        Label newSizeLabel = new Label(i("image_editor.size_explanation"), ContentMode.TEXT);

        GridLayout sizeGrid = new GridLayout(6, 2);
        sizeGrid.setSpacing(true);

        sizeGrid.addComponent(widthText, 0, 0, 0, 1);

        sizeGrid.addComponent(xLabel, 1, 1);
        sizeGrid.setComponentAlignment(xLabel, Alignment.MIDDLE_CENTER);

        sizeGrid.addComponent(heightText, 2, 0, 2, 1);

        sizeGrid.addComponent(borderText, 4, 0, 4, 1);

        sizeGrid.addComponent(newSizeLabel, 5, 1);
        sizeGrid.setComponentAlignment(newSizeLabel, Alignment.MIDDLE_LEFT);

        grid.addComponent(sizeGrid, 1, row);
        ++row;


        Label fileFormatLabel = new Label(i("image_editor.file_format"), ContentMode.TEXT);
        grid.addComponent(fileFormatLabel, 0, row);
        grid.setComponentAlignment(fileFormatLabel, Alignment.MIDDLE_LEFT);

        NativeSelect formatSelect = new NativeSelect();
        formatSelect.setImmediate(true);

        for (Format format : ALLOWED_FORMATS) {
            formatSelect.addItem(format);
        }

        formatSelect.setPropertyDataSource(multiFormatProp);
        formatSelect.setNullSelectionAllowed(false);
        grid.addComponent(formatSelect, 1, row);
        ++row;


        Label spaceAroundLabel = new Label(i("image_editor.space_around_image"), ContentMode.TEXT);
        grid.addComponent(spaceAroundLabel, 0, row);
        grid.setComponentAlignment(spaceAroundLabel, Alignment.MIDDLE_LEFT);

        TextField vspaceText = new TextField();
        vspaceText.setMaxLength(4);
        vspaceText.setWidth(48, Unit.PIXELS);
        vspaceText.setPropertyDataSource(multiVerticalSpaceProp);
        vspaceText.setConverter(intConverter);

        Label aboveBelowLabel = new Label(i("image_editor.above_below"), ContentMode.TEXT);

        TextField hspaceText = new TextField();
        hspaceText.setMaxLength(4);
        hspaceText.setWidth(48, Unit.PIXELS);
        hspaceText.setPropertyDataSource(multiHorizontalSpaceProp);
        hspaceText.setConverter(intConverter);

        Label leftRightLabel = new Label(i("image_editor.left_right"), ContentMode.TEXT);

        HorizontalLayout spaceAroundHoriz = new HorizontalLayout(vspaceText, aboveBelowLabel, hspaceText, leftRightLabel);
        spaceAroundHoriz.setSpacing(true);
        spaceAroundHoriz.setComponentAlignment(aboveBelowLabel, Alignment.MIDDLE_CENTER);
        spaceAroundHoriz.setComponentAlignment(leftRightLabel, Alignment.MIDDLE_LEFT);
        grid.addComponent(spaceAroundHoriz, 1, row);
        ++row;


        Label textAlignLabel = new Label(i("image_editor.text_alignment"), ContentMode.TEXT);
        grid.addComponent(textAlignLabel, 0, row);
        grid.setComponentAlignment(textAlignLabel, Alignment.MIDDLE_LEFT);

        NativeSelect alignSelect = new NativeSelect();
        alignSelect.setImmediate(true);
        alignSelect.addItem(ImageDomainObject.ALIGN_NONE);
        alignSelect.setItemCaption(ImageDomainObject.ALIGN_NONE, i("image_editor.align_none"));
        alignSelect.addItem(ImageDomainObject.ALIGN_TOP);
        alignSelect.setItemCaption(ImageDomainObject.ALIGN_TOP, i("image_editor.align_top"));
        alignSelect.addItem(ImageDomainObject.ALIGN_MIDDLE);
        alignSelect.setItemCaption(ImageDomainObject.ALIGN_MIDDLE, i("image_editor.align_middle"));
        alignSelect.addItem(ImageDomainObject.ALIGN_BOTTOM);
        alignSelect.setItemCaption(ImageDomainObject.ALIGN_BOTTOM, i("image_editor.align_bottom"));
        alignSelect.addItem(ImageDomainObject.ALIGN_LEFT);
        alignSelect.setItemCaption(ImageDomainObject.ALIGN_LEFT, i("image_editor.align_image_left"));
        alignSelect.addItem(ImageDomainObject.ALIGN_RIGHT);
        alignSelect.setItemCaption(ImageDomainObject.ALIGN_RIGHT, i("image_editor.align_image_right"));
        alignSelect.setNullSelectionItemId(ImageDomainObject.ALIGN_NONE);
        alignSelect.setPropertyDataSource(multiAlignProp);

        grid.addComponent(alignSelect, 1, row);
        ++row;

        final NativeSelect targetSelect = new NativeSelect();

        if (linkable) {
            Label linkedImageLabel = getLineLabel();new Label("", ContentMode.TEXT);
            linkedImageLabel.setValue(i("image_editor.linked_image"));
            grid.addComponent(linkedImageLabel, 0, row, 1, row);
            ++row;


            Label pathLabel = new Label(i("image_editor.path_url"), ContentMode.TEXT);

            final boolean linkInternal = true;
            final boolean linkExternal = false;

            final OptionGroup linkTypeGroup = new OptionGroup();
            linkTypeGroup.addItem(linkInternal);
            linkTypeGroup.setItemCaption(linkInternal, i("image_editor.internal"));
            linkTypeGroup.addItem(linkExternal);
            linkTypeGroup.setItemCaption(linkExternal, i("image_editor.external"));
            linkTypeGroup.setImmediate(true);
            linkTypeGroup.select(linkInternal);



            HorizontalLayout pathHoriz = new HorizontalLayout(pathLabel, linkTypeGroup);
            pathHoriz.setSpacing(true);
            pathHoriz.setComponentAlignment(pathLabel, Alignment.MIDDLE_LEFT);
            grid.addComponent(pathHoriz, 0, row);

            final TextField linkText = new TextField();
            linkText.setWidth(100, Unit.PERCENTAGE);
            linkText.setMaxLength(255);
            linkText.setConverter(new LinkUrlConverter());
            linkText.setPropertyDataSource(multiLinkUrlProp);
            linkText.setImmediate(true);

            Property.ValueChangeListener linkTypeValListener = event -> {
                boolean value = (Boolean) linkTypeGroup.getValue();

                if (value) {
                    linkText.setInputPrompt("meta_id");
                } else {
                    linkText.setInputPrompt("http://");
                }
            };

            linkTypeGroup.addValueChangeListener(linkTypeValListener);
            linkTypeValListener.valueChange(null);

            Property.ValueChangeListener linkUrlValListener = event -> {
                String value = multiLinkUrlProp.getValue();

                if (StringUtils.isBlank(value)) {
                    return;
                }

                boolean internal = true;

                try {
                    URI uri = new URI(value);

                    internal = !uri.isAbsolute();

                } catch (URISyntaxException ex) {
                    // ignore
                }

                linkTypeGroup.select(internal);
            };

            multiLinkUrlProp.addValueChangeListener(linkUrlValListener);
            linkUrlValListener.valueChange(null);

            grid.addComponent(linkText, 1, row);
            grid.setComponentAlignment(linkText, Alignment.MIDDLE_LEFT);
            ++row;


            Label targetLabel = new Label(i("image_editor.link_target"), ContentMode.TEXT);
            grid.addComponent(targetLabel, 0, row);
            grid.setComponentAlignment(targetLabel, Alignment.MIDDLE_LEFT);

            final String targetNone = "none";

            targetSelect.addItem(ImageDomainObject.TARGET_TOP);
            targetSelect.setItemCaption(ImageDomainObject.TARGET_TOP, i("image_editor.target_current_window"));
            targetSelect.addItem(ImageDomainObject.TARGET_BLANK);
            targetSelect.setItemCaption(ImageDomainObject.TARGET_BLANK, i("image_editor.target_new_window"));
            targetSelect.addItem(ImageDomainObject.TARGET_PARENT);
            targetSelect.setItemCaption(ImageDomainObject.TARGET_PARENT, i("image_editor.target_top_frame"));
            targetSelect.addItem(ImageDomainObject.TARGET_SELF);
            targetSelect.setItemCaption(ImageDomainObject.TARGET_SELF, i("image_editor.target_same_frame"));
            targetSelect.addItem(targetNone);
            targetSelect.setItemCaption(targetNone, i("image_editor.target_other_frame"));
            targetSelect.setNullSelectionAllowed(false);
            targetSelect.setImmediate(true);

            switch (multiTargetProp.getValue()) {
                case ImageDomainObject.TARGET_TOP:
                case ImageDomainObject.TARGET_BLANK:
                case ImageDomainObject.TARGET_PARENT:
                case ImageDomainObject.TARGET_SELF:
                    targetSelect.select(multiTargetProp.getValue());
                    break;
                default:
                    if (StringUtils.isBlank(multiTargetProp.getValue())) {
                        targetSelect.select(ImageDomainObject.TARGET_SELF);
                    } else {
                        targetSelect.select(targetNone);
                    }
                    break;
            }

            final TextField otherTargetText = new TextField();
            otherTargetText.setMaxLength(20);
            otherTargetText.setWidth(84, Unit.PIXELS);
            otherTargetText.setPropertyDataSource(multiTargetProp);

            Property.ValueChangeListener targetValListener = event -> {
                String selectedTarget = (String) targetSelect.getValue();

                if (!targetNone.equals(selectedTarget)) {
                    multiTargetProp.setValue(selectedTarget);
                    otherTargetText.setVisible(false);
                } else {
                    multiTargetProp.setValue("");
                    otherTargetText.setVisible(true);
                }
            };

            targetSelect.addValueChangeListener(targetValListener);
            targetValListener.valueChange(null);

            HorizontalLayout targetHoriz = new HorizontalLayout(targetSelect, otherTargetText);
            targetHoriz.setSpacing(true);
            grid.addComponent(targetHoriz, 1, row);
            ++row;
        }


        grid.addComponent(getLineLabel(), 0, row, 1, row);
        ++row;

        final Button.ClickListener returnListener = event -> {
            String redirectURL = returnURL;

            if (redirectURL == null) {
                redirectURL = contextPath + "/servlet/AdminDoc?meta_id=" + document.getId()
                        + "&flags=" + ImcmsConstants.DISPATCH_FLAG__EDIT_TEXT_DOCUMENT_IMAGES;
            }

            Page.getCurrent().setLocation(redirectURL);
        };

        Button saveBtn = new Button(i("btn_caption.save"));
        saveBtn.addClickListener(event -> {

            String firstGeneratedFilename = null;
            boolean first = true;

            for (ImageDomainObject image : imagesContainer.getImages().values()) {
                if (first || !shareImages.getValue()) {
                    image.generateFilename();
                    firstGeneratedFilename = image.getGeneratedFilename();

                    ImcmsImageUtils.generateImage(image, false);

                } else if (shareImages.getValue()) {
                    // share the same generated filename
                    image.setGeneratedFilename(firstGeneratedFilename);
                }

                first = false;
            }

            try {
                imcref.getDocumentMapper().saveTextDocImages(imagesContainer, user);
            } catch (NoPermissionToEditDocumentException e) {
                throw new ShouldHaveCheckedPermissionsEarlierException(e);
            } catch (NoPermissionToAddDocumentToMenuException e) {
                throw new ConcurrentDocumentModificationException(e);
            } catch (DocumentSaveException e) {
                throw new ShouldNotBeThrownException(e);
            }

            returnListener.buttonClick(null);
        });

        // set all fields to their defaults
        Button clearAllBtn = new Button(i("image_editor.clear_all"));

        clearAllBtn.addClickListener(event -> {
            multiSourceProp.setValue(new NullImageSource());
            multiCropRegionProp.setValue(new CropRegion());
            multiRotateDirectionProp.setValue(RotateDirection.NORTH);
            multiAlternateTextProp.setValue("");

            multiNameProp.setValue("");

            // don't reset width / height properties if they are read only (they shouldn't be changed by the user)
            if (!multiWidthProp.isReadOnly()) {
                multiWidthProp.setValue(0);
            }
            if (!multiHeightProp.isReadOnly()) {
                multiHeightProp.setValue(0);
            }

            multiBorderProp.setValue(0);
            multiFormatProp.setValue(Format.PNG);
            multiVerticalSpaceProp.setValue(0);
            multiHorizontalSpaceProp.setValue(0);
            multiAlignProp.setValue(ImageDomainObject.ALIGN_NONE);
            multiLinkUrlProp.setValue("");
            targetSelect.select(ImageDomainObject.TARGET_SELF);

            shareImages.setValue(Boolean.TRUE);
        });

        Button cancelBtn = new Button(i("btn_caption.cancel"));
        cancelBtn.addClickListener(returnListener);

        HorizontalLayout buttonsHoriz = new HorizontalLayout(saveBtn, clearAllBtn, cancelBtn);
        buttonsHoriz.setSpacing(true);
        grid.addComponent(buttonsHoriz, 0, row, 1, row);
        grid.setComponentAlignment(buttonsHoriz, Alignment.MIDDLE_RIGHT);
        ++row;
    }

    private static Label getLineLabel() {
        Label label = new Label();
        label.setStyleName("imcms-sep-line");

        return label;
    }

    /**
     * @return  {@code true} if there is more than one image and all images have the same image source, otherwise {@code false}
     */
    private static boolean isHasSameSource(TextDocImagesContainer imgCont) {
        if (imgCont == null || imgCont.getImages().size() < 2) {
            return false;
        }

        Iterator<ImageDomainObject> iterator = imgCont.getImages().values().iterator();
        ImageSource source = iterator.next().getSource();

        while (iterator.hasNext()) {
            ImageSource otherSource = iterator.next().getSource();

            if (source.getTypeId() != otherSource.getTypeId() ||
                    !source.getUrlPathRelativeToContextPath().equals(otherSource.getUrlPathRelativeToContextPath())) {
                return false;
            }
        }

        return true;
    }

    static boolean userHasImagePermissionsOnDocument(UserDomainObject user, TextDocumentDomainObject document) {
        TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject) user.getPermissionSetFor(document);
        return textDocumentPermissionSet.getEditImages();
    }
}
