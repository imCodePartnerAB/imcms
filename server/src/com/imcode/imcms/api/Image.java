package com.imcode.imcms.api;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.util.ImcmsImageUtils;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * A representation of an image in imcms. Provides methods of presentation as html <img/> tag along with a multitude of
 * attributes.
 */
public class Image {

    private ImageDomainObject internalImage;
    private Integer metaId;

    Image(ImageDomainObject internalImage, Integer metaId) {
        this.internalImage = internalImage;
        this.metaId = metaId;
    }

    /**
     * Constructs an empty image.
     */
    public Image() {
        this( new ImageDomainObject(), null);
    }

    /**
     * Returns the ID of a document that this image is part of.
     * 
     * @return ID of a document
     */
    public Integer getMetaId() {
        return metaId;
    }

    /**
     * Sets the ID of a document that this image is part of.
     * 
     * @param metaId ID of a document
     */
    public void setMetaId(Integer metaId) {
        this.metaId = metaId;
    }

    /**
     * Returns the name of this image
     * @return a String with image's name
     */
    public String getName() {    // html imagetag name
        return internalImage.getName();
    }

    /**
     * Sets this image's name.
     * @param name new name
     */
    public void setName( String name ) {    // html imagetag name
        internalImage.setName( name );
    }

    /**
     * Returns this image's src url relative to context path.
     * For a full url 'http://localhost:9090/images/imCMSpower.gif', the returned string would contain '/images/imCMSpower.gif'
     * @return this image's src url relative to context path.
     */
    public String getSrcRelativeToContextPath() { // image srcurl,  relative imageurl
        return internalImage.getUrlPathRelativeToContextPath();
    }

    /**
     * Returns the url of the low resolution version of this image if there's one.
     * @return src url of the low resolution version of this image or an empty string.
     */
    public String getLowSrc() {
        return internalImage.getLowResolutionUrl();
    }

    /**
     * Sets the url of the low resolution version of this image.
     * @param low_src low resolution url.
     */
    public void setLowSrc( String low_src ) {
        internalImage.setLowResolutionUrl( low_src );
    }

    /**
     * Returns width attribute of this image.
     * @return image width
     */
    public int getWidth() {
        return internalImage.getDisplayImageSize().getWidth();
    }

    /**
     * Sets this image's width attribute
     * The underlying image is not modified, this is just one of <img/> attributes.
     * @param width new image width
     */
    public void setWidth( int width ) {
        internalImage.setWidth( width );
    }

    /**
     * Returns height attribute of this image.
     * @return image height
     */
    public int getHeight() {
        return internalImage.getDisplayImageSize().getHeight();
    }

    /**
     * Sets this image's height attribute.
     * @param height new image height
     */
    public void setHeight( int height ) {
        internalImage.setHeight( height );
    }

    /**
     * Returns border width attribute of this image.
     * @return border width in pixels
     */
    public int getBorder() {
        return internalImage.getBorder();
    }

    /**
     * Sets this image's border width attribute.
     * @param border border width in pixels
     */
    public void setBorder( int border ) {
        internalImage.setBorder( border );
    }

    /**
     * Returns this image's 'align' attribute.
     * @return 'align' attribute
     */
    public String getAlign() {
        return internalImage.getAlign();
    }

    /**
     * Sets this image's 'align' attribute.
     * @param align new value for 'align' attribute
     */
    public void setAlign( String align ) {
        internalImage.setAlign( align );
    }

    /**
     * Returns alt text of this image
     * @return alt text
     */
    public String getAltText() {
        return internalImage.getAlternateText();
    }

    /**
     * Sets alt text of this image
     * @param alt_text new alt text string
     */
    public void setAltText( String alt_text ) {
        internalImage.setAlternateText( alt_text );
    }

    /**
     * Returns 'vspace' attribute
     * @return 'vspace' attribute
     */
    public int getVspace() {
        return internalImage.getVerticalSpace();
    }

