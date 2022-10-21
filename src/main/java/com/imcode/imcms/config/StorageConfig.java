package com.imcode.imcms.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.imcode.imcms.servlet.ImageFetcher;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StorageLocation;
import com.imcode.imcms.storage.impl.SynchronizedDiskToCloudStorageClient;
import com.imcode.imcms.storage.impl.cloud.CloudStorageClient;
import com.imcode.imcms.storage.impl.disk.DiskStorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import java.nio.file.Paths;

@Configuration
public class StorageConfig {
    @Value("${s3.access.key:#{null}}")
    private String s3AccessKey;
    @Value("${s3.secret.key:#{null}}")
    private String s3SecretKey;
    @Value("${s3.server.url:#{null}}")
    private String s3ServerUrl;
    @Value("${s3.bucket.name}#{null}")
    private String bucketName;

    @Bean
    public AmazonS3 amazonS3Client() {
        if (!(validateProperty(s3AccessKey) && validateProperty(s3SecretKey) && validateProperty(s3ServerUrl))) return null;

        return AmazonS3ClientBuilder.standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3ServerUrl, ""))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3AccessKey, s3SecretKey))).build();
    }

    @Bean(name = "imageStorageClient")
    public StorageClient imageStorageClient(@Value("${image.storage.location}") String imageStorageLocation,
                                            ServletContext servletContext,
                                            @Autowired(required = false) AmazonS3 amazonS3Client) {
        return createStorageClient(imageStorageLocation, servletContext, amazonS3Client);
    }

    @Bean(name = "storageImagePath")
    public String storageImagePath(ServletContext servletContext){
        return servletContext.getContextPath() + "/" + ImageFetcher.URL;
    }

    @Bean(name = "fileDocumentStorageClient")
    public StorageClient fileDocumentStorageClient(@Value("${file.storage.location}") String fileDocumentStorageLocation,
                                            ServletContext servletContext,
                                            @Autowired(required = false) AmazonS3 amazonS3Client) {
        return createStorageClient(fileDocumentStorageLocation, servletContext, amazonS3Client);
    }

    private StorageClient createStorageClient(String storageLocation, ServletContext servletContext,
                                              AmazonS3 amazonS3Client) {
        StorageClient imageStorageClient;

        switch (StorageLocation.getByName(storageLocation)) {
            case CLOUD:
                if(!amazonS3Client.doesBucketExistV2(bucketName)) amazonS3Client.createBucket(bucketName);
                imageStorageClient = new CloudStorageClient(amazonS3Client, bucketName);
                break;
            case SYNC:
                if(!amazonS3Client.doesBucketExistV2(bucketName)) amazonS3Client.createBucket(bucketName);
                DiskStorageClient diskStorageClient = new DiskStorageClient(Paths.get(servletContext.getRealPath("/")));
                CloudStorageClient cloudStorageClient = new CloudStorageClient(amazonS3Client, bucketName);

                imageStorageClient = new SynchronizedDiskToCloudStorageClient(diskStorageClient, cloudStorageClient);
                break;
            case DISK:
            default:
                imageStorageClient = new DiskStorageClient(Paths.get(servletContext.getRealPath("/")));
        }

        return imageStorageClient;
    }

    private boolean validateProperty(String property) {
        return property != null && !property.isEmpty();
    }
}