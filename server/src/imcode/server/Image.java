package imcode.server ;

public class Image implements java.io.Serializable {
	private final static String CVS_REV="$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	String image_ref ;
	String image_name ;
	int image_width, image_height, image_border ;
	String image_align ;
	String alt_text ;
	String low_scr ;
	int v_space,h_space ;
	String target ;
	String target_name ;
	String image_ref_link ;



	public Image() {

	}

	public Image(String image_ref,
		String image_name,
		int image_width,
		int image_height,
		int image_border,
		String image_align,
		String alt_text,
		String low_scr,
		int v_space,
		int h_space,
		String target,
		String target_name,
		String image_ref_link) {


		this.image_ref      = image_ref ;
		this.image_name     = image_name ;
		this.image_width    = image_width ;
		this.image_height   = image_height ;
		this.image_border   = image_border ;
		this.image_align    = image_align ;
		this.alt_text       = alt_text ;
		this.low_scr        = low_scr ;
		this.v_space        = v_space ;
		this.h_space        = h_space ;
		this.target         = target ;
		this.target_name    = target_name ;
		this.image_ref_link = image_ref_link ;
	}


	public String getImageRef() {
		return image_ref ;
	}

	public String getImageName() {
		return image_name ;
	}

	public int getImageWidth() {
		return image_width ;
	}

	public int getImageHeight() {
		return image_height ;
	}

	public int getImageBorder() {
		return image_border ;
	}

	public String getImageAlign() {
		return image_align ;
	}

	public String getAltText() {
		return alt_text ;
	}

	public String getLowScr() {
		return low_scr ;
	}

	public int getVerticalSpace() {
		return v_space ;
	}

	public int getHorizontalSpace() {
		return h_space ;
	}

	public String getTarget() {
		return target ;
	}

	public String getTargetName() {
		return target_name ;
	}

	public String getImageRefLink() {
		return image_ref_link ;
	}



	public void setImageRef(String image_ref) {
		this.image_ref = image_ref ;
	}

	public void setImageName(String image_name) {
		this.image_name = image_name ;
	}

	public void setImageWidth(int image_width) {
		this.image_width = image_width ;
	}

	public void setImageHeight(int image_height) {
		this.image_height = image_height ;
	}

	public void setImageBorder(int image_border) {
		this.image_border = image_border;
	}

	public void setImageAlign(String image_align) {
		this.image_align = image_align;
	}

	public void setAltText(String alt_text) {
		this.alt_text = alt_text;
	}

	public void setLowScr(String low_scr) {
		this.low_scr = low_scr ;
	}

	public void setVerticalSpace(int v_space) {
		this.v_space = v_space ;
	}

	public void setHorizonalSpace(int h_space) {
		this.h_space  = h_space;
	}

	public void setTarget(String target) {
		this.target = target ;
	}

	public void setTargetName(String target_name) {
		this.target_name = target_name ;
	}

	public void setImageRefLink(String image_ref_link) {
		this.image_ref_link = image_ref_link ;
	}
}
