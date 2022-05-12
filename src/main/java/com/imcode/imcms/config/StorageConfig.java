package com.imcode.imcms.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.impl.cloud.CloudStorageClient;
import com.imcode.imcms.storage.impl.disk.DiskStorageClient;
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
    public StorageClient imageStorageClient(@Value("${s3.image.permission}") String cloudStorage,
                                            ServletContext servletContext, AmazonS3 amazonS3Client) {
        boolean isCloudStorage = Boolean.parseBoolean(cloudStorage);

        if(isCloudStorage){
            if(!amazonS3Client.doesBucketExistV2(bucketName)) amazonS3Client.createBucket(bucketName);

            return new CloudStorageClient(amazonS3Client, bucketName, CannedAccessControlList.PublicRead);
        }else{
            return new DiskStorageClient(Paths.get(servletContext.getRealPath("/")));
        }
    }

    private boolean validateProperty(String property) {
        return property != null && !property.isEmpty();
    }


    @Bean(name = "storageImagePath")
    public String storageImagePath(@Value("${s3.image.permission}") String cloudStorage,
                                   @Value("${ImagePath}") String imagePath,
                                   ServletContext servletContext, AmazonS3 amazonS3Client){
        boolean isCloudStorage = Boolean.parseBoolean(cloudStorage);
        return (isCloudStorage ? amazonS3Client.getUrl(bucketName, "") : servletContext.getContextPath() + "/" ) + imagePath;
    }
}