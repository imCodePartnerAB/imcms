package com.imcode.imcms.api;

import imcode.server.ImageDomainObject;
import imcode.server.IMCServiceInterface;

public class Image {
    private ImageDomainObject internalImage;
    private IMCServiceInterface service;

    Image(ImageDomainObject internalImage, IMCServiceInterface service){
        this.internalImage = internalImage;
        this.service = service;
    }

    public String getName() {    // html imagetag name
		return internalImage.getImageName();
	}

    public String getSrcUrl(){ // image srcurl relative webapp ( /imcms/images/theimage.gif )
        return service.getImageUrl() + internalImage.getImageRef();
    }

    public String getSrc(){ // image srcurl,  relative imageurl
        return internalImage.getImageRef();
    }

    public String getLowSrc() {
		return internalImage.getLowScr();
	}

	public int getWidth() {
		return internalImage.getImageWidth();
	}

    public int getHeight(){
        return internalImage.getImageHeight();
    }

    public int getBorder() {
		return internalImage.getImageBorder();
	}

	public String getAlign() {
		return internalImage.getImageAlign();
	}

	public String getAltText() {
		return internalImage.getAltText();
	}

    public int getVspace() {
		return internalImage.getVerticalSpace();
	}

    public int getHspace() {
		return internalImage.getHorizontalSpace();
	}


    public String getLinkHref() {
		return internalImage.getImageRefLink();
	}

    public String getLinkTarget() {  // use target_name if target = _other
		if ("_other".equalsIgnoreCase(internalImage.getTarget())){
            return internalImage.getTargetName();
        }else{
             return internalImage.getTarget();
        }
	}

    public void setName(String name) {    // html imagetag name
		internalImage.setImageName( name );
	}

    public void setSrc(String src) {   // image srcurl,  relative imageurl
		internalImage.setImageRef( src );
	}

    public void setHeight(int height) {
		internalImage.setImageHeight( height ) ;
	}

    public void setWidth(int width) {
		internalImage.setImageWidth( width ) ;
	}

    public void setBorder(int border) {
		internalImage.setImageBorder( border ) ;
	}

    public void setVspace(int v_space) {
		internalImage.setVerticalSpace( v_space ); ;
	}

    public void setHspace(int h_space) {
		internalImage.setHorizonalSpace( h_space ); ;
	}

    public void setLinkTarget(String target) {
		internalImage.setTarget( target ); ;
	}

    public void setLinkTargetName(String target_name) {   // use this when target is set to "_other"
		internalImage.setTargetName( target_name ); ;
	}

    public void setAlign(String align) {
		internalImage.setImageAlign( align ); ;
	}

    public void setAltText(String alt_text){
        internalImage.setAltText( alt_text );
    }

    public void setLowSrc(String low_src){
        internalImage.setLowScr( low_src );
    }

    public void setLinkHref(String link_href){
        internalImage.setImageRefLink( link_href );
    }
}