package com.imcode.imcms.storage;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import com.imcode.imcms.storage.impl.SynchronizedDiskToCloudStorageClient;
import com.imcode.imcms.storage.impl.cloud.CloudStorageClient;
import com.imcode.imcms.storage.impl.disk.DiskStorageClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static org.junit.jupiter.api.Assertions.*;

public class SynchronizedDiskToCloudStorageClientTest extends WebAppSpringTestConfig {

    @Value("${s3.access.key}")
    private String s3AccessKey;
    @Value("${s3.secret.key}")
    private String s3SecretKey;
    @Value("${s3.server.url}")
    private String s3ServerUrl;
    @Value("${s3.bucket.name}")
    private String bucket;

    private AmazonS3 amazonS3Client;
    private SynchronizedDiskToCloudStorageClient synchronizedDiskToCloudStorageClient;

    final String mainFilePath = "file.txt";
    final String mainFolderPath = "testFolder1";

    @PostConstruct
    private void initStorage(){
        amazonS3Client = AmazonS3ClientBuilder.standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3ServerUrl, ""))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3AccessKey, s3SecretKey))).build();

        CloudStorageClient cloudStorageClient = new CloudStorageClient(amazonS3Client, bucket);
        DiskStorageClient diskStorageClient = new DiskStorageClient(Paths.get(""));

        synchronizedDiskToCloudStorageClient = new SynchronizedDiskToCloudStorageClient(diskStorageClient, cloudStorageClient);
    }

    @AfterEach
    public void cleanUp() throws IOException {
        amazonS3Client.listObjectsV2(bucket, mainFolderPath)
                .getObjectSummaries()
                .forEach(s3ObjectSummary -> amazonS3Client.deleteObject(bucket, s3ObjectSummary.getKey()));

        amazonS3Client.deleteObject(bucket, mainFilePath);

        if(Files.exists(Paths.get(mainFolderPath))) FileUtils.forceDelete(new File(mainFolderPath));
    }

    @Test
    public void getFile_When_FileExistsOnDisk_Expected_StorageFile() throws IOException {
        StoragePath filePath = StoragePath.get(FILE, mainFolderPath, "file.txt");

        final String someText = "some text";
        Files.createDirectory(Paths.get(mainFolderPath));
        Files.writeString(Paths.get(filePath.toString()), someText);

        try(StorageFile file = synchronizedDiskToCloudStorageClient.getFile(filePath)){
            assertEquals(someText, IOUtils.toString(file.getContent(), String.valueOf(StandardCharsets.UTF_8)));
        }
    }

    @Test
    public void getFile_When_FileExistsOnCloud_And_DoNotExistOnDisk_And_ParentDirectoryDoNotExistOnDisk_Expected_StorageFile_And_DirectoryCreated_And_FileSavedToDisk() throws IOException {
        StoragePath file = StoragePath.get(FILE, mainFolderPath, mainFilePath);

        final String text = "some text";

        createObject(text.getBytes(StandardCharsets.UTF_8), file.toString());

        assertFalse(Files.exists(Paths.get(file.toString())));

        try(StorageFile storageFile = synchronizedDiskToCloudStorageClient.getFile(file)){
            String textResult = org.apache.commons.io.IOUtils.toString(storageFile.getContent(), StandardCharsets.UTF_8);
            assertEquals(text, textResult);
        }

        assertArrayEquals(text.getBytes(), Files.readAllBytes(Paths.get(file.toString())));
    }

    @Test
    public void getFile_When_FileDoNotExistOnDiskAndCloud_Expected_StorageFileNotFoundException(){
        StoragePath file = StoragePath.get(FILE, mainFolderPath, mainFilePath);

        assertFalse(amazonS3Client.doesObjectExist(bucket, file.toString()));
        assertFalse(Files.exists(Paths.get(file.toString())));

        assertThrows(StorageFileNotFoundException.class, () -> synchronizedDiskToCloudStorageClient.getFile(file));
    }

    @Test
    public void listPaths_When_FilesExistOnCloud_And_DoNotExistOnDisk_Expected_ListStoragePathsFromCloud(){
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

        assertFalse(Files.exists(Paths.get(folderPath1.toString())));

        List<StoragePath> storagePaths = synchronizedDiskToCloudStorageClient.listPaths(folderPath12);
        assertEquals(2, storagePaths.size());
        assertTrue(storagePaths.contains(filePath12));
        assertTrue(storagePaths.contains(folderPath123));
    }

    @Test
    public void listPaths_When_FilesInFolderDoNotExist_Expected_EmptyList(){
        StoragePath folderPath = StoragePath.get(DIRECTORY, mainFolderPath);
        createObject(folderPath.toString());

        assertTrue(synchronizedDiskToCloudStorageClient.listPaths(folderPath).isEmpty());
    }

    @Test
    public void listPaths_When_DirectoryDoesNotExist_Expected_StorageFileNotFoundException(){
        StoragePath file = StoragePath.get(DIRECTORY, "nonExistent");
        assertThrows(StorageFileNotFoundException.class, () -> synchronizedDiskToCloudStorageClient.listPaths(file));
    }

    @Test
    public void walk_When_DirectoryAndFilesExistOnCloud_And_DirectoryAndFilesDoNotExistOnDisk_Expected_ListStoragePaths(){
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

        assertFalse(Files.exists(Paths.get(folderPath1.toString())));

        List<StoragePath> storagePaths = synchronizedDiskToCloudStorageClient.walk(folderPath1);
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

        final List<StoragePath> storagePaths = synchronizedDiskToCloudStorageClient.walk(folderPath1);
        assertEquals(1, storagePaths.size());
        assertTrue(storagePaths.contains(folderPath1));
    }

    @Test
    public void walk_When_DirectoryDoesNotExist_Expected_CorrectException(){
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);
        assertThrows(StorageFileNotFoundException.class, () -> synchronizedDiskToCloudStorageClient.walk(folderPath1));
    }

    @Test
    public void exists_When_FileExistsOnCloud_And_DoNotExistOnDisk_Expected_True(){
        StoragePath filePath = StoragePath.get(FILE, mainFolderPath, mainFilePath);
        createObject(filePath.getParentPath().toString());
        createObject(filePath.toString());

        assertFalse(Files.exists(Paths.get(filePath.toString())));
        assertTrue(amazonS3Client.doesObjectExist(bucket, filePath.toString()));

        assertTrue(synchronizedDiskToCloudStorageClient.exists(filePath));
    }

    @Test
    public void exists_When_FileExistsOnDisk_And_DoNotExistOnCloud_Expected_True() throws IOException {
        StoragePath filePath = StoragePath.get(FILE, mainFolderPath, mainFilePath);
        Files.createDirectory(Paths.get(filePath.getParentPath().toString()));
        Files.createFile(Paths.get(filePath.toString()));

        assertTrue(Files.exists(Paths.get(filePath.toString())));
        assertFalse(amazonS3Client.doesObjectExist(bucket, filePath.toString()));

        assertTrue(synchronizedDiskToCloudStorageClient.exists(filePath));
    }

    @Test
    public void exists_When_FileDoNotExistsOnDiskAndCloud_Expected_False(){
        StoragePath filePath = StoragePath.get(FILE, mainFolderPath, mainFilePath);

        assertFalse(Files.exists(Paths.get(filePath.toString())));
        assertFalse(amazonS3Client.doesObjectExist(bucket, filePath.toString()));

        assertFalse(synchronizedDiskToCloudStorageClient.exists(filePath));
    }

    @Test
    public void create_When_FileTypeIsFile_Expected_CreatedFileOnDiskAndCloud() throws IOException {
        StoragePath filePath = StoragePath.get(FILE, mainFolderPath, mainFilePath);
        Files.createDirectory(Paths.get(filePath.getParentPath().toString()));
        createObject(filePath.getParentPath().toString());

        assertFalse(Files.exists(Paths.get(filePath.toString())));
        assertFalse(amazonS3Client.doesObjectExist(bucket, filePath.toString()));

        synchronizedDiskToCloudStorageClient.create(filePath);

        assertTrue(Files.exists(Paths.get(filePath.toString())));
        assertTrue(amazonS3Client.doesObjectExist(bucket, filePath.toString()));
    }

    @Test
    public void create_When_FileTypeIsDirectory_Expected_CreateDirectoryOnDiskAndCloud(){
        StoragePath folderPath = StoragePath.get(DIRECTORY, mainFolderPath);

        assertFalse(Files.exists(Paths.get(folderPath.toString())));
        assertFalse(amazonS3Client.doesObjectExist(bucket, folderPath.toString()));

        synchronizedDiskToCloudStorageClient.create(folderPath);

        assertTrue(Files.exists(Paths.get(folderPath.toString())));
        assertTrue(amazonS3Client.doesObjectExist(bucket, folderPath.toString()));
    }

    @Test
    public void put_Expected_CreateFileWithContentOnDiskAndCloud() throws IOException, InterruptedException {
        StoragePath filePath = StoragePath.get(FILE, mainFolderPath, mainFilePath);
        final String text = "some text";

        Files.createDirectory(Paths.get(filePath.getParentPath().toString()));
        createObject(filePath.getParentPath().toString());

        assertFalse(Files.exists(Paths.get(filePath.toString())));
        assertFalse(amazonS3Client.doesObjectExist(bucket, filePath.toString()));

        synchronizedDiskToCloudStorageClient.put(filePath, new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));

        assertTrue(Files.exists(Paths.get(filePath.toString())));
        assertArrayEquals(text.getBytes(), Files.readAllBytes(Paths.get(filePath.toString())));
        assertTrue(amazonS3Client.doesObjectExist(bucket, filePath.toString()));

        Thread.sleep(1000);     // wait for the file to upload to the cloud
        try(S3Object s3Object = amazonS3Client.getObject(bucket, filePath.toString())){
            InputStream inputStream = s3Object.getObjectContent();
            assertArrayEquals(text.getBytes(), inputStream.readAllBytes());
        }
    }

    @Test
    public void move_When_FileTypeIsFile_Expected_MoveFileOnDiskAndCloud() throws IOException {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2");
        StoragePath filePath1 = StoragePath.get(FILE, mainFolderPath, "file.txt");

        createObject(folderPath1.toString());
        createObject(folderPath12.toString());
        createObject(filePath1.toString());
        Files.createDirectories(Paths.get(folderPath12.toString()));
        Files.createFile(Paths.get(filePath1.toString()));

        StoragePath fromPath = StoragePath.get(FILE, mainFolderPath, "file.txt");
        StoragePath toPath = StoragePath.get(FILE, mainFolderPath, "folder2/file.txt");

        synchronizedDiskToCloudStorageClient.move(fromPath, toPath);

        assertTrue(amazonS3Client.doesObjectExist(bucket, toPath.toString()));
        assertFalse(amazonS3Client.doesObjectExist(bucket, fromPath.toString()));
        assertFalse(Files.exists(Paths.get(fromPath.toString())));
        assertTrue(Files.exists(Paths.get(toPath.toString())));
    }

    @Test
    public void move_When_FileTypeIsDirectory_Expected_MoveDirectoryOnDiskAndCloud() throws IOException {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);

        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolderPath, "folder2/file.txt");

        StoragePath folderPath13 = StoragePath.get(DIRECTORY, mainFolderPath, "folder3");

        createObject(folderPath1.toString());
        createObject(folderPath12.toString());
        createObject(filePath12.toString());
        createObject(folderPath13.toString());
        Files.createDirectories(Paths.get(folderPath12.toString()));
        Files.createFile(Paths.get(filePath12.toString()));
        Files.createDirectory(Paths.get(folderPath13.toString()));

        StoragePath fromPath = folderPath12;
        StoragePath toPath = StoragePath.get(DIRECTORY, mainFolderPath, "folder3/folder2");

        synchronizedDiskToCloudStorageClient.move(fromPath, toPath);

        assertTrue(amazonS3Client.doesObjectExist(bucket, toPath.toString()));
        assertTrue(amazonS3Client.doesObjectExist(bucket, mainFolderPath + "/folder3/folder2/file.txt"));
        assertFalse(amazonS3Client.doesObjectExist(bucket, fromPath.toString()));
        assertFalse(amazonS3Client.doesObjectExist(bucket, filePath12.toString()));
        assertTrue(Files.exists(Paths.get(toPath.toString())));
        assertTrue(Files.exists(Paths.get(mainFolderPath, "/folder3/folder2/file.txt")));
        assertFalse(Files.exists(Paths.get(fromPath.toString())));
        assertFalse(Files.exists(Paths.get(filePath12.toString())));
    }

    @Test
    public void copy_When_FileTypeIsFile_Expected_MoveFileOnDiskAndCloud() throws IOException {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2");
        StoragePath filePath1 = StoragePath.get(FILE, mainFolderPath, "file.txt");

        createObject(folderPath1.toString());
        createObject(folderPath12.toString());
        createObject(filePath1.toString());
        Files.createDirectories(Paths.get(folderPath12.toString()));
        Files.createFile(Paths.get(filePath1.toString()));

        StoragePath fromPath = StoragePath.get(FILE, mainFolderPath, "file.txt");
        StoragePath toPath = StoragePath.get(FILE, mainFolderPath, "folder2/file.txt");

        synchronizedDiskToCloudStorageClient.copy(fromPath, toPath);

        assertTrue(amazonS3Client.doesObjectExist(bucket, toPath.toString()));
        assertTrue(amazonS3Client.doesObjectExist(bucket, fromPath.toString()));
        assertTrue(Files.exists(Paths.get(toPath.toString())));
        assertTrue(Files.exists(Paths.get(fromPath.toString())));
    }

    @Test
    public void copy_When_FileTypeIsDirectory_Expected_MoveDirectoryOnDiskAndCloud() throws IOException {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolderPath);

        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolderPath, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolderPath, "folder2/file.txt");

        StoragePath folderPath13 = StoragePath.get(DIRECTORY, mainFolderPath, "folder3");

        createObject(folderPath1.toString());
        createObject(folderPath12.toString());
        createObject(filePath12.toString());
        createObject(folderPath13.toString());
        Files.createDirectories(Paths.get(folderPath12.toString()));
        Files.createFile(Paths.get(filePath12.toString()));
        Files.createDirectory(Paths.get(folderPath13.toString()));

        StoragePath fromPath = folderPath12;
        StoragePath toPath = StoragePath.get(DIRECTORY, mainFolderPath, "folder3/folder2");

        synchronizedDiskToCloudStorageClient.copy(fromPath, toPath);

        assertTrue(amazonS3Client.doesObjectExist(bucket, toPath.toString()));
        assertTrue(amazonS3Client.doesObjectExist(bucket, mainFolderPath + "/folder3/folder2/file.txt"));
        assertTrue(amazonS3Client.doesObjectExist(bucket, fromPath.toString()));
        assertTrue(amazonS3Client.doesObjectExist(bucket, filePath12.toString()));
        assertTrue(Files.exists(Paths.get(toPath.toString())));
        assertTrue(Files.exists(Paths.get(mainFolderPath, "/folder3/folder2/file.txt")));
        assertTrue(Files.exists(Paths.get(fromPath.toString())));
        assertTrue(Files.exists(Paths.get(filePath12.toString())));
    }

    @Test
    public void delete_When_FileTypeIsFile_Expected_DeleteFileOnDiskAndCloud() throws IOException {
        StoragePath filePath = StoragePath.get(FILE, mainFolderPath, mainFilePath);
        Files.createDirectory(Paths.get(filePath.getParentPath().toString()));
        createObject(filePath.getParentPath().toString());
        Files.createFile(Paths.get(filePath.toString()));
        createObject(filePath.toString());

        assertTrue(Files.exists(Paths.get(filePath.toString())));
        assertTrue(amazonS3Client.doesObjectExist(bucket, filePath.toString()));

        synchronizedDiskToCloudStorageClient.delete(filePath, false);

        assertFalse(Files.exists(Paths.get(filePath.toString())));
        assertFalse(amazonS3Client.doesObjectExist(bucket, filePath.toString()));
    }

    @Test
    public void delete_When_FileTypeIsDirectory_And_Force_Expected_DeleteDirectoryOnDiskAndCloud() throws IOException {
        StoragePath filePath = StoragePath.get(FILE, mainFolderPath, mainFilePath);
        Files.createDirectory(Paths.get(filePath.getParentPath().toString()));
        createObject(filePath.getParentPath().toString());
        Files.createFile(Paths.get(filePath.toString()));
        createObject(filePath.toString());

        assertTrue(Files.exists(Paths.get(filePath.toString())));
        assertTrue(amazonS3Client.doesObjectExist(bucket, filePath.toString()));

        synchronizedDiskToCloudStorageClient.delete(filePath.getParentPath(), true);

        assertFalse(Files.exists(Paths.get(filePath.getParentPath().toString())));
        assertFalse(amazonS3Client.doesObjectExist(bucket, filePath.getParentPath().toString()));
    }

    @Test
    public void delete_When_FileTypeIsDirectory_And_NotForce_Expected_DoNotDeleteDirectory() throws IOException {
        StoragePath filePath = StoragePath.get(FILE, mainFolderPath, mainFilePath);
        Files.createDirectory(Paths.get(filePath.getParentPath().toString()));
        createObject(filePath.getParentPath().toString());
        Files.createFile(Paths.get(filePath.toString()));
        createObject(filePath.toString());

        assertTrue(Files.exists(Paths.get(filePath.toString())));
        assertTrue(amazonS3Client.doesObjectExist(bucket, filePath.toString()));

        assertThrows(DirectoryNotEmptyException.class, () -> synchronizedDiskToCloudStorageClient.delete(filePath.getParentPath(), false));
    }

    @Test
    public void delete_When_FileExistsOnCloudAndDoNotExistOnDisk_Expected_DeleteDirectoryOnCloud() {
        StoragePath filePath = StoragePath.get(FILE, mainFolderPath, mainFilePath);
        createObject(filePath.getParentPath().toString());
        createObject(filePath.toString());

        assertFalse(Files.exists(Paths.get(filePath.toString())));
        assertTrue(amazonS3Client.doesObjectExist(bucket, filePath.toString()));

        synchronizedDiskToCloudStorageClient.delete(filePath, false);

        assertFalse(Files.exists(Paths.get(filePath.toString())));
        assertFalse(amazonS3Client.doesObjectExist(bucket, filePath.toString()));
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
