package com.imcode.imcms.components.impl;

import com.imcode.imcms.components.ImageCompressor;
import com.imcode.imcms.components.exception.CompressionImageException;
import imcode.util.image.Format;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * External image compressor Resmush
 */
@Component
public class ResmushImageCompressor implements ImageCompressor {

    private final Logger logger = LogManager.getLogger(ResmushImageCompressor.class);

    @Value("${compression.image.resmush.url}")
    private String resmushUrl;

    @Override
    public byte[] compressImage(byte[] image, Format imageFormat) throws CompressionImageException {
        JSONObject json = sendCompressionRequest(image, imageFormat);
        String compressedImageUrl = (String) json.get("dest");

        return sendImageRequest(compressedImageUrl);
    }

    private JSONObject sendCompressionRequest(byte[] image, Format imageFormat) throws CompressionImageException {
        if(StringUtils.isNotBlank(resmushUrl)){
            try(CloseableHttpClient client = HttpClients.createDefault()){

                final URI uri = new URIBuilder(resmushUrl)
                        .addParameter("qlty", "80")
                        .build();

                final ContentType contentType = ContentType.getByMimeType(imageFormat.getMimeType());
                final String filename = "compressedImage." + imageFormat.getExtension();

                HttpPost httpPost = new HttpPost(uri);
                HttpEntity multipartForm = MultipartEntityBuilder.create()
                        .addBinaryBody("files", image, contentType, filename)
                        .build();
                httpPost.setEntity(multipartForm);

                CloseableHttpResponse response = client.execute(httpPost);

                JSONObject jsonObject = (JSONObject) new JSONParser().parse(new InputStreamReader(response.getEntity().getContent()));
                if(!jsonObject.containsKey("error")){
                    return jsonObject;
                }else{
                    logger.error("Error while sending image for compression using Resmush\n" + jsonObject);
                }
            } catch (IOException | URISyntaxException | ParseException e) {
                logger.error("Error while sending image for compression using Resmush", e);
            }
        }

        throw new CompressionImageException();
    }

    private byte[] sendImageRequest(String compressedImageUrl) throws CompressionImageException {
        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            HttpGet httpget = new HttpGet(compressedImageUrl);
            HttpResponse httpresponse = httpclient.execute(httpget);

            if(httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                return httpresponse.getEntity().getContent().readAllBytes();
            }else{
                logger.error("Error while getting compressed image using Resmush, url: " + compressedImageUrl);
            }
        } catch (IOException e) {
            logger.error("Error while getting compressed image using Resmush", e);
        }

        throw new CompressionImageException();
    }
}
