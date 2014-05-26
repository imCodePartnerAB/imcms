package com.imcode.imcms.admin.docadmin.image;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles zooming logic of an image based on available viewport dimensions (width / height)
 * and the dimensions of the image.
 */
public class ZoomHandler {
    public static interface ZoomChangeListener {
        /**
         * The zoom handler has calculated a new zoom value with display width and height.
         *
         * @param zoom                  the zoom value, e.g. 50
         * @param displayImageWidth     new width value for the image, can smaller / larger than original width
         * @param displayImageHeight    new height value for the image, can smaller / larger than original height
         * @param heightOverflow        is the new {@code displayImageHeight} greater than the viewport height
         */
        void zoomChanged(int zoom, int displayImageWidth, int displayImageHeight, boolean heightOverflow);

        /**
         * One of the zoom actions should be enabled / disabled.
         *
         * @param action    the action whose state changed
         * @param enabled   true if the {@code action} should be shown in a enabled state, otherwise false
         */
        void actionStateChanged(ZoomAction action, boolean enabled);
    }

    /** A zooming action that can be performed on a {@link ZoomHandler}. */
    public static enum ZoomAction {
        /** Zoom in. Increase zoom value, e.g. 100% to 110%. */
        IN,
        /** Zoom out. Decrease zoom value, e.g. 100% to 90%. */
        OUT,
        /** Zoom at full size (100%). */
        ACTUAL,
        /** Zoom to the best value that will cover the maximum viewing area of the viewport. */
        FIT;
    }

    private static class BestFitParams {
        int zoomIndex;
        float zoom;

        private BestFitParams(int zoomIndex, float zoom) {
            this.zoomIndex = zoomIndex;
            this.zoom = zoom;
        }
    }

    /**
     * The range of zoom percent values that the zoom handler allows the user to zoom in/out between.
     * These factors do not apply to the best fit action, which calculates its own best zoom value.
     */
    private static final int ZOOM_FACTORS[] = {
            2, 5, 10, 30, 50, 67, 80, 90, 100, 110,
            120, 133, 150, 170, 200, 240, 300
    };

    private static final int ZOOM_INDEX_UNDEFINED = -1;

    private static int ZOOM_ACTUAL_INDEX = -1;


    // Width and height of the viewing area that we can work with.
    private int viewportWidth;
    private int viewportHeight;

    // True image width and height that is later scaled by this handler.
    private int imageWidth;
    private int imageHeight;

    private int zoomIndex = ZOOM_ACTUAL_INDEX;
    private float zoom = 100;

    // State flags for zoom actions, set if the corresponding zoom action is currently enabled.
    private boolean inEnabled = true;
    private boolean outEnabled = true;
    private boolean actualEnabled = true;
    private boolean bestFitEnabled = true;

    private List<ZoomChangeListener> zoomChangeListeners = new ArrayList<>();

    static {
        // Find the index of 100% zoom (actual zoom) from the zoom factors.
        for (int i = 0; i < ZOOM_FACTORS.length; ++i) {
            if (ZOOM_FACTORS[i] == 100) {
                ZOOM_ACTUAL_INDEX = i;
                break;
            }
        }

        assert ZOOM_ACTUAL_INDEX != -1;
    }

    public ZoomHandler() {
    }

    /**
     * Updates the display width and height based on the current zoom value. This method should not normally
     * be invoked directly as it's called by default when one of the zoom actions is performed.
     *
     * <p>
     * This method should be called if the image width / height or viewport width / height has changed.
     * </p>
     * <p>
     * {@link ZoomChangeListener}'s will be notified of the zoom value change.
     * </p>
     */
    public void updateZoom() {
        if (zoomIndex != ZOOM_INDEX_UNDEFINED) {
            // Find the zoom value from the zoom factors by index.
            zoomIndex = Math.max(Math.min(zoomIndex, ZOOM_FACTORS.length - 1), 0);
            zoom = ZOOM_FACTORS[zoomIndex];
        }

        float percent = zoom / 100.0f;
        int displayWidth = Math.round(imageWidth * percent);
        int displayHeight = Math.round(displayWidth / getImageRatio());
        boolean heightOverflow = (displayHeight > viewportHeight);

        triggerZoomValueChanged(Math.round(zoom), displayWidth, displayHeight, heightOverflow);
        updateActionStates();
    }

