package com.imcode.imcms.api;

import imcode.server.ApplicationServer;
import imcode.server.document.textdocument.*;
import imcode.server.document.textdocument.ImageDomainObject;

public class Image {

    private ImageDomainObject internalImage;

    Image(ImageDomainObject internalImage) {
        this.internalImage = internalImage;
    }

    public String getName() {    // html imagetag name
        return internalImage.getName();
    }

    public String getSrcUrl() { // image srcurl relative webapp ( /imcms/images/theimage.gif )
        String result = "";
        if( !"".equals(internalImage.getUrl())){
            result =  ApplicationServer.getIMCServiceInterface().getConfig().getImageUrl() + internalImage.getUrl();
        }
        return result;
    }

    public String getSrc() { // image srcurl,  relative imageurl
        return internalImage.getUrl();
    }

    public String getLowSrc() {
        return internalImage.getLowResolutionUrl();
    }

    public int getWidth() {
        return internalImage.getWidth();
    }

    public int getHeight() {
        return internalImage.getHeight();
    }

    public int getBorder() {
        return internalImage.getBorder();
    }

    public String getAlign() {
        return internalImage.getAlign();
    }

    public String getAltText() {
        return internalImage.getAlternateText();
    }

    public int getVspace() {
        return internalImage.getVerticalSpace();
    }

    public int getHspace() {
        return internalImage.getHorizontalSpace();
    }

    public String getLinkHref() {
        return internalImage.getLinkUrl();
    }

    public String getLinkTarget() {  // use target_name if target = _other
        return internalImage.getTarget();
    }

    public void setName( String name ) {    // html imagetag name
        internalImage.setName( name );
    }

    public void setSrc( String src ) {   // image srcurl,  relative imageurl
        internalImage.setUrl( src );
    }

    public void setHeight( int height ) {
        internalImage.setHeight( height );
    }

    public void setWidth( int width ) {
        internalImage.setWidth( width );
    }

    public void setBorder( int border ) {
        internalImage.setBorder( border );
    }

    public void setVspace( int v_space ) {
        internalImage.setVerticalSpace( v_space );
    }

    public void setHspace( int h_space ) {
        internalImage.setHorizontalSpace( h_space );
    }

    public void setLinkTarget( String target ) {
        internalImage.setTarget( target );
    }

    /** @deprecated Use {@link #setLinkTarget(java.lang.String)} **/
    public void setLinkTargetName( String target_name ) {   // use this when target is set to "_other"
        setLinkTarget( target_name );
    }

    public void setAlign( String align ) {
        internalImage.setAlign( align );
    }

    public void setAltText( String alt_text ) {
        internalImage.setAlternateText( alt_text );
    }

    public void setLowSrc( String low_src ) {
        internalImage.setLowResolutionUrl( low_src );
    }

    public void setLinkHref( String link_href ) {
        internalImage.setLinkUrl( link_href );
    }
}