package imcode.server.document.textdocument;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.util.ImageSize;

@Entity(name="Image")
@Table(name="imcms_text_doc_images")
public class ImageDomainObject implements Serializable, Cloneable, DocVersionItem, DocI18nItem, DocContentLoopItem {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
    
	@Transient
	private ImageSource source = new NullImageSource();
	
	@Column(name="doc_id")
	private Integer docId;
	
	@Column(name="doc_version_no")
	private Integer docVersionNo;

    /** Image no in a document.*/
    private String no = "";
	
    private int width;
    private int height;
    private int border;
    private String align = "";
    
    @Column(name="alt_text")
    private String alternateText = "";
    
    @Column(name="low_scr")
    private String lowResolutionUrl = "";
    
    @Column(name="v_space")
    private int verticalSpace;
    
    @Column(name="h_space")
    private int horizontalSpace;
    private String target = "";
    
    @Column(name="linkurl")
    private String linkUrl = "";
    
    @Column(name="imgurl")
    private String imageUrl = "";
    
    private Integer type;

    @Column(name="content_loop_no")
    private Integer contentLoopNo;

    @Column(name="content_no")
    private Integer contentNo;    
    
    /**
     * i18n support 
     */
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="language_id", referencedColumnName="id")
    private I18nLanguage language;

    public String getName() {
        return no;
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

    public void setName(String image_name) {
        this.no = image_name;
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

    @Override
    public boolean equals( Object obj ) {
        if ( !( obj instanceof ImageDomainObject ) ) {
            return false;
        }
        
        final ImageDomainObject o = (ImageDomainObject)obj;
        return new EqualsBuilder().append(source.toStorageString(), o.getSource().toStorageString())
                .append(no, o.getName())
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
                .append(language, o.getLanguage())               
                .isEquals();
   }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(source.toStorageString())
                .append(no).append(width).append(height)
                .append(border).append(align).append(alternateText)
                .append(lowResolutionUrl).append(verticalSpace).append(horizontalSpace)
                .append(target).append(linkUrl)
                .append(language)
                .toHashCode();
    }

	public I18nLanguage getLanguage() {
		return language;
	}

	public void setLanguage(I18nLanguage language) {
		this.language = language;
	}

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}
	
	public ImageDomainObject clone() {
		try {
			return (ImageDomainObject)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

    @Deprecated
	public Integer getIndex() {
		return no == null ? null : new Integer(no);
	}

    @Deprecated
	public void setIndex(Integer index) {
		if (index == null) {
			no = null;
		} else {
			no = index.toString();
		}
	}


	public Integer getNo() {
		return getIndex();
	}

	public void setNo(Integer no) {
		setIndex(no);
	}

	public Integer getDocVersionNo() {
		return docVersionNo;
	}

	public void setDocVersionNo(Integer docVersionNo) {
		this.docVersionNo = docVersionNo;
	}

    public Integer getContentLoopNo() {
        return contentLoopNo;
    }

    public void setContentLoopNo(Integer contentLoopNo) {
        this.contentLoopNo = contentLoopNo;
    }

    public Integer getContentNo() {
        return contentNo;
    }

    public void setContentNo(Integer contentNo) {
        this.contentNo = contentNo;
    }
}