    private void updateActionStates() {
        boolean nextInEnabled = (getNextZoomInOutIndex(true) != null);

        if (inEnabled != nextInEnabled) {
            inEnabled = nextInEnabled;
            triggerActionStateChanged(ZoomAction.IN, inEnabled);
        }

        boolean nextOutEnabled = (getNextZoomInOutIndex(false) != null);

        if (outEnabled != nextOutEnabled) {
            outEnabled = nextOutEnabled;
            triggerActionStateChanged(ZoomAction.OUT, outEnabled);
        }

        boolean nextActualEnabled = (zoomIndex != ZOOM_ACTUAL_INDEX);

        if (actualEnabled != nextActualEnabled) {
            actualEnabled = nextActualEnabled;
            triggerActionStateChanged(ZoomAction.ACTUAL, actualEnabled);
        }

        boolean nextBestFitEnabled = (getBestFitParams().zoom != zoom);

        if (bestFitEnabled != nextBestFitEnabled) {
            bestFitEnabled = nextBestFitEnabled;
            triggerActionStateChanged(ZoomAction.FIT, bestFitEnabled);
        }
    }

    /**
     * Performs a zooming action, invokes one of the zooming methods that corresponds to the {@code action} parameter.
     *
     * <p>
     * {@link ZoomChangeListener}'s will be notified of the zoom value change.
     * </p>
     *
     * @param action    the zoom action to perform
     */
    public void zoomAction(ZoomAction action) {
        switch (action) {
            case IN:
                zoomIn();
                break;
            case OUT:
                zoomOut();
                break;
            case ACTUAL:
                zoomActualSize();
                break;
            case FIT:
                zoomBestFit();
                break;
            default:
                throw new IllegalStateException("Unhandled action = " + action);
        }
    }

    /**
     * Zooms in by increasing the zoom value.
     *
     * <p>
     * {@link ZoomChangeListener}'s will be notified of the zoom value change.
     * </p>
     */
    public void zoomIn() {
        zoomInOut(true);
    }

    /**
     * Zooms out by decreasing the zoom value.
     *
     * <p>
     * {@link ZoomChangeListener}'s will be notified of the zoom value change.
     * </p>
     */
    public void zoomOut() {
        zoomInOut(false);
    }

    private void zoomInOut(boolean in) {
        Integer nextIndex = getNextZoomInOutIndex(in);

        if (nextIndex == null) {
            return;
        }

        zoomIndex = nextIndex;

        updateZoom();
    }

    /**
     * Calculate the next (in) / previous (out) zoom factor index.
     *
     * @param in    zoom in or out
     * @return      The next or previous zoom factor index. Returns {@code null} if the zoom has reached an end.
     */
    private Integer getNextZoomInOutIndex(boolean in) {
        if (zoomIndex == ZOOM_INDEX_UNDEFINED) {
            // If the current zoom index is unknown (a best fit zoom was performed) find the next
            // zoom index based on the current zoom value.
            // If zooming in, traverse the factors array from start to end, otherwise from end to start.
            // If zooming in, find the next largest zoom value otherwise the next smallest zoom value
            // from the zoom factors array.

            int start = (in ? 0                   : ZOOM_FACTORS.length - 1);
            int end =   (in ? ZOOM_FACTORS.length : -1);

            int i = start;
            while (i != end) {
                if ((in && ZOOM_FACTORS[i] > zoom) ||
                        (!in && ZOOM_FACTORS[i] < zoom)) {

                    return i;
                }

                if (in) {
                    ++i;
                } else {
                    --i;
                }
            }

            return null;

        } else if (in) {
            if (zoomIndex == (ZOOM_FACTORS.length - 1)) {
                return null;
            }

            return zoomIndex + 1;

        } else {
            if (zoomIndex == 0) {
                return null;
            }

            return zoomIndex - 1;

        }
    }

