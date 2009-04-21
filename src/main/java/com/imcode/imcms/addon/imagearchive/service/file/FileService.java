package com.imcode.imcms.addon.imagearchive.service.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.imcode.imcms.addon.imagearchive.Config;
import com.imcode.imcms.addon.imagearchive.dto.LibrariesDto;
import com.imcode.imcms.addon.imagearchive.dto.LibraryEntryDto;
import com.imcode.imcms.addon.imagearchive.entity.Libraries;
import com.imcode.imcms.addon.imagearchive.util.image.Color;
import com.imcode.imcms.addon.imagearchive.util.image.Format;
import com.imcode.imcms.addon.imagearchive.util.image.Gravity;
import com.imcode.imcms.addon.imagearchive.util.image.ImageOp;

public class FileService {
    private static final Log log = LogFactory.getLog(FileService.class);
    
    public static final String[] IMAGE_EXTENSIONS = new String[] { 
        "ai", "bmp", "eps", "gif", 
        "jpeg", "jpg", "pct", "pdf", "pic", "pict", 
        "png", "ps", "psd", "svg", 
        "tif", "tiff", "xcf"
    };
    public static final Set<String> IMAGE_EXTENSIONS_SET = new HashSet<String>(Arrays.asList(IMAGE_EXTENSIONS));
    
    
    public static final Pattern FILENAME_PATTERN = Pattern.compile("^.*?/?([^/\\:]+?)$");
    
    
    @Autowired
    private Config config;
    
    
    public File createTemporaryFile(String prefix) {
        try {
            return File.createTempFile(prefix, ".tmp", config.getTmpPath());
        } catch (IOException ex) {
            log.fatal(ex.getMessage(), ex);
        }
        
        return null;
    }
    
    public boolean storeImage(File tempFile, long imageId, boolean temporary) {
        String temp = (temporary ? "_tmp" : "");
        File rootPath = new File(config.getStoragePath(), Long.toString(imageId));
        
        File originalFile = new File(rootPath, String.format("%d_orig%s", imageId, temp));
        File fullFile = new File(rootPath, String.format("%d_full%s", imageId, temp));
        File thumbSmallFile = new File(rootPath, String.format("%d_%s%s", imageId, ThumbSize.SMALL.getName(), temp));
        File thumbMedFile = new File(rootPath, String.format("%d_%s%s", imageId, ThumbSize.MEDIUM.getName(), temp));
        
        try {
            FileUtils.copyFile(tempFile, originalFile);
            
            new ImageOp().input(originalFile)
                    .outputFormat(Format.JPEG)
                    .processToFile(fullFile);
            
            new ImageOp().input(fullFile)
                    .resizeProportional(ThumbSize.SMALL.getWidth(), ThumbSize.SMALL.getHeight(), Color.WHITE, Gravity.CENTER)
                    .outputFormat(Format.JPEG)
                    .processToFile(thumbSmallFile);
            
            new ImageOp().input(fullFile)
                    .resizeProportional(ThumbSize.MEDIUM.getWidth(), ThumbSize.MEDIUM.getHeight(), Color.WHITE, Gravity.CENTER)
                    .outputFormat(Format.JPEG)
                    .processToFile(thumbMedFile);
            
            return true;
        } catch (IOException ex) {
            log.fatal(ex.getMessage(), ex);
            
            thumbSmallFile.delete();
            thumbMedFile.delete();
            fullFile.delete();
            originalFile.delete();
        }
        
        return false;
    }
    
    public void moveTempImageToCurrent(long imageId) {
        File rootPath = new File(config.getStoragePath(), Long.toString(imageId));
        
        String originalPath = String.format("%d_orig", imageId);
        String fullPath = String.format("%d_full", imageId);
        String thumbSmallPath = String.format("%d_%s", imageId, ThumbSize.SMALL.getName());
        String thumbMedPath = String.format("%d_%s", imageId, ThumbSize.MEDIUM.getName());
        
        File originalFile = new File(rootPath, originalPath);
        File fullFile = new File(rootPath, fullPath);
        File thumbSmallFile = new File(rootPath, thumbSmallPath);
        File thumbMedFile = new File(rootPath, thumbMedPath);
        
        File originalTempFile = new File(rootPath, originalPath + "_tmp");
        File fullTempFile = new File(rootPath, fullPath + "_tmp");
        File thumbSmallTempFile = new File(rootPath, thumbSmallPath + "_tmp");
        File thumbMedTempFile = new File(rootPath, thumbMedPath + "_tmp");
        
        try {
            FileUtils.copyFile(originalTempFile, originalFile);
            FileUtils.copyFile(fullTempFile, fullFile);
            FileUtils.copyFile(thumbSmallTempFile, thumbSmallFile);
            FileUtils.copyFile(thumbMedTempFile, thumbMedFile);
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
        } finally {
            originalTempFile.delete();
            fullTempFile.delete();
            thumbSmallTempFile.delete();
            thumbMedTempFile.delete();
        }
    }
    
