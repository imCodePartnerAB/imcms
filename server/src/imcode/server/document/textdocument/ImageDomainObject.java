package imcode.server.document.textdocument;

import com.imcode.util.ImageSize;
import imcode.server.Imcms;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import imcode.util.image.Format;
import imcode.util.image.ImageInfo;
import imcode.util.image.Resize;
import java.io.File;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImageDomainObject implements Serializable {
    public static final int GEN_FILE_LENGTH = 255;

    private ImageSource source = new NullImageSource();

    private Integer imageIndex;
    private String name = "";
    private int width;
    private int height;
    private int border;
    private String align = "";
    private String alternateText = "";
    private String lowResolutionUrl = "";
    private int verticalSpace;
    private int horizontalSpace;
    private String target = "";
    private String linkUrl = "";
    private Long archiveImageId;
    private Format format;
    private CropRegion cropRegion = new CropRegion();
    private RotateDirection rotateDirection = RotateDirection.NORTH;
    private String generatedFilename;
    private Resize resize;

    public String getName() {
        return name;
    }

    public ImageSize getDisplayImageSize() {
        ImageSize realImageSize = getRealImageSize( );

        int wantedWidth = getWidth( );
        int wantedHeight = getHeight( );
        if ( 0 == wantedWidth && 0 != wantedHeight && 0 != realImageSize.getHeight( ) ) {
            wantedWidth = (int)( realImageSize.getWidth( ) * ( (double)wantedHeight / realImageSize.getHeight( ) ) );
        } else if ( 0 == wantedHeight && 0 != wantedWidth && 0 != realImageSize.getWidth( ) ) {
            wantedHeight = (int)( realImageSize.getHeight( ) * ( (double)wantedWidth / realImageSize.getWidth( ) ) );
        } else if ( 0 == wantedWidth && 0 == wantedHeight ) {
            wantedWidth = realImageSize.getWidth( );
            wantedHeight = realImageSize.getHeight( );
        }
        return new ImageSize( wantedWidth, wantedHeight );
    }

    public ImageSize getRealImageSize() {
        ImageSize imageSize = new ImageSize( 0, 0 );
        if ( !isEmpty( ) ) {
            try {
                imageSize = source.getImageSize( );
            } catch ( IOException ignored ) {}
        }
        return imageSize;
    }
    
    public ImageInfo getImageInfo() {
    	if (!isEmpty()) {
    		try {
    			return source.getImageInfo();
    		} catch (IOException ex) {}
    	}
    	
    	return null;
    }

    public Integer getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(Integer imageIndex) {
        this.imageIndex = imageIndex;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBorder() {
        return border;
    }

    public String getAlign() {
        return align;
    }

    public String getAlternateText() {
        return alternateText;
    }

    public String getLowResolutionUrl() {
        return lowResolutionUrl;
    }

    public int getVerticalSpace() {
        return verticalSpace;
    }

    public int getHorizontalSpace() {
        return horizontalSpace;
    }

    public String getTarget() {
        return target;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

	public Format getFormat() {
		return format;
	}
	
	public RotateDirection getRotateDirection() {
        return rotateDirection;
    }

	public void setName(String image_name) {
        this.name = image_name;
    }

    public void setWidth(int image_width) {
        this.width = image_width;
    }

    public void setHeight(int image_height) {
        this.height = image_height;
    }

    public void setBorder(int image_border) {
        this.border = image_border;
    }

    public void setAlign(String image_align) {
        this.align = image_align;
    }

    public void setAlternateText(String alt_text) {
        this.alternateText = alt_text;
    }

    public void setLowResolutionUrl(String low_scr) {
        this.lowResolutionUrl = low_scr;
    }

    public void setVerticalSpace(int v_space) {
        this.verticalSpace = v_space;
    }

    public void setHorizontalSpace(int h_space) {
        this.horizontalSpace = h_space;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setLinkUrl(String image_ref_link) {
        this.linkUrl = image_ref_link;
    }
    
    public Long getArchiveImageId() {
    	return archiveImageId;
    }
    
    public void setArchiveImageId(Long archiveImageId) {
    	this.archiveImageId = archiveImageId;
    }

    public void setFormat(Format format) {
		this.format = format;
	}
    
    public void setRotateDirection(RotateDirection rotateDirection) {
        this.rotateDirection = rotateDirection;
    }
    
	public void setSourceAndClearSize(ImageSource source) {
        setSource( source );
        setWidth( 0 );
        setHeight( 0 );
    }

    public void setSource(ImageSource source) {
        if (null == source) {
            throw new NullArgumentException("source");
        }
        this.source = source;
    }

    public boolean isEmpty() {
        return source.isEmpty( );
    }

    public String getUrlPath(String contextPath) {
        String urlPathRelativeToContextPath = getUrlPathRelativeToContextPath( );
        if ( StringUtils.isBlank( urlPathRelativeToContextPath ) ) {
            return "";
        }
        return contextPath + urlPathRelativeToContextPath;
    }

    public String getUrlPathRelativeToContextPath() {
        return source.getUrlPathRelativeToContextPath( );
    }

    public File getGeneratedFile() {
        File basePath = Imcms.getServices().getConfig().getImagePath();

        return new File(basePath, "generated/" + getGeneratedFilename());
    }

    public String getGeneratedUrlPath(String contextPath) {
        return contextPath + getGeneratedUrlPathRelativeToContextPath();
    }

    public String getGeneratedUrlPathRelativeToContextPath() {
        String imagesUrl = Imcms.getServices().getConfig().getImageUrl();

        return imagesUrl + "generated/" + getGeneratedFilename();
    }

    public String getGeneratedFilename() {
        return generatedFilename;
    }

    public void setGeneratedFilename(String generatedFilename) {
        this.generatedFilename = generatedFilename;
    }

    public void generateFilename() {
        String suffix = "_" + UUID.randomUUID().toString();

        Format fmt = getFormat();
        if (fmt != null) {
            suffix += "." + fmt.getExtension();
        }

        int maxlength = GEN_FILE_LENGTH - suffix.length();

        String filename = source.getNameWithoutExt();

        if (filename.length() > maxlength) {
            filename = filename.substring(0, maxlength);
        }
        
        String[][] specialCharacterReplacements = {
                { "\u00e5", "a" },// å
                { "\u00c5", "A" },
                { "\u00e4", "a" },// ä
                { "\u00c4", "A" },
                { "\u00f6", "o" },// ö
                { "\u00d6", "O" },
                { "\u00e9", "e" },// é
                { "\u00c9", "E" },
                { "\u00f8", "o" },// ø
                { "\u00d8", "O" },
                { "\u00e6", "ae" },// æ
                { "\u00c6", "AE" },
                { "\u0020", "_" } // space
        } ;
        for (String[] replacement : specialCharacterReplacements) {
          filename = filename.replace(replacement[0], replacement[1]) ;
        }
        
        generatedFilename = filename + suffix;
    }

    public Resize getResize() {
        return resize;
    }

    public void setResize(Resize resize) {
        this.resize = resize;
    }

    public long getSize() {
        if ( isEmpty( ) ) {
            return 0;
        }
        try {
            return source.getInputStreamSource( ).getSize( );
        } catch ( IOException e ) {
            return 0;
        }
    }

    public ImageSource getSource() {
        if ( isEmpty( ) ) {
            return new NullImageSource( );
        }
        return source;
    }

    public CropRegion getCropRegion() {
		return cropRegion;
	}

	public void setCropRegion(CropRegion cropRegion) {
		this.cropRegion = cropRegion;
	}

	public boolean equals( Object obj ) {
        if ( !( obj instanceof ImageDomainObject ) ) {
            return false;
        }
        final ImageDomainObject o = (ImageDomainObject)obj;
        CropRegion otherCropRegion = o.getCropRegion();
        return new EqualsBuilder().append(source.toStorageString(), o.getSource().toStorageString())
                .append(name, o.getName())
                .append(width, o.getWidth())
                .append(height, o.getHeight())
                .append(border, o .getBorder())
                .append(align, o.getAlign())
                .append(alternateText,o.getAlternateText())
                .append(lowResolutionUrl, o.getLowResolutionUrl())
                .append(verticalSpace, o.getVerticalSpace())
                .append(horizontalSpace, o.getHorizontalSpace())
                .append(target, o.getTarget())
                .append(linkUrl, o.getLinkUrl())
                .append(format, o.getFormat())
                .append(cropRegion.getCropX1(), otherCropRegion.getCropX1())
                .append(cropRegion.getCropY1(), otherCropRegion.getCropY1())
                .append(cropRegion.getCropX2(), otherCropRegion.getCropX2())
                .append(cropRegion.getCropY2(), otherCropRegion.getCropY2())
                .append(rotateDirection, o.getRotateDirection())
                .append(resize, o.getResize())
                .isEquals();
   }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(source.toStorageString())
                .append(name).append(width).append(height)
                .append(border).append(align).append(alternateText)
                .append(lowResolutionUrl).append(verticalSpace).append(horizontalSpace)
                .append(target).append(linkUrl).append(format)
                .append(cropRegion.getCropX1()).append(cropRegion.getCropY1())
                .append(cropRegion.getCropX2()).append(cropRegion.getCropY2())
                .append(rotateDirection)
                .append(resize)
                .toHashCode();
    }

    
    public static class CropRegion implements Serializable {
    	private static final long serialVersionUID = -586488435877347784L;
    	
    	private int cropX1;
    	private int cropY1;
    	private int cropX2;
    	private int cropY2;
    	
    	private boolean valid;
    	
    	
    	public CropRegion() {
    		cropX1 = -1;
    		cropY1 = -1;
    		cropX2 = -1;
    		cropY2 = -1;
    	}
    	
    	public CropRegion(int cropX1, int cropY1, int cropX2, int cropY2) {
    		if (cropX1 > cropX2) {
    			this.cropX1 = cropX2;
    			this.cropX2 = cropX1;
    		} else {
    			this.cropX1 = cropX1;
    			this.cropX2 = cropX2;
    		}
    		
    		if (cropY1 > cropY2) {
    			this.cropY1 = cropY2;
    			this.cropY2 = cropY1;
    		} else {
    			this.cropY1 = cropY1;
    			this.cropY2 = cropY2;
    		}
    		
    		updateValid();
    	}
    	
    	public void updateValid() {
    		valid = (cropX1 >= 0 && cropY1 >= 0 && cropX2 >= 0 && cropY2 >= 0 
					&& cropX1 != cropX2 && cropY1 != cropY2);
    	}
    	
    	public boolean isValid() {
    		return valid;
    	}
    	
    	public int getCropX1() {
    		return cropX1;
    	}

    	public void setCropX1(int cropX1) {
    		this.cropX1 = cropX1;
    	}

    	public int getCropY1() {
    		return cropY1;
    	}

    	public void setCropY1(int cropY1) {
    		this.cropY1 = cropY1;
    	}

    	public int getCropX2() {
    		return cropX2;
    	}

    	public void setCropX2(int cropX2) {
    		this.cropX2 = cropX2;
    	}

    	public int getCropY2() {
    		return cropY2;
    	}

    	public void setCropY2(int cropY2) {
    		this.cropY2 = cropY2;
    	}
    	
    	public int getWidth() {
    		return isValid() ? cropX2 - cropX1 : 0;
    	}
    	
    	public int getHeight() {
    		return isValid() ? cropY2 - cropY1 : 0;
    	}

    	
    	@Override
    	public int hashCode() {
    		final int prime = 31;
    		int result = 1;
    		result = prime * result + cropX1;
    		result = prime * result + cropY1;
    		result = prime * result + cropX2;
    		result = prime * result + cropY2;
    		
    		return result;
    	}

    	@Override
    	public boolean equals(Object obj) {
    		if (this == obj) {
    		    return true;
    		} else if (obj == null || getClass() != obj.getClass()) {
    		    return false;
    		}
    		
    		CropRegion other = (CropRegion) obj;
    		if (cropX1 != other.cropX1 || cropY1 != other.cropY1 || 
    		        cropX2 != other.cropX2 || cropY2 != other.cropY2) {
    		    return false;
    		}
    		
    		return true;
    	}
    	
    	@Override
    	public String toString() {
    		return String.format("%s(%d, %d, %d, %d)", CropRegion.class.getName(), cropX1, cropY1, cropX2, cropY2);
    	}
    }
    
    public enum RotateDirection {
        NORTH(0, -90, 90), 
        EAST(90, 0, 180), 
        SOUTH(180, 90, -90), 
        WEST(-90, 180, 0);
        
        private static final Map<Integer, RotateDirection> ANGLE_MAP = 
            new HashMap<Integer, RotateDirection>(RotateDirection.values().length);
        
        static {
            for (RotateDirection direction : RotateDirection.values()) {
                ANGLE_MAP.put(direction.getAngle(), direction);
            }
        }
        
        private final int angle;
        private final int leftAngle;
        private final int rightAngle;
        
        private RotateDirection(int angle, int leftAngle, int rightAngle) {
            this.angle = angle;
            this.leftAngle = leftAngle;
            this.rightAngle = rightAngle;
        }
        
        public int getAngle() {
            return angle;
        }
        
        public RotateDirection getLeftDirection() {
            return getByAngle(leftAngle);
        }
        
        public RotateDirection getRightDirection() {
            return getByAngle(rightAngle);
        }
        
        public static RotateDirection getByAngle(int angle) {
            return ANGLE_MAP.get(angle);
        }
        
        public boolean isDefault() {
            return this == RotateDirection.NORTH;
        }
        
        public static RotateDirection getByAngleDefaultIfNull(int angle) {
            RotateDirection direction = getByAngle(angle);
            
            return (direction != null ? direction : RotateDirection.NORTH);
        }
    }
}
