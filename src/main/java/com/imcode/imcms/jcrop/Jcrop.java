package com.imcode.imcms.jcrop;

import com.imcode.imcms.jcrop.client.JcropState;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@JavaScript({
    "vaadin://addons/jcrop/js/jquery.min.js",
    "vaadin://addons/jcrop/js/jquery.color.js",
    "vaadin://addons/jcrop/js/jquery.Jcrop.min.js",
    "vaadin://addons/jcrop/js/jcrop-vaadin.js"
})
@StyleSheet({"vaadin://addons/jcrop/css/jquery.Jcrop.min.css"})
public class Jcrop extends AbstractJavaScriptComponent {

    public interface SelectionChangeListener extends Serializable {
        /**
         * The jcrop selection area has changed. It's necessary to check if the {@code area} is valid
         * by calling method {@link JcropSelectionArea#isValid()}, if it's not then the selection was reset.
         *
         * @param area  the crop selection
         */
        void selectionChanged(JcropSelectionArea area);
    }

    public interface ViewportChangeListener extends Serializable {
        /**
         * The viewing area available to the jcrop component has changed.
         *
         * @param viewportWidth     the new viewport width
         * @param viewportHeight    the new viewport height
         */
        void viewportChanged(int viewportWidth, int viewportHeight);
    }

    protected List<SelectionChangeListener> selectionListeners = new ArrayList<>();

    protected List<ViewportChangeListener> viewportChangeListeners = new ArrayList<>();

    /**
     * @param imageResource     the image to crop
     * @param imageWidth        the true width of the image
     * @param imageHeight       the true height of the image
     */
    public Jcrop(Resource imageResource, int imageWidth, int imageHeight) {
        setImageResource(imageResource);
        setImageWidth(imageWidth);
        setImageHeight(imageHeight);

        // invoked from the client side when the crop selection area has changed
        addFunction("onSelectionChanged", arguments -> {
            JSONObject cropSelArea = arguments.getJSONObject(0);

            JcropSelectionArea area = new JcropSelectionArea();
            area.setX1(cropSelArea.getInt("x1"));
            area.setY1(cropSelArea.getInt("y1"));
            area.setX2(cropSelArea.getInt("x2"));
            area.setY2(cropSelArea.getInt("y2"));

            setSelectionAreaInternal(area);
        });

        // invoked from the client side when the available viewing area has changed
        addFunction("onViewportChanged", arguments -> {
            getState().viewportWidth = arguments.getInt(0);
            getState().viewportHeight = arguments.getInt(1);

            triggerViewportChange(getState().viewportWidth, getState().viewportHeight);
        });
    }

    /**
     * Reset the crop selection.
     */
    public void reset() {
        setSelectionAreaInternal(new JcropSelectionArea());

        callFunction("reset");
    }

    /**
     * Set the cropped image.
     *
     * @param resource  the image that will be cropped
     */
    public void setImageResource(Resource resource) {
        setResource("image", resource);
    }

    /**
     * @return  the cropped image
     */
    public Resource getImageResource() {
        return getResource("image");
    }

    /**
     * @return  the true width of the image
     */
    public int getImageWidth() {
        return getState().imageWidth;
    }

    /**
     * Sets the true width of the image.
     *
     * @param imageWidth
     */
    public void setImageWidth(int imageWidth) {
        getState().imageWidth = imageWidth;
    }

    /**
     * @return  the true height of the image
     */
    public int getImageHeight() {
        return getState().imageHeight;
    }

    /**
     * Sets the true height of the image.
     *
     * @param imageHeight
     */
    public void setImageHeight(int imageHeight) {
        getState().imageHeight = imageHeight;
    }

    /**
     * @return  the width at which the image should be displayed
     */
    public int getDisplayWidth() {
        return getState().displayWidth;
    }

    /**
     * Sets the width at which the image should be displayed.
     *
     * @param displayWidth
     */
    public void setDisplayWidth(int displayWidth) {
        getState().displayWidth = displayWidth;
    }

    /**
     * @return  the height at which the image should be displayed
     */
    public int getDisplayHeight() {
        return getState().displayHeight;
    }

    /**
     * Sets the height at which the image should be displayed.
     *
     * @param displayHeight
     */
    public void setDisplayHeight(int displayHeight) {
        getState().displayHeight = displayHeight;
    }

    /**
     * @return the desired apect ratio for the crop selection area, {@code null} if the aspect ratio can
     * be arbitrary
     */
    public Double getAspectRatio() {
        return getState().aspectRatio;
    }

    /**
     * Set the desired aspect ratio for the crop selection area. If {@code null} then the crop selection
     * won't be restricted by aspect ratio.
     *
     * @param aspectRatio   the desired crop selection aspect ratio, {@code null} if one isn't desired
     */
    public void setAspectRatio(Double aspectRatio) {
        getState().aspectRatio = aspectRatio;
    }

    /**
     * @return the width of the viewing area available to jcrop
     */
    public int getViewportWidth() {
        return getState().viewportWidth;
    }

    /**
     * @return the height of the viewing area available to jcrop
     */
    public int getViewportHeight() {
        return getState().viewportHeight;
    }

    /**
     * Sets the crop selection area that should be selected.
     *
     * @param area  the crop selection area
     */
    public void setSelectionArea(JcropSelectionArea area) {
        setSelectionAreaInternal(area);

        callFunction("setSelection", area.getX1(), area.getY1(), area.getX2(), area.getY2());
    }

    protected void setSelectionAreaInternal(JcropSelectionArea area) {
        getState().selectionArea = area;

        triggerSelectionChange(area);
    }

    /**
     * @return The selected crop area. The selection should be checked if it's valid by calling method
     * {@link JcropSelectionArea#isValid()}.
     */
    public JcropSelectionArea getSelectionArea() {
        return getState().selectionArea;
    }

    @Override
    protected JcropState getState() {
        return (JcropState) super.getState();
    }

    public void addSelectionChangeListener(SelectionChangeListener listener) {
        selectionListeners.add(listener);
    }

    public void removeSelectionChangeListener(SelectionChangeListener listener) {
        selectionListeners.remove(listener);
    }

    protected void triggerSelectionChange(JcropSelectionArea area) {
        for (SelectionChangeListener listener : selectionListeners) {
            listener.selectionChanged(area);
        }
    }

    public void addViewportChangeListener(ViewportChangeListener listener) {
        viewportChangeListeners.add(listener);

        updateReportViewportChangeFlag();
    }

    public void removeViewportChangeListener(ViewportChangeListener listener) {
        viewportChangeListeners.remove(listener);

        updateReportViewportChangeFlag();
    }

    protected void updateReportViewportChangeFlag() {
        getState().reportViewportChange = !viewportChangeListeners.isEmpty();
    }

    protected void triggerViewportChange(int viewportWidth, int viewportHeight) {
        for (ViewportChangeListener listener : viewportChangeListeners) {
            listener.viewportChanged(viewportWidth, viewportHeight);
        }
    }
}
