package com.imcode.imcms.storage;

import org.junit.jupiter.api.Test;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static org.junit.jupiter.api.Assertions.*;

public class StoragePathTest {

    @Test
    public void get_When_FileTypeIsFile_Expected_CorrectStoragePath() {
        final String folder1 = "folder1";
        final String folder2 = "folder2";
        final String file = "file.txt";

        final StoragePath resultPath = StoragePath.get(FILE, folder1, folder2, file);
        final String expectedPath = "folder1/folder2/file.txt";

        assertEquals(expectedPath, resultPath.toString());
    }

    @Test
    public void get_When_FileTypeIsDirectory_Expected_CorrectStoragePath() {
        final String folder12 = "/folder1/folder2/";
        final String folder3 = "/folder3/";

        final StoragePath resultPath = StoragePath.get(DIRECTORY, folder12, folder3);
        final String expectedPath = "folder1/folder2/folder3/";

        assertEquals(expectedPath, resultPath.toString());
    }

    @Test
    public void get_When_PathHasMultipleSlashes_Expected_CorrectStoragePath(){
        final String folder12 = "/folder1//folder2/";
        final String folder3 = "//folder3//";
        final String file = "//file.txt/";

        final StoragePath resultFolderPath = StoragePath.get(DIRECTORY, folder12, folder3);
        final String expectedFolderPath = "folder1/folder2/folder3/";
        assertEquals(expectedFolderPath, resultFolderPath.toString());

        final StoragePath resultFilePath = StoragePath.get(FILE, folder12, folder3, file);
        final String expectedFilePath = "folder1/folder2/folder3/file.txt";
        assertEquals(expectedFilePath, resultFilePath.toString());
    }

    @Test
    public void resolve_When_FileTypeIsFile_Expected_CorrectStoragePath() {
        final String folder12 = "/folder1/folder2/";
        final String file = "/file.txt";

        final StoragePath folderPath12 = StoragePath.get(DIRECTORY, folder12);
        final StoragePath resultPath = folderPath12.resolve(FILE, file);

        final String expectedPath = "folder1/folder2/file.txt";
        assertEquals(expectedPath, resultPath.toString());
    }

    @Test
    public void resolve_When_FileTypeIsDirectory_Expected_CorrectStoragePath() {
        final String folder12 = "/folder1/folder2/";
        final String folder3 = "folder3/";
        final String folder4 = "/folder4";

        final StoragePath folderPath12 = StoragePath.get(DIRECTORY, folder12);
        final StoragePath resultPath = folderPath12.resolve(DIRECTORY, folder3, folder4);

        final String expectedPath = "folder1/folder2/folder3/folder4/";
        assertEquals(expectedPath, resultPath.toString());
    }

    @Test
    public void resolve_When_ParameterIsStoragePath_And_Path_Expected_CorrectStoragePath() {
        final String folder1 = "/folder1/";
        final String folder2 = "folder2/";
        final String folder3 = "/folder3";
        final String file = "file.txt";

        final StoragePath folderPath1 = StoragePath.get(DIRECTORY, folder1);
        final StoragePath folderPath23 = StoragePath.get(DIRECTORY, folder2, folder3);
        final StoragePath filePath = StoragePath.get(FILE, file);

        final StoragePath resultPath = folderPath1.resolve(FILE, folderPath23, filePath);
        final String expectedPath = "folder1/folder2/folder3/file.txt";

        assertEquals(expectedPath, resultPath.toString());
    }

    @Test
    public void relativize_When_FileTypeIsDirectory_Expected_CorrectStoragePath(){
        final String folder1 = "/folder1/";
        final String folder2 = "folder2/";
        final String folder3 = "/folder3";

        final StoragePath folderPath12 = StoragePath.get(DIRECTORY, folder1, folder2);
        final StoragePath folderPath123 = StoragePath.get(DIRECTORY, folder1, folder2, folder3);

        final String expectedPath = "folder3/";
        assertEquals(expectedPath, folderPath12.relativize(folderPath123).toString());
    }

    @Test
    public void relativize_When_FileTypeIsFile_Expected_CorrectStoragePath(){
        final String folder1 = "/folder1/";
        final String folder2 = "folder2/";
        final String file = "file.txt";

        final StoragePath folderPath1 = StoragePath.get(DIRECTORY, folder1);
        final StoragePath filePath12 = StoragePath.get(FILE, folder1, folder2, file);

        final String expectedPath = "folder2/file.txt";
        assertEquals(expectedPath, folderPath1.relativize(filePath12).toString());
    }

    @Test
    public void getName_When_FileTypeIsDirectory_Expected_CorrectName() {
        final String folder1 = "/folder1/";
        final String folder2 = "folder2/";

        final StoragePath folderPath12 = StoragePath.get(DIRECTORY, folder1, folder2);

        final String expectedName = "folder2";
        assertEquals(expectedName, folderPath12.getName());
    }

    @Test
    public void getName_When_FileTypeIsDirectory_And_OneDirectory_Expected_CorrectName() {
        final String folder1 = "folder1";

        final StoragePath folderPath1 = StoragePath.get(DIRECTORY, folder1);
        assertEquals(folder1, folderPath1.getName());
    }

    @Test
    public void getName_When_FileTypeIsFile_Expected_CorrectName() {
        final String folder1 = "/folder1/";
        final String folder2 = "folder2/";
        final String file = "file.txt";

        final StoragePath filePath12 = StoragePath.get(FILE, folder1, folder2, file);

        assertEquals(file, filePath12.getName());
    }

    @Test
    public void getName_When_FileTypeIsFile_And_PathWithoutDirectory_Expected_CorrectName() {
        final String file = "file.txt";

        final StoragePath filePath12 = StoragePath.get(FILE, file);
        assertEquals(file, filePath12.getName());
    }

    @Test
    public void getParent_When_FileTypeIsDirectory_Expected_CorrectParentName() {
        final String folder1 = "folder1";
        final String folder2 = "folder2";

        final StoragePath folderPath12 = StoragePath.get(DIRECTORY, folder1, folder2);
        final StoragePath parentStoragePath = StoragePath.get(DIRECTORY, folder1);
        assertEquals(parentStoragePath, folderPath12.getParentPath());
    }

    @Test
    public void getParent_When_FileTypeIsDirectory_And_NoParent_Expected_CorrectParentName() {
        final String folder1 = "/folder1/";

        final StoragePath folderPath12 = StoragePath.get(DIRECTORY, folder1);
        assertNull(folderPath12.getParentPath());
    }

    @Test
    public void getParent_When_FileTypeIsFile_Expected_CorrectParentName() {
        final String folder1 = "/folder1/";
        final String folder2 = "folder2";
        final String file = "file.txt";

        final StoragePath filePath12 = StoragePath.get(FILE, folder1, folder2, file);
        final StoragePath parentStoragePath = StoragePath.get(DIRECTORY, folder1, folder2);
        assertEquals(parentStoragePath, filePath12.getParentPath());
    }

    @Test
    public void getParent_When_FileTypeIsFile_And_PathWithoutDirectory_Expected_CorrectParentName() {
        final String file = "file.txt";

        final StoragePath filePath12 = StoragePath.get(FILE, file);
        assertNull(filePath12.getParentPath());
    }
}
