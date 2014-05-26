package com.imcode.imcms.admin.docadmin.image;

import static com.vaadin.ui.Button.ClickListener;
import static imcode.util.Utility.i;
import static imcode.util.Utility.f;
import com.imcode.imcms.jcrop.Jcrop;
import com.imcode.imcms.jcrop.JcropSelectionArea;
import com.imcode.util.ImageSize;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinServletService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.util.Utility;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class CropImageWindow extends Window {
    /**
     * Listener that is notified if a crop selection has been made.
     */
    public static interface CropSelectionListener {
        /**
         * A crop region has been selected or deselected. If it has been deselected then the {@code region}
         * won't be valid.
         * <p>
         * The {@code region} should be checked if it's valid
         * by calling method {@link imcode.server.document.textdocument.ImageDomainObject.CropRegion#isValid()}.
         * </p>
         *
         * @param region            the crop selection
         * @param rotateDirection   the rotate direction
         */
        void regionSelected(ImageDomainObject.CropRegion region, RotateDirection rotateDirection);
    }

    private static final int JCROP_RESIZE_HANDLE_HALF_SIZE = 4;

    private final ImageDomainObject image;

    private RotateDirection rotateDirection;

    private Jcrop jcrop;

    private ZoomHandler zoomHandler = new ZoomHandler();

    private boolean firstViewportSizeSet;

    protected List<CropSelectionListener> cropSelectionListeners = new ArrayList<>();


    /**
     * @param image                 the image to crop
     * @param useSizeAspectRatio    should we use the width and height that is set on the {@code image} to restrict
     *                              the aspect ratio of the crop region
     */
    public CropImageWindow(ImageDomainObject image, boolean useSizeAspectRatio) {
        this.image = image;

        // setup window properties
        setCaption(i("crop_image.title"));
        setSizeFull();
        setModal(true);
        setWindowMode(WindowMode.MAXIMIZED);
        setDraggable(false);
        setResizable(false);
        center();

        VerticalLayout vlayout = new VerticalLayout();
        vlayout.setMargin(true);
        vlayout.setSpacing(true);

        setContent(vlayout);


        rotateDirection = image.getRotateDirection();
        ImageSize realImageSize = image.getRealImageSize();

        jcrop = new Jcrop(getImageResource(), realImageSize.getWidth(), realImageSize.getHeight());
        jcrop.setStyleName("imcms-jcrop-center");

        zoomHandler.setImageWidth(realImageSize.getWidth());
        zoomHandler.setImageHeight(realImageSize.getHeight());

        // set the desired aspect ratio that a crop selection should have
        if (useSizeAspectRatio && image.getWidth() > 0 && image.getHeight() > 0) {
            jcrop.setAspectRatio(image.getWidth() / (double) image.getHeight());
        }

        // set the intial crop selection
        ImageDomainObject.CropRegion region = image.getCropRegion();
        if (region != null && region.isValid()) {
            JcropSelectionArea area = new JcropSelectionArea(region.getCropX1(), region.getCropY1(),
                    region.getCropX2(), region.getCropY2());

            jcrop.setSelectionArea(area);
        }

        if (rotateDirection == RotateDirection.EAST || rotateDirection == RotateDirection.WEST) {
            swapImageWidthWithHeight();
        }

        final CssLayout scrollContainer = new CssLayout(jcrop);
        scrollContainer.setWidth(100, Unit.PERCENTAGE);
        scrollContainer.setStyleName("imcms-jcrop-overflow");

        vlayout.addComponent(scrollContainer);


        // separator line
        Label sep = new Label();
        sep.setStyleName("imcms-sep-line");

        vlayout.addComponent(sep);

        // crop region coordinates
        final TextField cropX1Text = new TextField("X1");
        cropX1Text.setWidth(46, Unit.PIXELS);
        cropX1Text.setValue("0");
        cropX1Text.setReadOnly(true);

        final TextField cropY1Text = new TextField("Y1");
        cropY1Text.setWidth(46, Unit.PIXELS);
        cropY1Text.setValue("0");
        cropY1Text.setReadOnly(true);

        final TextField cropX2Text = new TextField("X2");
        cropX2Text.setWidth(46, Unit.PIXELS);
        cropX2Text.setValue("0");
        cropX2Text.setReadOnly(true);

        final TextField cropY2Text = new TextField("Y2");
        cropY2Text.setWidth(46, Unit.PIXELS);
        cropY2Text.setValue("0");
        cropY2Text.setReadOnly(true);

        // crop region width and height
        final TextField widthText = new TextField(i("crop_image.width"));
        widthText.setWidth(46, Unit.PIXELS);
        widthText.setValue("0");
        widthText.setReadOnly(true);

        final TextField heightText = new TextField(i("crop_image.height"));
        heightText.setWidth(46, Unit.PIXELS);
        heightText.setValue("0");
        heightText.setReadOnly(true);

        // reset crop selection button
        final Button resetBtn = new Button(i("btn_caption.reset"));

        // rotate image and crop selection buttons
        final Button rotateLeftBtn = new Button(i("crop_image.rotate_left"));
        final Button rotateRightBtn = new Button(i("crop_image.rotate_right"));

        HorizontalLayout leftHoriz = new HorizontalLayout(cropX1Text, cropY1Text, cropX2Text, cropY2Text,
                widthText, heightText, resetBtn, rotateLeftBtn, rotateRightBtn);
        leftHoriz.setSpacing(true);
        leftHoriz.setComponentAlignment(resetBtn, Alignment.BOTTOM_LEFT);
        leftHoriz.setComponentAlignment(rotateLeftBtn, Alignment.BOTTOM_LEFT);
        leftHoriz.setComponentAlignment(rotateRightBtn, Alignment.BOTTOM_LEFT);


        String contextPath = getContextPath();

        // the current zoom percent value
        final Label zoomValueLabel = new Label(f("image_editor.zoom_value", 100), ContentMode.TEXT);

        // zoom in button
        Resource zoomInRes = new ExternalResource(contextPath + "/imcms/images/zoom_in.gif");
        final Button zoomInBtn = new Button(i("image_editor.zoom_in"));
        zoomInBtn.setData(ZoomHandler.ZoomAction.IN);
        zoomInBtn.setIcon(zoomInRes, i("image_editor.zoom_in"));
        zoomInBtn.setStyleName(BaseTheme.BUTTON_LINK + " imcms-no-underline");

        // zoom out button
        Resource zoomOutRes = new ExternalResource(contextPath + "/imcms/images/zoom_out.gif");
        final Button zoomOutBtn = new Button(i("image_editor.zoom_out"));
        zoomOutBtn.setData(ZoomHandler.ZoomAction.OUT);
        zoomOutBtn.setIcon(zoomOutRes, i("image_editor.zoom_out"));
        zoomOutBtn.setStyleName(BaseTheme.BUTTON_LINK + " imcms-no-underline");

        // zoom to actual size button (100%)
        Resource actualSizeRes = new ExternalResource(contextPath + "/imcms/images/zoom_actual_size.gif");
        final Button zoomActualSizeBtn = new Button(i("image_editor.zoom_actual_size"));
        zoomActualSizeBtn.setData(ZoomHandler.ZoomAction.ACTUAL);
        zoomActualSizeBtn.setIcon(actualSizeRes, i("image_editor.zoom_actual_size"));
        zoomActualSizeBtn.setStyleName(BaseTheme.BUTTON_LINK + " imcms-no-underline");

        // zoom to best fit button
        Resource bestFitRes = new ExternalResource(contextPath + "/imcms/images/zoom_fit.gif");
        final Button zoomBestFitBtn = new Button(i("image_editor.zoom_fit"));
        zoomBestFitBtn.setData(ZoomHandler.ZoomAction.FIT);
        zoomBestFitBtn.setIcon(bestFitRes, i("image_editor.zoom_fit"));
        zoomBestFitBtn.setStyleName(BaseTheme.BUTTON_LINK + " imcms-no-underline");
        zoomBestFitBtn.setEnabled(false);

        Label spacerLabel = new Label();
        spacerLabel.setWidth(50, Unit.PIXELS);

        final HorizontalLayout zoomHoriz = new HorizontalLayout(spacerLabel, zoomValueLabel,
                new Label(), zoomInBtn, zoomOutBtn, zoomActualSizeBtn, zoomBestFitBtn);
        zoomHoriz.setComponentAlignment(zoomValueLabel, Alignment.MIDDLE_LEFT);
        zoomHoriz.setSpacing(true);

        // finish cropping and use the selected crop region
        final Button cropBtn = new Button(i("crop_image.crop"));
        // cancel cropping
        final Button cancelBtn = new Button(i("btn_caption.cancel"));

        HorizontalLayout rightHoriz = new HorizontalLayout(cropBtn, cancelBtn);
        rightHoriz.setSpacing(true);
        rightHoriz.setComponentAlignment(cropBtn, Alignment.BOTTOM_RIGHT);
        rightHoriz.setComponentAlignment(cancelBtn, Alignment.BOTTOM_RIGHT);


        HorizontalLayout parentHoriz = new HorizontalLayout(leftHoriz, zoomHoriz, rightHoriz);
        parentHoriz.setWidth(100, Unit.PERCENTAGE);
        parentHoriz.setComponentAlignment(zoomHoriz, Alignment.BOTTOM_LEFT);
        parentHoriz.setComponentAlignment(rightHoriz, Alignment.BOTTOM_RIGHT);
        parentHoriz.setExpandRatio(rightHoriz, 1);

        vlayout.addComponent(parentHoriz);
        vlayout.setComponentAlignment(parentHoriz, Alignment.MIDDLE_LEFT);


        jcrop.addViewportChangeListener((viewportWidth, viewportHeight) -> {
            int workableViewportWidth = Math.max(viewportWidth - 2 * JCROP_RESIZE_HANDLE_HALF_SIZE, 0);
            int workableViewportHeight = Math.max(viewportHeight - 2 * JCROP_RESIZE_HANDLE_HALF_SIZE, 0);

            zoomHandler.setViewportWidth(workableViewportWidth);
            zoomHandler.setViewportHeight(workableViewportHeight);

            scrollContainer.setWidth(viewportWidth, Unit.PIXELS);
            scrollContainer.setHeight(viewportHeight, Unit.PIXELS);

            if (!firstViewportSizeSet) {
                firstViewportSizeSet = true;

                zoomHandler.zoomBestFit();
            }
        });

        Jcrop.SelectionChangeListener selectionListener = area -> {
            area = jcrop.getSelectionArea();

            boolean valid = area.isValid();

            cropX1Text.setReadOnly(false);
            cropX1Text.setValue(Integer.toString(valid ? area.getX1() : 0));
            cropX1Text.setReadOnly(true);

            cropY1Text.setReadOnly(false);
            cropY1Text.setValue(Integer.toString(valid ? area.getY1() : 0));
            cropY1Text.setReadOnly(true);

            cropX2Text.setReadOnly(false);
            cropX2Text.setValue(Integer.toString(valid ? area.getX2() : 0));
            cropX2Text.setReadOnly(true);

            cropY2Text.setReadOnly(false);
            cropY2Text.setValue(Integer.toString(valid ? area.getY2() : 0));
            cropY2Text.setReadOnly(true);

            widthText.setReadOnly(false);
            widthText.setValue(Integer.toString(valid ? area.getWidth() : 0));
            widthText.setReadOnly(true);

            heightText.setReadOnly(false);
            heightText.setValue(Integer.toString(valid ? area.getHeight() : 0));
            heightText.setReadOnly(true);
        };

        jcrop.addSelectionChangeListener(selectionListener);
        selectionListener.selectionChanged(null);

        resetBtn.addClickListener(event -> {
            jcrop.reset();
        });

        ClickListener rotateListener = event -> {
            rotateImage(event.getButton() == rotateRightBtn);
        };

        rotateLeftBtn.addClickListener(rotateListener);
        rotateRightBtn.addClickListener(rotateListener);

        zoomHandler.addZoomChangeListener(new ZoomHandler.ZoomChangeListener() {
            @Override
            public void zoomChanged(int zoom, int displayImageWidth, int displayImageHeight, boolean heightOverflow) {

                zoomValueLabel.setValue(f("image_editor.zoom_value", zoom));

                // display the cropped image with new width / height based on the zoom value
                jcrop.setDisplayWidth(displayImageWidth);
                jcrop.setDisplayHeight(displayImageHeight);

                // set a fixed size width and height for the jcrop element so it would be centered in the middle
                jcrop.setWidth(displayImageWidth + JCROP_RESIZE_HANDLE_HALF_SIZE + 1, Unit.PIXELS);
                jcrop.setHeight(displayImageHeight + JCROP_RESIZE_HANDLE_HALF_SIZE + 1, Unit.PIXELS);
            }

            @Override
            public void actionStateChanged(ZoomHandler.ZoomAction action, boolean enabled) {
                // enable / disable zoom button

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

        // handle zoom button presses
        ClickListener zoomBtnListener = event -> {
            ZoomHandler.ZoomAction action = (ZoomHandler.ZoomAction) event.getButton().getData();

            zoomHandler.zoomAction(action);
        };

        zoomInBtn.addClickListener(zoomBtnListener);
        zoomOutBtn.addClickListener(zoomBtnListener);
        zoomActualSizeBtn.addClickListener(zoomBtnListener);
        zoomBestFitBtn.addClickListener(zoomBtnListener);

        cropBtn.addClickListener(event -> {
            JcropSelectionArea area = jcrop.getSelectionArea();

            ImageDomainObject.CropRegion selectedRegion;

            if (area.isValid()) {
                selectedRegion = new ImageDomainObject.CropRegion(area.getX1(), area.getY1(), area.getX2(), area.getY2());
            } else {
                selectedRegion = new ImageDomainObject.CropRegion(0, 0, 0, 0);
            }

            try {
                triggerCropRegionSelected(selectedRegion, rotateDirection);
            } finally {
                close();
            }
        });

        cancelBtn.addClickListener(event -> {
            close();
        });
    }

    private String getContextPath() {
        return VaadinServletService.getCurrentServletRequest().getContextPath();
    }

    private Resource getImageResource() {
        String url = getContextPath() + "/servlet/ImagePreview?path=" + Utility.encodeUrl(image.getUrlPathRelativeToContextPath());
        url += "&rangle=" + rotateDirection.getAngle();

        return new ExternalResource(url);
    }

    private void rotateImage(boolean toRight) {
        rotateDirection = (toRight ? rotateDirection.getRightDirection() : rotateDirection.getLeftDirection());

        jcrop.setImageResource(getImageResource());

        JcropSelectionArea area = jcrop.getSelectionArea();

        if (!area.isValid()) {
            // skip rotating the selection area as it's not valid
            swapImageWidthWithHeight();
            return;
        }

        // skip rotating the selection area if the area must have a specific aspect ratio (as given by width / height)
        if (image.getWidth() > 0 && image.getHeight() > 0) {
            swapImageWidthWithHeight();
            return;
        }

        // Rotate the crop region

        AffineTransform rotateTransform;
        AffineTransform translateTransform;

        if (toRight) {
            rotateTransform = AffineTransform.getRotateInstance(Math.PI / 2.0);
            translateTransform = AffineTransform.getTranslateInstance(jcrop.getImageHeight(), 0.0);

        } else {
            rotateTransform = AffineTransform.getRotateInstance(- Math.PI / 2.0);
            translateTransform = AffineTransform.getTranslateInstance(0.0, jcrop.getImageWidth());

        }

        // rotate then translate
        translateTransform.concatenate(rotateTransform);

        float[] src = { area.getX1(), area.getY1(), area.getX2(), area.getY2() };
        translateTransform.transform(src, 0, src, 0, src.length / 2);

        JcropSelectionArea transformedArea = new JcropSelectionArea((int) src[0], (int) src[1], (int) src[2], (int) src[3]);
        jcrop.setSelectionArea(transformedArea);

        swapImageWidthWithHeight();
    }

    private void swapImageWidthWithHeight() {
        int temp = jcrop.getImageWidth();
        jcrop.setImageWidth(jcrop.getImageHeight());
        jcrop.setImageHeight(temp);

        zoomHandler.setImageWidth(jcrop.getImageWidth());
        zoomHandler.setImageHeight(jcrop.getImageHeight());
        zoomHandler.updateZoom();
    }

    public void addCropSelectionListener(CropSelectionListener listener) {
        cropSelectionListeners.add(listener);
    }

    public void removeCropSelectionListener(CropSelectionListener listener) {
        cropSelectionListeners.remove(listener);
    }

    protected void triggerCropRegionSelected(ImageDomainObject.CropRegion region, RotateDirection rotateDirection) {
        for (CropSelectionListener listener : cropSelectionListeners) {
            listener.regionSelected(region, rotateDirection);
        }
    }
}
