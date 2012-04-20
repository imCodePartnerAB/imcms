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

/*
Prints out library sub-folders recursively as html listByNamedParams
Used in external files for library tree rendering
 */
public class LibraryChildren extends TagSupport {

    @Override
    public int doStartTag() throws JspException {
        String path = getLibrary().getFilepath();
        if (path != null) {
            File file = new File(path, getLibrary().getFolderNm());
            try {
                JspWriter out = pageContext.getOut();
                LibrariesDto lib = getLibrary();
                String currentLibraryClass = "";
                if(getCurrentLibrary().getId() == lib.getId()) {
                    currentLibraryClass = " class='currentLibrary'";
                }
                out.print("<li data-library-id='" + lib.getId() + "'>" + "<span" + currentLibraryClass + ">" + lib.getLibraryNm() + "</span>");
                getSubdirs(file, new FileFilter() {
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

    private void getSubdirs(File file, FileFilter filter, JspWriter output) throws IOException {
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
            String currentLibraryClass = "";
            if(getCurrentLibrary().getId() == subLib.getId()) {
                currentLibraryClass = " class='currentLibrary'";
            }
            output.print("<li data-library-id='" + subLib.getId() + "'>" + "<span" + currentLibraryClass + ">" + subdir.getName() + "</span>");
            getSubdirs(subdir, filter, output);
            output.print("</li>");
            output.print("</ul>");
        }
    }

    private LibrariesDto matchPathToLibrary(File path) {
        for(LibrariesDto lib: getLibraries()) {
            if(lib.getFilepath() != null) {
                File f = new File(lib.getFilepath(), lib.getFolderNm());
                if(path.equals(f)) {
                    return lib;
                }
            }
        }

        return null;
    }


    /* Library to output children for */
    public LibrariesDto getLibrary() {
        return library;
    }

    public void setLibrary(LibrariesDto library) {
        this.library = library;
    }

    /* Current library */
    public LibrariesDto getCurrentLibrary() {
        return currentLibrary;
    }

    public void setCurrentLibrary(LibrariesDto currentLibrary) {
        this.currentLibrary = currentLibrary;
    }

    /* List of all libraries avaibale to the user */
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
