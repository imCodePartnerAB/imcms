package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.api.exception.FileAccessDeniedException;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.exception.EmptyFileNameException;
import com.imcode.imcms.domain.exception.TemplateFileException;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.FileService;
import com.imcode.imcms.domain.service.TemplateCSSService;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.persistence.entity.TemplateJPA;
import imcode.util.image.Format;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
public class DefaultFileService implements FileService {

    private final DocumentService<DocumentDTO> documentService;
    private final TemplateService templateService;
	private final TemplateCSSService templateCSSService;

    private final BiFunction<Path, Boolean, SourceFile> fileToSourceFile;

    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> rootPaths;
    @Value("${rootPath}")
    private Path rootPath;

    private final Set<String> changeableExtensions = new HashSet<>(Arrays.asList("jsp", "jspx", "html", "css", "js", "txt", "pdf", "mp4"));

    @Autowired
    public DefaultFileService(DocumentService<DocumentDTO> documentService,
                              TemplateService templateService,
                              TemplateCSSService templateCSSService, BiFunction<Path, Boolean, SourceFile> fileToSourceFile) {
        this.documentService = documentService;
        this.templateService = templateService;
	    this.templateCSSService = templateCSSService;
	    this.fileToSourceFile = fileToSourceFile;
    }

    @PostConstruct
    private void init() {
        this.rootPaths = rootPaths.stream().filter(Files::exists).collect(Collectors.toList());
    }

    @Override
    public List<SourceFile> getRootFolders() {
        final List<Path> filteredRootPaths = rootPaths.stream()
                .filter(Files::exists)
                .collect(Collectors.toList());

        final List<Path> uniqueParentPaths = getUniqueParentsRelativeToPath(filteredRootPaths, rootPath);

        return uniqueParentPaths.stream()
                .map(parentPath -> {
                    SourceFile sourceFile = fileToSourceFile.apply(parentPath, false);
                    if(!rootPaths.contains(parentPath)) sourceFile.setEditable(false);
                    return sourceFile;
                })
                .collect(Collectors.toList());
    }

    /**
     * Check access (in the server.properties), return available folders and files
     *
     * If the path is the root folder which is part of the available paths (in the properties), we return the next available folders.
     * For example: We have WEB-INF/templates/text, WEB-INF/logs in properties. If required path is WEB-INF, method return WEB-INF/templates and WEB-INF/logs.
     */
    @Override
    public List<SourceFile> getFiles(Path path) throws IOException {
        if(path == null) return getRootFolders();

        final List<Path> filteredRootPaths = rootPaths.stream()
                .filter(filteredRootPath -> filteredRootPath.startsWith(path) && !filteredRootPath.equals(path))
                .collect(Collectors.toList());
        if(!filteredRootPaths.isEmpty() && !path.equals(rootPath)){
            final List<Path> uniqueParentPaths = getUniqueParentsRelativeToPath(filteredRootPaths, path);

            return uniqueParentPaths.stream()
                    .map(parentPath -> {
                        SourceFile sourceFile = fileToSourceFile.apply(parentPath, false);
                        if(!rootPaths.contains(parentPath)) sourceFile.setEditable(false);
                        return sourceFile;
                    })
                    .collect(Collectors.toList());
        }

        checkAccessAllowed(path, false);

        if(!Files.isDirectory(path)) return Collections.EMPTY_LIST;

        return Files.list(path).sorted()
		        .filter(filePath -> !filePath.toFile().isHidden())
                .map(filePath -> {
                    final SourceFile sourceFile = fileToSourceFile.apply(filePath, false);
                    if (isTemplatePath(filePath) && !Files.isDirectory(filePath)) {
                        sourceFile.setNumberOfDocuments(countDocumentsByTemplateName(filePath));
                    }
                    if(!checkExtensionAllowed(filePath)) sourceFile.setEditable(false);
                    return sourceFile;
                })
                .sorted(Comparator.comparing(SourceFile::getFileType))
                .collect(Collectors.toList());
    }

    /**
     * Filter folders and return root folders (relative to the path).
     */
    private List<Path> getUniqueParentsRelativeToPath(List<Path> paths, Path relativePath){
        List<Path> uniqueParentPaths = new ArrayList<>();

        for(Path path: paths){
            final Path topParent = relativePath.relativize(path).getName(0);
            if(!uniqueParentPaths.contains(topParent)) uniqueParentPaths.add(topParent);
        }

        return uniqueParentPaths.stream().map(relativePath::resolve).collect(Collectors.toList());
    }

