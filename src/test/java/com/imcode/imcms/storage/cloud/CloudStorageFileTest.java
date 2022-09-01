package com.imcode.imcms.storage.cloud;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.impl.cloud.CloudStorageFile;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;

import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static org.junit.jupiter.api.Assertions.*;

public class CloudStorageFileTest extends WebAppSpringTestConfig {

    @Value("${s3.access.key}")
    private String s3AccessKey;
    @Value("${s3.secret.key}")
    private String s3SecretKey;
    @Value("${s3.server.url}")
    private String s3ServerUrl;
    @Value("${s3.bucket.name}")
    private String bucket;

    private AmazonS3 amazonS3Client;
    final String mainFilePath = "file.txt";

    @PostConstruct
    private void initStorage(){
        amazonS3Client = AmazonS3ClientBuilder.standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3ServerUrl, ""))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3AccessKey, s3SecretKey))).build();
    }

    @AfterEach
    public void cleanUp() {
        amazonS3Client.deleteObject(bucket, mainFilePath);
    }

    @Test
    public void getContent_Expected_InputStreamWithContent() throws Exception {
        final StoragePath filePath = StoragePath.get(FILE, mainFilePath);
        final String text = "some text";
        createObject(text.getBytes(StandardCharsets.UTF_8), filePath.toString());

        final S3Object object = amazonS3Client.getObject(bucket, filePath.toString());

        //use reflection to create an instance
        Constructor<?> privateConstructor = CloudStorageFile.class.getDeclaredConstructors()[0];
        privateConstructor.setAccessible(true);
        CloudStorageFile cloudStorageFile  = (CloudStorageFile) privateConstructor.newInstance(object);

        InputStream inputStream = cloudStorageFile.getContent();
        assertEquals(text, IOUtils.toString(inputStream, StandardCharsets.UTF_8));

        //check getting stream again
        inputStream = cloudStorageFile.getContent();
        assertEquals(text, IOUtils.toString(inputStream, StandardCharsets.UTF_8));
    }

    @Test
    public void close_Expected_UnableGetContent() throws Exception {
        final StoragePath filePath = StoragePath.get(FILE, mainFilePath);
        createObject(new byte[0], filePath.toString());

        final S3Object object = amazonS3Client.getObject(bucket, filePath.toString());

        //use reflection to create an instance
        Constructor<?> privateConstructor = CloudStorageFile.class.getDeclaredConstructors()[0];
        privateConstructor.setAccessible(true);
        CloudStorageFile cloudStorageFile  = (CloudStorageFile) privateConstructor.newInstance(object);

        assertDoesNotThrow(() -> cloudStorageFile.getContent());

        cloudStorageFile.close();

        assertThrows(NullPointerException.class, () -> cloudStorageFile.getContent());
    }

    private void createObject(byte[] content, String key){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(content.length);
        InputStream inputStream = new ByteArrayInputStream(content);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, inputStream, metadata);
        amazonS3Client.putObject(putObjectRequest);
    }
}
