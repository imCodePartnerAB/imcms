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
import imcode.util.image.Color;
import imcode.util.image.Format;
import imcode.util.image.Gravity;
import imcode.util.image.ImageOp;

public class FileService {
    private static final Log log = LogFactory.getLog(FileService.class);
    
    public static final String[] IMAGE_EXTENSIONS = new String[] { 
        "ai", "bmp", "eps", "gif", 
        "jpeg", "jpg", "pct", "pdf", "pic", "pict", 
        "png", "ps", "psd", "svg", 
        "tif", "tiff", "xcf",
        "AI", "BMP", "EPC", "GIF",
        "JPEG", "JPG", "PCT", "PDF", "PIC", "PICT",
        "PNG", "PS", "PSD", "SVG",
        "TIF", "TIFF", "XCF",
    };
    public static final Set<String> IMAGE_EXTENSIONS_SET = new HashSet<String>(Arrays.asList(IMAGE_EXTENSIONS));
    
    
    public static final Pattern FILENAME_PATTERN = Pattern.compile("^.*?/?([^/\\:]+?)$");
    
    private static final String IMAGE_ORIGINAL_INFIX = "orig";
    private static final String IMAGE_FULL_INFIX = "full";
    private static final String IMAGE_SMALL_THUMB_INFIX = ThumbSize.SMALL.getName();
    private static final String IMAGE_MEDIUM_THUMB_INFIX = ThumbSize.MEDIUM.getName();
    
    
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
    
    public File getImageRootPath(long imageId) {
        return new File(config.getStoragePath(), Long.toString(imageId));
    }
    
    public File getImageFile(long imageId, String infix, boolean temporary) {
        StringBuilder filenameBuilder = new StringBuilder();
        
        filenameBuilder.append(imageId);
        filenameBuilder.append("_");
        filenameBuilder.append(infix);
        
        if (temporary) {
            filenameBuilder.append("_tmp");
        }
        
        return new File(getImageRootPath(imageId), filenameBuilder.toString());
    }
    
    public File getImageOriginalFile(long imageId, boolean temporary) {
        return getImageFile(imageId, IMAGE_ORIGINAL_INFIX, temporary);
    }
    
    public File getImageFullFile(long imageId, boolean temporary) {
        return getImageFile(imageId, IMAGE_FULL_INFIX, temporary);
    }
    