    /**
     * Sets 'vspace' attribute
     * @param v_space new 'vspace' attribute
     */
    public void setVspace( int v_space ) {
        internalImage.setVerticalSpace( v_space );
    }

    /**
     * Returns 'hspace' attribute
     * @return 'hspace' attribute
     */
    public int getHspace() {
        return internalImage.getHorizontalSpace();
    }

    /**
     * Sets 'hspace' attribute
     * @param h_space new 'hspace' attribute
     */
    public void setHspace( int h_space ) {
        internalImage.setHorizontalSpace( h_space );
    }

    /**
     * Returns the 'href' attribute of a link surrounding this image's <img/> presentation
     * @return 'href' attribute of a link surrounding this image's <img/> presentation or an empty String
     */
    public String getLinkHref() {
        return internalImage.getLinkUrl();
    }

    /**
     * Sets the url of the link surrounding(if present) this image.
     * @param link_href href attribute for a link.
     */
    public void setLinkHref( String link_href ) {
        internalImage.setLinkUrl( link_href );
    }

    /**
     * Returns the 'target' attribute of a link surrounding(if present) this image.
     * @return 'target' attribute
     */
    public String getLinkTarget() {  // use target_name if target = _other
        return internalImage.getTarget();
    }

    /**
     * Sets the 'target' attribute of a link surrounding(if present) this image.
     * @param target new 'target' attribute value
     */
    public void setLinkTarget( String target ) {
        internalImage.setTarget( target );
    }

    /**
     * Attempt to replace this image's datasource, with a new one created from the given src url.
     * Sets {@link imcode.server.document.textdocument.NullImageSource} if the argument is empty.
     * @param src relative imageurl
     */
    public void setSrc( String src ) {   // image srcurl,  relative imageurl
        ImageSource imageSource = ImcmsImageUtils.createImageSourceFromString( src ) ;
        internalImage.setSource(imageSource);
    }

    /**
     * Tests whether this image's underlying datasource's inputstream size is 0
     * @return true if there's an actual image backing this Image object, false otherwise.
     */
    public boolean isEmpty() {
        return internalImage.isEmpty() ;
    }

    /**
     * Returns this image's size in bytes.
     * @return image's size in bytes.
     */
    public long getSize() {
        return internalImage.getSize();
    }

    ImageDomainObject getInternal() {
        return internalImage;
    }

    /**
     * Returns image src url with given context path.
     * @param contextPath context path to append the image url to.
     * @return a String representing this image's url with provided context path.
     */
    public String getSrc(String contextPath) { // image srcurl relative webapp ( /imcms/images/theimage.gif )
        return internalImage.getUrlPath( contextPath ) ;
    }

    /**
     * Returns a string representing this image as html <img/> tag
     * @param contextPath context path for <img/> tag 'src' attribute's url.
     * @return a string with html image tag produced from this image
     */
    public String toHtmlUrl(String contextPath) {
        return StringEscapeUtils.escapeHtml(ImcmsImageUtils.getImageUrl(metaId, internalImage, contextPath));
    }

    /**
     * Returns a string representing the image as html img tag, adds optional attributes like id, class, usemap and style.
     * @param request a http request
     * @param attributes Not null, optional attributes such as id, class, usemap and style.
     * @param absolute true if the url in img src attribute should be absolute
     * @return a string representing the image as html img tag
     */
    public String toHtmlTag(HttpServletRequest request, Properties attributes, boolean absolute) {
        return ImcmsImageUtils.getImageHtmlTag(metaId, internalImage, request, attributes, absolute);
    }

    /**
     * Returns a string representing the image as html img tag, adds optional attributes like id, class, usemap and style.
     * The src attribute will have relative path
     * @param request a http request
     * @param attributes Not null, optional attributes such as id, class, usemap and style.
     * @return a string representing the image as html img tag
     */
    public String toHtmlTag(HttpServletRequest request, Properties attributes) {
        return toHtmlTag(request, attributes, false);
    }
}