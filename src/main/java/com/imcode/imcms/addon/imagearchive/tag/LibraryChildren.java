package com.imcode.imcms.addon.imagearchive.tag;

import com.imcode.imcms.addon.imagearchive.dto.LibrariesDto;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibraryChildren extends TagSupport {

    @Override
    public int doStartTag() throws JspException {
        String path = getLibrary().getFilepath();
        if (path != null) {
            File file = new File(path);
            try {
                JspWriter out = pageContext.getOut();
                LibrariesDto lib = getLibrary();
                out.print("<li data-library-id='" + lib.getId() + "'>" + lib.getLibraryNm());
                getSubdirs(lib, file, new FileFilter() {
                    public boolean accept(File file) {
                        String name = file.getName();

                        return file.isDirectory() && name.length() <= 255;
                    }
                }, out);
                out.print("</li>");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return SKIP_BODY;
    }

    private void getSubdirs(LibrariesDto lib, File file, FileFilter filter, JspWriter output) throws IOException {
        if (file == null) {
            return;
        }

        File[] subDirsTmp = file.listFiles(filter);
        if (subDirsTmp == null) {
            subDirsTmp = new File[0];
        }

        List<File> subdirs = Arrays.asList(subDirsTmp);
        subdirs = new ArrayList<File>(subdirs);

        for (File subdir : subdirs) {
            LibrariesDto subLib = matchPathToLibrary(subdir);
            if(subLib == null) {
                return;
            }
            output.print("<ul>");
            output.print("<li data-library-id='" + subLib.getId() + "'>" + subdir.getName());
            getSubdirs(lib, subdir, filter, output);
            output.print("</li>");
            output.print("</ul>");
        }
    }

    private LibrariesDto matchPathToLibrary(File path) {
        for(LibrariesDto lib: getLibraries()) {
            if(lib.getFilepath() != null) {
                File f = new File(lib.getFilepath());
                if(path.equals(f)) {
                    return lib;
                }
            }
        }

        return null;
    }


    public LibrariesDto getLibrary() {
        return library;
    }

    public void setLibrary(LibrariesDto library) {
        this.library = library;
    }

    public LibrariesDto getCurrentLibrary() {
        return currentLibrary;
    }

    public void setCurrentLibrary(LibrariesDto currentLibrary) {
        this.currentLibrary = currentLibrary;
    }
    
    public List<LibrariesDto> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<LibrariesDto> libraries) {
        this.libraries = libraries;
    }


    private LibrariesDto library;
    private LibrariesDto currentLibrary;
    private List<LibrariesDto> libraries;
}