    public boolean storeImage(File tempFile, long imageId, boolean temporary) {
        File originalFile = getImageFile(imageId, IMAGE_ORIGINAL_INFIX, temporary);
        File fullFile = getImageFile(imageId, IMAGE_FULL_INFIX, temporary);
        File thumbSmallFile = getImageFile(imageId, IMAGE_SMALL_THUMB_INFIX, temporary);
        File thumbMedFile = getImageFile(imageId, IMAGE_MEDIUM_THUMB_INFIX, temporary);
        
        try {
            originalFile.getParentFile().mkdirs();

            FileUtils.copyFile(tempFile, originalFile);
            
            new ImageOp().input(originalFile)
                    .outputFormat(Format.JPEG)
                    .processToFile(fullFile);
            
            generateThumbnail(fullFile, thumbSmallFile, ThumbSize.SMALL);
            generateThumbnail(fullFile, thumbMedFile, ThumbSize.MEDIUM);
            
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
    
    private void generateThumbnail(File inputFile, File outputFile, ThumbSize thumbnailSize) {
        new ImageOp().input(inputFile)
            .resizeProportional(thumbnailSize.getWidth(), thumbnailSize.getHeight(), Color.WHITE, Gravity.CENTER)
            .outputFormat(Format.JPEG)
            .processToFile(outputFile);
    }
    
    public void rotateImage(long imageId, int angle, boolean temporary) {
        File fullFile = getImageFile(imageId, IMAGE_FULL_INFIX, temporary);
        File thumbSmallFile = getImageFile(imageId, IMAGE_SMALL_THUMB_INFIX, temporary);
        File thumbMedFile = getImageFile(imageId, IMAGE_MEDIUM_THUMB_INFIX, temporary);
        
        new ImageOp().input(fullFile)
                .rotate(angle)
                .processToFile(fullFile);
        
        generateThumbnail(fullFile, thumbSmallFile, ThumbSize.SMALL);
        generateThumbnail(fullFile, thumbMedFile, ThumbSize.MEDIUM);
    }
    
    public void copyTemporaryImageToCurrent(long imageId) {
        File originalFile = getImageFile(imageId, IMAGE_ORIGINAL_INFIX, false);
        File fullFile = getImageFile(imageId, IMAGE_FULL_INFIX, false);
        File thumbSmallFile = getImageFile(imageId, IMAGE_SMALL_THUMB_INFIX, false);
        File thumbMedFile = getImageFile(imageId, IMAGE_MEDIUM_THUMB_INFIX, false);
        
        File originalTempFile = getImageFile(imageId, IMAGE_ORIGINAL_INFIX, true);
        File fullTempFile = getImageFile(imageId, IMAGE_FULL_INFIX, true);
        File thumbSmallTempFile = getImageFile(imageId, IMAGE_SMALL_THUMB_INFIX, true);
        File thumbMedTempFile = getImageFile(imageId, IMAGE_MEDIUM_THUMB_INFIX, true);
        
        try {
            FileUtils.copyFile(originalTempFile, originalFile);
            FileUtils.copyFile(fullTempFile, fullFile);
            FileUtils.copyFile(thumbSmallTempFile, thumbSmallFile);
            FileUtils.copyFile(thumbMedTempFile, thumbMedFile);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void createTemporaryCopyOfCurrentImage(long imageId) {
        File originalFile = getImageFile(imageId, IMAGE_ORIGINAL_INFIX, false);
        File fullFile = getImageFile(imageId, IMAGE_FULL_INFIX, false);
        File thumbSmallFile = getImageFile(imageId, IMAGE_SMALL_THUMB_INFIX, false);
        File thumbMedFile = getImageFile(imageId, IMAGE_MEDIUM_THUMB_INFIX, false);
        
        File originalTempFile = getImageFile(imageId, IMAGE_ORIGINAL_INFIX, true);
        File fullTempFile = getImageFile(imageId, IMAGE_FULL_INFIX, true);
        File thumbSmallTempFile = getImageFile(imageId, IMAGE_SMALL_THUMB_INFIX, true);
        File thumbMedTempFile = getImageFile(imageId, IMAGE_MEDIUM_THUMB_INFIX, true);
        
        try {
            FileUtils.copyFile(originalFile, originalTempFile);
            FileUtils.copyFile( fullFile, fullTempFile);
            FileUtils.copyFile(thumbSmallFile, thumbSmallTempFile);
            FileUtils.copyFile(thumbMedFile, thumbMedTempFile);
        } catch (Exception ex) {
            FileUtils.deleteQuietly(originalTempFile);
            FileUtils.deleteQuietly(fullTempFile);
            FileUtils.deleteQuietly(thumbSmallTempFile);
            FileUtils.deleteQuietly(thumbMedTempFile);
            
            throw new RuntimeException(ex);
        }
    }
    
    public void deleteTemporaryImage(long imageId) {
        File originalTempFile = getImageFile(imageId, IMAGE_ORIGINAL_INFIX, true);
        File fullTempFile = getImageFile(imageId, IMAGE_FULL_INFIX, true);
        File thumbSmallTempFile = getImageFile(imageId, IMAGE_SMALL_THUMB_INFIX, true);
        File thumbMedTempFile = getImageFile(imageId, IMAGE_MEDIUM_THUMB_INFIX, true);
        
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

    public List<File> getSubdirs(File file, FileFilter filter) {
        if(file == null) {
            return Collections.emptyList();
        }

        File[] subDirsTmp = file.listFiles(filter);
        if(subDirsTmp == null) {
            subDirsTmp = new File[0];
        }

        List<File> subdirs = Arrays.asList(subDirsTmp);
        subdirs = new ArrayList<File>(subdirs);

        List<File> deepSubdirs = new ArrayList<File>();
        for(File subdir : subdirs) {
            deepSubdirs.addAll(getSubdirs(subdir, filter));
        }

        subdirs.addAll(deepSubdirs);
        return subdirs;
    }

    public List<File> listFirstLevelLibraryFolders() {
        final String usersFolder = config.getUsersLibraryFolder();
        List<File> firstLevelLibraryFolder = new ArrayList<File>();

        File[] oldLibrariesPaths = config.getOldLibraryPaths();
        File[] files = config.getLibrariesPath().listFiles(new FileFilter() {
            public boolean accept(File file) {
                String name = file.getName();

                return file.isDirectory() && !name.equals(usersFolder) && name.length() <= 255;
            }
        });

        if(oldLibrariesPaths != null) {
            firstLevelLibraryFolder.addAll(Arrays.asList(oldLibrariesPaths));
        }

        if(files != null) {
            firstLevelLibraryFolder.addAll(Arrays.asList(files));
        }


        return firstLevelLibraryFolder;
    }

    public List<File> listLibraryFolders() {
        final String usersFolder = config.getUsersLibraryFolder();

        List<File> files = getSubdirs(config.getLibrariesPath(), new FileFilter() {
            public boolean accept(File file) {
                String name = file.getName();

                return file.isDirectory() && !name.equals(usersFolder) && name.length() <= 255;
            }
        });

        if (files == null) {
        	return Collections.emptyList();
        }

        return files;
    }

    @SuppressWarnings("unchecked")
    public List<LibraryEntryDto> listLibraryEntries(LibrariesDto library, LibrarySort sortBy) {
        File libraryFile;

        if (library.isUserLibrary()) {
            String libraryComponent = String.format("%s/%s", config.getUsersLibraryFolder(), library.getFolderNm());

            libraryFile = new File(config.getLibrariesPath(), libraryComponent);

        } else if (library.getLibraryType() == Libraries.TYPE_OLD_LIBRARY) {
            libraryFile = new File(library.getFilepath(), library.getFolderNm());

        } else {
            libraryFile = new File(library.getFilepath(), library.getFolderNm());

        }

        if (!(libraryFile.exists() || libraryFile.isDirectory()) || !library.isCanUse()) {
            return Collections.emptyList();
        }

        Collection<File> files = FileUtils.listFiles(libraryFile, IMAGE_EXTENSIONS, false);
        List<LibraryEntryDto> entries = new ArrayList<LibraryEntryDto>(files.size());

        for (File file : files) {
            LibraryEntryDto entry = new LibraryEntryDto();
            entry.setFileName(file.getName());
            entry.setFileSize((int) file.length());
            entry.setLastModified(file.lastModified());
            entry.setImageInfo(ImageOp.getImageInfo(file));

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
        File parent;
        
        if (library.isUserLibrary()) {
            parent = new File(config.getLibrariesPath(), config.getUsersLibraryFolder());

        } else if (library.getLibraryType() == Libraries.TYPE_OLD_LIBRARY) {
            parent = new File(library.getFilepath());

        } else {
            parent = new File(library.getFilepath());

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

                /* skipping OSX resource forks(__MAXOSC/) */
                if (fileName.startsWith("__MACOSX/") ||!matcher.matches() || StringUtils.isEmpty((fileName = matcher.group(1).trim()))) {
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
        File parent;
        
        if (library.isUserLibrary()) {
            parent = new File(config.getLibrariesPath(), config.getUsersLibraryFolder());
            
        } else if (library.getLibraryType() == Libraries.TYPE_OLD_LIBRARY) {
            parent = new File(library.getFilepath());
            
        } else {
            parent = new File(library.getFilepath());

        }
        
        return new File(parent, String.format("%s/%s", library.getFolderNm(), fileName));
    }
}
