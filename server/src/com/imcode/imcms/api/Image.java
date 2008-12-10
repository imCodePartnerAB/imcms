package com.imcode.imcms.api;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.util.ImcmsImageUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

public class Image {

    private ImageDomainObject internalImage;

    Image(ImageDomainObject internalImage) {
        this.internalImage = internalImage;
    }

    public Image() {
        this( new ImageDomainObject() );
    }

    public String getName() {    // html imagetag name
        return internalImage.getName();
    }

    public void setName( String name ) {    // html imagetag name
        internalImage.setName( name );
    }

    public String getSrcRelativeToContextPath() { // image srcurl,  relative imageurl
        return internalImage.getUrlPathRelativeToContextPath();
    }

    public String getLowSrc() {
        return internalImage.getLowResolutionUrl();
    }

    public void setLowSrc( String low_src ) {
        internalImage.setLowResolutionUrl( low_src );
    }

    public int getWidth() {
        return internalImage.getDisplayImageSize().getWidth();
    }

    public void setWidth( int width ) {
        internalImage.setWidth( width );
    }

    public int getHeight() {
        return internalImage.getDisplayImageSize().getHeight();
    }

    public void setHeight( int height ) {
        internalImage.setHeight( height );
    }

    public int getBorder() {
        return internalImage.getBorder();
    }

    public void setBorder( int border ) {
        internalImage.setBorder( border );
    }

    public String getAlign() {
        return internalImage.getAlign();
    }

    public void setAlign( String align ) {
        internalImage.setAlign( align );
    }

    public String getAltText() {
        return internalImage.getAlternateText();
    }

    public void setAltText( String alt_text ) {
        internalImage.setAlternateText( alt_text );
    }

    public int getVspace() {
        return internalImage.getVerticalSpace();
    }

    public void setVspace( int v_space ) {
        internalImage.setVerticalSpace( v_space );
    }

    public int getHspace() {
        return internalImage.getHorizontalSpace();
    }

    public void setHspace( int h_space ) {
        internalImage.setHorizontalSpace( h_space );
    }

    public String getLinkHref() {
        return internalImage.getLinkUrl();
    }

    public void setLinkHref( String link_href ) {
        internalImage.setLinkUrl( link_href );
    }

    public String getLinkTarget() {  // use target_name if target = _other
        return internalImage.getTarget();
    }

    public void setLinkTarget( String target ) {
        internalImage.setTarget( target );
    }

    public void setSrc( String src ) {   // image srcurl,  relative imageurl
        ImageSource imageSource = ImcmsImageUtils.createImageSourceFromString( src ) ;
        internalImage.setSource( imageSource );
    }

    public boolean isEmpty() {
        return internalImage.isEmpty() ;
    }

    public long getSize() {
        return internalImage.getSize();
    }

    ImageDomainObject getInternal() {
        return internalImage;
    }

    public String getSrc(String contextPath) { // image srcurl relative webapp ( /imcms/images/theimage.gif )
        return internalImage.getUrlPath( contextPath ) ;
    }

    public String toHtmlTag(HttpServletRequest request, Properties attributes, boolean absolute) {
        return ImcmsImageUtils.getImageHtmlTag(internalImage, request, attributes, absolute);
    }

    public String toHtmlTag(HttpServletRequest request, Properties attributes) {
        return toHtmlTag(request, attributes, false);
    }
}