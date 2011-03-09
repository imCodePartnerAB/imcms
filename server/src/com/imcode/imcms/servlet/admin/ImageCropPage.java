package com.imcode.imcms.servlet.admin;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.image.ImageInfo;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.OkCancelPage;

public class ImageCropPage extends OkCancelPage {
	private static final long serialVersionUID = 2032206435742139836L;
	
	public static final String REQUEST_PARAMETER__ROTATE_LEFT = "rotateLeft";
	public static final String REQUEST_PARAMETER__ROTATE_RIGHT = "rotateRight";
	
	public static final String PARAM_CROP_X1 = "crop_x1";
	public static final String PARAM_CROP_Y1 = "crop_y1";
	public static final String PARAM_CROP_X2 = "crop_x2";
	public static final String PARAM_CROP_Y2 = "crop_y2";
	
	private static final Map<String, Object> CONSTANTS = Utility.getConstants(ImageCropPage.class);
	
	private Handler<CropRegion> selectRegionCommand;
	private CropRegion region;
	private ImageDomainObject image;
	private int imageWidth;
	private int imageHeight;
	private int forcedWidth;
	private int forcedHeight;

	
	public ImageCropPage(DispatchCommand okCancelCommand, Handler<CropRegion> selectRegionCommand, 
	        ImageDomainObject image, int forcedWidth, int forcedHeight) {
		super(okCancelCommand, okCancelCommand);
		
		this.selectRegionCommand = selectRegionCommand;
		this.image = image;
		this.region = image.getCropRegion();
		this.forcedWidth = forcedWidth;
		this.forcedHeight = forcedHeight;
		
		if (forcedWidth > 0) {
			image.setWidth(forcedWidth);
		}
		if (forcedHeight > 0) {
			image.setHeight(forcedHeight);
		}
		
		ImageInfo imageInfo = image.getImageInfo();
		imageWidth = imageInfo.getWidth();
		imageHeight = imageInfo.getHeight();
		
		RotateDirection rotateDirection = image.getRotateDirection();
		if (rotateDirection == RotateDirection.EAST || rotateDirection == RotateDirection.WEST) {
			exchangeImageWidthAndHeight();
		}
	}
	
	@Override
	public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("const", CONSTANTS);
		request.setAttribute("cropPage", this);
		request.setAttribute("region", region);
		request.setAttribute("image", image);
		
		request.setAttribute("imageWidth", imageWidth);
		request.setAttribute("imageHeight", imageHeight);
		request.setAttribute("forcedWidth", forcedWidth);
		request.setAttribute("forcedHeight", forcedHeight);
		
		super.forward(request, response);
	}
	
	@Override
	protected void updateFromRequest(HttpServletRequest request) {
		int cropX1 = NumberUtils.toInt(request.getParameter(PARAM_CROP_X1), -1);
		int cropY1 = NumberUtils.toInt(request.getParameter(PARAM_CROP_Y1), -1);
		int cropX2 = NumberUtils.toInt(request.getParameter(PARAM_CROP_X2), -1);
		int cropY2 = NumberUtils.toInt(request.getParameter(PARAM_CROP_Y2), -1);
		
		region = new CropRegion(cropX1, cropY1, cropX2, cropY2);
	}
	
	@Override
	protected void dispatchOther(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	    if (request.getParameter(REQUEST_PARAMETER__ROTATE_LEFT) != null) {
	        image.setRotateDirection(image.getRotateDirection().getLeftDirection());
	        rotateCropRegion(false);
	        exchangeImageWidthAndHeight();
	        
	    } else if (request.getParameter(REQUEST_PARAMETER__ROTATE_RIGHT) != null) {
	        image.setRotateDirection(image.getRotateDirection().getRightDirection());
	        rotateCropRegion(true);
	        exchangeImageWidthAndHeight();
	        
	    }
	    
		forward(request, response);
	}
	
	private void rotateCropRegion(boolean toRight) {
		if (image.getWidth() > 0 && image.getHeight() > 0) {
			return;
		}
		
		AffineTransform rotateTransform = null;
		AffineTransform translateTransform = null;
		
		if (toRight) {
			rotateTransform = AffineTransform.getRotateInstance(Math.PI / 2.0);
			translateTransform = AffineTransform.getTranslateInstance(imageHeight, 0.0);
			
		} else {
			rotateTransform = AffineTransform.getRotateInstance(- Math.PI / 2.0);
			translateTransform = AffineTransform.getTranslateInstance(0.0, imageWidth);
			
		}
		
		translateTransform.concatenate(rotateTransform);
		
		float[] src = { region.getCropX1(), region.getCropY1(), region.getCropX2(), region.getCropY2() };
		translateTransform.transform(src, 0, src, 0, src.length / 2);
		
		region = new CropRegion((int) src[0], (int) src[1], (int) src[2], (int) src[3]);
	}
	
	private void exchangeImageWidthAndHeight() {
	    int temp = imageWidth;
        imageWidth = imageHeight;
        imageHeight = temp;
	}
	
	@Override
	protected void dispatchOk(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		selectRegionCommand.handle(region);
		super.dispatchOk(request, response);
	}

	@Override
	public String getPath(HttpServletRequest request) {
		UserDomainObject user = Utility.getLoggedOnUser(request);
        return "/imcms/" + user.getLanguageIso639_2() + "/jsp/crop_img.jsp";
	}

	public CropRegion getRegion() {
		return region;
	}

	public ImageDomainObject getImage() {
		return image;
	}
}
