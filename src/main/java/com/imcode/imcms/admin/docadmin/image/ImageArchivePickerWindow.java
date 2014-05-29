package com.imcode.imcms.admin.docadmin.image;

import com.imcode.imcms.addon.imagearchive.util.SessionUtils;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinServletService;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static imcode.util.Utility.i;

/**
 * Select an image from image archive. Displays an iframe inside a popup window.
 */
public class ImageArchivePickerWindow extends Window {
    public static interface ImagePickedListener {
        public void imagePicked(long imageId, String imageName, String imageFilename, String imageAltText);
    }

    private static final String PICKED_JS_FUNC = "imagePicked";

    protected List<ImagePickedListener> pickedListeners = new ArrayList<>();

    public ImageArchivePickerWindow() {
        setCaption(i("image_archive_picker.title"));
        setSizeFull();
        setModal(true);
        setWindowMode(WindowMode.MAXIMIZED);
        setDraggable(false);
        setResizable(false);
        center();

        String contextPath = VaadinServletService.getCurrentServletRequest().getContextPath();

        ExternalResource resource = new ExternalResource(contextPath + "/web/archive");

        BrowserFrame frame = new BrowserFrame();
        frame.setSource(resource);
        frame.setSizeFull();

        setContent(frame);

        SessionUtils.setTransferToPicker(getHttpSession());

        addCloseListener(closeEvent -> {
            SessionUtils.removeTransferToPicker(getHttpSession());

            JavaScript.getCurrent().removeFunction(PICKED_JS_FUNC);
        });

        // This function will be invoked from inside the image archive iframe once an image has been selected.
        JavaScript.getCurrent().addFunction(PICKED_JS_FUNC, jsonArray -> {
            close();

            long imageId = jsonArray.getLong(0);
            String imageName = jsonArray.getString(1);
            String imageFilename = FilenameUtils.getName(jsonArray.getString(2));
            String imageAltText = jsonArray.getString(3);

            triggerImagePicked(imageId, imageName, imageFilename, imageAltText);
        });
    }

    private static HttpSession getHttpSession() {
        return VaadinServletService.getCurrentServletRequest().getSession();
    }

    protected void triggerImagePicked(long imageId, String imageName, String imageFilename, String imageAltText) {
        for (ImagePickedListener listener : pickedListeners) {
            listener.imagePicked(imageId, imageName, imageFilename, imageAltText);
        }
    }

    public void addImagePickedListener(ImagePickedListener listener) {
        pickedListeners.add(listener);
    }

    public void removeImagePickedListener(ImagePickedListener listener) {
        pickedListeners.remove(listener);
    }
}
