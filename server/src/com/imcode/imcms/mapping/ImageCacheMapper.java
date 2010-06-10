package com.imcode.imcms.mapping;

import imcode.server.Imcms;
import imcode.server.document.DirectDocumentReference;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.FileDocumentImageSource;
import imcode.server.document.textdocument.ImageArchiveImageSource;
import imcode.server.document.textdocument.ImageCacheDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.util.image.Format;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.imcode.db.Database;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.servlet.ImageCacheManager;
import imcode.server.DatabaseVendor;

public class ImageCacheMapper {
    private static final Logger log = Logger.getLogger(ImageCacheMapper.class);
    
	private Database database;
	
	private static final String SQL__UPDATE_FREQUENCY = "UPDATE images_cache SET frequency = frequency + 1 WHERE " +
			"id = ? AND frequency < ?";
	
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
		
		database.execute(new SqlUpdateCommand(getDeleteDocumentImagesCacheSQL(cacheIds), null));
		
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

        ImageCacheManager.deleteTextImageCacheEntries(cacheIds);
	}
	
	private static String getDeleteDocumentImagesCacheSQL(Collection<String> cacheIds) {
		StringBuilder builder = new StringBuilder("DELETE FROM images_cache WHERE id IN (");
		joinIds(builder, cacheIds);
		builder.append(')');
		
		return builder.toString();
	}
	
	private static String getExistingDocumentCacheIdsSQL(Collection<String> cacheIds) {
		StringBuilder builder = new StringBuilder("SELECT id FROM images_cache WHERE id IN (");
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
		String totalSql = "SELECT sum(ic.file_size) FROM images_cache ic";
		
		return (Long) database.execute(new SqlQueryCommand(totalSql, null, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();

				return rs.getLong(1);
			}
		}));
	}
	
	public void deleteTextImageCacheLFUEntries() {
		String countSql = "SELECT COUNT(ic.id) FROM images_cache ic";
		
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

        DatabaseVendor vendor = Imcms.getServices().getConfig().getDatabaseVendor();
        boolean mssql = (vendor == DatabaseVendor.MSSQL);
        
        StringBuilder idsSql = new StringBuilder("SELECT ");
        
        if (mssql) {
            idsSql.append("TOP ");
            idsSql.append(deleteCount);
            idsSql.append(' ');
        }
        idsSql.append("id FROM images_cache ORDER BY frequency ASC ");
        
        if (!mssql) {
            idsSql.append("LIMIT ?");
        }

		ResultSetHandler idsHandler = new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				List<String> ids = new ArrayList<String>();
				
				while (rs.next()) {
					ids.add(rs.getString(1));
				}
				
				return ids;
			}
		};
		
        Object[] params = null;
        if (!mssql) {
            params = new Object[] { deleteCount };
        }

        List<String> cacheIds = (List<String>) database.execute(new SqlQueryCommand(idsSql.toString(), params, idsHandler));
        if (cacheIds.isEmpty()) {
            return;
        }

        database.execute(new SqlUpdateCommand(getDeleteTextEntriesSQL(cacheIds), null));
        ImageCacheManager.deleteTextImageCacheEntries(cacheIds);
	}
	
	private static String getDeleteTextEntriesSQL(Collection<String> cacheIds) {
		StringBuilder builder = new StringBuilder("DELETE FROM images_cache WHERE id IN (");
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
		String deleteSQL = "DELETE FROM images_cache WHERE id = ?";
		String id = imageCache.getId();
		
		database.execute(new SqlUpdateCommand(deleteSQL, new Object[] { id }));
		
		database.execute(new InsertIntoTableDatabaseCommand("images_cache", getColumnNamesAndValues(imageCache)));
	}
	
	public void incrementFrequency(String cacheId) {
		database.execute(new SqlUpdateCommand(SQL__UPDATE_FREQUENCY, new Object[] { cacheId, Integer.MAX_VALUE }));
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
		
		return new Object[][] {
				{ "id", cache.getId() }, 
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
				{ "created_dt", cache.getCreatedDate() }
		};
	}
}
