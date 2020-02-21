package com.imcode.imcms.api;

import com.imcode.imcms.model.SpaceAround;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.util.ImcmsImageUtils;
import org.apache.commons.text.StringEscapeUtils;

public class Image {

    private ImageDomainObject internalImage;

    Image(ImageDomainObject internalImage) {
        this.internalImage = internalImage;
    }

    public Image() {
        this(new ImageDomainObject());
    }

    public String getName() {    // html imagetag name
        return internalImage.getName();
    }

    public void setName(String name) {    // html imagetag name
        internalImage.setName(name);
    }

    public int getWidth() {
        return internalImage.getDisplayImageSize().getWidth();
    }

    public void setWidth(int width) {
        internalImage.setWidth(width);
    }

    public int getHeight() {
        return internalImage.getDisplayImageSize().getHeight();
    }

    public void setHeight(int height) {
        internalImage.setHeight(height);
    }

    public int getBorder() {
        return internalImage.getBorder();
    }

    public void setBorder(int border) {
        internalImage.setBorder(border);
    }

    public String getAlign() {
        return internalImage.getAlign();
    }

    public void setAlign(String align) {
        internalImage.setAlign(align);
    }

    public String getAltText() {
        return internalImage.getAlternateText();
    }

    public void setAltText(String alt_text) {
        internalImage.setAlternateText(alt_text);
    }

    public SpaceAround getSpaceAround() {
        return internalImage.getSpaceAround();
    }

    public void setSpaceAround(SpaceAround spaceAround) {
        internalImage.setSpaceAround(spaceAround);
    }

    public String getLinkHref() {
        return internalImage.getLinkUrl();
    }

    @SuppressWarnings("unused")
    public void setLinkHref(String linkHref) {
        internalImage.setLinkUrl(linkHref);
    }

    public String getLinkTarget() {  // use target_name if target = _other
        return internalImage.getTarget();
    }

    @SuppressWarnings("unused")
    public void setLinkTarget(String linkTarget) {
        internalImage.setTarget(linkTarget);
    }

    @SuppressWarnings("unused")
    public String getSrcRelativeToContextPath() {
        return internalImage.getUrlPathRelativeToContextPath();
    }

    @Deprecated
    @SuppressWarnings("unused")
    public void setLowSrc(String lowSrc) {
        this.internalImage.setLowResolutionUrl(lowSrc);
    }

    public void setSrc(String src) {   // image srcurl,  relative imageurl
        ImageSource imageSource = ImcmsImageUtils.createImageSourceFromString(src);
        internalImage.setSource(imageSource);
    }

    public boolean isEmpty() {
        return internalImage.isEmpty();
    }

    public String getSize() {
        return internalImage.getSize();
    }

    ImageDomainObject getInternal() {
        return internalImage;
    }

    public String getSrc(String contextPath) { // image srcurl relative webapp ( /imcms/images/theimage.gif )
        return internalImage.getUrlPath(contextPath);
    }

    public String toHtmlUrl(String contextPath) {
        return StringEscapeUtils.escapeHtml4(ImcmsImageUtils.getImageUrl(internalImage, contextPath));
    }
}