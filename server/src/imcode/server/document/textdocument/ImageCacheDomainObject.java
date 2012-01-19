package imcode.server.document.textdocument;

import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.util.image.Format;
import imcode.util.image.Resize;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ImageCacheDomainObject implements Serializable {
	private static final long serialVersionUID = -2547384841538448930L;
	
	public static final short TYPE_PATH = 1;
	public static final short TYPE_FILE_DOCUMENT = 2;
	public static final short TYPE_URL = 3;
	
	private String id;
	private String resource;
    private Integer metaId;
    private Integer no;
    private String fileNo;
	private short type;
	private int fileSize;
	private int frequency;
	private Format format;
	private int width;
	private int height;
    private Resize resize;
	CropRegion cropRegion;
	private RotateDirection rotateDirection;
    private Timestamp createdDate = new Timestamp(new Date().getTime());
    
    
    public ImageCacheDomainObject() {
	}
    
	public ImageCacheDomainObject(String id, String resource,  
			short type, int fileSize, int frequency, Format format, int width, int height, Resize resize, 
			CropRegion cropRegion, RotateDirection rotateDirection, Timestamp createdDate) {
		this.id = id;
		this.resource = resource;
		this.type = type;
		this.fileSize = fileSize;
		this.frequency = frequency;
		this.format = format;
		this.width = width;
		this.height = height;
		this.cropRegion = cropRegion;
		this.rotateDirection = rotateDirection;
		this.createdDate = createdDate;
	}
	
	public void generateId() {
		StringBuilder builder = new StringBuilder();
		builder.append(resource);
		builder.append(type);
		builder.append((format != null ? format.getOrdinal() : null));
		builder.append(width);
		builder.append(height);
        
        if (resize != null) {
            builder.append(resize.getOrdinal());
        }
		
		if (cropRegion.isValid()) {
			builder.append(cropRegion.getCropX1());
			builder.append(cropRegion.getCropY1());
			builder.append(cropRegion.getCropX2());
			builder.append(cropRegion.getCropY2());
		} else {
			builder.append("-1-1-1-1");
		}
		
		if (rotateDirection != RotateDirection.NORTH) {
		    builder.append(rotateDirection.getAngle());
		}
		
		id = DigestUtils.shaHex(builder.toString());
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

    public Resize getResize() {
        return resize;
    }

    public void setResize(Resize resize) {
        this.resize = resize;
    }

	public CropRegion getCropRegion() {
		return cropRegion;
	}

	public void setCropRegion(CropRegion cropRegion) {
		this.cropRegion = cropRegion;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public RotateDirection getRotateDirection() {
        return rotateDirection;
    }

    public void setRotateDirection(RotateDirection rotateDirection) {
        this.rotateDirection = rotateDirection;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }

    public Integer getMetaId() {
        return metaId;
    }

    public void setMetaId(Integer metaId) {
        this.metaId = metaId;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    @Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(id)
				.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ImageCacheDomainObject)) {
			return false;
		}
		
		final ImageCacheDomainObject o = (ImageCacheDomainObject) obj;
		
		return new EqualsBuilder()
				.append(id, o.getId())
				.isEquals();
	}
	
	@Override
	public String toString() {
		return String.format("imcode.server.document.textdocument.ImageCacheDomainObject" +
				"[id: %s, resource: %s, type: %d, format: %s]", id, resource, type, format);
	}
}
