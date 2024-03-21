package com.imcode.imcms.servlet;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import imcode.server.Imcms;
import imcode.util.ImcmsImageUtils;
import imcode.util.Utility;
import imcode.util.image.Resize;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.imcode.imcms.api.SourceFile.FileType.FILE;

public class ImageFetcher extends HttpServlet {

    private StorageClient imageStorageClient;
    private String imagesPath;

    public static final String URL = "image";
    public static final String PATH_PARAMETER = "path";
    public static final String WIDTH_PARAMETER = "width";
    public static final String HEIGHT_PARAMETER = "height";

    @Override
    public void init() throws ServletException {
        imageStorageClient = Imcms.getServices().getManagedBean("imageStorageClient", StorageClient.class);
        imagesPath = ImcmsImageUtils.imagesPath;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String path = Utility.unescapeValue(request.getParameter(PATH_PARAMETER));
        final String width = request.getParameter(WIDTH_PARAMETER);
        final String height = request.getParameter(HEIGHT_PARAMETER);

        if(StringUtils.isBlank(path)){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if(StringUtils.isBlank(width) && StringUtils.isBlank(height)){
            fetchImage(path, response);
        }else{
            generateImage(path, width, height, response);
        }
    }

    private void fetchImage(String path, HttpServletResponse response) throws IOException{
        try(final StorageFile file = imageStorageClient.getFile(StoragePath.get(FILE, imagesPath, path));
            OutputStream responseOutputStream = response.getOutputStream()){

            response.setContentLengthLong(file.size());
            response.setContentType(new Tika().detect(path));

            IOUtils.copy(file.getContent(), responseOutputStream);
        }catch(StorageFileNotFoundException e){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void generateImage(String path, String widthStr, String heightStr, HttpServletResponse response) throws IOException {
        final ImageDTO imageDTO = new ImageDTO();
        imageDTO.setSource(ImcmsImageUtils.getImageSource(path));

        imageDTO.setCompress(false);

        int width = StringUtils.isNotBlank(widthStr) ? Integer.parseInt(widthStr) : 0;
        int height = StringUtils.isNotBlank(heightStr) ? Integer.parseInt(heightStr) : 0;
        imageDTO.setWidth(width);
        imageDTO.setHeight(height);

        Resize resize = width == 0 || height == 0 ? Resize.DEFAULT : Resize.FORCE;
        imageDTO.setResize(resize);

        byte[] generatedImage = ImcmsImageUtils.generateImage(imageDTO);

        if(generatedImage == null || generatedImage.length == 0){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try(ByteArrayInputStream input = new ByteArrayInputStream(generatedImage);
            OutputStream responseOutputStream = response.getOutputStream()){
            IOUtils.copy(input, responseOutputStream);
        }
    }

}
