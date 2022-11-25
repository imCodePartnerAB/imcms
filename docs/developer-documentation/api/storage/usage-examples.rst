Usage Examples
==============

.. code-block:: java

        StorageClient storageClient = new CloudStorageClient(s3Client, "testBucketName");

        //create a directory
        StoragePath directoryPath = StoragePath.get(SourceFile.FileType.DIRECTORY);
        storageClient.create(directoryPath);

        //upload a file
        StoragePath filePath = directoryPath.resolve(SourceFile.FileType.FILE, "file.txt");
        storageClient.put(filePath, new StringInputStream("some text"));

        //check for existence
        System.out.println("Existence of of a file.txt " + storageClient.exists(filePath));

        //receive the file
        //Don't forget to close StorageFile!
        try(StorageFile file = storageClient.getFile(filePath)){
            System.out.println("Content from file: " + new String(file.getContent().readAllBytes()));
        }

        //delete the directory
        storageClient.delete(directoryPath, true);
