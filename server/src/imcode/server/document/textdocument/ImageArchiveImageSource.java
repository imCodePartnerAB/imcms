package imcode.server.document.textdocument;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.util.io.FileInputStreamSource;
import imcode.util.io.InputStreamSource;

import java.io.File;
import java.util.Date;

public class ImageArchiveImageSource extends ImageSource {
	private static final long serialVersionUID = -1520182526108369695L;
	
	private String path;

	
	public ImageArchiveImageSource(String path) {
		this.path = path.replace('\\', '/');
	}
	
	@Override
	public InputStreamSource getInputStreamSource() {
		return new FileInputStreamSource(getFile());
	}

	private File getFile() {
		ImcmsServices service = Imcms.getServices();
		File basePath = isAbsolute() ? Imcms.getPath() : service.getConfig().getImageArchiveImagePath();
		
		return new File(basePath, path);
	}
	
	@Override
	public String getUrlPathRelativeToContextPath() {
		if (!isAbsolute()) {
			return getImagesUrlPath() + path;
		}
		
		return path;
	}
	
	private boolean isAbsolute() {
		return path.startsWith("/");
	}
	
	public static String getImagesUrlPath() {
		return Imcms.getServices().getConfig().getImageArchiveImageUrl();
	}

	@Override
	public String toStorageString() {
		return path;
	}
	
	@Override
	public int getTypeId() {
		return ImageSource.IMAGE_TYPE_ID__IMAGE_ARCHIVE;
	}

	@Override
	public Date getModifiedDatetime() {
		return new Date(getFile().lastModified());
	}

    public String getName() {
        return new File(path).getName();
    }
}
