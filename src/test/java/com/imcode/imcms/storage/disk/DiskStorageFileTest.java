package com.imcode.imcms.storage.disk;

import com.imcode.imcms.storage.impl.disk.DiskStorageFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class DiskStorageFileTest {

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
    public void getContent_Expected_InputStreamWithContent() throws Exception {
        final String text = "some text";
        Files.write(Paths.get(mainFolder1Path, "file.txt"), text.getBytes(StandardCharsets.UTF_8));

        //use reflection to create an instance
        Constructor<?> privateConstructor = DiskStorageFile.class.getDeclaredConstructors()[0];
        privateConstructor.setAccessible(true);
        DiskStorageFile diskStorageFile  = (DiskStorageFile) privateConstructor.newInstance(Paths.get(mainFolder1Path, "file.txt"));

        InputStream inputStream = diskStorageFile.getContent();
        assertEquals(text, IOUtils.toString(inputStream, StandardCharsets.UTF_8));

        //check getting stream again
        inputStream = diskStorageFile.getContent();
        assertEquals(text, IOUtils.toString(inputStream, StandardCharsets.UTF_8));
    }

    @Test
    public void close_Expected_NullPointerException() throws Exception {
        Files.createFile(Paths.get(mainFolder1Path, "file.txt"));

        //use reflection to create an instance
        Constructor<?> privateConstructor = DiskStorageFile.class.getDeclaredConstructors()[0];
        privateConstructor.setAccessible(true);
        DiskStorageFile diskStorageFile  = (DiskStorageFile) privateConstructor.newInstance(Paths.get(mainFolder1Path, "file.txt"));

        assertDoesNotThrow(() -> diskStorageFile.getContent());

        diskStorageFile.close();

        assertThrows(NullPointerException.class, () -> diskStorageFile.getContent());
    }

}
