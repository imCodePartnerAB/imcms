package imcode.server.document.textdocument;

import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.util.image.Format;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity(name="ImageCache")
@Table(name="images_cache")
public class ImageCacheDomainObject implements Serializable {
	private static final long serialVersionUID = -2547384841538448930L;
	
	public static final short TYPE_PATH = 1;
	public static final short TYPE_FILE_DOCUMENT = 2;
	public static final short TYPE_URL = 3;

    @Id
    @Column(name="id", length=40, nullable=false)
	private String id;

    @Column(name="resource", length=255, nullable=false)
	private String resource;

    @Column(name="cache_type", nullable=false)
	private short type;

    @Column(name="file_size", nullable=false)
	private int fileSize;

    @Column(name="frequency", nullable=false)
	private int frequency;

    @Column(name="format", nullable=false)
    private short format;
	
    @Column(name="rotate_angle", nullable=false)
    private short rotateAngle;

    @Column(name="width", nullable=false)
	private int width;

    @Column(name="height", nullable=false)
	private int height;

    @Column(name="crop_x1", nullable=false)
    private int cropX1;

    @Column(name="crop_y1", nullable=false)
    private int cropY1;

    @Column(name="crop_x2", nullable=false)
    private int cropX2;

    @Column(name="crop_y2", nullable=false)
    private int cropY2;

    @Column(name="created_dt", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();
    
    
    public ImageCacheDomainObject() {
	}
    
	public ImageCacheDomainObject(String id, String resource,  
			short type, int fileSize, int frequency, Format format, int width, int height, 
			CropRegion cropRegion, RotateDirection rotateDirection, Timestamp createdDate) {
		this.id = id;
		this.resource = resource;
		this.type = type;
		this.fileSize = fileSize;
		this.frequency = frequency;
        setFormat(format);
		this.width = width;
		this.height = height;
        setCropRegion(cropRegion);
		setRotateDirection(rotateDirection);
		this.createdDate = createdDate;
	}
	
	public void generateId() {
		StringBuilder builder = new StringBuilder();
		builder.append(resource);
		builder.append(type);
		builder.append(format);
		builder.append(width);
		builder.append(height);

        CropRegion cropRegion = getCropRegion();
		if (cropRegion.isValid()) {
			builder.append(cropRegion.getCropX1());
			builder.append(cropRegion.getCropY1());
			builder.append(cropRegion.getCropX2());
			builder.append(cropRegion.getCropY2());
		} else {
			builder.append("-1-1-1-1");
		}

        RotateDirection rotateDirection = getRotateDirection();
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
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

    public Format getFormat() {
        return Format.findFormat(format);
    }

    public void setFormat(Format format) {
        this.format = (short) (format != null ? format.getOrdinal() : 0);
    }

    public CropRegion getCropRegion() {
        return new CropRegion(cropX1, cropY1, cropX2, cropY2);
    }

    public void setCropRegion(CropRegion region) {
        if (region.isValid()) {
            cropX1 = region.getCropX1();
            cropY1 = region.getCropY1();
            cropX2 = region.getCropX2();
            cropY2 = region.getCropY2();
        } else {
            cropX1 = -1;
            cropY1 = -1;
            cropX2 = -1;
            cropY2 = -1;
        }
    }

    public RotateDirection getRotateDirection() {
        return RotateDirection.getByAngleDefaultIfNull(rotateAngle);
    }

    public void setRotateDirection(RotateDirection dir) {
        this.rotateAngle = (short) (dir != null ? dir.getAngle() : 0);
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