    @Override
    public SourceFile getFile(Path file) throws IOException {
        checkAccessAllowed(file, false);
        if (Files.exists(file)) {
            return fileToSourceFile.apply(file, true);
        } else {
            log.error("File doesn't exist: " + file);
            throw new NoSuchFileException("File is not exist!");
        }
    }

    @Override
    public List<DocumentDTO> getDocumentsByTemplatePath(Path template) {
        return documentService.getDocumentsByTemplateName(FilenameUtils.removeExtension(template.getFileName().toString()));
    }

    @Override
    public void deleteFile(Path file) throws IOException {
        checkActionsAllowed(file, false);

        if (Files.isDirectory(file)) {
            if(isTemplatePath(file)) throwTemplateException("Folder with templates can't be deleted!  Got path:" + file);

	        Files.list(file)
			        .map(Path::toFile)
			        .forEach(FileSystemUtils::deleteRecursively);
	        Files.delete(file);
        } else {
            if(isTemplatePath(file)) deleteTemplate(file);

            Files.delete(file);
        }
    }

    @Override
    public List<SourceFile> moveFile(List<Path> src, Path target) throws IOException {
        src.forEach(path -> checkActionsAllowed(path, true));
        src.forEach(path -> {
            if(isTemplatePath(path)) throwTemplateException("Folder with templates or templates cannot be moved! Got path: " + path);
        });
        checkAccessAllowed(target, false);
        if(isTemplatePath(target)) throwTemplateException("Files cannot be moved to the folder with templates! Got path: " + target);

        final List<SourceFile> files = new ArrayList<>();
        for (Path srcPath : src) {
            files.add(fileToSourceFile.apply(Files.move(srcPath, target.resolve(srcPath.getFileName())), false));
        }
        return files;
    }

    @Override
    public SourceFile renameFile(Path src, String newName) throws IOException {
        checkActionsAllowed(src, true);

        if (StringUtils.isNotBlank(src.toString()) && StringUtils.isNotBlank(newName)) {
            final Path path = Files.move(src, Paths.get(src.getParent().toString(), newName));

            try{
                if(Files.isDirectory(path) && isTemplatePath(src)) throwTemplateException("Folder with templates can't be deleted!");
                if(isTemplatePath(src)) renameTemplate(src, newName);
            }catch(TemplateFileException e){
                Files.move(path, src);
                throw e;
            }

            return fileToSourceFile.apply(path, false);
        } else {
            final String errorMessage = "Filepath or filename is empty!";
            log.error(errorMessage);
            throw new EmptyFileNameException(errorMessage);
        }
    }

    @Override
    public List<SourceFile> copyFile(List<Path> src, Path target) throws IOException {
        src.forEach(path -> checkActionsAllowed(path, false));
        src.forEach(path -> {
            if(isTemplatePath(path)) throwTemplateException("Folder with templates or templates cannot be copied! Got path: " + path);
        });
        checkAccessAllowed(target, false);
        if(isTemplatePath(target)) throwTemplateException("Files cannot be copied to the folder with templates! Got path: " + target);

        final List<SourceFile> files = new ArrayList<>();
        Path targetPath;
        for (Path srcPath : src) {
            targetPath = target.resolve(srcPath.getFileName());

            if(Files.isDirectory(srcPath)){
                org.apache.commons.io.FileUtils.copyDirectory(srcPath.toFile(), targetPath.toFile());
            }else{
                Files.copy(srcPath, target.resolve(srcPath.getFileName()));
            }

            files.add(fileToSourceFile.apply(targetPath, false));
        }
        return files;
    }

    @Override
    public SourceFile saveFile(Path location, byte[] content, OpenOption writeMode) throws IOException {
        checkActionsAllowed(location, false);

        if (StringUtils.isNotBlank(location.toString())) {
	        if (!isTemplatePath(location) && Files.exists(location)) renameExistingFile(location);

            Path writeFilePath = writeMode == null ? Files.write(location, content) : Files.write(location, content, writeMode);

            if(isTemplatePath(location) && Files.exists(location)) createAndSaveTemplate(location);

            return fileToSourceFile.apply(writeFilePath, true);
        } else {
            final String errorMessage = "Filepath is empty!";
            log.error(errorMessage);
            throw new EmptyFileNameException(errorMessage);
        }
    }

