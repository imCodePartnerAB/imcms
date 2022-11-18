package com.imcode.imcms.storage.disk;

import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.ConflictFileTypeException;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import com.imcode.imcms.storage.impl.disk.DiskStorageClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static org.junit.jupiter.api.Assertions.*;

public class DiskStorageClientTest {

    final DiskStorageClient storageClient = new DiskStorageClient(Paths.get(""));

    final String mainFolder1Path = "testFolder1";

    @BeforeEach
    public void createData() throws IOException {
        Files.createDirectory(Paths.get(mainFolder1Path));
    }

    @AfterEach
    public void cleanUp() throws IOException {
        FileUtils.forceDelete(new File(mainFolder1Path));
    }

    @Test
    public void getFile_When_FileExists_Expected_StorageFile() throws IOException {
        StoragePath filePath1 = StoragePath.get(FILE, mainFolder1Path, "file.txt");

        final String someText = "some text";
        Files.write(Paths.get(filePath1.toString()), someText.getBytes(StandardCharsets.UTF_8));

        try(StorageFile file = storageClient.getFile(filePath1)){
            assertEquals(someText, IOUtils.toString(file.getContent(), String.valueOf(StandardCharsets.UTF_8)));
        }
    }

    @Test
    public void getFile_When_FileDoesNotExist_Expected_StorageFileNotFoundException() {
        StoragePath filePath = StoragePath.get(FILE, "nonExistent.txt");
        assertThrows(StorageFileNotFoundException.class, () -> storageClient.getFile(filePath));
    }

    @Test
    public void listPaths_When_FilesExist_Expected_ListStoragePaths() throws IOException {
        StoragePath filePath1_2 = StoragePath.get(FILE, mainFolder1Path, "file2.txt");
        StoragePath filePath1_3 = StoragePath.get(FILE, mainFolder1Path, "file3.txt");
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolder1Path, "folder2/file.txt");

