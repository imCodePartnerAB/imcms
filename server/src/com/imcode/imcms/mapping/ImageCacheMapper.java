package com.imcode.imcms.mapping;

import imcode.server.Imcms;
import imcode.server.document.textdocument.ImageCacheDomainObject;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.util.image.Format;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.imcode.db.Database;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import imcode.server.DatabaseVendor;
import imcode.util.image.Resize;
import java.util.*;

public class ImageCacheMapper {
    private static final Logger log = Logger.getLogger(ImageCacheMapper.class);
    
	private Database database;
	
	private static final String SQL__UPDATE_FREQUENCY = "UPDATE images_cache SET frequency = frequency + 1 WHERE " +
			"id = ? AND frequency < ?";
	
	public ImageCacheMapper(Database database) {
		this.database = database;
	}
	
    public List<String> getImagesCacheIds(Integer metaId, Integer no, String fileNo) {
        StringBuilder builder = new StringBuilder("SELECT id FROM images_cache");
        
        List<String> restrictions = new ArrayList<String>(3);
        List<Object> params = new ArrayList<Object>(3);
        
        if (metaId != null) {
            restrictions.add("meta_id = ?");
            params.add(metaId);
        }
        if (no != null) {
            restrictions.add("no = ?");
            params.add(no);
        }
        if (fileNo != null) {
            restrictions.add("file_no = ?");
            params.add(fileNo);
        }
        
        if (!restrictions.isEmpty()) {
            builder.append(" WHERE ");
            
            builder.append(StringUtils.join(restrictions.iterator(), " AND "));
        }
        
        IdResultSetHandler handler = new IdResultSetHandler();
        
        return (List<String>) database.execute(new SqlQueryCommand(builder.toString(), params.toArray(), handler));
    }
    
    public void deleteAllImagesCache() {
        String sql = getDeleteImagesCacheSQL(null);
        
        database.execute(new SqlUpdateCommand(sql, null));
    }
    
    public void deleteImagesCache(Collection<String> ids) {
        if (ids.isEmpty()) {
            return;
        }
        
        String sql = getDeleteImagesCacheSQL(ids);
        
        database.execute(new SqlUpdateCommand(sql, null));
    }
	
	private static String getDeleteImagesCacheSQL(Collection<String> cacheIds) {
        if (cacheIds == null) {
            return "DELETE FROM images_cache";
        }
        
		StringBuilder builder = new StringBuilder("DELETE FROM images_cache WHERE id IN (");
		joinIds(builder, cacheIds);
		builder.append(')');
		
		return builder.toString();
	}
	
	public long getCacheFileSizeTotal() {
		String totalSql = "SELECT sum(ic.file_size) FROM images_cache ic";
		
		return (Long) database.execute(new SqlQueryCommand(totalSql, null, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();

				return rs.getLong(1);
			}
		}));
	}
	
	public List<String> deleteCacheLFUEntries() {
		String countSql = "SELECT COUNT(ic.id) FROM images_cache ic";
		
		long count = (Long) database.execute(new SqlQueryCommand(countSql, null, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				
				return rs.getLong(1);
			}
		}));
		
		int deleteCount = (int) Math.ceil(count * 0.1);
		if (deleteCount < 1) {
			return Collections.emptyList();
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

		Object[] params = null;
        if (!mssql) {
            params = new Object[] { deleteCount };
        }

        IdResultSetHandler idsHandler = new IdResultSetHandler();
        
        List<String> cacheIds = (List<String>) database.execute(new SqlQueryCommand(idsSql.toString(), params, idsHandler));
        deleteImagesCache(cacheIds);
        
        return cacheIds;
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
	
	private static Object[][] getColumnNamesAndValues(ImageCacheDomainObject cache) {
		Format format = cache.getFormat();
        Resize resize = cache.getResize();
		
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
                { "resize", (resize != null ? resize.getOrdinal() : null) },
				{ "crop_x1", (valid ? region.getCropX1() : -1) }, 
				{ "crop_y1", (valid ? region.getCropY1() : -1) }, 
				{ "crop_x2", (valid ? region.getCropX2() : -1) }, 
				{ "crop_y2", (valid ? region.getCropY2() : -1) }, 
				{ "rotate_angle", cache.getRotateDirection().getAngle() }, 
				{ "created_dt", cache.getCreatedDate() }, 
                { "meta_id", cache.getMetaId() }, 
                { "no", cache.getNo() }, 
                { "file_no", cache.getFileNo() }
		};
	}
    
    private static class IdResultSetHandler implements ResultSetHandler {
        @Override
        public Object handle(ResultSet rs) throws SQLException {
            List<String> ids = new ArrayList<String>();
            
            while (rs.next()) {
                ids.add(rs.getString(1));
            }
            
            return ids;
        }
    }
}