    /**
     * Sets the zoom value to 100%.
     * <p>
     * {@link ZoomChangeListener}'s will be notified of the zoom value change.
     * </p>
     */
    public void zoomActualSize() {
        zoomIndex = ZOOM_ACTUAL_INDEX;
        updateZoom();
    }

    /**
     * Calculates the best zoom value at which the image will cover the largest area of the viewport,
     * but not larger than 100%.
     * <p>
     * {@link ZoomChangeListener}'s will be notified of the zoom value change.
     * </p>
     */
    public void zoomBestFit() {
        BestFitParams bestFitParams = getBestFitParams();

        zoomIndex = bestFitParams.zoomIndex;
        zoom = bestFitParams.zoom;

        updateZoom();
    }

    /**
     * Calculates the best zoom value at which the image will cover the largest area of the viewport.
     * The zoom value won't be larger than 100%.
     *
     * @return  an index into the zoom factors array (if one exists, {@code null} if it doesn't) and a zoom value
     */
    private BestFitParams getBestFitParams() {
        if ((imageWidth > viewportWidth) || (imageHeight > viewportHeight)) {
            int finalWidth;
            float imageRatio = getImageRatio();

            if (imageWidth > viewportWidth && imageHeight > viewportHeight) {
                float targetRatio = viewportWidth / (float) viewportHeight;

                if (imageRatio > targetRatio) {
                    finalWidth = viewportWidth;
                } else {
                    finalWidth = (int) Math.floor(viewportHeight * imageRatio);
                }

            } else if (imageWidth > viewportWidth) {
                finalWidth = viewportWidth;

            } else {
                finalWidth = (int) Math.floor(viewportHeight * imageRatio);

            }

            float bestZoom = (finalWidth / (float) imageWidth) * 100;

            return new BestFitParams(ZOOM_INDEX_UNDEFINED, bestZoom);

        } else {

            return new BestFitParams(ZOOM_ACTUAL_INDEX, ZOOM_FACTORS[ZOOM_ACTUAL_INDEX]);
        }
    }


    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    protected float getImageRatio() {
        return imageWidth / (float) imageHeight;
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public void setViewportWidth(int viewportWidth) {
        this.viewportWidth = viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    public void setViewportHeight(int viewportHeight) {
        this.viewportHeight = viewportHeight;
    }

    public void addZoomChangeListener(ZoomChangeListener listener) {
        zoomChangeListeners.add(listener);
    }

    public void removeZoomChangeListener(ZoomChangeListener listener) {
        zoomChangeListeners.remove(listener);
    }

    /**
     * Notify listeners of a new zoom value with display width and height.
     *
     * @param newZoom               the zoom value, e.g. 50
     * @param displayImageWidth     new width value for the image, can smaller / larger than original width
     * @param displayImageHeight    new height value for the image, can smaller / larger than original height
     * @param heightOverflow        is the new {@code displayImageHeight} greater than the viewport height
     */
    protected void triggerZoomValueChanged(int newZoom, int displayImageWidth, int displayImageHeight, boolean heightOverflow) {
        for (ZoomChangeListener listener : zoomChangeListeners) {
            listener.zoomChanged(newZoom, displayImageWidth, displayImageHeight, heightOverflow);
        }
    }

    /**
     * Notify listeners that a {@link ZoomAction} should be enabled / disabled.
     *
     * @param action    the action whose state changed
     * @param enabled   true if the {@code action} should be shown in a enabled state, otherwise false
     */
    protected void triggerActionStateChanged(ZoomAction action, boolean enabled) {
        for (ZoomChangeListener listener : zoomChangeListeners) {
            listener.actionStateChanged(action, enabled);
        }
    }
}