    public void deleteTempImage(long imageId) {
        File rootPath = new File(config.getStoragePath(), Long.toString(imageId));
        
        File originalTempFile = new File(rootPath, String.format("%d_orig_tmp", imageId));
        File fullTempFile = new File(rootPath, String.format("%d_full_tmp", imageId));
        File thumbSmallTempFile = new File(rootPath, String.format("%d_%s_tmp", imageId, ThumbSize.SMALL.getName()));
        File thumbMedTempFile = new File(rootPath, String.format("%d_%s_tmp", imageId, ThumbSize.MEDIUM.getName()));
        
        originalTempFile.delete();
        fullTempFile.delete();
        thumbSmallTempFile.delete();
        thumbMedTempFile.delete();
    }
    
    public void deleteImage(long imageId) {
        File rootPath = new File(config.getStoragePath(), Long.toString(imageId));
        
        try {
            FileUtils.deleteDirectory(rootPath);
        } catch (Exception ex) {
            log.fatal(ex.getMessage(), ex);
        }
    }
    
    public Object[] getImageThumbnail(long imageId, ThumbSize thumbSize, boolean temporary) {
        try {
            String temp = (temporary ? "_tmp" : "");
            
            File rootPath = new File(config.getStoragePath(), Long.toString(imageId));
            
            File thumbFile = new File(rootPath, String.format("%d_%s%s", imageId, thumbSize.getName(), temp));
            
            if (!thumbFile.exists()) {
                return null;
            }
            
            return new Object[] {
                thumbFile.length(), 
                FileUtils.openInputStream(thumbFile)
            };
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return null;
    }
    
    public Object[] getImageFull(long imageId, boolean temporary) {
        try {
            String temp = (temporary ? "_tmp" : "");
            
            File rootPath = new File(config.getStoragePath(), Long.toString(imageId));
            
            File fullFile = new File(rootPath, String.format("%d_full%s", imageId, temp));
            
            if (!fullFile.exists()) {
                return null;
            }
            
            return new Object[] {
                fullFile.length(), 
                FileUtils.openInputStream(fullFile)
            };
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return null;
    }
    
    public File getImageOriginalFile(long imageId) {
        File rootPath = new File(config.getStoragePath(), Long.toString(imageId));
        
        return new File(rootPath, String.format("%d_orig", imageId));
    }
    
    public String transferImageToImcms(long imageId) {
        String filename = String.format("archive_img%d.jpg", imageId);
        
        File rootPath = new File(config.getStoragePath(), Long.toString(imageId));
        
        File fullFile = new File(rootPath, String.format("%d_full", imageId));
        
        File imcmsFile = new File(config.getImagesPath(), filename);
        
        try {
            FileUtils.copyFile(fullFile, imcmsFile);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return filename;
    }
    
    public Set<String> listLibraryFolders() {
        final String usersFolder = config.getUsersLibraryFolder();
        
        File[] files = config.getLibrariesPath().listFiles(new FileFilter() {
            public boolean accept(File file) {
                String name = file.getName();
                
                return file.isDirectory() && !name.equals(usersFolder) && name.length() <= 255;
            }
        });
        
        if (files == null) {
        	return Collections.emptySet();
        }
        
        Set<String> dirNames = new HashSet<String>(files.length);
        
        for (int i = 0, len = files.length; i < len; i++) {
            dirNames.add(files[i].getName());
        }
        
        return dirNames;
    }
    
    @SuppressWarnings("unchecked")
    public List<LibraryEntryDto> listLibraryEntries(LibrariesDto library, LibrarySort sortBy) {
        File libraryFile = null;
        
        if (library.isUserLibrary()) {
            String libraryComponent = String.format("%s/%s", config.getUsersLibraryFolder(), library.getFolderNm());
            
            libraryFile = new File(config.getLibrariesPath(), libraryComponent);
            
        } else if (library.getLibraryType() == Libraries.TYPE_OLD_LIBRARY) {
            libraryFile = new File(library.getFilepath(), library.getFolderNm());
            
        } else {
            libraryFile = new File(config.getLibrariesPath(), library.getFolderNm());
            
        }
        
        if (!(libraryFile.exists() || libraryFile.isDirectory())) {
            return Collections.emptyList();
        }
        
        Collection<File> files = FileUtils.listFiles(libraryFile, IMAGE_EXTENSIONS, false);
        List<LibraryEntryDto> entries = new ArrayList<LibraryEntryDto>(files.size());
        
        for (File file : files) {
            LibraryEntryDto entry = new LibraryEntryDto();
            entry.setFileName(file.getName());
            entry.setFileSize((int) file.length());
            entry.setLastModified(file.lastModified());
            
            entries.add(entry);
        }
        
        Collections.sort(entries, new LibraryEntryComparator(sortBy));
        
        return entries;
    }
    
    public boolean storeImageToLibrary(LibrariesDto library, File tempFile, String fileName) {
        File imageFile = getLibraryFile(library, fileName);
        try {
            FileUtils.copyFile(tempFile, imageFile);
            
            return true;
        } catch (Exception ex) {
            imageFile.delete();
        }
        
        return false;
    }
    
    public boolean storeZipToLibrary(LibrariesDto library, File tempFile) {
        File parent = null;
        
        if (library.isUserLibrary()) {
            parent = new File(config.getLibrariesPath(), config.getUsersLibraryFolder());
            
        } else if (library.getLibraryType() == Libraries.TYPE_OLD_LIBRARY) {
            parent = new File(library.getFilepath());
            
        } else {
            parent = config.getLibrariesPath();
            
        }
        
        File rootPath = new File(parent, library.getFolderNm());
        
        ZipFile zip = null;
        try {
            zip = new ZipFile(tempFile, ZipFile.OPEN_READ);
            
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                
                String fileName = entry.getName();
                Matcher matcher = FILENAME_PATTERN.matcher(fileName);
                
                if (!matcher.matches() || StringUtils.isEmpty((fileName = matcher.group(1).trim()))) {
                    continue;
                }
                
                String extension = StringUtils.substringAfterLast(fileName, ".").toLowerCase();
                if (!IMAGE_EXTENSIONS_SET.contains(extension)) {
                    continue;
                }
                
                File entryFile = new File(rootPath, fileName);
                
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    inputStream = zip.getInputStream(entry);
                    outputStream = new BufferedOutputStream(FileUtils.openOutputStream(entryFile));
                    
                    IOUtils.copy(inputStream, outputStream);
                } catch (Exception ex) {
                    log.warn(ex.getMessage(), ex);
                    entryFile.delete();
                } finally {
                    IOUtils.closeQuietly(outputStream);
                    IOUtils.closeQuietly(inputStream);
                }
            }
            
            return true;
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException ex) {
                    log.warn(ex.getMessage(), ex);
                }
            }
        }
        
        return false;
    }
    
    public void deleteFileFromLibrary(LibrariesDto library, String fileName) {
        File file = getLibraryFile(library, fileName);
        file.delete();
    }
    
    public File getImageFileFromLibrary(LibrariesDto library, String fileName) {
        File file = getLibraryFile(library, fileName);
        if (!file.exists()) {
            return null;
        }
        
        return file;
    }
    
    private File getLibraryFile(LibrariesDto library, String fileName) {
        File parent = null;
        
        if (library.isUserLibrary()) {
            parent = new File(config.getLibrariesPath(), config.getUsersLibraryFolder());
            
        } else if (library.getLibraryType() == Libraries.TYPE_OLD_LIBRARY) {
            parent = new File(library.getFilepath());
            
        } else {
            parent = config.getLibrariesPath();
            
        }
        
        return new File(parent, String.format("%s/%s", library.getFolderNm(), fileName));
    }
}
