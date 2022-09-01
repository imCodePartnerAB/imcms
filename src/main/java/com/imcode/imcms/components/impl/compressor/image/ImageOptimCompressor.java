package com.imcode.imcms.components.impl.compressor.image;

import com.imcode.imcms.components.ImageCompressor;
import com.imcode.imcms.components.exception.CompressionImageException;
import imcode.util.image.Format;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * External image compressor ImageOptim
 */
public class ImageOptimCompressor  implements ImageCompressor {

    private final Logger logger = LogManager.getLogger(ImageOptimCompressor.class);

    private final String imageOptimUrl;
    private final String username;
    private final String quality;

    public ImageOptimCompressor(String imageOptimUrl, String username, String quality){
        this.imageOptimUrl = imageOptimUrl;
        this.username = username;
        this.quality = quality;
    }

    @Override
    public byte[] compressImage(byte[] image, Format imageFormat) throws CompressionImageException {
        return sendCompressionRequest(image, imageFormat);
    }

    private byte[] sendCompressionRequest(byte[] image, Format imageFormat) throws CompressionImageException {
            try(CloseableHttpClient client = HttpClients.createDefault()){
                final URI uri = new URI(String.format(imageOptimUrl + "/%s/quality=%s,format=%s", username, quality, imageFormat.getExtension()));

                final ContentType contentType = ContentType.getByMimeType(imageFormat.getMimeType());
                final String filename = "compressedImage." + imageFormat.getExtension();

                HttpPost httpPost = new HttpPost(uri);
                HttpEntity multipartForm = MultipartEntityBuilder.create()
                        .addBinaryBody("file", image, contentType, filename)
                        .build();
                httpPost.setEntity(multipartForm);

                try(CloseableHttpResponse httpResponse = client.execute(httpPost)){
                    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        return httpResponse.getEntity().getContent().readAllBytes();
                    } else {
                        logger.error("Error while sending image for compression using ImageOptim. Response: " + httpResponse.getStatusLine());
                    }
                }
            } catch (IOException | URISyntaxException e) {
                logger.error("Error while sending image for compression using ImageOptim", e);
            }

        throw new CompressionImageException();
    }
}
