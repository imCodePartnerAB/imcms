package com.imcode.imcms.jcrop.client;

import com.imcode.imcms.jcrop.JcropSelectionArea;
import com.vaadin.shared.ui.JavaScriptComponentState;

/**
 * The shared server/client state of the vaadin jcrop component.
 */
public class JcropState extends JavaScriptComponentState {
    public JcropSelectionArea selectionArea = new JcropSelectionArea();

    /** The true width of the cropped image. */
    public int imageWidth;

    /** The true height of the cropped image. */
    public int imageHeight;

    /** The width at which the cropped image should be displayed. */
    public int displayWidth;

    /** The height at which the cropped image should be displayed. */
    public int displayHeight;

    /**
     * The desired aspect ratio that the cropped selection area must have. Is {@code null} if
     * the ratio can be arbitrary.
     */
    public Double aspectRatio;

    /**
     * The width of the maximum viewing area that the jcrop component can work with. Starts out at {@code 0}.
     * Is reported to the server from client after the component has rendered.
     */
    public int viewportWidth;

    /**
     * The height of the maximum viewing area that the jcrop component can work with. Starts out at {@code 0}.
     * Is reported to the server from client after the component has rendered.
     */
    public int viewportHeight;

    /**
     * If the viewport dimensions have changed, should the client report to the server the new dimensions.
     */
    public boolean reportViewportChange;
}
