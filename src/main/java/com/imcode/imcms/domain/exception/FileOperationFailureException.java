package com.imcode.imcms.domain.exception;

import com.imcode.imcms.api.SourceFile;
import lombok.Getter;

import java.io.Serial;
import java.util.List;

@Getter
public class FileOperationFailureException extends Exception {

	@Serial
	private static final long serialVersionUID = 7616528046983456058L;

	private List<SourceFile> conflictFiles;

	public FileOperationFailureException(List<SourceFile> conflictFiles) {
		this.conflictFiles = conflictFiles;
	}

	public FileOperationFailureException(String message, List<SourceFile> conflictFiles) {
		super(message);
		this.conflictFiles = conflictFiles;
	}

	public FileOperationFailureException(String message, Throwable cause, List<SourceFile> conflictFiles) {
		super(message, cause);
		this.conflictFiles = conflictFiles;
	}

	public FileOperationFailureException(Throwable cause, List<SourceFile> conflictFiles) {
		super(cause);
		this.conflictFiles = conflictFiles;
	}

	public FileOperationFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<SourceFile> conflictFiles) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.conflictFiles = conflictFiles;
	}

}