    @Override
    public SourceFile createFile(SourceFile file, boolean isDirectory) throws IOException {
        final Path filePath = Paths.get(file.getFullPath());
        if (StringUtils.isNotBlank(file.getFileName())) {
            Path newSrcFilePath;
            if (isDirectory) {
                checkAccessAllowed(filePath, false);
                if(isTemplatePath(filePath)) throwTemplateException("Folder cannot be created in the folder with templates");
                newSrcFilePath = Files.createDirectory(filePath);
            } else {
                checkActionsAllowed(filePath, false);
                newSrcFilePath = Files.createFile(filePath);
                if(isTemplatePath(filePath) && Files.exists(newSrcFilePath)) createAndSaveTemplate(filePath);
            }

            return fileToSourceFile.apply(newSrcFilePath, false);
        } else {
            final String errorMessage = "Filename is empty!";
            log.error(errorMessage);
            throw new EmptyFileNameException(errorMessage);
        }
    }

    /**
     *  Restrict read-only files
     */
    private void checkActionsAllowed(Path path, boolean excludeRoot){
        checkAccessAllowed(path, excludeRoot);
        if(!checkExtensionAllowed(path)){
            log.error("File access denied! Got path: " + path);
            throw new FileAccessDeniedException("File access denied!");
        }
    }

    private void checkAccessAllowed(Path path, boolean excludeRoot) {
        String normalizedPath = path.normalize().toString();
        final String finalNormalize = StringUtils.isBlank(normalizedPath)
                ? rootPath.toString()
                : normalizedPath;

        boolean access = false;
        for (Path pathRoot : rootPaths) {
            try {
                access = Files.walk(pathRoot)
                        .anyMatch(pathWalk -> !(excludeRoot && pathWalk.toString().equals(pathRoot.toString())) && Paths.get(finalNormalize).startsWith(pathWalk));
                if (access) break;
            } catch (IOException e) {
                log.warn("There is no such file in the project. Got path: " + pathRoot);
            }
        }

        if (!access) {
            log.error("Access to the file with this extension is denied! Got path: " + path);
            throw new FileAccessDeniedException("Access to the file with this extension is denied!");
        }
    }

    private boolean checkExtensionAllowed(Path path){
        final String extension = FilenameUtils.getExtension(path.toString());
        return Files.isDirectory(path) || changeableExtensions.contains(extension) || Format.isImage(extension);
    }


    private boolean isTemplatePath(Path path) {
        return path.startsWith(templateService.getTemplateDirectory().toPath());
    }

    private void createAndSaveTemplate(Path path) throws IOException {
        final String templateName = FilenameUtils.removeExtension(path.getFileName().normalize().toString());

        if(templateService.get(templateName) != null) return;

        if(templateService.isValidName(path.getFileName().toString())){
            final TemplateJPA templateJPA = new TemplateJPA();
            templateJPA.setName(templateName);
            templateJPA.setHidden(false);

            try{
                templateService.save(templateJPA);
				templateCSSService.create(templateName);
            }catch (DataIntegrityViolationException e){
                Files.delete(path);
                throwTemplateException("Template with the same name already exists!");
            }
        }
    }

    private void renameTemplate(Path path, String newName){
        if(templateService.isValidName(newName)){
            final String oldTemplateName = FilenameUtils.removeExtension(path.getFileName().normalize().toString());
	        final String newClearName = FilenameUtils.removeExtension(newName);

	        templateService.renameTemplate(oldTemplateName, newClearName);
	        templateCSSService.rename(oldTemplateName, newClearName);
        }else{
            deleteTemplate(path);
        }
    }

    private void deleteTemplate(Path path){
        final String orgTemplateName = FilenameUtils.removeExtension(path.getFileName().toString());
        if(documentService.getDocumentsByTemplateName(orgTemplateName).isEmpty()){
            final Template template = templateService.get(orgTemplateName);
            if(template != null) templateService.delete(template.getId());
        }else{
            throwTemplateException("Template is used in documents, cannot be deleted! Got path: " + path);
        }
    }

    private int countDocumentsByTemplateName(Path template){
        return documentService.countDocumentsByTemplateName(FilenameUtils.removeExtension(template.getFileName().toString()));
    }

    private void throwTemplateException(String errorMessage){
        log.error(errorMessage);
        throw new TemplateFileException(errorMessage);
    }

	private void renameExistingFile(Path location) throws IOException {
		final String filePath = location.toFile().getAbsolutePath();
		final String baseName = FilenameUtils.getBaseName(filePath);
		final String extension = FilenameUtils.getExtension(filePath);
		final String fullPath = FilenameUtils.getFullPath(filePath);

		int copiesCount = 1;
		String newNameOldFile;
		do {
			newNameOldFile = baseName + '-' + copiesCount++ + '.' + extension;
		} while (Files.exists(Path.of(fullPath + newNameOldFile)));

		renameFile(location, newNameOldFile);
	}
}
