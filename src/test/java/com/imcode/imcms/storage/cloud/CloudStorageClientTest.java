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
import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.ConflictFileTypeException;
import com.imcode.imcms.storage.exception.FolderNotEmptyException;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import com.imcode.imcms.storage.impl.cloud.CloudStorageClient;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static org.junit.jupiter.api.Assertions.*;

public class CloudStorageClientTest extends WebAppSpringTestConfig {

    @Value("${s3.access.key}")
    private String s3AccessKey;
    @Value("${s3.secret.key}")
    private String s3SecretKey;
    @Value("${s3.server.url}")
    private String s3ServerUrl;
    @Value("${s3.bucket.name}")
    private String bucket;

    private AmazonS3 amazonS3Client;
    private CloudStorageClient cloudStorageClient;

    final String mainFilePath = "file.txt";
    final String mainFolderPath = "testFolder1";

    @PostConstruct
    private void initStorage(){
        amazonS3Client = AmazonS3ClientBuilder.standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3ServerUrl, ""))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3AccessKey, s3SecretKey))).build();

        cloudStorageClient = new CloudStorageClient(amazonS3Client, bucket);
    }

    @AfterEach
    public void cleanUp() {
        amazonS3Client.listObjectsV2(bucket, mainFolderPath)
                .getObjectSummaries()
                .forEach(s3ObjectSummary -> amazonS3Client.deleteObject(bucket, s3ObjectSummary.getKey()));

        amazonS3Client.deleteObject(bucket, mainFilePath);
    }

    @Test
    public void getFile_When_FileExists_Expected_StorageFile() throws IOException {
        StoragePath file = StoragePath.get(FILE, mainFilePath);

        final String text = "some text";

        createObject(text.getBytes(StandardCharsets.UTF_8), file.toString());

        try(StorageFile storageFile = cloudStorageClient.getFile(file)){
            String textResult = IOUtils.toString(storageFile.getContent(), StandardCharsets.UTF_8);
            assertEquals(text, textResult);
        }
    }

    @Test
    public void getFile_When_FileDoesNotExist_Expected_StorageFileNotFoundException(){
        StoragePath file = StoragePath.get(FILE, "nonExistent.txt");
        assertThrows(StorageFileNotFoundException.class, () -> cloudStorageClient.getFile(file));
    }

    @Test
    public void listPaths_When_FilesExist_Expected_ListStoragePaths() {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolderPath, "folder2/file.txt");
        StoragePath folderPath123 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2/folder3");
        StoragePath folderPath1234 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2/folder3/folder4");
        StoragePath filePath123 = StoragePath.get(FILE, mainFolderPath, "folder2/folder3/file.txt");
        StoragePath folderPath14 = StoragePath.get(DIRECTORY, mainFolderPath, "folder4");

        createObject(folderPath1.toString());
        createObject(folderPath12.toString());
        createObject(filePath12.toString());
        createObject(folderPath123.toString());
        createObject(folderPath1234.toString());
        createObject(filePath123.toString());
        createObject(folderPath14.toString());

        List<StoragePath> storagePaths = cloudStorageClient.listPaths(folderPath12);
        assertEquals(2, storagePaths.size());
        assertTrue(storagePaths.contains(filePath12));
        assertTrue(storagePaths.contains(folderPath123));
    }

    @Test
    public void listPaths_When_FilesInFolderDoNotExist_Expected_EmptyList(){
        StoragePath folderPath = StoragePath.get(DIRECTORY, mainFolderPath);
        createObject(folderPath.toString());

        assertTrue(cloudStorageClient.listPaths(folderPath).isEmpty());
    }

    @Test
    public void listPaths_When_DirectoryDoesNotExist_Expected_StorageFileNotFoundException(){
        StoragePath file = StoragePath.get(DIRECTORY, "nonExistent");
        assertThrows(StorageFileNotFoundException.class, () -> cloudStorageClient.listPaths(file));
    }

    @Test
    public void walk_When_DirectoryExists_And_FilesExist_Expected_ListStoragePaths(){
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolderPath, "folder2/file.txt");
        StoragePath folderPath123 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2/folder3");
        StoragePath filePath123 = StoragePath.get(FILE, mainFolderPath, "folder2/folder3/file.txt");
        StoragePath filePath = StoragePath.get(FILE, mainFilePath);

        createObject(folderPath1.toString());
        createObject(folderPath12.toString());
        createObject(filePath12.toString());
        createObject(folderPath123.toString());
        createObject(filePath123.toString());
        createObject(filePath.toString());

        List<StoragePath> storagePaths = cloudStorageClient.walk(folderPath1);
        assertEquals(5, storagePaths.size());
        assertTrue(storagePaths.contains(folderPath12));
        assertTrue(storagePaths.contains(filePath12));
        assertTrue(storagePaths.contains(folderPath123));
        assertTrue(storagePaths.contains(filePath123));
        assertFalse(storagePaths.contains(filePath));
    }

    @Test
    public void walk_When_DirectoryExists_And_FilesDoNotExist_Expected_ListWithRootPathOnly(){
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);
        createObject(folderPath1.toString());

        final List<StoragePath> storagePaths = cloudStorageClient.walk(folderPath1);
        assertEquals(1, storagePaths.size());
        assertTrue(storagePaths.contains(folderPath1));
    }

    @Test
    public void walk_When_DirectoryDoesNotExist_Expected_CorrectException(){
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);
        assertThrows(StorageFileNotFoundException.class, () -> cloudStorageClient.walk(folderPath1));
    }

        @Test
    public void exists_When_FileExists_Expected_True(){
        StoragePath filePath = StoragePath.get(FILE, mainFilePath);
        createObject(filePath.toString());

        assertTrue(cloudStorageClient.exists(filePath));
    }

    @Test
    public void exists_When_FileDoesNotExist_Expected_False(){
        StoragePath folderPath = StoragePath.get(DIRECTORY, "nonExistent");
        assertFalse(cloudStorageClient.exists(folderPath));
    }

    @Test
    public void create_When_FileTypeIsFile_Expected_CreateFile(){
        StoragePath filePath = StoragePath.get(FILE, mainFilePath);
        cloudStorageClient.create(filePath);

        assertTrue(amazonS3Client.doesObjectExist(bucket, filePath.toString()));
    }

    @Test
    public void create_When_FileTypeIsDirectory_Expected_CreateDirectory(){
        StoragePath folderPath = StoragePath.get(DIRECTORY, mainFolderPath);
        cloudStorageClient.create(folderPath);

        assertTrue(amazonS3Client.doesObjectExist(bucket, folderPath.toString()));
    }

    @Test
    public void put_Expected_CreateFileWithContent() throws IOException {
        final String text = "some text";

        StoragePath filePath = StoragePath.get(FILE, mainFilePath);
        cloudStorageClient.put(filePath, new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));

        try(S3Object s3Object = amazonS3Client.getObject(bucket, filePath.toString())){
            InputStream inputStream = s3Object.getObjectContent();
            assertEquals(text, IOUtils.toString(inputStream, StandardCharsets.UTF_8));
        }
    }

    @Test
    public void move_When_FileTypeIsFile_Expected_MoveFile() {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2");
        StoragePath filePath1 = StoragePath.get(FILE, mainFolderPath, "file.txt");

        createObject(folderPath1.toString());
        createObject(folderPath12.toString());
        createObject(filePath1.toString());

        StoragePath fromPath = StoragePath.get(FILE, mainFolderPath, "file.txt");
        StoragePath toPath = StoragePath.get(FILE, mainFolderPath, "folder2/file.txt");

        cloudStorageClient.move(fromPath, toPath);

        assertTrue(amazonS3Client.doesObjectExist(bucket, toPath.toString()));
        assertFalse(amazonS3Client.doesObjectExist(bucket, fromPath.toString()));
    }

    @Test
    public void move_When_FileTypeIsDirectory_Expected_MoveDirectory() {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);

        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolderPath, "folder2/file.txt");

        StoragePath folderPath13 = StoragePath.get(DIRECTORY, mainFolderPath, "folder3");

        createObject(folderPath1.toString());
        createObject(folderPath12.toString());
        createObject(filePath12.toString());
        createObject(folderPath13.toString());

        StoragePath fromPath = folderPath12;
        StoragePath toPath = StoragePath.get(DIRECTORY, mainFolderPath, "folder3/folder2");

        cloudStorageClient.move(fromPath, toPath);

        assertTrue(amazonS3Client.doesObjectExist(bucket, toPath.toString()));
        assertTrue(amazonS3Client.doesObjectExist(bucket, mainFolderPath + "/folder3/folder2/file.txt"));
        assertFalse(amazonS3Client.doesObjectExist(bucket, fromPath.toString()));
        assertFalse(amazonS3Client.doesObjectExist(bucket, filePath12.toString()));
    }

    @Test
    public void move_When_TypesAreNotSame_Expected_ConflictFileTypeException() {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolderPath, "folder2/file.txt");

        createObject(folderPath1.toString());
        createObject(folderPath12.toString());
        createObject(filePath12.toString());

        assertThrows(ConflictFileTypeException.class, () -> cloudStorageClient.move(filePath12, folderPath1));
    }

    @Test
    public void copy_When_FileTypeIsFile_Expected_MoveFile() {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2");
        StoragePath filePath1 = StoragePath.get(FILE, mainFolderPath, "file.txt");

        createObject(folderPath1.toString());
        createObject(folderPath12.toString());
        createObject(filePath1.toString());

        StoragePath fromPath = StoragePath.get(FILE, mainFolderPath, "file.txt");
        StoragePath toPath = StoragePath.get(FILE, mainFolderPath, "folder2/file.txt");

        cloudStorageClient.copy(fromPath, toPath);

        assertTrue(amazonS3Client.doesObjectExist(bucket, toPath.toString()));
        assertTrue(amazonS3Client.doesObjectExist(bucket, fromPath.toString()));
    }

    @Test
    public void copy_When_FileTypeIsDirectory_Expected_MoveDirectory() {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);

        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolderPath, "folder2/file.txt");

        StoragePath folderPath13 = StoragePath.get(DIRECTORY, mainFolderPath, "folder3");

        createObject(folderPath1.toString());
        createObject(folderPath12.toString());
        createObject(filePath12.toString());
        createObject(folderPath13.toString());

        StoragePath fromPath = folderPath12;
        StoragePath toPath = StoragePath.get(DIRECTORY, mainFolderPath, "folder3/folder2");

        cloudStorageClient.copy(fromPath, toPath);

        assertTrue(amazonS3Client.doesObjectExist(bucket, toPath.toString()));
        assertTrue(amazonS3Client.doesObjectExist(bucket, mainFolderPath + "/folder3/folder2/file.txt"));
        assertTrue(amazonS3Client.doesObjectExist(bucket, fromPath.toString()));
        assertTrue(amazonS3Client.doesObjectExist(bucket, filePath12.toString()));
    }

    @Test
    public void copy_When_TypesAreNotSame_Expected_ConflictFileTypeException() {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolderPath, "folder2/file.txt");

        createObject(folderPath1.toString());
        createObject(folderPath12.toString());
        createObject(filePath12.toString());

        assertThrows(ConflictFileTypeException.class, () -> cloudStorageClient.copy(filePath12, folderPath1));
    }

    @Test
    public void delete_When_FileTypeIsFile_Expected_DeleteFile(){
        StoragePath filePath = StoragePath.get(FILE, mainFilePath);
        cloudStorageClient.delete(filePath, false);

        assertFalse(amazonS3Client.doesObjectExist(bucket, filePath.toString()));
    }

    @Test
    public void delete_When_FileTypeIsDirectory_And_NotForce_Expected_DoNotDeleteDirectory() {
        StoragePath folderPath = StoragePath.get(DIRECTORY, mainFolderPath);
        StoragePath filePath = StoragePath.get(FILE, mainFolderPath, "file.txt");

        createObject(folderPath.toString());
        createObject(filePath.toString());

        assertThrows(FolderNotEmptyException.class, () -> cloudStorageClient.delete(folderPath, false));
    }

    @Test
    public void delete_When_FileTypeIsDirectory_And_Force_Expected_DeleteDirectory() {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);
        StoragePath filePath1 = StoragePath.get(FILE, mainFolderPath, "file.txt");
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolderPath, "folder2/file.txt");
        StoragePath filePath = StoragePath.get(FILE, mainFilePath);

        createObject(folderPath1.toString());
        createObject(filePath1.toString());
        createObject(folderPath12.toString());
        createObject(filePath12.toString());
        createObject(filePath.toString());

        cloudStorageClient.delete(folderPath1, true);
        assertFalse(amazonS3Client.doesObjectExist(bucket, folderPath1.toString()));
        assertFalse(amazonS3Client.doesObjectExist(bucket, filePath1.toString()));
        assertFalse(amazonS3Client.doesObjectExist(bucket, folderPath12.toString()));
        assertFalse(amazonS3Client.doesObjectExist(bucket, filePath12.toString()));
        assertTrue(amazonS3Client.doesObjectExist(bucket, filePath.toString()));
    }

    private void createObject(String key){
        createObject(new byte[0], key);
    }

    private void createObject(byte[] content, String key){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(content.length);
        InputStream inputStream = new ByteArrayInputStream(content);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, inputStream, metadata);
        amazonS3Client.putObject(putObjectRequest);
    }

}
