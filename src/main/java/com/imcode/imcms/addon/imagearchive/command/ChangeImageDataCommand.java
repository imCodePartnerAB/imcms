package com.imcode.imcms.addon.imagearchive.command;

import com.imcode.imcms.addon.imagearchive.entity.Exif;
import com.imcode.imcms.addon.imagearchive.entity.Images;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ChangeImageDataCommand implements Serializable {
    private static final long serialVersionUID = 7628469804368247486L;
    
    private String imageNm;
    private String description;
    private String categories;
    private String keywords;
    private String imageKeywords;
    private String artist;
    private String uploadedBy;
    private String copyright;
    private String licenseDt;
    private String licenseEndDt;
    private String publishDt;
    private String archiveDt;
    private String publishEndDt;
    
    private boolean changedFile;
    private CommonsMultipartFile file;
    
    private Date licenseDate;
    private Date licenseEndDate;
    private Date publishDate;
    private Date archiveDate;
    private Date publishEndDate;
    private String altText;
    
    private List<Integer> categoryIds = new ArrayList<Integer>();
    private List<String> keywordNames = new ArrayList<String>();
    private List<String> imageKeywordNames = new ArrayList<String>();
    
    
    public ChangeImageDataCommand() {
    }

    
    public void fromImage(Images image) {
        Exif exif = image.getChangedExif();
        
        this.artist = exif.getArtist();
        this.copyright = exif.getCopyright();
        this.description = exif.getDescription();
        this.imageNm = image.getImageNm();
        this.uploadedBy = image.getUploadedBy();
        this.altText = image.getAltText();
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        if (image.getLicenseDt() != null) {
            this.licenseDt = df.format(image.getLicenseDt());
        }
        
        if (image.getLicenseEndDt() != null) {
            this.licenseEndDt = df.format(image.getLicenseEndDt());
        }
        
        if (image.getPublishDt() != null) {
            this.publishDt = df.format(image.getPublishDt());
        }
        
        if (image.getArchiveDt() != null) {
            this.archiveDt = df.format(image.getArchiveDt());
        }
        
        if (image.getPublishEndDt() != null) {
            this.publishEndDt = df.format(image.getPublishEndDt());
        }
    }
    
    public void toImage(Images image) {
        Exif exif = image.getChangedExif();
        exif.setDescription(StringUtils.trimToEmpty(description));
        exif.setArtist(StringUtils.trimToEmpty(artist));
        exif.setCopyright(StringUtils.trimToEmpty(copyright));
        
        image.setImageNm(StringUtils.trimToEmpty(imageNm));
        image.setUploadedBy(StringUtils.trimToEmpty(uploadedBy));
        image.setLicenseDt(licenseDate);
        image.setLicenseEndDt(licenseEndDate);
        image.setPublishDt(publishDate);
        image.setArchiveDt(archiveDate);
        image.setPublishEndDt(publishEndDate);
        image.setAltText(altText);
    }
    
    public String getArchiveDt() {
        return archiveDt;
    }

    public void setArchiveDt(String archiveDt) {
        this.archiveDt = archiveDt;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageNm() {
        return imageNm;
    }

    public void setImageNm(String imageNm) {
        this.imageNm = imageNm;
    }

    public String getLicenseDt() {
        return licenseDt;
    }

    public void setLicenseDt(String licenseDt) {
        this.licenseDt = licenseDt;
    }

    public String getLicenseEndDt() {
        return licenseEndDt;
    }

    public void setLicenseEndDt(String licenseEndDt) {
        this.licenseEndDt = licenseEndDt;
    }

    public String getPublishDt() {
        return publishDt;
    }

    public void setPublishDt(String publishDt) {
        this.publishDt = publishDt;
    }

    public String getPublishEndDt() {
        return publishEndDt;
    }

    public void setPublishEndDt(String publishEndDt) {
        this.publishEndDt = publishEndDt;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Date getArchiveDate() {
        return archiveDate;
    }

    public void setArchiveDate(Date archiveDate) {
        this.archiveDate = archiveDate;
    }

    public Date getLicenseDate() {
        return licenseDate;
    }

    public void setLicenseDate(Date licenseDate) {
        this.licenseDate = licenseDate;
    }

    public Date getLicenseEndDate() {
        return licenseEndDate;
    }

    public void setLicenseEndDate(Date licenseEndDate) {
        this.licenseEndDate = licenseEndDate;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getPublishEndDate() {
        return publishEndDate;
    }

    public void setPublishEndDate(Date publishEndDate) {
        this.publishEndDate = publishEndDate;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        if (categories != null) {
            String[] parts = categories.split(",");
            categoryIds = new ArrayList<Integer>(parts.length);
            
            for (String part : parts) {
                try {
                    categoryIds.add(Integer.parseInt(part, 10));
                } catch (NumberFormatException ex) {
                }
            }
        }
        
        this.categories = categories;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public String getImageKeywords() {
        return imageKeywords;
    }

    public void setImageKeywords(String imageKeywords) {
        imageKeywordNames.clear();
        imageKeywords = StringUtils.trimToNull(imageKeywords);
        if (imageKeywords != null) {
            String[] parts = imageKeywords.split("/");
            
            for (String part : parts) {
                try {
                    part = URLDecoder.decode(part, "UTF-8").trim();
                    
                    if (!StringUtils.isEmpty(part)) {
                        imageKeywordNames.add(StringUtils.substring(part.toLowerCase(), 0, 50));
                    }
                } catch (UnsupportedEncodingException ex) {
                }
            }
        }
        
        this.imageKeywords = imageKeywords;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        keywordNames.clear();
        StringUtils.trimToNull(keywords);
        if (keywords != null) {
            String[] parts = keywords.split("/");
            
            for (String part : parts) {
                try {
                    part = URLDecoder.decode(part, "UTF-8").trim();
                    
                    if (!StringUtils.isEmpty(part)) {
                        keywordNames.add(StringUtils.substring(part.toLowerCase(), 0, 50));
                    }
                } catch (UnsupportedEncodingException ex) {
                }
            }
        }
        
        this.keywords = keywords;
    }

    public List<String> getImageKeywordNames() {
        return imageKeywordNames;
    }

    public List<String> getKeywordNames() {
        return keywordNames;
    }

    public boolean isChangedFile() {
        return changedFile;
    }

    public void setChangedFile(boolean changedFile) {
        this.changedFile = changedFile;
    }

    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }
}
