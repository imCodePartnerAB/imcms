package com.imcode.imcms.servlet.admin;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.image.ImageInfo;

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
	
	public static final String PARAM_CROP_X1 = "crop_x1";
	public static final String PARAM_CROP_Y1 = "crop_y1";
	public static final String PARAM_CROP_X2 = "crop_x2";
	public static final String PARAM_CROP_Y2 = "crop_y2";
	
	private static final Map<String, Object> CONSTANTS = Utility.getConstants(ImageCropPage.class);
	
	private Handler<CropRegion> selectRegionCommand;
	private CropRegion region;
	private ImageDomainObject image;
	private int forcedWidth;
	private int forcedHeight;

	
	public ImageCropPage(DispatchCommand okCancelCommand, Handler<CropRegion> selectRegionCommand, ImageDomainObject image, 
			int forcedWidth, int forcedHeight) {
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
	}
	
	@Override
	public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("const", CONSTANTS);
		request.setAttribute("cropPage", this);
		request.setAttribute("region", region);
		request.setAttribute("image", image);
		
		ImageInfo imageInfo = image.getImageInfo();
		request.setAttribute("imageWidth", imageInfo.getWidth());
		request.setAttribute("imageHeight", imageInfo.getHeight());
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
		forward(request, response);
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
