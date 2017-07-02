package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.servlet.ImageCacheManager;
import imcode.server.Imcms;
import imcode.server.document.DirectDocumentReference;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.*;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.util.image.Format;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class ImageCacheMapper {
    private static final Logger log = Logger.getLogger(ImageCacheMapper.class);
    
	private Database database;
	
	private static final String SQL__UPDATE_FREQUENCY = "UPDATE images_cache SET frequency = frequency + 1 WHERE " +
			"id = ? AND meta_id <= 0";
	
	public ImageCacheMapper(Database database) {
		this.database = database;
	}
	
	public void deleteDocumentImagesCache(int metaId, Map<Integer, ImageDomainObject> images) {
		Set<String> cacheIds = new HashSet<String>();
		
		for (int imageIndex : images.keySet()) {
			ImageDomainObject image = images.get(imageIndex);
			
			if (image.isEmpty()) {
				continue;
			}
			
			ImageCacheDomainObject imageCache = new ImageCacheDomainObject();
			imageCache.setMetaId(metaId);
			imageCache.setImageIndex(imageIndex);
			imageCache.setWidth(image.getWidth());
			imageCache.setHeight(image.getHeight());
			imageCache.setFormat(image.getFormat());
			imageCache.setCropRegion(image.getCropRegion());
			imageCache.setRotateDirection(image.getRotateDirection());
			
			ImageSource source = image.getSource();
			if (source instanceof FileDocumentImageSource) {
				FileDocumentImageSource fileDocSource = (FileDocumentImageSource) source;
				imageCache.setResource(Integer.toString(fileDocSource.getFileDocument().getId()));
				imageCache.setType(ImageCacheDomainObject.TYPE_FILE_DOCUMENT);
			} else {
				imageCache.setResource(image.getUrlPathRelativeToContextPath());
				imageCache.setType(ImageCacheDomainObject.TYPE_PATH);
			}
			imageCache.generateId();
			
			cacheIds.add(imageCache.getId());
		}
		
		if (cacheIds.isEmpty()) {
			return;
		}
		
		database.execute(new SqlUpdateCommand(getDeleteDocumentImagesCacheSQL(cacheIds), new Object[] { metaId }));
		
		String existingSQL = getExistingDocumentCacheIdsSQL(cacheIds);
		
		Set<String> existingIds = (Set<String>) database.execute(new SqlQueryCommand(existingSQL, null, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				Set<String> ids = new HashSet<String>();
				
				while (rs.next()) {
					ids.add(rs.getString(1));
				}
				
				return ids;
			}
		}));
		
		cacheIds.removeAll(existingIds);
		
		for (String cacheId : cacheIds) {
			ImageCacheManager.deleteDocumentImageCacheEntry(cacheId);
		}
	}
	
	private static String getDeleteDocumentImagesCacheSQL(Collection<String> cacheIds) {
		StringBuilder builder = new StringBuilder("DELETE FROM images_cache WHERE meta_id = ? AND id IN (");
		joinIds(builder, cacheIds);
		builder.append(')');
		
		return builder.toString();
	}
	
	private static String getExistingDocumentCacheIdsSQL(Collection<String> cacheIds) {
		StringBuilder builder = new StringBuilder("SELECT id FROM images_cache WHERE meta_id > 0 AND id IN (");
		joinIds(builder, cacheIds);
		builder.append(')');
		
		return builder.toString();
	}
	
	public String deleteDocumentImageCache(int metaId, int imageIndex) {
		String cacheIdSql = "SELECT ic.id FROM images_cache ic WHERE ic.meta_id = ? AND ic.image_index = ?";
		
		Object[] cacheIdParams = { metaId, imageIndex };
		String cacheId = (String) database.execute(new SqlQueryCommand(cacheIdSql, cacheIdParams, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				return rs.next() ? rs.getString(1) : null;
			}
		}));
		
		if (cacheId == null) {
			return null;
		}
		
		String deleteEntrySql = "DELETE FROM images_cache WHERE id = ? AND meta_id = ? AND image_index = ?";
		String countSql = "SELECT COUNT(ic.id) FROM images_cache ic WHERE ic.id = ? AND ic.meta_id > 0";
		
		Object[] deleteParams = { cacheId, metaId, imageIndex };
		database.execute(new SqlUpdateCommand(deleteEntrySql, deleteParams));
		
		Object[] countParams = { cacheId };
		long count = (Long) database.execute(new SqlQueryCommand(countSql, countParams, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				
				return rs.getLong(1);
			}
		}));
		
		return (count == 0L ? cacheId : null); 
	}
	
	public long getTextImageCacheFileSizeTotal() {
		String totalSql = "SELECT sum(ic.file_size) FROM images_cache ic WHERE ic.meta_id <= 0";
		
		return (Long) database.execute(new SqlQueryCommand(totalSql, null, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();

				return rs.getLong(1);
			}
		}));
	}
	
	public void deleteTextImageCacheLFUEntries() {
		String countSql = "SELECT COUNT(ic.id) FROM images_cache ic WHERE ic.meta_id <= 0";
		
		long count = (Long) database.execute(new SqlQueryCommand(countSql, null, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				
				return rs.getLong(1);
			}
		}));
		
		int deleteCount = (int) Math.ceil(count * 0.1);
		if (deleteCount < 1) {
			return;
		}
		
		
		String idsSql = "SELECT id FROM images_cache WHERE meta_id <= 0 ORDER BY frequency ASC LIMIT ?";
		
		ResultSetHandler idsHandler = new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				List<String> ids = new ArrayList<String>();
				
				while (rs.next()) {
					ids.add(rs.getString(1));
				}
				
				return ids;
			}
		};
		
		while (deleteCount > 0) {
			int delete = (deleteCount > 1000 ? 1000 : deleteCount);
			deleteCount -= delete;
			
			List<String> cacheIds = (List<String>) database.execute(new SqlQueryCommand(idsSql, new Object[] { delete }, idsHandler));
			if (cacheIds.isEmpty()) {
				return;
			}
			
			database.execute(new SqlUpdateCommand(getDeleteTextEntriesSQL(cacheIds), null));
			ImageCacheManager.deleteTextImageCacheEntries(cacheIds);
		}
	}
	
	private static String getDeleteTextEntriesSQL(Collection<String> cacheIds) {
		StringBuilder builder = new StringBuilder("DELETE FROM images_cache WHERE meta_id <= 0 AND id IN (");
		joinIds(builder, cacheIds);
		builder.append(')');
		
		return builder.toString();
	}
	
	private static void joinIds(StringBuilder builder, Collection<String> ids) {
		Iterator<String> it = ids.iterator();
		while (it.hasNext()) {
			builder.append('\'');
			builder.append(it.next());
			builder.append('\'');
			
			if (it.hasNext()) {
				builder.append(',');
			}
		}
	}
	
	public void addImageCache(ImageCacheDomainObject imageCache) {
	    try {
            String deleteSQL = "DELETE FROM images_cache WHERE id = ? AND meta_id = ? AND image_index = ?";
            String id = imageCache.getId();
            int metaId = imageCache.getMetaId();
            int imageIndex = imageCache.getImageIndex();

            database.execute(new SqlUpdateCommand(deleteSQL, new Object[] { id, metaId, imageIndex }));
            database.execute(new InsertIntoTableDatabaseCommand("images_cache", getColumnNamesAndValues(imageCache)));

	    } catch (Exception e) {
            log.error("Error while adding image cache data to DB", e);
        }
	}
	
	public void incrementFrequency(String cacheId) {
		database.execute(new SqlUpdateCommand(SQL__UPDATE_FREQUENCY, new Object[] { cacheId }));
	}
	
	public Map<Integer, Map<Integer, ImageDomainObject>> getAllDocumentImages() {
	    final Map<Integer, Map<Integer, ImageDomainObject>> documentImages = new HashMap<Integer, Map<Integer, ImageDomainObject>>();
	    
	    final DocumentGetter documentGetter = Imcms.getServices().getDocumentMapper().getDocumentGetter();
	    
	    ResultSetHandler imageHandler = new ResultSetHandler() {
            public Object handle(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    int metaId = rs.getInt(1);
                    int index = rs.getInt(2);
                    
                    Map<Integer, ImageDomainObject> images = documentImages.get(metaId);
                    if (images == null) {
                        images = new HashMap<Integer, ImageDomainObject>();
                        documentImages.put(metaId, images);
                    }
                    
                    ImageDomainObject image = new ImageDomainObject();
                    images.put(index, image);
                    
                    image.setWidth(rs.getInt(3));
                    image.setHeight(rs.getInt(4));
                    image.setFormat(Format.findFormat(rs.getShort(5)));
                    
                    CropRegion region = new CropRegion(rs.getInt(6), rs.getInt(7), rs.getInt(8), rs.getInt(9));
                    image.setCropRegion(region);
                    
                    image.setRotateDirection(RotateDirection.getByAngleDefaultIfNull(rs.getShort(10)));
                    
                    String imageSource = rs.getString(11);
                    int imageType = rs.getInt(12);
                    
                    if (StringUtils.isNotBlank(imageSource)) {
                        if (imageType == ImageSource.IMAGE_TYPE_ID__FILE_DOCUMENT) {
                            try {
                                int fileDocumentId = Integer.parseInt(imageSource);
                                DocumentDomainObject document = documentGetter.getDocument(new Integer(fileDocumentId));
                                
                                if ( null != document ) {
                                    image.setSource(new FileDocumentImageSource(new DirectDocumentReference(document)));
                                }
                            } catch (NumberFormatException ex) {
                                log.warn("Failed to set image source for file document with id: " + imageSource);
                            }
                        } else if (imageType == ImageSource.IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH) {
                            image.setSource(new ImagesPathRelativePathImageSource(imageSource));
                        } else if (imageType == ImageSource.IMAGE_TYPE_ID__IMAGE_ARCHIVE) {
                            image.setSource(new ImageArchiveImageSource(imageSource));
                        }
                    }
                }
                
                return null;
            }
        };
        
        database.execute(new SqlQueryCommand(
                "SELECT meta_id, name, width, height, format, crop_x1, crop_y1, crop_x2, crop_y2, rotate_angle, imgurl, type FROM images", new Object[] {}, imageHandler));
	    
	    return documentImages;
	}
	
	private static Object[][] getColumnNamesAndValues(ImageCacheDomainObject cache) {
		Format format = cache.getFormat();
		
		CropRegion region = cache.getCropRegion();
		boolean valid = region.isValid();

        final Timestamp createdDateWrongType = cache.getCreatedDate();
        final java.sql.Date createdDateCorrectType = new Date(createdDateWrongType.getTime());

        return new Object[][] {
				{ "id", cache.getId() }, 
				{ "meta_id", cache.getMetaId() }, 
				{ "image_index", cache.getImageIndex() }, 
				{ "resource", cache.getResource() }, 
				{ "cache_type", cache.getType() }, 
				{ "file_size", cache.getFileSize() }, 
				{ "frequency", cache.getFrequency() }, 
				{ "format", (format != null ? format.getOrdinal() : 0) }, 
				{ "width", cache.getWidth() }, 
				{ "height", cache.getHeight() }, 
				{ "crop_x1", (valid ? region.getCropX1() : -1) }, 
				{ "crop_y1", (valid ? region.getCropY1() : -1) }, 
				{ "crop_x2", (valid ? region.getCropX2() : -1) }, 
				{ "crop_y2", (valid ? region.getCropY2() : -1) }, 
				{ "rotate_angle", cache.getRotateDirection().getAngle() }, 
				{ "created_dt", createdDateCorrectType}
		};
	}
}