        Files.createFile(Paths.get(filePath1_2.toString()));
        Files.createFile(Paths.get(filePath1_3.toString()));
        Files.createDirectory(Paths.get(folderPath12.toString()));
        Files.createFile(Paths.get(filePath12.toString()));

        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolder1Path);
        List<StoragePath> storagePaths = storageClient.listPaths(folderPath1);

        assertEquals(3, storagePaths.size());
        assertTrue(storagePaths.contains(filePath1_2));
        assertTrue(storagePaths.contains(filePath1_3));
        assertTrue(storagePaths.contains(folderPath12));
    }

    @Test
    public void listPaths_When_FolderDoesNotExist_Expected_StorageFileNotFoundException(){
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, "nonExistent");
        assertThrows(StorageFileNotFoundException.class, () -> storageClient.listPaths(folderPath1));

    }

    @Test
    public void listPaths_When_FilesInFolderDoNotExist_Expected_EmptyList() {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolder1Path);
        assertTrue(storageClient.listPaths(folderPath1).isEmpty());
    }

    @Test
    public void walk_When_DirectoryExists_And_FilesExist_Expected_ListNestedStoragePaths() throws IOException {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolder1Path);
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolder1Path, "folder2/file.txt");
        StoragePath folderPath123 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2/folder3");
        StoragePath filePath123 = StoragePath.get(FILE, mainFolder1Path, "folder2/folder3/file.txt");

        Files.createDirectory(Paths.get(folderPath12.toString()));
        Files.createFile(Paths.get(filePath12.toString()));
        Files.createDirectory(Paths.get(folderPath123.toString()));
        Files.createFile(Paths.get(filePath123.toString()));

        List<StoragePath> storagePaths = storageClient.walk(folderPath1);
        assertEquals(5, storagePaths.size());
        assertTrue(storagePaths.contains(folderPath1));
        assertTrue(storagePaths.contains(folderPath12));
        assertTrue(storagePaths.contains(filePath12));
        assertTrue(storagePaths.contains(folderPath123));
        assertTrue(storagePaths.contains(filePath123));
    }

    @Test
    public void walk_When_DirectoryExists_And_FilesDoNotExist_Expected_ListWithFolderOnly(){
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolder1Path);
        final List<StoragePath> storagePaths = storageClient.walk(folderPath1);
        assertEquals(1, storagePaths.size());
        assertTrue(storagePaths.contains(folderPath1));
    }

    @Test
    public void walk_When_DirectoryDoesNotExist_Expected_CorrectException(){
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2");
        assertThrows(StorageFileNotFoundException.class, () -> storageClient.walk(folderPath12));
    }

    @Test
    public void exists_When_FileTypeIsDirectory_And_FolderExists_Expected_True() {
        StoragePath folderPath1 = StoragePath.get(DIRECTORY, mainFolder1Path);
        assertTrue(storageClient.exists(folderPath1));
    }

    @Test
    public void exists_When_FileTypeIsFile_And_FileExists_Expected_True() throws IOException {
        StoragePath filePath1 = StoragePath.get(FILE, mainFolder1Path, "file.txt");

        Files.createFile(Paths.get(filePath1.toString()));

        assertTrue(storageClient.exists(filePath1));
    }

    @Test
    public void exists_When_FileTypeIsFile_And_FileExists_Expected_False() {
        StoragePath filePath = StoragePath.get(FILE, "nonExistent.txt");
        assertFalse(storageClient.exists(filePath));
    }

    @Test
    public void create_When_FileTypeIsDirectory_Expected_CreateFolder() {
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2");
        storageClient.create(folderPath12);

        assertTrue(Files.exists(Paths.get(mainFolder1Path, "folder2")));
    }

    @Test
    public void create_When_FileTypeIsFile_Expected_CreateFile() {
        StoragePath filePath1 = StoragePath.get(FILE, mainFolder1Path, "file.txt");
        storageClient.create(filePath1);

        assertTrue(Files.exists(Paths.get(filePath1.toString())));
    }

    @Test
    public void put_When_FileTypeIsFile_Expected_CreateFileWithContent() throws IOException {
        StoragePath filePath1 = StoragePath.get(FILE, mainFolder1Path, "file.txt");

        final String text = "some text";
        storageClient.put(filePath1, new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));

        assertEquals(text, new String(Files.readAllBytes(Paths.get(filePath1.toString()))));
    }

    @Test
    public void move_When_FileTypeIsFile_Expected_MoveFile() throws IOException {
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolder1Path, "folder2/file.txt");
        StoragePath folderPath13 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder3");

        Files.createDirectory(Paths.get(folderPath12.toString()));
        Files.createFile(Paths.get(filePath12.toString()));
        Files.createDirectory(Paths.get(folderPath13.toString()));

        StoragePath fromPath = filePath12;
        StoragePath toPath = StoragePath.get(FILE, mainFolder1Path, "folder3/file.txt");
        storageClient.move(fromPath, toPath);

        assertTrue(Files.exists(Paths.get(folderPath12.toString())));
        assertFalse(Files.exists(Paths.get(fromPath.toString())));
        assertTrue(Files.exists(Paths.get(toPath.toString())));
    }

    @Test
    public void move_When_FileTypeIsDirectory_Expected_RenameDirectory() throws IOException {
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolder1Path, "folder2/file.txt");

        Files.createDirectory(Paths.get(folderPath12.toString()));
        Files.createFile(Paths.get(filePath12.toString()));

        StoragePath fromPath = folderPath12;
        StoragePath toPath = StoragePath.get(DIRECTORY, mainFolder1Path, "folder3");

        storageClient.move(fromPath, toPath);

        assertTrue(Files.exists(Paths.get(toPath.toString())));
        assertTrue(Files.exists(Paths.get(mainFolder1Path, "folder3/file.txt")));
        assertFalse(Files.exists(Paths.get(fromPath.toString())));
    }

    @Test
    public void move_When_FileTypeIsDirectory_Expected_MoveDirectory() throws IOException {
        StoragePath folderPath123 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2/folder3");
        StoragePath filePath123 = StoragePath.get(FILE, mainFolder1Path, "folder2/folder3/file.txt");
        StoragePath folderPath14 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder4");

        Files.createDirectories(Paths.get(folderPath123.toString()));
        Files.createFile(Paths.get(filePath123.toString()));
        Files.createDirectory(Paths.get(folderPath14.toString()));

        StoragePath fromPath = folderPath123;
        StoragePath toPath = StoragePath.get(DIRECTORY, mainFolder1Path, "folder4/folder3");

        storageClient.move(fromPath, toPath);

        assertTrue(Files.exists(Paths.get(toPath.toString())));
        assertTrue(Files.exists(Paths.get(mainFolder1Path, "folder4/folder3/file.txt")));
        assertFalse(Files.exists(Paths.get(fromPath.toString())));
    }

    @Test
    public void move_When_TypesAreNotSame_Expected_ConflictFileTypeException() throws IOException {
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2");
        StoragePath filePath1 = StoragePath.get(FILE, mainFolder1Path, "file.txt");

        Files.createDirectory(Paths.get(folderPath12.toString()));
        Files.createFile(Paths.get(filePath1.toString()));

        StoragePath fromPath = filePath1;
        StoragePath toPath = StoragePath.get(DIRECTORY, "folder/folder2");

        assertThrows(ConflictFileTypeException.class, () -> storageClient.move(fromPath, toPath));
    }

    @Test
    public void copy_When_FileTypeIsFile_Expected_MoveFile() throws IOException {
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolder1Path, "folder2/file.txt");
        StoragePath folderPath13 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder3");

        Files.createDirectory(Paths.get(folderPath12.toString()));
        Files.createFile(Paths.get(filePath12.toString()));
        Files.createDirectory(Paths.get(folderPath13.toString()));

        StoragePath fromPath = filePath12;
        StoragePath toPath = StoragePath.get(FILE, mainFolder1Path, "folder3/file.txt");
        storageClient.copy(fromPath, toPath);

        assertTrue(Files.exists(Paths.get(folderPath12.toString())));
        assertTrue(Files.exists(Paths.get(fromPath.toString())));
        assertTrue(Files.exists(Paths.get(toPath.toString())));
    }

    @Test
    public void copy_When_FileTypeIsDirectory_Expected_MoveDirectory() throws IOException {
        StoragePath folderPath123 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2/folder3");
        StoragePath filePath123 = StoragePath.get(FILE, mainFolder1Path, "folder2/folder3/file.txt");
        StoragePath folderPath14 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder4");

        Files.createDirectories(Paths.get(folderPath123.toString()));
        Files.createFile(Paths.get(filePath123.toString()));
        Files.createDirectory(Paths.get(folderPath14.toString()));

        StoragePath fromPath = folderPath123;
        StoragePath toPath = StoragePath.get(DIRECTORY, mainFolder1Path, "folder4/folder3");

        storageClient.copy(fromPath, toPath);

        assertTrue(Files.exists(Paths.get(toPath.toString())));
        assertTrue(Files.exists(Paths.get(mainFolder1Path, "folder4/folder3/file.txt")));
        assertTrue(Files.exists(Paths.get(fromPath.toString())));
        assertTrue(Files.exists(Paths.get(filePath123.toString())));
    }

    @Test
    public void copy_When_TypesAreNotSame_Expected_ConflictFileTypeException() throws IOException {
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2");
        StoragePath filePath1 = StoragePath.get(FILE, mainFolder1Path, "file.txt");

        Files.createDirectory(Paths.get(folderPath12.toString()));
        Files.createFile(Paths.get(filePath1.toString()));

        StoragePath fromPath = filePath1;
        StoragePath toPath = StoragePath.get(DIRECTORY, "folder/folder2");

        assertThrows(ConflictFileTypeException.class, () -> storageClient.copy(fromPath, toPath));
    }

    @Test
    public void delete_When_FileTypeIsFile_Expected_DeleteFile() throws IOException {
        StoragePath filePath1 = StoragePath.get(FILE, mainFolder1Path, "file.txt");

        Files.createFile(Paths.get(filePath1.toString()));

        storageClient.delete(filePath1, false);
        assertFalse(Files.exists(Paths.get(filePath1.toString())));
    }

    @Test
    public void delete_When_FolderIsNotEmpty_And_Force_Expected_DeleteFolder() throws IOException {
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolder1Path, "folder2/file.txt");

        Files.createDirectory(Paths.get(folderPath12.toString()));
        Files.createFile(Paths.get(filePath12.toString()));

        storageClient.delete(folderPath12, true);
        assertFalse(Files.exists(Paths.get(folderPath12.toString())));
        assertFalse(Files.exists(Paths.get(filePath12.toString())));
    }

    @Test
    public void delete_When_FolderIsNotEmpty_And_NotForce_Expected_NotDeleteFolder() throws IOException {
        StoragePath folderPath12 = StoragePath.get(DIRECTORY, mainFolder1Path, "folder2");
        StoragePath filePath12 = StoragePath.get(FILE, mainFolder1Path, "folder2/file.txt");

        Files.createDirectory(Paths.get(folderPath12.toString()));
        Files.createFile(Paths.get(filePath12.toString()));

        assertThrows(DirectoryNotEmptyException.class, () -> storageClient.delete(folderPath12, false));
    }
}
